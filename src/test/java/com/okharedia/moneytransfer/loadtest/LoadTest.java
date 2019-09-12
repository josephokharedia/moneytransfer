package com.okharedia.moneytransfer.loadtest;

import com.okharedia.moneytransfer.domain.*;
import com.okharedia.moneytransfer.domain.internal.DefaultMoneyTransferService;
import com.okharedia.moneytransfer.repository.DefaultAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

class LoadTest {

    private MoneyTransferService service;
    private AccountRepository repository;
    private List<Account> testAccounts;
    private List<TransferInstruction> transferInstructions;
    private List<TransferResult> transferResults;
    private ExecutorService executorService;
    private List<Future<?>> submittedFutures;

    @AfterEach
    public void teardown() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);
    }

    @BeforeEach
    public void setup() {
        testAccounts = new ArrayList<>();

        // Add test accounts
        IntStream.rangeClosed(1, 3).forEach(n -> {
            Account account = new Account(String.valueOf(n));
            account.setBalance(BigDecimal.valueOf(100));
            testAccounts.add(account);
        });

        repository = new DefaultAccountRepository(testAccounts);
        service = new DefaultMoneyTransferService(repository);

        /* Create transfer instructions and expected results
        -----
        INSTR:
        (amount) fromAccNumber -> toAccNumber
        (50) 1 -> 2
        (10) 2 -> 3
        (10) 3 -> 1
        (20) 3 -> 2

        ------
        RESULT:
        accountNumber: balance
        1: 60
        2: 160
        3: 80
       */
        transferInstructions = Arrays.asList(
                new TransferInstruction("1", "2", BigDecimal.valueOf(50)),
                new TransferInstruction("2", "3", BigDecimal.valueOf(10)),
                new TransferInstruction("3", "1", BigDecimal.valueOf(10)),
                new TransferInstruction("3", "2", BigDecimal.valueOf(20))
        );

        transferResults = Arrays.asList(
                new TransferResult("1", BigDecimal.valueOf(60)),
                new TransferResult("2", BigDecimal.valueOf(160)),
                new TransferResult("3", BigDecimal.valueOf(80))
        );

        // Setup Executor service threads to run all transfer instructions
        submittedFutures = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(transferInstructions.size());
        for (TransferInstruction i : transferInstructions) {
            executorService.submit(() -> {
                try {
                    service.transferMoney(i.fromAccountNumber, i.toAccountNumber, i.value);
                } catch (InsufficientFundsException | AccountNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Test
    public void run() throws ExecutionException, InterruptedException {
        waitUntilAllTransfersCompleted();
        assertAllTransfers();
    }

    private void assertAllTransfers() {
        for (TransferResult transferResult : transferResults) {
            assertBalance(transferResult.accountNumber, transferResult.balance);
        }
    }

    private void waitUntilAllTransfersCompleted() throws ExecutionException, InterruptedException {
        for (Future<?> future : submittedFutures) {
            future.get();
        }
    }

    private void assertBalance(String accountNumber, BigDecimal balance) {
        Account account = repository.getAccountByAccountNumber(accountNumber).get();
        System.out.println("assert: act[" + accountNumber + "] with balance: " + balance + " actualBalance: " + account.getBalance());
        assert account.getBalance().equals(balance);
    }


    static class TransferInstruction {
        BigDecimal value;
        String fromAccountNumber;
        String toAccountNumber;

        TransferInstruction(String fromAccountNumber, String toAccountNumber, BigDecimal value) {
            this.fromAccountNumber = fromAccountNumber;
            this.toAccountNumber = toAccountNumber;
            this.value = value;
        }
    }

    static class TransferResult {
        String accountNumber;
        BigDecimal balance;

        public TransferResult(String accountNumber, BigDecimal balance) {
            this.accountNumber = accountNumber;
            this.balance = balance;
        }
    }
}
