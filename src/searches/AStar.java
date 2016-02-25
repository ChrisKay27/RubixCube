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

    private Set<Searchable> closedSet = new HashSet<>();
    private Set<Searchable> openSet = new HashSet<>();


    private boolean currentlyExploring = false;

	public AStar(Consumer<Searchable> pathTracer) {
		this.pathTracer = pathTracer;
	}


	@Override
	public List<EdgeChildPair> findGoal(Searchable start, Searchable targetState) {
		if( currentlyExploring )
            throw new RuntimeException("Currently Exploring and trying to find goal again! Create a new search!");
        currentlyExploring = true;

        closedSet = new HashSet<>(10000000);
        openSet = new HashSet<>(10000000);

		//Path tracing hash map
		Map<EdgeChildPair, EdgeChildPair> cameFrom = new HashMap<>(10000000);

		//Map to store f score
		Map<Searchable,Integer> fScore = new HashMap<>(10000000);
		fScore.put(start,start.heuristicDistanceTo(targetState));

		//Priority Queue initialization
		Comparator<EdgeChildPair> c = (o1, o2) -> {
            long dist1 = fScore.get(o1.child);
            long dist2 = fScore.get(o2.child);
            if( dist1 < dist2 ) return -1;
            if( dist2 < dist1 ) return 1;
            return 0;
        };
		PriorityQueue<EdgeChildPair> priorityQueue = new PriorityQueue<>(c);
        openSet.add(start);
		priorityQueue.add(new EdgeChildPair(null,start));

		//Map to stop g score
		Map<Searchable,Integer> gScore = new HashMap<>();
		gScore.put(start,0);


		//System.out.println("start.heuristicDistanceTo(targetState)=" + start.heuristicDistanceTo(targetState));


		while(!priorityQueue.isEmpty()){
			if(stopFlag){
                currentlyExploring = false;
				return new ArrayList<>();
			}

			EdgeChildPair ecp = priorityQueue.poll();
			Searchable current = ecp.child;

			if( pathTracer != null )
				pathTracer.accept(current);

			long distanceFromGoal = current.heuristicDistanceTo(targetState);
			if ( distanceFromGoal == 0){
				if( current.equals(targetState)) {
					//System.out.println("Goal state found!: " + current);
					return reconstruct_path(cameFrom, ecp);
				}
			}

			openSet.remove(current);
            closedSet.add(current);

			List<EdgeChildPair> children = current.getChildren();

			int childrens_g_score = gScore.get(current) + 1;
			for (EdgeChildPair neighbor : children ){

                final Searchable child = neighbor.child;
                int newFCost = childrens_g_score + child.heuristicDistanceTo(targetState);

                if( closedSet.contains(child) ) {

                    if( fScore.get(child) > newFCost ){
                        fScore.put(child,newFCost);
                        closedSet.remove(child);
                        openSet.add(child);
                        priorityQueue.add(neighbor);
                    }
                    continue;
                }

				if( !openSet.contains(child) ) {
					openSet.add(child);
					gScore.put(child, childrens_g_score);
					fScore.put(child, newFCost);
					priorityQueue.add(neighbor);
				}
				else if (childrens_g_score >= gScore.get(child))
					continue;// This is not a better path.

				// This path is the best until now. Record it!
				cameFrom.put(neighbor,ecp);
				gScore.put(child, childrens_g_score);
				fScore.put(child, newFCost);
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
		return path;
	}

	@Override
	public List<List<EdgeChildPair>> findGoals(List<Searchable> ss, Searchable targetState) {
        List<List<EdgeChildPair>> results = new ArrayList<>();
        ss.forEach(searchable -> results.add(findGoal(searchable,targetState)));
		return results;
	}



	public void stop() {
		stopFlag = true;
	}



	private SearchDiagnostic searchDiagnostic;
    public SearchDiagnostic getSearchDiagnostic(){
		if( searchDiagnostic == null )
			searchDiagnostic = new SearchDiagnostic() {
				@Override
				public long getStatesExplored() {
					return closedSet.size();
				}
				@Override
				public long getKnownUnexploredStates() {
					return openSet.size();
				}
			};
        return searchDiagnostic;
    }


}
