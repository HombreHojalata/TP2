package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;

public interface AnimalMapView {
	public double getWidth();
	public double getHeight();
	public double getFood(Animal animal, double dt);
	
	public List<Animal> getAnimalsInRange(Vector2D pos, double range, String attribute, String value);
}
