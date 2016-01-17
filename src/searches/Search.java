package searches;

import java.util.List;

/**
 * Created by chris_000 on 1/5/2016.
 */
public interface Search {
	enum Searches{
		BFS,ASTAR
	}

	List<Searchable.EdgeChildPair> findGoal(Searchable s, Searchable targetState);
	List<SearchResult> findGoals(List<Searchable> ss, Searchable targetState);

	void stop();

	SearchDiagnostic getSearchDiagnostic();
}
