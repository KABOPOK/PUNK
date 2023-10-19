package ru.kabopok.punk_jv.classes;

import android.content.Context;
import android.graphics.Matrix;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.kabopok.punk_jv.R;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapterVh>{

    private List<Product> productList = new ArrayList<>();

    private Context context;

    private ProductOnClickListener productOnClickListener;



    public ProductAdapter(List<Product> productList, Context context, ProductOnClickListener productOnClickListener){
        this.productList = productList;
        this.context = context;
        this.productOnClickListener  = productOnClickListener;
    }

    public void setProductList(List<Product> filteredList) {
        productList = filteredList;
        notifyDataSetChanged();
    }

    public interface ProductOnClickListener{
        void selectedProduct(Product product);
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
        Picasso.with(context).load(product.getURL()).into(holder.row_image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productOnClickListener.selectedProduct(product);
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
        private TextView row_info;
        private TextView row_price;
        public ProductAdapterVh(@NonNull View itemView) {
            super(itemView);
            row_image = itemView.findViewById(R.id.row_image_ImageView);
            row_name = itemView.findViewById(R.id.row_productName_TextView);
            row_info = itemView.findViewById(R.id.row_productInfo_TextView);
            row_price = itemView.findViewById(R.id.row_productPrice_TextView);
        }
    }
}
