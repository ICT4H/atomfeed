package org.ict4htw.atomfeed.motechclient;

import org.ict4htw.atomfeed.client.domain.Marker;
import org.ict4htw.atomfeed.client.repository.datasource.MarkerDataSource;

import java.net.URI;
import java.util.HashMap;


/**
 * For sample Marker Datasource
 */
public class InmemoryMarkerDataSource implements MarkerDataSource{
    private HashMap<URI, Marker> map=new HashMap<URI, Marker>();

    @Override
    public Marker get(URI feedUri) {
        return map.get(feedUri);
    }

    @Override
    public void put(Marker marker) {
        map.put(marker.getFeedUri(), marker);
    }
}
