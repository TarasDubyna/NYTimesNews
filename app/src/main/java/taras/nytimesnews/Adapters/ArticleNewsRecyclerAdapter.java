package taras.nytimesnews.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class ArticleNewsRecyclerAdapter extends RecyclerView.Adapter<ArticleNewsRecyclerAdapter.ViewHolder>{

    private Context mContext;

    public ArticleNewsRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


}
