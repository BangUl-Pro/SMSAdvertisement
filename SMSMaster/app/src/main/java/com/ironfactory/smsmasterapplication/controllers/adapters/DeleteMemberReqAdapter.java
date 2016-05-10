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
import com.ironfactory.smsmasterapplication.entities.DeleteMemberEntity;
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

import java.util.List;

/**
 * Created by IronFactory on 2016. 2. 7..
 */
public class DeleteMemberReqAdapter extends RecyclerView.Adapter<DeleteMemberReqAdapter.DeleteMemberReqViewHolder> {

    private static final String TAG = "DeleteMemberReqAdapter";

    private Context context;

    private List<DeleteMemberEntity> userList;

    public DeleteMemberReqAdapter(Context context, List<DeleteMemberEntity> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public DeleteMemberReqViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delete_member, parent, false);
        return new DeleteMemberReqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeleteMemberReqViewHolder holder, final int position) {
        holder.userIdView.setText(userList.get(position).getUserId());
        if (userList.get(position).getGroupName() != null)
            holder.groupNameView.setText(userList.get(position).getGroupName());
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketManager.deleteMember(userList.get(position).getGroupId(), userList.get(position).getUserId(), new SocketListener.OnDeleteMember() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "멤버 추방 성공");
                        userList.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onException() {
                        Log.d(TAG, "멤버 추방 실패");
                        Toast.makeText(context, "계정 삭제에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketManager.cancelDeleteMember(userList.get(position).getGroupId(), userList.get(position).getUserId(), new SocketListener.OnCancelDeleteMember() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "계정 삭제 취소 성공");
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

    public void setUserList(List<DeleteMemberEntity> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public void setGroupName(String name, int position) {
        this.userList.get(position).setGroupName(name);
        notifyDataSetChanged();
    }

    public class DeleteMemberReqViewHolder extends RecyclerView.ViewHolder {

        final TextView userIdView;
        final TextView groupNameView;
        final Button deleteBtn;
        final Button cancelBtn;

        public DeleteMemberReqViewHolder(View itemView) {
            super(itemView);

            userIdView = (TextView) itemView.findViewById(R.id.item_delete_member_user_id);
            groupNameView = (TextView) itemView.findViewById(R.id.item_delete_member_group_name);
            deleteBtn = (Button) itemView.findViewById(R.id.item_delete_member_delete);
            cancelBtn = (Button) itemView.findViewById(R.id.item_delete_member_cancel);
        }
    }
}
