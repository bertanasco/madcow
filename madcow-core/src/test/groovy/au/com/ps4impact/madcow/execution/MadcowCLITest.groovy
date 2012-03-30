package au.com.ps4impact.madcow.execution

/**
 * Test for Command Line invocation of Madcow.
 */
class MadcowCLITest extends GroovyTestCase {

    void testTestToRunOption() {
        def options = MadcowCLI.parseArgs(['-t', 'AddressTest.grass'].toArray() as String[]);
        assertEquals('AddressTest.grass', options.ts.first());
        assertEquals('AddressTest.grass', options.tests.first());

        options = MadcowCLI.parseArgs(['--test', 'AddressTest.grass'].toArray() as String[]);
        assertEquals('AddressTest.grass', options.ts.first());
        assertEquals('AddressTest.grass', options.tests.first());

        options = MadcowCLI.parseArgs(['-t', 'AddressTest.grass,AddressTest2.grass'].toArray() as String[]);
        assertArrayEquals(['AddressTest.grass', 'AddressTest2.grass'].toArray(), options.ts.toArray());
        assertArrayEquals(['AddressTest.grass', 'AddressTest2.grass'].toArray(), options.tests.toArray());

        options = MadcowCLI.parseArgs(['-t', '"AddressTest.grass,AddressTest2.grass"'].toArray() as String[]);
        assertArrayEquals(['AddressTest.grass', 'AddressTest2.grass'].toArray(), options.ts.toArray());
        assertArrayEquals(['AddressTest.grass', 'AddressTest2.grass'].toArray(), options.tests.toArray());
    }
    
    void testEnvironment() {
        def options = MadcowCLI.parseArgs(['-e', 'DEV'].toArray() as String[]);
        assertEquals('DEV', options.e);
        assertEquals('DEV', options.env);

        options = MadcowCLI.parseArgs(['--env', 'DEV'].toArray() as String[]);
        assertEquals('DEV', options.e);
        assertEquals('DEV', options.env);
    }

    protected void checkHelpOutput(Closure functionCall) {

        // capture the system output stream so we can look at the help printed stuff
        ByteArrayOutputStream systemOutOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOutOutputStream));

        functionCall.call();

        String systemOutput = systemOutOutputStream.toString();

        def expectedHelpMessage = """usage: runMadcow [options]
Options:
 -a,--all               Run all tests
 -e,--env <env-name>    Environment to load from the madcow-config.xml
 -h,--help              Show usage information
 -t,--test <testname>   Comma seperated list of test names
""";

        assertEquals(expectedHelpMessage, systemOutput);

        MadcowCLI.main(['-h'] as String[])
        assertEquals(expectedHelpMessage, systemOutput);

        System.setOut(null);
    }

    void testHelp() {
        checkHelpOutput({MadcowCLI.parseArgs(['-h'].toArray() as String[])});
        checkHelpOutput({MadcowCLI.main(['-h'] as String[])});
    }
}
