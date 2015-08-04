/**
 * 
 */
package edu.uci.ics.comet.analyzer.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ObjectUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import edu.uci.ics.comet.analyzer.evaluation.COMETEvent;
import edu.uci.ics.comet.analyzer.evaluation.Evaluation;
import edu.uci.ics.comet.analyzer.evaluation.EvaluationContext;
import edu.uci.ics.comet.analyzer.evaluation.EvaluationResultType;
import edu.uci.ics.comet.analyzer.evaluation.Evaluations;
import edu.uci.ics.comet.analyzer.evaluation.EventEvaluation;
import edu.uci.ics.comet.analyzer.evaluation.Not;
import edu.uci.ics.comet.analyzer.evaluation.Or;
import edu.uci.ics.comet.analyzer.evaluation.SequentialEvaluation;
import edu.uci.ics.comet.analyzer.evaluation.UnorderedEvaluation;
import edu.uci.ics.comet.analyzer.evaluation.VolumeEvaluation;
import edu.uci.ics.comet.analyzer.evaluation.capture.CaptureEngine;
import edu.uci.ics.comet.analyzer.query.QueryHandler;
import edu.uci.ics.comet.analyzer.query.mongodb.MongoQueryHandler;
import edu.uci.ics.comet.protocol.fields.COMETFields;

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

	/*
	 * TODO Evaluations should be receiving their own capture engine instead of
	 * the root one.
	 */

	private static Map<String, Object> globals;

	private static Element root;

	private static QueryHandler queryHandler;

	/**
	 * @throws IOException
	 * @throws JDOMException
	 * 
	 */
	public static void init(String filepath) throws IOException, JDOMException {
		globals = new HashMap<String, Object>();

		SAXBuilder jdomBuilder = new SAXBuilder();

		Document jdomDoc = jdomBuilder.build(filepath);

		root = jdomDoc.getRootElement();
	}

	public static Evaluation createElements() {
		readGlobals();

		initEvaluationContext();

		initQueryHandler();

		return createEvaluations();
	}

	private static void initEvaluationContext() {
		if (ConfigReader.globals().containsKey("last-component")) {
			EvaluationContext.put(EvaluationContext.LAST_COMPONENT_KEY, (String) ConfigReader.globals().get("last-component"));
		}

		String correlationField = (String) ConfigReader.globals().get("correlation-field");
		EvaluationContext.put(EvaluationContext.CORRELATION_FIELD_KEY, ObjectUtils.defaultIfNull(correlationField, COMETFields.MQ_TIME.getName()));
	}

	private static void initQueryHandler() {
		queryHandler = new MongoQueryHandler(globals());
		queryHandler.init();
	}

	public static void shutdown() {
		queryHandler.shutdown();
	}

	private static Evaluation createEvaluations() {
		Element assertions = root.getChild("assertions");

		if (assertions.getChildren().size() == 1) {
			return createEvaluation(assertions.getChildren().get(0), null);
		} else {
			UnorderedEvaluation unordered = new UnorderedEvaluation(CaptureEngine.getRootEngine());

			for (Element assertion : assertions.getChildren()) {
				createEvaluation(assertion, unordered);
			}

			unordered.setQueryHandler(queryHandler);

			return unordered;
		}
	}

	private static Evaluation createEvaluation(Element assertion, Evaluation parent) {
		Evaluation evaluation;
		switch (assertion.getName()) {
		case "sequence":
			evaluation = createSequence(assertion);
			break;
		case "volume":
			evaluation = createVolume(assertion);
			break;
		case "or":
			evaluation = createOr(assertion);
			break;
		case "unordered":
			evaluation = createAnd(assertion);
			break;
		case "not":
			evaluation = createNot(assertion);
			break;
		case "event":
			evaluation = createEvent(assertion);
			break;
		default:
			throw new IllegalArgumentException(assertion.getName() + " is not a valid evaluation type.");
		}

		evaluation.setDescription(assertion.getAttributeValue("description"));

		evaluation.setQueryHandler(queryHandler);

		configureSeverity(assertion, evaluation);

		if (parent != null) {
			parent.addNestedEvaluation(evaluation);
		}

		return evaluation;
	}

	private static Evaluation createNot(Element assertion) {
		Not not = new Not(CaptureEngine.getRootEngine());

		List<Element> nestedAssertion = assertion.getChildren();

		if (nestedAssertion.size() != 1) {
			throw new RuntimeException("NOT evaluation must have exactly one nested evaluation.");
		}

		createEvaluation(nestedAssertion.get(0), not);

		return not;
	}

	private static Evaluation createAnd(Element andAssertion) {
		UnorderedEvaluation and = new UnorderedEvaluation(CaptureEngine.getRootEngine());

		createChildrenEvaluations(andAssertion, and);

		return and;
	}

	private static Evaluation createOr(Element orAssertion) {
		Or or = new Or(CaptureEngine.getRootEngine());

		createChildrenEvaluations(orAssertion, or);

		return or;
	}

	private static void createChildrenEvaluations(Element evalConf, Evaluation eval) {
		for (Element subConf : evalConf.getChildren()) {
			createEvaluation(subConf, eval);
		}
	}

	private static Evaluation createVolume(Element volume) {
		int timerange = Integer.parseInt(volume.getAttributeValue("timerange"));
		int minRange = Integer.parseInt(volume.getAttributeValue("minrange"));
		int maxRange = Integer.parseInt(volume.getAttributeValue("maxrange"));
		TimeUnit timeUnit = getTimeUnit(volume.getAttributeValue("unit"));

		VolumeEvaluation eval = new VolumeEvaluation(timerange, timeUnit, minRange, maxRange, CaptureEngine.getRootEngine());

		createChildrenEvaluations(volume, eval);

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

	private static Evaluation createSequence(Element sequence) {
		SequentialEvaluation eval = new SequentialEvaluation(CaptureEngine.getRootEngine().newEngine());

		createChildrenEvaluations(sequence, eval);

		return eval;
	}

	private static Evaluation createEvent(Element event) {
		Map<String, Object> fields = new HashMap<String, Object>();
		propertiesToMap(event, fields);
		EventEvaluation eventEvaluation = new EventEvaluation(new COMETEvent(fields), queryHandler, CaptureEngine.getRootEngine());
		return eventEvaluation;
	}

	private static void configureSeverity(Element evalConf, Evaluation eval) {
		String severityValue = evalConf.getAttributeValue("severity", EvaluationResultType.FAILED.getName());
		EvaluationResultType severity = Evaluations.toEvaluationResult(severityValue);
		if (severity == null) {
			throw new IllegalArgumentException(String.format("Severity [%s] declared for [%s] is invalid.", severityValue, evalConf.getName()));
		}

		eval.setConfiguredSeverity(severity);
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