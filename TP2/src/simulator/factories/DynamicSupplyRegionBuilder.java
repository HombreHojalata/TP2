package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {
	private double factor;
	private double food;

	public DynamicSupplyRegionBuilder() {
		super("dynamic", "Creates a dynamic region (Food grows overtime)");
		factor = 2.0;
		food = 100.0;
	}
	@Override
	public void fillInData(JSONObject o) {
		if(o.has("factor")) factor = o.getDouble("factor");
		if(o.has("food")) food = o.getDouble("food");
	}

	@Override
	protected Region createInstance(JSONObject data) {
		if (data == null) throw new IllegalArgumentException("Missing data"); 
		fillInData(data);
		return new DynamicSupplyRegion(factor, food);
	}

}
