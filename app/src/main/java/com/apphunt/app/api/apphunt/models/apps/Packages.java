package com.apphunt.app.api.apphunt.models.apps;

import com.apphunt.app.constants.Constants;

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
}
