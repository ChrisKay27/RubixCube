package experiment;

import cube.RubixCube;
import searches.*;
import searches.Search.Searches;
import searches.Searchable.EdgeChildPair;
import util.WTFException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This experiment knows about Rubix Cubes, I have chosen not to decouple this class from the rubix cube
 * Created by Chris on 1/16/2016.
 */
public class RubixCubeExperiment {
    private final int giveUpAfterStates;
    private final int cubeSize;
    private final int depth;
    private final Searches searchType;
    private boolean runningExperiment;
    private Search search;

    private RubixCube startState;


    public RubixCubeExperiment(ExperimentParameters expParams) {
        this.cubeSize = expParams.getCubeSize();
        this.searchType = expParams.getSearch();
        giveUpAfterStates = expParams.getGiveUpAfterStates();
        depth = expParams.getDepth();
        startState = expParams.getStartState();

        init(expParams);
    }

    private void init(ExperimentParameters expParams){
        Consumer<Searchable> pathTracer = s -> {
            expParams.getPathTracer().accept(s);

            if(searchDiagnostic.getKnownUnexploredStates()+searchDiagnostic.getStatesExplored() > giveUpAfterStates){
                search.stop();

                System.out.println("Too many states encountered, stopping and running new exp");

                expParams.getTooManyStatesListener().tooManyStatesEncountered();
            }
        };
        switch(searchType){
            case ASTAR: search = new AStar(pathTracer); break;
            default:
            case BFS: search = new BreadthFirstSearch(pathTracer); break;
        }

        searchDiagnostic = getSearchDiagnostic();
    }



    public List<Tuple> runExperiment(){
        if( runningExperiment )
            throw new WTFException("Already running experiment and told to run again!");

        RubixCube goal = new RubixCube(cubeSize);

        RubixCube cube = new RubixCube(cubeSize);

        if( startState != null )
            cube = new RubixCube(startState);
        else {
            //Randomize the cube
            for (int i = 0; i < depth; i++) {
                List<EdgeChildPair> children = cube.getChildren();
                cube = (RubixCube) children.get((int) (Math.random() * children.size())).child;
            }
        }

        runningExperiment = true;
        List<EdgeChildPair> results = search.findGoal(cube, goal);
        runningExperiment = false;


        //If we have pressed the stop button then the search will return an empty list.
        if(results.isEmpty())
            return null;


        List<Tuple> resultTuples = new ArrayList<>();
        RubixCube nextState = (RubixCube) results.get(0).child;
        results.remove(0);

        for (EdgeChildPair ecp : results){
            Tuple t = new Tuple(nextState,ecp.edge);
            //System.out.println(t);
            resultTuples.add(t);
            nextState = (RubixCube) ecp.child;
        }

        Tuple t = new Tuple(nextState,null);
        resultTuples.add(t);




        return resultTuples;
    }


    public void stop() {
        if(!runningExperiment)
            throw new WTFException("Not running experiment and told to stop!");
        runningExperiment = false;
        search.stop();
    }



    private SearchDiagnostic searchDiagnostic;
    public SearchDiagnostic getSearchDiagnostic() {
        if( searchDiagnostic == null )
            searchDiagnostic = new SearchDiagnostic() {

                @Override
                public long getStatesExplored() {
                    return search.getSearchDiagnostic().getStatesExplored();
                }

                @Override
                public long getKnownUnexploredStates() {
                    return search.getSearchDiagnostic().getKnownUnexploredStates();
                }
            };
        return searchDiagnostic;
    }
}
