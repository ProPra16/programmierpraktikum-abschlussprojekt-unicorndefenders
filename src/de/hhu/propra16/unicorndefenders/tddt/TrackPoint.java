package de.hhu.propra16.unicorndefenders.tddt;

/**
 * Created by Alessa on 08.07.16.
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
