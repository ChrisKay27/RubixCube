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


    public BreadthFirstSearch(@NotNull Consumer<Searchable> c) {
        pathTracer = c;
    }

    @Override
    public List<EdgeChildPair> findGoal(Searchable s, Searchable targetState) {
        distancesAndParent = new HashMap<>(1000000);

        queue = new LinkedList<>();
        queue.add(new EdgeChildPair(null,s));
        distancesAndParent.put(s,new Pair(0,null));

        long lastTime = System.currentTimeMillis();
        int lookedAtStatesCounter = 0;
        long dt;
        long cycletime = System.currentTimeMillis();
        int displayTimes = 10000;
        while (!queue.isEmpty()) {
            if( displayTimes == 0)
                displayTimes = 10000;
            displayTimes--;
            if( displayTimes == 0 ) {
                cycletime = System.currentTimeMillis() - cycletime;
                //System.out.println("CycleTime:" + cycletime + "ms");
            }
            cycletime = System.currentTimeMillis();

            EdgeChildPair state = queue.poll();
            // dt = System.currentTimeMillis() - t;
            //System.out.println("Took " + dt + "ms to check poll the queue, queue size=" + queue.size());

            pathTracer.accept(state.child);

            lookedAtStatesCounter++;
            if(lookedAtStatesCounter%10000==0) {
                dt = System.currentTimeMillis() - lastTime;
                lastTime = System.currentTimeMillis();
                //System.out.println("Looked at " + lookedAtStatesCounter + " States. Took " + dt + "ms since last check. States in HashMap:" + distancesAndParent.size());
            }
            if(state.child.isGoal()){
                System.out.println("Goal Found!");
                List<EdgeChildPair> path = new LinkedList<>();

                while( state != null ){
                    path.add(0,state);

                    state = distancesAndParent.get(state.child).parent;
                }

                return path;
            }

            Pair parentInfo = distancesAndParent.get(state.child);

           // long getChildrenTime = System.currentTimeMillis();
            List<EdgeChildPair> children = state.child.getChildren();
//            dt = System.currentTimeMillis() - getChildrenTime;
//            if( displayTimes == 0)
//                System.out.println("Took " + dt + "ms to get this states children.");
           // System.out.println("Adding " + children.size() + " more children.");


            int newStateCounter = 0, sameStateCounter = 0;
            for (EdgeChildPair child : children) {

                long lastContainsKeyTime = System.currentTimeMillis();
                if(!distancesAndParent.containsKey(child.child)) {

                   // dt = System.currentTimeMillis() - lastContainsKeyTime;
//                    if( displayTimes == 0)
//                        System.out.println("Took " + dt + "ms to check distancesAndParent.containsKey(child.child) (not found");
                    newStateCounter++;
                   // if(newStateCounter%100==0)
                   //     System.out.println("Hit " + newStateCounter + " States.");

                    Pair info = new Pair(Integer.MAX_VALUE, null);
                    distancesAndParent.put(child.child, info );

                    if (info.distance == Integer.MAX_VALUE){
                        info.distance = parentInfo.distance + 1;
                        info.parent = state;
                        queue.add(child);
                    }
                }
                else{
//                    dt = System.currentTimeMillis() - lastContainsKeyTime;
//                    if( displayTimes == 0)
//                        System.out.println("Took " + dt + "ms to check distancesAndParent.containsKey(child.child) (found)");
                    sameStateCounter++;
                   //if(sameStateCounter%100==0)
                   //     System.out.println("Hit the same state " + sameStateCounter + " times.");
                    //System.out.println("Arrived at the same state, ignoring: " + child.child.hashCode());
//                    Pair info = distancesAndParent.get(child.child);
//                    if (info.distance == Integer.MAX_VALUE){
//                        info.distance = parentInfo.distance + 1;
//                        info.parent = state;
//                        queue.add(child);  -2147483648
//                    }
                }
            }
        }
        throw new IllegalArgumentException("Target state unreachable from starting state!");

    }



    @Override
    public List<SearchResult> findGoals(List<Searchable> ss, Searchable targetState) {
        return null;
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
