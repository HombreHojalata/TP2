package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal {
	final static String SHEEP_GENETIC_CODE = "Sheep";
	final static double INIT_SIGHT_SHEEP = 40.0;
	final static double INIT_SPEED_SHEEP = 35.0;
	final static double BOOST_FACTOR_SHEEP = 2.0;
	final static double MAX_AGE_SHEEP = 8.0;
	final static double FOOD_DROP_BOOST_FACTOR_SHEEP = 1.2;
	final static double FOOD_DROP_RATE_SHEEP = 20.0;
	final static double DESIRE_THRESHOLD_SHEEP = 65.0;
	final static double DESIRE_INCREASE_RATE_SHEEP = 40.0;
	final static double PREGNANT_PROBABILITY_SHEEP = 0.9;
	
	private Animal dangerSource;
	private SelectionStrategy dangerStrategy;
	
	public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy, Vector2D pos) {
		super(SHEEP_GENETIC_CODE, Diet.HERBIVORE, INIT_SIGHT_SHEEP, INIT_SPEED_SHEEP, mateStrategy, pos);
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
		case DEAD: break; // Nunca esta en este estado
		}
		AnimalMapView regMngr = getRegionManager();
		if (outOfMap()) {
			adjustPos(regMngr.getWidth(), regMngr.getHeight());
			setState(State.NORMAL);
		}
		if (getEnergy() <= 0 || getAge() > MAX_AGE_SHEEP)
			setState(State.DEAD);
		if (getState() != State.DEAD) {
			double food = regMngr.getFood(this, dt);
			addEnergy(food);
		}
	}
	
	private void updateNormal(double dt) {
		moveAndStats(dt, -FOOD_DROP_RATE_SHEEP * dt, DESIRE_INCREASE_RATE_SHEEP * dt, 1.0);
		if (getPosition().distanceTo(getDestination()) < COLLISION_RANGE) 
			setPosition(randomPosition(0, getRegionManager().getWidth(), 0, getRegionManager().getHeight()));
		if (dangerSource == null)
			dangerSource = this.dangerStrategy.select(this, getAnimalsInRange(Diet.CARNIVORE.toString()));
		if (dangerSource != null)
			setState(State.DANGER);
		else if (getDesire() > DESIRE_THRESHOLD_SHEEP)
			setState(State.MATE);
	}
	
	private void updateDanger(double dt) {
		if (dangerSource != null && dangerSource.getState() == State.DEAD)
			dangerSource = null;
		if (dangerSource == null)
			updateNormal(dt);
		else {
			setDestination(getPosition().plus(getPosition().minus(dangerSource.getPosition().direction())));
			moveAndStats(dt, -FOOD_DROP_RATE_SHEEP * dt * FOOD_DROP_BOOST_FACTOR_SHEEP, DESIRE_INCREASE_RATE_SHEEP * dt, BOOST_FACTOR_SHEEP);
		}
		if (dangerSource == null || getPosition().distanceTo(dangerSource.getPosition()) > getSightRange())
			dangerSource = this.dangerStrategy.select(this, getAnimalsInRange(Diet.CARNIVORE.toString()));
		if (dangerSource == null)
			setState(getDesire() < DESIRE_THRESHOLD_SHEEP ? State.NORMAL : State.MATE);
	}
	private void updateMate(double dt) {
		Animal mateTarget = getMateTarget();
		if (mateTarget != null && (mateTarget.getState() == State.DEAD || getPosition().distanceTo(mateTarget.getPosition()) > getSightRange()))
			setMateTarget(null);
		if (getMateTarget() == null) {
			setMateTarget(getMateStrategy().select(this, getAnimalsInRange(this.getGeneticCode())));
			if (getMateTarget() == null)
				updateNormal(dt);
		}
		if (getMateTarget() != null) {
			setDestination(getMateTarget().getPosition());
			moveAndStats(dt, -FOOD_DROP_RATE_SHEEP * dt * FOOD_DROP_BOOST_FACTOR_SHEEP, DESIRE_INCREASE_RATE_SHEEP * dt, BOOST_FACTOR_SHEEP);
			if (getPosition().distanceTo(getMateTarget().getPosition()) < COLLISION_RANGE) {
				setDesire(0);
				getMateTarget().setDesire(0);
				if (!isPregnant() && Utils.RAND.nextDouble() < PREGNANT_PROBABILITY_SHEEP)
					this.setBaby(new Sheep(this, getMateTarget()));
				setMateTarget(null);
			}
		}
		dangerSource = this.dangerStrategy.select(this, getAnimalsInRange(Diet.CARNIVORE.toString()));
		if (dangerSource != null)
			setState(State.DANGER);
		else if (getDesire() < DESIRE_THRESHOLD_SHEEP)
			setState(State.NORMAL);
	}

	
	@Override protected void setNormalStateAction() { dangerSource = null; setMateTarget(null); }
	@Override protected void setMateStateAction() {	dangerSource = null; }
	@Override protected void setHungerStateAction() { /**/ }
	@Override protected void setDangerStateAction() { setMateTarget(null); }
	@Override protected void setDeadStateAction() { dangerSource = null; setMateTarget(null); }
	
	
} 
