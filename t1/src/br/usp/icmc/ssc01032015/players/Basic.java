package br.usp.icmc.ssc01032015.players;
import br.usp.icmc.ssc01032015.Competitor;

public class Basic implements Competitor {
    private double cash;

    public Basic() {
        cash = 0;
    }

	public double declareDonationTo(Competitor c) {
        return 10;
    }

	public void informDonationFrom(Competitor c, double donation) {
        return;
    }

	public void addCash(double amount) {
        cash += amount;
    }

	public double getTotalCash() {
        return cash;
    }
}
