package com.wasor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wasor.interfacee.IFirebaseLoadDone;
import com.wasor.modal.Rac;

import java.util.ArrayList;
import java.util.List;

//Màn hình đầu tiên mà ứng dụng hiện lên
//Ứng dụng sẽ kiểm tra xem người dùng đã xem Màn hình giới thiệu chưa,
//Nếu có thì chuyển qua Màn hình giới thiệu (IntroActivity).
//Nếu không thì tiếp tục hiển thị màn hình Chính này (MainActivity).
public class MainActivity extends AppCompatActivity implements IFirebaseLoadDone {

    public final static String FIREBASE = "FIREBASE";

    //Tìm kiếm và sổ giá trị xuống
    SearchableSpinner searchableSpinner;

    //Biến lưu dữ liệu từ Cloud truyền xuống
    DatabaseReference racRef;

    //Biến để lưu dữ liệu người dùng.
    SharedPreferences sharedPreferences;

    IFirebaseLoadDone iFirebaseLoadDone;

    List<Rac> dsRac;


    // onCreate sau đó mới đến onStart
    // Tạo màn hình trước (onCreate), rồi sau đó hiển thị màn hình đó (onStart)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences("PREF", Context.MODE_PRIVATE);
        iFirebaseLoadDone = this;



        //Lấy dữ liệu phân loại rác từ firebase
        getDataFirebase();

        searchableSpinner = findViewById(R.id.searchSpinner);
        searchableSpinner.setTitle(String.valueOf(R.string.placeholder_search));


        //Cài đặt Khi chọn giá trị trong ô search thì sẽ xảy ra
        searchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Rac rac = dsRac.get(position);

                //Hiển thị màn hình chi tiết loại rác

                //Todo
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không chọn thì ko có chuyện gì xảy ra hết
            }
        });
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

    //Lẫy dữ liệu từ Cloud
    public void getDataFirebase(){

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Lấy danh sách rác từ key dsrac trên firebase
        racRef = database.getReference("dsrac");


        // Read from the database
        racRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Rac> racs = new ArrayList<>();
                for(DataSnapshot racSnapshot:dataSnapshot.getChildren()){
                    racs.add(racSnapshot.getValue(Rac.class));
                }
                iFirebaseLoadDone.onFirebaseLoadSuccess(racs);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(FIREBASE, "Failed to read value.", error.toException());
                iFirebaseLoadDone.onFirebaseLoadFailed(error.getMessage());
            }
        });
    }

    @Override
    public void onFirebaseLoadSuccess(List<Rac> racList) {

        //Nếu lấy được danh sách ở firebase thành công thì nhúng data vào ô search khi tìm
        dsRac = racList;

        //Lấy toàn bộ tên rác
        List<String> name_rac = new ArrayList<>();
        for(Rac rac: dsRac){
            name_rac.add(rac.getTenrac());
        }

        //Tạo adapter và cài đặt cho spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,name_rac);
        searchableSpinner.setAdapter(adapter);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {

    }
}