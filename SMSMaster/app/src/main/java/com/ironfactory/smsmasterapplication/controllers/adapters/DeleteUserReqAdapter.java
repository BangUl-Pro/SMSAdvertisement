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
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

import java.util.List;

/**
 * Created by IronFactory on 2016. 2. 7..
 */
public class DeleteUserReqAdapter extends RecyclerView.Adapter<DeleteUserReqAdapter.DeleteUserReqViewHolder> {

    private static final String TAG = "DeleteUserReqAdapter";

    private Context context;

    private List<String> userList;

    public DeleteUserReqAdapter(Context context, List<String> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public DeleteUserReqViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delete_user, parent, false);
        return new DeleteUserReqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeleteUserReqViewHolder holder, final int position) {
        holder.idView.setText(userList.get(position));
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketManager.deleteUser(userList.get(position), new SocketListener.OnDeleteUser() {
                    @Override
                    public void onSuccess() {
                        userList.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "계정 삭제 실패");
                        Toast.makeText(context, "계정 삭제에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketManager.cancelDeleteUser(userList.get(position), new SocketListener.OnCancelDeleteUser() {
                    @Override
                    public void onSuccess() {
                        userList.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "계정 삭제 취소 실패");
                        Toast.makeText(context, "계정 삭제 취소에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public class DeleteUserReqViewHolder extends RecyclerView.ViewHolder {

        final TextView idView;
        final Button deleteBtn;
        final Button cancelBtn;

        public DeleteUserReqViewHolder(View itemView) {
            super(itemView);

            idView = (TextView) itemView.findViewById(R.id.item_delete_user);
            deleteBtn = (Button) itemView.findViewById(R.id.item_delete_user_delete);
            cancelBtn = (Button) itemView.findViewById(R.id.item_delete_user_cancel);
        }
    }
}
