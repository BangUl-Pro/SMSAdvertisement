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
public class SetMasterAdapter extends RecyclerView.Adapter {

    private static final String TAG = "SetMasterAdapter";
    private static final int CONTENT = 1;
    private static final int FOOTER = 2;

    private Context context;

    private GroupEntity groupEntity;

    private OnMaster handler;

    public SetMasterAdapter(Context context, GroupEntity groupEntity) {
        this.context = context;
        this.groupEntity = groupEntity;

        handler = (OnMaster) context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == groupEntity.getMasters().size())
            return FOOTER;
        return CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CONTENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set_group_mate, parent, false);
            return new SetMasterContentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set_group_mate_footer, parent, false);
            return new SetMasterFooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SetMasterContentViewHolder) {
            ((SetMasterContentViewHolder) holder).mateView.setText(groupEntity.getMasters().get(position));
            ((SetMasterContentViewHolder) holder).mateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Global.USER_TYPE == Global.TYPE_SUPER_MASTER) {
                        // 최고관리자라면 그룹장 정보 변경 가능
                        Intent intent = new Intent(context, SetMemberActivity.class);
                        UserEntity userEntity = new UserEntity();
                        userEntity.setId(groupEntity.getMasters().get(position));
                        intent.putExtra(Global.USER, userEntity);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
            ((SetMasterContentViewHolder) holder).mateView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (Global.USER_TYPE == Global.TYPE_SUPER_MASTER) {
                        // 최고관리자라면 그룹장 제명 가능
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                        builder.title("제명");
                        builder.content(groupEntity.getMasters().get(position) + "님을 제명시키시겠습니까?");
                        builder.positiveText("예");
                        builder.negativeText("아니오");
                        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                handler.onRemoveMaster(groupEntity.getMasters().get(position));
                                groupEntity.getMasters().remove(position);
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
                    }
                    return false;
                }
            });
        } else if (holder instanceof SetMasterFooterViewHolder) {
            ((SetMasterFooterViewHolder) holder).addView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Global.USER_TYPE == Global.TYPE_SUPER_MASTER) {
                        // 최고관리자라면 관리자 추가 가능
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                        builder.input("관리자 아이디", "", false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                final String masterId = input.toString();
                                handler.onAddMaster(masterId);
                                groupEntity.getMasters().add(masterId);
                                notifyDataSetChanged();
                            }
                        });
                        builder.cancelable(true);
                        builder.show();
                    }
                }
            });
        }
    }

    public void removeMaster(String id) {
        groupEntity.getMasters().remove(id);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        // 최고관리자라면 Footer추가
        return (Global.USER_TYPE == Global.TYPE_SUPER_MASTER ? groupEntity.getMasters().size() + 1 : groupEntity.getMasters().size());
    }

    public class SetMasterContentViewHolder extends RecyclerView.ViewHolder {

        final TextView mateView;

        public SetMasterContentViewHolder(View itemView) {
            super(itemView);

            mateView = (TextView) itemView.findViewById(R.id.item_set_group_mate);
        }
    }

    public class SetMasterFooterViewHolder extends RecyclerView.ViewHolder {

        final TextView addView;

        public SetMasterFooterViewHolder(View itemView) {
            super(itemView);

            addView = (TextView) itemView.findViewById(R.id.item_set_group_mate_add);
        }
    }

    public interface OnMaster {
        void onAddMaster(String id);
        void onRemoveMaster(String id);
    }
}
