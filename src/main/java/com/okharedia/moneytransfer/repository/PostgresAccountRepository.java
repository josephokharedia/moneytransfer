package com.okharedia.moneytransfer.repository;

public class PostgresAccountRepository extends JdbcAccountRepository {

    static final String URL = "jdbc:postgresql://localhost/postgres";
    static final String USER = "postgres";
    static final String PASSWORD = "postgres";

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
}
