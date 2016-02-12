package org.ict4h.atomfeed.client.repository.datasource;

import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.AtomFeedProperties;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import static java.lang.String.format;

public class DefaultHttpClient implements HttpClient {
    @Override
    public String fetch(URI uri, AtomFeedProperties atomFeedProperties, Map<String, String> clientCookies) {
        HttpURLConnection connection = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/atom+xml");
            connection.addRequestProperty("User-Agent", "Mozilla");
            ClientCookies cookies = new ClientCookies(clientCookies);
            String httpRequestPropertyValue = cookies.getHttpRequestPropertyValue();
            if (httpRequestPropertyValue != null)
                connection.setRequestProperty("Cookie", httpRequestPropertyValue);
            connection.setDoOutput(true);
            connection.setConnectTimeout(atomFeedProperties.getConnectTimeout());
            connection.setReadTimeout(atomFeedProperties.getReadTimeout());
            //don't automatically follow redirection
            connection.setInstanceFollowRedirects(false);

            //connection.connect();
            boolean redirect = false;
            redirect = isRedirectResponse(connection.getResponseCode());
            boolean shouldHandleRedirection = atomFeedProperties.isRedirectionHandled();

            if (redirect && shouldHandleRedirection) {
                String redirectedUrl = connection.getHeaderField("Location");
                String returnedCookie = connection.getHeaderField("Set-Cookie");
                connection.disconnect();
                System.out.println("Redirect to URL : " + redirectedUrl);
                connection = (HttpURLConnection) new URL(redirectedUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/atom+xml");
                connection.addRequestProperty("User-Agent", "Mozilla");
                if ((returnedCookie == null) || "".equals(returnedCookie)) {
                    if (httpRequestPropertyValue != null)
                        connection.setRequestProperty("Cookie", httpRequestPropertyValue);
                } else {
                    connection.setRequestProperty("Cookie", returnedCookie);
                }
                connection.setDoOutput(true);
                connection.setConnectTimeout(atomFeedProperties.getConnectTimeout());
                connection.setReadTimeout(atomFeedProperties.getReadTimeout());
            }

            connection.connect();
            int responseCode  = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + '\n');
                }
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                throw new RuntimeException(format("Resource not found at %s", uri.toString()));
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                throw new RuntimeException("User not authorized");
            } else {
                throw new RuntimeException(format("Unexpected response status %d", responseCode));
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

    private boolean isRedirectResponse(int responseCode) {
        boolean redirect = false;
        if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
            redirect = true;
        }
        return redirect;
    }
}