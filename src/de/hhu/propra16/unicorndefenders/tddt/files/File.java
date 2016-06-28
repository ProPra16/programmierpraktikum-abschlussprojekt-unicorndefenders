package de.hhu.propra16.unicorndefenders.tddt.files;

/**
 * Eine virtuelle Datei.
 *
 * Diese Klasse kann innerhalb der Anwendung verwendet werden um verschiedene Arten von
 * Dateien abzubilden.
 *
 * Diese Klasse wird erweitert von der Klasse PhysicalFile. Diese kann verwendet werden um
 * Dateien physisch auf dem Dateisystem zu verwalten.
 *
 * @author Pascal
 */
public class File {

   /**
    * Der logische Name einer Datei.
    *
    * Da diese Datei nicht physisch auf dem Dateisystem vorhanden ist, kann hier
    * jeder String verwendet werden, den Java akzeptiert.
    *
    * Beachte: In der Klasse PhysicalFile muss auf Namenseinschraenkungen bzgl. des
    * Dateisystems geachtet werden.
    */
   protected String logicalName;

   /**
    * Der Inhalt der Datei.
    */
   protected String content;

   /**
    * Konstruktor.
    *
    * @param logicalName Der logische Name der Datei
    * @param content Inhalt der Datei
    */
   public File(String logicalName, String content) {
      this.logicalName = logicalName;
      this.content = content;
   }

   public File(){}
}
