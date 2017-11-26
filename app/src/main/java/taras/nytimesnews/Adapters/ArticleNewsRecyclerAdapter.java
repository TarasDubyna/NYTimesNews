package taras.nytimesnews.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import taras.nytimesnews.Models.Article;
import taras.nytimesnews.Models.Media;
import taras.nytimesnews.Models.MediaParam;
import taras.nytimesnews.R;

public class ArticleNewsRecyclerAdapter extends RecyclerView.Adapter<ArticleNewsRecyclerAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<Article> articles;

    public ArticleNewsRecyclerAdapter(Context context, ArrayList<Article> articles) {
        this.mContext = context;
        this.articles = articles;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CardView mCardView;
        public LinearLayout mImageCaptionLayout;
        public ImageView mImageView;
        public TextView mCaptionText;
        public TextView mTitleText;
        public TextView mAbstractText;
        public TextView mByLineText;
        public TextView mPublishedDateText;
        public Button mBtnMore;

        public ViewHolder(View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.article_card_view);
            mImageCaptionLayout = itemView.findViewById(R.id.article_image_caption_layout);
            mTitleText = itemView.findViewById(R.id.article_title_text);
            mAbstractText = itemView.findViewById(R.id.article_abstract_text);
            mImageView = itemView.findViewById(R.id.article_image_view);
            mCaptionText = itemView.findViewById(R.id.article_caption_text);
            mByLineText = itemView.findViewById(R.id.article_byline_text);
            mPublishedDateText = itemView.findViewById(R.id.article_published_date_text);
            mBtnMore = itemView.findViewById(R.id.article_more_btn);
        }
    }

    @Override
    public ArticleNewsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20,100, 20, 20);
            holder.mCardView.setLayoutParams(layoutParams);
        }
        holder.mTitleText.setText(articles.get(position).getTitle());
        holder.mAbstractText.setText(articles.get(position).getAbstracts());

        //image with caption
        createImageCaptionView(holder, articles.get(position));

        holder.mByLineText.setText(articles.get(position).getByline());
        holder.mPublishedDateText.setText(articles.get(position).getPublished_date());

        holder.mBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    private void createImageCaptionView(ViewHolder holder, Article article){
        if (article.getMedia().size() > 0 && article.getBitmapImage() != null){
            if (article.getMedia().get(0).getCaption().isEmpty()){
                holder.mCaptionText.setVisibility(View.GONE);
            } else {
                holder.mCaptionText.setText(article.getMedia().get(0).getCaption());
                holder.mImageView.setImageBitmap(article.getBitmapImage());
                holder.mImageCaptionLayout.setVisibility(View.VISIBLE);
            }
        } else {
            holder.mImageCaptionLayout.setVisibility(View.GONE);
        }
    }






}
