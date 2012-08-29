package kc.servr.index.lucene;

import kc.servr.index.Index;
import kc.servr.model.Row;
import kc.servr.model.Rows;
import org.apache.commons.lang.Validate;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * User: kclemens
 * Date: 8/29/12
 */
public class LuceneIndex implements Index {

    private static final Logger LOG = Logger.getLogger(LuceneIndex.class.getSimpleName());

    private final StandardAnalyzer analyzer;
    private final RAMDirectory index;
    private final String[] headers;

    public LuceneIndex(String[] headers) {
        LOG.info("creating index with " + headers.length + " headers: " + Arrays.toString(headers));
        this.headers = headers;
        this.analyzer = new StandardAnalyzer(Version.LUCENE_36);
        this.index = new RAMDirectory();
    }

    @Override
    public String addRow(String[] fields) {
        Validate.isTrue(fields.length == this.headers.length);
        try {
            IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(Version.LUCENE_36, analyzer));
            Document document = new Document();
            for (int i = 0; i < fields.length; i++) {
                document.add(new Field(headers[i], fields[i], Field.Store.YES, Field.Index.ANALYZED));
            }
            writer.addDocument(document);
            writer.close();
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Exception while adding row to index!", e);
        }
    }

    @Override
    public Rows query(String query) {
        try {
            BooleanQuery booleanQuery = new BooleanQuery();
            for (String header : headers) {
                Term prefixTerm = new Term(header, query);
                booleanQuery.add(new PrefixQuery(prefixTerm), BooleanClause.Occur.SHOULD);
            }

            IndexSearcher searcher = new IndexSearcher(IndexReader.open(index));
            TopDocs docs = searcher.search(booleanQuery, Integer.MAX_VALUE);

            Row[] rows = new Row[docs.scoreDocs.length];
            for (int i = 0; i < rows.length; i++) {
                Document doc = searcher.doc(docs.scoreDocs[i].doc);
                String[] fields = new String[headers.length];
                for (int j = 0; j < headers.length; j++) {
                    fields[j] = doc.get(headers[j]);
                }
                rows[i] = new Row(String.format("%d", docs.scoreDocs[i].doc), fields);
            }

            return new Rows(headers, rows);
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading from index!", e);
        }
    }

    @Override
    public Rows getRow(String id) {
        return null;
    }
}