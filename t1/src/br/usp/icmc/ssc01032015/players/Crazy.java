package br.usp.icmc.ssc01032015.players;
import br.usp.icmc.ssc01032015.Competitor;
import java.util.Random;

public class Crazy extends Basic {
    private static Random rnd;
    private double cash;

    static {
        rnd = new Random();
    }

	public double declareDonationTo(Competitor c) {
        return rnd.nextDouble() * 10;
    }
}
