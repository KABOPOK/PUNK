package ru.kabopok.punk_jv.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.List;

import ru.kabopok.punk_jv.R;
import ru.kabopok.punk_jv.current.Online;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapterVh>{

    private List<Product> productList = new ArrayList<>();

    private Context context;

    private ProductOnClickListener productOnClickListener;

    private ActivateHeartListener activateHeartListener;

    private DeactivateHeartListener deactivateHeartListener;

    private User currentUser = Online.getCurrentUser();

    public ProductAdapter(List<Product> productList, Context context, ProductOnClickListener productOnClickListener,
    ActivateHeartListener activateHeartListener, DeactivateHeartListener deactivateHeartListener){
        this.productList = productList;
        this.context = context;
        this.productOnClickListener  = productOnClickListener;
        this.activateHeartListener = activateHeartListener;
        this.deactivateHeartListener = deactivateHeartListener;
    }
    public ProductAdapter(List<Product> productList, Context context, ProductOnClickListener productOnClickListener){
        this.productList = productList;
        this.context = context;
        this.productOnClickListener  = productOnClickListener;
        this.activateHeartListener = null;
        this.deactivateHeartListener = null;
    }
    public void setProductList(List<Product> filteredList) {
        productList = filteredList;
        notifyDataSetChanged();
    }

    public interface ProductOnClickListener{
        void selectedProduct(Product product);
    }

    public interface ActivateHeartListener{
        void activateHeart(Product product);
    }

    public interface DeactivateHeartListener {
        void deactivateHeart(Product product);
    }

    @NonNull
    @Override
    public ProductAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_products, parent, false);
        return new ProductAdapterVh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapterVh holder, int position) {
        Product product = productList.get(position);
        holder.row_name.setText(product.getProductName());
        holder.row_price.setText(product.getProductPrice());
        holder.row_info.setText(product.getProductInfo());
        holder.row_user_name.setText(product.getProductOwnerName());
        Picasso.with(context).load(product.getURL()).into(holder.row_image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productOnClickListener.selectedProduct(product);
            }
        });

        setSparkButtonCondition(holder.heartButton, product.getProductKey());
        holder.heartButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if(buttonState){
                    activateHeartListener.activateHeart(product);
                }
                else{
                    deactivateHeartListener.deactivateHeart(product);
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });
    }

    private void setSparkButtonCondition(SparkButton heartButton, String key) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.child("Users").child(currentUser.getNumber()).child(
                       "FavouriteProducts").child(key).exists()){
                   heartButton.setChecked(true);
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductAdapterVh extends RecyclerView.ViewHolder {
        private ImageView row_image;
        private TextView row_name;
        private TextView row_user_name;
        private TextView row_info;
        private TextView row_price;
        private SparkButton heartButton;
        public ProductAdapterVh(@NonNull View itemView) {
            super(itemView);
            row_image = itemView.findViewById(R.id.row_image_ImageView);
            row_name = itemView.findViewById(R.id.row_productName_TextView);
            row_info = itemView.findViewById(R.id.row_productInfo_TextView);
            row_price = itemView.findViewById(R.id.row_productPrice_TextView);
            heartButton = itemView.findViewById(R.id.heartOnProduct_SparkButton);
            row_user_name = itemView.findViewById(R.id.row_userName_TextView);
        }
    }
}
