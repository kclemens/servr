package kc.servr.server;

import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: kclemens
 * Date: 8/17/12
 */
public class IndexServletTest {

    private static HttpConnector connector = new HttpConnector("localhost:8080/servr/index");
    private static String csv = generateCsv("first-name,last-name,phone number,email",
            "Alice,Anderson,555-1234,allice@anderson.at",
            "Charlene,Chaplin,,cc@gmail.com",
            "Domenique,Donson,555-4321,",
            "Beatrice,Babelsberg,1234-babsy,bb@gmail.com");

    @Before
    public void createIndexes() {
        connector.withHeader("Content-Type", "text/csv").put("/test-index/", csv);
        connector.withHeader("Content-Type", "text/csv").put("/test-index-two/", csv);
        connector.withHeader("Content-Type", "text/csv").put("/test-index-3/", csv);
    }

    @Test
    public void testListIndexes() {
        Set<String> indexes = new HashSet<String>(Arrays.asList(StringUtils.split(connector.get("/"), "\n\r")));
        Assert.assertEquals(6, indexes.size());
        Assert.assertTrue(indexes.contains("iso 639-3 language codes"));
        Assert.assertTrue(indexes.contains("iso 3166 country codes"));
        Assert.assertTrue(indexes.contains("test-index"));
        Assert.assertTrue(indexes.contains("test-index-two"));
        Assert.assertTrue(indexes.contains("test-index-3"));
    }

    @Test
    public void queryInIndex() {
        List<String> rows = Arrays.asList(StringUtils.split(connector.get("/test-index/?query=gmail"), "\n\r"));
        Assert.assertEquals(3, rows.size());
        Assert.assertEquals("id,first-name,last-name,phone number,email", rows.get(0));
        Assert.assertEquals("1,Charlene,Chaplin,,cc@gmail.com", rows.get(1));
        Assert.assertEquals("3,Beatrice,Babelsberg,1234-babsy,bb@gmail.com", rows.get(2));
    }

    @After
    public void deleteIndexes() {
        connector.delete("/test-index/");
        connector.delete("/test-index-two/");
        connector.delete("/test-index-3/");
    }

    private static String generateCsv(String... lines) {
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            builder.append(line).append('\n');
        }
        return builder.toString();
    }
}
