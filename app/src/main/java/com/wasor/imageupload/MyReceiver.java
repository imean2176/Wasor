package com.wasor.imageupload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
public class MyReceiver extends BroadcastReceiver {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String status;

    @Override
    public void onReceive(Context context, Intent intent) {
        status = NetworkUtil.getConnectivityStatusString(context);
        if(status.isEmpty()) {
            status="Không có kết nối internet, bạn vui lòng bật wifi để tiếp tục";

//            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        }
    }
}
