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
import com.ironfactory.smsmasterapplication.controllers.activities.InputActivity;
import com.ironfactory.smsmasterapplication.entities.UserEntity;

/**
 * Created by IronFactory on 2016. 2. 7..
 */
public class MsgAdapter extends RecyclerView.Adapter {

    private static final String TAG = "SelectGroupAdapter";
    private static final int CONTENT = 1;
    private static final int FOOTER = 2;

    private Context context;

    private UserEntity userEntity;

    public MsgAdapter(Context context, UserEntity userEntity) {
        this.context = context;
        this.userEntity = userEntity;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == userEntity.getMsgEntities().size())
            return FOOTER;
        return CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CONTENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_content, parent, false);
            return new MsgContentViewHolder(view);
        } else if (viewType == FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_footer, parent, false);
            return new MsgFooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MsgContentViewHolder) {
            MsgContentViewHolder viewHolder = (MsgContentViewHolder) holder;
            viewHolder.portView.setText(userEntity.getMsgEntities().get(position).getPort());
            viewHolder.contentView.setText(userEntity.getMsgEntities().get(position).getContent());
            viewHolder.changeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, InputActivity.class);
                    intent.putExtra(Global.MSG, userEntity.getMsgEntities().get(position));
                    intent.putExtra(Global.ID, userEntity.getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        } else if (holder instanceof MsgFooterViewHolder) {
            MsgFooterViewHolder viewHolder = (MsgFooterViewHolder) holder;
            viewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, InputActivity.class);
                    intent.putExtra(Global.ID, userEntity.getId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userEntity.getMsgEntities().size() + 1;
    }

    // Footer ViewHolder
    public class MsgFooterViewHolder extends RecyclerView.ViewHolder {

        final TextView addBtn;

        public MsgFooterViewHolder(View itemView) {
            super(itemView);

            addBtn = (TextView) itemView.findViewById(R.id.item_msg_footer_add);
        }
    }

    // Content ViewHolder
    public class MsgContentViewHolder extends RecyclerView.ViewHolder {

        final TextView portView;
        final TextView contentView;
        final TextView changeBtn;

        public MsgContentViewHolder(View itemView) {
            super(itemView);

            portView = (TextView) itemView.findViewById(R.id.item_msg_content_port);
            contentView = (TextView) itemView.findViewById(R.id.item_msg_content);
            changeBtn = (TextView) itemView.findViewById(R.id.item_msg_content_change);
        }
    }
}
