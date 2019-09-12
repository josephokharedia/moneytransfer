package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.AccountRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultAccountRepository implements AccountRepository {

    List<Account> ACCOUNTS;

    public DefaultAccountRepository(List<Account> ACCOUNTS) {
        this.ACCOUNTS = new CopyOnWriteArrayList<>(ACCOUNTS);
    }

    @Override
    public void saveAtomically(Account... accounts) throws AccountNotFoundException {
        for (Account account : accounts) {

            int idx = this.ACCOUNTS.indexOf(account);
            if (idx == -1) {
                throw new AccountNotFoundException(account.getAccountNumber());
            }

            this.ACCOUNTS.set(idx, account);
        }
    }

    @Override
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return ACCOUNTS.stream()
                .filter(a -> a.getAccountNumber().equalsIgnoreCase(accountNumber))
                .findFirst()
                .map(a -> {
                    Account account = new Account(a.getAccountNumber());
                    account.setBalance(a.getBalance());
                    return account;
                });

    }
}
