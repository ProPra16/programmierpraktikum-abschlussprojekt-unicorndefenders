package de.hhu.propra16.unicorndefenders.tddt;

/**
 * Speicherung von Phasen mit zug. Zeiten zur Uebergabe vom Controller an Tracking
 *
 * @author Alessandra
 */
public class TrackPoint {

   private long time;
   private Cycle cycle;

   public TrackPoint(long time, Cycle cycle){
      this.cycle = cycle;
      this.time = time;
   }

   public Cycle getCycle() {
      return cycle;
   }

   public long getTime() {
      return time;
   }
}
