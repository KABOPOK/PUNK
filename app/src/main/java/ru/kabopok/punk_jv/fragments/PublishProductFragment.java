package ru.kabopok.punk_jv.fragments;

import static ru.kabopok.punk_jv.fragments.ProfileFragment.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import me.relex.circleindicator.CircleIndicator;
import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.activities.HomeActivity;
import ru.kabopok.punk_jv.activities.RegistrationActivity;
import ru.kabopok.punk_jv.classes.ImageResizer;
import ru.kabopok.punk_jv.classes.LineLimitFilter;
import ru.kabopok.punk_jv.classes.QuitDialog;
import ru.kabopok.punk_jv.classes.ViewPagerAdapter;
import ru.kabopok.punk_jv.classes.LoadingBar;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.current.Online;
import ru.kabopok.punk_jv.databinding.FragmentPublishProductBinding;

public class PublishProductFragment extends Fragment {
    FragmentPublishProductBinding binding;
    StorageReference storageReference;
    private EditText productTitle;
    private EditText productPrice;
    TextView counterLines;
    QuitDialog quitDialog;
    private EditText productInfo;
    private Button PushProduct;
    private StorageReference StoreRef;
    private Uri uriOfImg;
    private String imgUrl;
    CircleIndicator indicator;
    private int counter;
    private User currentUser = Online.getCurrentUser();
    private ViewPager viewPager;
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    private String key;
    Button pick;
    private final int REQUEST_PERMISSION_CODE = 35;
    private final int PICK_IMAGE_CODE = 39;
    int joke=0;
    LoadingBar loadingBar;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish_product, container, false);
        storageReference = FirebaseStorage.getInstance().getReference();
        StoreRef = FirebaseStorage.getInstance().getReference("Image");
        productTitle = (EditText) view.findViewById(R.id.product_title);
        productPrice = (EditText) view.findViewById(R.id.product_price);
        productInfo = (EditText) view.findViewById(R.id.product_info);
        PushProduct = (Button) view.findViewById(R.id.push_product_button);
        viewPager = view.findViewById(R.id.images_ViewPager);
        pick = view.findViewById(R.id.pick);
        indicator = view.findViewById(R.id.indicator_0);
        counterLines = view.findViewById(R.id.infoLinesCounter);
        binding = FragmentPublishProductBinding.inflate(getLayoutInflater());
        quitDialog = new QuitDialog(getActivity(),getContext());
        loadingBar = new LoadingBar(this.getActivity());
        productInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Online.lineCount = productInfo.getLineCount();
                if(Online.lineCount > 7) {
                    counterLines.setTextColor(Color.RED);
                }
                else{
                    counterLines.setTextColor(Color.WHITE);
                }
                counterLines.setText(Online.lineCount + "/7");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        productInfo.setFilters(new InputFilter[]{new LineLimitFilter(8)});
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(joke<8) {
                    Intent pickImg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    upLoadImage.launch(pickImg);
                }else {
                    pick.setText("я дурачёк");
                    Toast.makeText(getContext(), "нет, ты просто студент", Toast.LENGTH_SHORT).show();
                }
            }

        });
        PushProduct.setOnClickListener(v -> {
            Online.lineCount = 0;
            loadingBar.show();
            if(CorrectData()) {
                sendToBase();
                uploadImgWithCompress();
            }else{
                loadingBar.dismiss();
            }
        });
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Toast.makeText(getContext(), "permisson denied", Toast.LENGTH_SHORT);
                    return false;
                }
                return false;
            }
        });
        setOnBackPressed();
        return view;
    }

    private void setOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                quitDialog.show();
            }
        });
    }

    ActivityResultLauncher<Intent> upLoadImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result->{
                if(result.getResultCode() == RESULT_OK && result.getData()!=null){
                    ++joke;
                    switch (joke){
                        case 1:
                            pick.setText("ещё одну подгрузить");
                            break;
                        case 2:
                            pick.setText("и ещё");
                            break;
                        case 3:
                            pick.setText("ещё!!!");
                            break;
                        case 4:
                            pick.setText("ну вот ещё одну");
                            break;
                        case 5:
                            pick.setText("ну вот надо ещё");
                            break;
                        case 6:
                            pick.setText("ну вот последнюю");
                            break;
                        default:
                            if (joke > 7) {
                                pick.setText("я дурачёк");
                            }
                    }
                    uriArrayList.add(result.getData().getData());
                    setAdapter();
                    indicator.setViewPager(viewPager);
                }
            }
    );
