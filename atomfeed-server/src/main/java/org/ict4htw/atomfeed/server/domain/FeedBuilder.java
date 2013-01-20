package org.ict4htw.atomfeed.server.domain;

import com.sun.syndication.feed.atom.*;

import java.util.Date;
import java.util.List;

public class FeedBuilder {

    private Feed feed;

    public FeedBuilder() {
        feed = new Feed();
    }

    public FeedBuilder type(String type) {
        feed.setFeedType(type);
        return this;
    }

    public FeedBuilder id(String id) {
        feed.setId(id);
        return this;
    }

    public FeedBuilder title(String title) {
        feed.setTitle(title);
        return this;
    }

    public FeedBuilder generator(Generator generator) {
        feed.setGenerator(generator);
        return this;
    }

    public FeedBuilder authors(List<Person> authors) {
        feed.setAuthors(authors);
        return this;
    }

    public FeedBuilder entries(List<Entry> entries) {
        feed.setEntries(entries);
        return this;
    }

    public FeedBuilder updated(Date updatedAt) {
        feed.setUpdated(updatedAt);
        return this;
    }

    public FeedBuilder link(Link link) {
        feed.getAlternateLinks().add(link);
        return this;
    }

    public FeedBuilder links(List<Link> links) {
        feed.getAlternateLinks().addAll(links);
        return this;
    }

    public Feed build() {
        return feed;
    }
}
