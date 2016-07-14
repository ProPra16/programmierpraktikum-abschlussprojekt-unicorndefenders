package de.hhu.propra16.unicorndefenders.tddt;

/**
 * Objekt vom Typ HighScore enthält den Highscore in Sekunden und Namen des Benutzers
 *
 * @author Eyyüp
 */

public class HighScore {

    public String name; // Name eines Highscore-Eintrags

    public long secs;   // Anzahl MilliSekunden eines Highscore-Eintrags

    public HighScore(String name, long secs){

        this.name=name;
        this.secs=secs;
    }
}
