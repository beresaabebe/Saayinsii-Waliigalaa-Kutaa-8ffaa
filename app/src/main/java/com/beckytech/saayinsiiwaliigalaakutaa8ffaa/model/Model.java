package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model;

import java.io.Serializable;

public class Model implements Serializable {
    private final String title;
    private final String subtitle;
    private final int pageStart;
    private final int pageEnd;

    public Model(String title, String subtitle, int start, int end) {
        this.title = title;
        this.subtitle = subtitle;
        this.pageStart = start;
        this.pageEnd = end;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getPageStart() {
        return pageStart;
    }

    public int getPageEnd() {
        return pageEnd;
    }
}
