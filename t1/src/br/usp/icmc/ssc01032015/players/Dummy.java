package br.usp.icmc.ssc01032015.players;
import java.util.Random;
import br.usp.icmc.ssc01032015.Competitor;

public class Dummy extends Basic {
    private static Random rnd;
    private double myAmount;

    static {
        rnd = new Random();
    }

    public Dummy() {
        myAmount = rnd.nextDouble() * 10;
    }

	public double declareDonationTo(Competitor c) {
        return myAmount;
    }
}
