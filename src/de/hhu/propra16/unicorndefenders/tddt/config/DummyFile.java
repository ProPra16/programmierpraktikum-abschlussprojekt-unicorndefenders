package de.hhu.propra16.unicorndefenders.tddt.config;

import org.xml.sax.InputSource;

import java.io.StringReader;

/**
 * Dateisimulation fuer Tests.
 *
 * @author Pascal
 */
public class DummyFile implements ReallyExistingFile {
   private String content;

   public DummyFile(String content) {
      this.content = content;
   }

   public InputSource getFileObject() {
      return new InputSource(new StringReader(content));
   }
}
