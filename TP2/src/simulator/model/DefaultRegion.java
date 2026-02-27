package simulator.model;

public class DefaultRegion extends Region {
	final static double FOOD_EAT_RATE_HERBS = 60.0;
	final static double FOOD_SHORTAGE_TH_HERBS = 5.0;
	final static double FOOD_SHORTAGE_EXP_HERBS = 2.0;

	public DefaultRegion() {
		super();
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		int n = getAnimalsDiet(Diet.HERBIVORE).size();
		if (a.getDiet() == Diet.HERBIVORE) return FOOD_EAT_RATE_HERBS * Math.exp(-Math.max(0, n - FOOD_SHORTAGE_TH_HERBS) * FOOD_SHORTAGE_EXP_HERBS) * dt;
		return 0.0;
	}

}
