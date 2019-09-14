package com.okharedia.moneytransfer.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Account implements Cloneable {

    private String accountNumber;
    private BigDecimal balance;
    private int version;

    public Account(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public Account(String accountNumber, BigDecimal balance, int version) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.version = version;
    }

    public void transfer(BigDecimal amount, Account toAccount) throws InsufficientFundsException {
        if (this.equals(toAccount)) {
            throw new IllegalArgumentException("From and To Account must not be equal");
        }

        this.withdraw(amount);
        toAccount.deposit(amount);
    }

    void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        balance = getBalance().add(amount);
    }

    void withdraw(BigDecimal amount) throws InsufficientFundsException {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        BigDecimal newBalance = getBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException();
        }
        balance = newBalance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    public Account newVersion() {
        Account account = this.clone();
        ++account.version;
        return account;
    }

    @Override
    public Account clone() {
        return new Account(accountNumber, balance, version);
    }
}
