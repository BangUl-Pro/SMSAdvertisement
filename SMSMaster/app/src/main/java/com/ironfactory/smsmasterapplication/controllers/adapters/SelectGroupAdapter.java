package com.ironfactory.smsmasterapplication.controllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.controllers.activities.MasterActivity;
import com.ironfactory.smsmasterapplication.controllers.activities.SuperMasterActivity;
import com.ironfactory.smsmasterapplication.entities.GroupEntity;

import java.util.List;

/**
 * Created by IronFactory on 2016. 2. 7..
 */
public class SelectGroupAdapter extends RecyclerView.Adapter<SelectGroupAdapter.SelectGroupViewHolder> {

    private static final String TAG = "SelectGroupAdapter";

    private Context context;

    private List<GroupEntity> groupEntities;

    public SelectGroupAdapter(Context context, List<GroupEntity> groupEntities) {
        this.context = context;
        this.groupEntities = groupEntities;
    }

    @Override
    public SelectGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_group, parent, false);
        return new SelectGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectGroupViewHolder holder, final int position) {
        holder.textView.setText(groupEntities.get(position).getName());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupEntities.get(position).getName().equals(Global.MASTER_GROUP_NAME)) {
                    Intent intent = new Intent(context, SuperMasterActivity.class);
                    intent.putExtra(Global.GROUP, groupEntities.get(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, MasterActivity.class);
                    intent.putExtra(Global.GROUP, groupEntities.get(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void setGroupEntities(List<GroupEntity> groupEntities) {
        this.groupEntities = groupEntities;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return groupEntities.size();
    }

    public class SelectGroupViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;

        public SelectGroupViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.item_select_group_text);
        }
    }
}
