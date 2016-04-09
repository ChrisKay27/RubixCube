package genetics;

import util.WTFException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Chris on 3/26/2016.
 */
public class CrossOver {


    public static List<Genome> forceMating(int numKids, List<Genome> elites){
        if( elites.size() < 2 )
            throw new WTFException("Asexual production not allowed (elites.size() must be >= 2)");

        List<Genome> kids = new ArrayList<>();


        while( kids.size() < numKids ){

            Genome dad = elites.get((int)(Math.random()*elites.size()));
            Genome otherDad = elites.get((int)(Math.random()*elites.size()));
            if( dad == otherDad ) continue;

            List<Gene> dadGenes = new ArrayList<>(dad.getGenes());
            List<Gene> otherDadGenes = new ArrayList<>(otherDad.getGenes());

            //Make sure that at least one gene is swapped
            boolean swappedSomething = false;
            while(!swappedSomething){
                for (int i = 0; i < dadGenes.size(); i++) {
                    if( Math.random() < 0.5 ){
                        Gene g = dadGenes.get(i);
                        dadGenes.set(i,otherDadGenes.get(i));
                        otherDadGenes.set(i,g);
                        swappedSomething = true;
                    }
                }
            }

            Genome nonBinaryGenderIdentifingChild = new Genome(dadGenes);
            Genome sister = new Genome(otherDadGenes);

            nonBinaryGenderIdentifingChild.setUserData(dad.getUserData());
            sister.setUserData(dad.getUserData());

            kids.add(nonBinaryGenderIdentifingChild);
            kids.add(sister);
        }

        //If there's too many then drop the last kid....
        if( kids.size() != numKids )
            kids.remove(kids.size()-1);

        return kids;
    }
}
