package com.ironfactory.smsmasterapplication.controllers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.entities.UserEntity;
import com.ironfactory.smsmasterapplication.utils.Sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IronFactory on 2016. 2. 7..
 */
public class MemberAdapter extends RecyclerView.Adapter {

    private static final String TAG = "MemberAdapter";
    private static final int HEADER = 1;
    private static final int CONTENT = 2;

    private Context context;

    private List<UserEntity> userEntities;

    public MemberAdapter(Context context, List<UserEntity> userEntities) {
        this.context = context;
        this.userEntities = userEntities;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER;
        return CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_header, parent, false);
            return new MemberHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_content, parent, false);
            return new MemberContentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MemberContentViewHolder) {
            final int POSITION = position - 1;
            MemberContentViewHolder viewHolder = (MemberContentViewHolder) holder;
            viewHolder.nameView.setText(userEntities.get(POSITION).getName());
            viewHolder.coinView.setText(String.valueOf(userEntities.get(POSITION).getCoin()));
            viewHolder.isConnectedView.setText((userEntities.get(POSITION).isConnected() ? "ON" : "OFF"));
        } else {
            MemberHeaderViewHolder viewHolder = (MemberHeaderViewHolder) holder;
            viewHolder.nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 가맹점 클릭
                    Collections.sort(userEntities, new NameCompare());
                }
            });
            viewHolder.coinView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 코인 클릭
                    Sort.quickSort(userEntities, Sort.TYPE_ASC);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userEntities.size() + 1;
    }


    // Header ViewHolder
    class MemberHeaderViewHolder extends RecyclerView.ViewHolder {

        final TextView nameView;
        final TextView coinView;

        public MemberHeaderViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.item_member_header_name);
            coinView = (TextView) itemView.findViewById(R.id.item_member_header_coin);
        }
    }


    // Content ViewHolder
    class MemberContentViewHolder extends RecyclerView.ViewHolder {

        final TextView nameView;
        final TextView coinView;
        final TextView isConnectedView;

        public MemberContentViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.item_member_content_name);
            coinView = (TextView) itemView.findViewById(R.id.item_member_content_coin);
            isConnectedView = (TextView) itemView.findViewById(R.id.item_member_content_is_connected);
        }
    }


    public static class NameCompare implements Comparator<UserEntity> {
        @Override
        public int compare(UserEntity lhs, UserEntity rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }
}
