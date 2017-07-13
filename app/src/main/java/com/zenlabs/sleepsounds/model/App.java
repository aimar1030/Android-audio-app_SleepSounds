package com.zenlabs.sleepsounds.model;

/**
 * Created by fedoro on 5/12/16.
 */
public class App {

    private String name;
    private String image;
    private String url;
    private String desc;
    private String panelImage;

    public App() {

    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPanelImage() {
        return panelImage;
    }

    public void setPanelImage(String panelImage) {
        this.panelImage = panelImage;
    }

    @Override
    public String toString() {
        return "App{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", url='" + url + '\'' +
                ", desc='" + desc + '\'' +
                ", panelImage='" + panelImage + '\'' +
                '}';
    }
}
