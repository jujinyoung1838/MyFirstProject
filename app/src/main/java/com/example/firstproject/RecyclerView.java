package com.example.firstproject;

import android.media.Image;
import android.widget.ImageView;

public class RecyclerView {

    int CSImage;
    String name;

    public RecyclerView(int CSImage, String name) {
        this.CSImage = CSImage;
        this.name = name;
    }

    public int getCSImage() {
        return CSImage;
    }

    public String getName() {
        return name;
    }

    public void setCSImage(int CSImage) {
        this.CSImage = CSImage;
    }

    public void setName(String name) {
        this.name = name;
    }
}
