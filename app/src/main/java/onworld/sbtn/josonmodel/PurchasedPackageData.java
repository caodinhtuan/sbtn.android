package onworld.sbtn.josonmodel;

import java.util.ArrayList;

/**
 * Created by onworldtv on 6/24/16.
 */
public class PurchasedPackageData {
    private int error;
    private String message;
    private ArrayList<PurchasedPackageListDetailData> data;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<PurchasedPackageListDetailData> getData() {
        return data;
    }

    public void setData(ArrayList<PurchasedPackageListDetailData> data) {
        this.data = data;
    }
}
