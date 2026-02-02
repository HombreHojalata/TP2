package simulator.model;

import org.json.JSONObject;

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
			double x = pos.getX(), y = pos.getY();
			double width = regionMngr.getWidth();
			double height = regionMngr.getHeigh();
			while (x >= width) x = (x - width);  
			while (x < 0) x = (x + width);  
			while (y >= height) y = (y - height);  
			while (y < 0) y = (y + height);
			Vector2D newPos = new Vector2D(x, y);
			pos = newPos;
		}
		dest = Vector2D.getRandomVector(0.0, regionMngr.getMinDimension() - 1);
	}
	
	Animal deliverBaby() { // TODO: Excepción si null
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
		json.put("pos", pos.toString());
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
	
	public void setPosition(Vector2D pos) {	this.pos = pos;	}
	public void setDestination(Vector2D dest) {this.dest = dest; }
	public void setMateTarget(Animal mateTarget) { this.mateTarget = mateTarget; }
	
	protected void setEnergy(double energy) {
		this.energy = energy;
		if (energy > 100.0) energy = 100.0;
		else if (energy < 0.0) energy = 0.0;
	}
	protected void setAge(double age) {
		this.age = age;
	}
	protected void setDesire(double desire) {
		this.desire = desire;
	}
}
