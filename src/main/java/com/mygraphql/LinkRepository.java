package com.mygraphql;


import com.google.common.base.Strings;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.omg.PortableServer.THREAD_POLICY_ID;

import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.eq;
import java.util.List;
import java.util.ArrayList;

public class LinkRepository {

    private final MongoCollection<Document> links;

    public LinkRepository(MongoCollection<Document> links) {
        this.links = links;
    }

    public List<Link> getAllLinks(final Integer min, final Integer max, String domain){
        System.out.println(String.format("getAllLinksEntry:%s", Utils.getTimeStamp(System.currentTimeMillis())));
        List<Link> allLinks = new ArrayList<>();
        for (Document doc : links.find()) {
            allLinks.add(link(doc));
        }
        System.out.println(String.format("allLinks:%s min:%s max:%s, domain:%s, ts:%s" , allLinks.size(), min,
                max, domain, Utils.getTimeStamp(System.currentTimeMillis())));
        if(allLinks.size() < min) return allLinks;
        List<Link> finalLinks = new ArrayList<>();
        if(!Strings.isNullOrEmpty(domain)){
            for(Link link : allLinks){
                if(link.getUrl().toLowerCase().contains(domain)){
                    finalLinks.add(link);
                }
            }
        }else{
            finalLinks.addAll(allLinks);
        }
        return finalLinks.size() > max? finalLinks.subList(0, max) : finalLinks;
    }

    public void saveLink(Link link) {
        Document doc = new Document();
        doc.append("url", link.getUrl());
        doc.append("description", link.getDescription());
        links.insertOne(doc);
    }

    private Link link(Document doc) {
        return new Link(
                doc.get("_id").toString(),
                doc.getString("url"),
                doc.getString("description"));
    }
}