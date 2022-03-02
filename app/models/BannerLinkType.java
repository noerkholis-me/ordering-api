package models;

/**
 * Created by hendriksaragih on 2/5/17.
 */
public class BannerLinkType {
    public static BannerLinkType linkTypeCategory = new BannerLinkType(1, "Category");
    public static BannerLinkType linkTypeProductList = new BannerLinkType(2, "Product List");
    public static BannerLinkType linkTypeProductDetail = new BannerLinkType(3, "Product Detail");

    private int id;
    private String name;

    public BannerLinkType(int id, String name){
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

    public static BannerLinkType[] getBannerLinkTypeByType(int type){
        switch (type){
            case 1 :{
                return new BannerLinkType[]{};
            }
            case 2 :{
                return new BannerLinkType[]
                        {BannerLinkType.linkTypeCategory, BannerLinkType.linkTypeProductList, BannerLinkType.linkTypeProductDetail};
            }
            default :{
                return new BannerLinkType[]{};
            }
        }
    }

    public static BannerLinkType getBannerLinkTypeByTypeAndId(int type, int id){
        BannerLinkType result = null;
        BannerLinkType[] listLinkType = getBannerLinkTypeByType(type);
        for(BannerLinkType linkType:listLinkType){
            if(linkType.id == id){
                result = linkType;
            }
        }
        return result;
    }
}
