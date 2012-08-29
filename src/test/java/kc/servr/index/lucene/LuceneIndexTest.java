package kc.servr.index.lucene;

import org.junit.Before;
import org.junit.Test;

/**
 * User: kclemens
 * Date: 8/29/12
 */
public class LuceneIndexTest {

    private LuceneIndex index;

    @Before
    public void beforeEachTest() {
        index = new LuceneIndex(new String[]{"first", "last", "phone", "mail"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectWrongFieldCount() {
        index.addRow(new String[] {"first", "last", "mail"});
    }

    @Test
    public void shouldIndexAndFind() {
        index.addRow(new String[]{"alice", "anderson", "555-12345", "alice@anderson.at"});
        index.addRow(new String[]{"brenda", "babushka", "12345", "bb@gmail.com"});
        index.addRow(new String[]{"charlotte", "clemens", "", "charly@gmail.com"});
        index.addRow(new String[]{"denice", "dickens", "1800-dedi", "dedi@gmx.at"});

        System.out.println(index.query("12345"));
    }
}
