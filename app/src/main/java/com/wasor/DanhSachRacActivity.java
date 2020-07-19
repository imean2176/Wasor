package com.wasor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wasor.adapter.RacAdapter;
import com.wasor.modal.Rac;

import java.util.ArrayList;

//Màn hình danh sách rác
public class DanhSachRacActivity extends AppCompatActivity {

    //Biến để hiển thị danh sách rác
    RecyclerView recyclerView;
    RacAdapter adapter;
    ArrayList<Rac> racs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_rac);

        //Lấy dữ liệu danh sách rác từ hệ thống
        Intent intent = getIntent();
        ArrayList<Rac> dsRac = (ArrayList<Rac>) intent
                .getSerializableExtra("dsrac");
        ArrayList<String> nameRac = (ArrayList<String>) intent
                .getSerializableExtra("namerac");

        recyclerView = findViewById(R.id.list_view_rac);

        //Thêm toàn bộ danh sách rác vào để hiển thị trên ứng dụng
        racs = new ArrayList<Rac>();
        if (dsRac != null) {
            racs.addAll(dsRac);
            racs.remove(0);
        }

        adapter = new RacAdapter(racs, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }



    //Nhấn nút Back
    public void onBack(View view) {
        this.onBackPressed();
        finish();
    }
}
