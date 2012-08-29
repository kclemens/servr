package kc.servr.index.naive;

import kc.servr.index.Index;
import kc.servr.model.Row;
import kc.servr.model.Rows;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;

import java.util.*;
import java.util.logging.Logger;

/**
 * User: kclemens
 * Date: 6/22/12
 */
public class NaiveIndex implements Index {
    private final static Logger LOG = Logger.getLogger(NaiveIndex.class.getSimpleName());

    private final Map<String, List<Row>> termIndex;
    private final Map<String, Row> idIndex;
    private final String[] headers;

    private int nextId = 0;

    NaiveIndex(String[] headers) {
        LOG.info("creating index with " + headers.length + " headers: " + Arrays.toString(headers));
        this.headers = headers;
        this.termIndex = new HashMap<String, List<Row>>();
        this.idIndex = new LinkedHashMap<String, Row>();
    }

    @Override
    public String addRow(String[] fields) {
        Row row = getNewRow(fields);

        idIndex.put(row.getId(), row);

        for (String field : fields) {
            if (field != null) {
                for (String token : field.split("\\W")) {
                    token = token.toLowerCase(Locale.US);
                    for (int i = token.length(); i > 0; i--) {
                        String subToken = token.substring(0, i);
                        if (!termIndex.containsKey(subToken)) {
                            termIndex.put(subToken, new ArrayList<Row>());
                        }
                        termIndex.get(subToken).add(row);
                    }
                }
            }
        }

        LOG.fine("indexed row with id " + row.getId() + ": " + Arrays.toString(fields));
        return row.getId();
    }

    @Override
    public Rows query(String query) {
        List<Row> rows;
        if (StringUtils.isNotBlank(query)) {
            final Map<Row, Integer> itemCounts = new HashMap<Row, Integer>();
            LOG.fine("searching for " + query);
            // collect and count rows
            for (String token : query.split("\\W")) {
                token = token.toLowerCase(Locale.US);
                if (termIndex.containsKey(token)) {
                    for (Row row : termIndex.get(token)) {
                        if (termIndex.containsKey(token)) {
                            if (!itemCounts.containsKey(row)) {
                                itemCounts.put(row, 0);
                            }
                            itemCounts.put(row, itemCounts.get(row) + 1);
                        }
                    }
                }
            }

            // sort them according to reception counts
            TreeSet<Row> rowsTree = new TreeSet<Row>(new Comparator<Row>() {
                @Override
                public int compare(Row o1, Row o2) {
                    CompareToBuilder builder = new CompareToBuilder();
                    builder.append(itemCounts.get(o2), itemCounts.get(o1));
                    builder.append(o1.getId(), o2.getId());
                    return builder.toComparison();
                }
            });
            for (Row row : itemCounts.keySet()) {
                rowsTree.add(row);
            }
            rows = new ArrayList<Row>(rowsTree);
            LOG.fine("found " + rows.size() + " matching rows for " + query);

        } else {
            LOG.fine("fetching all rows");
            rows = new ArrayList<Row>(idIndex.values());
        }

        return new Rows(headers, rows.toArray(new Row[]{}));
    }

    @Override
    public Rows getRow(String id) {
        if (idIndex.containsKey(id)) {
            return new Rows(headers, idIndex.get(id));
        } else {
            return null;
        }
    }

    private synchronized Row getNewRow(String[] fields) {
        if (this.headers.length != fields.length) {
            throw new IllegalArgumentException(String.format("Cannot addRow item with %s fields %s into addRow with %s headers!", fields.length, Arrays.toString(fields), this.headers.length));
        }
        return new Row(String.format("%d", this.nextId++), fields);
    }
}
