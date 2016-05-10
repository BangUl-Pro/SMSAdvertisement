package com.ironfactory.smsmasterapplication.controllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.controllers.activities.SetMemberActivity;
import com.ironfactory.smsmasterapplication.entities.GroupEntity;
import com.ironfactory.smsmasterapplication.entities.UserEntity;

/**
 * Created by IronFactory on 2016. 2. 7..
 */
public class SetMemberAdapter extends RecyclerView.Adapter {

    private static final String TAG = "SetMemberAdapter";
    private static final int CONTENT = 1;
    private static final int FOOTER = 2;

    private Context context;

    private GroupEntity groupEntity;

    private OnMember handler;

    public SetMemberAdapter(Context context, GroupEntity groupEntity) {
        this.context = context;
        this.groupEntity = groupEntity;

        handler = (OnMember) context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == groupEntity.getMembers().size())
            return FOOTER;
        return CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CONTENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set_group_mate, parent, false);
            return new SetMateContentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set_group_mate_footer, parent, false);
            return new SetMateFooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SetMateContentViewHolder) {
            ((SetMateContentViewHolder) holder).mateView.setText(groupEntity.getMembers().get(position).getId());
            ((SetMateContentViewHolder) holder).mateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SetMemberActivity.class);
                    intent.putExtra(Global.USER, groupEntity.getMembers().get(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            ((SetMateContentViewHolder) holder).mateView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                    builder.title("제명");
                    builder.content(groupEntity.getMembers().get(position).getId() + "님을 제명시키시겠습니까?");
                    builder.positiveText("예");
                    builder.negativeText("아니오");
                    builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            handler.onRemoveMember(groupEntity.getMembers().get(position).getId());
                            groupEntity.getMembers().remove(position);
                            notifyDataSetChanged();
                        }
                    });
                    builder.onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.dismiss();
                        }
                    });
                    builder.cancelable(true);
                    builder.show();

                    return false;
                }
            });
        } else if (holder instanceof SetMateFooterViewHolder) {
            ((SetMateFooterViewHolder) holder).addView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                    builder.input("유저 아이디", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            String userId = input.toString();
                            handler.onAddMember(userId);

                            addMember(userId);
                        }
                    });
                    builder.cancelable(true);
                    builder.show();
                }
            });
        }
    }

    public void addMember(String id) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        groupEntity.getMembers().add(userEntity);
        notifyDataSetChanged();
    }

    public void removeMember(String id) {
        for (int i = 0; i < groupEntity.getMembers().size(); i++) {
            String userId = groupEntity.getMembers().get(i).getId();
            if (userId.equals(id)) {
                groupEntity.getMembers().remove(i);
                return;
            }
        }
    }

    @Override
    public int getItemCount() {
        return groupEntity.getMembers().size() + 1;
    }

    public class SetMateContentViewHolder extends RecyclerView.ViewHolder {

        final TextView mateView;

        public SetMateContentViewHolder(View itemView) {
            super(itemView);

            mateView = (TextView) itemView.findViewById(R.id.item_set_group_mate);
        }
    }

    public class SetMateFooterViewHolder extends RecyclerView.ViewHolder {

        final TextView addView;

        public SetMateFooterViewHolder(View itemView) {
            super(itemView);

            addView = (TextView) itemView.findViewById(R.id.item_set_group_mate_add);
        }
    }

    public interface OnMember {
        void onAddMember(String id);
        void onRemoveMember(String id);
    }
}
