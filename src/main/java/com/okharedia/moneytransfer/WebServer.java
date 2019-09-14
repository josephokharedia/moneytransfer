package com.okharedia.moneytransfer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.okharedia.moneytransfer.controller.MoneyTransferController;
import com.okharedia.moneytransfer.controller.MoneyTransferRequest;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class WebServer {

    private static final Integer PORT = 8000;

    private final MoneyTransferController moneyTransferController;

    public WebServer(MoneyTransferController moneyTransferController) {
        this.moneyTransferController = moneyTransferController;
    }

    void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        serveMoneyTransfer(server);

        server.start();
        System.out.println("WebServer started successfully on PORT " + PORT);
    }


    private void serveMoneyTransfer(HttpServer server) {
        server.createContext("/", he -> {

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


    MoneyTransferRequest deserializeMoneyTransferRequest(String request) throws IllegalArgumentException {
        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(request);

        if (!element.isJsonObject()) {
            throw new IllegalArgumentException("Failed to parse request");
        }

        try {
            JsonObject jsonObject = element.getAsJsonObject();
            return MoneyTransferRequest.newRequestBuilder()
                    .fromAccount(jsonObject.get("fromAccount").getAsString())
                    .toAccount(jsonObject.get("toAccount").getAsString())
                    .amount(jsonObject.get("amount").getAsBigDecimal())
                    .build();

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse request", e);
        }
    }

    private String readRequestBody(HttpExchange he) throws IOException {
        int contentLength = Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"));
        InputStream is = he.getRequestBody();
        byte[] data = new byte[contentLength];
        int length = is.read(data);
        return new String(data);
    }

    private void failWithInternalServerError(HttpExchange he, Throwable t) throws IOException {
        he.sendResponseHeaders(500, t.getMessage().length());
        OutputStream output = he.getResponseBody();
        output.write(t.getMessage().getBytes());
        output.flush();
        he.close();
    }

    private void failWithMethodNotFoundError(HttpExchange he) throws IOException {
        he.sendResponseHeaders(405, 0);
        he.close();
    }

    private void sendOK(HttpExchange he) throws IOException {
        String response = "OK";
        he.sendResponseHeaders(200, response.length());
        OutputStream output = he.getResponseBody();
        output.write(response.getBytes());
        output.flush();
        he.close();
    }

}
