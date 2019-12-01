import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

    public static void main(final String args[]) {
        final Scanner in = new Scanner(System.in);

        int power = 100;
        final int seuilDistanceFreinage = 3000;
        final int seuilDistanceBoost = 3 * seuilDistanceFreinage;
        final int seuilBigAngle = 120;
        final int seuilSmallAngle = 45;
        final int nbBoost = 2;
        final int decrement = 5;
        final int increment = 5;
        final int seuil = 30;
        int numeroBase = 1;

        // Coordonnees / distance
        final Map<Integer, Target> mapOfTargets = new HashMap<>();
        final Set<String> setOfCoords = new HashSet<>();

        // game loop
        while (true) {
            // Real position
            final int x = in.nextInt();
            final int y = in.nextInt();

            // checkpoint
            final int nextCheckpointX = in.nextInt(); // x position of the next check point
            final int nextCheckpointY = in.nextInt(); // y position of the next check point
            final int nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            final int nextCheckpointAngle = Math.abs(in.nextInt()); // angle between your pod orientation and the
                                                                    // direction of the
            // next
            // Enemy
            final int opponentX = in.nextInt();
            final int opponentY = in.nextInt();

            // Targets
            final Target nextTarget = new Target(numeroBase, nextCheckpointX, nextCheckpointY);
            final String nextCoord = nextTarget.toCoord();

            // Construction de la map des points / distances : a chaque detection d'une
            // nouvelle prochaine base
            if (!setOfCoords.contains(nextCoord)) {
                mapOfTargets.put(numeroBase, nextTarget);
                // On n'initialise la distance qu'à la création
                nextTarget.distTo = nextCheckpointDist;

                nextTarget.distNext = 1000;

                setOfCoords.add(nextTarget.toCoord());

                // System.err.println("nouvelle : " + nextTarget);

                if (mapOfTargets.get(numeroBase - 1) != null) {
                    mapOfTargets.get(numeroBase - 1).distNext = mapOfTargets.get(numeroBase).distTo;
                }
                numeroBase++;
            }

            int numeroProchaineCible = calculRangProchaineCible(nextCoord, mapOfTargets);
            Target prochaineCible = mapOfTargets.get(numeroProchaineCible);

            // TODO : ne plus faire ça si tout est trouvé allComplete
            if (numeroProchaineCible > 0) {

                // Calcul de l'angle entre la source, la cible et la suivante
                int index = numeroProchaineCible;
                int indexAvant = index - 1;
                int indexApres = index + 1;

                if (numeroProchaineCible == 1 && mapOfTargets.size() > 1) {
                    // System.err.println("avant: " + indexAvant);
                    indexAvant = mapOfTargets.size();
                    // System.err.println("avant: " + indexAvant);
                }
                if (numeroProchaineCible > 2 && indexApres > mapOfTargets.size()) {
                    // System.err.println("apres: " + indexApres);
                    indexApres = 1;
                    // System.err.println("apres: " + indexApres);
                }

                // System.err.println("avant: " + indexAvant + " " +
                // mapOfTargets.get(indexAvant));
                // System.err.println("index: " + index + " " + mapOfTargets.get(index));
                // System.err.println("apres: " + indexApres + " " +
                // mapOfTargets.get(indexApres));
                if (mapOfTargets.get(indexAvant) != null && mapOfTargets.get(indexApres) != null) {
                    mapOfTargets.get(indexAvant).angleNext = calculAngle(mapOfTargets.get(indexAvant), prochaineCible,
                            mapOfTargets.get(indexApres));
                }
                // System.err.println("Cible: " + prochaineCible);
            }

            // Freinage : quand on arrive proche du prochain point ou que l'angle devient
            // trop grand
            if (nextCheckpointDist <= seuilDistanceFreinage) {
                // Premier freinage si à l'approche mais pas trop
                if (power - decrement >= 2 * seuil) {
                    power -= decrement;
                    System.err.println("Freinage approche " + power);
                    // Deuxième freinage si proche et prochain angle serré
                    if (prochaineCible.angleNext != null && Math.abs(prochaineCible.angleNext) < seuilBigAngle) {
                        if (power - 2 * decrement >= seuil) {
                            power -= 2 * decrement;
                            System.err.println("Freinage angle serré " + power);
                        }
                    }
                    // troisième freinage si proche et prochaine distance courte
                    if (prochaineCible.distNext < 2 * seuilDistanceFreinage) {
                        if (power - decrement >= seuil) {
                            power -= decrement;
                            System.err.println("Freinage prochaine distance courte " + power);
                        }
                    }
                }
                play(nextCheckpointX, nextCheckpointY, power,
                        "down" + " " + power + " vers: " + prochaineCible.name + "/" + prochaineCible.angleNext);
                continue;
            }
            // Accélération : lorsqu'on est loin du prochain point
            if (nextCheckpointDist > seuilDistanceFreinage && nextCheckpointAngle < seuilSmallAngle) {
                if (power + increment <= 100) {
                    power += increment;
                    System.err.println("Accélération loin " + power + " " + nextCheckpointAngle);
                }
                // Deuxième accélération prochain angle large
                if (prochaineCible.angleNext != null && Math.abs(prochaineCible.angleNext) > seuilBigAngle) {
                    if (power + increment <= 100) {
                        power += increment;
                        System.err.println("Accélération grand angle" + power);
                    }
                }
                // troisième accélération si proche et prochaine distance courte
                if (prochaineCible.distNext > 2 * seuilDistanceFreinage) {
                    if (power + increment <= 100) {
                        power += increment;
                        System.err.println("Accélération prochaine distance longue " + power);
                    }
                }
                play(nextCheckpointX, nextCheckpointY, power,
                        "up" + " " + power + " vers: " + prochaineCible.name + "/" + prochaineCible.angleNext);
                continue;
            }

            // power = 100;
            play(nextCheckpointX, nextCheckpointY, power,
                    String.valueOf(power) + " vers: " + prochaineCible.name + "/" + prochaineCible.angleNext);

        }
    }

    private static int calculRangProchaineCible(String nextCoord, Map<Integer, Target> mapOfTargets) {
        if (mapOfTargets != null) {
            for (Integer key : mapOfTargets.keySet()) {
                if (nextCoord.equals(mapOfTargets.get(key).toCoord())) {
                    return key;
                }
            }
        }
        return 0;
    }

    public static void play(final int nextCheckpointX, final int nextCheckpointY, final int power,
            final String leReste) {
        System.out.println((int) (nextCheckpointX) + " " + (int) (nextCheckpointY) + " " + (int) power + " " + leReste);
    }

    public static int calculAngle(final Target pointA, final Target pointB, final Target pointC) {

        // System.err.println("CalculAngle:" + pointA);
        // System.err.println("CalculAngle:" + pointB);
        // System.err.println("CalculAngle:" + pointC);
        final Double distAB = calculDistance(pointA, pointB);
        final Double distAC = calculDistance(pointA, pointC);
        Double result = Math.toDegrees(Math.acos(calculProduitScalaire(pointA, pointB, pointC) / (distAB * distAC)));
        // System.err.println("Angle:" + result);
        return result.intValue();
    }

    public static Double calculDistance(final Target point1, final Target point2) {
        final Double distX = Double.valueOf(point2.x - point1.x);
        final Double distY = Double.valueOf(point2.y - point1.y);
        return Math.sqrt(distX * distX + distY * distY);
    }

    /**
     * Calcul du produit scalaire AB.AC
     * 
     * @param pointA
     * @param pointB
     * @param pointC
     * @return
     */
    public static Double calculProduitScalaire(final Target pointA, final Target pointB, final Target pointC) {
        final Double distAB_X = Double.valueOf(pointB.x - pointA.x);
        final Double distAC_X = Double.valueOf(pointC.x - pointA.x);

        final Double distAB_Y = Double.valueOf(pointB.y - pointA.y);
        final Double distAC_Y = Double.valueOf(pointC.y - pointA.y);

        return (distAB_X * distAC_X) + (distAB_Y * distAC_Y);
    }
}

class Target {
    public int name;
    public int x;
    public int y;
    public int distTo;
    public int distNext;
    public Integer angleNext;

    public Target(final int name, final int x, final int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String toCoord() {
        return this.x + "-" + this.y;
    }

    public String toString() {
        return "Name: " + name + " x:" + this.x + " y:" + this.y + " to:" + this.distTo + " next:" + this.distNext
                + " angle: " + angleNext;
    }

    public boolean isComplete() {
        return x > 0 && y > 0 && distTo > 0 && distNext > 0 && angleNext != null;
    }

}
