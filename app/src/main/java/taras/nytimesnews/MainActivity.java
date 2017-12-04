package taras.nytimesnews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.w3c.dom.Text;

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

    LayoutWorkplaceManager layoutWorkplaceManager;

    Toolbar toolbar;

    String typeRequest = "mostpopular/v2/";
    String section = "mostviewed";
    int timePeriod = 1;
    int idCheckedNavMenu = 1;

    TextView toolbarTitleTextView;
    TextView toolbarSupportTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initNavigationDrawer();
        this.initWorkplaceLayout();
    }

    private void initWorkplaceLayout(){
        layoutWorkplaceManager = new LayoutWorkplaceManager
                .WorkplaceBuilder(this)
                .contentLayout((LinearLayout) findViewById(R.id.main_content_layout))
                .supportLayout((LinearLayout) findViewById(R.id.main_support_layout))
                .build();

        layoutWorkplaceManager.createView(LayoutWorkplaceManager.SUPPORT, LayoutWorkplaceManager.TOP_SELECTOR_RECYCLER_VIEW);
    }
    private void initNavigationDrawer(){
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarTitleTextView = findViewById(R.id.toolbar_title_text);
        toolbarSupportTextView = findViewById(R.id.toolbar_support_text);
        toolbarTitleTextView.setText(R.string.mostviewed);
        String text = getResources().getString(R.string.time_period_text) + " " + timePeriod + " day";
        toolbarSupportTextView.setText(text);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    public void onTopSelectorClickCalled(String value){
        getDataFromInternet(typeRequest, value, timePeriod);
    }

    public void getDataFromInternet(String typeRequest, String section, int timePeriod){
        layoutWorkplaceManager.createView(LayoutWorkplaceManager.CONTENT, LayoutWorkplaceManager.PROGRESS_VIEW);
        NetworkConnection networkConnection = new NetworkConnection.BuildRequestParam()
                .typeRequest(typeRequest)
                .mostPopularParams(NetworkConnection.MOST_MAILED, section, timePeriod)
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
                } else {
                    System.out.println("response == null");
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
                layoutWorkplaceManager.addStringResource(R.string.no_articles)
                        .createView(LayoutWorkplaceManager.CONTENT, LayoutWorkplaceManager.ERROR_VIEW);

            } else {
                layoutWorkplaceManager.addArticleArrayList(articleList)
                        .createView(LayoutWorkplaceManager.CONTENT, LayoutWorkplaceManager.ARTICLE_RECYCLER_VIEW);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_time_period, menu);
        menu.getItem(0).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String text = null;
        switch (id){
            case R.id.time_period_1:
                timePeriod = 1;
                text = getResources().getString(R.string.time_period_text) + " " + timePeriod + " day";
                break;
            case R.id.time_period_7:
                timePeriod = 7;
                text = getResources().getString(R.string.time_period_text) + " " + timePeriod + " days";
                break;
            case R.id.time_period_30:
                timePeriod = 30;
                text = getResources().getString(R.string.time_period_text) + " " + timePeriod + " days";
                break;
        }
        toolbarSupportTextView.setText(text);
        getDataFromInternet(typeRequest, section, timePeriod);
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (idCheckedNavMenu != id){
            switch (id){
                case R.id.nav_favorite:
                    break;
                case R.id.nav_mostviewed:
                    toolbarTitleTextView.setText(R.string.mostviewed);
                    typeRequest = NetworkConnection.MOST_POPULAR_REQUEST;
                    section = NetworkConnection.MOST_VIEWED;
                    getDataFromInternet(typeRequest, section , 1);
                    break;
                case R.id.nav_mostmailed:
                    toolbarTitleTextView.setText(R.string.mostemailed);
                    typeRequest = NetworkConnection.MOST_POPULAR_REQUEST;
                    section = NetworkConnection.MOST_MAILED;
                    getDataFromInternet(typeRequest, section , 1);
                    break;
                case R.id.nav_most_shared:
                    toolbarTitleTextView.setText(R.string.mostshared);
                    typeRequest = NetworkConnection.MOST_POPULAR_REQUEST;
                    section = NetworkConnection.MOST_SHARED;
                    getDataFromInternet(typeRequest, section , 1);
                    break;
                case R.id.nav_search:
                    toolbarTitleTextView.setText(R.string.search);
                    typeRequest = NetworkConnection.SEARCH_REQUEST;
                    layoutWorkplaceManager.createView(LayoutWorkplaceManager.SUPPORT, LayoutWorkplaceManager.SEARCH_ARTICLE_VIEW);
                    toolbar.getMenu().clear();
                    //getDataFromInternet(typeRequest, section , 1);
                    break;
                case R.id.nav_archive:
                    toolbarTitleTextView.setText(R.string.search);
                    typeRequest = NetworkConnection.ARCHIVE_REQUEST;
                    layoutWorkplaceManager.createView(LayoutWorkplaceManager.SUPPORT, LayoutWorkplaceManager.ARCHIVE_ARTICLE_VIEW);
                    toolbar.getMenu().clear();
                    break;
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}
