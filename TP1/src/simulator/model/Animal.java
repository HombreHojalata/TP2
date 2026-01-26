package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {
	private String geneticCode;
	private Diet diet;
	private State state;
	private Vector2D pos;
	private Vector2D dest;
	private double energy;
	private double speed;
	private double age;
	private double desire;
	private double sightRange;
	private Animal mateTarget;
	private Animal baby;
	private AnimalMapView regionMngr;
	private SelectionStrategy mateStrategy;

	protected Animal(String geneticCode, Diet diet, double sightRange, double initSpeed, SelectionStrategy mateStrategy, Vector2D pos) {
		this.geneticCode = geneticCode;
		this.diet = diet;
		this.state = State.NORMAL;
		this.pos = pos;
		this.dest = null;
		this.energy = 100.0;
		this.speed = Utils.getRandomizedParameter(initSpeed, 0.1);
		this.age = 0.0;
		this.desire = 0.0;
		this.sightRange = sightRange;
		this.mateTarget = null;
		this.baby = null;
		this.regionMngr = null;
		this.mateStrategy = mateStrategy;	
	}
	
	protected Animal(Animal p1, Animal p2) {
		this.geneticCode = p1.geneticCode;
		this.diet = p1.diet;
		this.state = State.NORMAL;
		this.pos = p1.getPosition().plus(Vector2D.getRandomVector(-1, 1).scale(60.0 * (Utils.RAND.nextGaussian() + 1)));
		this.dest = null;
		this.energy = (p1.energy + p2.energy) / 2;
		this.speed = Utils.getRandomizedParameter((p1.getSpeed() + p2.getSpeed()) / 2, 0.2);
		this.age = 0.0;
		this.desire = 0.0;
		this.sightRange = Utils.getRandomizedParameter((p1.getSightRange() + p2.getSightRange()) / 2, 0.2);
		this.mateTarget = null;
		this.baby = null;
		this.regionMngr = null;
		this.mateStrategy = p2.mateStrategy;
	}
	
	void init(AnimalMapView regMngr) {
		regionMngr = regMngr;
		if (pos == null) pos = Vector2D.getRandomVector(0.0, regionMngr.getMinDimension() - 1);
		else  {
			// TODO: Ajustar dentro del mapa
		}
		dest = Vector2D.getRandomVector(0.0, regionMngr.getMinDimension() - 1);
	}
	
	Animal deliverBaby() {
		Animal newBaby = baby;
		baby = null;
		return newBaby;
	}	
}
