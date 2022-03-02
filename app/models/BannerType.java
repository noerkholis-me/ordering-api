package models;

/**
 * Created by hendriksaragih on 2/5/17.
 */
public class BannerType {
    public static BannerType bannerTypeAll    = new BannerType(0, "Web & Mobile");
    public static BannerType bannerTypeWeb    = new BannerType(1, "Web");
    public static BannerType bannerTypeMobile = new BannerType(2, "Mobile");
    private int id;
    private String name;

    public BannerType(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
