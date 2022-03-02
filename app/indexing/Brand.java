package indexing;

import com.github.cleverage.elasticsearch.Indexable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hendriksaragih on 8/8/17.
 */
public class Brand implements Indexable {
    public String id;
    public String name;

    public Brand() {
        super();
    }

    public Brand(models.Brand model) {
        this.id = Long.toString(model.id);
        this.name = model.name;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Map toIndex() {
        Map map = new HashMap();
        map.put("id", id);
        map.put("name", name);
        return map;
    }

    @Override
    public Indexable fromIndex(Map map) {
        if (map == null) {
            return new Brand();
        }
        Brand result = new Brand();
        result.id = (String) map.get("id");
        result.name = (String) map.get("name");
        return result;
    }

}
