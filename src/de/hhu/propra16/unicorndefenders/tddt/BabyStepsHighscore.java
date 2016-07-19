package de.hhu.propra16.unicorndefenders.tddt;

import javafx.scene.control.TextInputDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Klasse zum Anlegen und Sortieren der BabySteps-Highscore-Listen
 *
 * @author Eyyüp
 */

public class BabyStepsHighscore {

    // Name der Aufgabe, für die die Highscore-Zeit gemessen wird
    public static String aufgabe;

    // Liste, die die Highscores im gewünschten String-Format enthälz
    // und an ein Objekt vom Typ BabyStepsHighscoreStage übergeben wird
    public static List<String> highscorestrings = new ArrayList<>();


    // Lege eine Highscore-Textdatei für jede BabySteps-Aufgabe an, falls nicht bereits existent
    public static void createFile(String dateiname) {

        // erhalte den relativen Pfad des Projekt-Ordners
        File f = new File("");
        String relPfad = f.getAbsolutePath();

        // relativer Pfad der Highscore-Textdatei, die erzeugt werden soll, wenn nicht bereits existent
        String pfad = relPfad + "//BabyStepsHighscore-Listen//" + dateiname + ".txt";

        Path path = Paths.get(pfad);

        // wenn die Datei nicht existiert, wird eine leere Datei für den Highscore angelegt
        // der Dateiname ist der Aufgabenname
        if (!Files.exists(path)) {
            Path p = null;
            p = Paths.get(pfad);
            ArrayList<String> output = new ArrayList<String>();
            try {
                Files.write(p, output);
            } catch (final IOException ex) {
                System.out.println("IOException");
            }
        }
    }


    // zum Anlegen und Sortieren von Highscorelisten
    public static void handling() {

        Controller.stopThread = true; //stoppe den Timer, wenn die Highscorezeit eingetragen werden soll

        // Liste mit den Highscores aus der Datei und dem neuen hinzuzufügenden Highscore
        List<HighScore> highscoreliste = new ArrayList<>();

        // Sortierte Liste mit den Highscores aus der Datei und dem neuen hinzuzufügenden Highscore
        List<HighScore> highscorelisteSorted = new ArrayList<>();

        // erhalte den relativen Pfad des Projekt-Ordners
        File f = new File("");
        String relPfad = f.getAbsolutePath();

        // relativer Pfad der erzeugten Highscore-Textdatei
        String pfad = relPfad + "//BabyStepsHighscore-Listen//" + aufgabe + ".txt";


        //lese bereits existierende Highscores aus der Datei ein
        try {
            Scanner sc = new Scanner(new File(pfad));

            while (sc.hasNextLine()) {

                // lese Zeilen ein
                String line = sc.nextLine();

                // als Trennzeichen zwischen Namen und Sekundenanzahl wird ein Leerzeichen vewendet
                // trenne daher die Zeile nach dem Leerzeichen
                String[] words = line.split(" ");

                // wandle den zweiten Token einer Zeile in eine long-Variable um
                long sekunden= Long.parseLong(words[1]);

                // speichere den Highscore-Eintrag in einem Objekt und füge ihn der Highscoreliste hinzu
                HighScore h = new HighScore(words[0], sekunden);
                highscoreliste.add(h);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String puffer = "";

        // der neu hinzukommende Eintrag wird in einem Text-Input-Dialog erfragt
        TextInputDialog namensabfrage = new TextInputDialog("");
        namensabfrage.setTitle("Highscore-Name");
        namensabfrage.setContentText("Geben Sie bitte Ihren Namen für die Highscore-Liste ein:");
        Optional<String> eingabe = namensabfrage.showAndWait();

        // wenn eine Eingabe getätigt wird, wird diese als Name des Benutzers in der Highscoreliste gespeichert
        // wenn nichts eingegeben wird, geht der Benutzer als "unknown" in die Liste ein
        if (eingabe.isPresent()) {
            puffer = eingabe.get();
        }
        else{
            puffer="unknown";
        }

        // der neue Eintrag wird in einem HighScore-Objekt gespeichert und der Liste hinzugefügt
        HighScore h = new HighScore(puffer, BabyStepsStoppUhr.gesamtzeit);
        highscoreliste.add(h);

        //sortiere die Liste absteigend nach der benötigten Zeit und speichere sie in einer anderen Liste
        int size = highscoreliste.size();
        for (int j = 0; j < size; j++) {
            int minIndex = 0;
            for (int i = 1; i < highscoreliste.size(); i++) {
                long newnumber = highscoreliste.get(i).secs;
                if ((newnumber < highscoreliste.get(minIndex).secs)) {
                    minIndex = i;
                }
            }
            highscorelisteSorted.add(highscoreliste.get(minIndex));
            highscoreliste.remove(minIndex);
        }

        // die sortierte Liste wird in die zuvor gelesene Datei schreiben
        Path p = null;
        p = Paths.get(pfad);
        ArrayList<String> output = new ArrayList<String>();

        // nur die Top-5 werden betrachtet
        int a = Math.min(5, highscorelisteSorted.size());

        for (int k = 0; k < a; k++) {
            output.add(highscorelisteSorted.get(k).name + " " + Long.toString(highscorelisteSorted.get(k).secs));
        }

        try {
            Files.write(p, output);
        } catch (final IOException ex) {
            System.out.println("IOException");
        }


        // bisher enthielt die Datei die Highscores mit einer Millisekundenangabe und dem Namen des Nutzers
        // nun erfolgt die Umrechnung der Millisekunden in das gewünschte Format mm:ss
        // füge diese Einträge dann in eine String-Liste ein
        for (int i = 0; i < a; i++) {
            long temp=highscorelisteSorted.get(i).secs;
            String s=highscorelisteSorted.get(i).name + " "+BabyStepsStoppUhr.highscoreString(temp);
            highscorestrings.add(s);
        }

    }
}
