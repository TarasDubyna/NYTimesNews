package taras.nytimesnews;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TopSelectorRecyclerAdapter extends RecyclerView.Adapter<TopSelectorRecyclerAdapter.TopSelectorViewHolder> {


    @Override
    public TopSelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(TopSelectorViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private class TopSelectorViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextView;

        public TopSelectorViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.top_selector_card_view_text);
        }
    }


}
