package com.okharedia.moneytransfer.controller;

import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.InsufficientFundsException;
import com.okharedia.moneytransfer.domain.MoneyTransferService;

public class MoneyTransferController {

    private final MoneyTransferService moneyTransferService;

    public MoneyTransferController(MoneyTransferService moneyTransferService) {
        this.moneyTransferService = moneyTransferService;
    }

    public void transferMoney(MoneyTransferRequest moneyTransferRequest) throws InsufficientFundsException, AccountNotFoundException {
        this.moneyTransferService.transferMoney(
                moneyTransferRequest.getFromAccount(),
                moneyTransferRequest.getToAccount(),
                moneyTransferRequest.getAmount()
        );
    }
}
