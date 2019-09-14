package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountRepository;
import com.okharedia.moneytransfer.domain.StaleAccountException;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class H2DatabaseAccountRepository implements AccountRepository {

    private static final String URL = "jdbc:h2:mem:moneytransfer;DB_CLOSE_DELAY=-1";
    private static final String USER = "";
    private static final String PASSWORD = "";
    private static final String COL_BALANCE = "balance";
    private static final String COL_VERSION = "version";

    @SuppressWarnings("SqlResolve")
    private static final String FIND_ACCOUNT_BY_ACCOUNT_NUMBER =
            "select account_number, balance, version from account where account_number=?";
    @SuppressWarnings("SqlResolve")
    private static final String UPDATE_ACCOUNT_BALANCE =
            "update account set balance=?, version=version+1 where account_number=? and version=?";


    JdbcConnectionPool pool;
    ReentrantLock lock = new ReentrantLock();


    public H2DatabaseAccountRepository() {
        Flyway.configure()
                .dataSource(URL, USER, PASSWORD)
                .load()
                .migrate();

        pool = JdbcConnectionPool.create(URL, USER, PASSWORD);
    }


    @Override
    public void saveAtomically(Account... accounts) throws StaleAccountException {
        Connection connection = null;
        try {

            connection = pool.getConnection();

            connection.setAutoCommit(false);

            lock.lock();
            for (Account account : accounts) {

                PreparedStatement pstmt = connection.prepareStatement(UPDATE_ACCOUNT_BALANCE);
                pstmt.setBigDecimal(1, account.getBalance());
                pstmt.setString(2, account.getAccountNumber());
                pstmt.setInt(3, account.getVersion());

                int count = pstmt.executeUpdate();
                if (count != 1) {
                    throw new StaleAccountException(account.getAccountNumber());
                }
            }

            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                try {

                    connection.rollback();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
            if (connection != null) {
                try {

                    connection.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public Optional<Account> getAccount(String accountNumber) {
        Connection connection = null;
        Account account = null;
        ResultSet resultSet = null;
        try {

            connection = pool.getConnection();

            PreparedStatement pstmt = connection.prepareStatement(FIND_ACCOUNT_BY_ACCOUNT_NUMBER);
            pstmt.setString(1, accountNumber);
            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                account = new Account(accountNumber);
                account.setBalance(resultSet.getBigDecimal(COL_BALANCE));
                account.setVersion(resultSet.getInt(COL_VERSION));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {

                    if (resultSet != null) {
                        resultSet.close();
                    }
                    connection.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return Optional.ofNullable(account);
    }
}
