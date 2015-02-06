package it.appspice.android.api.models;

import java.util.Map;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/2/15.
 */
public class VariableProperties {

    private final Map<String, Object> rawProperties;

    private VariableProperties(Map<String, Object> rawProperties) {
        this.rawProperties = rawProperties;
    }

    public static VariableProperties fromVariable(Variable variable) {
        return new VariableProperties(variable.getValue());
    }

    public String get(String name) {
        return rawProperties.get(name).toString();
    }

    public int getInt(String name) {
        return Integer.parseInt(get(name));
    }

    public long getLong(String name) {
        return (long) Double.parseDouble(get(name));
    }
}
