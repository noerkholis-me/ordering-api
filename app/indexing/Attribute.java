package indexing;

import com.github.cleverage.elasticsearch.Indexable;

import java.util.*;

/**
 * Created by hendriksaragih on 8/8/17.
 */
public class Attribute implements Indexable {
    public String id;
    public String name;


    public Attribute() {
        super();
    }

    public Attribute(models.Attribute model) {
        this.id = Long.toString(model.id);
        this.name = model.baseAttribute.name.concat(" : ").concat(model.getName());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Map toIndex() {
        Map map = new HashMap();
        map.put("id", id);
        map.put("name", name);
        return map;
    }

    public static List<Attribute> convertAttributes(Set<models.Attribute> models) {
        List<Attribute> result = new ArrayList<>();
        for (models.Attribute attribute : models) {
            Attribute insertTarget = new Attribute(attribute);
            result.add(insertTarget);
        }
        return result;
    }

    @Override
    public Indexable fromIndex(Map map) {
        if (map == null) {
            return new Brand();
        }
        Attribute result = new Attribute();
        result.id = (String) map.get("id");
        result.name = (String) map.get("name");
        return result;
    }
}
