package com.okharedia.moneytransfer.loadtest;

import com.okharedia.moneytransfer.domain.*;
import com.okharedia.moneytransfer.domain.internal.DefaultMoneyTransferService;
import com.okharedia.moneytransfer.repository.DefaultAccountRepository;
import com.okharedia.moneytransfer.repository.H2DatabaseAccountRepository;
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

    List<TransferInstruction> transferInstructions;
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
    }

    @Test
    public void testDefaultRepository() throws ExecutionException, InterruptedException {
        List<Account> testAccounts = new ArrayList<>();

        // Add test accounts
        IntStream.rangeClosed(1, 3).forEach(n -> {
            Account account = new Account(String.valueOf(n));
            account.setBalance(BigDecimal.valueOf(100));
            testAccounts.add(account);
        });

        AccountRepository repository = new DefaultAccountRepository(testAccounts);
        MoneyTransferService service = new DefaultMoneyTransferService(repository);

        submitAllTransferInstructions(service);
        waitUntilAllTransfersCompleted();
        assertAllTransfers(repository);
    }

    @Test
    public void testH2DatabaseRepository() throws ExecutionException, InterruptedException {
        AccountRepository repository = new H2DatabaseAccountRepository();
        MoneyTransferService service = new DefaultMoneyTransferService(repository);

        submitAllTransferInstructions(service);
        waitUntilAllTransfersCompleted();
        assertAllTransfers(repository);
    }

    private void submitAllTransferInstructions(MoneyTransferService service) {
        // Setup Executor service threads to run all transfer instructions
        submittedFutures = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(3/*transferInstructions.size()*/);
        for (TransferInstruction i : transferInstructions) {
            Future<?> future = executorService.submit(() -> {
                try {
                    service.transferMoney(i.fromAccountNumber, i.toAccountNumber, i.value);
                } catch (InsufficientFundsException | AccountNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            submittedFutures.add(future);
        }
    }

    private void assertAllTransfers(AccountRepository repository) {
        for (TransferResult transferResult : transferResults) {
            assertBalance(repository, transferResult.accountNumber, transferResult.balance);
        }
    }

    private void waitUntilAllTransfersCompleted() throws ExecutionException, InterruptedException {
        for (Future<?> future : submittedFutures) {
            future.get();
        }
    }

    private void assertBalance(AccountRepository repository, String accountNumber, BigDecimal balance) {
        Account account = repository.getAccount(accountNumber).get();
        System.out.println("assert: act[" + accountNumber + "] with balance: " + balance + " actualBalance: " + account.getBalance());
        assert account.getBalance().compareTo(balance) == 0;
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
