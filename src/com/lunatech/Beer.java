package com.lunatech;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable model for beer.
 */
public class Beer {

   private final static Logger log = Logger.getLogger(Beer.class);

   private final String name;
   private final String type;
   private final float strength;
   private final String brewery;

   public Beer(String name, String type, float strength, String brewery) {
      this.name = name;
      this.type = type;
      this.strength = strength;
      this.brewery = brewery;
   }

   public String getName() {
      return name;
   }

   public String getType() {
      return type;
   }

   public float getStrength() {
      return strength;
   }

   public String getBrewery() {
      return brewery;
   }

   /**
    * Returns a list of beers, loaded from a tab-separated file at the given classpath path.
    */
   public static Set<Beer> load(String filePath) throws IOException {
      final Set<Beer> beers = new HashSet<Beer>();
      final InputStream input = Beer.class.getResourceAsStream(filePath);
      final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      String line;
      while ((line = reader.readLine()) != null) {
         final String[] columns = line.split("\t");
         if (columns.length == 4) {
            final String name = columns[0];
            final String type = columns[1];
            final String strength = columns[2].trim().replaceAll("%", "");
            final Float strengthValue = strength.equals("") ? 0 : Float.valueOf(strength);
            final String brewery = columns[3];
            beers.add(new Beer(name, type, strengthValue, brewery));
         }
         else {
            log.warn("Invalid beer data: " + line);
         }
      }
      return beers;
   }
}
