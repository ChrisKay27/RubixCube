package searches;

import searches.Searchable.EdgeChildPair;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by chris_000 on 1/5/2016.
 * Initial Memory ~ 33MB
 *
 * HashSet - 560000 rubes ~ 1GB, 970MB of cubes/560000 cubes = 1.7KB per cube
 * ArrayList - 56000 rubes ~ 300MB, 270MB/56000 cubes = 4.8KB per cube
 * HashMap - 560000 rubes ~ 1GB, 970MB of cubes/560000 cubes = 1.7KB per cube
 *
 */
public class AStar implements Search {

	private final Consumer<Searchable> pathTracer;
	private boolean stopFlag;

//    private List<Searchable> closedSet;
//    private List<Searchable> openSet;
    private Set<Searchable> closedSet;
    private Set<Searchable> openSet;
//    private HashMap<Searchable,Integer> closedSet;
//    private HashMap<Searchable,Integer>  openSet;


    private boolean currentlyExploring = false;

	public AStar(Consumer<Searchable> pathTracer) {
		this.pathTracer = pathTracer;
	}


	@Override
	public List<EdgeChildPair> findGoal(Searchable start, Searchable targetState) {
		if( currentlyExploring )
            throw new RuntimeException("Currently Exploring and trying to find goal again! Create a new search!");
        currentlyExploring = true;

        closedSet = new HashSet<>();
        openSet = new HashSet<>();
//        closedSet = new HashMap<>();
//        openSet = new HashMap<>();
//        closedSet = new ArrayList<>();
//        openSet = new ArrayList<>();

		Map<EdgeChildPair, EdgeChildPair> cameFrom = new HashMap<>();

		Map<Searchable,Integer> fScore = new HashMap<>();
		fScore.put(start,start.distanceFrom(targetState));

		Comparator<EdgeChildPair> c = (o1, o2) -> {
            long dist1 = fScore.get(o1.child);
            long dist2 = fScore.get(o2.child);
            if( dist1 < dist2 ) return -1;
            if( dist2 < dist1 ) return 1;
            return 0;
        };
		PriorityQueue<EdgeChildPair> priorityQueue = new PriorityQueue<>(c);
        openSet.add(start); //openSet.put(start,0);//
		priorityQueue.add(new EdgeChildPair(null,start));

		Map<Searchable,Integer> gScore = new HashMap<>();
		gScore.put(start,0);


		//System.out.println("start.distanceFrom(targetState)=" + start.distanceFrom(targetState));

		while(!priorityQueue.isEmpty()){
			if(stopFlag){
                currentlyExploring = false;
				return new ArrayList<>();
			}

			EdgeChildPair ecp = priorityQueue.poll();
			Searchable current = ecp.child;

			if( pathTracer != null )
				pathTracer.accept(current);

			long distanceFromGoal = current.distanceFrom(targetState);
			if ( distanceFromGoal == 0){
				if( current.equals(targetState)) {
					System.out.println("Goal state found!: " + current);
					return reconstruct_path(cameFrom, ecp);
				}
			}
			else {
                //System.out.println(distanceFromGoal);
            }
			openSet.remove(current);
            closedSet.add(current); //closedSet.put(current,0);//

			List<EdgeChildPair> children = current.getChildren();
			//System.out.println("Adding " + children.size() + " children states.");c
			for (EdgeChildPair neighbor : children ){
				if( closedSet.contains(neighbor.child) ) //closedSet.containsKey(neighbor.child) ) //
					continue;

				int tentative_g_score = gScore.get(current) + 1;

				if( !openSet.contains(neighbor.child) ) {//!openSet.containsKey(neighbor.child) ) { //
					openSet.add(neighbor.child); //openSet.put(neighbor.child,0); //
					gScore.put(neighbor.child, tentative_g_score);
					fScore.put(neighbor.child, tentative_g_score + neighbor.child.distanceFrom(targetState));
					priorityQueue.add(neighbor);
				}
				else if (tentative_g_score >= gScore.get(neighbor.child))
					continue;// This is not a better path.

				// This path is the best until now. Record it!
				cameFrom.put(neighbor,ecp);
				gScore.put(neighbor.child, tentative_g_score);
				fScore.put(neighbor.child, tentative_g_score + neighbor.child.distanceFrom(targetState));
			}
		}
        currentlyExploring = false;
		throw new IllegalArgumentException("Target state unreachable from starting state!");
	}

	private List<EdgeChildPair> reconstruct_path(Map<EdgeChildPair, EdgeChildPair> cameFrom, EdgeChildPair ecp) {

		List<EdgeChildPair> path = new ArrayList<>();

		while( ecp != null){
			path.add(ecp);
			ecp = cameFrom.get(ecp);
		}

		//path.forEach(System.out::println);

		Collections.reverse(path);
        currentlyExploring = false;
		//System.out.println("");
		return path;
	}

	@Override
	public List<SearchResult> findGoals(List<Searchable> ss, Searchable targetState) {

		return null;
	}



	public void stop() {
		stopFlag = true;
	}


    public SearchDiagnostic getSearchDiagnostic(){
        return new SearchDiagnostic() {
            @Override
            public long getStatesExplored() {
                return closedSet.size();
            }
            @Override
            public long getKnownUnexploredStates() {
                return openSet.size();
            }
        };
    }
}
