package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.AccountRepository;
import com.okharedia.moneytransfer.domain.StaleAccountException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class DefaultAccountRepository implements AccountRepository {

    List<Account> accountDb;
    private final ReentrantLock lock = new ReentrantLock();

    public DefaultAccountRepository(Collection<Account> accountDb) {
        this.accountDb = new CopyOnWriteArrayList<>(accountDb);
    }

    private void throwIfAccountIsStale(Account... accounts) throws AccountNotFoundException, StaleAccountException {
        for (Account account : accounts) {
            Account accountInDb = getAccount(account.getAccountNumber())
                    .orElseThrow(() -> new AccountNotFoundException(account.getAccountNumber()));
            if (account.getVersion() != accountInDb.getVersion()) {
                throw new StaleAccountException(account.getAccountNumber());
            }
        }
    }

    @Override
    public void saveAtomically(Account... accounts) throws AccountNotFoundException, StaleAccountException {

        try {

            lock.lock();

            throwIfAccountIsStale(accounts);

            Stream.of(accounts)
                    .forEach(account -> {

                        int idx = this.accountDb.indexOf(account);
                        account.incrementVersion();
                        this.accountDb.set(idx, account);

                    });

        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<Account> getAccount(String accountNumber) {
        return accountDb.stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst()
                .map(Account::clone);
    }
}
