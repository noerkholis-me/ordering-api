package models;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
public class ConfigSettings extends BaseModel{
    public String module;

    @Column(unique=true)
    public String key;


    public String name;

    public String value;

    public static Finder<Long, ConfigSettings> find = new Finder<>(Long.class, ConfigSettings.class);

    
    
    public ConfigSettings(String module, String key, String name, String value) {
		super();
		this.module = module;
		this.key = key;
		this.name = name;
		this.value = value;
	}



	public static String validation(String name, String key, String value, String module) {
        if (name.equals("")|| name==null) {
            return "Name must not empty.";
        }
        if (key.equals("")|| key==null) {
            return "Key must not empty.";
        }
        if (module.equals("")|| module == null) {
            return "Description must not empty.";
        }
        return null;
    }
}
