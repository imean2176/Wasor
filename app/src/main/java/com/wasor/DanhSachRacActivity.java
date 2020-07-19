package com.wasor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.wasor.adapter.EndlessRecyclerViewScrollListener;
import com.wasor.adapter.RacAdapter;
import com.wasor.modal.Rac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DanhSachRacActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RacAdapter adapter;
    ArrayList<Rac> racs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_rac);


        Intent intent = getIntent();
        ArrayList<Rac> dsRac = (ArrayList<Rac>) intent
                .getSerializableExtra("dsrac");
        ArrayList<String> nameRac = (ArrayList<String>) intent
                .getSerializableExtra("namerac");

        recyclerView = findViewById(R.id.list_view_rac);

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



    public void onBack(View view) {
        this.onBackPressed();
        finish();
    }
}
