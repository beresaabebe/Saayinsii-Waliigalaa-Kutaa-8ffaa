package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model;

public class ModelViewPager {
    String title, link;
    int image;

    public ModelViewPager(String title, String link, int image) {
        this.title = title;
        this.link = link;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public int getImage() {
        return image;
    }
}
