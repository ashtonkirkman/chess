package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import model.ErrorResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientCommunicator {
    public String doGet(String urlString, String authToken) throws IOException, ResponseException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", authToken); // Set authorization header
        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            return readInputStream(responseBody);
        }
        else {
            // SERVER RETURNED AN HTTP ERROR
            InputStream errorStream = connection.getErrorStream();
            String response = readInputStream(errorStream);
            ErrorResponse errorResponse = new Gson().fromJson(response, ErrorResponse.class);
            var status = connection.getResponseCode();
            throw new ResponseException(status, errorResponse.message());
        }
    }

    public String doPost(String urlString, String requestBody, String authToken) throws IOException, ResponseException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        if (authToken != null && !authToken.isEmpty()) {
            connection.setRequestProperty("Authorization", authToken);
        }

        connection.connect();

        try(OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(requestBody.getBytes());
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            return readInputStream(responseBody);
        }
        else {
            // SERVER RETURNED AN HTTP ERROR
            InputStream errorStream = connection.getErrorStream();
            String response = readInputStream(errorStream);
            ErrorResponse errorResponse = new Gson().fromJson(response, ErrorResponse.class);
            var status = connection.getResponseCode();
            throw new ResponseException(status, errorResponse.message());
        }
    }

    public void doDelete(String urlString, String authToken) throws IOException, ResponseException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", authToken); // Set authorization header
        connection.connect();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            // Handle error response
            InputStream errorStream = connection.getErrorStream();
            String response = readInputStream(errorStream);
            ErrorResponse errorResponse = new Gson().fromJson(response, ErrorResponse.class);
            var status = connection.getResponseCode();
            throw new ResponseException(status, errorResponse.message());
        }
    }

    public void doPut(String urlString, String requestBody, String authToken) throws IOException, ResponseException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        if (authToken != null && !authToken.isEmpty()) {
            connection.setRequestProperty("Authorization", authToken);
        }

        connection.connect();

        try(OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(requestBody.getBytes());
        }

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            InputStream errorStream = connection.getErrorStream();
            String response = readInputStream(errorStream);
            ErrorResponse errorResponse = new Gson().fromJson(response, ErrorResponse.class);
            var status = connection.getResponseCode();
            throw new ResponseException(status, errorResponse.message());
        }
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }
}
