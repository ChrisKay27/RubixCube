package genetics;

/**
 * Created by chris_000 on 3/26/2016.
 */
public interface Gene {
    Gene getMutatedCopy();
    Gene getRandomCopy();

    double distanceTo(Gene gene);

    void mutate();

    Gene getCopy();
}
