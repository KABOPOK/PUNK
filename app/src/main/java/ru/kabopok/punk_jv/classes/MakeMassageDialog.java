package ru.kabopok.punk_jv.classes;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.activities.RegistrationActivity;
import ru.kabopok.punk_jv.current.Online;

public class MakeMassageDialog {
    Activity activity;
    Dialog dialog;
    CircleImageView send;
    EditText text;
    public MakeMassageDialog(Activity activity, Context context) {
        dialog = new Dialog(context);
        this.activity = activity;
    }
    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.make_massage_dialog);
        text = dialog.findViewById(R.id.text_of_message);
        send = dialog.findViewById(R.id.send);
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
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> messageHashMap = new HashMap<>();
                String name = Online.getCurrentUser().getName();
                if(Online.getCurrentUser().getNumber().length() > 4){
                    name+=Online.getCurrentUser().getNumber().charAt(4);
                }
                messageHashMap.put(name,text.getText().toString());
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Messages");
                rootRef.updateChildren(messageHashMap);
                if(!text.getText().toString().equals("Денис Усик")){
                    Toast.makeText(activity, "спасибо за отзыв " + Online.getCurrentUser().getName() + " =)", Toast.LENGTH_LONG).show();
                }
                dismiss();
            }
        });
        dialog.show();

    }

    public void dismiss(){
        dialog.dismiss();
    }
    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
