package com.check.swipe_delete.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.check.swipe_delete.DModelClass;
import com.check.swipe_delete.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    Context context;
    List<DModelClass> mList;

    public Adapter(Context context, List<DModelClass> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.lay_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.txvItemName.setText(mList.get(position).getItemName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView txvItemName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvItemName=itemView.findViewById(R.id.txv_lay_item_name);
        }
    }

    //region remove on swipe
    public void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);

    }
    //endregion
}
