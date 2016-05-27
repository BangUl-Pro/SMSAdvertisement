package com.ironfactory.smsmasterapplication.controllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ironfactory.smsmasterapplication.Global;
import com.ironfactory.smsmasterapplication.R;
import com.ironfactory.smsmasterapplication.controllers.activities.MsgActivity;
import com.ironfactory.smsmasterapplication.entities.GroupEntity;
import com.ironfactory.smsmasterapplication.entities.UserEntity;
import com.ironfactory.smsmasterapplication.networks.SocketListener;
import com.ironfactory.smsmasterapplication.networks.SocketManager;

import java.util.List;

/**
 * Created by IronFactory on 2016. 2. 7..
 */
public class MasterAdapter extends RecyclerView.Adapter {

    private static final String TAG = "MasterAdapter";
    public static final int MASTER = 1;
    public static final int DETAIL = 2;

    private static final int HEADER = 1;
    private static final int CONTENT = 2;

    private Context context;
    private GroupEntity groupEntity;

    private List<Integer> yesterdayList;
    private List<Integer> todayList;

    private int state;

    private int coin = 0;

    public MasterAdapter(Context context, GroupEntity groupEntity) {
        this.context = context;
        this.groupEntity = groupEntity;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER;
        return CONTENT;
    }

    public List<Integer> getYesterdayList() {
        return yesterdayList;
    }

    public void setYesterdayList(List<Integer> yesterdayList) {
        this.yesterdayList = yesterdayList;
    }

    public List<Integer> getTodayList() {
        return todayList;
    }

    public void setTodayList(List<Integer> todayList) {
        this.todayList = todayList;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return groupEntity.getMembers().size() + 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_header, parent, false);
            return new MasterHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_master, parent, false);
            return new MasterContentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MasterContentViewHolder) {
            final int POSITION = position - 1;
            List<UserEntity> userEntities = groupEntity.getMembers();
            final UserEntity userEntity = userEntities.get(POSITION);

            final MasterContentViewHolder viewHolder = (MasterContentViewHolder) holder;
            viewHolder.container.setVisibility((viewHolder.isVisible ? View.VISIBLE : View.GONE));
            viewHolder.parentNameView.setText(userEntity.getName());
            if (userEntity.getName() == null || TextUtils.isEmpty(userEntity.getName())) {
                viewHolder.parentNameView.setText(userEntity.getId());
            }
            if (state == MASTER) {
                viewHolder.parentCoinView.setText(String.valueOf(userEntity.getCoin()));
                viewHolder.parentIsConnectedView.setText((userEntity.isConnected() ? "ON" : "OFF"));
            } else {
                viewHolder.parentCoinView.setText(String.valueOf(yesterdayList.get(POSITION)));
                viewHolder.parentIsConnectedView.setText(String.valueOf(todayList.get(POSITION)));
            }

            viewHolder.nameView.setText(userEntity.getName());
            viewHolder.phoneView.setText(userEntity.getPhone());
            viewHolder.accountView.setText(userEntity.getId());
//            viewHolder.passwordView.setText(userEntity.getPassword());
            viewHolder.coinView.setText(String.valueOf(userEntity.getCoin()));
            viewHolder.groupView.setText(groupEntity.getName());
            viewHolder.allowChangeMsgBtn.setChecked(userEntity.isAbleChangeMsg());
            viewHolder.allowShowAdDetailBtn.setChecked(userEntity.isAbleShowAdDetail());
            viewHolder.allowSendToReceiveNumBtn.setChecked(userEntity.isAbleSendPhone());
            viewHolder.allowInfiniteCoinBtn.setChecked(userEntity.isInfiniteCoin());

            viewHolder.infiniteCoinContainer.setVisibility((Global.USER_TYPE != Global.TYPE_SUPER_MASTER ? View.GONE : View.VISIBLE));

            viewHolder.allowChangeMsgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int isAble = (viewHolder.allowChangeMsgBtn.isChecked() ? UserEntity.ABLE : UserEntity.DISABLE);
                    SocketManager.setChangeMsg(userEntity.getId(), isAble, new SocketListener.OnSetChangeMsg() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "setChangeMsg 성공");
                        }

                        @Override
                        public void onException() {
                            Log.d(TAG, "setChangeMsg 실패");
                        }
                    });
                }
            });

            viewHolder.allowShowAdDetailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int isAble = (viewHolder.allowShowAdDetailBtn.isChecked() ? UserEntity.ABLE : UserEntity.DISABLE);
                    SocketManager.setShowAdDetail(userEntity.getId(), isAble, new SocketListener.OnSetShowAdDetail() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "setShowAdDetail 성공");
                        }

                        @Override
                        public void onException() {
                            Log.d(TAG, "setShowAdDetail 실패");
                        }
                    });
                }
            });

            viewHolder.allowSendToReceiveNumBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int isAble = (viewHolder.allowSendToReceiveNumBtn.isChecked() ? UserEntity.ABLE : UserEntity.DISABLE);
                    SocketManager.setSend(userEntity.getId(), isAble, new SocketListener.OnSetSend() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "setSend 성공");
                        }

                        @Override
                        public void onException() {
                            Log.d(TAG, "setSend 실패");
                        }
                    });
                }
            });

            viewHolder.allowInfiniteCoinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int isAble = (viewHolder.allowInfiniteCoinBtn.isChecked() ? UserEntity.ABLE : UserEntity.DISABLE);
                    SocketManager.setInfiniteCoin(userEntity.getId(), isAble, new SocketListener.OnSetInfiniteCoin() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "setInfinite 성공");
                        }

                        @Override
                        public void onException() {
                            Log.d(TAG, "setInfinite 실패");
                        }
                    });
                }
            });


            viewHolder.changeMsgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "메세지 변경 버튼 클릭");
