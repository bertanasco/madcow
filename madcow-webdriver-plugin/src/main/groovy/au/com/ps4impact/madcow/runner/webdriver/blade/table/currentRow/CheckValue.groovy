package au.com.ps4impact.madcow.runner.webdriver.blade.table.currentRow

import au.com.ps4impact.madcow.runner.webdriver.WebDriverStepRunner
import au.com.ps4impact.madcow.step.MadcowStep
import au.com.ps4impact.madcow.step.MadcowStepResult
import org.apache.commons.lang3.StringUtils
import au.com.ps4impact.madcow.runner.webdriver.blade.table.util.TableXPather
import org.openqa.selenium.By
import au.com.ps4impact.madcow.grass.GrassBlade
import org.openqa.selenium.WebElement

/**
 * CheckValue.
 *
 * @author Gavin Bunney
 */
class CheckValue extends CurrentRowBladeRunner {

    public void execute(WebDriverStepRunner stepRunner, MadcowStep step) {

        TableXPather xPather = new TableXPather(step.blade);

        if (!super.validateSelectedRow(xPather, step))
            return;

        step.blade.parameters.each { String column, String value ->

            String cellXPath = xPather.getCellXPath(step.testCase.runtimeStorage[xPather.getRuntimeStorageKey()], column);

            WebElement element
            try {
                element = stepRunner.driver.findElements(By.xpath(cellXPath + '//*')).first();
            } catch (ignored) {

                try {
                    element = stepRunner.driver.findElements(By.xpath(cellXPath + '/self::node()')).first();
                } catch (ignored2) {
                    step.result = MadcowStepResult.FAIL("Unable to find a matching row and column.");
                    step.result.detailedMessage = "Cell XPath: $cellXPath";
                    return;
                }
            }

            String cellText = getElementText(element);

            String expectedValue = StringUtils.trim(value);
            if (expectedValue == cellText) {
                step.result = MadcowStepResult.PASS();
            } else {
                step.result = MadcowStepResult.FAIL("Expected: '$expectedValue', Present: '$cellText'");
            }
        }
    }

    /**
     * List of supported blade types.
     */
    protected Collection<GrassBlade.GrassBladeType> getSupportedBladeTypes() {
        return [GrassBlade.GrassBladeType.EQUATION];
    }

    /**
     * Get the list of supported parameter types, which for table operations is a map
     */
    protected List<Class> getSupportedParameterTypes() {
        return [Map.class];
    }
}