package taras.nytimesnews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import taras.nytimesnews.Adapters.TopSelectorRecyclerAdapter;
import taras.nytimesnews.Models.Article;
import taras.nytimesnews.Network.NetworkConnection;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView mTopSelectorRecycler;
    private TopSelectorRecyclerAdapter topSelectorTopSelectorRecyclerAdapter;

    private String selectedSectionName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initWidgets();




    }

    private void initWidgets(){
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("Most viewed");

        mTopSelectorRecycler = (RecyclerView) findViewById(R.id.top_selector_recycler_view);
        mTopSelectorRecycler.setHasFixedSize(true);
        mTopSelectorRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        topSelectorTopSelectorRecyclerAdapter = new TopSelectorRecyclerAdapter(this);
        mTopSelectorRecycler.setAdapter(topSelectorTopSelectorRecyclerAdapter);
    }

    public void onTopSelectorClickCalled(String value){
        Toast.makeText(this, "MainActivity: " + value, Toast.LENGTH_SHORT).show();
        getDataFromInternet(value, 1);
    }

    public void getDataFromInternet(String section, int timePeriod){

        NetworkConnection networkConnection = new NetworkConnection.BuildRequestParam()
                .section(section)
                .mostParam(NetworkConnection.MOST_VIEWED)
                .timePeriod(timePeriod)
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
                                    Toast.makeText(MainActivity.this, "ArticleList size: " + articles.size(), Toast.LENGTH_SHORT).show();
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
}
