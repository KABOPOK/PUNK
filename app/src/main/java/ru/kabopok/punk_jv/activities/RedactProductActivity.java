package ru.kabopok.punk_jv.activities;

import static ru.kabopok.punk_jv.fragments.ProfileFragment.RESULT_OK;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
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
import ru.kabopok.punk_jv.classes.ImageResizer;
import ru.kabopok.punk_jv.classes.LineLimitFilter;
import ru.kabopok.punk_jv.classes.LoadingBar;
import ru.kabopok.punk_jv.classes.Photo;
import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.QuitDialog;
import ru.kabopok.punk_jv.classes.User;
import ru.kabopok.punk_jv.classes.ViewPagerAdapter;
import ru.kabopok.punk_jv.current.Online;
import ru.kabopok.punk_jv.databinding.ActivityHomeBinding;
import ru.kabopok.punk_jv.databinding.FragmentPublishProductBinding;
import ru.kabopok.punk_jv.fragments.PublishProductFragment;
import ru.kabopok.punk_jv.fragments.UserProductsFragment;

public class RedactProductActivity extends AppCompatActivity {
    FragmentPublishProductBinding binding;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private EditText productTitle;
    DatabaseReference rootRef;
    private EditText productPrice;
    TextView counterLines;
    QuitDialog quitDialog;
    private EditText productInfo;
    private Button PushProduct;
    private Button pick;
    private StorageReference StoreRef;
    private Uri uriOfImg;
    private String imgUrl;
    CircleIndicator indicator;
    private int counter;
    private User currentUser = Online.getCurrentUser();
    private ViewPager viewPager;
    ArrayList<String> imagePathsList = new ArrayList<>();
    ArrayList<String> removedUrls = new ArrayList<>();
    ArrayList<Photo> DataPhotoAfterDelete = new ArrayList<>();
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    private String key;
    LoadingBar loadingBar;
    int joke=Online.CurrentRedactProduct.getImagesPathList().size();
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
                    Uri currentPhotoPath = result.getData().getData();
                    uriArrayList.add(currentPhotoPath);
                    addToAdapter(currentPhotoPath.toString());
                    indicator.setViewPager(viewPager);
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redact_product);
        rootRef = FirebaseDatabase.getInstance().getReference();
        productTitle = (EditText) findViewById(R.id.product_title);
        productPrice = (EditText) findViewById(R.id.product_price);
        productInfo = (EditText) findViewById(R.id.product_info);
        PushProduct = (Button) findViewById(R.id.push_product_button);
        counterLines = findViewById(R.id.infoLinesCounter);
        pick = (Button) findViewById(R.id.pick);
        viewPager = findViewById(R.id.images_ViewPager);
        indicator = findViewById(R.id.indicator_0);
        loadingBar = new LoadingBar(this);

        productPrice.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        productTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        productInfo.setFilters(new InputFilter[]{new LineLimitFilter(8)});
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
        PushProduct.setOnClickListener(v -> {
            loadingBar.show();
            if(CorrectData()) {
                Online.lineCount = 0;
                deleteRemovedProducts();
            }else{
                loadingBar.dismiss();
            }
        });
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(joke<8) {
                    Intent pickImg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    upLoadImage.launch(pickImg);
                }else {
                    pick.setText("я дурачёк");
                    Toast.makeText(RedactProductActivity.this, "нет, ты просто студент", Toast.LENGTH_SHORT).show();
                }
            }

        });

        imagePathsList = Online.CurrentRedactProduct.getImagesURLs();
        setAdapter();
        setOnBackPressed();
        setText();
    }
    private void deleteRemovedProducts() {
        //get Product
        DatabaseReference reLoadRef = FirebaseDatabase.getInstance().getReference("Products").child(Online.CurrentRedactProduct.getProductKey());
        rootRef = FirebaseDatabase.getInstance().getReference("Products").child(Online.CurrentRedactProduct.getProductKey()).child("images");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final StorageReference storeRef = FirebaseStorage.getInstance().getReference();
                    for (DataSnapshot snapshotPhotoList : snapshot.getChildren()) {
                        Photo photo = snapshotPhotoList.getValue(Photo.class);
                        if (removedUrls.size() != 0) {
                            boolean equal = true;
                            String url1 = photo.getURL();
                            String url2 = removedUrls.get(0);
                            if(url1.length() == url2.length()) {
                                for (int i = 0; i < photo.getURL().length(); ++i) {
                                    if (url1.charAt(i) != url2.charAt(i)) {
                                        equal = false;
                                        break;
                                    }
                                }
                            }
                            if (equal) {
                                removedUrls.remove(0);
                                storeRef.child("images/").child(photo.getCloudPath()).delete();
                            }
                        }
                        else {
                            DataPhotoAfterDelete.add(photo);
                        }
                    }
                reLoadRef.removeValue();
                ReLoadProduct();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void ReLoadProduct() {
        final DatabaseReference reLoadRef;
        reLoadRef = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> userHashMap= new HashMap<>();
        userHashMap.put("productKey",Online.CurrentRedactProduct.getProductKey());
        userHashMap.put("productOwnerName",Online.CurrentRedactProduct.getProductOwnerName());
        userHashMap.put("productName",productTitle.getText().toString());
        userHashMap.put("productPrice",productPrice.getText().toString());
        userHashMap.put("productInfo",productInfo.getText().toString());
        userHashMap.put("productOwner",Online.CurrentRedactProduct.getProductName());
        userHashMap.put("booked",false);
        reLoadRef.child("Products").child(Online.CurrentRedactProduct.getProductKey()).updateChildren(userHashMap);

        for(int i =0; i < DataPhotoAfterDelete.size(); ++i) {
            String currentPath = "images/" + UUID.randomUUID().toString();
            HashMap<String, Object> photoHashMap = new HashMap<>();
            photoHashMap.put("cloudPath", DataPhotoAfterDelete.get(i).getCloudPath());
            photoHashMap.put("URL", DataPhotoAfterDelete.get(i).getURL());
            reLoadRef.child("Products").child(Online.CurrentRedactProduct.getProductKey()).child(currentPath).updateChildren(photoHashMap);
        }
        if(uriArrayList.size() == 0){
            loadingBar.dismiss();
            Online.UserProduct = true;
            Intent backToMain  = new Intent(RedactProductActivity.this, HomeActivity.class);
            startActivity(backToMain);
        }
        else{
            uploadImgWithCompress();
        }
    }
    private void uploadImgWithCompress(){
        for(int i =0;  i < uriArrayList.size(); ++i) {
            uriOfImg = uriArrayList.get(i);
            byte[] bytes = new byte[0];
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriOfImg);
                Bitmap newWay = ImageResizer.reduceBitmapSize(bitmap, 1000000);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                newWay.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                bytes = byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String path = UUID.randomUUID().toString();
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
                                Intent inputIntent = new Intent(RedactProductActivity.this, HomeActivity.class);
                                startActivity(inputIntent);
                            }
                        }
                    });
                }
            });
        }
    }
    private void addPhoto(String imgUrl, String path) {
        HashMap<String, Object> photoHashMap= new HashMap<>();
        photoHashMap.put("cloudPath",path);
        photoHashMap.put("URL",imgUrl);
        rootRef.child(path).updateChildren(photoHashMap);
    }
    private void deleteImage(int current){
        String currentPath = imagePathsList.get(current);
        if(currentPath.contains("https")){
            removedUrls.add(currentPath);
        }
        imagePathsList.remove(current);
        indicator.setViewPager(viewPager);
        setAdapter();
        --joke;
    }
    private void zoomPicture(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_zoom);
        ViewPager pager  = dialog.findViewById(R.id.custom_ViewPager_dialog);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,imagePathsList, null, this::deleteImage, true);
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
    //passive

    private void setText() {
        productTitle.setText(Online.CurrentRedactProduct.getProductName());
        productPrice.setText(Online.CurrentRedactProduct.getProductPrice());
        productInfo.setText(Online.CurrentRedactProduct.getProductInfo());
    }
    private boolean CorrectData(){
        if(imagePathsList.size() == 0){Toast.makeText(this,"без фоточки нельзя!!!",Toast.LENGTH_SHORT).show();return false;}
        if(TextUtils.isEmpty(productTitle.getText())){Toast.makeText(this,"что/кого ты продаёшь?",Toast.LENGTH_SHORT).show(); return false;}
        if(TextUtils.isEmpty(productPrice.getText())){Toast.makeText(this,"почём эта шафка?",Toast.LENGTH_SHORT).show(); return false;}
        return true;
    }
    private void setAdapter(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,imagePathsList, this::zoomPicture, this::deleteImage);
        viewPager.setAdapter(viewPagerAdapter);
        indicator.setViewPager(viewPager);
    }
    private void addToAdapter(String currentPhotoPath){
        imagePathsList.add(currentPhotoPath);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,imagePathsList, this::zoomPicture, this::deleteImage);
        viewPager.setAdapter(viewPagerAdapter);
    }
    private void setOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Online.UserProduct = true;
                Intent backToMain  = new Intent(RedactProductActivity.this, HomeActivity.class);
                startActivity(backToMain);
            }
        });
    }
}