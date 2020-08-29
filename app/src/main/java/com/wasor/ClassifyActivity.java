package com.wasor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wasor.imageupload.APIConstants;
import com.wasor.imageupload.APIServices;
import com.wasor.imageupload.ImageUploadResponse;
import com.wasor.imageupload.PickImageDialog;
import com.wasor.imageupload.PickImageDialogInterface;
import com.wasor.imageupload.ServiceFactory;
import com.wasor.modal.Rac;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorFailedException;
import rx.schedulers.Schedulers;

import static com.wasor.PersistenceApplication.getContext;

public class ClassifyActivity extends AppCompatActivity implements PickImageDialogInterface {
    private Subscription mPicSubscription;
    private Uri mImageFileUri;
    private File mImageFile = null;
    private PickImageDialog mPickImageDialog;
    private ImageView mImageView;
    private ProgressBar mInsideProgressBar;
    private Button btnUploadImage;
    TextView txtPleaseWaiting;

    ArrayList<Rac> racs;
    /**
     * Permissions required to read and write contacts.
     */
    private static String[] permissionsCameraGallery = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    /**
     * Permissions required to read camera.
     */
    public static final int REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        initView();

        //Lấy dữ liệu danh sách rác từ hệ thống
        Intent intent = getIntent();
        ArrayList<Rac> dsRac = (ArrayList<Rac>) intent
                .getSerializableExtra("dsrac");
        ArrayList<String> nameRac = (ArrayList<String>) intent
                .getSerializableExtra("namerac");

