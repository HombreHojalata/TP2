package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.factories.*;
import simulator.model.Animal;
import simulator.model.Region;
import simulator.model.SelectionStrategy;
import simulator.model.Simulator;
import simulator.misc.Utils;

public class Main {
	
	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");
		
		private String tag;
		private String desc;

		private ExecMode(String modeTag, String modeDesc) {
			tag = modeTag;
			desc = modeDesc;
		}

		public String getTag() {
			return tag;
		}

		public String getDesc() {
			return desc;
		}
	}
	
	private static Factory<Region> regionFactory;
	private static Factory<Animal> animalFactory;

	// default values for some parameters
	//
	private final static Double DEFAULT_TIME = 10.0; // in seconds

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double time = 60.0; // TODO: Cambiar para meterlo luego
	private static Double dt = 0.03; // TODO: Cambiar para meterlo luego
	private static boolean sv = true; // TODO: Cambiar para meterlo luego
	private static String inFile = "ex1.json";
	private static String outFile = null;
	private static ExecMode mode = ExecMode.BATCH;

	private static void parseArgs(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseHelpOption(line, cmdLineOptions);
			parseInFileOption(line);
			parseTimeOption(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("A real number representing the total simulation time in seconds. Default value: "
						+ DEFAULT_TIME + ".")
				.build());

		return cmdLineOptions;
	}

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
		inFile = line.getOptionValue("i");
		if (mode == ExecMode.BATCH && inFile == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}

	private static void parseTimeOption(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", DEFAULT_TIME.toString());
		try {
			time = Double.parseDouble(t);
			assert (time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}

	private static void initFactories() {
		List<Builder<SelectionStrategy>> selectionStrategyBuilders = new ArrayList<>();  
		selectionStrategyBuilders.add(new SelectFirstBuilder());  
		selectionStrategyBuilders.add(new SelectClosestBuilder());
		selectionStrategyBuilders.add(new SelectYoungestBuilder());
		Factory<SelectionStrategy> selectionStrategyFactory = new BuilderBasedFactory<SelectionStrategy>(selectionStrategyBuilders);
		List<Builder<Animal>> animalBuilders = new ArrayList<>();
		animalBuilders.add(new SheepBuilder(selectionStrategyFactory));
		animalBuilders.add(new WolfBuilder(selectionStrategyFactory));
		animalFactory = new BuilderBasedFactory<Animal>(animalBuilders);
		List<Builder<Region>> regionBuilders = new ArrayList<>();
		regionBuilders.add(new DefaultRegionBuilder());
		regionBuilders.add(new DynamicSupplyRegionBuilder());
		regionFactory = new BuilderBasedFactory<Region>(regionBuilders);
		
	}

	private static JSONObject loadJSONFile(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}


	private static void startBatchMode() throws Exception {
		InputStream is = new FileInputStream(new File(inFile));
		JSONObject obj = loadJSONFile(is);
		OutputStream out = outFile == null ? System.out : new FileOutputStream(new File(outFile));
		Simulator sim = new Simulator(obj.getInt("cols"), obj.getInt("rows"), obj.getInt("width"), obj.getInt("height"), animalFactory, regionFactory); 
		Controller cont = new Controller(sim);
		cont.loadData(obj);
		cont.run(time, dt, sv, out);
		out.close();
	}

	private static void startGUIMode() throws Exception {
		throw new UnsupportedOperationException("GUI mode is not ready yet ...");
	}

	private static void start(String[] args) throws Exception {
		initFactories();
		parseArgs(args);
		switch (mode) {
		case BATCH:
			startBatchMode();
			break;
		case GUI:
			startGUIMode();
			break;
		}
	}

	public static void main(String[] args) {
		Utils.RAND.setSeed(2147483647l);
		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
