package com.okharedia.moneytransfer.controller;

import java.math.BigDecimal;
import java.util.Objects;

public class MoneyTransferRequest {

    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;

    public static Builder newRequestBuilder() {
        return new Builder();
    }

    private MoneyTransferRequest(Builder builder) {
        Objects.requireNonNull(builder.fromAccount, "fromAccount is required");
        Objects.requireNonNull(builder.toAccount, "toAccount is required");
        Objects.requireNonNull(builder.amount, "amount is required");

        this.fromAccount = builder.fromAccount;
        this.toAccount = builder.toAccount;
        this.amount = builder.amount;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public static class Builder {
        private String fromAccount;
        private String toAccount;
        private BigDecimal amount;

        public Builder fromAccount(String fromAccount) {
            this.fromAccount = fromAccount;
            return this;
        }

        public Builder toAccount(String toAccount) {
            this.toAccount = toAccount;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public MoneyTransferRequest build() {
            return new MoneyTransferRequest(this);
        }
    }
}
