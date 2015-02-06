package com.apphunt.app.api.models;

import com.apphunt.app.utils.Constants;

import java.util.ArrayList;

public class Packages {
    private ArrayList<String> packages = new ArrayList<>();
    private ArrayList<String> availablePackages;
    private ArrayList<String> existingPackages;
    private String platform = Constants.PLATFORM;

    public ArrayList<String> getPackages() {
        return packages;
    }

    public void setPackages(ArrayList<String> packages) {
        this.packages = packages;
    }

    public ArrayList<String> getAvailablePackages() {
        return availablePackages;
    }

    public void setAvailablePackages(ArrayList<String> availablePackages) {
        this.availablePackages = availablePackages;
    }

    public ArrayList<String> getExistingPackages() {
        return existingPackages;
    }

    public void setExistingPackages(ArrayList<String> existingPackages) {
        this.existingPackages = existingPackages;
    }

    @Override
    public String toString() {
        return "Packages{" +
                "packages=" + packages +
                '}';
    }
}
