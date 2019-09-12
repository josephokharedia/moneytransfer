package com.okharedia.moneytransfer.domain;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository {

    /**
     * Update current balance on an account
     *
     * @param accountNumber - accountNumber of account
     * @param newBalance    - new current balance of the account
     * @throws AccountNotFoundException - when account is not found
     */
    void updateBalance(String accountNumber, BigDecimal newBalance) throws AccountNotFoundException;

    /**
     * Find account by account number
     *
     * @param accountNumber - accountNumber of account
     * @return - account
     */
    Optional<Account> getAccountByAccountNumber(String accountNumber);
}
