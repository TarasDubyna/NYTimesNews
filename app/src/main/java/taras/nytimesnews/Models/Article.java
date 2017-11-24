package taras.nytimesnews.Models;


import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Article {
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("count_type")
    @Expose
    private String count_type;
    @SerializedName("column")
    @Expose
    private String column;
    @SerializedName("section")
    @Expose
    private String section;
    @SerializedName("byline")
    @Expose
    private String byline;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("abstract")
    @Expose
    private String abstracts;
    @SerializedName("published_date")
    @Expose
    private String published_date;
    @SerializedName("source")
    @Expose
    private String source;

    @Nullable
    @SerializedName("media")
    @Expose
    private List<Media> media = new ArrayList<Media>();

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getCount_type() {
        return count_type;
    }
    public void setCount_type(String count_type) {
        this.count_type = count_type;
    }

    public String getColumn() {
        return column;
    }
    public void setColumn(String column) {
        this.column = column;
    }

    public String getSection() {
        return section;
    }
    public void setSection(String section) {
        this.section = section;
    }

    public String getByline() {
        return byline;
    }
    public void setByline(String byline) {
        this.byline = byline;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstracts() {
        return abstracts;
    }
    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public String getPublished_date() {
        return published_date;
    }
    public void setPublished_date(String published_date) {
        this.published_date = published_date;
    }

    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }

    public List<Media> getMedia() {
        return media;
    }
    public void setMedia(List<Media> media) {
        this.media = media;
    }
}
