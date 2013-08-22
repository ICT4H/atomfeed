package org.ict4h.atomfeed.client.repository.datasource;

import junit.framework.Assert;
import org.junit.Test;

import java.util.HashMap;

public class ClientCookiesTest {
    @Test
    public void getHttpRequestPropertyValue() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("JSessionId", "1");
        map.put("SomethingElse", "2");
        ClientCookies clientCookies = new ClientCookies(map);
        Assert.assertEquals(" SomethingElse=2 ; JSessionId=1 ", clientCookies.getHttpRequestPropertyValue());
    }
}