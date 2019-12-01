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
    public void calculAngle() {
        //A(2;1), B(−2;4) et C(1;−3)
        Target pA = new Target(1, 2, 1);
        Target pB = new Target(2,-2, 4);
        Target pC = new Target(3,1, -3);

        assertEquals(112, Player.calculAngle(pA, pB, pC));
    }

    @Test
    public void calculDistance() {
        //A(2;1), B(−2;4)
        Target p1 = new Target(1, 2, 1);
        Target p2 = new Target(2,-2, 4);
        
        assertEquals(Double.valueOf(5), Player.calculDistance(p1, p2));
    }

    @Test
    public void calculProduitScalaire() {
        //A(2;1), B(−2;4) et C(1;−3)
        Target pA = new Target(1, 2, 1);
        Target pB = new Target(2, -2, 4);
        Target pC = new Target(3, 1, -3);

        assertEquals(Double.valueOf(-8d), Player.calculProduitScalaire(pA, pB, pC));
    }
}