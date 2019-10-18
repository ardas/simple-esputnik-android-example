package ua.esputnik.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import ua.esputnik.support.db.PushEntity;

public class WordListAdapter extends
        RecyclerView.Adapter<WordListAdapter.WordViewHolder>  {


    private LayoutInflater mInflater;
    List<PushEntity> data;

    public WordListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public WordListAdapter.WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.wordlist_item,
                parent, false);
        return new WordViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(WordListAdapter.WordViewHolder holder, int position) {
        PushEntity pushEntity = data.get(position);
        holder.titleView.setText(pushEntity.title);
        holder.contentView.setText(pushEntity.content);
        holder.iidView.setText(pushEntity.iid);
    }

    @Override
    public int getItemCount() {
        if (null == data) {
            return 0;
        }
        return data.size();
    }

    public void setData(List<PushEntity> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    class WordViewHolder extends RecyclerView.ViewHolder {

        public final TextView titleView;
        public final TextView contentView;
        public final TextView iidView;
        final WordListAdapter mAdapter;

        public WordViewHolder(View itemView, WordListAdapter adapter) {
            super(itemView);
            titleView = itemView.findViewById(R.id.push_title);
            contentView = itemView.findViewById(R.id.push_content);
            iidView = itemView.findViewById(R.id.push_iid);

            this.mAdapter = adapter;
        }


    }

}
