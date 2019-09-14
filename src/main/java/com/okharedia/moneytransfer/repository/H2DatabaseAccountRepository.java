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

    static final String URL = "jdbc:h2:mem:moneytransfer;DB_CLOSE_DELAY=-1";
    static final String USER = "";
    static final String PASSWORD = "";
    static final String COL_BALANCE = "BALANCE";
    static final String COL_VERSION = "VERSION";
    @SuppressWarnings("SqlResolve")
    static final String FIND_ACCOUNT_BY_ACCOUNT_NUMBER =
            "select account_number, balance, version from account where account_number=?";
    @SuppressWarnings("SqlResolve")
    static final String UPDATE_ACCOUNT_BALANCE =
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
    public void saveAtomically(Account... accounts) throws AccountNotFoundException {
    }

    @Override
    public Optional<Account> getAccount(String accountNumber) {
        return Optional.empty();
    }
}
