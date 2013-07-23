package org.ict4h.atomfeed.client.repository.datasource;

import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.factory.AtomFeedProperties;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class WebClient {
    public String fetch(URI uri, AtomFeedProperties atomFeedProperties) {
        HttpURLConnection connection = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/atom+xml");
            connection.setDoOutput(true);
            connection.setConnectTimeout(atomFeedProperties.getConnectTimeout());
            connection.setReadTimeout(atomFeedProperties.getReadTimeout());
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + '\n');
            }
        } catch (Exception e) {
            throw new AtomFeedClientException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return stringBuilder.toString();
    }
}