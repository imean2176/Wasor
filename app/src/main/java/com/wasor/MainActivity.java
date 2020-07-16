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

import com.androidnetworking.AndroidNetworking;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wasor.interfacee.IFirebaseLoadDone;
import com.wasor.modal.Rac;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

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

    List<String> nameRac;

    ArrayAdapter<String> adapter;


    // onCreate sau đó mới đến onStart
    // Tạo màn hình trước (onCreate), rồi sau đó hiển thị màn hình đó (onStart)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences("PREF", Context.MODE_PRIVATE);
        iFirebaseLoadDone = this;

        //Kiểm tra kết nối mạng wifi
        network();


        searchableSpinner = findViewById(R.id.searchSpinner);
        searchableSpinner.setTitle("Nhập tên rác thải");
        initSearchableSpinner();


        //Lấy dữ liệu phân loại rác từ firebase
        getDataFirebase();

        searchableSpinner.setSelection(0,false);
        //Cài đặt Khi chọn giá trị trong ô search thì sẽ xảy ra
        searchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Rac rac = dsRac.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("rac", rac);

                //Hiển thị màn hình chi tiết loại rác
                Intent intent = new Intent(getApplicationContext(), CachXuLyActivity.class);

                intent.putExtra("rac",bundle);

                //Truyền dữ liệu Rác qua màn hình chi tiết rác
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không chọn thì ko có chuyện gì xảy ra hết
            }
        });




    }

    private void network() {
        AndroidNetworking.initialize(getApplicationContext());
        // Adding an Network Interceptor for Debugging purpose :
        OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);
        // Then set the JacksonParserFactory like below
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
    }

    private void initSearchableSpinner() {
        //Lấy toàn bộ tên rác
        nameRac = new ArrayList<>();
        dsRac = new ArrayList<>();
        Rac rac = new Rac("Nhập tên rác thải","Truy xuất thất bại","Loại rác hiện tại ứng dụng chưa hỗ trợ","Vui lòng thử lại với loại rác khác");
        dsRac.add(rac);
        nameRac.add(rac.getTenrac());

        //Tạo adapter và cài đặt cho spinner
        adapter = new ArrayAdapter<>(this,R.layout.search_item, nameRac);
        searchableSpinner.setAdapter(adapter);
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


        // Lấy dữ liệu từ Cơ sở dữ liệu
        racRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dsRac.clear();
                for(DataSnapshot racSnapshot:dataSnapshot.getChildren()){
                    dsRac.add(racSnapshot.getValue(Rac.class));
                }
                iFirebaseLoadDone.onFirebaseLoadSuccess(dsRac);
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
        // Nếu danh sách rác đã tồn tại, xóa toàn bộ
        if (nameRac.size() > 0) {
            nameRac.clear();
        }

        for(Rac rac: racList){
            nameRac.add(rac.getTenrac());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFirebaseLoadFailed(String message) {

    }
}