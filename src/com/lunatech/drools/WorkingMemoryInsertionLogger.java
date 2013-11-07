package com.lunatech.drools;

import org.apache.log4j.Logger;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;

import java.util.*;

/**
 * Event listener that keeps track of how many instances of each type are inserted into working memory.
 */
public class WorkingMemoryInsertionLogger implements WorkingMemoryEventListener {

	private final static Logger log = Logger.getLogger(WorkingMemoryInsertionLogger.class);

	private Map<String,Long> inserts = new HashMap<String, Long>();

	/**
	 * Output the number of instances of each type, sorted by fully-qualified class name.
	 */
	public void log() {
		final SortedSet<String> insertedClasses = new TreeSet<String>();
		insertedClasses.addAll(inserts.keySet());
		for(final String insertedClass : insertedClasses) {
			log.info(String.format("%8d × %s", inserts.get(insertedClass), insertedClass));
		}
	}

	/**
	 * Record a working memory fact insertion.
	 */
	public void objectInserted(ObjectInsertedEvent event) {
		final String factClass = event.getObject().getClass().getName();
		if (!inserts.containsKey(factClass)) {
			inserts.put(factClass, 0L);
		}
		inserts.put(factClass, inserts.get(factClass) + 1);
	}

	public void objectUpdated(org.drools.event.rule.ObjectUpdatedEvent objectUpdatedEvent) {
	}

	public void objectRetracted(org.drools.event.rule.ObjectRetractedEvent objectRetractedEvent) {
	}
}
