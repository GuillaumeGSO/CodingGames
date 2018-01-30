package com.isograd.exercise;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;


class IsoContestTest {

    @Test
    public void testInput1() throws Exception {
        Path pathInput = Paths.get("input1.txt");
        Path pathOutput = Paths.get("output1.txt");

        byte[] data = Files.readAllBytes(pathInput);

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream  out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        // do your thing
        IsoContest.main(null);

        String reponseAttendue = Files.readAllLines(pathOutput).get(0);
        assertEquals(reponseAttendue, out.toString().replaceAll("\\r\\n", ""));

        // optionally, reset System.in to its original
        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    public void testInput2() throws Exception {
        Path pathInput = Paths.get("input2.txt");
        Path pathOutput = Paths.get("output2.txt");

        byte[] data = Files.readAllBytes(pathInput);

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream  out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        // do your thing
        IsoContest.main(null);

        String reponseAttendue = Files.readAllLines(pathOutput).get(0);
        assertEquals(reponseAttendue, out.toString().replaceAll("\\r\\n", ""));

        // optionally, reset System.in to its original
        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    public void testInput3() throws Exception {
        Path pathInput = Paths.get("input3.txt");
        Path pathOutput = Paths.get("output3.txt");

        byte[] data = Files.readAllBytes(pathInput);

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream  out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        // do your thing
        IsoContest.main(null);

        String reponseAttendue = Files.readAllLines(pathOutput).get(0);
        assertEquals(reponseAttendue, out.toString().replaceAll("\\r\\n", ""));

        // optionally, reset System.in to its original
        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    public void testInput4() throws Exception {
        Path pathInput = Paths.get("input4.txt");
        Path pathOutput = Paths.get("output4.txt");

        byte[] data = Files.readAllBytes(pathInput);

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream  out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        // do your thing
        IsoContest.main(null);

        String reponseAttendue = Files.readAllLines(pathOutput).get(0);
        assertEquals(reponseAttendue, out.toString().replaceAll("\\r\\n", ""));

        // optionally, reset System.in to its original
        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    public void testInput5() throws Exception {
        Path pathInput = Paths.get("input5.txt");
        Path pathOutput = Paths.get("output5.txt");

        byte[] data = Files.readAllBytes(pathInput);

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream  out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        // do your thing
        IsoContest.main(null);

        String reponseAttendue = Files.readAllLines(pathOutput).get(0);
        assertEquals(reponseAttendue, out.toString().replaceAll("\\r\\n", ""));

        // optionally, reset System.in to its original
        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    public void testInput6() throws Exception {
        Path pathInput = Paths.get("input6.txt");
        Path pathOutput = Paths.get("output6.txt");

        byte[] data = Files.readAllBytes(pathInput);

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream  out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        // do your thing
        IsoContest.main(null);

        String reponseAttendue = Files.readAllLines(pathOutput).get(0);
        assertEquals(reponseAttendue, out.toString().replaceAll("\\r\\n", ""));

        // optionally, reset System.in to its original
        System.setIn(System.in);
        System.setOut(System.out);
    }

}
