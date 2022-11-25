package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model;

public class SliderModel {
    private final int image;
    private final String url;

    public SliderModel(int image, String url) {
        this.image = image;
        this.url = url;
    }

    public int getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }
}
