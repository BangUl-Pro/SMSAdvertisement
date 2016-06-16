package com.adplan.smsapplication.controllers.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adplan.smsapplication.Global;
import com.adplan.smsapplication.controllers.activities.TabActivity;
import com.adplan.smsapplication.entities.GroupEntity;
import com.adplan.smsapplication.entities.UserEntity;
import com.adplan.smsapplication.R;

import java.util.List;

/**
 * Created by IronFactory on 2016. 2. 7..
 */
public class SelectGroupAdapter extends RecyclerView.Adapter<SelectGroupAdapter.SelectGroupViewHolder> {

    private static final String TAG = "SelectGroupAdapter";

    private Context context;

    private List<GroupEntity> groupEntities;
    private UserEntity userEntity;

    public SelectGroupAdapter(Context context, List<GroupEntity> groupEntities, UserEntity userEntity) {
        this.context = context;
        this.groupEntities = groupEntities;
        this.userEntity = userEntity;
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
                Intent intent = new Intent(context, TabActivity.class);
                intent.putExtra(Global.GROUP, groupEntities.get(position));
                intent.putExtra(Global.USER, userEntity);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
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
