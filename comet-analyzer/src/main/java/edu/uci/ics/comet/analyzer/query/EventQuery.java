package edu.uci.ics.comet.analyzer.query;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EventQuery {

	private List<QueryMember> queryMembers;

	public EventQuery() {
		queryMembers = new LinkedList<EventQuery.QueryMember>();
	}

	/**
	 * Creates an {@link EventQuery}, adding initial query members. EQ operation
	 * is assumed.
	 * 
	 * @param fields
	 *            Keys and values of the initial query members.
	 */
	public EventQuery(Map<String, Object> fields) {
		this();
		for (Entry<String, Object> field : fields.entrySet()) {
			addMember(field.getKey(), field.getValue(), QueryOperation.EQ);
		}
	}

	public EventQuery(QueryMember... members) {
		this();
		for (QueryMember member : members) {
			this.addMember(member);
		}
	}

	/**
	 * Adds a {@link QueryMember} to this {@link EventQuery}.
	 * 
	 * @param key
	 * @param value
	 * @param operation
	 */
	public EventQuery addMember(QueryMember member) {
		queryMembers.add(member);
		return this;
	}

	/**
	 * Adds a {@link QueryMember} to this {@link EventQuery}.
	 * 
	 * @param key
	 * @param value
	 * @param operation
	 */
	public EventQuery addMember(String key, Object value, QueryOperation operation) {
		this.addMember(new QueryMember(key, value, operation));
		return this;
	}

	/**
	 * @return The first {@link QueryMember}. If there are no query members,
	 *         <code>null</code>.
	 */
	public QueryMember first() {
		if (queryMembers.isEmpty()) {
			return null;
		}
		return queryMembers.get(0);
	}

	/**
	 * @return The number of {@link QueryMember}s.
	 */
	public int size() {
		return queryMembers.size();
	}

	/**
	 * @return <code>true</code> if there are no {@link QueryMember}s.
	 *         Otherwise, <code>false</code>.
	 */
	public boolean isEmpty() {
		return this.size() == 0;
	}

	/**
	 * @return An immutable list of {@link QueryMember}s.
	 */
	public List<QueryMember> getQueryMembers() {
		return Collections.unmodifiableList(queryMembers);
	}

	public static class QueryMember {
		private String key;

		private Object value;

		private QueryOperation operation;

		public QueryMember(String key, Object value, QueryOperation operation) {
			this.key = key;
			this.value = value;
			this.operation = operation;
		}

		public String getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}

		public QueryOperation getOperation() {
			return operation;
		}

		@Override
		public String toString() {
			return String.format("%s:%s (%s)", key, value, operation);
		}
	}

	public enum QueryOperation {
		EQ, NE, GT, GE, LT, LE;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (QueryMember queryMember : queryMembers) {
			builder.append(queryMember).append(", ");
		}

		return builder.toString();
	}
}