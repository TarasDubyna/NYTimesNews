package taras.nytimesnews.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Media {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("subtype")
    @Expose
    private String subtype;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("copyright")
    @Expose
    private String copyright;
    @SerializedName("media-metadata")
    @Expose
    private List<MediaParam> mediaParam = new ArrayList<MediaParam>();


    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }
    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCopyright() {
        return copyright;
    }
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public List<MediaParam> getMedia() {
        return mediaParam;
    }
    public void setMedia(List<MediaParam> mediaParam) {
        this.mediaParam = mediaParam;
    }
}
