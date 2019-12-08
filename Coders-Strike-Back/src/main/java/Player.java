import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

    private static final int seuilDistanceFreinage = 3000;

    private static int nextCheckpointX;
    private static int nextCheckpointY;
    private static int nextCheckpointDist;
    private static int nextCheckpointAngle;
    private static int power;
    private static int decrement = 5;
    private static int increment = 5;
    private static int powerMini = 30;
    private static int seuilBigAngle = 100;
    private static int seuilSmallAngle = 45;

    // Coordonnees / distance
    final static Map<Integer, Target> mapOfTargets = new HashMap<>();
    final static Set<String> setOfCoords = new HashSet<>();

    public static void main(final String args[]) {
        final Scanner in = new Scanner(System.in);

        power = 100;
        Target lastTarget = null;

        // game loop
        while (true) {
            // Real position
            final int x = in.nextInt();
            final int y = in.nextInt();

            // checkpoint
            nextCheckpointX = in.nextInt(); // x position of the next check point
            nextCheckpointY = in.nextInt(); // y position of the next check point
            nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            // angle between your pod orientation and the direction of the next Enemy
            nextCheckpointAngle = in.nextInt();
            final int opponentX = in.nextInt();
            final int opponentY = in.nextInt();

            // Targets
            Target nextTarget = majTargets(lastTarget, nextCheckpointX, nextCheckpointY);

            play(new Target(0, x, y), nextTarget);
            // playSimple(nextCheckpointX, nextCheckpointY);
            // System.err.println(nextTarget);
            // System.err.println(new Target(0, x, y));
            // System.err.println(nextTarget.targetApres);
            // System.err.println("CalculAngle:" + calculAngle(nextTarget, new Target(0, x, y), nextTarget.targetApres));
            lastTarget = nextTarget;

        }
    }

    /**
     * A partir de la Target précédente et des coordonnées visés, détermine si il
     * s'agit d'une Target connue ou inconnue.
     * 
     * @param lastTarget
     * @param x
     * @param y
     * @return
     */
    public static Target majTargets(Target lastTarget, int x, int y) {

        final String nextCoord = x + "-" + y;
        Target result = null;
        if (!setOfCoords.contains(nextCoord)) {
            // Ajout d'une nouvelle Target dans les references
            setOfCoords.add(nextCoord);
            Target target = new Target(setOfCoords.size(), x, y);
            mapOfTargets.put(target.name, target);
            target.distanceNext = nextCheckpointDist;
            target.targetAvant = lastTarget;
            result = target;
        } else {
            // La Target existe déjà dans les références
            if (lastTarget != null && lastTarget.x == x && lastTarget.y == y) {
                // Si on continue la visée sur lastTarget, rien à faire
                return lastTarget;
            } else {
                // On a changé de Target, mise à jour si possible
                result = mapOfTargets.get(lastTarget.name + 1);
                if (result == null) {
                    result = mapOfTargets.get(1);
                    result.targetAvant = lastTarget;
                    result.targetApres = mapOfTargets.get(result.name + 1);
                } else {
                    result.targetApres = findPreviousTarget(result);
                }
            }
        }

        if (result != null && (result.angleNext == null || result.angleNext == 0)) {
            calculAngle(result);
        }
        return result;
    }

    public static Target findPreviousTarget(Target target) {
        for (Map.Entry<Integer, Target> entry : mapOfTargets.entrySet()) {
            Target iTarget = entry.getValue();

            if (iTarget.targetAvant != null && target.name == iTarget.targetAvant.name) {
                return entry.getValue();
            }
        }

        return null;
    }

    public static void calculAngle(Target target) {
        if (target != null) {
            target.angleNext = calculAngle(target.targetApres, target, target.targetAvant);
        }
    }

    public static void playSimple(int x, int y) {
        play(x, y, 90, "simple");
    }

    public static void play(Target myPosition, Target prochaineCible) {
        // Freinage : quand on arrive proche du prochain point ou que l'angle devient
        // trop grand
        int nextAngle = calculAngle(prochaineCible, myPosition, prochaineCible.targetApres);

        if (nextCheckpointDist <= seuilDistanceFreinage) {
            // Premier freinage si à l'approche mais pas trop
            if (power - decrement >= powerMini) {
                power -= decrement;
                // System.err.println("Freinage approche " + power);
                // Deuxième freinage si proche et prochain angle serré
                if (Math.abs(nextAngle) < seuilBigAngle) {
                    if (power - decrement >= powerMini) {
                        power -= decrement;
                        // System.err.println("Freinage angle serré " + power);
                    }
                }
                // troisième freinage si proche et prochaine distance courte
                if (prochaineCible.distanceNext < seuilDistanceFreinage) {
                    if (power - decrement >= powerMini) {
                        power -= decrement;
                        // System.err.println("Freinage prochaine distance courte " + power);
                    }
                }
            }
            String infos = "down" + " " + power + " vers: " + prochaineCible.name + " / " + prochaineCible.angleNext
                    + "°";
            infos = "NextAngle:" + nextAngle;
            play(nextCheckpointX, nextCheckpointY, power, infos);
            return;
        }

        // Accélération : lorsqu'on est loin du prochain point
        if (nextCheckpointDist > seuilDistanceFreinage && Math.abs(nextAngle) > seuilSmallAngle) {
            if (power + increment <= 100) {
                power += increment;
                // System.err.println("Accélération loin " + power + " " + nextCheckpointAngle);
            }
            // Deuxième accélération prochain angle large
            if (Math.abs(nextAngle) > seuilBigAngle) {
                if (power + increment <= 100) {
                    power += increment;
                    // System.err.println("Accélération grand angle" + power);
                }
            }
            // troisième accélération si proche et prochaine distance courte
            if (prochaineCible.distanceNext > 2 * seuilDistanceFreinage) {
                if (power + increment <= 100) {
                    power += increment;
                    // System.err.println("Accélération prochaine distance longue " + power);
                }
            }

            String infos = "up" + " " + power + " vers: " + prochaineCible.name + " / " + prochaineCible.angleNext
                    + "°";
            infos = "NextAngle:" + nextAngle;
            play(nextCheckpointX, nextCheckpointY, power, infos);
            return;
        }

        power = 100;
        String infos = String.valueOf(power) + " vers: " + prochaineCible.name + " / " + prochaineCible.angleNext + "°";
        infos = "NextAngle:" + nextAngle;
        play(nextCheckpointX, nextCheckpointY, power, infos);

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
        if (distAB != null && distAC != null) {
            Double result = Math
                    .toDegrees(Math.acos(calculProduitScalaire(pointA, pointB, pointC) / (distAB * distAC)));
            // System.err.println("Angle:" + result);
            return result.intValue();
        }
        return 0;
    }

    public static Double calculDistance(final Target point1, final Target point2) {
        if (point1 != null && point2 != null) {
            final Double distX = Double.valueOf(point2.x - point1.x);
            final Double distY = Double.valueOf(point2.y - point1.y);
            return Math.sqrt(distX * distX + distY * distY);
        }
        return null;
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
    public Integer angleNext;
    public int distanceNext;
    public Target targetAvant;
    public Target targetApres;

    public Target(final int name, final int x, final int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String toCoord() {
        return this.x + "-" + this.y;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" x:" + this.x + " y:" + this.y);
        sb.append(" angle:" + angleNext);
        sb.append(" av:" + (this.targetAvant != null ? this.targetAvant.name : "-"));
        sb.append(" ap:" + (this.targetApres != null ? this.targetApres.name : "-"));
        return sb.toString();
    }

}
