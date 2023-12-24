package ru.kabopok.punk_jv.classes;

import java.util.ArrayList;
import java.util.Vector;

public class Product {

    String productKey;
    String productOwnerName;
    Photo photo;
    ArrayList<String> pathImages = new ArrayList<>();
    String productName;
    String productPrice;
    String productInfo;
    String productOwner;

    ArrayList<String> imagesURLs = new ArrayList<>();
    public Product(){
        this.photo = null;
        this.productKey = null;
        this.productOwnerName = null;
        this.productName = null;
        this.productPrice = null;
        this.productInfo = null;
        this.productOwner = null;
    }

    public Product(String productKey, String productOwnerName, String URL, String productName, String productPrice, String productInfo, String productOwner, Photo photo) {
        this.productKey = productKey;
        this.productOwnerName = productOwnerName;
        this.photo = photo;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productInfo = productInfo;
        this.productOwner = productOwner;
    }
    public void pushImagesPath(String X){
        pathImages.add(X);
    }
    public ArrayList<String> getImagesPathList(){
        return pathImages;
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

    public Photo getPhoto() {
        return photo;
    }

    public void setURL(String URL){this.photo.setURL(URL);}
    public String getURL(String URL){return photo.getURL();}

    public void setPhoto(String URL) {
        this.photo = photo;
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

    public Product(Photo photo, String productName, String productPrice, String productInfo, String productOwner) {
        this.photo = photo;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productInfo = productInfo;
        this.productOwner = productOwner;
    }
}

