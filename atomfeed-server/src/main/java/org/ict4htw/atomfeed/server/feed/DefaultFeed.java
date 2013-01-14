package org.ict4htw.atomfeed.server.feed;

import com.sun.syndication.feed.atom.Feed;

public class DefaultFeed extends Feed {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4523761918609092624L;
	private boolean isCacheable = false;

	public boolean isCacheable() {
		return isCacheable;
	}

	public void setCacheable(boolean isCacheable) {
		this.isCacheable = isCacheable;
	}

}
