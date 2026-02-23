package simulator.factories;

import org.json.JSONObject;

import simulator.model.DefaultRegion;
import simulator.model.Region;

public class DefaultRegionBuilder extends Builder<Region> {

	public DefaultRegionBuilder() {
		super("default", "Creates a default Region");
	}

	@Override
	protected Region createInstance(JSONObject data) {
		if (data == null) throw new IllegalArgumentException("Missing data"); 
		if (!data.isEmpty()) throw new IllegalArgumentException("DefaultRegion strategy data must be empty");
		return new DefaultRegion();
	}
}
