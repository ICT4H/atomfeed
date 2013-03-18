package org.ict4h.atomfeed.client.api.data;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class EventTest {
    @Test
    public void shouldStripCDATAFromContentsNode(){
        Entry entry = new Entry();
        ArrayList<Content> contents = new ArrayList<Content>();
        Content content = new Content();
        content.setValue(String.format("%s%s%s","<![CDATA[","contents","]]>"));
        contents.add(content);
        entry.setContents(contents);
        Event event = new Event(entry);
        Assert.assertEquals("contents",event.getContent());
    }

    @Test
    public void shouldNotStripCDATAFromWhenContentsAreNotPresent(){
        Entry entry = new Entry();
        Event event = new Event(entry);
        Assert.assertNull(event.getContent());
    }
}
