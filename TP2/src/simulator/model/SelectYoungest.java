package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy {

	public SelectYoungest() {
	}

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as !=null) {
		Animal aux = as.getFirst();
		for(Animal an: as) {
			if (an.getAge() < aux.getAge()) aux = an;
		}
		return aux;
		}
		else {
			return null;
		}
	}

}
