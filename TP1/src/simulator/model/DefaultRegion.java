package simulator.model;

public class DefaultRegion extends Region {

	public DefaultRegion() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getFood(Animal a, double dt) {
		int n = getAnimalsDiet(Diet.HERBIVORE).size();
		if (a.getDiet() == Diet.CARNIVORE) return 0.0;
		else if(a.getDiet() == Diet.HERBIVORE) return 60.0*Math.exp(-Math.max(0,n-5.0)*2.0)*dt;
		else return 0.0;
	}

}
