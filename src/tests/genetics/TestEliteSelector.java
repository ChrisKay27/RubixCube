package tests.genetics;

import genetics.DoubleGene;
import genetics.EliteSelector;
import genetics.Genome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created by Chris on 3/26/2016.
 */
public class TestEliteSelector {

    public static void main(String[] args) {
        testEliteSelection();
    }

    public static boolean testEliteSelection() {

        System.out.println("\nTesting Elite Selector");
        boolean testSelectFirstElite = testSelectFirstElite();
        System.out.println("Select First Elite : " + (testSelectFirstElite?"Passed":"Fail"));
        boolean testSelectElites = testSelectElites();
        System.out.println("Select Select Elites : " + (testSelectElites?"Passed":"Fail"));

//        for (int i = 0; i < 100000; i++) {
//            if( !testSelectFirstElite() || !testSelectElites()) {
//                System.out.println("Fail!! at " + i);
//                System.exit(-666);
//            }
//        }

        return testSelectFirstElite && testSelectElites;
    }


    public static boolean testSelectElites(){
        boolean passed = true;

        List<Genome> genomes = TestGeneticAlgorithm.getGenomes(10);

        int[] selectedCount = new int[10];

        for (int i = 0; i < 10000; i++) {
            List<Genome> elites = EliteSelector.selectElites(0.2, 0.667, genomes);

            passed &= elites.size() == 2; //ensure correct number of elites have been returned

            for (int j = 0; j < elites.size(); j++)
                selectedCount[(int) elites.get(j).getFitness()]++;
        }

        passed = selectedCount[0] > 6300 && selectedCount[0] < 7200; //should be 6670~
        passed &= selectedCount[9] > 6300 && selectedCount[9] < 7200; //should be 6670~

//        System.out.println(selectedCount[0]);
//        System.out.println(selectedCount[9]);
//        for (int i = 0; i < 10; i++)
//            System.out.println(selectedCount[i]);


        return passed;
    }




    public static boolean testSelectFirstElite(){

        boolean passed;

        List<Genome> genomes = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Genome g = new Genome();
            g.setFitness(i);
            genomes.add(g);
        }

        Collections.sort(genomes,(ge1, ge2) -> {
            if( ge1.getFitness() < ge2.getFitness() ) return 1;
            if( ge1.getFitness() > ge2.getFitness() ) return -1;
            return 0;
        });


        int[] selectedCount = new int[10];

        for (int i = 0; i < 10000; i++)
            selectedCount[(int) EliteSelector.getElite(0.667,genomes).getFitness()]++;


        passed = selectedCount[9] > 6300 && selectedCount[9] < 7200; //should be 6670
        passed &= selectedCount[8] > 1700 && selectedCount[8] < 2500; //should be 2220

//        System.out.println(selectedCount[9]);
//        System.out.println(selectedCount[8]);
//        for (int i = 0; i < 10; i++)
//            System.out.println(selectedCount[i]);

        return passed;
    }
}
