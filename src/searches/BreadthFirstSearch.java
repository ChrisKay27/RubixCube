package searches;



import org.jetbrains.annotations.NotNull;
import searches.Searchable.EdgeChildPair;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by chris_000 on 1/5/2016.
 */
public class BreadthFirstSearch implements Search{


    private final Consumer<Searchable> pathTracer;
    private boolean stopFlag;
    private Queue<EdgeChildPair> queue;
    private Map<Searchable, Pair>  distancesAndParent;


    public BreadthFirstSearch(Consumer<Searchable> c) {
        pathTracer = c;
    }

    @Override
    public List<EdgeChildPair> findGoal(Searchable s, Searchable goal) {
        distancesAndParent = new HashMap<>(1000000);

        queue = new LinkedList<>();
        queue.add(new EdgeChildPair(null,s));
        distancesAndParent.put(s,new Pair(0,null));


        while (!queue.isEmpty()) {

            EdgeChildPair state = queue.poll();

            if( pathTracer != null )
                pathTracer.accept(state.child);


            if(state.child.equals(goal)){
                //System.out.println("Goal Found!");
                List<EdgeChildPair> path = new LinkedList<>();

                while( state != null ){
                    path.add(0,state);

                    state = distancesAndParent.get(state.child).parent;
                }

                return path;
            }

            Pair parentInfo = distancesAndParent.get(state.child);


            List<EdgeChildPair> children = state.child.getChildren();


            for (EdgeChildPair child : children) {

                if(!distancesAndParent.containsKey(child.child)) {

                    Pair info = new Pair(Integer.MAX_VALUE, null);
                    distancesAndParent.put(child.child, info );

                    if (info.distance == Integer.MAX_VALUE){
                        info.distance = parentInfo.distance + 1;
                        info.parent = state;
                        queue.add(child);
                    }
                }
            }
        }
        throw new IllegalArgumentException("Target state unreachable from starting state!");

    }


    @Override
    public List<List<EdgeChildPair>> findGoals(List<Searchable> ss, Searchable targetState) {
        List<List<EdgeChildPair>> results = new ArrayList<>();
        ss.forEach(searchable -> results.add(findGoal(searchable,targetState)));
        return results;
    }


    @Override
    public void stop() {
        stopFlag = true;
    }

    @Override
    public SearchDiagnostic getSearchDiagnostic() {
        return new SearchDiagnostic() {
            @Override
            public long getStatesExplored() {
                return distancesAndParent.size();
            }

            @Override
            public long getKnownUnexploredStates() {
                return queue.size();
            }
        };
    }


    public static class Pair {
        public int distance;
        public EdgeChildPair parent;

        public Pair(int dist, EdgeChildPair par) {
            this.distance = dist;
            this.parent = par;
        }
    }


}
