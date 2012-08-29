package kc.servr.model;

import java.util.Arrays;

/**
 * User: kclemens
 * Date: 6/25/12
 */
public class Rows {

    private final String[] headers;
    private final Row[] rows;

    public Rows(String[] headers, Row... rows) {
        this.headers = headers;
        this.rows = rows;
    }

    public String[] getHeaders() {
        return headers;
    }

    public Row[] getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return "Rows{" +
                "headers=" + (headers == null ? null : Arrays.asList(headers)) +
                ", rows=" + (rows == null ? null : Arrays.asList(rows)) +
                '}';
    }
}
