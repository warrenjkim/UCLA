import org.junit.*;
import java.io.*;
import java.nio.file.*;

public class ParseTest {
    private void runTestCase(String testName) {
        String expectedOutput = "";
        String result = "";
        try {
            String inputPath = "testcases/hw1/" + testName;
            FileInputStream testInput = new FileInputStream(inputPath);
            System.setIn(testInput);

            ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(testOutput));

            Parse.main(new String[]{});

            System.setIn(System.in);
            System.setOut(originalOut);
            testInput.close();

            result = testOutput.toString().trim();
            String expectedOutputPath = "testcases/hw1/" + testName + ".out";
            expectedOutput = new String(Files.readAllBytes(Paths.get(expectedOutputPath))).trim();

            Assert.assertEquals(testName + " passed", expectedOutput, result);
        } catch (Exception e) {
            String message = String.format("%s failed: expected '%s' but got '%s'", testName, expectedOutput, result);
            Assert.fail(message);
        }
    }

    @Test public void test01() { runTestCase("test01"); }
    @Test public void test02() { runTestCase("test02"); }
    @Test public void test03() { runTestCase("test03"); }
    @Test public void test04() { runTestCase("test04"); }
    @Test public void test05() { runTestCase("test05"); }
    @Test public void test06() { runTestCase("test06"); }
    @Test public void test07() { runTestCase("test07f"); }
    @Test public void test08() { runTestCase("test08"); }
    @Test public void test09() { runTestCase("test09f"); }
    @Test public void test10() { runTestCase("test10f"); }
    @Test public void test11() { runTestCase("test11"); }
}

