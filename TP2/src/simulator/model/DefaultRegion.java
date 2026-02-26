package simulator.model;

public class DefaultRegion extends Region {

	public DefaultRegion() {
		super();
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		int n = getAnimalsDiet(Diet.HERBIVORE).size();
		if (a.getDiet() == Diet.HERBIVORE) return 60.0 * Math.exp(-Math.max(0, n - 5.0) * 2.0) * dt;
		return 0.0;
	}

}
