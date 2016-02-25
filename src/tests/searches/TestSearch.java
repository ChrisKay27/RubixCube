package tests.searches;

import searches.AStar;
import searches.BreadthFirstSearch;
import searches.Search;
import searches.Searchable;
import searches.Searchable.EdgeChildPair;
import util.WTFException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Chris on 1/28/2016.
 */
public class TestSearch {

    static Searchable start = new Searchable() {
        @Override
        public List<EdgeChildPair> getChildren() {
            List<EdgeChildPair> kids = new ArrayList<>();
            for (int i = 0; i < 17; i++) {
                kids.add(new EdgeChildPair("some move",new NotPathToGoalNode(){
                    @Override
                    public int heuristicDistanceTo(Searchable s) {
                        return (int)(Math.random() * 1000) +  100;
                    }
                }));
                kids.add(new EdgeChildPair("Correct Move",pathToGoalNode));

            }
            return kids;
        }

        @Override
        public int heuristicDistanceTo(Searchable s) {
            if( s == goal )
                return 2;
            return (int)(Math.random() * 1000) +  100;
        }
    };

    static Searchable pathToGoalNode = new Searchable(){

        @Override
        public List<EdgeChildPair> getChildren() {
            List<EdgeChildPair> kids = new ArrayList<>();
            for (int i = 0; i < 17; i++) {
                kids.add(new EdgeChildPair("incorrect move",new NotPathToGoalNode() ));
            }

            kids.add(new EdgeChildPair("Correct Move",goal));
            return kids;
        }

        @Override
        public int heuristicDistanceTo(Searchable s) {
            if( s == goal )
                return 1;
            return (int)(Math.random() * 1000) +  100;
        }
    };

    static Searchable goal = new Searchable() {
        @Override
        public List<EdgeChildPair> getChildren() {
            throw new WTFException("Should not be trying to get the children of the goal node!");
        }

        @Override
        public int heuristicDistanceTo(Searchable s) {
            if( s == this )
                return 0;
            else return Integer.MAX_VALUE;
        }
    };

    static class NotPathToGoalNode implements Searchable{

        @Override
        public List<EdgeChildPair> getChildren() {
            List<EdgeChildPair> kids = new ArrayList<>();
            for (int i = 0; i < 17; i++) {
                kids.add(new EdgeChildPair("some move",new NotPathToGoalNode()));
            }

            return kids;
        }

        @Override
        public int heuristicDistanceTo(Searchable s) {
            return (int)(Math.random() * 1000) +  100;
        }
    }


    /**
     * Creates a predetermined searchable solution and runs it through the searches
     */
    public static boolean test() {

        if( !testAStar() )
            return false;

        if( !testBFS() )
            return false;

        return true;
    }


    public static boolean testAStar() {
        Search s = new AStar(null);

        List<EdgeChildPair> path = s.findGoal(start,goal);

        if( path.get(0).child != start )
            return false;
        if( path.get(1).child != pathToGoalNode )
            return false;
        if( path.get(2).child != goal )
            return false;

        return true;
    }

    public static boolean testBFS() {
        Search s = new BreadthFirstSearch(null);

        List<EdgeChildPair> path = s.findGoal(start,goal);

        if( path.get(0).child != start )
            return false;
        if( path.get(1).child != pathToGoalNode )
            return false;
        if( path.get(2).child != goal )
            return false;

        return true;
    }


}
