package kc.servr.server;

import com.google.inject.Inject;
import kc.servr.index.Index;
import kc.servr.index.IndexService;
import kc.servr.model.Row;
import kc.servr.model.Rows;

import javax.ws.rs.*;
import java.io.IOException;

/**
 * User: kclemens
 * Date: 6/22/12
 * <p/>
 * GET / - list of indexes [format? csv index name, headers?]
 * GET /[index-name] - all rows
 * GET /[index-name]?q=[query] - matching rows
 * GET /[index-name]/[row-id] - single row
 * <p/>
 * PUT /[index-name]/ - creates new index from CSV lines in payload
 * <p/>
 * POST /[index-name]/ - adds new row to index from CSV line in payload
 * <p/>
 * DELETE /[index-name]/ - deletes the index
 * DELETE /[index-name]/[row-id] - deletes the row from the index
 */

@Path("/")
public class IndexServlet {

    private final IndexService service;

    @Inject
    public IndexServlet(IndexService service) {
        this.service = service;
    }

    @GET
    @Produces("text/csv")
    public String listAllIndexesCSV() {
        StringBuilder builder = new StringBuilder("index");
        for (String index : service.getIndexes()) {
            builder.append("\n").append(index);
        }
        return builder.toString();
    }

    @GET
    @Path("/{indexName}")
    @Produces("text/csv")
    public Rows queryCSV(@QueryParam("query") @DefaultValue("") String query, @PathParam("indexName") String indexName) {
        return service.query(indexName, query);
    }

    @PUT
    @Consumes("text/csv")
    @Path("/{indexName}")
    public void createIndex(@PathParam("indexName") String indexName, Rows rows) throws IOException {
        Index index = service.createIndex(indexName, rows.getHeaders());
        for (Row row : rows.getRows()) {
            index.addRow(row.getFields());
        }
    }

    @DELETE
    @Path("/{indexName}")
    public void deleteIndex(@PathParam("indexName") String indexName) {
        service.deleteIndex(indexName);
    }
}
