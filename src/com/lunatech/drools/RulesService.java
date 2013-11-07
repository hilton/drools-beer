package com.lunatech.drools;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;

import java.util.*;

/**
 * Service facade for running the rules engine.
 */
public class RulesService {

	private static final String QUERY_ROW_VALUE = "value";
	private final static String RESULTS_QUERY = "results";
	private final static Logger log = Logger.getLogger(RulesService.class);

	private static KnowledgeBase knowledgeBase;

	/** Map of problem solution values, keyed on problem number. */
	private List results;
   private String rulesFilePath;

   public RulesService(String path) {
      rulesFilePath = path;
   }

	/**
	 * Utility method to construct and cache the {@link KnowledgeBase} instance.
	 */
	private KnowledgeBase initKnowledgeBase() {
		log.info("initKnowledgeBase");
		if (knowledgeBase == null) {
			log.debug("Load rules");
			final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			try {
				builder.add(ResourceFactory.newClassPathResource(rulesFilePath, RulesService.class), ResourceType.DRL);
			}
			catch (final Exception e) {
				log.error("Could not load rules file : " + e.getMessage());
				return null;
			}

			log.debug("Compile rules");
			final Collection<KnowledgePackage> packages = builder.getKnowledgePackages();

			log.debug("Add packages");
			if (builder.hasErrors()) {
				log.error("Rules compilation failed: " + builder.getErrors());
			}
			else {
				final Properties properties = new Properties();
				properties.setProperty("org.drools.sequential", "true");
				final ClassLoader classLoader = this.getClass().getClassLoader();
				final KnowledgeBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(properties, classLoader);
				knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(kbConf);
				knowledgeBase.addKnowledgePackages(packages);
			}
		}
		return knowledgeBase;
	}

	/**
	 * Executes the rules session, in order to calculate results.
	 */
	public List execute(Set facts) {

		// Get the compiled rules.
		initKnowledgeBase();

		log.info("Rule session");
		if (knowledgeBase == null) {
			throw new IllegalStateException("KnowledgeBase not initialised");
		}

		final StatelessKnowledgeSession session = knowledgeBase.newStatelessKnowledgeSession();
		session.addEventListener(new EventListener());
		final WorkingMemoryInsertionLogger workingMemoryLogger = new WorkingMemoryInsertionLogger();
		session.addEventListener(workingMemoryLogger);

		final long start = System.currentTimeMillis();
		final List<Command> commands = new ArrayList<Command>();
		commands.add(CommandFactory.newInsertElements(facts));
		commands.add(CommandFactory.newFireAllRules());
		commands.add(CommandFactory.newQuery(RESULTS_QUERY, "results"));
		final ExecutionResults executionResults = session.execute(CommandFactory.newBatchExecution(commands));

		results = new ArrayList();
		final QueryResults queryResults = (QueryResults) executionResults.getValue(RESULTS_QUERY);
		final double duration = (System.currentTimeMillis() - start) / 1000;
		log.info(String.format("%d results found in %d ms", queryResults.size(), System.currentTimeMillis() - start));
		for (final QueryResultsRow row : queryResults) {
			final Object result = row.get(QUERY_ROW_VALUE);
         results.add(result);
			log.info(String.format("Result: %s", result.toString()));
		}

		workingMemoryLogger.log();
      return results;
	}
}
