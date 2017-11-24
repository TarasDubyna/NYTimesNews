package taras.nytimesnews.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Response {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("copyright")
    @Expose
    private String copyright;
    @SerializedName("num_results")
    @Expose
    private int num_results;
    @SerializedName("results")
    @Expose
    private List<Article> results = new ArrayList<Article>();


    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getCopyright() {
        return copyright;
    }
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public int getNum_results() {
        return num_results;
    }
    public void setNum_results(int num_results) {
        this.num_results = num_results;
    }

    public List<Article> getResults() {
        return results;
    }
    public void setResults(List<Article> results) {
        this.results = results;
    }
}
