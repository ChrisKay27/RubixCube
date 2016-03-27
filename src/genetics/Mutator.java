package genetics;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Chris on 3/26/2016.
 */
public class Mutator {


    public static List<Genome> getMutations(int numFreaks, double percentOfGenome, List<Genome> elites){
        List<Genome> mutations = new ArrayList<>();

        double numMutated = numFreaks;
        while(mutations.size() < numMutated ){
            Genome freak = elites.get((int)(Math.random()*elites.size())).copy();
            freak.mutate(percentOfGenome);
            mutations.add(freak);
        }

        return mutations;
    }

}
