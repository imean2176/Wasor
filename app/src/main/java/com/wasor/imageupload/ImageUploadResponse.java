package com.wasor.imageupload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageUploadResponse {

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @SerializedName("label")
    @Expose
    public String label;


//    @SerializedName("success")
//    @Expose
//    private boolean success;
//
//    @SerializedName("result")
//    @Expose
//    private ImageUploadResult result = new ImageUploadResult();
//
//    public boolean isSuccess() {
//        return success;
//    }
//
//    public void setSuccess(boolean success) {
//        this.success = success;
//    }
//
//    public ImageUploadResult getResult() {
//        return result;
//    }
//
//    public void setResult(ImageUploadResult result) {
//        this.result = result;
//    }
}

