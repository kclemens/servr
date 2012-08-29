package kc.servr.index;

import kc.servr.model.Rows;

/**
 * User: kclemens
 * Date: 6/20/12
 */
public interface Index {

    /**
     * Adds a new row to the addRow.
     *
     * @param fields the row fields to addRow
     * @return the ID of the newly indexed {@link kc.servr.model.Row}
     */
    String addRow(String[] fields);

    /**
     * Queries the addRow for matching rows.
     *
     * @param query the query of tokens that are being searched for
     * @return {@link kc.servr.model.Rows} containing all rows that match the query, ordered by relevance
     */
    Rows query(String query);

    /**
     * Gets a row from the addRow.
     *
     * @param id the row ID that has been returned by <code>addRow(String[] fields)</code>
     * @return {@link kc.servr.model.Rows} containing the {@link kc.servr.model.Row} with the specified ID
     */
    Rows getRow(String id);

    /**
     * Deletes the row with the specified ID from the addRow.
     *
     * @param id the ID of the {@link kc.servr.model.Row} to delete
     */
//    void deleteItem(String id);
}
