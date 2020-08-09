package com.wasor.imageupload;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import static com.wasor.PersistenceApplication.getContext;


public class PickImageDialog {
    private Activity mActivity;
    public PickImageDialogInterface delegate;

    public PickImageDialog(Activity activity) {
        this.mActivity = activity;
    }

    public void showDialog() {

        String str[] = new String[]{"Chụp ảnh", "Ảnh từ thư viện"};
        new AlertDialog.Builder(mActivity)
                .setItems(str,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    createImageFile(false);
                                    Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (picIntent.resolveActivity(mActivity.getPackageManager())
                                            != null) {

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            picIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            Uri contentUri = FileProvider.getUriForFile(getContext(), "com.wasor.fileProvider", file);
                                            picIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                                            delegate.handleIntent(picIntent,
                                                    AppUtils.REQUEST_CODE_SELECT_IMAGE);
                                        } else {
                                            delegate.holdRecordingFile(photoUri, file);
                                            picIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                            delegate.handleIntent(picIntent,
                                                    AppUtils.REQUEST_CODE_SELECT_IMAGE);
                                        }
                                    }
                                } else {
                                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                            MediaStore.Images.Media
                                                    .EXTERNAL_CONTENT_URI);

                                    delegate.handleIntent(galleryIntent,
                                            AppUtils.REQUEST_CODE_SELECT_IMAGE_GALLERY);
                                }
                            }
                        }).show();
    }

    public void showFrontCamera() {
        createImageFile(false);
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (picIntent.resolveActivity(mActivity.getPackageManager())
                != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                picIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(getContext(), "com.wasor.fileProvider", file);
                picIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                delegate.handleIntent(picIntent,
                        AppUtils.REQUEST_CODE_SELECT_IMAGE);
            } else {
                delegate.holdRecordingFile(photoUri, file);
                picIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                delegate.handleIntent(picIntent,
                        AppUtils.REQUEST_CODE_SELECT_IMAGE);
            }
        }

    }

    private File file;
    private Uri photoUri = null;

    private Uri createImageFile(boolean isGaleeryPic) {
        try {
            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath());

            File dataFolder;
            if (isGaleeryPic) {
                File androidFolder = new File(dir, "DCIM");
                if (!androidFolder.exists()) {
                    androidFolder.mkdir();
                }
                dataFolder = new File(androidFolder, "Camera");
                if (!dataFolder.exists()) {
                    dataFolder.mkdir();
                }
            } else {
                File androidFolder = new File(dir, "Android");
                if (!androidFolder.exists()) {
                    androidFolder.mkdir();
                }
                File dFolder = new File(androidFolder, "data");
                if (!dFolder.exists()) {
                    dFolder.mkdir();
                }
                dataFolder = new File(dFolder, "com.wasor");
                if (!dataFolder.exists()) {
                    dataFolder.mkdir();
                }
            }

            Calendar cal = Calendar.getInstance();
            long millis = (cal.get(Calendar.HOUR_OF_DAY) * 24 * 60 * 60)
                    + (cal.get(Calendar.MINUTE) * 60 * 60) + (cal.get(Calendar.SECOND) * 60);
            String date = cal.get(Calendar.YEAR) + "" + cal.get(Calendar.MONTH) + ""
                    + cal.get(Calendar.DATE) + "_" + millis;

            // Create a name for the saved image
            file = new File(dataFolder, date + ".jpg");
            if (!file.exists()) {
                file.createNewFile();
            }
            photoUri = Uri.fromFile(file);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return photoUri;
    }

    public void resetFiles(Uri savUri, File recFile) {
        photoUri = savUri;
        file = recFile;
    }

    public void onActivityResult(int requestCode, Intent data) {

        switch (requestCode) {
            case AppUtils.REQUEST_CODE_SELECT_IMAGE: //Camera
                delegate.holdRecordingFile(photoUri, file);
                cropImage(photoUri, photoUri);
                break;

            case AppUtils.REQUEST_CODE_SELECT_IMAGE_GALLERY: //Gallery
                if (data != null) {
                    createImageFile(true);
                    delegate.holdRecordingFile(photoUri, file);
                    cropImage(data.getData(), photoUri);
                }
                break;

            case UCrop.REQUEST_CROP:
                updateImageVisibilityInGallery();
                delegate.uploadPickedImage(file);

                break;

            case UCrop.RESULT_ERROR:
                onResultCancelled();
                break;

            default:
                break;

        }
    }

    public boolean onResultCancelled() {
        return file != null && file.delete();
    }

    private void cropImage(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(16, 16)//16 9
                .start(mActivity);
    }

    private void updateImageVisibilityInGallery() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            if (photoUri != null) {
                intent.setData(photoUri);
            }
            mActivity.sendBroadcast(intent);

        } else {
            mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));

        }
    }
}
