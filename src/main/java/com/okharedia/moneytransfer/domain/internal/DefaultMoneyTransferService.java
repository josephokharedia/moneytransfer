package com.okharedia.moneytransfer.domain.internal;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.InsufficientFundsException;
import com.okharedia.moneytransfer.domain.MoneyTransferService;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.AccountRepository;

import java.math.BigDecimal;

public class DefaultMoneyTransferService implements MoneyTransferService {

    private AccountRepository accountRepository;

    public DefaultMoneyTransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void transferMoney(String fromAccountNumber, String toAccountNumber, BigDecimal amount)
            throws InsufficientFundsException, AccountNotFoundException {

        Account fromAccount = accountRepository.getAccountByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountNumber));

        Account toAccount = accountRepository.getAccountByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountNumber));

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        accountRepository.updateBalance(fromAccountNumber, fromAccount.getBalance());
        accountRepository.updateBalance(toAccountNumber, toAccount.getBalance());
    }
}
