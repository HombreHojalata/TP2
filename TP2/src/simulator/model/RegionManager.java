package simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;


public class RegionManager implements AnimalMapView {
	private int cols;
	private int rows;
	private int width;
	private int height;
	private int regWidth;
	private int regHeight;
	private Region[][] regions;
	private Map<Animal, Region> animalRegion;	
	
	public RegionManager(int cols, int rows, int width, int height) {
		this.cols = cols;
		this.rows = rows;
		this.width = width;
		this.height = height;
		regWidth = (int) Math.ceil((double) width / cols);
		regHeight = (int) Math.ceil((double) height / rows);
		regions = new Region[rows][cols];
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				regions[i][j] = new DefaultRegion();
		animalRegion = new HashMap<>();
	}
	
	public void setRegion(int row, int col, Region r) {
		Region oldRegion = regions[row][col];
		regions[row][col] = r;
		for (Animal a : oldRegion.getAnimals()) {
			r.addAnimal(a);
			animalRegion.put(a, r);
		}
	}
	
	public void registerAnimal(Animal a) {
		a.init(this);
		Region region = getRegionFromPos(a.getPosition());
		region.addAnimal(a);
		animalRegion.put(a, region);
	}
	
	public void unregisterAnimal(Animal a) {
		Region region = getRegionFromPos(a.getPosition());
		if (region != null) {
			region.removeAnimal(a);
			animalRegion.remove(a);
		}
	}
	
	public void updateAnimalRegion(Animal a) {
		Region currRegion = animalRegion.get(a);
		Region newRegion = getRegionFromPos(a.getPosition());
		
		if (currRegion != newRegion) {
			currRegion.removeAnimal(a);
			newRegion.addAnimal(a);
			animalRegion.put(a, newRegion);
		}
	}
	
	private Region getRegionFromPos(Vector2D pos) {
		int x = (int) (pos.getY() / regHeight);
		int y = (int) (pos.getX() / regWidth);
		
		
		x = Math.max(0, Math.min(x, rows - 1));
		y = Math.max(0, Math.min(y, cols - 1));
		return regions[x][y];
	}
	
	@Override
	public double getFood(AnimalInfo a, double dt) {
		return animalRegion.get(a).getFood(a, dt);
	}
	
	public void updateAllRegions(double dt) {
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				regions[i][j].update(dt);
	}
	
	@Override
	public List<Animal> getAnimalsInRange(Animal a, Predicate<Animal> filter) { // TODO: Rehacer	
		List<Animal> list = new ArrayList<>();
		int rangeInCols = (int) Math.ceil(a.getSightRange() / regWidth);
		int rangeInRows = (int) Math.ceil(a.getSightRange() / regHeight);
		int centerCol = (int) a.getPosition().getX() / regWidth;
		int centerRow = (int) a.getPosition().getY() / regHeight;

		for (int i = centerRow - rangeInRows; i <= centerRow + rangeInRows; i++) {
			for (int j = centerCol - rangeInCols; j <= centerCol + rangeInCols; j++) {
				if (i >= 0 && i < rows && j >= 0 && j < cols) { // Comprueba el out of bounds
					for (Animal an : regions[i][j].getAnimals()) {
						if (an != a && a.getPosition().distanceTo(an.getPosition()) <= a.getSightRange() && filter.test(an)) {
							list.add(an);
						}
					}
				}
			}
		}
		return list;
	}
	
	public JSONObject asJSON() {
		JSONArray jsonArray = new JSONArray();
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				JSONObject json = new JSONObject();
				json.put("row", i);
				json.put("col", j);
				json.put("data", regions[i][j].asJSON());
				jsonArray.put(json);
			}
		}
		
		JSONObject result = new JSONObject();
		result.put("regions", jsonArray);
		return result;
	}
	
	@Override public int getCols() { return cols; }
	@Override public int getRows() { return rows; }
	@Override public int getWidth() { return width; }
	@Override public int getHeight() { return height; }
	@Override public int getRegionWidth() {	return regWidth; }
	@Override public int getRegionHeight() { return regHeight; }
}
