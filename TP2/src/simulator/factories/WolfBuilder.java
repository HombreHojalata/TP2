package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Animal> {
	private Factory<SelectionStrategy> stratFactory;

	public WolfBuilder(Factory<SelectionStrategy> stratFactory) {
		super("wolf", "Builder for Wolf");
		this.stratFactory = stratFactory;
	}
	
	@Override
	protected Animal createInstance(JSONObject data) {
		if (data == null) throw new IllegalArgumentException("Data cannot be null"); 
		SelectionStrategy mateStrategy = stratFactory.createInstance(new JSONObject().put("type", "first"));
        SelectionStrategy huntingStrategy = stratFactory.createInstance(new JSONObject().put("type", "first"));
        Vector2D pos = null;
        
		if (data.has("mate_strategy"))	
			mateStrategy = stratFactory.createInstance(data.getJSONObject("mate_strategy"));
		if (data.has("hunting_strategy"))	
			huntingStrategy = stratFactory.createInstance(data.getJSONObject("hunting_strategy"));
		if (data.has("pos")) {
			JSONArray p = data.getJSONArray("pos");
        	pos = new Vector2D(p.getDouble(0), p.getDouble(1));
		}
		return new Wolf(mateStrategy, huntingStrategy, pos);
	}
	
	@Override
	protected void fillInData(JSONObject o) {}
}
