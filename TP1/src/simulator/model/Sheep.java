package simulator.model;

import simulator.misc.Vector2D;

public class Sheep extends Animal {
	private Animal dangerSource;
	private SelectionStrategy dangerStrategy;
	
	public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy, Vector2D pos) {
		super("Sheep", Diet.HERBIVORE, 40.0, 35.0, mateStrategy, pos);
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
		State state = getState();
		if (state != State.DEAD) {
			switch(state) {
			case NORMAL:	
				if (getPosition().distanceTo(getDestination()) < 8.0) 
					setDestination(Vector2D.getRandomVector(0.0, getRegionManager().getMinDimension() - 1));
				move(getSpeed() * dt * Math.exp((getEnergy() - 100.0) * 0.007));
				setAge(getAge() + dt);
				setEnergy(getEnergy() - 20.0 * dt);
				setDesire(getDesire() + 40.0 * dt);
				break;
			case MATE:
				break;
			case HUNGER:
				break;
			case DANGER:
				break;
			}
			double x = getPosition().getX(), y = getPosition().getY();
			double width = getRegionManager().getWidth();
			double height = getRegionManager().getHeigh();
			while (x >= width) x = (x - width);  
			while (x < 0) x = (x + width);  
			while (y >= height) y = (y - height);  
			while (y < 0) y = (y + height);
			Vector2D newPos = new Vector2D(x, y);
			if (!getPosition().equals(newPos)) {
				setPosition(newPos);
				setState(State.NORMAL);
			}
			if (getEnergy() <= 0.0 || getAge() > 8.0) 
				setState(State.DEAD);
			setEnergy(getEnergy() + getRegionManager().getFood(this, dt));
		}
	}

	@Override
	protected void setNormalStateAction() {
		dangerSource = null;
		setMateTarget(null);
	}

	@Override
	protected void setMateStateAction() {
		dangerSource = null;
	}

	@Override
	protected void setHungerStateAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setDangerStateAction() {
		setMateTarget(null);
	}

	@Override
	protected void setDeadStateAction() {
		// TODO Auto-generated method stub
		
	}
	
	
} 
