package com.okharedia.moneytransfer.service;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.InsufficientFundsException;
import com.okharedia.moneytransfer.domain.internal.DefaultMoneyTransferService;
import com.okharedia.moneytransfer.repository.DefaultAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

class DefaultMoneyTransferServiceTest {

    @Mock
    private DefaultAccountRepository accountRepository;
    @InjectMocks
    private DefaultMoneyTransferService moneyTransferService;
    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    private void setup() {
        MockitoAnnotations.initMocks(this);
        fromAccount = new Account("FROM");
        fromAccount.setBalance(BigDecimal.valueOf(100));

        toAccount = new Account("TO");
        toAccount.setBalance(BigDecimal.valueOf(0));

        when(accountRepository.getAccountByAccountNumber(fromAccount.getAccountNumber()))
                .thenReturn(Optional.of(fromAccount));

        when(accountRepository.getAccountByAccountNumber(toAccount.getAccountNumber()))
                .thenReturn(Optional.of(toAccount));
    }

    @Test
    public void testMoneyTransfer() throws InsufficientFundsException, AccountNotFoundException {

        moneyTransferService.transferMoney(
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                BigDecimal.ONE);

        assert fromAccount.getBalance().equals(BigDecimal.valueOf(99));
        assert toAccount.getBalance().equals(BigDecimal.valueOf(1));

        verify(accountRepository).getAccountByAccountNumber(fromAccount.getAccountNumber());
        verify(accountRepository).getAccountByAccountNumber(toAccount.getAccountNumber());

        verify(accountRepository).updateBalance(fromAccount.getAccountNumber(), fromAccount.getBalance());
        verify(accountRepository).updateBalance(toAccount.getAccountNumber(), toAccount.getBalance());
    }
}
