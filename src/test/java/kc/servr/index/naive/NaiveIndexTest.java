package kc.servr.index.naive;

import kc.servr.index.Index;
import kc.servr.model.Row;
import kc.servr.model.Rows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: kclemens
 * Date: 6/22/12
 */
public class NaiveIndexTest {

    public static final String COLUMN_A = "column_a";
    public static final String COLUMN_B = "column_b";
    public static final String COLUMN_C = "column_c";
    public static final String[] COLUMNS = new String[]{COLUMN_A, COLUMN_B, COLUMN_C};

    private Index index;

    @Before
    public void beforeEachTest() {
        index = new NaiveIndex(COLUMNS);
    }

    @Test
    public void shouldIndexAndGet() {
        String[] fields = {"value_a", "value_b", "value_c"};

        String id = index.addRow(fields);
        Rows rows = index.getRow(id);

        Assert.assertEquals(1, rows.getRows().length);
        Assert.assertArrayEquals(fields, rows.getRows()[0].getFields());
    }

    @Test
    public void shouldIndexAndFind() {
        String id = index.addRow(new String[]{"text to", "addRow", "and find"});

        Assert.assertEquals(Arrays.asList(id), extractRowIds(index.query("text")));
        Assert.assertEquals(Arrays.asList(id), extractRowIds(index.query("to")));
        Assert.assertEquals(Arrays.asList(id), extractRowIds(index.query("addRow")));
        Assert.assertEquals(Arrays.asList(id), extractRowIds(index.query("and")));
        Assert.assertEquals(Arrays.asList(id), extractRowIds(index.query("find")));
    }

    @Test
    public void shouldIndexAndFindMultiple() {
        String id_a = index.addRow(new String[]{"both", "ab", "ac"});
        String id_b = index.addRow(new String[]{"both", "bb", "bc"});

        Assert.assertFalse(id_a.equals(id_b));
        Assert.assertEquals(Arrays.asList(id_a), extractRowIds(index.query("ab")));
        Assert.assertEquals(Arrays.asList(id_a), extractRowIds(index.query("ac")));
        Assert.assertEquals(Arrays.asList(id_a, id_b), extractRowIds(index.query("both")));
        Assert.assertEquals(Arrays.asList(id_b), extractRowIds(index.query("bb")));
        Assert.assertEquals(Arrays.asList(id_b), extractRowIds(index.query("bc")));
    }

    @Test
    public void shouldIndexWithNullFields() {
        String id = index.addRow(new String[]{"hello", null, "hallo"});

        Assert.assertEquals(Arrays.asList(id), extractRowIds(index.query("hello")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldIndexWithExtraFieldRejected() {
        index.addRow(new String[]{"value_a", "value_b", "value_c", "extra_value"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIndexWithMissingFieldRejected() {
        index.addRow(new String[]{"value_a", "value_b"});
    }

    @Test
    public void shouldOrderSuggestionListByRelevance() {
        String id_a = index.addRow(new String[]{"hit", null, "hit"});
        String id_b = index.addRow(new String[]{"hit", null, null});
        String id_c = index.addRow(new String[]{"hit", "hit", "hit"});

        Assert.assertEquals(Arrays.asList(id_c, id_a, id_b), extractRowIds(index.query("hit")));
    }

    @Test
    public void shouldListFullIndexOnEmptyQuery() {
        String id_a = index.addRow(new String[]{"hit", null, "hit"});
        String id_b = index.addRow(new String[]{"hit", null, null});
        String id_c = index.addRow(new String[]{"hit", "hit", "hit"});

        Assert.assertEquals(Arrays.asList(id_a, id_b, id_c), extractRowIds(index.query("")));
    }

    @Test
    public void shouldFindByPrefix() {
        String id_a = index.addRow(new String[]{"hit", null, "hit"});
        String id_b = index.addRow(new String[]{"hit", null, null});
        String id_c = index.addRow(new String[]{"hit", "hit", "hit"});

        Assert.assertEquals(Arrays.asList(id_c, id_a, id_b), extractRowIds(index.query("hi")));
    }

    @Test
    public void shouldFindByToken() {
        String id_a = index.addRow(new String[]{"some/hit", null, "other hit"});
        String id_b = index.addRow(new String[]{"blah@hit", null, null});
        String id_c = index.addRow(new String[]{"foo hit", "bar&hit", "hit"});

        Assert.assertEquals(Arrays.asList(id_c, id_a, id_b), extractRowIds(index.query("hit")));
    }

    @Test
    public void shouldIgnoreCasing() {
        String id_a = index.addRow(new String[]{"HiT", null, null});
        String id_b = index.addRow(new String[]{"HIT", null, null});
        String id_c = index.addRow(new String[]{"hit", null, null});

        Assert.assertEquals(Arrays.asList(id_a, id_b, id_c), extractRowIds(index.query("hIt")));

    }

    private List<String> extractRowIds(Rows rows) {
        List<String> ids = new ArrayList<String>();
        for (Row row : rows.getRows()) {
            ids.add(row.getId());
        }
        return ids;
    }
}
