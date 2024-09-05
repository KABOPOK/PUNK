package ru.kabopok.punk_jv.current;

import com.google.firebase.database.core.view.Change;

import ru.kabopok.punk_jv.classes.Product;
import ru.kabopok.punk_jv.classes.User;

public class Online {
    public static boolean UserProduct;
    private static Product CurrentProduct;
    public static Product CurrentRedactProduct;
    public static boolean TurnOff = false;
    public static int lineCount = 0;
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
    public static void ChangePhotoData(String URL, String path){
        CurrentUser.setPhotoUserUrl(URL);
        CurrentUser.setPhotoUserCloudPath(path);
    }
}
