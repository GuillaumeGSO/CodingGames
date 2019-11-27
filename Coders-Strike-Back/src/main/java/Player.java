import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        int power = 100;
        int seuilDistanceFreinage = 1400;
        int seuilDistanceBoost = 3 * seuilDistanceFreinage;
        int seuilBigAngle = 45;
        int seuilSmallAngle = 20;
        int nbBoost = 2;
        int decrement = 20;
        int increment = 20;
        int seuil = 10;
        int nbTours = 0;
        int nbBases = 0;

        // Coordonnees / distance
        Map<String, Coord> map = new HashMap<>();
        List<Coord> coords = new ArrayList<>();

        boolean premierTour = true;
        // game loop
        while (true) {

            int x = in.nextInt();
            int y = in.nextInt();
            int nextCheckpointX = in.nextInt(); // x position of the next check point
            int nextCheckpointY = in.nextInt(); // y position of the next check point
            int nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            int nextCheckpointAngle = in.nextInt(); // angle between your pod orientation and the direction of the next
                                                    // checkpoint
            int opponentX = in.nextInt();
            int opponentY = in.nextInt();
            Coord coord = new Coord(nextCheckpointX, nextCheckpointY);
            String coordStr = coord.toString();

            // Construction de la map des points / distances
            if (map.get(coordStr) == null) {
                map.put(coordStr, coord);
                coords.add(coord);
                nbBases++;
                System.err.println("nouvelle : " + coord);
                //FIXME utliser coord.equals...
            }
            // A la fin du premier tour, on connait toutes les positions et distances
            // pour améliorer la trajectoire pour les tours suivants
            if (coords.size() >= 3) {
                System.err.println(calculAngle(coords.get(1), coords.get(0), coords.get(2)));
            }    


            if (premierTour) {
                // Freinage : quand on arrive proche du prochain point ou que l'angle devient
                // trop grand
                if (nextCheckpointDist <= seuilDistanceFreinage || Math.abs(nextCheckpointAngle) >= seuilBigAngle) {
                    if (power - decrement >= seuil) {
                        power -= decrement;
                    }
                    choose(nextCheckpointX, nextCheckpointY, power,
                            "down" + " " + nextCheckpointDist + " " + nextCheckpointAngle);
                    continue;
                }

                // Accéleration : lorsqu'on est loin du prochain point et que l'angle est faible
                if (nextCheckpointDist > seuilDistanceFreinage && Math.abs(nextCheckpointAngle) <= seuilBigAngle) {
                    if (power + increment <= 100) {
                        power += increment;
                    }
                    choose(nextCheckpointX, nextCheckpointY, power,
                            "up" + " " + nextCheckpointDist + " " + nextCheckpointAngle);
                    continue;
                }
                power = 100;
                choose(nextCheckpointX, nextCheckpointY, power, "max");

            } else {
                power = 100;
                System.err.println(map);
                choose(nextCheckpointX, nextCheckpointY, power, "Fin du premier tour !");
            }
        }
    }

    public static void choose(int nextCheckpointX, int nextCheckpointY, int power, String leReste) {
        System.out.println((int) (nextCheckpointX) + " " + (int) (nextCheckpointY) + " " + (int) power + " " + leReste);
    }

    public static Double calculAngle(Coord pointA, Coord pointB, Coord pointC) {
        Double distAB = calculDistance(pointA, pointB);
        Double distAC = calculDistance(pointA, pointC);
        return Math.toDegrees(Math.acos(calculProduitScalaire(pointA, pointB, pointC)/(distAB * distAC)));
    }

    public static Double calculDistance(Coord point1, Coord point2) {
        Double distX = Double.valueOf(point2.x - point1.x);
        Double distY = Double.valueOf(point2.y - point1.y);
        return Math.sqrt(distX*distX + distY*distY);
    }

    /**
     * Calcul du produit scalaire AB.AC
     * @param pointA
     * @param pointB
     * @param pointC
     * @return
     */
    public static Double calculProduitScalaire(Coord pointA, Coord pointB, Coord pointC) {
        Double distAB_X = Double.valueOf(pointB.x - pointA.x);
        Double distAC_X = Double.valueOf(pointC.x - pointA.x);
        
        Double distAB_Y = Double.valueOf(pointB.y - pointA.y);
        Double distAC_Y = Double.valueOf(pointC.y - pointA.y);

        return (distAB_X*distAC_X) + (distAB_Y*distAC_Y);
    }
}

class Coord {
    public int x;
    public int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return this.x + "-" + this.y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coord other = (Coord) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
}
