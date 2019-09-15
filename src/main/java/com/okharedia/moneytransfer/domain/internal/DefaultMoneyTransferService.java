package com.okharedia.moneytransfer.domain.internal;

import com.okharedia.moneytransfer.domain.*;

import java.math.BigDecimal;

public class DefaultMoneyTransferService implements MoneyTransferService {

    private static final Integer MAX_RETRY_TRANSFER_MONEY = 10;

    private AccountRepository accountRepository;
    private ThreadLocal<Integer> maxRetryTransferMoney = ThreadLocal.withInitial(() -> MAX_RETRY_TRANSFER_MONEY);

    public DefaultMoneyTransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void transferMoney(String fromAccountNumber, String toAccountNumber, BigDecimal amount)
            throws InsufficientFundsException, AccountNotFoundException {

        boolean staleAccountExceptionIsRaised;

        do {
            try {
                Account fromAccount = accountRepository.getAccount(fromAccountNumber)
                        .orElseThrow(() -> new AccountNotFoundException("account " + fromAccountNumber + " does not exist"));

                Account toAccount = accountRepository.getAccount(toAccountNumber)
                        .orElseThrow(() -> new AccountNotFoundException("account " + toAccountNumber + " does not exist"));

                fromAccount.transfer(amount, toAccount);

                accountRepository.saveAtomically(fromAccount, toAccount);
                staleAccountExceptionIsRaised = false;

            } catch (StaleAccountException e) {
                staleAccountExceptionIsRaised = true;

                if (!retryTransferMoneyRemaining()) {
                    throw new RuntimeException("Max reties exceeded for transfer money", e);
                }

                System.out.println(String.format("[%s] StaleAccountException: " + "retrying amount:[%s] from:[%s] to[%s]",
                        Thread.currentThread().getName(), amount, fromAccountNumber, toAccountNumber));

                decrementRetryTransferMoney();
            }
        } while (retryTransferMoneyRemaining() && staleAccountExceptionIsRaised);
    }

    private void decrementRetryTransferMoney() {
        maxRetryTransferMoney.set(maxRetryTransferMoney.get() - 1);
    }

    private boolean retryTransferMoneyRemaining() {
        return maxRetryTransferMoney.get() > 0;
    }

}
