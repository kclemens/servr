package kc.servr.index;

/**
 * User: kclemens
 * Date: 6/25/12
 */
public interface IndexFactory {
    Index createNewIndex(String[] columns);
}
