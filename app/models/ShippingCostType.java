package models;

public class ShippingCostType {
    public static ShippingCostType costTypeReguler = new ShippingCostType(1, "Regular");
    public static ShippingCostType costTypeExpress = new ShippingCostType(2, "Express");

    private int id;
    private String name;

    public ShippingCostType(int id, String name){
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

    public static ShippingCostType[] getShippingCostTypeList(){
        return new ShippingCostType[]
                {ShippingCostType.costTypeReguler, ShippingCostType.costTypeExpress};
    }

    public static ShippingCostType getShippingCostTypeById(int id){
        ShippingCostType result = null;
        ShippingCostType[] listType = getShippingCostTypeList();
        for(ShippingCostType type:listType){
            if(type.id == id){
                result = type;
            }
        }
        return result;
    }
}
