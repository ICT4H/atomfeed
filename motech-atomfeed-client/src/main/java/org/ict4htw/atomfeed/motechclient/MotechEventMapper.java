package org.ict4htw.atomfeed.motechclient;

import com.sun.syndication.feed.atom.Entry;
import org.motechproject.event.MotechEvent;

public interface MotechEventMapper {
    MotechEvent map(Entry event);
}
