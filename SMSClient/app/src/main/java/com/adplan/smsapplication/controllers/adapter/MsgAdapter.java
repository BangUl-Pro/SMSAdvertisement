package com.adplan.smsapplication.controllers.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adplan.smsapplication.Global;
import com.adplan.smsapplication.entities.MsgEntity;
import com.adplan.smsapplication.entities.UserEntity;
import com.adplan.smsapplication.networks.SocketListener;
import com.adplan.smsapplication.networks.SocketManager;
import com.adplan.smsapplication.R;
import com.adplan.smsapplication.controllers.activities.InputActivity;

import java.util.ArrayList;

/**
 * Created by IronFactory on 15. 11. 8..
 */
public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MainViewHolder> {

    private static final String TAG = "MsgAdapter";
    private ArrayList<MsgEntity> msgEntities;
    private UserEntity userEntity;
    private Context context;

    public MsgAdapter(Context context, ArrayList<MsgEntity> msgEntities, UserEntity userEntity) {
        this.msgEntities = msgEntities;
        this.context = context;
        this.userEntity = userEntity;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, final int position) {
        holder.numView.setText(msgEntities.get(position).getPort());
        holder.contentView.setText(msgEntities.get(position).getContent());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEntity.isAbleChangeMsg()) {
                    Intent intent = new Intent(context, InputActivity.class);
                    intent.putExtra(Global.USER, userEntity.getId());
                    intent.putExtra(Global.MSG, msgEntities.get(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "문구 수정 허용을 체크해주십시오", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketManager.deleteMsg(msgEntities.get(position), new SocketListener.OnDeleteMsg() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "메세지 삭제 성공");
                        Toast.makeText(context, "메세지 삭제 성공", Toast.LENGTH_SHORT).show();
                        msgEntities.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "메세지 삭제 실패");
                        Toast.makeText(context, "메세지 삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void addMsg(MsgEntity msgEntity) {
        msgEntities.add(msgEntity);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return msgEntities.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        TextView numView;
        TextView deleteView;
        TextView contentView;
        LinearLayout container;


        public MainViewHolder(View itemView) {
            super(itemView);

            deleteView = (TextView) itemView.findViewById(R.id.item_main_delete);
            numView = (TextView) itemView.findViewById(R.id.item_main_num);
            contentView = (TextView) itemView.findViewById(R.id.item_main_content);
            container = (LinearLayout) itemView.findViewById(R.id.item_main_container);
        }
    }
}
