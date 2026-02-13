package simulator.model;

import java.util.List;

public interface SelectionStrategy {
	Animal select(Animal a, List<Animal> as);
	Animal SelectFirst(List<Animal> as);
	Animal SelectClosest(List<Animal> as);
	Animal SelectYoungest(List<Animal> as);
}
