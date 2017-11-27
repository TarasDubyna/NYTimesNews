package taras.nytimesnews.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import taras.nytimesnews.MainActivity;
import taras.nytimesnews.R;

public class TopSelectorRecyclerAdapter extends RecyclerView.Adapter<TopSelectorRecyclerAdapter.ViewHolder>{

    private String[] mSelectorTextArray;
    private Context mContext;

    private  int selectedPosition = -1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView mTextView;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mCardView = v.findViewById(R.id.top_selector_card_view);
            mTextView = v.findViewById(R.id.top_selector_recycler_item_text);
            mImageView = v.findViewById(R.id.top_selector_recycler_item_image);
        }
    }

    public TopSelectorRecyclerAdapter(Context context) {
        mContext = context;
        mSelectorTextArray = mContext.getResources().getStringArray(R.array.top_section_array);
    }

    @Override
    public TopSelectorRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.top_selector_recycle_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (selectedPosition == -1){
            selectedPosition = 1;
            ((MainActivity) holder.mCardView.getContext()).onTopSelectorClickCalled(mSelectorTextArray[selectedPosition]);
        }

        if (selectedPosition == position){
            holder.mImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_icon_selected));
            holder.mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.black));
            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            holder.mImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.star_icon));
            holder.mCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.black));
        }

        if (position == 0){
            holder.mImageView.setVisibility(View.VISIBLE);
        } else {
            holder.mImageView.setVisibility(View.GONE);
        }

        holder.mTextView.setText(mSelectorTextArray[position]);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = position;
                if (position == 0){
                    Toast.makeText(mContext, "Favorite articles", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                } else {
                    ((MainActivity) view.getContext()).onTopSelectorClickCalled(mSelectorTextArray[selectedPosition]);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSelectorTextArray.length;
    }
}

