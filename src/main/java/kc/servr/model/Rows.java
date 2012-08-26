package kc.servr.model;

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

    //TODO add possibility to highlight query matches
}
