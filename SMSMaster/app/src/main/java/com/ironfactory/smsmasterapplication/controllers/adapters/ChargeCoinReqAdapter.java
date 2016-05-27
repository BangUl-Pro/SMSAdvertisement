package com.ironfactory.smsmasterapplication.controllers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.entities.ChargeCoinEntity;
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

import java.util.List;

/**
 * Created by IronFactory on 2016. 2. 7..
 */
public class ChargeCoinReqAdapter extends RecyclerView.Adapter<ChargeCoinReqAdapter.ChargeCoinReqViewHolder> {

    private static final String TAG = "ChargeCoinReqAdapter";

    private Context context;

    private List<ChargeCoinEntity> userList;

    public ChargeCoinReqAdapter(Context context, List<ChargeCoinEntity> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public ChargeCoinReqViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delete_user, parent, false);
        return new ChargeCoinReqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChargeCoinReqViewHolder holder, final int position) {
        holder.idView.setText(userList.get(position).getId());
        holder.chargeBtn.setText("충전");

        holder.chargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketManager.chargeCoin(userList.get(position).getId(), userList.get(position).getPrice(), new SocketListener.OnChargeCoin() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "코인 추가 성공");
                        userList.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "코인 추가 실패");
                        Toast.makeText(context, "코인 추가에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketManager.cancelChargeCoin(userList.get(position).getId(), new SocketListener.OnCancelChargeCoin() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "코인 추가 취소 성공");
                        userList.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "코인 추가 취소 실패");
                        Toast.makeText(context, "코인 추가 취소에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<ChargeCoinEntity> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public class ChargeCoinReqViewHolder extends RecyclerView.ViewHolder {

        final TextView idView;
        final Button chargeBtn;
        final Button cancelBtn;

        public ChargeCoinReqViewHolder(View itemView) {
            super(itemView);

            idView = (TextView) itemView.findViewById(R.id.item_delete_user);
            chargeBtn = (Button) itemView.findViewById(R.id.item_delete_user_delete);
            cancelBtn = (Button) itemView.findViewById(R.id.item_delete_user_cancel);
        }
    }
}
