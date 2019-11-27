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
        Coord pA = new Coord(2, 1);
        Coord pB = new Coord(-2, 4);
        Coord pC = new Coord(1, -3);

        assertEquals(Double.valueOf(112.83), Player.calculAngle(pA, pB, pC), 0.1d);
    }

    @Test
    public void calculDistance() {
        //A(2;1), B(−2;4)
        Coord p1 = new Coord(2, 1);
        Coord p2 = new Coord(-2, 4);
        
        assertEquals(Double.valueOf(5), Player.calculDistance(p1, p2));
    }

    @Test
    public void calculProduitScalaire() {
        //A(2;1), B(−2;4) et C(1;−3)
        Coord pA = new Coord(2, 1);
        Coord pB = new Coord(-2, 4);
        Coord pC = new Coord(1, -3);

        assertEquals(Double.valueOf(-8d), Player.calculProduitScalaire(pA, pB, pC));
    }
}