package taras.nytimesnews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import taras.nytimesnews.Adapters.ArticleNewsRecyclerAdapter;
import taras.nytimesnews.Adapters.TopSelectorRecyclerAdapter;
import taras.nytimesnews.Models.Article;
import taras.nytimesnews.Models.MediaParam;
import taras.nytimesnews.Network.NetworkConnection;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    LinearLayout linearLayout;

    private RecyclerView mTopSelectorRecycler;

    private TopSelectorRecyclerAdapter topSelectorTopSelectorRecyclerAdapter;

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

        linearLayout = findViewById(R.id.main_layout);

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

        linearLayout.removeAllViews();
        ProgressBar progressBar = new ProgressBar(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        layoutParams.gravity = Gravity.CENTER;
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(this.getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setLayoutParams(layoutParams);
        LinearLayout progressBarLayout = new LinearLayout(this);
        progressBarLayout.setGravity(Gravity.CENTER);
        progressBarLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        progressBarLayout.addView(progressBar);
        linearLayout.addView(progressBarLayout);

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
                                            article.setPublished_date(object.get("published_date").toString());
                                            article.setSection(object.get("section").toString());
                                            article.setSource(object.get("source").toString());
                                        } else {
                                            article = gson.fromJson(object, Article.class);
                                            MediaParam mediaParam = article.getMedia().get(0).getMediaParam().get(article.getMedia().get(0).getMediaParam().size() - 1);
                                            article.setBitmapImage(getBitmapFromURL(mediaParam.getUrl()));
                                        }
                                        articles.add(article);
                                    }
                                    createViewByResponse(articles);
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

    public static Bitmap getBitmapFromURL(String src) {
        final Bitmap[] myBitmap = new Bitmap[1];
        try {
            final URL url = new URL(src);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        myBitmap[0] = BitmapFactory.decodeStream(input);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (IOException e) {
            // Log exception
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return myBitmap[0];
    }

    private void createViewByResponse(Object value){
        if (value.getClass().equals(ArrayList.class)){
            ArrayList<Article> articleList = (ArrayList<Article>) value;
            value = null;
            if (articleList.size() == 0){
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                TextView textView = new TextView(this);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                textView.setText("No articles!");
                linearLayout.removeAllViews();
                this.linearLayout.addView(textView);
            } else {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                RecyclerView recyclerView = new RecyclerView(this);
                recyclerView.setLayoutParams(layoutParams);
                recyclerView.setLayoutManager(layoutManager);
                ArticleNewsRecyclerAdapter adapter = new ArticleNewsRecyclerAdapter(this, articleList);
                recyclerView.setAdapter(adapter);
                linearLayout.removeAllViews();
                this.linearLayout.addView(recyclerView);
            }
        } else {
        }

    }
}
