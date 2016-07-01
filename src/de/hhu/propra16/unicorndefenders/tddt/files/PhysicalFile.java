package de.hhu.propra16.unicorndefenders.tddt.files;

/**
 * Eine physisch auf dem Dateisystem vorhandene Datei.
 * (Bis jetzt nur eine Wrapper-Klasse fuer java.io.File)
 *
 * @author Pascal
 */
public class PhysicalFile extends File {

   /**
    * Dateiname
    */
   protected String nameInFilesystem;

   /**
    * Java-File Objekt der Datei
    */
   protected java.io.File file;

   /**
    * Konstruktor.
    *
    * @param nameInFilesystem Dateiname
    */
   public PhysicalFile(String nameInFilesystem) {
      this.nameInFilesystem = nameInFilesystem;
      file = new java.io.File(nameInFilesystem);
   }

   /**
    * Liefert das File-Objekt.
    *
    * @return
    */
   public java.io.File getFileObject() {
      return file;
   }

}
