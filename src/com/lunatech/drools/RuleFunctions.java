package com.lunatech.drools;

import org.apache.log4j.Logger;
import org.drools.spi.KnowledgeHelper;

/**
 * Functions for use in DRL files.
 */
public class RuleFunctions {

	/**
	 * Log a message from a rule, using the rule’s package and name as the Log4J category.
	 */
	public static void log(final KnowledgeHelper drools, final String message, final Object... parameters) {
		final String category = drools.getRule().getPackageName() + "." + drools.getRule().getName();
		final String formattedMessage = String.format(message, parameters);
		Logger.getLogger(category).debug(formattedMessage);
	}

	public static boolean isPalindrome(final long n) {
		final String forwards = String.valueOf(n);
		final String backwards = new StringBuilder(forwards).reverse().toString();
		return forwards.equals(backwards);
	}

}
