package taras.nytimesnews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import taras.nytimesnews.Adapters.ArticleNewsRecyclerAdapter;
import taras.nytimesnews.Adapters.TopSelectorRecyclerAdapter;
import taras.nytimesnews.Models.Article;

public class LayoutWorkplaceManager {

    public static final String SUPPORT = "support";
    public static final String CONTENT = "content";

    public static final String PROGRESS_VIEW = "update_view";
    public static final String ERROR_VIEW = "error_view";
    public static final String TOP_SELECTOR_RECYCLER_VIEW = "top_selector_recycler";
    public static final String ARTICLE_RECYCLER_VIEW = "article_selector_recycler";
    public static final String SEARCH_ARTICLE_VIEW = "search_article";
    public static final String ARCHIVE_ARTICLE_VIEW = "archive_article";


    private Context mContext;
    private LinearLayout contentLayout;
    private LinearLayout supportLayout;

    private int resource = 0;
    private ArrayList<Article> articles;
    private int timePeriod = 1;
    private String searchValue = null;



    private LayoutWorkplaceManager(WorkplaceBuilder workplaceBuilder) {
        mContext = workplaceBuilder.mContext;
        contentLayout = workplaceBuilder.contentLayout;
        supportLayout = workplaceBuilder.supportLayout;
    }

    public static class WorkplaceBuilder{

        private Context mContext;
        private LinearLayout contentLayout;
        private LinearLayout supportLayout;

        public WorkplaceBuilder(Context mContext) {
            this.mContext = mContext;
        }

        public WorkplaceBuilder contentLayout(LinearLayout contentLayout){
            this.contentLayout = contentLayout;
            return this;
        }

        public WorkplaceBuilder supportLayout(LinearLayout supportLayout){
            this.supportLayout = supportLayout;
            return this;
        }

        public LayoutWorkplaceManager build(){
            return new LayoutWorkplaceManager(this);
        }
    }

    public LayoutWorkplaceManager addArticleArrayList(ArrayList<Article> articles){
        this.articles = articles;
        return this;
    }

    public LayoutWorkplaceManager addStringResource(int resource){
        this.resource = resource;
        return this;
    }

    public void createView(String layoutName, String viewType){
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        supportLayout.setOrientation(LinearLayout.VERTICAL);
        View view = null;
        switch (viewType){
            case TOP_SELECTOR_RECYCLER_VIEW:
                view = new RecyclerView(mContext);
                TopSelectorRecyclerAdapter topSelectorRecyclerAdapter = new TopSelectorRecyclerAdapter(mContext);
                ((RecyclerView)view).setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                ((RecyclerView)view).setAdapter(topSelectorRecyclerAdapter);
                addViewInLayout(layoutName, view);
                break;
            case ARTICLE_RECYCLER_VIEW:
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
                view = new RecyclerView(mContext);
                ArticleNewsRecyclerAdapter adapter = new ArticleNewsRecyclerAdapter(mContext, articles);
                ((RecyclerView) view).setLayoutManager(layoutManager);
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                ((RecyclerView) view).setAdapter(adapter);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.ALIGN_TOP, R.id.main_support_layout);
                addViewInLayout(layoutName, view);
                break;
            case SEARCH_ARTICLE_VIEW:
                view = new CardView(mContext);
                LinearLayout.LayoutParams searchLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPixels(mContext, 40));
                searchLayoutParams.setMargins(dipToPixels(mContext, 5),dipToPixels(mContext, 5),dipToPixels(mContext, 5),dipToPixels(mContext, 5));
                view.setLayoutParams(searchLayoutParams);

                LinearLayout viewCardLayout = new LinearLayout(mContext);
                viewCardLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                SearchView searchView = new SearchView(mContext, null, R.style.SearchViewStyle1);
                LinearLayout.LayoutParams inputSearchParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dipToPixels(mContext, 40));
                searchView.setLayoutParams(inputSearchParam);
                searchView.setIconified(false);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        Toast.makeText(mContext, "TextSubmit", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });

                viewCardLayout.addView(searchView);
                ((CardView) view).addView(viewCardLayout);
                addViewInLayout(layoutName, view);
                break;
            case ARCHIVE_ARTICLE_VIEW:
                view = new CardView(mContext);
                LinearLayout.LayoutParams archiveViewParams = new LinearLayout.LayoutParams(dipToPixels(mContext, 160),dipToPixels(mContext, 40));
                archiveViewParams.gravity = Gravity.CENTER_HORIZONTAL;
                view.setLayoutParams(archiveViewParams);

                LinearLayout archiveContent = new LinearLayout(mContext);
                archiveContent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                TextView dateText = new TextView(mContext);
                dateText.setText("25 Oct 2017");
                dateText.setGravity(Gravity.CENTER);

                archiveContent.addView(dateText);
                ((CardView) view).addView(archiveContent);
                break;
            case PROGRESS_VIEW:
                ProgressBar progressBar = new ProgressBar(mContext);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dipToPixels(mContext, 50), dipToPixels(mContext, 50));
                layoutParams.gravity = Gravity.CENTER;
                progressBar.setIndeterminate(true);
                progressBar.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setLayoutParams(layoutParams);
                view = new LinearLayout(mContext);
                ((LinearLayout) view).setGravity(Gravity.CENTER);
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                ((LinearLayout) view).addView(progressBar);
                addViewInLayout(layoutName, view);
                break;
            case ERROR_VIEW:
                view = new TextView(mContext);
                ((TextView) view).setGravity(Gravity.CENTER);
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                ((TextView) view).setText(mContext.getResources().getString(resource));
                addViewInLayout(layoutName, view);
                break;
        }

    }

    private void addViewInLayout(String layoutName, View view){
        switch (layoutName){
            case CONTENT:
                contentLayout.removeAllViews();
                contentLayout.addView(view);
                break;
            case SUPPORT:
                supportLayout.removeAllViews();
                supportLayout.addView(view);
                break;
        }
    }

    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

}
