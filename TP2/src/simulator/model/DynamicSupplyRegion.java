package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {
	private double food;
	private double factor; //Factor de crecimiento

	public DynamicSupplyRegion(double food, double factor) {
		super();
		this.food = food;
		this.factor = factor;
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		int n = getAnimalsDiet(Diet.HERBIVORE).size();
		if (a.getDiet() == Diet.HERBIVORE) {
			double d = Math.min(food, 60.0 * Math.exp(-Math.max(0, n - 5.0) * 2.0) * dt);
			food -= d;
			return d;
		}
		return 0.0;
	}
	
	@Override
	public void update(double dt) {
		if (Utils.RAND.nextDouble() < 0.5) food += factor*dt; // TODO: Igual hay que hacer funcionamiento de update normal
	}
}
