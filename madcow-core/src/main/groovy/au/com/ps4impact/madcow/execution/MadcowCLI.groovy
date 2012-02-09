package au.com.ps4impact.madcow;

import groovyjarjarcommonscli.ParseException;
import groovyjarjarcommonscli.Option;

/**
 * Run Madcow from the Command Line.
 */
public class MadcowCLI {

    protected static def parseArgs(incomingArgs) throws ParseException {
        def cli = new CliBuilder(usage:'runMadcow [options]', header:'Options:')

        cli.with {
            h(longOpt : 'help', 'Show Usage Information')
            e(longOpt : 'env',  args: 1, argName: 'env-name', 'environment to load from the madcow-config.xml')
            t(longOpt : 'test', args: Option.UNLIMITED_VALUES, valueSeparator: ',', argName : 'testname', 'comma seperated list of test names')
        }

        cli.stopAtNonOption = true;

        def options = cli.parse(incomingArgs);

        if (options.help) {
            cli.usage();
            System.exit(0);
        }

        return options;
    }

    /**
     * Entry point.
     */
    static main(args)
    {
        def options = parseArgs(args);

        MadcowTestCase testCase = new MadcowTestCase()
    }

}