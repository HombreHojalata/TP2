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
	private Vector2D pos;
	private SelectionStrategy mateStrategy;
	private SelectionStrategy dangerStrategy;

	public SheepBuilder(Factory<SelectionStrategy> stratFactory) {
		super("sheep", "Builder for Sheep");
		this.stratFactory = stratFactory;
		pos = null;
		dangerStrategy = stratFactory.createInstance(new JSONObject().put("type", "first"));
		mateStrategy = stratFactory.createInstance(new JSONObject().put("type", "first"));
	}
	
	@Override
	protected void fillInData(JSONObject o) {
		if (o.has("mate_strategy"))	
			mateStrategy = stratFactory.createInstance(o.getJSONObject("mate_strategy"));
		if (o.has("danger_strategy"))	
			dangerStrategy = stratFactory.createInstance(o.getJSONObject("danger_strategy"));
		if (o.has("pos")) {
			JSONObject p = o.getJSONObject("pos");
			JSONArray xRange = p.getJSONArray("x_range");
			JSONArray yRange = p.getJSONArray("y_range");
			
			double x = xRange.getDouble(0) + (xRange.getDouble(1) - xRange.getDouble(0)) * Utils.RAND.nextDouble();
            double y = yRange.getDouble(0) + (yRange.getDouble(1) - yRange.getDouble(0)) * Utils.RAND.nextDouble();
            pos = new Vector2D(x, y);
		}
	}

	@Override
	protected Animal createInstance(JSONObject data) {
		if (data == null) throw new IllegalArgumentException("Missing data"); 
		fillInData(data);
		return new Sheep(mateStrategy, dangerStrategy, pos);
	}
}
