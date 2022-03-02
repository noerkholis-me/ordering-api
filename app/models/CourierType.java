package models;

public class CourierType {
    public static CourierType courierTypeVolumetric = new CourierType(1, "Volumetric");
    public static CourierType courierTypeVolume = new CourierType(2, "Volume");
    public static CourierType courierTypeWeight = new CourierType(3, "Weight");

    private int id;
    private String name;

    public CourierType(int id, String name){
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

    public static CourierType[] getCourierTypeList(){
        return new CourierType[]
                {CourierType.courierTypeVolumetric, CourierType.courierTypeVolume, CourierType.courierTypeWeight};

    }

    public static CourierType[] getCourierTypeList2(){
        return new CourierType[]
                {CourierType.courierTypeVolumetric, CourierType.courierTypeWeight};

    }

    public static CourierType getCourierTypeById(int id){
        CourierType result = null;
        CourierType[] listCourierType = getCourierTypeList();
        for(CourierType courierType:listCourierType){
            if(courierType.id == id){
                result = courierType;
            }
        }
        return result;
    }
}
