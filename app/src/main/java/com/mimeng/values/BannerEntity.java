package com.mimeng.values;

import java.io.Serializable;

public class BannerEntity implements Serializable {
    private String image;
    private String link;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
