package kc.servr.index;

import junit.framework.Assert;
import kc.servr.model.Row;
import kc.servr.model.Rows;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashSet;

/**
 * User: kclemens
 * Date: 6/25/12
 */
public class IndexServiceTest {

    private IndexFactory factory;
    private IndexService service;

    @Before
    public void beforeEachTest() {
        factory = Mockito.mock(IndexFactory.class);
        service = new IndexService(factory);
    }

    @Test
    public void shouldCreateNewIndexWithColumns() {
        String name_a = "name_a";
        String name_b = "name_b";
        String[] columns_a = {"columns_a"};
        String[] columns_b = {"columns_b", "columns_b_2"};
        prepareMockIndexFactory(columns_a);
        prepareMockIndexFactory(columns_b);

        service.createIndex(name_a, columns_a);
        service.createIndex(name_b, columns_b);

        Mockito.verify(factory).createNewIndex(columns_a);
        Mockito.verify(factory).createNewIndex(columns_b);
    }

    @Test
    public void shouldListCreatedIndexes() {
        String name_a = "name_a";
        String name_b = "name_b";
        String name_c = "name_c";
        String[] columns = {"columns"};
        prepareMockIndexFactory(columns);

        service.createIndex(name_a, columns);
        service.createIndex(name_b, columns);
        service.createIndex(name_c, columns);

        Assert.assertEquals(new HashSet<String>(Arrays.asList(name_a, name_b, name_c)), service.getIndexes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotOverwriteExistingIndex() {
        String name = "name";
        String[] columns = {"columns"};
        prepareMockIndexFactory(columns);

        service.createIndex(name, columns);
        service.createIndex(name, columns);
    }

    @Test
    public void shouldDeleteSpecifiedIndex() {
        String name_a = "name_a";
        String name_b = "name_b";
        String name_c = "name_c";
        String[] columns = {"columns"};
        prepareMockIndexFactory(columns);

        service.createIndex(name_a, columns);
        service.createIndex(name_b, columns);
        service.createIndex(name_c, columns);
        service.deleteIndex(name_b);

        Assert.assertEquals(new HashSet<String>(Arrays.asList(name_a, name_c)), service.getIndexes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotDeleteNonExistingIndex() {
        service.deleteIndex("not-existing-addRow");
    }

    @Test
    public void shouldIndexInSpecifiedIndex() {
        String name = "name";
        String[] columns = {"columns"};
        String[] fields = {"fields"};
        Index index = prepareMockIndexFactory(columns);

        service.createIndex(name, columns);
        service.index(name, fields);

        Mockito.verify(index).addRow(fields);
    }

    @Test
    public void shouldQueryInSpecifiedIndex() {
        String[] columns = {"columns"};
        String name = "name";
        String query = "query";
        Index index = prepareMockIndexFactory(columns);
        Rows rows = new Rows(columns);
        Mockito.when(index.query(query)).thenReturn(rows);

        service.createIndex(name, columns);

        Assert.assertEquals(rows, service.query(name, query));
        Mockito.verify(index).query(query);
    }

    @Test
    public void shouldGetFromSpecifiedIndex() {
        String[] columns = {"columns"};
        String name = "name";
        String id = "id";
        Index index = prepareMockIndexFactory(columns);
        Rows rows = Mockito.mock(Rows.class);
        Mockito.when(index.getRow(id)).thenReturn(rows);

        service.createIndex(name, columns);

        Assert.assertEquals(rows, service.getRow(name, id));
        Mockito.verify(index).getRow(id);
    }

    @Test
    public void shouldNotReturnMoreThen100Results() {
        String[] headers = {"column"};

        Index index = prepareMockIndexFactory(headers);
        Rows justEnoughRows = Mockito.mock(Rows.class);
        Rows tooManyRows = Mockito.mock(Rows.class);
        Mockito.when(justEnoughRows.getRows()).thenReturn(new Row[IndexService.MAX_ROW_COUNT]);
        Mockito.when(tooManyRows.getRows()).thenReturn(new Row[IndexService.MAX_ROW_COUNT + 1]);
        Mockito.when(index.query("justEnough")).thenReturn(justEnoughRows);
        Mockito.when(index.query("tooMany")).thenReturn(tooManyRows);

        service.createIndex("index", headers);

        Assert.assertEquals(IndexService.MAX_ROW_COUNT, service.query("index", "justEnough").getRows().length);
        Assert.assertEquals(0, service.query("index", "tooMany").getRows().length);
    }

    private Index prepareMockIndexFactory(String[] columns) {
        Index index = Mockito.mock(Index.class);
        Mockito.when(factory.createNewIndex(columns)).thenReturn(index);
        return index;
    }

}
