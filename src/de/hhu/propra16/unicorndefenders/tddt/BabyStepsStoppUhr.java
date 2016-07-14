package de.hhu.propra16.unicorndefenders.tddt;

/**
 * Klasse zur Zeitmessung für die Highscore-Listen von BabySteps
 *
 * @author Eyyüp
 */

public class BabyStepsStoppUhr {
    public static long beginn=0;     // Startzeit
    public static long ende=0;       // Endzeit


    // Gesamtzeit, die für eine Aufgabe benötigt wird
    // wenn eine andere Aufgabe, für die BabySteps aktiviert ist, gewählt wird,
    // wird diese Gesamtzeit auf 0 gesetzt

    public static long gesamtzeit=0;

    // misst die Startzeit
    public static void starten(){
        beginn = System.currentTimeMillis();
    }

    // misst die Endzeit und aktualisiert die Gesamtzeit, die für eine Aufgabe benötigt wurde
    public static void beenden() {

        ende = System.currentTimeMillis();
        long time = 0;

        if (beginn != 0 && ende != 0) {
            time = ende - beginn;
        }

        gesamtzeit = gesamtzeit + time;
    }

    // wandelt die Gesamtzeit in einen String des gewünschten Formats um
    public static String highscoreString(long milliseconds){

        String highscore = "";

        long seconds=milliseconds/1000; // umrechnen in Sekunden
        long min=seconds/60;            // Anzahl Minuten
        long sec= seconds%60;           // Anzahl Sekunden

        if(sec<10) highscore=min+":0"+sec;
        else highscore=min+":"+sec;

        return highscore;
    }
}

