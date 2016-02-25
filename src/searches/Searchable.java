package searches;

import java.util.List;

/**
 *
 * Created by Chris on 1/5/2016.
 */
public interface Searchable {
    List<EdgeChildPair> getChildren();
    int heuristicDistanceTo(Searchable s);


    /**
     * The edge represents the move from one node to another
     */
    class EdgeChildPair {
        public Object edge;
        public Searchable child;

        /**
         * @param edge the move from one node to another
         * @param child the child obtained by this move
         */
        public EdgeChildPair(Object edge, Searchable child) {
            this.edge = edge;
            this.child = child;
        }

        @Override
        public String toString() {
            return '(' + (child!=null?child.toString():null) + ',' + (edge!=null?edge.toString():null) + ')';
        }

        @Override
        public boolean equals(Object obj) {
            if( obj == this ) return true;
            if(!(obj instanceof EdgeChildPair)) return false;

            return child.equals(((EdgeChildPair)obj).child);
        }
    }



}
