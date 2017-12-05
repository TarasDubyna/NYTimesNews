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

    String selectedTypeRequest = null;

    String typeRequest = "mostviewed/";
    String section = "all-sections";
    int timePeriod = 1;
    int idCheckedNavMenu = R.id.nav_mostviewed;

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
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void onTopSelectorMostPopularCalled(String value){
        section = value;
        getMostPopularRequest(typeRequest, value, timePeriod);
    }


    public void getDataFromInternet(NetworkConnection networkConnection, final String typeRequest){
        layoutWorkplaceManager.createView(LayoutWorkplaceManager.CONTENT, LayoutWorkplaceManager.PROGRESS_VIEW);
        networkConnection.createRequest(new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                if (responseString != null){
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(responseString, JsonObject.class);
                    switch (typeRequest){
                        case NetworkConnection.MOST_POPULAR_REQUEST:
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
                            break;
                        case NetworkConnection.SEARCH_REQUEST:
                            JsonObject responseJsonObject = jsonObject.getAsJsonObject("response");
                            JsonArray docsJsonArray = responseJsonObject.getAsJsonArray("docs");

                            ArrayList<Article> articles = new ArrayList<>();
                            for (int i = 0; i < docsJsonArray.size(); i++){
                                JsonObject object = (JsonObject) docsJsonArray.get(i);
                                Article article = new Article();
                                article.setUrl(object.get("web_url").toString());
                                article.setAbstracts(object.get("snippet").toString());
                                article.setPublished_date(object.get("pub_date").toString());
                                articles.add(article);
                            }
                            createViewByResponse(articles);
                            break;
                    }

                } else {
                    System.out.println("response == null");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                layoutWorkplaceManager.addStringResource(R.string.error_request_url)
                        .createView(LayoutWorkplaceManager.CONTENT, LayoutWorkplaceManager.ERROR_VIEW);
            }
        }, typeRequest);
    }

    public void getSearchRequest(String searchText){
        NetworkConnection networkConnection = new NetworkConnection.BuildRequestParam()
                .searchParams(searchText)
                .createUrl();
        getDataFromInternet(networkConnection, NetworkConnection.SEARCH_REQUEST);
    }

    public void getMostPopularRequest(String typeRequest, String section, int timePeriod){
        NetworkConnection networkConnection = new NetworkConnection.BuildRequestParam()
                .mostPopularParams(typeRequest, section, timePeriod)
                .createUrl();
        getDataFromInternet(networkConnection, NetworkConnection.MOST_POPULAR_REQUEST);
    }

    public void getArchiveRequests(int year, int month){
        NetworkConnection networkConnection = new NetworkConnection.BuildRequestParam()
                .archiveParams(year, month)
                .createUrl();
        getDataFromInternet(networkConnection, NetworkConnection.ARCHIVE_REQUEST);
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
        if (menu.size() == 0){
            getMenuInflater().inflate(R.menu.menu_time_period, menu);
            menu.getItem(0).setChecked(true);
            timePeriod = 1;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String text = null;
        if (Integer.parseInt(item.getTitle().toString()) != timePeriod){
            timePeriod = Integer.parseInt(item.getTitle().toString());
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
            getMostPopularRequest(typeRequest, section, timePeriod);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String text = getResources().getString(R.string.time_period_text) + " " + timePeriod + " day";
        if (idCheckedNavMenu != id){
            idCheckedNavMenu = id;
            switch (id){

                case R.id.nav_mostviewed:
                    toolbarTitleTextView.setText(R.string.mostviewed);
                    typeRequest = NetworkConnection.MOST_VIEWED;
                    if (!layoutWorkplaceManager.selectedSupportView.equals(layoutWorkplaceManager.TOP_SELECTOR_RECYCLER_VIEW)){
                        layoutWorkplaceManager.createView(LayoutWorkplaceManager.SUPPORT, LayoutWorkplaceManager.TOP_SELECTOR_RECYCLER_VIEW);
                    }
                    onCreateOptionsMenu(toolbar.getMenu());
                    toolbarSupportTextView.setText(text);
                    getMostPopularRequest(typeRequest, section , 1);
                    break;
                case R.id.nav_mostmailed:
                    toolbarTitleTextView.setText(R.string.mostemailed);
                    typeRequest = NetworkConnection.MOST_MAILED;
                    if (!layoutWorkplaceManager.selectedSupportView.equals(layoutWorkplaceManager.TOP_SELECTOR_RECYCLER_VIEW)){
                        layoutWorkplaceManager.createView(LayoutWorkplaceManager.SUPPORT, LayoutWorkplaceManager.TOP_SELECTOR_RECYCLER_VIEW);
                    }
                    onCreateOptionsMenu(toolbar.getMenu());
                    toolbarSupportTextView.setText(text);
                    getMostPopularRequest(typeRequest, section , 1);
                    break;
                case R.id.nav_most_shared:
                    toolbarTitleTextView.setText(R.string.mostshared);
                    typeRequest = NetworkConnection.MOST_SHARED;
                    if (!layoutWorkplaceManager.selectedSupportView.equals(layoutWorkplaceManager.TOP_SELECTOR_RECYCLER_VIEW)){
                        layoutWorkplaceManager.createView(LayoutWorkplaceManager.SUPPORT, LayoutWorkplaceManager.TOP_SELECTOR_RECYCLER_VIEW);
                    }
                    onCreateOptionsMenu(toolbar.getMenu());
                    toolbarSupportTextView.setText(text);
                    getMostPopularRequest(typeRequest, section , 1);
                    break;
                case R.id.nav_search:
                    toolbarTitleTextView.setText(R.string.search);
                    typeRequest = NetworkConnection.SEARCH_REQUEST;
                    if (!layoutWorkplaceManager.selectedSupportView.equals(layoutWorkplaceManager.SEARCH_ARTICLE_VIEW)){
                        layoutWorkplaceManager.createView(LayoutWorkplaceManager.SUPPORT, LayoutWorkplaceManager.SEARCH_ARTICLE_VIEW);
                    }
                    toolbar.getMenu().clear();
                    toolbarSupportTextView.setText("");
                    break;

            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
