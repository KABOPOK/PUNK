package ru.kabopok.punk_jv.classes;

import java.util.ArrayList;
import java.util.Vector;

public class Product {

    String productKey;
    String productOwnerName;
    String URL;
    String productName;
    String productPrice;
    String productInfo;
    String productOwner;

    ArrayList<String> imagesURLs = new ArrayList<>();
    public Product(){
        this.URL = null;
        this.productKey = null;
        this.productOwnerName = null;
        this.productName = null;
        this.productPrice = null;
        this.productInfo = null;
        this.productOwner = null;
        imagesURLs = null;
    }

    public Product(String productKey, String productOwnerName, String URL, String productName, String productPrice, String productInfo, String productOwner) {
        this.productKey = productKey;
        this.productOwnerName = productOwnerName;
        this.URL = URL;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productInfo = productInfo;
        this.productOwner = productOwner;
    }

    public String getOnePhoto(){
        return imagesURLs.get(0);
    }

    public void setImagesURLs(ArrayList<String> imagesURLs){
        this.imagesURLs = imagesURLs;
    }

    public ArrayList<String> getImagesURLs() {
        return imagesURLs;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getProductOwnerName() {
        return productOwnerName;
    }

    public void setProductOwnerName(String productOwnerName) {
        this.productOwnerName = productOwnerName;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    public String getProductOwner() {
        return productOwner;
    }

    public void setProductOwner(String productOwner) {
        this.productOwner = productOwner;
    }

    public Product(String imgPath, String productName, String productPrice, String productInfo, String productOwner) {
        this.URL = imgPath;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productInfo = productInfo;
        this.productOwner = productOwner;
    }
}

