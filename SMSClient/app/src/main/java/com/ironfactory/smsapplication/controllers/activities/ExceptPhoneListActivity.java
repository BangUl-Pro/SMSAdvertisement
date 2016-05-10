package com.ironfactory.smsapplication.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ironfactory.smsapplication.DBManager;
import com.ironfactory.smsapplication.Global;
import com.ironfactory.smsapplication.R;
import com.ironfactory.smsapplication.controllers.adapter.ExceptPhoneAdapter;

import java.util.List;

public class ExceptPhoneListActivity extends AppCompatActivity {

    private static final String TAG = "ExceptPhoneActivity";
    private RecyclerView recyclerView;
    private DBManager dbManager;

    private List<String> phoneList;
    private ExceptPhoneAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_except_phone_list);
        setSupportActionBar((Toolbar) findViewById(R.id.activity_except_phone_list_toolbar));
        setTitle("제외목록");

        init();
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.activity_except_phone_list_recycler);

        getDB();
        setRecyclerView();
    }

    private void getDB() {
        dbManager = new DBManager(getApplicationContext(), Global.APP_NAME, null, 1);
        phoneList = dbManager.getPhone();
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(
                getApplicationContext(), LinearLayoutManager.VERTICAL, false
        ));

        adapter = new ExceptPhoneAdapter(this, phoneList, dbManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_except_phone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_except_phone_add) {
            // 추가
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title("추가");
            builder.input("번호를 제대로 입력하세요", "", false, new MaterialDialog.InputCallback() {
                @Override
                public void onInput(MaterialDialog dialog, CharSequence input) {
                    String phone = input.toString();
                    if (checkNum(phone)) {
                        // 번호라면
                        dbManager.insertPhone(phone);
                        adapter.addPhone(phone);
                    } else {
                        Toast.makeText(getApplicationContext(), "번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.positiveText("확인");
            builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(MaterialDialog dialog, DialogAction which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean checkNum(String phone) {
        if (phone.length() < 11)
            return false;

        try {
            long num = Long.parseLong(phone);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
