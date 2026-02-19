package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectClosest;
import simulator.model.SelectionStrategy;

public class SelectClosestBuilder extends Builder<SelectionStrategy> {
	
	public SelectClosestBuilder() {
		super("closest", "Select the closest animals");
	}
	
	@Override
	protected SelectionStrategy createInstance(JSONObject data) {
		if (data == null) throw new IllegalArgumentException("Missing data"); 
		if (!data.isEmpty()) throw new IllegalArgumentException("SelectClosest strategy data must be empty"); 
		return new SelectClosest();
	}
}
