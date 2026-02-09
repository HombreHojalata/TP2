package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import java.util.List;

public class Sheep extends Animal {
	private Animal dangerSource;
	private SelectionStrategy dangerStrategy;
	
	private static final double INIT_SIGHT_RANGE = 40.0;
	private static final double INIT_SPEED = 35.0;
	private static final double DESIRE_THRESHOLD = 65.0;
	
	public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy, Vector2D pos) {
		super("Sheep", Diet.HERBIVORE, INIT_SIGHT_RANGE, INIT_SPEED, mateStrategy, pos);
		if (dangerStrategy == null) throw new IllegalArgumentException("Danger stratergy cannot be null");
		this.dangerStrategy = dangerStrategy;
		this.dangerSource = null;
	}
	
	public Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this.dangerStrategy = p1.dangerStrategy;
		this.dangerSource = null;
	}

	@Override
	public void update(double dt) {
		if (getState() == State.DEAD) return;
		switch (getState()) {
		case NORMAL: updateNormal(dt); break;
		case DANGER: updateDanger(dt); break;
		case MATE: updateMate(dt); break;
		case HUNGER: break; // Un objeto Sheep nunca puede estar en este estado
		}
		AnimalMapView regMngr = getRegionManager();
		if (outOfMap()) {
			Vector2D pos = getPosition();
			pos.adjustPos(regMngr.getWidth(), regMngr.getHeight());
			setPosition(pos);
			setState(State.NORMAL);
		}
		if (getEnergy() <= 0.0 || getAge() > 8.0)
			setState(State.DEAD);
		if (getState() != State.DEAD) {
			double food = regMngr.getFood(this, dt);
			addEnergy(food);
		}
	}
	
	private void updateNormal(double dt) {
		moveAndStats(dt, 1.0);
		if (getPosition().distanceTo(getDestination()) < 8.0) 
			setPosition(Vector2D.getRandomVector2(0.0, getRegionManager().getWidth(), getRegionManager().getHeight()));
		if (dangerSource == null)
			dangerSource = findTarget("diet", Diet.CARNIVORE.toString(), dangerStrategy);
		if (dangerSource != null)
			setState(State.DANGER);
		else if (getDesire() > DESIRE_THRESHOLD)
			setState(State.MATE);
	}
	
	private void updateDanger(double dt) {
		if (dangerSource != null && dangerSource.getState() == State.DEAD)
			dangerSource = null;
		if (dangerSource == null)
			updateNormal(dt);
		else {
			setDestination(getPosition().plus(getPosition().minus(dangerSource.getPosition().direction())));
			moveAndStats(dt, 2.0);
		}
		if (dangerSource == null || getPosition().distanceTo(dangerSource.getPosition()) > getSightRange())
			dangerSource = findTarget("diet", Diet.CARNIVORE.toString(), dangerStrategy);
		if (dangerSource == null)
			setState(getDesire() < DESIRE_THRESHOLD ? State.NORMAL : State.MATE);
	}
	private void updateMate(double dt) {
		Animal mateTarget = getMateTarget();
		if (mateTarget != null && (mateTarget.getState() == State.DEAD) || getPosition().distanceTo(mateTarget.getPosition()) > getSightRange())
			setMateTarget(null);
		if (getMateTarget() == null) {
			setMateTarget(findTarget("gcode", getGeneticCode(), getMateStrategy()));
			if (getMateTarget() == null)
				updateNormal(dt);
		}
		if (getMateTarget() != null) {
			setDestination(getMateTarget().getPosition());
			moveAndStats(dt, 2.0);
			if (getPosition().distanceTo(getMateTarget().getPosition()) < 8.0) {
				setDesire(0.0);
				getMateTarget().setDesire(0.0);
				if (!isPregnant() && Utils.RAND.nextDouble() < 0.9)
					this.setBaby(new Sheep(this, getMateTarget()));
				setMateTarget(null);
			}
		}
		dangerSource = findTarget("diet", Diet.CARNIVORE.toString(), dangerStrategy);
		if (dangerSource != null)
			setState(State.DANGER);
		else if (getDesire() < DESIRE_THRESHOLD)
			setState(State.NORMAL);
	}

	private void moveAndStats(double dt, double speedMult) {
		double speed = getSpeed() * dt * Math.exp((getEnergy() - 100.0) * 0.007);
		move(speed * speedMult);
		addAge(dt);
		addEnergy(-20.0 * dt * speedMult);
		addDesire(40.0 * dt);
	}
	
	@Override protected void setNormalStateAction() { dangerSource = null; setMateTarget(null); }
	@Override protected void setMateStateAction() {	dangerSource = null; }
	@Override protected void setHungerStateAction() { /*...*/ }
	@Override protected void setDangerStateAction() { setMateTarget(null); }
	@Override protected void setDeadStateAction() { dangerSource = null; setMateTarget(null); }
	
	private void addEnergy(double amount) {
		setEnergy(getEnergy() + amount);
	}
	private void addAge(double amount) {
		setAge(getAge() + amount);
	}
	private void addDesire(double amount) {
		setDesire(getDesire() + amount);
	}
} 
