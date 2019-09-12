package com.okharedia.moneytransfer.domain;

import java.math.BigDecimal;

public interface MoneyTransferService {

    /**
     * Transfer amount between accounts
     *
     * @param fromAccountNumber - fromAccount number to be debited
     * @param toAccountNumber   - toAccount number to be credited
     * @param amount            - amount to be transferred
     * @throws InsufficientFundsException - when fromAccount balance is lower than amount
     * @throws AccountNotFoundException   - when either fromAccount or toAccount is not found
     */
    void transferMoney(String fromAccountNumber, String toAccountNumber, BigDecimal amount)
            throws InsufficientFundsException, AccountNotFoundException;
}
