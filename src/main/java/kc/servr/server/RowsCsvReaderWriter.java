package kc.servr.server;

import com.google.inject.Singleton;
import kc.servr.model.Row;
import kc.servr.model.Rows;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: kclemens
 * Date: 7/6/12
 */
@Singleton
@Provider
@Produces("text/csv")
@Consumes("text/csv")
public class RowsCsvReaderWriter implements MessageBodyWriter<Rows>, MessageBodyReader<Rows> {

    private final static Logger LOG = Logger.getLogger(RowsCsvReaderWriter.class.getSimpleName());
    public static final String SEPARATOR = ",";

    public RowsCsvReaderWriter() {
        LOG.info("initialized.");
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Rows.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Rows rows, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Rows rows, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        LOG.info("serializing headers and " + rows.getRows().length + " rows");
        PrintWriter writer = new PrintWriter(entityStream);

        // serialize headers
        writer.print("id");
        writer.print(SEPARATOR);
        writer.println(StringUtils.join(rows.getHeaders(), SEPARATOR));

        // serialize content
        for (Row row : rows.getRows()) {
            writer.print(row.getId());
            writer.print(SEPARATOR);
            writer.println(StringUtils.join(row.getFields(), SEPARATOR));
        }

        writer.flush();
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isAssignableFrom(Rows.class);
    }

    @Override
    public Rows readFrom(Class<Rows> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(entityStream));
        String[] headers = StringUtils.splitPreserveAllTokens(reader.readLine(), SEPARATOR);

        List<Row> rows = new ArrayList<Row>();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            rows.add(new Row(StringUtils.splitPreserveAllTokens(line, SEPARATOR)));
        }

        return new Rows(headers, rows.toArray(new Row[0]));
    }
}
