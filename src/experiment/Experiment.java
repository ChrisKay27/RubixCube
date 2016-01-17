package experiment;

import cube.RubixCube;
import searches.*;
import searches.Search.Searches;
import searches.Searchable.EdgeChildPair;
import util.WTFException;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by chris_000 on 1/16/2016.
 */
public class Experiment {
    private final int giveUpAfterStates;
    private final int cubeSize;
    private final int depth;
    private final Searches searchType;
    private boolean runningExperiment;
    private Search search;
    private SearchDiagnostic searchDiagnostic;


    public Experiment(ExperimentParameters expParams) {
        this.cubeSize = expParams.getCubeSize();
        this.searchType = expParams.getSearch();
        giveUpAfterStates = expParams.getGiveUpAfterStates();
        depth = expParams.getDepth();

        init(expParams);
    }

    private void init(ExperimentParameters expParams){
        Consumer<Searchable> pathTracer = s -> {
            expParams.getPathTracer().accept(s);

            if(searchDiagnostic.getKnownUnexploredStates()+searchDiagnostic.getStatesExplored() > giveUpAfterStates){
                search.stop();

                Thread t = new Thread(()->{
                    init(expParams);
                    runExperiment();
                });
                t.setDaemon(true);
                t.start();
            }
        };
        switch(searchType){
            case ASTAR: search = new AStar(pathTracer); break;
            default:
            case BFS: search = new BreadthFirstSearch(pathTracer); break;
        }

        searchDiagnostic = search.getSearchDiagnostic();
    }

    public List<EdgeChildPair> runExperiment(){
        RubixCube goal = new RubixCube(cubeSize);
        RubixCube cube = new RubixCube(cubeSize);

        for (int i = 0; i < depth; i++) {
            List<EdgeChildPair> children = cube.getChildren();
            cube = (RubixCube) children.get((int)(Math.random()*children.size())).child;
        }


        runningExperiment = true;

        return search.findGoal(cube, goal);
    }

    public void stop() {
        if(!runningExperiment)
            throw new WTFException("Not running experiment and told to stop!");

        search.stop();
    }

    public SearchDiagnostic getSearchDiagnostic() {
        return searchDiagnostic;
    }
}
