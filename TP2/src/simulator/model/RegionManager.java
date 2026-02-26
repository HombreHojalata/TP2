package simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.json.JSONArray;
import org.json.JSONObject;


public class RegionManager implements AnimalMapView {
	private int cols;
	private int rows;
	private int width;
	private int height;
	private int regWidth;
	private int regHeight;
	private Region[][] regions;
	private Map<Animal, Region> animalRegion;	
	
	public RegionManager(int cols, int rows, int width, int height) { // TODO: MIRAR LAS ROWS/COLS
		this.cols = cols;
		this.rows = rows;
		this.width = width;
		this.height = height;
		regWidth = width / cols;
		regHeight = height / rows;
		regions = new Region[cols][rows];
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++)
				regions[i][j] = new DefaultRegion();
		animalRegion = new HashMap<>();
	}
	
	public void setRegion(int row, int col, Region r) {
		regions[col][row] = r;
		for (Animal a : r.getAnimals())
			animalRegion.put(a, r);
	}
	
	public void registerAnimal(Animal a) { // TODO: Esto tiene sentido... no?
		a.init(this);
		int x = (int) a.getPosition().getX() / regWidth;
		int y = (int) a.getPosition().getY() / regHeight;

		regions[x][y].addAnimal(a);
		animalRegion.put(a, regions[x][y]);
	}
	
	public void unregisterAnimal(Animal a) {
		int x = (int) a.getPosition().getX() / regWidth;
		int y = (int) a.getPosition().getY() / regHeight;

		regions[x][y].removeAnimal(a);
		animalRegion.remove(a);
	}
	
	public void updateAnimalRegion(Animal a) {
		int x = (int) a.getPosition().getX() / regWidth;
		int y = (int) a.getPosition().getY() / regHeight;
		
		if (regions[x][y] != animalRegion.get(a)) {
			animalRegion.get(a).removeAnimal(a);
			registerAnimal(a);
		}
	}
	
	@Override
	public double getFood(AnimalInfo a, double dt) {
		return animalRegion.get(a).getFood(a, dt);
	}
	
	public void updateAllRegions(double dt) {
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++)
				regions[i][j].update(dt);
	}
	
	@Override
	public List<Animal> getAnimalsInRange(Animal a, Predicate<Animal> filter) { // TODO: Rehacer
		List<Animal> list = new ArrayList<>();
		int rangeInCols = (int) Math.ceil(a.getSightRange() / regWidth);
		int rangeInRows = (int) Math.ceil(a.getSightRange() / regHeight);
		int centerCol = (int) a.getPosition().getX() / regWidth;
		int centerRow = (int) a.getPosition().getY() / regHeight;
		
		for (int i = centerCol - rangeInCols; i <= rangeInCols + rangeInRows; i++) {
			for (int j = centerRow - rangeInRows; j <= rangeInRows + rangeInRows; j++) {
				if (i >= 0 && i < cols && j >= 0 && j < rows) {
					for (Animal an : regions[i][j].getAnimals()) {
						if (an != a && a.getPosition().distanceTo(an.getPosition()) <= a.getSightRange() && filter.test(an))
							list.add(an);
					}
				}
			}
		}

		return list;
	}
	
	public JSONObject asJSON() {
		JSONArray jsonArray = new JSONArray();
		
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
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
