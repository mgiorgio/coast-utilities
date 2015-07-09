/**
 * 
 */
package edu.uci.ics.como.analyzer.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import edu.uci.ics.como.analyzer.evaluation.And;
import edu.uci.ics.como.analyzer.evaluation.COMETEvent;
import edu.uci.ics.como.analyzer.evaluation.Evaluation;
import edu.uci.ics.como.analyzer.evaluation.EvaluationResult;
import edu.uci.ics.como.analyzer.evaluation.Not;
import edu.uci.ics.como.analyzer.evaluation.Or;
import edu.uci.ics.como.analyzer.evaluation.PatternEvaluation;
import edu.uci.ics.como.analyzer.evaluation.VolumeEvaluation;

/**
 * This class is responsible for reading the XML file and creating all config
 * elements and evaluations.
 * 
 * Implementation is quite ugly and should be decomposed using Builders, POJO
 * objects, potentially reflection... This is not going to be done in the
 * short-term (it might actually never be done) because I am unsure whether this
 * piece of code will be ever used after I finish my MS Thesis.
 * 
 * @author Matias Giorgio
 *
 */
public class ConfigReader {

	private static Map<String, Object> globals;

	public static final String CONFIG_PATH = "eventsprocessing.xml";

	private static Element root;

	/**
	 * @throws IOException
	 * @throws JDOMException
	 * 
	 */
	public static void init() throws IOException, JDOMException {
		globals = new HashMap<String, Object>();

		SAXBuilder jdomBuilder = new SAXBuilder();

		Document jdomDoc = jdomBuilder.build(CONFIG_PATH);

		root = jdomDoc.getRootElement();
	}

	public static void createElements() {
		readGlobals();

		createEvaluations();
	}

	public static void main(String[] args) throws IOException, JDOMException {
		ConfigReader.init();
		ConfigReader.createElements();
	}

	private static void createEvaluations() {
		Element assertions = root.getChild("assertions");

		And and = new And();

		for (Element assertion : assertions.getChildren()) {
			createEvaluation(assertion, and);
		}
	}

	private static void createEvaluation(Element assertion, Evaluation parent) {
		switch (assertion.getName()) {
		case "pattern":
			parent.addNestedEvaluation(createPattern(assertion));
			break;
		case "volume":
			parent.addNestedEvaluation(createVolume(assertion));
			break;
		case "or":
			parent.addNestedEvaluation(createOr(assertion));
			break;
		case "and":
			parent.addNestedEvaluation(createAnd(assertion));
			break;
		case "not":
			parent.addNestedEvaluation(createNot(assertion));
			break;
		default:
			break;
		}
	}

	private static Evaluation createNot(Element assertion) {
		Not not = new Not();

		createEvaluation(assertion, not);

		return not;
	}

	private static Evaluation createAnd(Element andAssertion) {
		And and = new And();

		for (Element assertion : andAssertion.getChildren()) {
			createEvaluation(assertion, and);
		}

		return and;
	}

	private static Evaluation createOr(Element orAssertion) {
		Or or = new Or();

		for (Element assertion : orAssertion.getChildren()) {
			createEvaluation(assertion, or);
		}

		return or;
	}

	private static Evaluation createVolume(Element volume) {
		int timerange = Integer.parseInt(volume.getAttributeValue("timerange"));
		int minRange = Integer.parseInt(volume.getAttributeValue("minrange"));
		int maxRange = Integer.parseInt(volume.getAttributeValue("maxrange"));
		TimeUnit timeUnit = getTimeUnit(volume.getAttributeValue("unit"));

		VolumeEvaluation eval = new VolumeEvaluation(timerange, timeUnit, minRange, maxRange);
		eval.setSeverity(getSeverity(volume.getAttributeValue("severity")));

		for (Element event : volume.getChildren()) {
			Map<String, Object> fields = new HashMap<String, Object>();
			propertiesToMap(event, fields);
			COMETEvent cometEvent = new COMETEvent(fields);
			eval.addCOMETEvent(cometEvent);
		}

		return eval;
	}

	private static TimeUnit getTimeUnit(String timeUnit) {
		switch (timeUnit) {
		case "minutes":
			return TimeUnit.MINUTES;
		case "millis":
			return TimeUnit.MILLISECONDS;
		case "seconds":
		default:
			return TimeUnit.SECONDS;
		}
	}

	private static Evaluation createPattern(Element pattern) {
		PatternEvaluation eval = new PatternEvaluation();
		eval.setSeverity(getSeverity(pattern.getAttributeValue("severity")));

		for (Element event : pattern.getChildren()) {
			Map<String, Object> fields = new HashMap<String, Object>();
			propertiesToMap(event, fields);
			COMETEvent cometEvent = new COMETEvent(fields);
			eval.addCOMETEvent(cometEvent);
		}

		return eval;
	}

	private static EvaluationResult getSeverity(String value) {
		if (value == null || "fail".equalsIgnoreCase(value)) {
			return EvaluationResult.FAILED;
		} else if ("warning".equalsIgnoreCase(value)) {
			return EvaluationResult.WARNING;
		}

		return EvaluationResult.FAILED;
	}

	private static void readGlobals() {
		Element global = root.getChild("global");
		propertiesToMap(global, globals);
	}

	private static void propertiesToMap(Element global, Map<String, Object> map) {
		for (Element eachGlobal : global.getChildren()) {
			map.put(eachGlobal.getName(), eachGlobal.getValue());
		}
	}

	public static Map<String, Object> globals() {
		return Collections.unmodifiableMap(globals);
	}
}