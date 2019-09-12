package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class DefaultAccountRepository implements AccountRepository {

    @Override
    public void updateBalance(String accountNumber, BigDecimal newBalance) {
    }

    @Override
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return Optional.empty();
    }
}
