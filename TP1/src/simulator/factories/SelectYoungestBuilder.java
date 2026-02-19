package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy> {
	
	public SelectYoungestBuilder() {
		super("youngest", "Selects the youngest animal on the list");
	}

	@Override
	protected SelectionStrategy createInstance(JSONObject data) {
		if (data == null) throw new IllegalArgumentException("Missing data"); 
		if (!data.isEmpty()) throw new IllegalArgumentException("SelectYoungest strategy data must be empty"); 
		return new SelectYoungest();
	}
}
