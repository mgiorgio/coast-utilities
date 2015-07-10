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
	public static EvaluationResult fromName(String resultName) {
		EvaluationResult[] values = EvaluationResult.values();
		for (int i = 0; i < values.length; i++) {
			if (values[i].getName().equals(resultName)) {
				return values[i];
			}
		}

		return null;
	}

}
