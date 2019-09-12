package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class H2DatabaseAccountRepository implements AccountRepository {

    
    @Override
    public void updateBalance(String accountNumber, BigDecimal newBalance) throws AccountNotFoundException {

    }

    @Override
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return Optional.empty();
    }
}
