package com.adplan.smsapplication.controllers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adplan.smsapplication.DBManager;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.adplan.smsapplication.R;

import java.util.List;

/**
 * Created by IronFactory on 2016. 2. 7..
 */
public class ExceptPhoneAdapter extends RecyclerView.Adapter<ExceptPhoneAdapter.SelectGroupViewHolder> {

    private static final String TAG = "SelectGroupAdapter";

    private Context context;

    private List<String> phoneList;
    private DBManager dbManager;

    public ExceptPhoneAdapter(Context context, List<String> phoneList, DBManager dbManager) {
        this.context = context;
        this.phoneList = phoneList;
        this.dbManager = dbManager;
    }

    @Override
    public SelectGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_group, parent, false);
        return new SelectGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectGroupViewHolder holder, final int position) {
        holder.textView.setText(phoneList.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                builder.title("제거");
                builder.content(phoneList.get(position) + "를 제거하시겠습니까?");
                builder.positiveText("예");
                builder.negativeText("아니오");
                builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dbManager.removePhone(phoneList.get(position));
                        phoneList.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return phoneList.size();
    }

    public void addPhone(String phone) {
        phoneList.add(phone);
        notifyDataSetChanged();
    }

    public class SelectGroupViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;

        public SelectGroupViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.item_select_group_text);
        }
    }
}
