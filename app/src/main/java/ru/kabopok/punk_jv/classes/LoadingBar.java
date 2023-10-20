package ru.kabopok.punk_jv.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import ru.kabopok.punk_jv.R;

public class LoadingBar {
    Activity activity;
    AlertDialog dialog;


    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_bar,null));
        dialog  = builder.create();
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