//                    if (viewHolder.allowChangeMsgBtn.isChecked()) {
                        Intent intent = new Intent(context, MsgActivity.class);
                        intent.putExtra(Global.USER, userEntity);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
//                    } else {
//                        Toast.makeText(context, "문구 수정 허용해주십시오", Toast.LENGTH_SHORT).show();
//                    }
                }
            });


            viewHolder.chargeCoinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                    builder.title("코인 충전")
                            .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
                            .input("충전 금액", "", false, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    Integer.parseInt(input.toString());
                                }
                            }).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    SocketManager.chargeCoinReq(userEntity.getId(), coin, new SocketListener.OnChargeCoinReq() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "코인 수동 충전 요청 성공");
                                            Toast.makeText(context, "요청 성공", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onException() {
                                            Log.d(TAG, "코인 수동 충전 요청 실패");
                                            Toast.makeText(context, "요청 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).positiveText("확인")
                            .show();
                }
            });

            viewHolder.parentContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.container.setVisibility((!viewHolder.isVisible ? View.VISIBLE : View.GONE));
                    viewHolder.isVisible = !viewHolder.isVisible;
                }
            });
        } else {
            MasterHeaderViewHolder viewHolder = (MasterHeaderViewHolder) holder;
            if (state == MASTER) {
                viewHolder.nameView.setText("가맹점");
                viewHolder.coinView.setText("코인");
                viewHolder.isConnectView.setText("접속");
            } else {
                viewHolder.nameView.setText("가맹점");
                viewHolder.coinView.setText("어제");
                viewHolder.isConnectView.setText("오늘");
            }
        }
    }

    public class MasterHeaderViewHolder extends RecyclerView.ViewHolder {

        final TextView nameView;
        final TextView coinView;
        final TextView isConnectView;

        public MasterHeaderViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.item_member_header_name);
            coinView = (TextView) itemView.findViewById(R.id.item_member_header_coin);
            isConnectView = (TextView) itemView.findViewById(R.id.item_member_header_is_connected);
        }
    }

    public class MasterContentViewHolder extends RecyclerView.ViewHolder {

        final TextView parentNameView;
        final TextView parentCoinView;
        final TextView parentIsConnectedView;

        final LinearLayout container;
        final TextView nameView;
        final TextView phoneView;
        final TextView accountView;
        final TextView passwordView;
        final TextView coinView;
        final TextView groupView;
        final ToggleButton allowChangeMsgBtn;
        final ToggleButton allowShowAdDetailBtn;
        final ToggleButton allowSendToReceiveNumBtn;
        final ToggleButton allowInfiniteCoinBtn;
        final Button changeMsgBtn;
        final Button chargeCoinBtn;

        final LinearLayout infiniteCoinContainer;
        final LinearLayout parentContainer;

        boolean isVisible = false;

        public MasterContentViewHolder(View itemView) {
            super(itemView);

            parentNameView = (TextView) itemView.findViewById(R.id.item_master_parent_name);
            parentCoinView = (TextView) itemView.findViewById(R.id.item_master_parent_coin);
            parentIsConnectedView = (TextView) itemView.findViewById(R.id.item_master_parent_is_connected);

            container = (LinearLayout) itemView.findViewById(R.id.item_master_container);
            nameView = (TextView) itemView.findViewById(R.id.item_master_name);
            phoneView = (TextView) itemView.findViewById(R.id.item_master_phone);
            accountView = (TextView) itemView.findViewById(R.id.item_master_account);
            passwordView = (TextView) itemView.findViewById(R.id.item_master_password);
            coinView = (TextView) itemView.findViewById(R.id.item_master_coin);
            groupView = (TextView) itemView.findViewById(R.id.item_master_group);
            allowChangeMsgBtn = (ToggleButton) itemView.findViewById(R.id.item_master_allow_change_msg);
            allowShowAdDetailBtn = (ToggleButton) itemView.findViewById(R.id.item_master_allow_show_ad_detail);
            allowSendToReceiveNumBtn = (ToggleButton) itemView.findViewById(R.id.item_master_allow_send_to_receive_num);
            allowInfiniteCoinBtn = (ToggleButton) itemView.findViewById(R.id.item_master_allow_infinite_coin);
            changeMsgBtn = (Button) itemView.findViewById(R.id.item_master_change_msg);
            chargeCoinBtn = (Button) itemView.findViewById(R.id.item_master_charge_coin);

            infiniteCoinContainer = (LinearLayout) itemView.findViewById(R.id.item_master_infinite_coin_container);
            parentContainer = (LinearLayout) itemView.findViewById(R.id.item_master_parent_container);
        }
    }

    public void setGroupEntity(GroupEntity groupEntity) {
        this.groupEntity = groupEntity;
        notifyDataSetChanged();
    }
}
