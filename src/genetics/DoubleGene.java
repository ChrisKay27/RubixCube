package genetics;

/**
 * Created by chris_000 on 3/26/2016.
 */
public class DoubleGene implements Gene{
    private double value;

    public DoubleGene(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public Gene getMutatedCopy() {
        return null;
    }

    @Override
    public Gene getRandomCopy() {
        return null;
    }

    @Override
    public double distanceTo(Gene gene) {
        return Math.abs(value - ((DoubleGene)gene).value);
    }

    @Override
    public void mutate() {
        value += -1 + (Math.random()*2);
    }

    @Override
    public Gene getCopy() {
        return new DoubleGene(value);
    }

    @Override
    public void randomize() {
        value = -5 + (10*Math.random());
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == this ) return true;
        if( !(obj instanceof DoubleGene) ) return false;
        DoubleGene g = (DoubleGene) obj;

        return value == g.value;
    }
}
