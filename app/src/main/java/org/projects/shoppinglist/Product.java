package org.projects.shoppinglist;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String name;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product() {} //Empty constructor we will need later!
    private Product(Parcel in) {
        name = in.readString();
        quantity = in.readInt();
    }
    public Product(String name, int quantity)
    {
        this.name = name;
        this.quantity = quantity;
    }
    @Override
    public String toString() {
        return name+" "+quantity;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeInt(quantity);
    }

    public static final Parcelable.Creator<Product> CREATOR
            = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };


}
