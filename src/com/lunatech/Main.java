package com.lunatech;

import com.lunatech.drools.RulesService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Main {

   private final static Logger log = Logger.getLogger(Main.class);

   public static void main(String[] args) throws IOException {
      final Set<Beer> beers = Beer.load("/beer.tabs.csv");
      log.info(String.format("%d beers loaded", beers.size()));

      final RulesService rules = new RulesService("beer.drl");
      rules.execute(beers);
   }
}
