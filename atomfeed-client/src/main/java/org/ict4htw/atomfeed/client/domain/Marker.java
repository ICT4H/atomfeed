package org.ict4htw.atomfeed.client.domain;

public class Marker {
    private String entryId;
    private String consumerId;

    public Marker(String consumerId, String entryId) {
		this.consumerId = consumerId;
		this.entryId = entryId;
	}

	public String getEntryId() {
        return entryId;
    }
	
	public String getConsumerId() {
		return this.consumerId;
	}

	//?? do we need this setter
    public void setFeedEntryId(String feedEntryId) {
        this.entryId = feedEntryId;
    }
	
		
}