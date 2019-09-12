package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.AccountRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultAccountRepository implements AccountRepository {

    List<Account> accounts;

    public DefaultAccountRepository(List<Account> accounts) {
        this.accounts = new CopyOnWriteArrayList<>(accounts);
    }

    @Override
    public void updateBalanceAtomically(Account... accounts) throws AccountNotFoundException {
        for (Account account : accounts) {
            Account _account = getAccountByAccountNumber(account.getAccountNumber())
                    .orElseThrow(() -> new AccountNotFoundException(account.getAccountNumber()));
            _account.setBalance(account.getBalance());
        }
    }

    @Override
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber().equalsIgnoreCase(accountNumber))
                .findFirst();
    }
}
