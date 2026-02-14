package simulator.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Region implements FoodSupplier, RegionInfo, Entity {

	protected List<Animal> Animals;
	
	public Region() {Animals = new ArrayList<Animal>(); }
	@Override
	public void update(double dt) {
		for (Animal a : Animals) {
			a.update(dt);
		}
	}
	final void addAnimal(Animal a) {Animals.add(a);}
	final void removeAnimal(Animal a) {Animals.remove(a);} //Deberia estar bien, pero si hay fallos revisar
	final List<Animal> getAnimals() { return Animals;}
	
	public List<Animal> getAnimalsDiet(Diet d) {
		List<Animal> aux = new ArrayList<Animal>();
		for (Animal a : Animals) {
			if (a.getDiet() == d) aux.add(a);
		}
		return aux;
	}
	
	public JSONObject asJSON() {
		   JSONArray jsonArray = new JSONArray();

	        for (Animal a : Animals) {
	            jsonArray.put(a.asJSON());
	        }

	        JSONObject result = new JSONObject();
	        result.put("animals", jsonArray);

	        return result;
	}
}
