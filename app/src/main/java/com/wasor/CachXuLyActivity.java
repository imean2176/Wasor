package com.wasor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.widget.ANImageView;
import com.wasor.modal.Rac;

//Màn hình chi tiết và cách xử lý của một rác thải bất kỳ
public class CachXuLyActivity extends AppCompatActivity {
    ANImageView imageView;
    ImageButton btnBack;
    TextView txtTenRac;
    TextView txtLoaiRac;
    TextView txtMoTaLoaiRac;
    TextView txtCachXuLy;
    TextView txtMoTaCachXuLy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cach_xu_ly);

        findID();

        //Lấy dữ liệu cần hiển thị cách xử lý của rác nào
        Bundle bundle = getIntent().getBundleExtra("rac");

        if(bundle!=null){
            Rac rac = (Rac) bundle.getSerializable("rac");
            Log.d("CACHXULY",rac.toString());
            HienThiGiaTri(rac);
        }

    }

    private void HienThiGiaTri(Rac rac) {
        txtTenRac.setText(rac.getTenrac());
        txtLoaiRac.setText(rac.getLoairac());
        txtMoTaLoaiRac.setText(rac.getMotaloairac());
        txtMoTaCachXuLy.setText(rac.getCachxuly());

        if(!rac.getUrlImage().contains("NONE") || !rac.getUrlImage().isEmpty()){
            imageView.setImageUrl(rac.getUrlImage());
        }
    }

    //Lấy từng loại giao diện trên màn hình
    private void findID() {
        imageView = findViewById(R.id.image_rac);
        imageView.bringToFront();

        btnBack = findViewById(R.id.btn_backCXL);
        btnBack.bringToFront();

        txtTenRac= findViewById(R.id.txtTenRac);
        txtLoaiRac= findViewById(R.id.txtLoaiRac);
        txtMoTaLoaiRac= findViewById(R.id.txtMoTaLoaiRac);
        txtCachXuLy= findViewById(R.id.txtCachXuLy);
        txtMoTaCachXuLy= findViewById(R.id.txtMoTaCachXuLy);
    }

    //Nhấn quay về màn hình trước đó
    public void onBack(View view) {
        this.onBackPressed();
        finish();
    }
}
