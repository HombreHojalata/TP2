package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal> {
	private Factory<SelectionStrategy> stratFactory;

	public SheepBuilder(Factory<SelectionStrategy> stratFactory) {
		super("sheep", "Builder for Sheep");
		this.stratFactory = stratFactory;
	}
	
	@Override
	protected Animal createInstance(JSONObject data) {
		if (data == null) throw new IllegalArgumentException("Data cannot be null"); 
		SelectionStrategy mateStrategy = stratFactory.createInstance(new JSONObject().put("type", "first"));
        SelectionStrategy dangerStrategy = stratFactory.createInstance(new JSONObject().put("type", "first"));
        Vector2D pos = null;
        
        if (data.has("mate_strategy"))
        	mateStrategy = stratFactory.createInstance(data.getJSONObject("mate_strategy"));
        if (data.has("danger_strategy"))
        	dangerStrategy = stratFactory.createInstance(data.getJSONObject("danger_strategy"));
        if (data.has("pos")) {
        	JSONArray p = data.getJSONArray("pos");
        	pos = new Vector2D(p.getDouble(0), p.getDouble(1));
        }
		return new Sheep(mateStrategy, dangerStrategy, pos);
	}
	
	@Override
	protected void fillInData(JSONObject o) {}
}
