package kc.servr.index.lucene;

import kc.servr.index.Index;
import kc.servr.index.IndexFactory;

/**
 * User: kclemens
 * Date: 6/25/12
 */
public class LuceneIndexFactory implements IndexFactory {

    @Override
    public Index createNewIndex(String[] headers) {
        return new LuceneIndex(headers);
    }
}
