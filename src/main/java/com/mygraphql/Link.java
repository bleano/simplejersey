package com.mygraphql;

public class Link {

    private final String id; //the new field
    private final String url;
    private final String description;

    public Link(String url, String description) {
        this(null, url, description);
    }

    public Link(String id, String url, String description) {
        this.id = id;
        this.url = url;
        this.description = description;
    }

    public String getId() {
        System.out.println(String.format("getId:%s, ts:%s", id,
                Utils.getTimeStamp(System.currentTimeMillis())));
        return id;
    }

    public String getUrl() {
        System.out.println(String.format("getUrl:%s, ts:%s", url,
                Utils.getTimeStamp(System.currentTimeMillis())));
        return url;
    }

    public String getDescription() {
        System.out.println(String.format("description:%s, ts:%s", description,
                Utils.getTimeStamp(System.currentTimeMillis())));
        return description;
    }

}
