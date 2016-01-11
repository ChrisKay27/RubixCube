package searches;

import java.util.List;

/**
 * Created by chris_000 on 1/5/2016.
 */
public interface Searchable {
    List<EdgeChildPair> getChildren();

    boolean isGoal();

    class EdgeChildPair {
        public Object edge;
        public Searchable child;

        public EdgeChildPair(Object edge, Searchable child) {
            this.edge = edge;
            this.child = child;
        }
    }

    long distanceFrom(Searchable s);

}
