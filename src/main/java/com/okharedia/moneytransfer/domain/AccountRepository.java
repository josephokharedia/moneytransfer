package com.okharedia.moneytransfer.domain;

import java.util.Optional;

public interface AccountRepository {

    /**
     * Update current balance on an account atomically.
     * All accounts are updated together successfully or none at all
     *
     * @param accounts - list of accounts that need balance updated
     * @throws AccountNotFoundException - when account is not found
     */
    void updateBalanceAtomically(Account... accounts) throws AccountNotFoundException;

    /**
     * Find account by account number
     *
     * @param accountNumber - accountNumber of account
     * @return - account
     */
    Optional<Account> getAccountByAccountNumber(String accountNumber);
}
