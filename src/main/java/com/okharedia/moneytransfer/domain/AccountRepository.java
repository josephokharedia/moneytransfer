package com.okharedia.moneytransfer.domain;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository {

    void updateBalance(String accountNumber, BigDecimal newBalance);

    Optional<Account> getAccountByAccountNumber(String accountNumber);
}
