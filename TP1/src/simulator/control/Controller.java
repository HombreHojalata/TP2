package simulator.control;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Controller {
	private Simulator sim;

	public Controller(Simulator sim) {
		this.sim = sim;
	}
	
	
	public void loadData(JSONObject data) {
		if (data.has("regions")) {
		JSONArray regions = data.getJSONArray("regions");
		for (int i = 0; i < regions.length(); i++) {
			int rf = regions.getJSONObject(i).getJSONArray("row").getInt(0);
			int rt = regions.getJSONObject(i).getJSONArray("row").getInt(1);
			int cf = regions.getJSONObject(i).getJSONArray("col").getInt(0);
			int ct = regions.getJSONObject(i).getJSONArray("col").getInt(1);
			for (int R = rf; R < rt; R++)
				for (int C = cf; C < ct; C++)
					sim.setRegion(R, C, regions.getJSONObject(i).getJSONObject("spec"));
		}
		
		}
		if (data.has("animals")) {
			JSONArray animals = data.getJSONArray("animals");
			for (int j = 0; j < animals.length(); j++) 
				for (int k = 0; k < animals.getJSONObject(j).getInt("amount"); k++)
					sim.addAnimal(animals.getJSONObject(j).getJSONObject("spec"));
		}
	}
	
	public void run(double t, double dt, boolean sv, OutputStream out) throws IOException { // TODO: Comprobar que escriba el JSON correctamente
		JSONObject obj = new JSONObject();
		obj.put("in", sim.asJSON());
		while (sim.getTime() > t) {
			SimpleObjectViewer view = null;  
			if (sv) {  
			   MapInfo m = sim.getMapInfo();  
			   view = new SimpleObjectViewer("[ECOSYSTEM]", m.getWidth(), m.getHeight(), m.getCols(), m.getRows());  
			   view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt); 
			}
			sim.advance(dt);
			if (sv) view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
		}
		obj.put("out", sim.asJSON());
		try (Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
			writer.write(obj.toString(4));
		    writer.flush();
		}
	}


	private List<ObjInfo> toAnimalsInfo(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.getGeneticCode(), (int) a.getPosition().getX(), (int) a.getPosition().getY(),8));
		return ol;
	}

}
