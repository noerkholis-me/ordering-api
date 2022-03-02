package models;

/**
 * Created by hendriksaragih on 2/11/17.
 */
public class KeyValue {
    private Integer id;
    private String value;

    public KeyValue(Integer id, String value){
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
