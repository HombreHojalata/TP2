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
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Controller {
	private Simulator sim;

	public Controller(Simulator sim) {
		this.sim = sim;
	}
	
	public void reset(int cols, int rows, int width, int height) {
		sim.reset(cols, rows, width, height);
	}
	
	public void setRegions(JSONObject rs) {
		if (rs.has("regions")) {
			JSONArray regions = rs.getJSONArray("regions");
			for (int i = 0; i < regions.length(); i++) {
				JSONObject regObj = regions.getJSONObject(i);
				int rf = regObj.getJSONArray("row").getInt(0);
				int rt = regObj.getJSONArray("row").getInt(1);
				int cf = regObj.getJSONArray("col").getInt(0);
				int ct = regObj.getJSONArray("col").getInt(1);
				JSONObject spec = regObj.getJSONObject("spec");
				
				for (int R = rf; R <= rt; R++) {
					for (int C = cf; C <= ct; C++) {
						sim.setRegion(R, C, spec);
					}
				}
			}
 		}
	}
	
	public void loadData(JSONObject data) {
		setRegions(data);

		if (data.has("animals")) {
			JSONArray animals = data.getJSONArray("animals");
			for (int i = 0; i < animals.length(); i++) {
				JSONObject animalEntry = animals.getJSONObject(i);
				int amount = animalEntry.getInt("amount");
				JSONObject spec = animalEntry.getJSONObject("spec");
				for (int k = 0; k < amount; k++) {
					sim.addAnimal(spec);
				}
			}
		}
	}
	
	public void advance(double dt) {
		sim.advance(dt);
	}
	
	public void addObserver(EcoSysObserver o) {
		sim.addObserver(o);
	}
	
	public void removeObserver(EcoSysObserver o) {
		sim.removeObserver(o);
	}
	
	public void run(double t, double dt, boolean sv, OutputStream out) throws IOException { // TODO: Mirar como escribir por pantalla
		JSONObject obj = new JSONObject();
		obj.put("in", sim.asJSON());
		SimpleObjectViewer view = null; 
		if (sv) {  
			   MapInfo m = sim.getMapInfo();  
			   view = new SimpleObjectViewer("[ECOSYSTEM]", m.getWidth(), m.getHeight(), m.getCols(), m.getRows());  
			   view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt); 
			}
		while (sim.getTime() <= t) {
			sim.advance(dt);
			if (sv) view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
		}
		obj.put("out", sim.asJSON());
		try (Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
			writer.write(obj.toString(4));
		    writer.flush();
		}
		if (sv) view.close(); // Pare cerrar el viewer cuando acabe
	}


	private List<ObjInfo> toAnimalsInfo(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.getGeneticCode(), (int) a.getPosition().getX(), (int) a.getPosition().getY(), (int) Math.round(a.getAge()) + 2));
		return ol;
	}

}
