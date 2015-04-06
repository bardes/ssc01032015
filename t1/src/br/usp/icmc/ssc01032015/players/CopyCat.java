package br.usp.icmc.ssc01032015.players;
import br.usp.icmc.ssc01032015.Competitor;
import java.util.HashMap;

public class CopyCat extends Basic { 
    private static double initialDonation; 
    private HashMap<Competitor, Double> lastDonation;

    public CopyCat() {
        initialDonation = 10;
        lastDonation = new HashMap<Competitor, Double>();
    }

    public CopyCat(double init) {
        initialDonation = init >= 0 && init <= 10 ? init : 10;
        lastDonation = new HashMap<Competitor, Double>();
    }

	public double declareDonationTo(Competitor c) {
        Double val = lastDonation.get(c);
        return val != null ? val : initialDonation;
    }

	public void informDonationFrom(Competitor c, double donation) {
        lastDonation.put(c, donation);
    }
}
