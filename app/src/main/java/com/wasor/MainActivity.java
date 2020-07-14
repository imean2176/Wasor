package com.wasor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

//Màn hình đầu tiên mà ứng dụng hiện lên
//Ứng dụng sẽ kiểm tra xem người dùng đã xem Màn hình giới thiệu chưa,
//Nếu có thì chuyển qua Màn hình giới thiệu (IntroActivity).
//Nếu không thì tiếp tục hiển thị màn hình Chính này (MainActivity).
public class MainActivity extends AppCompatActivity {

    //Biến để lưu dữ liệu người dùng.
    SharedPreferences sharedPreferences;

    //Tìm kiếm và sổ giá trị xuống
    SearchableSpinner searchableSpinner;

    // onCreate sau đó mới đến onStart
    // Tạo màn hình trước (onCreate), rồi sau đó hiển thị màn hình đó (onStart)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences("PREF", Context.MODE_PRIVATE);


//        searchableSpinner = findViewById(R.id.btnSearchSpinner);
//        searchableSpinner.setTitle(String.valueOf(R.string.placeholder_search));
//        searchableSpinner.setPositiveButton("OK");
    }

    @Override
    public void onStart() {
        super.onStart();

        //Biến này kiểm tra xem đây có phải lần đầu khách hàng truy cập vào màn hình chính hay không
        boolean isFirstLogin = sharedPreferences.getBoolean("isFirstLogin", false);

        //Nếu lần đầu, thì cho khách hàng xem màn hình giới thiệu đầu tiên
        if(!isFirstLogin)
        {
            //Cập nhật lại biến "Lần đầu truy cập" thành lần 2 truy cập, để lần sau khách hàng vào lại sẽ không hiển thị màn hình intro nữa.
            sharedPreferences.edit().putBoolean("isFirstLogin", true).apply();

            //Hiển thị màn hình giới thiệu
            startActivity(new Intent(this, IntroActivity.class));
        }

        // Nếu không phải lần đầu thì vẫn hiển thị màn hình chính.
    }
}