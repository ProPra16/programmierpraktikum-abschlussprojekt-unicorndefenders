package de.hhu.propra16.unicorndefenders.tddt.files;

import de.hhu.propra16.unicorndefenders.tddt.config.ReallyExistingFile;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Eine physisch auf dem Dateisystem vorhandene Datei.
 * (Bis jetzt nur eine Wrapper-Klasse fuer java.io.File)
 *
 * @author Pascal
 */
public class FilesystemFile extends File implements ReallyExistingFile {

   /**
    * Dateiname
    */
   protected String nameInFilesystem;

   /**
    * Java-File Objekt der Datei
    */
   protected FileInputStream file;

   /**
    * Konstruktor.
    *
    * @param nameInFilesystem Dateiname
    */
   public FilesystemFile(String nameInFilesystem) throws FileNotFoundException {
      this.nameInFilesystem = nameInFilesystem;
      file = new FileInputStream(nameInFilesystem);
      //file = new java.io.File(nameInFilesystem);
   }

   /**
    * Liefert das File-Objekt.
    *
    * @return
    */
   public InputSource getFileObject() {
      return new InputSource(file);
   }

}
