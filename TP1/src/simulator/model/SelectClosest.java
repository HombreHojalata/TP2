package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy {

	public SelectClosest() {
	}

	@Override
	public Animal select(Animal a, List<Animal> as) {
		double min = a.getPosition().distanceTo(as.getFirst().getPosition());
		Animal aux = as.getFirst();
		for (Animal an : as) {
			if(min > a.getPosition().distanceTo(an.getPosition())) {
				aux = an;
				min = a.getPosition().distanceTo(an.getPosition());
			}
		}
		return aux;
	}

}
