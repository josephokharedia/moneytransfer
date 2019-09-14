package com.okharedia.moneytransfer;

import com.okharedia.moneytransfer.controller.MoneyTransferController;
import com.okharedia.moneytransfer.domain.AccountRepository;
import com.okharedia.moneytransfer.domain.MoneyTransferService;
import com.okharedia.moneytransfer.domain.internal.DefaultMoneyTransferService;
import com.okharedia.moneytransfer.repository.H2DatabaseAccountRepository;

import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException {
        AccountRepository accountRepository = new H2DatabaseAccountRepository();
        MoneyTransferService moneyTransferService = new DefaultMoneyTransferService(accountRepository);
        MoneyTransferController moneyTransferController = new MoneyTransferController(moneyTransferService, accountRepository);
        new WebServer(moneyTransferController).start();
    }
}
