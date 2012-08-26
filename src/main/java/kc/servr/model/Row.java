package kc.servr.model;

/**
 * User: kclemens
 * Date: 6/20/12
 */
public class Row {

    private final String id;
    private final String[] fields;

    public Row(String id, String[] fields) {
        this.id = id;
        this.fields = fields;
    }

    public Row(String[] fields) {
        this.id = null;
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public String[] getFields() {
        return fields;
    }
}
