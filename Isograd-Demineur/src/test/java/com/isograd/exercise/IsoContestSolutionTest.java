package com.isograd.exercise;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class IsoContestSolutionTest {


    @Test
    public void testInput1() throws Exception {
        runTest("1");
    }

    @Test
    public void testInput2() throws Exception {
        runTest("2");
    }

    @Test
    public void testInput3() throws Exception {
        runTest("3");
    }

    @Test
    public void testInput4() throws Exception {
        runTest("4");
    }

    @Test
    public void testInput5() throws Exception {
        runTest("5");
    }

    @Test
    public void testInput6() throws Exception {
        runTest("6");
    }

    private void runTest(String num) throws Exception{
        //System.err.println(Paths.get("").toAbsolutePath().toAbsolutePath());

        Path pathInput = Paths.get("Isograd-Demineur", "src", "test", "resources", "input" + num + ".txt");
        Path pathOutput = Paths.get("Isograd-Demineur", "src", "test", "resources","output" + num + ".txt");

        byte[] data = Files.readAllBytes(pathInput);

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream  out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        // do your thing
        IsoContestSolution.main(null);

        String reponseAttendue = Files.readAllLines(pathOutput).get(0);
        //Pour compatibilit? Windows & OSX
        Assert.assertEquals(reponseAttendue, out.toString().replaceAll("\\r\\n", "").replaceAll("\\n", ""));

        // optionally, reset System.in to its original
        System.setIn(System.in);
        System.setOut(System.out);
    }

}
