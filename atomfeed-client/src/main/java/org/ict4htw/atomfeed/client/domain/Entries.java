package org.ict4htw.atomfeed.client.domain;

import java.util.ArrayList;
import java.util.List;

import com.sun.syndication.feed.atom.Entry;

public class Entries {
    private List list;

    public Entries(List list) {
        this.list = list;
    }

    public Entry getEntryWith(String feedEntryId) {
        if (feedEntryId == null) return null;

        for (Object object : list) {
            Entry entry = (Entry) object;
            if (feedEntryId.equals(entry.getId())) {
                return entry;
            }
        }
        return null;
    }

    public List<Entry> newerEntries(String feedEntryId) {
    	List<Entry> newEntries = new ArrayList<Entry>();
    	for (int i=list.size()-1; i>=0; i--) {
    		Entry entry = (Entry) list.get(i);
    		if ((feedEntryId != null) && feedEntryId.equals(entry.getId())) {
                break;
            }
            newEntries.add(entry);
    	}
    	return newEntries;
    }
}