        //Thêm toàn bộ danh sách rác vào để hiển thị trên ứng dụng
        racs = new ArrayList<Rac>();
        if (dsRac != null) {
            racs.addAll(dsRac);
            racs.remove(0);
        }
    }
    private void initView() {
        mInsideProgressBar = findViewById(R.id.insidePB);
        mImageView = findViewById(R.id.pickedImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO take image from gallery OR Camera and Upload to Server
                checkCameraAndStoragePermission();
            }
        });

        txtPleaseWaiting = findViewById(R.id.txtPleaseWaiting);
    }

    private void showImagePickDialog() {

        if (mPickImageDialog == null) {
            mPickImageDialog = new PickImageDialog(this);
            mPickImageDialog.delegate = this;
            mPickImageDialog.showDialog();
        } else {
            mPickImageDialog.delegate = this;
            mPickImageDialog.showDialog();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPickImageDialog == null) {
            mPickImageDialog = new PickImageDialog(this);
            mPickImageDialog.delegate = this;
            mPickImageDialog.resetFiles(mImageFileUri, mImageFile);
        }
        if (resultCode == Activity.RESULT_OK) {
            mPickImageDialog.onActivityResult(requestCode, data);

        } else {
            mPickImageDialog.onResultCancelled();
        }
    }

    @Override
    public void holdRecordingFile(Uri fileUri, File file) {
        this.mImageFileUri = fileUri;
        this.mImageFile = file;
    }

    @Override
    public void handleIntent(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void uploadPickedImage(File file) {
        mImageFile = file;
        showImagePicked(mImageFile);
        uploadPhotoToServer(mImageFile, "image");
    }

    private void showImagePicked(File mImageFile) {
        Uri contentUri = FileProvider.getUriForFile(getContext(), "com.wasor.fileProvider", mImageFile);
        mImageView.setImageURI(contentUri);
    }

    private void checkCameraAndStoragePermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkSDCardReadPermission(ClassifyActivity.this)) {
                displaySDCardReadPermissionAlert(ClassifyActivity.this);
            } else {
                showImagePickDialog();
            }
        } else {
            showImagePickDialog();
        }
    }


    //method checks for SD card read, write  permission granted OR not.
    public boolean checkSDCardReadPermission(Context thisActivity) {
        return !(ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(thisActivity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED);

    }

    public void displaySDCardReadPermissionAlert(Activity thisActivity) {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(thisActivity, permissionsCameraGallery, REQUEST_CAMERA);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkCameraAndStoragePermission();
                } else {
                    displayAlert(ClassifyActivity.this, REQUEST_CAMERA);
                }
                break;
        }
    }

    public void displayAlert(final Activity context, final int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        if (position == REQUEST_CAMERA) {
            builder1.setTitle(context.getResources().getString(R.string.camera));
            builder1.setMessage(context.getResources().getString(R.string.camera_desc));
        }
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (position == REQUEST_CAMERA) {
                            displaySDCardReadPermissionAlert(context);
                        }
                    }
                });

        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert1 = builder1.create();
        alert1.show();
    }


    private void uploadPhotoToServer(File mImageFile, String imageType) {

        //Cập nhật UI
        mInsideProgressBar.setVisibility(View.VISIBLE);
        btnUploadImage.setVisibility(View.GONE);
        txtPleaseWaiting.setVisibility(View.VISIBLE);

        //Gửi yêu cầu nhận diện ảnh lên server
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(imageType,
                mImageFile.getName(), RequestBody.create(MediaType.parse("image/*"), mImageFile));

        APIServices service = ServiceFactory.createRetrofitService(APIServices.class);
        mPicSubscription = service.uploadPhoto(APIConstants.IMAGE_UPLOAD_URL,
                filePart)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ImageUploadResponse>() {
                    @Override
                    public void onCompleted() {
//                        mPicSubscription = null;
//
//                        mInsideProgressBar.setVisibility(View.GONE);
//                        btnUploadImage.setVisibility(View.VISIBLE);
//                        txtPleaseWaiting.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {

                        mPicSubscription = null;
                        Toast.makeText(getApplicationContext(), "Hệ thống vẫn đang còn trong quá trình nghiên cứu, kết quả có thể thấp hơn dự tính.", Toast.LENGTH_SHORT).show();
                        mInsideProgressBar.setVisibility(View.GONE);
                        btnUploadImage.setVisibility(View.VISIBLE);
                        txtPleaseWaiting.setVisibility(View.GONE);
                        try {
                            Rac rac = new Rac("Phân loại thất bại","Dữ liệu phức tạp","Ứng dụng chưa nhận diện được loại rác bạn yêu cầu phân loại","Vui lòng thử lại với loại rác khác");
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("rac", rac);

                            //Hiển thị màn hình chi tiết loại rác
                            Intent intent = new Intent(getApplicationContext(), CachXuLyActivity.class);

                            intent.putExtra("rac",bundle);

                            //Truyền dữ liệu Rác qua màn hình chi tiết rác
                            startActivity(intent);

                            finish();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(ImageUploadResponse imageResultModel) {

//                        String imageUrl = imageResultModel.getResult().getImage();
//                        Picasso.with(ClassifyActivity.this).load(APIConstants.SERVER_URL + "containers/identificationImages/download/" + imageUrl)
//                                .fit().centerCrop().into(mImageView);

                        Log.d("UPLOADED_IMAGE", imageResultModel.getLabel());
                        Toast.makeText(getApplicationContext(), "Hoàn tất phân loại", Toast.LENGTH_SHORT).show();

                        Rac item = findItem(imageResultModel.getLabel());
                        Bundle bundle = new Bundle();


                        if(item == null){
                            Rac rac = new Rac("Phân loại thất bại","Dữ liệu phức tạp","Ứng dụng chưa nhận diện được loại rác bạn yêu cầu phân loại","Vui lòng thử lại với loại rác khác");
                            bundle.putSerializable("rac", rac);
                        }else{
                            bundle.putSerializable("rac", item);
                        }

                        //Hiển thị màn hình chi tiết loại rác
                        Intent intent = new Intent(getApplicationContext(), CachXuLyActivity.class);

                        intent.putExtra("rac",bundle);

                        //Truyền dữ liệu Rác qua màn hình chi tiết rác
                        startActivity(intent);

                        finish();
                    }
                });

    }

    private Rac GetRandomRac() {
        Random rd = new Random();
        int position = rd.nextInt(racs.size());
        Rac rac = racs.get(position);
        return rac;
    }

    private Rac findItem(String tenRac){
        int size = racs.size();
        for(int i = 0;i<size;i++){
            Rac rac = racs.get(i);
            if(rac.getTenrac().equals(tenRac)){
                return rac;
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPicSubscription != null) {
            mPicSubscription.unsubscribe();
        }
    }

    public void onBack(View view) {
        onBackPressed();
    }
}
