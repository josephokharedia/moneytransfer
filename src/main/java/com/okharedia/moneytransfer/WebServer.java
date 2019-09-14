package com.okharedia.moneytransfer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.okharedia.moneytransfer.controller.MoneyTransferController;
import com.okharedia.moneytransfer.controller.MoneyTransferRequest;
import com.okharedia.moneytransfer.domain.Account;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

class WebServer {

    static final Integer PORT = 8000;
    final MoneyTransferController moneyTransferController;

    WebServer(MoneyTransferController moneyTransferController) {
        this.moneyTransferController = moneyTransferController;
    }

    void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        serveAccounts(server);
        serveMoneyTransfer(server);

        server.start();
        System.out.println("WebServer started successfully on PORT " + PORT);
    }


    void serveMoneyTransfer(HttpServer server) {
        server.createContext("/transfer", he -> {

            if (!he.getRequestMethod().equalsIgnoreCase("POST")) {
                failWithMethodNotFoundError(he);
                return;
            }

            try {
                String request = readRequestBody(he);

                MoneyTransferRequest moneyTransferRequest = deserializeMoneyTransferRequest(request);

                moneyTransferController.transferMoney(moneyTransferRequest);

                sendOK(he);

            } catch (Exception e) {
                failWithInternalServerError(he, e);
            }

        });
    }

    void serveAccounts(HttpServer server) {
        server.createContext("/accounts", he -> {

            if (!he.getRequestMethod().equalsIgnoreCase("GET")) {
                failWithMethodNotFoundError(he);
                return;
            }

            try {
                List<Account> accounts = moneyTransferController.allAccounts();

                String response = serializeAccounts(accounts);

                sendResponse(he, response);

            } catch (Exception e) {
                failWithInternalServerError(he, e);
            }

        });
    }


    String serializeAccounts(List<Account> accounts) {
        Gson gson = new Gson();
        return gson.toJson(accounts);
    }


    MoneyTransferRequest deserializeMoneyTransferRequest(String request) throws IllegalArgumentException {
        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(request);

        if (!element.isJsonObject()) {
            throw new IllegalArgumentException("Failed to parse request: request is not a Json object");
        }


        try {
            final JsonObject jsonObject = element.getAsJsonObject();

            Function<String, String> getFieldAsStringOrNull = field ->
                    Optional.ofNullable(jsonObject.get(field)).map(JsonElement::getAsString).orElse(null);

            Function<String, BigDecimal> getFieldAsBigDecimalOrNull = field ->
                    Optional.ofNullable(jsonObject.get(field)).map(JsonElement::getAsBigDecimal).orElse(null);

            return MoneyTransferRequest.newRequestBuilder()
                    .fromAccount(getFieldAsStringOrNull.apply("fromAccount"))
                    .toAccount(getFieldAsStringOrNull.apply("toAccount"))
                    .amount(getFieldAsBigDecimalOrNull.apply("amount"))
                    .build();

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse request: " + e.getMessage(), e);
        }
    }

    String readRequestBody(HttpExchange he) throws IOException {
        int contentLength = Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"));
        InputStream is = he.getRequestBody();
        byte[] data = new byte[contentLength];
        int length = is.read(data);
        return new String(data);
    }

    void failWithInternalServerError(HttpExchange he, Throwable t) throws IOException {
        he.sendResponseHeaders(500, t.getMessage().length());
        OutputStream output = he.getResponseBody();
        output.write(t.getMessage().getBytes());
        output.flush();
        he.close();
    }

    void failWithMethodNotFoundError(HttpExchange he) throws IOException {
        he.sendResponseHeaders(405, 0);
        he.close();
    }

    void sendOK(HttpExchange he) throws IOException {
        String response = "OK";
        he.sendResponseHeaders(200, response.length());
        OutputStream output = he.getResponseBody();
        output.write(response.getBytes());
        output.flush();
        he.close();
    }

    void sendResponse(HttpExchange he, String response) throws IOException {
        he.getResponseHeaders().put("Content-Type", Collections.singletonList("application/json"));
        he.sendResponseHeaders(200, response.length());
        OutputStream output = he.getResponseBody();
        output.write(response.getBytes());
        output.flush();
        he.close();
    }

}
