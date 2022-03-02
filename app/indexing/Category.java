package indexing;

import com.github.cleverage.elasticsearch.Indexable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hendriksaragih on 8/8/17.
 */
public class Category implements Indexable {
    public String id;
    public String name;

    public Category() {
        super();
    }

    public Category(models.Category model) {
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
        Category result = new Category();
        result.id = (String) map.get("id");
        result.name = (String) map.get("name");
        return result;
    }
}
