package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

class DefaultAccountRepositoryTest {

    private DefaultAccountRepository accountRepository;
    private final String TEST_ACCOUNT_NUMBER = "TEST";

    @BeforeEach
    public void setup() {
        accountRepository = new DefaultAccountRepository();
        Account account = new Account(TEST_ACCOUNT_NUMBER);
        accountRepository.ACCOUNTS.add(account);
    }

    @Test
    public void testGetAccount() {
        Optional<Account> account = accountRepository.getAccountByAccountNumber(TEST_ACCOUNT_NUMBER);
        assert account.isPresent();
    }

    @Test
    public void withNonExistingAccountNumber_testGetAccount() {
        Optional<Account> account = accountRepository.getAccountByAccountNumber("Not Exist");
        assert !account.isPresent();
    }

    @Test
    public void testUpdateBalance() throws AccountNotFoundException {
        BigDecimal newBalance = BigDecimal.valueOf(20);
        accountRepository.updateBalance(TEST_ACCOUNT_NUMBER, newBalance);
        Optional<Account> account = accountRepository.ACCOUNTS.stream()
                .filter(a -> TEST_ACCOUNT_NUMBER.equalsIgnoreCase(a.getAccountNumber()))
                .findAny();

        assert account.isPresent();
        assert account.get().getBalance().equals(newBalance);
    }

}
