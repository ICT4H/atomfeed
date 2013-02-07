package com.motech.atomfeed;

import org.ict4htw.atomfeed.client.api.data.Event;
import org.ict4htw.atomfeed.client.repository.datasource.WebClient;
import org.ict4htw.atomfeed.motechclient.AtomClient;
import org.ict4htw.atomfeed.motechclient.EventToMotechEventMapper;
import org.motechproject.event.MotechEvent;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.net.URI;
import java.net.URISyntaxException;

public class Activator implements BundleActivator{

    @Override
    public void start(BundleContext bundleContext) {
        try {
            URI startURI= null;
            startURI = new URI("http://localhost:8080/events/recent");
            WebClient webClient = new WebClient();
            EventToMotechEventMapper eventToMotechEventMapper = new EventToMotechEventMapper() {
                @Override
                public MotechEvent map(Event event) {
                    return null;
                }
            };
            new AtomClient(startURI, webClient, eventToMotechEventMapper);
        } catch (URISyntaxException e) {

        }
    }

    @Override
    public void stop(BundleContext bundleContext){

    }
}
