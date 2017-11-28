package taras.nytimesnews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import taras.nytimesnews.Adapters.ArticleNewsRecyclerAdapter;
import taras.nytimesnews.Adapters.TopSelectorRecyclerAdapter;
import taras.nytimesnews.Models.Article;
import taras.nytimesnews.Models.MediaParam;
import taras.nytimesnews.Network.NetworkConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    LinearLayout linearLayoutContent;
    LinearLayout linearLayoutRecyclerView;
    LinearLayout progressBarLayout;


    private RecyclerView mTopSelectorRecycler;
    private RecyclerView mArticleRecycler;
    private TopSelectorRecyclerAdapter topSelectorTopSelectorRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        this.initWidgets();
    }

    private void initWidgets(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("Most viewed");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        linearLayoutContent = findViewById(R.id.main_content_layout);
        linearLayoutRecyclerView = new LinearLayout(this);
        linearLayoutRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mTopSelectorRecycler = new RecyclerView(this);
        TopSelectorRecyclerAdapter topSelectorRecyclerAdapter = new TopSelectorRecyclerAdapter(this);
        mTopSelectorRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTopSelectorRecycler.setAdapter(topSelectorRecyclerAdapter);
        mTopSelectorRecycler.setBackgroundColor(getResources().getColor(R.color.grey));
        this.linearLayoutContent.addView(mTopSelectorRecycler);
        this.linearLayoutContent.addView(linearLayoutRecyclerView);
    }

    public void onTopSelectorClickCalled(String value){
        getDataFromInternet(value, 1);
    }

    public void getDataFromInternet(String section, int timePeriod){

        linearLayoutRecyclerView.removeAllViews();
        ProgressBar progressBar = new ProgressBar(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        layoutParams.gravity = Gravity.CENTER;
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(this.getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setLayoutParams(layoutParams);
        progressBarLayout = new LinearLayout(this);
        progressBarLayout.setGravity(Gravity.CENTER);
        progressBarLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        progressBarLayout.addView(progressBar);
        linearLayoutRecyclerView.addView(progressBarLayout);

        NetworkConnection networkConnection = new NetworkConnection.BuildRequestParam()
                .typeRequest("mostpopular")
                .mostPopularParams("mostemailed", section, timePeriod)
                .createUrl();
        networkConnection.createRequest(new TextHttpResponseHandler() {
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
            linearLayoutRecyclerView.removeView(progressBarLayout);
            if (articleList.size() == 0){
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                TextView textView = new TextView(this);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                textView.setText("No articles!");
                this.linearLayoutRecyclerView.addView(textView);
            } else {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                mArticleRecycler = new RecyclerView(this);
                ArticleNewsRecyclerAdapter adapter = new ArticleNewsRecyclerAdapter(this, articleList);
                mArticleRecycler.setLayoutManager(layoutManager);
                mArticleRecycler.setLayoutParams(layoutParams);
                mArticleRecycler.setAdapter(adapter);
                this.linearLayoutRecyclerView.addView(mArticleRecycler);
            }
        } else {
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
