package simulator.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Region implements FoodSupplier, RegionInfo, Entity {

	protected List<Animal> animals;
	
	public Region() { animals = new ArrayList<Animal>(); }
	
	public final void addAnimal(Animal a) { animals.add(a); }
	public final void removeAnimal(Animal a) { animals.remove(a); } //Deberia estar bien, pero si hay fallos revisar
	public final List<Animal> getAnimals() { return animals; }
	
	public JSONObject asJSON() {
		   JSONArray jsonArray = new JSONArray();

	        for (Animal a : animals) {
	            jsonArray.put(a.asJSON());
	        }

	        JSONObject result = new JSONObject();
	        result.put("animals", jsonArray);

	        return result;
	}
	
	@Override
	public void update(double dt) {
		for (Animal a : animals) {
			a.update(dt);
		}
	}
	
	public List<Animal> getAnimalsDiet(Diet d) {
		List<Animal> aux = new ArrayList<Animal>();
		for (Animal a : animals) {
			if (a.getDiet() == d) aux.add(a);
		}
		return aux;
	}
}
	
	
