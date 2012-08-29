package kc.servr.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import kc.servr.index.IndexFactory;
import kc.servr.index.IndexService;
import kc.servr.index.lucene.LuceneIndexFactory;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * User: kclemens
 * Date: 6/22/12
 */
public class GuiceServletListener extends GuiceServletContextListener {

    private final static Logger LOG = Logger.getLogger(GuiceServletListener.class.getSimpleName());

    @Override
    protected Injector getInjector() {
        Injector injector = Guice.createInjector(new JerseyServletModule() {
            @Override
            protected void configureServlets() {
                /* guice wiring */
                bind(IndexFactory.class).to(LuceneIndexFactory.class); // index used by servlet
                bind(HelloJerseyServlet.class); // servlet
                bind(IndexServlet.class); // servlet
                bind(RowsCsvReaderWriter.class); // jersey provider

                /* bind to path */
                serve("/index/*").with(GuiceContainer.class);
            }
        });

        try {
            /* bootstrap index */
            IndexService indexService = injector.getInstance(IndexService.class);
            boostrapWith(indexService, "/bootstrap/iso-639-3.csv", "iso 639-3 language codes");
            boostrapWith(indexService, "/bootstrap/iso-3166.csv", "iso 3166 country codes");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warning("Could not bootstrap index: " + e.getMessage());
        }

        return injector;
    }

    private void boostrapWith(IndexService indexService, String fileName, String indexName) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(fileName), "UTF-8"));
        indexService.createIndex(indexName, StringUtils.splitPreserveAllTokens(reader.readLine(), RowsCsvReaderWriter.SEPARATOR));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            indexService.index(indexName, StringUtils.splitPreserveAllTokens(line, RowsCsvReaderWriter.SEPARATOR));
        }
        LOG.info("done bootstrapping index " + indexName);
    }
}
