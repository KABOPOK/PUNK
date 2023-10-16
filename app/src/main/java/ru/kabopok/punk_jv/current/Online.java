package ru.kabopok.punk_jv.current;

import ru.kabopok.punk_jv.classes.Product;

public class Online {
    private static Product CurrentProduct;
    public Online(){ CurrentProduct = null; }
    public Online(Product product){ CurrentProduct = product; }
    public static void setCurrentProduct(Product product) {
        CurrentProduct = product;
    }
    public static Product getCurrentProduct() {
        return CurrentProduct;
    }
}
