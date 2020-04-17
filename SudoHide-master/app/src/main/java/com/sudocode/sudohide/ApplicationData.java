package com.sudocode.sudohide;

import android.graphics.drawable.Drawable;

public class ApplicationData implements Comparable<ApplicationData> {

    private final String title;
    private final String key;

    public ApplicationData(String title, String key) {
        this.title = title;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int compareTo(ApplicationData another) {
        return this.title.compareToIgnoreCase(another.title);
    }
}
