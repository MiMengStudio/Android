package com.mimeng.values;

import java.io.Serializable;

public class BannerEntity implements Serializable {

    /**
     * title : 迷你x中国国际儿童时尚周，小楼走秀地图上线！
     * image : https://webpicture.mini1.cn/20240725/1715143773149b-c13f-64aa-9a8e-1345cd5ee3f2.jpg
     * url : https://www.mini1.cn/article/20240724/2390.html
     */

    private String title;
    private String image;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
