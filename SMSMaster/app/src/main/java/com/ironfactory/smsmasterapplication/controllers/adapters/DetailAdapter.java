package com.ironfactory.smsmasterapplication.controllers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
public class DetailAdapter extends RecyclerView.Adapter {

    private static final String TAG = "DetailAdapter";
    private static final int HEADER = 1;
    private static final int CONTENT = 2;

    private Context context;

    private List<UserEntity> userEntities;
    private List<Integer> msgYesterdayList;
    private List<Integer> msgTodayList;

    public DetailAdapter(Context context, List<UserEntity> userEntities, List<Integer> msgYesterdayList, List<Integer> msgTodayList) {
        this.context = context;
        this.userEntities = userEntities;
        this.msgYesterdayList = msgYesterdayList;
        this.msgTodayList = msgTodayList;
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
            return new DetailHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_content, parent, false);
            return new DetailContentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DetailContentViewHolder) {
            final int POSITION = position - 1;
            DetailContentViewHolder viewHolder = (DetailContentViewHolder) holder;
            viewHolder.nameView.setText(userEntities.get(POSITION).getName());
            if (TextUtils.isEmpty(userEntities.get(POSITION).getName()))
                viewHolder.nameView.setText(userEntities.get(POSITION).getId());
            viewHolder.yesterdayView.setText(String.valueOf(msgYesterdayList.get(POSITION)));
            viewHolder.todayView.setText(String.valueOf(msgTodayList.get(POSITION)));
        } else {
            DetailHeaderViewHolder viewHolder = (DetailHeaderViewHolder) holder;
            viewHolder.nameView.setText("가맹점");
            viewHolder.coinView.setText("어제");
            viewHolder.isConnectView.setText("오늘");
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

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            params.weight = 1;
            viewHolder.coinView.setLayoutParams(params);
            viewHolder.isConnectView.setLayoutParams(params);
            viewHolder.nameView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return userEntities.size() + 1;
    }


    public void setMsgYesterdayList(List<Integer> yesterdayList) {
        this.msgYesterdayList = yesterdayList;
        notifyDataSetChanged();
    }

    public void setMsgTodayList(List<Integer> todayList) {
        this.msgTodayList = todayList;
        notifyDataSetChanged();
    }

    // Header ViewHolder
    class DetailHeaderViewHolder extends RecyclerView.ViewHolder {

        final TextView nameView;
        final TextView coinView;
        final TextView isConnectView;

        public DetailHeaderViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.item_member_header_name);
            coinView = (TextView) itemView.findViewById(R.id.item_member_header_coin);
            isConnectView = (TextView) itemView.findViewById(R.id.item_member_header_is_connected);
        }
    }


    // Content ViewHolder
    class DetailContentViewHolder extends RecyclerView.ViewHolder {

        final TextView nameView;
        final TextView yesterdayView;
        final TextView todayView;

        public DetailContentViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.item_detail_content_name);
            yesterdayView = (TextView) itemView.findViewById(R.id.item_detail_content_yesterday);
            todayView = (TextView) itemView.findViewById(R.id.item_detail_content_today);
        }
    }


    public static class NameCompare implements Comparator<UserEntity> {
        @Override
        public int compare(UserEntity lhs, UserEntity rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }
}
