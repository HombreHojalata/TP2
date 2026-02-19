package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {

	public SelectFirstBuilder() {
		super("first", "Selects the first animal on the list");
	}

	@Override
	protected SelectionStrategy createInstance(JSONObject data) {
		if (data == null) throw new IllegalArgumentException("Missing data"); 
		if (!data.isEmpty()) throw new IllegalArgumentException("SelectFirst strategy data must be empty"); 
		return new SelectFirst();
	}

}
