package it.appspice.android.api.models;

import com.google.gson.annotations.Expose;

import java.util.Map;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/2/15.
 */
public class Variable {

    @Expose
    private String name;

    @Expose
    private Map<String, Object> value;

    public String getName() {
        return name;
    }

    public Map<String, Object> getValue() {
        return value;
    }
}
