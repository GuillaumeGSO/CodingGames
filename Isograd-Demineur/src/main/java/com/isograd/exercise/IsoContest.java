/*******
 * Read input from System.in
 * Use System.out.println to ouput your result.
 * Use:
 *  IsoContestBase.localEcho( variable)
 * to display variable in a dedicated area.
 * ***/
package com.isograd.exercise;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IsoContest {

    public static int ACC = 0;

    public static boolean LOG = false;

    public static void main(String[] argv) throws Exception {

        Scanner sc = new Scanner(System.in);
        int H = sc.nextInt();
        sc.nextLine();
        log("H:" + H);
        List<String> lines = new ArrayList<String>(H);
        int L = sc.nextInt();
        sc.nextLine();
        log("L:" + L);
        while (sc.hasNextLine()) {
            /* Lisez les donnees et effectuez votre traitement */
            lines.add(sc.nextLine());
        }

        int indexLigne = 0;
        int indexCol = 0;
        /*
         * Vous pouvez aussi effectuer votre traitement une fois que vous avez
         * lu toutes les donnees.
         */
        for (int l = 0; l < H; l++) {
            String str = lines.get(l);
            log(str);
            if (str.contains("x")) {
                indexLigne = l;
                indexCol = str.indexOf("x");
                ACC = findFin(str, indexCol) - findDebut(str, indexCol) + 1;
            }
        }

        System.out.println(calc(lines, indexLigne, indexCol));

    }

    private static String calc(List<String> lines, int indexLigne, int indexCol) {
        // Vers le haut
        String str = lines.get(indexLigne);
        int newDebCol = findDebut(str, indexCol);
        int newFinCol = findFin(str, indexCol);
        log("Ligne numero " + indexLigne + " : " + str + " Deb:" + newDebCol + " Fin:" + newFinCol);

        for (int l = indexLigne - 1; l >= 0; l--) {
            str = lines.get(l).subSequence(newDebCol, newFinCol + 1).toString();
            if (str.length() >= 3) {
                newDebCol = findDebut(str, str.length() - 1);
                newFinCol = findFin(str, 0);

                int lng = newFinCol - newDebCol + 1;

                if (lng >= 3) {
                    ACC += lng;
                    log(lng);
                } else {
                    break;
                }
            }
        }

        // vers le bas
        str = lines.get(indexLigne);
        newDebCol = findDebut(str, indexCol);
        newFinCol = findFin(str, indexCol);

        for (int l = indexLigne + 1; l < lines.size(); l++) {
            str = lines.get(l).subSequence(newDebCol, newFinCol + 1).toString();
            if (str.length() >= 3) {
                newDebCol = findDebut(str, str.length() - 1);
                newFinCol = findFin(str, 0);

                int lng = newFinCol - newDebCol + 1;

                if (lng >= 3) {
                    ACC += lng;
                    log(lng);
                } else {
                    break;
                }
            }
        }

        return String.valueOf(ACC);
    }

    private static int findDebut(String str, int index) {
        // Vers la gauche
        for (int i = index; i >= 0; i--) {
            if (str.charAt(i) == '*') {
                return i + 1;
            }
        }
        return 0;
    }

    private static int findFin(String str, int index) {
        // Vers la droite
        for (int i = index; i < str.length(); i++) {
            if (str.charAt(i) == '*') {
                return i - 1;
            }
        }
        return str.length() - 1;
    }

    private static void log(String variable) {
        if (LOG) {
            // IsoContestBase.localEcho( variable);
            System.err.println(variable);
        }
    }

    private static void log(int variable) {
        if (LOG) {
            // IsoContestBase.localEcho( String.valueOf(variable));
            System.err.println(String.valueOf(variable));
        }
    }

}
