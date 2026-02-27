package simulator.model;

import simulator.misc.Vector2D;
import simulator.misc.Utils;

public class Wolf extends Animal {
	final static String WOLF_GENETIC_CODE = "Wolf";
	final static double INIT_SIGHT_WOLF = 50.0;
	final static double INIT_SPEED_WOLF = 60.0;
	final static double BOOST_FACTOR_WOLF = 3.0;
	final static double MAX_AGE_WOLF = 14.0;
	final static double FOOD_THRESHOLD_WOLF = 50.0;
	final static double FOOD_DROP_BOOST_FACTOR_WOLF = 1.2;
	final static double FOOD_DROP_RATE_WOLF = 18.0;
	final static double FOOD_DROP_DESIRE_WOLF = 10.0;
	final static double FOOD_EAT_VALUE_WOLF = 50.0;
	final static double DESIRE_THRESHOLD_WOLF = 65.0;
	final static double DESIRE_INCREASE_RATE_WOLF = 30.0;
	final static double PREGNANT_PROBABILITY_WOLF = 0.75;
	
	private Animal huntTarget;
	private SelectionStrategy huntingStrategy;
	
	
	public Wolf(SelectionStrategy mateStrategy, SelectionStrategy huntingStrategy, Vector2D pos) {
		super(WOLF_GENETIC_CODE, Diet.CARNIVORE, INIT_SIGHT_WOLF, INIT_SPEED_WOLF, mateStrategy, pos);
		if (huntingStrategy == null) throw new IllegalArgumentException("Hunting stratergy cannot be null");
		this.huntingStrategy = huntingStrategy;
		huntTarget = null;
	}
	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		this.huntingStrategy = p1.huntingStrategy;
		huntTarget = null;
	}
	@Override
		public void update(double dt) {
			if (getState() == State.DEAD) return;
			switch (getState()) {
			case NORMAL: updateNormal(dt); break;
			case DANGER: break; // Un objeto Wolf nunca puede estar en este estado
			case MATE: updateMate(dt); break;
			case HUNGER: updateHunger(dt); break; 
			case DEAD: break; //Nunca esta en este estado
			}
			AnimalMapView regMngr = getRegionManager();
			if (outOfMap()) {
				adjustPos(regMngr.getWidth(), regMngr.getHeight());
				setState(State.NORMAL);
			}
			if (getEnergy() <= 0.0 || getAge() > MAX_AGE_WOLF)
				setState(State.DEAD);
			if (getState() != State.DEAD) {
				double food = regMngr.getFood(this, dt);
				addEnergy(food);
			}
		}
	private void updateNormal(double dt) {
		if (getPosition().distanceTo(getDestination()) < 8.0) 
			setPosition(randomPosition(0.00, getRegionManager().getWidth(), 0.00, getRegionManager().getHeight()));
		moveAndStats(dt, -FOOD_DROP_RATE_WOLF * dt, DESIRE_INCREASE_RATE_WOLF * dt, 1); // De normal el lobo no es más rápido
		if (getEnergy() < FOOD_THRESHOLD_WOLF) {
			setState(State.HUNGER);
		}
		else if (getDesire() > DESIRE_THRESHOLD_WOLF) {
			setState(State.MATE);
		}
	}
	
	private void updateMate(double dt) {
		Animal mateTarget = getMateTarget();
		if (mateTarget != null && (mateTarget.getState() == State.DEAD || getPosition().distanceTo(mateTarget.getPosition()) > getSightRange()))
			setMateTarget(null);
		if (getMateTarget() == null) {
			setMateTarget(this.getMateStrategy().select(this, getAnimalsInRange(this.getGeneticCode())));
			if (getMateTarget() == null)
				updateNormal(dt);
		}
		if (getMateTarget() != null) {
			setDestination(getMateTarget().getPosition());
			moveAndStats(dt, -18.0 * dt * FOOD_DROP_BOOST_FACTOR_WOLF, DESIRE_INCREASE_RATE_WOLF * dt, BOOST_FACTOR_WOLF);
			if (getPosition().distanceTo(getMateTarget().getPosition()) < 8.0) {
				setDesire(0.0);
				getMateTarget().setDesire(0.0);
				if (!isPregnant() && Utils.RAND.nextDouble() < PREGNANT_PROBABILITY_WOLF ) {
					this.setBaby(new Wolf(this, getMateTarget()));
					this.addEnergy(-FOOD_DROP_DESIRE_WOLF);
				}
				setMateTarget(null);
			}
		}
	}
	
	private void updateHunger(double dt) {
		if (huntTarget == null || huntTarget.getState() == State.DEAD) 
			huntTarget = this.huntingStrategy.select(this, getAnimalsInRange(Diet.HERBIVORE.toString()));
		if(huntTarget == null) updateNormal(dt);
		else {
			setDestination(huntTarget.getPosition());
			moveAndStats(dt, -FOOD_DROP_RATE_WOLF * dt * FOOD_DROP_BOOST_FACTOR_WOLF, DESIRE_INCREASE_RATE_WOLF * dt, BOOST_FACTOR_WOLF);
			if (getPosition().distanceTo(huntTarget.getPosition()) < 8.0) {
				huntTarget.setState(State.DEAD);
				huntTarget = null;
				addEnergy(FOOD_EAT_VALUE_WOLF);
				}
			if(getEnergy() > FOOD_THRESHOLD_WOLF) {
				if(getDesire() < DESIRE_THRESHOLD_WOLF) setState(State.NORMAL);
				else setState(State.MATE);
			}
		}
	}
		


	@Override
	protected void setNormalStateAction() {huntTarget = null; setMateTarget(null);}
	@Override
	protected void setMateStateAction() { huntTarget = null;}
	@Override
	protected void setHungerStateAction() { setMateTarget(null);}
	@Override
	protected void setDangerStateAction() {/**/}
	@Override
	protected void setDeadStateAction() {huntTarget = null; setMateTarget(null);}
}
