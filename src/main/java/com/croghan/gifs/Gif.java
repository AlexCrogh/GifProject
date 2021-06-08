package com.croghan.gifs;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import java.io.Serializable;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Gif {

    @Id//@GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String title;
    private String url;
    private String category;
    private boolean posted;

    public Gif(){
    }

    public Gif(int id, String title, String url, String category){
        this.id = id;
        this.title = title;
        this.url = url;
        this.category = category;
        this.posted = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public boolean isPosted() {
        return posted;
    }

    public void setPosted(boolean posted) {
        this.posted = posted;
    }
}
