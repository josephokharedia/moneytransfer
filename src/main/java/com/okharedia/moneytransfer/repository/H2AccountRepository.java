package com.okharedia.moneytransfer.repository;

import com.okharedia.moneytransfer.domain.Account;
import com.okharedia.moneytransfer.domain.AccountNotFoundException;
import com.okharedia.moneytransfer.domain.StaleAccountException;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

public class H2AccountRepository extends JdbcAccountRepository {

    static final String URL = "jdbc:h2:mem:moneytransfer;DB_CLOSE_DELAY=-1";
    //    static final String URL = "jdbc:h2:~/test";
    static final String USER = "";
    static final String PASSWORD = "";

    JdbcConnectionPool pool;

    @Override
    Connection getConnection() throws SQLException {
        if (pool == null) {
            pool = JdbcConnectionPool.create(getUrl(), getUser(), getPassword());
        }
        return pool.getConnection();
    }

    @Override
    String getUrl() {
        return URL;
    }

    @Override
    String getUser() {
        return USER;
    }

    @Override
    String getPassword() {
        return PASSWORD;
    }


    @Override
    public void saveAtomically(Account... accounts) throws AccountNotFoundException, StaleAccountException {
        try {
            /*
             * H2 cannot handle multi threaded row lock concurrency well.
             * It falls into a trap of detecting deadlocks among connection transactions and fails hard when it finds one.
             * For this reason, I will be using a more pessimistic approach by using application locks to prevent deadlocks
             */
            lock.lock();
            super.saveAtomically(accounts);
        } finally {
            lock.unlock();
        }
    }

    @Override
    void setIsolationLevel(Connection connection, int isolationLevel) throws SQLException {
        try {
            lock.lock();
            if (connection.getTransactionIsolation() != isolationLevel) {
                connection.setTransactionIsolation(isolationLevel);
            }
        } finally {
            lock.unlock();
        }

    }
}
