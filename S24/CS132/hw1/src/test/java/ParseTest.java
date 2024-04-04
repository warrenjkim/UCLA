import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ParseTest {
    private void runTest(String testName) {
        String expected = "";
        String actual = "";
        try {
            String inputPath = "testcases/hw1/" + testName;
            FileInputStream testInput = new FileInputStream(inputPath);
            System.setIn(testInput);

            ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
            PrintStream out = System.out;
            System.setOut(new PrintStream(testOutput));

            Parse.main(new String[]{});

            System.setIn(System.in);
            System.setOut(out);
            testInput.close();

            actual = testOutput.toString().trim();
            String expectedPath = "testcases/hw1/" + testName + ".out";
            expected = new String(Files.readAllBytes(Paths.get(expectedPath))).trim();

            Assert.assertEquals(testName + " passed", expected, actual);
        } catch (Exception e) {
            String message = String.format("%s failed: expected '%s' but got '%s'", testName, expected, actual);
            Assert.fail(message);
        }
    }

    @Test public void test01() { runTest("test01"); }
    @Test public void test02() { runTest("test02"); }
    @Test public void test03() { runTest("test03"); }
    @Test public void test04() { runTest("test04"); }
    @Test public void test05() { runTest("test05"); }
    @Test public void test06() { runTest("test06"); }
    @Test public void test07() { runTest("test07f"); }
    @Test public void test08() { runTest("test08"); }
    @Test public void test09() { runTest("test09f"); }
    @Test public void test10() { runTest("test10f"); }
    @Test public void test11() { runTest("test11"); }
    @Test public void test12() { runTest("test12"); }
    @Test public void test13() { runTest("test13f"); }
    @Test public void test14() { runTest("test14"); }
}

