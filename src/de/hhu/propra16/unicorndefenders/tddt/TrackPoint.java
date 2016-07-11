package de.hhu.propra16.unicorndefenders.tddt;

import de.hhu.propra16.unicorndefenders.tddt.files.File;

/**
 * Speicherung von Phasen mit zug. Zeiten zur Uebergabe vom Controller an Tracking
 *
 * @author Alessandra
 */
public class TrackPoint {

   private long time;
   private Cycle cycle;
   private File code;
   private File test;

   public TrackPoint(long time, Cycle cycle, File code, File test){
      this.cycle = cycle;
      this.time = time;
      this.code = code;
      this.test = test;
   }

   public Cycle getCycle() {
      return cycle;
   }

   public long getTime() {
      return time;
   }

   public File getTest() {
      return test;
   }

   public File getCode() {
      return code;
   }
}
