package simulator.factories;
import org.json.JSONObject;

import simulator.model.SelectClosest;
import simulator.model.SelectFirst;
import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;

public abstract class Builder<T> {  
	private String typeTag;  
	private String desc;

	public Builder(String typeTag, String desc) {
    if (typeTag == null || desc == null || typeTag.isBlank() || desc.isBlank())  
			throw new IllegalArgumentException("Invalid type/desc");  
		this.typeTag = typeTag;  
		this.desc = desc;
	}

	public String getTypeTag() {  
		return typeTag;  
	}

	public JSONObject getInfo() {  
		JSONObject info = new JSONObject();  
		info.put("type", typeTag);  
		info.put("desc", desc);  
		JSONObject data = new JSONObject();  
		fillInData(data);
		info.put("data", data);  
		return info;  
	}

	protected void fillInData(JSONObject o) {  
	}

	@Override  
	public String toString() {  
		return desc;  
	}

	protected abstract T createInstance(JSONObject data);  	
}

/*
 *
	public class SelectYoungestBuilder extends Builder<SelectionStrategy>{

		public SelectYoungestBuilder() {
			super("Youngest", "Elije el animal mas joven de la lista");
		}

		@Override
		protected SelectionStrategy createInstance(JSONObject data) {
			if (data == null) throw new IllegalArgumentException("Missing data"); 
			if (!data.isEmpty()) throw new IllegalArgumentException("SelectYoungest strategy data must be empty"); 
			return new SelectYoungest();
		}
	}
 * */
	
