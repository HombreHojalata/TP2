package simulator.model;

import java.util.List;

public class SelectFirst implements SelectionStrategy {

	public SelectFirst() {}

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as == null) return null;
		return as.getFirst();
	}

}
