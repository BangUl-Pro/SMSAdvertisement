package com.ironfactory.smsmasterapplication.controllers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.entities.GroupEntity;
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

/**
 * Created by IronFactory on 2016. 2. 7..
 */
public class TopMasterAdapter extends RecyclerView.Adapter {

    private static final String TAG = "TopMasterAdapter";
    private static final int CONTENT = 1;
    private static final int FOOTER = 2;

    private Context context;

    private GroupEntity groupEntity;

    public TopMasterAdapter(Context context, GroupEntity groupEntity) {
        this.context = context;
        this.groupEntity = groupEntity;
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
                    if (!groupEntity.getMasters().get(position).equals(Global.SUPER_MASTER_ID)) {
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                        builder.cancelable(true);
                        builder.title("관리자설정");
                        builder.positiveText("삭제");
                        builder.negativeText("취소");
                        builder.content(groupEntity.getMasters().get(position) + "를 최고관리자에서 제명하시겠습니까?");
                        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                SocketManager.deleteMaster(groupEntity.getId(), groupEntity.getMasters().get(position), new SocketListener.OnDeleteMaster() {
                                    @Override
                                    public void onSuccess() {
                                        groupEntity.getMasters().remove(position);
                                        notifyDataSetChanged();
                                        Log.d(TAG, "관리자 삭제 성공");
                                    }

                                    @Override
                                    public void onException(int code) {
                                        Log.d(TAG, "관리자 삭제 실패");
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    } else {
                        Toast.makeText(context, Global.SUPER_MASTER_ID + " 는 삭제할 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (holder instanceof SetMasterFooterViewHolder) {
            ((SetMasterFooterViewHolder) holder).addView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                    builder.input("관리자 아이디", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            final String masterId = input.toString();
                            SocketManager.insertMaster(groupEntity.getId(), masterId, new SocketListener.OnInsertMaster() {
                                @Override
                                public void onSuccess() {
                                    groupEntity.getMasters().add(masterId);
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onException(int code) {
                                    if (code == 432) {
                                        // 존재하지 않는아이디
                                        Toast.makeText(context, "해당 아이디가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    builder.cancelable(true);
                    builder.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return groupEntity.getMasters().size() + 1;
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
}
