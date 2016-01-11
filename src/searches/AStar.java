package searches;

import searches.BreadthFirstSearch.Pair;
import searches.Searchable.EdgeChildPair;

import java.util.*;

/**
 * Created by chris_000 on 1/5/2016.
 */
public class AStar implements Search {

	@Override
	public List<EdgeChildPair> findGoal(Searchable start, Searchable targetState) {

		Set<Searchable> closedSet = new HashSet<>();
		Set<Searchable> openSet = new HashSet<>();
		Map<EdgeChildPair, EdgeChildPair> cameFrom = new HashMap<>();

		Map<Searchable,Long> fScore = new HashMap<>();

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

		Map<Searchable,Long> gScore = new HashMap<>();
		gScore.put(start,0L);

		fScore.put(start,start.distanceFrom(targetState));

		while(!priorityQueue.isEmpty()){
			EdgeChildPair ecp = priorityQueue.poll();
			Searchable current = ecp.child;

			if (current.distanceFrom(targetState) == 0){
				return reconstruct_path(cameFrom, ecp);
			}

			openSet.remove(current);
			closedSet.add(current);

			List<EdgeChildPair> children = current.getChildren();

			for (EdgeChildPair neighbor : children ){
				if( closedSet.contains(neighbor.child) )
					continue;

				long tentative_g_score = gScore.get(current) + 1;

				if( !openSet.contains(neighbor.child) ) {
					openSet.add(neighbor.child);
				}
				else if (tentative_g_score >= gScore.get(neighbor.child))
					continue;// This is not a better path.

				// This path is the best until now. Record it!
				cameFrom.put(neighbor,ecp);
				gScore.put(neighbor.child, tentative_g_score);
				fScore.put(neighbor.child, tentative_g_score + neighbor.child.distanceFrom(targetState));
			}
		}
		throw new IllegalArgumentException("Target state unreachable from starting state!");
	}

	private List<EdgeChildPair> reconstruct_path(Map<EdgeChildPair, EdgeChildPair> cameFrom, EdgeChildPair ecp) {
		List<EdgeChildPair> path = new ArrayList<>();
		while(true){
			EdgeChildPair next = cameFrom.get(ecp);
			ecp = next;
			if( next == null ){
				break;
			}
			path.add(next);
		}
		return path;
	}

	@Override
	public List<SearchResult> findGoals(List<Searchable> ss, Searchable targetState) {
		return null;
	}


}
