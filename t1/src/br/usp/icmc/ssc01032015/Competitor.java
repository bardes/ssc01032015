package br.usp.icmc.ssc01032015;

public interface Competitor {
	// Indicates how much to donate to Competitor c
	double declareDonationTo(Competitor c);

	// Indicates how much was donated from Competitor c
	void informDonationFrom(Competitor c, double donation);

	// Adds the amount of cash
	void addCash(double amount);

	// Gets the amount of cash this competitor has
	double getTotalCash();
}
