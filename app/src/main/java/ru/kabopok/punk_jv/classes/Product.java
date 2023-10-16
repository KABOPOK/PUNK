package ru.kabopok.punk_jv.classes;
public class Product {
    String URL;
    String productName;
    String productPrice;
    String productInfo;
    String productOwner;
    public Product(){
        this.URL = null;
        this.productName = null;
        this.productPrice = null;
        this.productInfo = null;
        this.productOwner = null;
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

