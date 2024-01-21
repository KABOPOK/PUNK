package ru.kabopok.punk_jv.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.current.Online;
public class DeveloperAdapter extends RecyclerView.Adapter<DeveloperAdapter.UserAdapterVh>{
    private List<User> userList = new ArrayList<>();

    private Context context;

    private DeveloperAdapter.UserOnClickListener UserOnClickListener;

    private User currentUser = Online.getCurrentUser();
    long counter =0;
    long productsAmount =0;
    getDraw getDraw;

    public DeveloperAdapter(List<User> userList, Context context, DeveloperAdapter.UserOnClickListener userOnClickListener, getDraw getDraw) {
        this.userList = userList;
        this.context = context;
        this.UserOnClickListener = userOnClickListener;
        this.getDraw = getDraw;
    }

    @NonNull
    @Override
    public UserAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.row_user_developer, parent, false);
        return new UserAdapterVh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeveloperAdapter.UserAdapterVh holder, @SuppressLint("RecyclerView") int position)  {
        User user = userList.get(position);
        String name = user.getName();
        String number = user.getNumber();
        String ID = user.getGender();
        String URL = user.getPhotoUserUrl();
        String password = user.getPassword();

        holder.name.setText(name);
        holder.number.setText(number);
        holder.password.setText(password);
        holder.ID.setText(ID);
        if(URL.equals("stesnashka")){
            int drawableId = R.drawable.photo;  // Replace with your actual resource ID
            Drawable drawable = getDraw.getDrawable(drawableId);
            holder.image.setImageDrawable(drawable);
        }
        else {
            Glide.with(context).load(URL).into(holder.image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //productOnClickListener.selectedProduct(product);
            }
        });
        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserProducts(user);
                userList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    private void deleteUserProducts(User user) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(user.getNumber()).exists()){
                    productsAmount = snapshot.child("Users").child(user.getNumber()).getChildrenCount();
                    for (DataSnapshot productList : snapshot.child("Users").child(user.getNumber()).child("UserProducts").getChildren()) {
                        String productID = productList.getValue(String.class);
                        deleteProduct(productID, user);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteUser(User user) {
        final StorageReference storeRef = FirebaseStorage.getInstance().getReference();
        StorageReference reference = storeRef.child(user.getPhotoUserCloudPath());
        reference.delete();
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getNumber());
        rootRef.removeValue();
    }
    private void deleteProduct(String productID, User user) {
        final DatabaseReference[] rootRef = new DatabaseReference[1];
        final Product[] product = new Product[1];
        //get Product
        rootRef[0] = FirebaseDatabase.getInstance().getReference("Products").child(productID);

        ValueEventListener getValue = rootRef[0].addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final StorageReference storeRef = FirebaseStorage.getInstance().getReference();
                product[0] = snapshot.getValue(Product.class);
                if(snapshot.child("images").exists()) {
                    for (DataSnapshot photoList : snapshot.child("images").getChildren()) {
                        Photo photo = photoList.getValue(Photo.class);
                        storeRef.child(photo.getCloudPath()).delete();
                    }
                }
                FirebaseDatabase.getInstance().getReference("Products").child(productID).removeValue();
                deleteUser(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static class UserAdapterVh extends RecyclerView.ViewHolder{
        private ImageView image;
        private ImageView trash;
        private TextView name;
        private TextView password;
        private TextView number;
        private TextView ID;
        public UserAdapterVh(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.row_image_ImageView);
            number = itemView.findViewById(R.id.number_developer);
            password = itemView.findViewById(R.id.password_developer);
            name = itemView.findViewById(R.id.name_developer);
            ID = itemView.findViewById(R.id.telegramm_developer);
            trash = itemView.findViewById(R.id.trashCase);
        }
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }
    public interface UserOnClickListener{
        void selectedUser(User user);
    }
    public interface getDraw{
        Drawable getDrawable(int id);
    }
}
