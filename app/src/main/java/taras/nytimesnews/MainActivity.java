package taras.nytimesnews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import taras.nytimesnews.Models.Article;
import taras.nytimesnews.Models.Media;
import taras.nytimesnews.Network.NetworkConnection;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mTopSelectorRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkConnection networkConnection = new NetworkConnection.BuildRequestParam()
                .section("Arts")
                .mostParam(NetworkConnection.MOST_VIEWED)
                .timePeriod(1)
                .build();

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(
                networkConnection.getRequestUrl(),
                networkConnection.getRequestApiKey(),
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if (responseString != null){
                            Gson gson = new GsonBuilder().create();
                            JsonObject jsonObject = gson.fromJson(responseString, JsonObject.class);
                            if (jsonObject.has("results")){
                                JsonArray jsonResultArray = jsonObject.getAsJsonArray("results");
                                if (jsonResultArray != null){
                                    ArrayList<Article> articles = new ArrayList<>();
                                    for (int i = 0; i < jsonResultArray.size(); i++){
                                        JsonObject object = (JsonObject) jsonResultArray.get(i);
                                        Article article = new Article();
                                        if (object.get("media").toString().equals("\"\"")){
                                            article.setUrl(object.get("url").toString());
                                            article.setTitle(object.get("title").toString());
                                            article.setAbstracts(object.get("abstract").toString());
                                            article.setColumn(object.get("column").toString());
                                            article.setByline(object.get("byline").toString());
                                            //article.setCount_type(object.get("count_type").toString());
                                            article.setPublished_date(object.get("published_date").toString());
                                            article.setSection(object.get("section").toString());
                                            article.setSource(object.get("source").toString());
                                        } else {
                                            article = gson.fromJson(object, Article.class);
                                        }
                                        articles.add(article);
                                    }
                                    String text = null;
                                }

                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        String text = responseString;
                        System.out.println(text);
                    }
                });

    }

    private void initWidgets(){
        mTopSelectorRecycler = findViewById(R.id.top_selector_recycler_view);
    }
}
