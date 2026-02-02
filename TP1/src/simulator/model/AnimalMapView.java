package simulator.model;

import java.util.ArrayList;
import simulator.misc.Vector2D;

public interface AnimalMapView {
	public double getMinDimension();
	public double getWidth();
	public double getHeigh();
	
	public double getFood(Animal animal, double dt);
	public ArrayList<Animal> getAnimalsInRange(Vector2D pos, double sightRange, String key, String criterio);
}
