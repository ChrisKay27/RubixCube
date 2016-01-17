package experiment;

import searches.Search;
import searches.Searchable;

/**
 * Created by chris_000 on 1/16/2016.
 */
public class Tuple {

    public final Searchable state;
    public final Object edge;

    public Tuple(Searchable state, Object edge) {
        this.state = state;
        this.edge = edge;
    }

    @Override
    public String toString() {
        return "(" + state +"," + edge + ')';
    }
}
