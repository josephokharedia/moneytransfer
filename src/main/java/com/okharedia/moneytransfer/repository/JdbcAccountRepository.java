package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.AccountRepository;
import com.okharedia.moneytransfer.domain.StaleAccountException;
import org.flywaydb.core.Flyway;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings({"WeakerAccess"})
abstract class JdbcAccountRepository implements AccountRepository {

    static final String COL_ACCOUNT_NUMBER = "account_number";
    static final String COL_BALANCE = "balance";
    static final String COL_VERSION = "version";
    static final String FIND_ACCOUNT_BY_ACCOUNT_NUMBER = "select account_number, balance, version from account where account_number=?";
    static final String UPDATE_ACCOUNT_BALANCE = "update account set balance=?, version=version+1 where account_number=? and version=?";
    static final String ALL_ACCOUNTS = "select account_number, balance, version from account";

    final ReentrantLock lock = new ReentrantLock();

    JdbcAccountRepository() {
        Flyway.configure()
                .dataSource(getUrl(), getUser(), getPassword())
                .load()
                .migrate();
    }

    @Override
    public void saveAtomically(Account... accounts) throws AccountNotFoundException, StaleAccountException {
        try (Connection connection = getConnection()) {
            boolean oldAutoCommit = connection.getAutoCommit();
            try {

                connection.setAutoCommit(false);

                for (Account account : accounts) {
                    PreparedStatement stmt = connection.prepareStatement(UPDATE_ACCOUNT_BALANCE);
                    stmt.setBigDecimal(1, account.getBalance());
                    stmt.setString(2, account.getAccountNumber());
                    stmt.setInt(3, account.getVersion());
                    if (stmt.executeUpdate() != 1) {
                        throw new StaleAccountException(account.getAccountNumber());
                    }
                }
                connection.commit();
            } catch (SQLException | StaleAccountException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Account> getAccount(String accountNumber) {
        Account account = null;
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_ACCOUNT_BY_ACCOUNT_NUMBER)) {
            stmt.setString(1, accountNumber);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                BigDecimal balance = resultSet.getBigDecimal(COL_BALANCE);
                int version = resultSet.getInt(COL_VERSION);
                account = new Account(accountNumber, balance, version);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(account);
    }

    @Override
    public List<Account> allAccounts() {
        List<Account> accounts = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(ALL_ACCOUNTS)) {
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String accountNumber = resultSet.getString(COL_ACCOUNT_NUMBER);
                BigDecimal balance = resultSet.getBigDecimal(COL_BALANCE);
                int version = resultSet.getInt(COL_VERSION);
                Account account = new Account(accountNumber, balance, version);
                accounts.add(account);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return accounts;
    }

    abstract String getUrl();

    abstract String getUser();

    abstract String getPassword();

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }

    void setIsolationLevel(Connection connection, int isolationLevel) throws SQLException {
        try {
            lock.lock();
            connection.setTransactionIsolation(isolationLevel);
        } finally {
            lock.unlock();
        }
    }

}
