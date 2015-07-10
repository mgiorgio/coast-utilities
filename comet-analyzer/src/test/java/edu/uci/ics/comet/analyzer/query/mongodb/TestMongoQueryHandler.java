/**
 * 
 */
package edu.uci.ics.comet.analyzer.query.mongodb;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.uci.ics.comet.analyzer.query.EventQuery;
import edu.uci.ics.comet.analyzer.query.EventQuery.QueryOperation;
import edu.uci.ics.comet.analyzer.query.QueryResult;

/**
 * @author matias
 *
 */
public class TestMongoQueryHandler extends AbstractMongoTest {

	@Test
	public void testCount() throws Exception {
		EventQuery query = query(COMETMembers.SOURCE_ISLAND.getName(), "bob", QueryOperation.EQ);
		Assert.assertEquals("count() did not provide the right number of elements.", 1, getQueryHandler().count(query));

		query = query(COMETMembers.SOURCE_ISLAND.getName(), "alice", QueryOperation.EQ);
		Assert.assertEquals("count() did not provide the right number of elements.", 6, getQueryHandler().count(query));

		query = query(COMETMembers.SOURCE_ISLAND.getName(), "alice", QueryOperation.EQ);
		query.addQueryMember(COMETMembers.TYPE.getName(), "curl-new", QueryOperation.EQ);
		Assert.assertEquals("count() did not provide the right number of elements.", 1, getQueryHandler().count(query));

		query = query(COMETMembers.EVENT_ID.getName(), 3, QueryOperation.LT);
		Assert.assertEquals("count() did not provide the right number of elements.", 2, getQueryHandler().count(query));

		query = query(COMETMembers.EVENT_ID.getName(), 3, QueryOperation.LE);
		Assert.assertEquals("count() did not provide the right number of elements.", 3, getQueryHandler().count(query));

		query = query(COMETMembers.EVENT_ID.getName(), 3, QueryOperation.GT);
		Assert.assertEquals("count() did not provide the right number of elements.", 4, getQueryHandler().count(query));

		query = query(COMETMembers.EVENT_ID.getName(), 3, QueryOperation.GE);
		Assert.assertEquals("count() did not provide the right number of elements.", 5, getQueryHandler().count(query));
	}

	@Test
	public void testList() throws Exception {
		EventQuery query = query(COMETMembers.SOURCE_ISLAND.getName(), "bob", QueryOperation.EQ);
		List<QueryResult> bobEvents = getQueryHandler().list(query);

		assertEventIDs(bobEvents, Arrays.asList(2));

		query = query(COMETMembers.SOURCE_ISLAND.getName(), "alice", QueryOperation.EQ);
		List<QueryResult> aliceEvents = getQueryHandler().list(query);

		assertEventIDs(aliceEvents, Arrays.asList(1, 3, 4, 5, 6, 7));
	}
}