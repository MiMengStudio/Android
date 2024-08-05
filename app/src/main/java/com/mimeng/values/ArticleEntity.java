package com.mimeng.values;

import java.io.Serializable;
import java.util.List;

public class ArticleEntity implements Serializable {

    /**
     * _id : 66a792cf114844622fc1b853
     * id : 1
     * author : Test123
     * head : https://q1.qlogo.cn/g?b=qq&nk=3224815186&s=100
     * publishDate : 1722241750546
     * updateDate : 1722241750546
     * title : 测试文章标题
     * tags : ["教程","脚本","日常","Lua","触发器","测试"]
     * official : true
     * selected : true
     * locked : false
     * outline : 测试文章概要
     * images : []
     * views : 0
     * likes : 0
     * commentCounter : 1
     * shares : 0
     */

    private String _id;
    private int id;
    private String author;
    private String name;
    private String head;
    private long publishDate;
    private long updateDate;
    private String title;
    private boolean official;
    private boolean selected;
    private boolean locked;
    private String outline;
    private int views;
    private int likes;
    private int commentCounter;
    private int shares;
    private List<String> tags;
    private List<String> images;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getOutline() {
        return outline;
    }

    public void setOutline(String outline) {
        this.outline = outline;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getCommentCounter() {
        return commentCounter;
    }

    public void setCommentCounter(int commentCounter) {
        this.commentCounter = commentCounter;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
