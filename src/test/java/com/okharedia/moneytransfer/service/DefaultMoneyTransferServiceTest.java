package com.okharedia.moneytransfer.service;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.InsufficientFundsException;
import com.okharedia.moneytransfer.domain.StaleAccountException;
import com.okharedia.moneytransfer.domain.internal.DefaultMoneyTransferService;
import com.okharedia.moneytransfer.repository.DefaultAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(accountRepository.getAccount(fromAccount.getAccountNumber()))
                .thenReturn(Optional.of(fromAccount));

        when(accountRepository.getAccount(toAccount.getAccountNumber()))
                .thenReturn(Optional.of(toAccount));
    }

    @Test
    public void testMoneyTransfer() throws InsufficientFundsException, AccountNotFoundException, StaleAccountException {

        moneyTransferService.transferMoney(
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                BigDecimal.ONE);

        assert fromAccount.getBalance().equals(BigDecimal.valueOf(99));
        assert toAccount.getBalance().equals(BigDecimal.valueOf(1));

        verify(accountRepository).getAccount(fromAccount.getAccountNumber());
        verify(accountRepository).getAccount(toAccount.getAccountNumber());

        ArgumentCaptor<Account> argumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).saveAtomically(argumentCaptor.capture());
        List<Account> accounts = argumentCaptor.getAllValues();
        assert accounts.size() == 2;
        assert !(isDiffBetweenAccountList(accounts, Arrays.asList(fromAccount, toAccount)));
    }

    private boolean isDiffBetweenAccountList(List<Account> list1, List<Account> list2) {
        return list1.retainAll(list2);
    }
}
