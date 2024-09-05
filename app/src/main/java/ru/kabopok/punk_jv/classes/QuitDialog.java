package ru.kabopok.punk_jv.classes;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.activities.MainActivity;
import ru.kabopok.punk_jv.activities.RegistrationActivity;
import ru.kabopok.punk_jv.current.Online;

public class QuitDialog {
    Activity activity;
    Dialog dialog;
    Button yes;
    Button no;
    int counter=0;

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.quit_dialog);
        yes = dialog.findViewById(R.id.quit_yes);
        no = dialog.findViewById(R.id.quit_no);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true; // Consume back button press
                }
                return false;
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (counter){
                    case 0:
                        yes.animate().x(450).start();
                        Toast.makeText(activity,"Уверен??",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        yes.animate().x(150).start();
                        yes.animate().y(-50).start();
                        Toast.makeText(activity,"Ну прям точно уверен?",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Online.TurnOff = true;
                        finishAffinity(activity);
                        dismiss();
                }
                ++counter;
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.show();

    }

    public void dismiss(){
        dialog.dismiss();
    }
    public QuitDialog(Activity activity, Context context) {
        dialog = new Dialog(context);
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }
}
