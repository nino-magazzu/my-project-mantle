package com.project.mantle_v1;

import android.content.Context;
import android.widget.Toast;

public class ShowToast {

	public void showToast(String msg, Context mContext) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }
}
