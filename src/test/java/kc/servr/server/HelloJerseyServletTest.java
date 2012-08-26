package kc.servr.server;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: kclemens
 */
public class HelloJerseyServletTest {

    private static HttpConnector connector;

    @BeforeClass
    public static void beforeAllTests() {
        connector = new HttpConnector("localhost:8080/servr/index");
    }

    @Test
    public void testGet() {
        Assert.assertEquals("hello", connector.get("/hello/"));
        Assert.assertEquals("hello", connector.get("/hello"));
    }

    @Test
    public void testPost() {
        Assert.assertEquals("hello-post", connector.post("/hello/", "hello-post"));
        Assert.assertEquals("hello-post", connector.post("/hello", "hello-post"));
    }

    @Test
    public void testPut() {
        Assert.assertEquals("hello-put", connector.put("/hello/", "hello-put"));
        Assert.assertEquals("hello-put", connector.put("/hello", "hello-put"));
    }

    @Test
    public void testDelete() {
        Assert.assertEquals("deleted", connector.delete("/hello/"));
        Assert.assertEquals("deleted", connector.delete("/hello"));
    }
}
