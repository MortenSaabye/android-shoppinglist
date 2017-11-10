package org.projects.shoppinglist;

public class Product {

    String name;
    int quantity;

    public Product() {} //Empty constructor we will need later!

    public Product(String name, int quantity)
    {
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return name+" "+quantity;
    }

}
