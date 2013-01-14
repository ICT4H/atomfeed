package org.ict4htw.atomfeed.client.repository.datasource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class WebClient {
    public String fetch(URI uri) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
//        ClientResponse response = client.resource(uri).accept(ATOM_MEDIA_TYPE).get(ClientResponse.class);
//        String responseString = response.getEntity(String.class);

        //some UTF-8 encoded files include a three-byte UTF-8 Byte-order mark
        //strip this off (otherwise we get 'org.xml.sax.SAXParseException: Content is not allowed in prolog')
        return response.getBody();
    }
}