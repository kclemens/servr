package kc.servr.model;

import java.util.Arrays;

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

    @Override
    public String toString() {
        return "Row{" +
                "id='" + id + '\'' +
                ", fields=" + (fields == null ? null : Arrays.asList(fields)) +
                '}';
    }
}
