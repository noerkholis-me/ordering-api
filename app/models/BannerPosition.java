package models;

/**
 * Created by hendriksaragih on 2/5/17.
 */
public class BannerPosition {
    private static final int[] sizeMainDekstop = new int[]{1280,640};
    private static final int[] sizeMainMobile = new int[]{1280,960};
    private static final int[] sizeHalfDekstop = new int[]{1280,320};
    private static final int[] sizeHalfMobile = new int[]{1280,480};
    private static final int[] sizeMiddleDekstop = new int[]{1110,240};
    private static final int[] sizeFooterDekstop = new int[]{1110,400};

    public static BannerPosition bannerWMain   = new BannerPosition(1, "Desktop", "Main Banner", sizeMainDekstop, sizeMainDekstop);
    public static BannerPosition bannerWMiddle = new BannerPosition(2, "Desktop", "Middle Banner", sizeMiddleDekstop, sizeMiddleDekstop);
    public static BannerPosition bannerWFooter = new BannerPosition(3, "Desktop", "Footer Banner", sizeFooterDekstop, sizeFooterDekstop);
    public static BannerPosition bannerMMain   = new BannerPosition(4, "Mobile", "Main Banner", sizeMainMobile, null);
    public static BannerPosition bannerMMiddle = new BannerPosition(5, "Mobile", "Middle Banner", sizeHalfMobile, null);
    public static BannerPosition bannerMFooter = new BannerPosition(6, "Mobile", "Footer Banner", sizeMainMobile, null);

    private int id;
    private String type;
    private String name;
    private int[] resolution;
    private int[] resolutionResponsive;

    public BannerPosition(int id, String type, String name, int[] resolutionDekstop, int[] resolutionMobile) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.setResolution(resolutionDekstop);
        this.setResolutionResponsive(resolutionMobile);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResolution() {
        return (resolution==null) ? null : resolution[0]+"x"+resolution[1];
    }

    public String getJsonResolution(){
        return (resolution==null) ? null : "["+resolution[0]+","+resolution[1]+"]";
    }

    public int[] fetchRawResolution(){
        return resolution;
    }

    public void setResolution(int[] resolution) {
        this.resolution = resolution;
    }

    public String getResolutionResponsive() {
        return (resolutionResponsive==null) ? null : resolutionResponsive[0]+"x"+resolutionResponsive[1];
    }

    public String getJsonResolutionResponsive(){
        return (resolutionResponsive==null) ? null : "["+resolutionResponsive[0]+","+resolutionResponsive[1]+"]";
    }

    public int[] fetchRawResponsiveResolution(){
        return resolutionResponsive;
    }

    public void setResolutionResponsive(int[] resolutionMobile) {
        this.resolutionResponsive = resolutionMobile;
    }

    public static BannerPosition[] getBannerPositionByType(int type){
        switch (type){
            case 0 :{
                return new BannerPosition[]
                        {BannerPosition.bannerWMain, BannerPosition.bannerWMiddle,
                                BannerPosition.bannerWFooter};
            }
            case 1 :{
                return new BannerPosition[]
                        {BannerPosition.bannerWMain, BannerPosition.bannerWMiddle,
                                BannerPosition.bannerWFooter};
            }
            case 2 :{
                return new BannerPosition[]
                        {BannerPosition.bannerMMain, BannerPosition.bannerMMiddle,
                                BannerPosition.bannerMFooter};
            }
            default :{
                return new BannerPosition[]{};
            }
        }
    }
}
