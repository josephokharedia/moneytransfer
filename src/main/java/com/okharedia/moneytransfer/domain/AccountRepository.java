package com.okharedia.moneytransfer.domain;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    /**
     * Update current balance on an account atomically.
     * All accounts are updated together successfully or none at all
     *
     * @param accounts - list of accounts that needs to be saved
     * @throws AccountNotFoundException - when account is not found
     * @throws StaleAccountException    - when account currently being saved has old data
     *                                  because this account has already been updated
     */
    void saveAtomically(Account... accounts) throws AccountNotFoundException, StaleAccountException;

    /**
     * Find account by account number
     *
     * @param accountNumber - accountNumber of account
     * @return - account
     */
    Optional<Account> getAccount(String accountNumber);

    /**
     * Returns all accounts in repository
     *
     * @return all accounts
     */
    List<Account> allAccounts();
}
