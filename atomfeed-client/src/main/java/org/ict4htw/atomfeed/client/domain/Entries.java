package org.ict4htw.atomfeed.client.domain;

import com.sun.syndication.feed.atom.Entry;

import java.util.ArrayList;
import java.util.List;

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
        List<Entry> entries = new ArrayList<Entry>();
        for (Object object : list) {
            Entry entry = (Entry) object;
            entries.add(entry);
            if (feedEntryId.equals(entry.getId())) {
                break;
            }
        }
        return entries;
    }
}