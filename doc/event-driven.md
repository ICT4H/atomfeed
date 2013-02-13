Atom
====

What is Atom?
------------

Atom is an XML-based syndication format. Atom represents time-ordered series of events. In Atom terminology, each event is an *entry* and the series is a *feed*.

Both feeds and entries have metadata associated with them, for example a title and a unique identifier.

    <?xml version="1.0">
        <feed xmlns="http://www.w3.org/2005/Atom">
            <id>urn:uuid:ff31a040-75bc-11e2-bcfd-0800200c9a66</id>
            <title type="text">Recent notifications</title>
            <updated>2013-01-02T10:03:00Z</updated>
            <generator></generator>
            <link rel="self" href="http://example.com/notifications" />
            <link rel="prev" href="http://example.com/feeds/3" />
            <entry>
                <id>urn:uuid:eb3ee5a0-75be-11e2-bcfd-0800200c9a66</id>
                <title type="text">Document edited</title>
                <updated>2013-01-02T10:01:00Z</updated>
                <author>
                    <name>jsmith</name>
                </author>
                <link rel="self" href="http://example.com/entry/33" />
                <content type="application/vnd.example.document+xml">
                    <title>HR policies</title>
                    <link href="http://example.com/cms/doc/456" />
                </content>
            </entry>
            <entry>
                <id>urn:uuid:1d3334a0-75bd-11e2-bcfd-0800200c9a66</id>
                <title type="text">User created</title>
                <updated>2013-01-02T07:31:00Z</updated>
                <author>
                    <name>dgonzales</name>
                </author>
                <link rel="self" href="http://example.com/entry/32" />
                <content type="application/vnd.example.user+xml">
                    <username>jsmith</username>
                    <role>editor</role>
                    <email>jsmith@example.com</email>
                </content>
            </entry>
        </feed>

Atom is optimised and designed for RESTful systems that communicate over HTTP.

Atom is a general-purpose format that can be extended to fit a particular domain. By using a well-understood general-purpose format as a basis, we can reuse tools infrastructure and semantics.

Event-driven systems
--------------------

Atom can be used to implement event-driven systems by adding an entry to the feed every time something happens that subscribers might be interested in. Subscribers find out about these events by polling the feed. When new subscribers arrive, they can simply also start polling the feed without any change needed to the service.

Depending on the nature of the event and the size of the resource that it applies to, the Atom entry could either include a snapshot of the new state of the resource, or simply link to the resource so that the client can issue a fresh GET.

Because Atom is based on polling, it increases latency, which might make it unsuitable for real time systems. However, using a RESTful polling approach decouples client and server, and provides scalability through the opportunity for heavy caching.

Paginating feeds
----------------

As Atom entries are intended to be available indefinitely, the feed of events can grow very large over time. This makes the feed too large for consumers to conveniently GET over a network.

The solution is to break up a single logical feed into many phyisical feeds. As Atom is designed to follow RESTful conventions, Atom does this by means of links.

The server breaks up the series of events into separate physical feeds and gives each of them their own URL. The server might choose to make each physical feed represent a period of time e.g. a day, or might divide the series of events evenly so that each feed has e.g. 100 entries. 

Similar to a doubly-linked list, each physical feed has *prev* and *next* links that can be followed to find the next feed in the chain.

In addition to the URLs for each physical feed, the server also exposes a URL that points to the current working feed. This URL is effectively an alias for the feed that is still under construction.

    <?xml version="1.0">
        <feed xmlns="http://www.w3.org/2005/Atom">
            <id>urn:uuid:ff31a040-75bc-11e2-bcfd-0800200c9a66</id>
            <title type="text">Recent notifications</title>
            <link rel="self" href="http://example.com/notifications" />
            <link rel="prev" href="http://example.com/feeds/3" />
            <entry>..</entry>
            <entry>..</entry>
            <entry>..</entry>
        </feed>

This is the published entry point to the feed. Clients of the event feed will always be able to dereference http://example.com/notifications to get the most recent entries.

This resource would most likely not allow clients to cache it, as it changes every time a new entry comes along (though this could depend on the specific requirements of the system). 

Clients who are interested in older entries can follow the "prev" link to the previous feed.

    <?xml version="1.0">
        <feed xmlns="http://www.w3.org/2005/Atom">
            <id>urn:uuid:ff31a040-75bc-11e2-bcfd-0800200c9a66</id>
            <title type="text">Notifications</title>
            <link rel="self" href="http://example.com/feeds/4" />
            <link rel="prev" href="http://example.com/feeds/3" />
            <entry>..</entry>
            <entry>..</entry>
            <entry>..</entry>
        </feed>

This resource is effectively equivalent to the feed entry point. It represents the most recent feed that has not yet been "filled up". 

    <?xml version="1.0">
        <feed xmlns="http://www.w3.org/2005/Atom">
            <id>urn:uuid:ff31a040-75bc-11e2-bcfd-0800200c9a66</id>
            <title type="text">Notifications</title>
            <link rel="self" href="http://example.com/feeds/3" />
            <link rel="next" href="http://example.com/feeds/4" />
            <link rel="prev" href="http://example.com/feeds/2" />
            <entry>..</entry>
            <entry>..</entry>
            <entry>..</entry>
        </feed>

This feed has been finished, and should not change. It can therefore have cache ehaders with a long time-to-live.

Clients wishing to find older or newer entries than the ones in this feed can find them by following the "prev" and "next" links respectively. 

    <?xml version="1.0">
        <feed xmlns="http://www.w3.org/2005/Atom">
            <id>urn:uuid:ff31a040-75bc-11e2-bcfd-0800200c9a66</id>
            <title type="text">Notifications</title>
            <link rel="self" href="http://example.com/feeds/2" />
            <link rel="next" href="http://example.com/feeds/3" />
            <link rel="prev" href="http://example.com/feeds/1" />
            <entry>..</entry>
            <entry>..</entry>
            <entry>..</entry>
        </feed>

This is another finished feed, and can also be heavily cached. Finding older or newer entries is again a matter of following the "prev" or "next" links.

Consuming feeds
---------------

A client of an Atom feed keeps track of the unique identifier of the most recent entry it has processed. Because the entries are time ordered, and the only change is to add new entries onto the front of the feed, clients can work backwards till they find the oldest entry they have not yet processed, and then work forwards through the feed processing each event in turn. 
