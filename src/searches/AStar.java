package searches;

import searches.BreadthFirstSearch.Pair;

import java.util.*;

/**
 * Created by chris_000 on 1/5/2016.
 */
public class AStar implements Search {

	@Override
	public List<Searchable.EdgeChildPair> findGoal(Searchable s, Searchable targetState) {
		Map<Searchable, Pair> distancesAndParent = new HashMap<>(1000000);

		Set<Searchable> closedSet = new HashSet<>();
		Set<Searchable> openSet = new HashSet<>();

		Comparator<Searchable> c = (o1, o2) -> {
            long dist1 = o1.distanceFrom(targetState);
            long dist2 = o2.distanceFrom(targetState);
            if( dist1 < dist2 ) return -1;
            if( dist2 < dist1 ) return 1;
            return 0;
        };
		PriorityQueue<Searchable> priorityQueue = new PriorityQueue<>(c);
		openSet.add(s);
		priorityQueue.add(s);

		Map<Searchable,Long> gScore = new HashMap<>();
		gScore.put(s,s.distanceFrom(targetState));

		while(!priorityQueue.isEmpty()){




		}


		Queue<Searchable.EdgeChildPair> queue = new LinkedList<>();
		queue.add(new Searchable.EdgeChildPair(null,s));
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
				System.out.println("CycleTime:" + cycletime + "ms");
			}
			cycletime = System.currentTimeMillis();

			Searchable.EdgeChildPair state = queue.poll();
			// dt = System.currentTimeMillis() - t;
			//System.out.println("Took " + dt + "ms to check poll the queue, queue size=" + queue.size());


			lookedAtStatesCounter++;
			if(lookedAtStatesCounter%10000==0) {
				dt = System.currentTimeMillis() - lastTime;
				lastTime = System.currentTimeMillis();
				System.out.println("Looked at " + lookedAtStatesCounter + " States. Took " + dt + "ms since last check. States in HashMap:" + distancesAndParent.size());
			}
			if(state.child.isGoal()){
				System.out.println("Goal Found!");
				List<Searchable.EdgeChildPair> path = new LinkedList<>();

				while( state != null ){
					path.add(0,state);

					state = distancesAndParent.get(state.child).parent;
				}

				return path;
			}

			Pair parentInfo = distancesAndParent.get(state.child);

			// long getChildrenTime = System.currentTimeMillis();
			List<Searchable.EdgeChildPair> children = state.child.getChildren();
//            dt = System.currentTimeMillis() - getChildrenTime;
//            if( displayTimes == 0)
//                System.out.println("Took " + dt + "ms to get this states children.");
			// System.out.println("Adding " + children.size() + " more children.");


			int newStateCounter = 0, sameStateCounter = 0;
			for (Searchable.EdgeChildPair child : children) {

				long lastContainsKeyTime = System.currentTimeMillis();
				if(!distancesAndParent.containsKey(child.child)) {

					// dt = System.currentTimeMillis() - lastContainsKeyTime;
//                    if( displayTimes == 0)
//                        System.out.println("Took " + dt + "ms to check distancesAndParent.containsKey(child.child) (not found");
					newStateCounter++;
					if(newStateCounter%100==0)
						System.out.println("Hit " + newStateCounter + " States.");

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
					if(sameStateCounter%100==0)
						System.out.println("Hit the same state " + sameStateCounter + " times.");
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


}
