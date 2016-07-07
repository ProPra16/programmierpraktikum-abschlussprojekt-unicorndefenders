package de.hhu.propra16.unicorndefenders.tddt;

/**
 * Created by Sebastian on 04.07.2016.
 */
public class ClassNameExtractor {
   public static String getClassName(String input) {
      String temp="";
      if(!input.contains("class")) return null;
      int i = input.indexOf("class");
      if ((i+6)>input.length()) return null;
      if (input.charAt(i+5)!=' ') return null;
      while(input.charAt(i+6)!=' '&&input.charAt(i+6)!='{') {
         temp+=input.charAt(i+6);
         i++;
      }

      return temp;
   }
}
