package com.okharedia.moneytransfer.domain;

import java.math.BigDecimal;

public interface MoneyTransferService {

    void transferMoney(String fromAccountNumber, String toAccountNumber, BigDecimal amount)
            throws InsufficientFundsException, AccountNotFoundException;
}
