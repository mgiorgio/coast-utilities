/**
 * 
 */
package edu.uci.ics.comet.analyzer.query.mongodb;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.comet.analyzer.query.EventQuery;
import edu.uci.ics.comet.analyzer.query.EventQuery.QueryOperation;
import edu.uci.ics.comet.analyzer.query.QueryResult;
import edu.uci.ics.comet.generator.EventStream;
import edu.uci.ics.comet.protocol.fields.COMETFields;

/**
 * @author matias
 *
 */
public class TestMongoQueryHandler extends AbstractMongoTest {

	@BeforeClass
	public static void setupClass() {
		AbstractMongoTest.setupClass();

		insertInitialData();
	}

	protected static void insertInitialData() {
		List<EventStream> streams = new LinkedList<EventStream>();
		streams.add(new EventStream(createEventStreamConf("alice", "x", "curl-new", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("bob", "y", "curl-new", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("alice", "x", "curl-send", "inter", 5, 5)));

		for (EventStream eventStream : streams) {
			eventStream.run();
		}
	}

	@Test
	public void testCount() throws Exception {
		EventQuery query = query(COMETFields.SOURCE_ISLAND.getName(), "bob", QueryOperation.EQ);
		Assert.assertEquals("count() did not provide the right number of elements.", 1, getQueryHandler().count(query));

		query = query(COMETFields.SOURCE_ISLAND.getName(), "alice", QueryOperation.EQ);
		Assert.assertEquals("count() did not provide the right number of elements.", 6, getQueryHandler().count(query));

		query = query(COMETFields.SOURCE_ISLAND.getName(), "alice", QueryOperation.EQ);
		query.addMember(COMETFields.TYPE.getName(), "curl-new", QueryOperation.EQ);
		Assert.assertEquals("count() did not provide the right number of elements.", 1, getQueryHandler().count(query));

		query = query(COMETFields.EVENT_ID.getName(), 3, QueryOperation.LT);
		Assert.assertEquals("count() did not provide the right number of elements.", 2, getQueryHandler().count(query));

		query = query(COMETFields.EVENT_ID.getName(), 3, QueryOperation.LE);
		Assert.assertEquals("count() did not provide the right number of elements.", 3, getQueryHandler().count(query));

		query = query(COMETFields.EVENT_ID.getName(), 3, QueryOperation.GT);
		Assert.assertEquals("count() did not provide the right number of elements.", 4, getQueryHandler().count(query));

		query = query(COMETFields.EVENT_ID.getName(), 3, QueryOperation.GE);
		Assert.assertEquals("count() did not provide the right number of elements.", 5, getQueryHandler().count(query));
	}

	@Test
	public void testList() throws Exception {
		EventQuery query = query(COMETFields.SOURCE_ISLAND.getName(), "bob", QueryOperation.EQ);
		List<QueryResult> bobEvents = getQueryHandler().list(query);

		assertEventIDs(bobEvents, Arrays.asList(2L));

		query = query(COMETFields.SOURCE_ISLAND.getName(), "alice", QueryOperation.EQ);
		List<QueryResult> aliceEvents = getQueryHandler().list(query);

		assertEventIDs(aliceEvents, Arrays.asList(1L, 3L, 4L, 5L, 6L, 7L));
	}
}