package utils;

public enum ConfigSettingEnum {

    RADIUS("radius");

    private String value;

    ConfigSettingEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
