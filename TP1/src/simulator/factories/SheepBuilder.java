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
		if (data == null) throw new IllegalArgumentException("Missing data"); 
		
		SelectionStrategy mateStrategy;
		if (data.has("mate_strategy"))	
			mateStrategy = stratFactory.createInstance(data.getJSONObject("mate_strategy"));
		else
			mateStrategy = stratFactory.createInstance(new JSONObject().put("type", "first"));
		
		SelectionStrategy dangerStrategy;
		if (data.has("danger_strategy"))	
			dangerStrategy = stratFactory.createInstance(data.getJSONObject("danger_strategy"));
		else
			dangerStrategy = stratFactory.createInstance(new JSONObject().put("type", "first"));
		
		
		Vector2D pos = null;
		if (data.has("pos")) {
			JSONObject p = data.getJSONObject("pos");
			JSONArray xRange = p.getJSONArray("x_range");
			JSONArray yRange = p.getJSONArray("y_range");
			
			double x = xRange.getDouble(0) + (xRange.getDouble(1) - xRange.getDouble(0)) * Utils.RAND.nextDouble();
            double y = yRange.getDouble(0) + (yRange.getDouble(1) - yRange.getDouble(0)) * Utils.RAND.nextDouble();
            pos = new Vector2D(x, y);
		}
		
		return new Sheep(mateStrategy, dangerStrategy, pos);
	}
}
