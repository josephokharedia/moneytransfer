package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class H2DatabaseAccountRepositoryTest {

    private H2DatabaseAccountRepository repository;
    private List<String> testAccountNumbers = Arrays.asList("1", "2", "3");

    @BeforeEach
    public void setup() {
        repository = new H2DatabaseAccountRepository();
    }

    @Test
    public void testTestAccounts() {
        for (String testAccountNumber : testAccountNumbers) {

            Optional<Account> accountOptional = repository.getAccount(testAccountNumber);
            assert accountOptional.isPresent();
        }
    }
}
