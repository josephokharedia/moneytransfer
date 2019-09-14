package com.okharedia.moneytransfer.controller;

import com.okharedia.moneytransfer.domain.*;

import java.util.List;

public class MoneyTransferController {

    private final MoneyTransferService moneyTransferService;
    private final AccountRepository accountRepository;


    public MoneyTransferController(MoneyTransferService moneyTransferService, AccountRepository accountRepository) {
        this.moneyTransferService = moneyTransferService;
        this.accountRepository = accountRepository;
    }

    /**
     * Transfer money from one account to another.
     *
     * @param moneyTransferRequest - contains sender, recipient and amount for transaction
     * @throws InsufficientFundsException - when there is insufficient funds in the senders account
     * @throws AccountNotFoundException   - when either the sender or recipient account is not found
     */
    public void transferMoney(MoneyTransferRequest moneyTransferRequest) throws InsufficientFundsException, AccountNotFoundException {
        this.moneyTransferService.transferMoney(
                moneyTransferRequest.getFromAccount(),
                moneyTransferRequest.getToAccount(),
                moneyTransferRequest.getAmount()
        );
    }

    /**
     * Returns all accounts
     *
     * @return all accounts
     */
    public List<Account> allAccounts() {
        return accountRepository.allAccounts();
    }
}
