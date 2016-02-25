package experiment;

import cube.RubixCube;
import searches.Search.Searches;
import searches.Searchable;

import java.util.function.Consumer;

/**
 * Created by chris_000 on 1/16/2016.
 */
public class ExperimentParameters {

    private Searches search;
    private int cubeSize;
    private int giveUpAfterStates;
    private int depth;
    private Consumer<Searchable> pathTracer;
    private TooManyStatesListener tooManyStatesListener;
    private RubixCube startState;

    public ExperimentParameters(Searches search, int cubeSize, int giveUpAfterStates, int depth) {
        this.search = search;
        this.cubeSize = cubeSize;
        this.giveUpAfterStates = giveUpAfterStates;

        this.depth = depth;
    }

    public int getCubeSize() {
        return cubeSize;
    }

    public int getGiveUpAfterStates() {
        return giveUpAfterStates;
    }

    public Searches getSearch() {
        return search;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setPathTracer(Consumer<Searchable> pathTracer) {
        this.pathTracer = pathTracer;
    }

    public Consumer<Searchable> getPathTracer() {
        return pathTracer;
    }

    public TooManyStatesListener getTooManyStatesListener() {
        return tooManyStatesListener;
    }

    public void setTooManyStatesListener(TooManyStatesListener tooManyStatesListener) {
        this.tooManyStatesListener = tooManyStatesListener;
    }

    public void setStartState(RubixCube startState) {
        this.startState = startState;
    }

    public RubixCube getStartState() {
        return startState;
    }
}

