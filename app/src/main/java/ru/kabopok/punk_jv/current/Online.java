package ru.kabopok.punk_jv.current;

import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.User;

public class Online {
    private static Product CurrentProduct;
    private static User CurrentUser;

    public static final String UserPhoneKey = "";
    public static final String UserPasswordKey = "";

    public Online(){ CurrentProduct = null; }
    public Online(Product product){ CurrentProduct = product; }
    public static void setCurrentProduct(Product product) {
        CurrentProduct = product;
    }
    public static void setCurrentUser(User user) { CurrentUser = user; }
    public static Product getCurrentProduct() {
        return CurrentProduct;
    }
    public static User getCurrentUser() {
        return CurrentUser;
    }
}
