package simulator.model;

import java.util.List;
import java.util.function.Predicate;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {
	final static double INIT_ENERGY = 100.0;
	final static double MUTATION_TOLERANCE = 0.2;
	final static double NEARBY_FACTOR = 60.0;
	final static double COLLISION_RANGE = 8.0;
	final static double HUNGER_DECAY_EXP_FACTOR = 0.007;
	final static double MAX_ENERGY = 100.0;
	final static double MAX_DESIRE = 100.0;
	
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
		if (geneticCode == null || geneticCode.isEmpty()) throw new IllegalArgumentException("Genetic code cannot be empty");
		if (sightRange <= 0) throw new IllegalArgumentException("Sight range must be positive");
		if (initSpeed <= 0) throw new IllegalArgumentException("Initial speel must be positive");
		if (mateStrategy == null) throw new IllegalArgumentException("Mate strategy cannot be null");
		
		this.geneticCode = geneticCode;
		this.diet = diet;
		this.state = State.NORMAL;
		this.pos = pos;
		this.dest = null;
		this.energy = INIT_ENERGY;
		this.speed = Utils.getRandomizedParameter(initSpeed, 0.1);
		this.age = 0;
		this.desire = 0;
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
		this.pos = p1.getPosition().plus(Vector2D.getRandomVector(-1, 1).scale(NEARBY_FACTOR * (Utils.RAND.nextGaussian() + 1)));
		this.dest = null;
		this.energy = ((p1.energy + p2.energy) / 2);
		this.speed = Utils.getRandomizedParameter((p1.getSpeed() + p2.getSpeed()) / 2, MUTATION_TOLERANCE);
		this.age = 0;
		this.desire = 0;
		this.sightRange = Utils.getRandomizedParameter((p1.getSightRange() + p2.getSightRange()) / 2, MUTATION_TOLERANCE);
		this.mateTarget = null;
		this.baby = null;
		this.regionMngr = null;
		this.mateStrategy = p2.mateStrategy;
	}
	
	void init(AnimalMapView regMngr) {
		regionMngr = regMngr;
		if (pos == null)
			pos = randomPosition(0, regionMngr.getWidth(), 0, regionMngr.getHeight());
		else  {
			adjustPos(regionMngr.getWidth(), regionMngr.getHeight());
		}
		dest = randomPosition(0, regionMngr.getWidth(), 0, regionMngr.getHeight());
	}
	
	Animal deliverBaby() {
		Animal newBaby = baby;
		baby = null;
		return newBaby;
	}
	
	protected void move(double speed) {
		pos = pos.plus(dest.minus(pos).direction().scale(speed));
	}
	
	protected void setState(State state) {
		this.state = state;
		switch (state) {
		case NORMAL:
			setNormalStateAction();
			break;
		case MATE:
			setMateStateAction();
			break;
		case HUNGER:
			setHungerStateAction();
			break;
		case DANGER:
			setDangerStateAction();
			break;
		case DEAD:
			setDeadStateAction();
			break;
		}
	}
	
	abstract protected void setNormalStateAction();
	abstract protected void setMateStateAction();
	abstract protected void setHungerStateAction();
	abstract protected void setDangerStateAction();
	abstract protected void setDeadStateAction();
	
	public JSONObject asJSON() {
		JSONObject json = new JSONObject();
		json.put("pos", pos.asJSONArray());
		json.put("gcode", geneticCode);
		json.put("diet", diet.toString());
		json.put("state", state.toString());
		return json;		
	}
	
	@Override public State getState() { return state; }
	@Override public Vector2D getPosition() { return pos; }
	@Override public String getGeneticCode() { return geneticCode; }
	@Override public Diet getDiet() { return diet; }
	@Override public double getSpeed() { return speed; }
	@Override public double getSightRange() { return sightRange; }
	@Override public double getEnergy() { return energy; }
	@Override public double getAge() { return age; }
	@Override public Vector2D getDestination() { return dest; }
	@Override public boolean isPregnant() { return (baby != null); }
	
	public AnimalMapView getRegionManager() { return regionMngr; }
	public double getDesire() { return desire; }
	public Animal getMateTarget() { return mateTarget; }
	public SelectionStrategy getMateStrategy() { return mateStrategy; }
	
	public void setPosition(Vector2D pos) {	this.pos = pos;	}
	public void setDestination(Vector2D dest) {this.dest = dest; }
	public void setMateTarget(Animal mateTarget) { this.mateTarget = mateTarget; }
	protected void setBaby(Animal baby) { this.baby = baby; }
	
	protected void setEnergy(double energy) {
		if (energy > MAX_ENERGY) this.energy = MAX_ENERGY;
		else if (energy < 0) this.energy = 0;
		else this.energy = energy;
	}
	protected void setAge(double age) {
		if (age < 0) this.age = 0;
		this.age = age;
	}
	protected void setDesire(double desire) {
		if (desire > MAX_DESIRE) this.desire = MAX_DESIRE;
		if (desire < 0) this.desire = 0;
		this.desire = desire;
	}
	
	protected boolean outOfMap() {
		double x = pos.getX(), y = pos.getY();
		return (x < 0 || x >= regionMngr.getWidth() || y < 0 || y >= regionMngr.getHeight());
	}
	
	protected List<Animal> getAnimalsInRange(Predicate<Animal> filter) {
		List<Animal> candidates = regionMngr.getAnimalsInRange(this, filter);
		return candidates;
	}
	
	protected void moveAndStats(double dt, double E, double D, double speedMult) {
		double speed = getSpeed() * dt * Math.exp((getEnergy() - MAX_ENERGY) * HUNGER_DECAY_EXP_FACTOR);
		move(speed * speedMult);
		addAge(dt);
		addEnergy(E * speedMult);
		addDesire(D);
	}
	
	protected void addEnergy(double amount) {
		setEnergy(getEnergy() + amount);
	}
	protected void addAge(double amount) {
		setAge(getAge() + amount);
	}
	protected void addDesire(double amount) {
		setDesire(getDesire() + amount);
	}
	
	protected void adjustPos(double width, double height) {
		double x = this.pos.getX();
		double y = this.pos.getY();
		
		while (x >= width) x = (x - width);
	    while (x < 0) x = (x + width);
	    while (y >= height) y = (y - height);
	    while (y < 0) y = (y + height);
		
		this.pos = new Vector2D(x, y);
	}
	
	protected Vector2D randomPosition(double minX, double maxX, double minY, double maxY) {
		assert (maxX >= minX);
		assert (maxY >= minY);
		double x = minX + Utils.RAND.nextDouble(maxX - minX);
		double y = minY + Utils.RAND.nextDouble(maxY - minY);
		assert (x >= minX && x <= maxX);
		assert (y >= minY && y <= maxY);
		return new Vector2D(x, y);
	}
}
