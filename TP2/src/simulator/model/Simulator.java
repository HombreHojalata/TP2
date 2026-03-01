package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;

import simulator.factories.Factory;

public class Simulator implements JSONable {
	private Factory<Animal> animalsFactory;
	private Factory<Region> regionsFactory;
	private RegionManager regionManager;
	private List<Animal> animals;
	private double time;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animalsFactory, Factory<Region> regionsFactory) {
		this.animalsFactory = animalsFactory;
		this.regionsFactory = regionsFactory;
		regionManager = new RegionManager(cols, rows, width, height);
		animals = new ArrayList<>();
		time = 0.0;
	}
	
	private void setRegion(int row, int col, Region r) {
		regionManager.setRegion(row, col, r);
	}
	
	public void setRegion(int row, int col, JSONObject rJson) {
		Region r = regionsFactory.createInstance(rJson);
		setRegion(row, col, r);
	}
	
	private void addAnimal(Animal a) {
		animals.add(a);
		regionManager.registerAnimal(a);
	}
	public void addAnimal(JSONObject aJson) {
		Animal a = this.animalsFactory.createInstance(aJson);
		addAnimal(a);
	}
	
	public void advance(double dt) {
		time += dt;
		List<Animal> dead = this.animals.stream().filter(a -> a.getState() == State.DEAD).toList(); 
		for (Animal d : dead) {
			this.regionManager.unregisterAnimal(d);
			animals.remove(d);
		}	
		
		for (Animal a : animals) {
			a.update(dt);
			regionManager.updateAnimalRegion(a);
		}
		
		regionManager.updateAllRegions(dt);
		
		List<Animal> babys = new ArrayList<>();
		for (Animal a : animals) {
			if (a.isPregnant())
				babys.add(a.deliverBaby());
		}
		for (Animal b : babys)
			addAnimal(b);
	}
	
	public MapInfo getMapInfo() {return regionManager;}	
	public List<? extends AnimalInfo> getAnimals() { return Collections.unmodifiableList(animals);}
	public double getTime() { return time; }
	public JSONObject asJSON() {
		JSONObject obj = new JSONObject();
		obj.put("time", time);
		obj.put("state", regionManager.asJSON());	
		return obj;
	}
	
}
