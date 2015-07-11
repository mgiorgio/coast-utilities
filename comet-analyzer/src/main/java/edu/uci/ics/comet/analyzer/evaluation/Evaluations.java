package edu.uci.ics.comet.analyzer.evaluation;

public class Evaluations {

	private Evaluations() {
	}

	/**
	 * Retrieves a member from the {@link EvaluationResult} enumeration given
	 * its representative name.
	 * 
	 * @param resultName
	 *            The {@link EvaluationResult} name used in the configuration.
	 * @return An {@link EvaluationResult}, or <code>null</code> if the given
	 *         name does not represent any {@link EvaluationResult}.
	 */
	public static EvaluationResult toEvaluationResult(String resultName) {
		EvaluationResult[] values = EvaluationResult.values();
		for (int i = 0; i < values.length; i++) {
			if (values[i].getName().equals(resultName)) {
				return values[i];
			}
		}

		return null;
	}

	/**
	 * Checks if an {@link EvaluationResult} corresponds with a severity
	 * (unexpected result).
	 * 
	 * @param result
	 *            The, likely, unexpected result.
	 * @return <code>true</code> if the result is failed or warning. Otherwise,
	 *         <code>false</code>.
	 */
	public static boolean isSeverity(EvaluationResult result) {
		return result.equals(EvaluationResult.FAILED) || result.equals(EvaluationResult.WARNING);
	}

	/**
	 * Retrieves a severity represented as an {@link EvaluationResult}. Severity
	 * can only be failed or warning.
	 * 
	 * @param name
	 *            The severity String representation.
	 * @return An {@link EvaluationResult}.
	 * @throws IllegalArgumentException
	 *             if the string does not represent an actual severity.
	 */
	public static EvaluationResult toSeverity(String name) {
		if (isSeverity(toEvaluationResult(name))) {
			return toEvaluationResult(name);
		} else {
			throw new IllegalArgumentException("Severity can only be failed or warning.");
		}
	}
}