package com.okharedia.moneytransfer.domain;

import java.math.BigDecimal;

public class Account {

    private String accountNumber;
    private BigDecimal balance = BigDecimal.ZERO;

    public Account(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        BigDecimal newBalance = getBalance().add(amount);
        setBalance(newBalance);
    }

    public void withdraw(BigDecimal amount) throws InsufficientFundsException {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        BigDecimal newBalance = getBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException();
        }
        setBalance(newBalance);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
