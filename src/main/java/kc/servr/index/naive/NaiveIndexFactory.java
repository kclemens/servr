package kc.servr.index.naive;

import kc.servr.index.Index;
import kc.servr.index.IndexFactory;

/**
 * User: kclemens
 * Date: 6/25/12
 */
public class NaiveIndexFactory implements IndexFactory {

    @Override
    public Index createNewIndex(String[] headers) {
        return new NaiveIndex(headers);
    }
}
