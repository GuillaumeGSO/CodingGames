import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class PlayerTest {
    /**
     * Rigorous Test.
     */
    @Test
    public void calculAngle_A() {
        // A(2;1), B(−2;4) et C(1;−3)
        Target pA = new Target(1, 2, 1);
        Target pB = new Target(2, -2, 4);
        Target pC = new Target(3, 1, -3);

        assertEquals(112, Player.calculAngle(pA, pB, pC));
    }

    @Test
    public void calculAngle_B() {
        // A(2;1), B(−2;4) et C(1;−3)
        Target pA = new Target(1, 2, 1);
        Target pB = new Target(2, -2, 4);
        Target pC = new Target(3, 1, -3);

        assertEquals(29, Player.calculAngle(pB, pA, pC));
    }

    @Test
    public void calculAngle_C() {
        // A(2;1), B(−2;4) et C(1;−3)
        Target pA = new Target(1, 2, 1);
        Target pB = new Target(2, -2, 4);
        Target pC = new Target(3, 1, -3);

        assertEquals(37, Player.calculAngle(pC, pA, pB));
    }

    @Test
    public void calculDistance_AB() {
        // A(2;1), B(−2;4)
        Target p1 = new Target(1, 2, 1);
        Target p2 = new Target(2, -2, 4);

        assertEquals(Double.valueOf(5), Player.calculDistance(p1, p2));
    }

    @Test
    public void calculDistance_AC() {
        // A(2;1), C(1;−3)
        Target p1 = new Target(1, 2, 1);
        Target p2 = new Target(2, 1, -3);

        assertEquals(Double.valueOf(Math.sqrt(17)), Player.calculDistance(p1, p2));
    }

    @Test
    public void calculDistance_BC() {
        // B(2;1), C(1;−3)
        Target p1 = new Target(2, -2, 4);
        Target p2 = new Target(2, 1, -3);

        assertEquals(Double.valueOf(Math.sqrt(58)), Player.calculDistance(p1, p2));
    }

    @Test
    public void calculProduitScalaire_AB_AC() {
        // A(2;1), B(−2;4) et C(1;−3)
        Target pA = new Target(1, 2, 1);
        Target pB = new Target(2, -2, 4);
        Target pC = new Target(3, 1, -3);

        assertEquals(Double.valueOf(-8d), Player.calculProduitScalaire(pA, pB, pC));
    }

    @Test
    public void calculProduitScalaire_BA_BC() {
        // A(2;1), B(−2;4) et C(1;−3)
        Target pA = new Target(1, 2, 1);
        Target pB = new Target(2, -2, 4);
        Target pC = new Target(3, 1, -3);

        assertEquals(Double.valueOf(33d), Player.calculProduitScalaire(pB, pA, pC));
    }

    @Test
    public void calculProduitScalaire_CA_CB() {
        // A(2;1), B(−2;4) et C(1;−3)
        Target pA = new Target(1, 2, 1);
        Target pB = new Target(2, -2, 4);
        Target pC = new Target(3, 1, -3);

        assertEquals(Double.valueOf(25d), Player.calculProduitScalaire(pC, pA, pB));
    }

    @Test
    public void findPreviousTarget() {
        assertNull(Player.findPreviousTarget(null));

        // Simulation de plusieurs tours
        // Premiere base
        Target target1 = new Target(1, 2, 1);
        Player.setOfCoords.add(target1.toCoord());
        Player.mapOfTargets.put(target1.name, target1);

        // deuxième base
        Target target2 = new Target(2, -2, 4);
        target2.targetAvant = target1;
        Player.setOfCoords.add(target2.toCoord());
        Player.mapOfTargets.put(target2.name, target2);
        assertEquals(target2, Player.findPreviousTarget(target1));
        assertNull(Player.findPreviousTarget(target2));

        // troisième base : c'est le départ mais on ne le sais pas encore
        Target target3 = new Target(3, 1, -3);
        target3.targetAvant = target2;
        Player.setOfCoords.add(target3.toCoord());
        Player.mapOfTargets.put(target3.name, target3);
        assertEquals(target3, Player.findPreviousTarget(target2));
        assertEquals(target2, Player.findPreviousTarget(target1));
        assertNull(Player.findPreviousTarget(target3));

    }

    @Test
    public void majTargets() {
        // A(2;1), B(−2;4) et C(1;−3)
        // 112°,   29°,       37°
        // Simulation de plusieurs tours
        Player.setOfCoords.clear();
        Player.mapOfTargets.clear();

        // Direction premiere base A
        Target targetA = Player.majTargets(null, 2, 1);
        assertNotNull(targetA);
        assertEquals(1, targetA.name);
        assertEquals(Integer.valueOf(0), targetA.angleNext);
        
        // deuxième base : B
        Target targetB = Player.majTargets(targetA, -2, 4);
        assertNotNull(targetB);
        assertEquals(2, targetB.name);
        assertEquals(targetA, targetB.targetAvant);
        assertEquals(Integer.valueOf(0),targetA.angleNext);
        assertEquals(Integer.valueOf(0),targetB.angleNext);
        
        // troisième base C : c'est le départ mais on ne le sais pas encore
        Target targetC = Player.majTargets(targetB, 1, -3);
        assertNotNull(targetC);
        assertEquals(3, targetC.name);
        assertEquals(targetB, targetC.targetAvant);
        assertEquals(Integer.valueOf(0),targetA.angleNext);
        assertEquals(Integer.valueOf(0),targetB.angleNext);
        assertEquals(Integer.valueOf(0),targetC.angleNext);

        // Retour à la premiere base A
        assertEquals(targetA, Player.majTargets(targetC, 2, 1));
        assertEquals(targetC, targetA.targetAvant);
        assertEquals(targetB, targetA.targetApres);
        //Next angle est celui de B
        assertEquals(Integer.valueOf(29),targetA.angleNext);
        assertEquals(Integer.valueOf(0),targetB.angleNext);
        assertEquals(Integer.valueOf(0),targetC.angleNext);


        // puis le deuxième B
        assertEquals(targetB, Player.majTargets(targetA, -2, 4));
        assertEquals(targetA, targetB.targetAvant);
        assertEquals(targetC, targetB.targetApres);
        assertEquals(Integer.valueOf(29),targetA.angleNext);
        //Next angle est celui de C
        assertEquals(Integer.valueOf(37),targetB.angleNext);
        assertEquals(Integer.valueOf(0),targetC.angleNext);
        
        // puis la troisème
        assertEquals(targetC, Player.majTargets(targetB, 1, -3));
        assertEquals(targetB, targetC.targetAvant);
        assertEquals(targetA, targetC.targetApres);
        assertEquals(Integer.valueOf(29),targetA.angleNext);
        assertEquals(Integer.valueOf(37),targetB.angleNext);
        //Next angle est celui de A
        assertEquals(Integer.valueOf(112),targetC.angleNext);

        // et retour
        assertEquals(targetA, Player.majTargets(targetC, 2, 1));
        assertEquals(targetC, targetA.targetAvant);
        assertEquals(targetB, targetA.targetApres);
        //Les angles n'ont pas changés
        assertEquals(Integer.valueOf(29),targetA.angleNext);
        assertEquals(Integer.valueOf(37),targetB.angleNext);
        assertEquals(Integer.valueOf(112),targetC.angleNext);


        // Pour terminer...
        assertEquals(3, Player.setOfCoords.size());
        assertEquals(3, Player.mapOfTargets.size());

        System.out.println(Player.mapOfTargets);
    }

}