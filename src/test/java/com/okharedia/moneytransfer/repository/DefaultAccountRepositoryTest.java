package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.StaleAccountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class DefaultAccountRepositoryTest {

    private DefaultAccountRepository accountRepository;
    private String TEST_ACCOUNT_NUMBER = "TEST";


    @BeforeEach
    public void setup() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(TEST_ACCOUNT_NUMBER, BigDecimal.ZERO));
        accountRepository = new DefaultAccountRepository(accounts);
    }

    @Test
    public void testGetAccount() {
        Optional<Account> account = accountRepository.getAccount(TEST_ACCOUNT_NUMBER);
        assert account.isPresent();
    }

    @Test
    public void withNonExistingAccountNumber_testGetAccount() {
        Optional<Account> account = accountRepository.getAccount("Not Exist");
        assert !account.isPresent();
    }

    @Test
    public void testUpdateBalance() throws AccountNotFoundException, StaleAccountException {
        BigDecimal newBalance = BigDecimal.valueOf(20);
        Account testAccount = new Account(TEST_ACCOUNT_NUMBER, newBalance);
        accountRepository.saveAtomically(testAccount);
        Optional<Account> account = accountRepository.accountDb.stream()
                .filter(a -> TEST_ACCOUNT_NUMBER.equalsIgnoreCase(a.getAccountNumber()))
                .findAny();

        assert account.isPresent();
        assert account.get().getBalance().equals(newBalance);
    }

}
