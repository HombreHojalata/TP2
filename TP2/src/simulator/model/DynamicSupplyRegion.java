package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {
	final static double FOOD_EAT_RATE_HERBS = 60.0;
	final static double FOOD_SHORTAGE_TH_HERBS = 5.0;
	final static double FOOD_SHORTAGE_EXP_HERBS = 2.0;
	final static double INIT_FOOD = 100.0;
	final static double FACTOR = 2.0;
	
	private double food;
	private double factor;

	public DynamicSupplyRegion(double food, double factor) {
		super();
		if (food <= 0) throw new IllegalArgumentException("Initial food can't be negative");
	    if (factor < 0) throw new IllegalArgumentException("Growing (food) factpr can't be negative"); 
	    this.food = food;
	    this.factor = factor;
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		if (a.getDiet() != Diet.HERBIVORE) return 0;
		int n = 0;
		for (Animal an : getAnimals()) {
			if (an.getDiet() == Diet.HERBIVORE) n++;
		}
		double amount = Math.min(food, FOOD_EAT_RATE_HERBS * Math.exp(-Math.max(0, n - FOOD_SHORTAGE_TH_HERBS) * FOOD_SHORTAGE_EXP_HERBS));
		food -= amount;
		return amount;
	}
	
	@Override
	public void update(double dt) {
		if (Utils.RAND.nextDouble() < 0.5) food += factor*dt; // TODO: Igual hay que hacer funcionamiento de update normal
	}
}
