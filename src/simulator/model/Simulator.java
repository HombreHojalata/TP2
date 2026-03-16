package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;

import simulator.factories.Factory;

public class Simulator implements JSONable, Observable<EcoSysObserver> {
	private Factory<Animal> animalsFactory;
	private Factory<Region> regionsFactory;
	private RegionManager regionManager;
	private List<Animal> animals;
	private double time;
	private List<EcoSysObserver> observers;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animalsFactory, Factory<Region> regionsFactory) {
		this.animalsFactory = animalsFactory;
		this.regionsFactory = regionsFactory;
		regionManager = new RegionManager(cols, rows, width, height);
		animals = new ArrayList<>();
		time = 0.0;
		observers = new ArrayList<>();
	}
	
	private void setRegion(int row, int col, Region r) {
		regionManager.setRegion(row, col, r);
	}
	
	public void setRegion(int row, int col, JSONObject rJson) {
		Region r = regionsFactory.createInstance(rJson);
		setRegion(row, col, r);
		notifyOnRegionSet(row, col, r);
	}
	
	private void addAnimal(Animal a) {
		animals.add(a);
		regionManager.registerAnimal(a);
		notifyOnAnimalAdded(a);
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
		
		List<Animal> pregnants = this.animals.stream().filter(a -> a.isPregnant()).toList(); 
		for (Animal p : pregnants)
			addAnimal(p.deliverBaby());
		
		notifyOnAdvance(dt);
	}
	
	public void reset(int cols, int rows, int width, int height) {
		animals.clear(); // Vaciar la lista de animale. TODO: Preguntar a Pablo si es solo esto.
		regionManager = new RegionManager(cols, rows, width, height);
		time = 0;
		notifyOnReset();
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

	@Override
	public void addObserver(EcoSysObserver o) {
		if (o != null && !observers.contains(o)) {
			observers.add(o);
			o.onRegister(time, regionManager, new ArrayList<>(animals));
		}
	}

	@Override
	public void removeObserver(EcoSysObserver o) {
		if (o != null)
			observers.remove(o);
	}
	
	private void notifyOnReset() {
	    List<AnimalInfo> animalsInfo = new ArrayList<>(animals);
	    for (EcoSysObserver o : observers) {
	        o.onReset(time, regionManager, animalsInfo);
	    }
	}

	private void notifyOnAnimalAdded(Animal a) {
	    List<AnimalInfo> animalsInfo = new ArrayList<>(animals);
	    for (EcoSysObserver o : observers) {
	        o.onAnimalAdded(time, regionManager, animalsInfo, a);
	    }
	}

	private void notifyOnRegionSet(int row, int col, Region r) {
	    for (EcoSysObserver o : observers) {
	        o.onRegionSet(row, col, regionManager, r);
	    }
	}

	private void notifyOnAdvance(double dt) {
	    List<AnimalInfo> animalsInfo = new ArrayList<>(animals);
	    for (EcoSysObserver o : observers) {
	        o.onAdvance(time, regionManager, animalsInfo, dt);
	    }
	}
	
}
