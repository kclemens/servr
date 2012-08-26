package kc.servr.index;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import kc.servr.model.Rows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

/**
 * User: kclemens
 * Date: 6/23/12
 */
@Singleton
public class IndexService {

    public static final int MAX_ROW_COUNT = 100;

    private static final Logger LOG = Logger.getLogger(IndexService.class.getSimpleName());

    private final HashMap<String, Index> indexes;
    private final IndexFactory factory;

    @Inject
    public IndexService(final IndexFactory factory) {
        this.factory = factory;
        indexes = new HashMap<String, Index>();
    }

    public Index createIndex(String name, String[] columns) {
        if (indexes.containsKey(name)) {
            throw new IllegalArgumentException("Cannot create addRow '" + name + "' - it already exists!");
        }

        Index index = factory.createNewIndex(columns);
        indexes.put(name, index);

        LOG.fine("created new index " + name + " with " + columns.length + " columns: " + Arrays.toString(columns));
        return index;

    }

    public Set<String> getIndexes() {
        return indexes.keySet();
    }

    public void deleteIndex(String name) {
        checkIndexUsage("delete index", name);
        indexes.remove(name);
        LOG.fine("deleted index " + name);
    }

    public void index(String name, String[] fields) {
        checkIndexUsage("addRow into", name);
        indexes.get(name).addRow(fields);
        LOG.fine("added to index " + name + ": " + Arrays.toString(fields));
    }

    public Rows query(String name, String query) {
        checkIndexUsage("query in", name);
        Rows rows = indexes.get(name).query(query);

        if (rows.getRows().length > MAX_ROW_COUNT) {
            rows = new Rows(rows.getHeaders());
        }

        return rows;
    }

    public Rows getRow(String name, String id) {
        checkIndexUsage("get from", name);
        return indexes.get(name).getRow(id);
    }

    private void checkIndexUsage(String use, String name) {
        if (!indexes.containsKey(name)) {
            throw new IllegalArgumentException("Cannot " + use + " '" + name + "' - it does not exist!");
        }
    }
}
