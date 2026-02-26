package simulator.model;

import simulator.misc.Vector2D;
import simulator.misc.Utils;

public class Wolf extends Animal {
	private Animal huntTarget;
	private SelectionStrategy huntingStrategy;
	
	private static final double INIT_SIGHT_RANGE = 50.0;
	private static final double INIT_SPEED = 60.0;
	private static final double DESIRE_THRESHOLD = 65.0;
	private static final double ENERGY_THRESHOLD = 50.0;
	
	
	public Wolf(SelectionStrategy mateStrategy, SelectionStrategy huntingStrategy, Vector2D pos) {
		super("wolf", Diet.CARNIVORE, INIT_SIGHT_RANGE, INIT_SPEED, huntingStrategy, pos);
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
			if (getEnergy() <= 0.0 || getAge() > 14.0)
				setState(State.DEAD);
			if (getState() != State.DEAD) {
				double food = regMngr.getFood(this, dt);
				addEnergy(food);
			}
		}
	private void updateNormal(double dt) {
		if (getPosition().distanceTo(getDestination()) < 8.0) 
			setPosition(randomPosition(0.00, getRegionManager().getWidth(), 0.00, getRegionManager().getHeight()));
		moveAndStats(dt, -18.0*dt, 30.0*dt, 1.0);
		if(getEnergy() < ENERGY_THRESHOLD) {
			setState(State.HUNGER);
		}
		if(getDesire() > DESIRE_THRESHOLD) {
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
			moveAndStats(dt, -18.0 * dt * 1.2, 30.0 * dt, 3.0);
			if (getPosition().distanceTo(getMateTarget().getPosition()) < 8.0) {
				setDesire(0.0);
				getMateTarget().setDesire(0.0);
				if (!isPregnant() && Utils.RAND.nextDouble() < 0.9)
					this.setBaby(new Wolf(this, getMateTarget()));
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
			moveAndStats(dt, -18.0 * dt * 1.2, 30.0 * dt, 3.0);
			if (getPosition().distanceTo(huntTarget.getPosition()) < 8.0) {
				huntTarget.setState(State.DEAD);
				huntTarget = null;
				addEnergy(50.0);
				}
			if(getEnergy() > ENERGY_THRESHOLD) {
				if(getDesire() < DESIRE_THRESHOLD) setState(State.NORMAL);
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
	protected void setDangerStateAction() {/***/}
	@Override
	protected void setDeadStateAction() {huntTarget = null; setMateTarget(null);}
}
