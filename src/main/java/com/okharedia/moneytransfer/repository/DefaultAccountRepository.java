package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultAccountRepository implements AccountRepository {

    List<Account> accounts;

    public DefaultAccountRepository(List<Account> accounts) {
        this.accounts = new CopyOnWriteArrayList<>(accounts);
    }

    @Override
    public void updateBalance(String accountNumber, BigDecimal newBalance) throws AccountNotFoundException {
        Account account = getAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        account.setBalance(newBalance);
    }

    @Override
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        accounts.iterator();
        return accounts.stream()
                .filter(a -> a.getAccountNumber().equalsIgnoreCase(accountNumber))
                .findFirst();
    }
}
