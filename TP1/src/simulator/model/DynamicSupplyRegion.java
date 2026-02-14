package simulator.model;

import java.util.concurrent.ThreadLocalRandom;

public class DynamicSupplyRegion extends Region {
	private double food;
	private double factor; //Factor de crecimiento

	public DynamicSupplyRegion(double food, double factor) {
		super();
		this.food = food;
		this.factor = factor;
	}

	@Override
	public double getFood(Animal a, double dt) {
		int n = getAnimalsDiet(Diet.HERBIVORE).size();
		if (a.getDiet() == Diet.CARNIVORE) return 0.0;
		else if(a.getDiet() == Diet.HERBIVORE) {
			double d = 60.0*Math.exp(-Math.max(0,n-5.0)*2.0)*dt;
			food =- d;
			return d;
		}
		else return 0.0;
	}
	@Override
	public void update(double dt) {
		if (ThreadLocalRandom.current().nextDouble() < 0.5) food =+ factor*dt;
		}
}
