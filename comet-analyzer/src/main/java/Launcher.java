import java.io.IOException;

import org.jdom2.JDOMException;

import edu.uci.ics.comet.analyzer.config.ConfigReader;
import edu.uci.ics.comet.analyzer.evaluation.Evaluation;
import edu.uci.ics.comet.analyzer.evaluation.EvaluationsManager;

/**
 * 
 */

/**
 * @author matias
 *
 */
public class Launcher {

	private static final String DEFAULT_EVENTS_FILE = "eventsprocessing.xml";

	/**
	 * 
	 */
	public Launcher() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ConfigReader.init(getFilepath(args));
			Evaluation evaluation = ConfigReader.createElements();

			EvaluationsManager evaluationsManager = new EvaluationsManager();
			evaluationsManager.evaluate(evaluation);
		} catch (IOException e) {
			System.err.println("Unexpected error reading configuration: " + e.getMessage());
		} catch (JDOMException e) {
			System.err.println("XML Configuration is invalid: " + e.getMessage());
		} finally {
			ConfigReader.shutdown();
		}
	}

	private static String getFilepath(String[] args) {
		if (args.length > 0) {
			return args[0];
		} else {
			return DEFAULT_EVENTS_FILE;
		}
	}
}