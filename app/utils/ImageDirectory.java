package utils;

public enum ImageDirectory {
    BRAND("brand", "brand"),
    CATEGORY("category", "category");


    private String key;
    private String directory;

    private ImageDirectory(String key, String directory) {
        this.setKey(key);
        this.directory = directory;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public static String getImageDirectory(String key) {
        for (ImageDirectory imageDirectory : ImageDirectory.values()) {
            if (imageDirectory.getKey().equals(key)) {
                return imageDirectory.getDirectory().toLowerCase();
            }
        }
        return null;
    }
}