//    private void PickImages() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_CODE);
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if(REQUEST_PERMISSION_CODE == requestCode){
//            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                PickImages();
//            }
//            else{
//                Toast.makeText(this.getContext(), "permisson denied", Toast.LENGTH_SHORT);
//            }
//        }
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK){
//            ++joke;
//            switch (joke){
//                case 1:
//                    pick.setText("ещё одну подгрузить");
//                    break;
//                case 2:
//                    pick.setText("и ещё");
//                    break;
//                case 3:
//                    pick.setText("ещё!!!");
//                    break;
//                case 4:
//                    pick.setText("ну вот ещё одну");
//                    break;
//                case 5:
//                    pick.setText("ну вот надо ещё");
//                    break;
//                case 6:
//                    pick.setText("ну вот последнюю");
//                    break;
//                default:
//                    if(joke<8) {
//                        pick.setText("точно последнюю");
//                    }
//            }
//            uriArrayList.add(data.getClipData().getItemAt(0).getUri());
//            setAdapter();
//        }
//    }
    private void uploadImgWithCompress(){
        for(int i =0;  i < uriArrayList.size(); ++i) {
            uriOfImg = uriArrayList.get(i);
            byte[] bytes = new byte[0];
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), uriOfImg);
                Bitmap newWay = ImageResizer.reduceBitmapSize(bitmap, 1000000);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                newWay.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                bytes = byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String path = "images/" + UUID.randomUUID().toString();
            StorageReference ref = storageReference.child(path);
            ref.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(this.getContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgUrl = uri.toString();
                            addPhoto(imgUrl, path);
                            ++counter;
                            if(counter >= uriArrayList.size()){
                                counter=0;
                                loadingBar.dismiss();
                                Intent inputIntent = new Intent(PublishProductFragment.this.getContext(), HomeActivity.class);
                                startActivity(inputIntent);
                            }
                        }
                    });
                }
            });
        }
    }
    private void addPhoto(String imgUrl, String path) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> photoHashMap= new HashMap<>();
                photoHashMap.put("cloudPath",path);
                photoHashMap.put("URL",imgUrl);
                rootRef.child("Products").child(key).child(path).updateChildren(photoHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void sendToBase() {
        String productName = productTitle.getText().toString();
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UUID uniqueKey = UUID.randomUUID();
                key = String.valueOf(uniqueKey);
                if(!dataSnapshot.child("Products").child(key).exists()){
                    HashMap<String, Object> userHashMap= new HashMap<>();
                    userHashMap.put("productKey",key);
                    userHashMap.put("productOwnerName",currentUser.getName());
                    userHashMap.put("productName",productName);
                    userHashMap.put("productPrice",productPrice.getText().toString());
                    userHashMap.put("productInfo",productInfo.getText().toString());
                    userHashMap.put("productOwner",currentUser.getNumber());
                    rootRef.child("Products").child(String.valueOf(uniqueKey)).updateChildren(userHashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                    }
                                    else{

                                    }
                                }
                            });
                    HashMap<String, Object> currentProductName = new HashMap<>();
                    currentProductName.put(String.valueOf(uniqueKey),String.valueOf(uniqueKey));
                    rootRef.child("Users").child(currentUser.getNumber()).child("UserProducts").updateChildren(currentProductName);
                }
                else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setAdapter(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getContext(),uriArrayList,null, this::zoomPicture, this::setImageCounter);
        viewPager.setAdapter(viewPagerAdapter);
    }
    private void zoomPicture(){
        Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(R.layout.custom_dialog_zoom);
        ViewPager pager  = dialog.findViewById(R.id.custom_ViewPager_dialog);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getContext(),uriArrayList,null, null, this::setImageCounter);
        pager.setAdapter(viewPagerAdapter);

        ImageView closeDialog = dialog.findViewById(R.id.custom_button_dialog);
        CircleIndicator bar = dialog.findViewById(R.id.indicator);
        bar.setViewPager(pager);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private boolean CorrectData(){
        if(uriArrayList.size() == 0){Toast.makeText(this.getContext(),"без фоточки нельзя!!!",Toast.LENGTH_SHORT).show();return false;}
        if(TextUtils.isEmpty(productTitle.getText())){Toast.makeText(this.getContext(),"что/кого ты продаёшь?",Toast.LENGTH_SHORT).show(); return false;}
        if(TextUtils.isEmpty(productPrice.getText())){Toast.makeText(this.getContext(),"почём эта шафка?",Toast.LENGTH_SHORT).show(); return false;}
        return true;
    }

    void setImageCounter(int current, int amount){
    }

}
