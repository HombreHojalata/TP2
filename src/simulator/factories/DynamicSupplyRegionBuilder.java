package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	public DynamicSupplyRegionBuilder() {
		super("dynamic", "Dynamic food supply");
	}
	
	@Override
	public void fillInData(JSONObject o) {
		o.put("factor", "food increase factor (optional, default 2.0)");
	    o.put("food", "initial amount of food (optional, default 100.0)");
	}

	@Override
	protected Region createInstance(JSONObject data) {
		double factor = data.has("factor") ? data.getDouble("factor") : 2.0;
		double food = data.has("food") ? data.getDouble("food") : 100.0;
		return new DynamicSupplyRegion(factor, food);
	}
}
