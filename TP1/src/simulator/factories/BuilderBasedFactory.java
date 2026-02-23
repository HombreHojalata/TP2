package simulator.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {  
	private Map<String, Builder<T>> builders;  
	private List<JSONObject> buildersInfo;

	public BuilderBasedFactory() {  
      builders = new HashMap<>();
      buildersInfo = new LinkedList<>();
	}

	public BuilderBasedFactory(List<Builder<T>> builders) {  
		this();
		for (Builder<T> b : builders) {
			addBuilder(b);
		} 
	}

	public void addBuilder(Builder<T> b) {  
		builders.put(b.getTypeTag(), b);
		buildersInfo.add(b.getInfo());
	}

	@Override  
	public T createInstance(JSONObject info) {  
		T T = null;
		if (info == null) {  
			throw new IllegalArgumentException("'info' cannot be null");  
		}
		if (!info.has("type") || info.getString("type").isEmpty()) throw new IllegalArgumentException("'info' needs a type that can't be empty");
		
		for (String s : builders.keySet()) {
			if (s.equals(info.getString("type"))) {
				JSONObject obj =  info.has("data") ? info.getJSONObject("data") : new JSONObject();
				 T = builders.get(s).createInstance(obj);
			}
		}
		
		if (T != null) return T;
		else throw new IllegalArgumentException("Unrecognized 'info':" + info.toString());  
	}

	@Override  
	public List<JSONObject> getInfo() {  
		return Collections.unmodifiableList(buildersInfo);  
	}

}