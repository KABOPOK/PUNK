package ru.kabopok.punk_jv.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import ru.kabopok.punk_jv.R;

public class LoadingBar {
    Activity activity;
    AlertDialog dialog;

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_bar,null));
        dialog  = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true; // Consume back button press
                }
                return false;
            }
        });
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }
    public LoadingBar(Activity activity) {
        this.activity = activity;
        this.dialog = dialog;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }
}
