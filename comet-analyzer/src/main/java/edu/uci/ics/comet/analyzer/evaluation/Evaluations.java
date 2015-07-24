package edu.uci.ics.comet.analyzer.evaluation;

public class Evaluations {

	private Evaluations() {
	}

	/**
	 * Retrieves a member from the {@link EvaluationResultType} enumeration
	 * given its representative name.
	 * 
	 * @param resultName
	 *            The {@link EvaluationResultType} name used in the
	 *            configuration.
	 * @return An {@link EvaluationResultType}, or <code>null</code> if the
	 *         given name does not represent any {@link EvaluationResultType}.
	 */
	public static EvaluationResultType toEvaluationResult(String resultName) {
		EvaluationResultType[] values = EvaluationResultType.values();
		for (int i = 0; i < values.length; i++) {
			if (values[i].getName().equals(resultName)) {
				return values[i];
			}
		}

		return null;
	}

	/**
	 * Checks if an {@link EvaluationResultType} corresponds with a severity
	 * (unexpected result).
	 * 
	 * @param result
	 *            The, likely, unexpected result.
	 * @return <code>true</code> if the result is failed or warning. Otherwise,
	 *         <code>false</code>.
	 */
	public static boolean isSeverity(EvaluationResultType result) {
		return result.equals(EvaluationResultType.FAILED) || result.equals(EvaluationResultType.WARNING);
	}

	/**
	 * Retrieves a severity represented as an {@link EvaluationResultType}.
	 * Severity can only be failed or warning.
	 * 
	 * @param name
	 *            The severity String representation.
	 * @return An {@link EvaluationResultType}.
	 * @throws IllegalArgumentException
	 *             if the string does not represent an actual severity.
	 */
	public static EvaluationResultType toSeverity(String name) {
		if (isSeverity(toEvaluationResult(name))) {
			return toEvaluationResult(name);
		} else {
			throw new IllegalArgumentException("Severity can only be failed or warning.");
		}
	}
}