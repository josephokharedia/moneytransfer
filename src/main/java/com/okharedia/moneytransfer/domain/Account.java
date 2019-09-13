package com.okharedia.moneytransfer.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Account implements Cloneable {

    private String accountNumber;
    private BigDecimal balance = BigDecimal.ZERO;
    private int version;

    public Account(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void transfer(BigDecimal amount, Account toAccount) throws InsufficientFundsException {
        if (this.equals(toAccount)) {
            throw new IllegalArgumentException("From and To Account must not be equal");
        }

        this.withdraw(amount);
        toAccount.deposit(amount);
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

    public void setVersion(int version) {
        this.version = version;
    }

    public void incrementVersion() {
        ++this.version;
    }

    @Override
    public Account clone() {
        Account account = new Account(accountNumber);
        account.setVersion(version);
        account.setBalance(balance);
        return account;
    }
}
