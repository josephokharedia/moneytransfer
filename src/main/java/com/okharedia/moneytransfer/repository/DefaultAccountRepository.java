package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.AccountRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultAccountRepository implements AccountRepository {

    List<Account> ACCOUNTS = new ArrayList<>();

    @Override
    public void updateBalance(String accountNumber, BigDecimal newBalance) throws AccountNotFoundException {
        Account account = getAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        account.setBalance(newBalance);
    }

    @Override
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return ACCOUNTS.stream()
                .filter(a -> a.getAccountNumber().equalsIgnoreCase(accountNumber))
                .findFirst();
    }
}
