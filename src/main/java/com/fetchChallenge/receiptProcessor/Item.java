package com.fetchChallenge.receiptProcessor;

public class Item {
    private String shortDescription;
    private double price;

    public Item(String shortDescription, double price) {
        this.shortDescription = shortDescription;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return shortDescription;
    }

    public void setDescription(String description) {
        shortDescription = description;
    }
}
