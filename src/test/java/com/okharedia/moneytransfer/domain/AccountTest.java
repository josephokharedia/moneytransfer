package com.okharedia.moneytransfer.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    public void testDeposit() {
        Account account = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(100));

        account.deposit(BigDecimal.valueOf(100));
        assert account.getBalance().equals(BigDecimal.valueOf(200));
    }

    @Test
    public void throwException_whenDepositNegativeAmount() {
        Account account = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(100));

        assertThrows(IllegalArgumentException.class, () ->
                account.deposit(BigDecimal.valueOf(-100)));
    }

    @Test
    public void testWithdrawal() throws InsufficientFundsException {
        Account account = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(100));

        account.withdraw(BigDecimal.valueOf(50));
        assert account.getBalance().equals(BigDecimal.valueOf(50));
    }

    @Test
    public void throwException_whenWithdrawNegativeAmount() {
        Account account = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(100));

        assertThrows(IllegalArgumentException.class, () ->
                account.withdraw(BigDecimal.valueOf(-100)));
    }

    @Test
    public void throwException_whenWithdrawalInsufficientFunds() {
        Account account = new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(100));

        assertThrows(InsufficientFundsException.class, () ->
                account.withdraw(BigDecimal.valueOf(200)));
    }


}
