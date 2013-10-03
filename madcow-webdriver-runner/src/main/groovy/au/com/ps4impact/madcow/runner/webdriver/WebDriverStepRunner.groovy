/*
 * Copyright 2012 4impact, Brisbane, Australia
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package au.com.ps4impact.madcow.runner.webdriver

import au.com.ps4impact.madcow.step.MadcowStepRunner
import au.com.ps4impact.madcow.step.MadcowStep
import org.apache.commons.io.FileUtils
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.apache.commons.lang3.StringUtils
import au.com.ps4impact.madcow.step.MadcowStepResult
import au.com.ps4impact.madcow.grass.GrassBlade
import au.com.ps4impact.madcow.grass.GrassParseException
import org.openqa.selenium.firefox.FirefoxProfile
import au.com.ps4impact.madcow.runner.webdriver.driver.WebDriverType
import au.com.ps4impact.madcow.MadcowTestCase
import org.openqa.selenium.NoSuchElementException
import com.gargoylesoftware.htmlunit.BrowserVersion
import org.openqa.selenium.remote.Augmenter
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver

/**
 * Implementation of the WebDriver step runner.
 *
 * @author Gavin Bunney
 */
class WebDriverStepRunner extends MadcowStepRunner {

    public WebDriver driver;
    public WebDriverType driverType;
    public String lastPageSource;
    public String lastPageTitle;
    public boolean initRemoteTimedOut = false;
    public int retryCount = 0;
    def driverParameters = null;

    WebDriverStepRunner(MadcowTestCase testCase, HashMap<String, String> parameters) {

        this.testCase = testCase;

        // default the browser if not specified
        parameters.browser = StringUtils.upperCase(parameters.browser ?: "${WebDriverType.HTMLUNIT.toString()}");

        try {
            driverType = WebDriverType.getDriverType(parameters.browser);
            if (driverType == null)
                throw new ClassNotFoundException("Unknown browser '${parameters.browser}'")

            testCase.logInfo("Configuring WebDriver browser '${driverType.name}'")

            switch (driverType) {
                case WebDriverType.REMOTE:

                    driverParameters = [:];
                    if ((parameters.remoteServerUrl ?: '') != '') {
                        driverParameters.url = parameters.remoteServerUrl;
                        if ((parameters.emulate ?: '') != '') {
                            switch (StringUtils.upperCase(parameters.emulate)) {
                                case 'IE7':
                                case 'IE8':
                                case 'IE9':
                                case 'IE':
                                    driverParameters.desiredCapabilities = DesiredCapabilities.internetExplorer();
                                    break;
                                case 'OPERA':
                                    driverParameters.desiredCapabilities = DesiredCapabilities.opera();
                                    break;
                                case 'CHROME':
                                    driverParameters.desiredCapabilities = DesiredCapabilities.chrome();
                                    break;
                                case 'SAFARI':
                                    driverParameters.desiredCapabilities = DesiredCapabilities.safari();
                                    break;
                                case 'PHANTOMJS':
                                    driverParameters.desiredCapabilities = DesiredCapabilities.phantomjs();
                                    break;
                                case 'IPAD':
                                    driverParameters.desiredCapabilities = DesiredCapabilities.ipad();
                                    break;
                                case 'IPHONE':
                                    driverParameters.desiredCapabilities = DesiredCapabilities.iphone();
                                    break;
                                case 'FIREFOX':
                                case 'FF3':
                                case 'FF3.6':
                                default:
                                    driverParameters.desiredCapabilities = DesiredCapabilities.firefox();
                                    break;
                            }
                        }
                        driverParameters.desiredCapabilities.setCapability("selenium-version", "2.33.0");
                        //set javascript on
                        driverParameters.desiredCapabilities.setJavascriptEnabled(true);
                        driverParameters.requiredCapabilities = null;

                        testCase.logInfo("Test case will attempt to start using remoteServerUrl '${parameters.remoteServerUrl}'")
                    }else{
                        throw new Exception("Cannot start '${driverType.name}' WebDriver without remoteServerUrl config parameter");
                    }
                    break;

                case WebDriverType.CHROME:
//                    driverParameters = new ChromeDriverService()
//                    driverParameters.start()
//                    initialiseDriver()
                    break;

                case WebDriverType.FIREFOX:
                    driverParameters = new FirefoxProfile()
                    driverParameters.setEnableNativeEvents(true)
//                    initialiseDriver()
                    break;

                case WebDriverType.PHANTOMJS:
                    //driverParameters = new ResolvingPhantomJSDriverService.createDefaultService(capabilities)
//                    initialiseDriver()
                    break;

                case WebDriverType.IE:
//                    initialiseDriver()
                    break;

                case WebDriverType.HTMLUNIT:
                    driverParameters = BrowserVersion.FIREFOX_3_6;
                    if ((parameters.emulate ?: '') != '') {
                        switch (StringUtils.upperCase(parameters.emulate)) {
                            case 'IE6':
                                driverParameters = BrowserVersion.INTERNET_EXPLORER_6;
                                break;
                            case 'IE7':
                                driverParameters = BrowserVersion.INTERNET_EXPLORER_7;
                                break;
                            case 'IE8':
                                driverParameters = BrowserVersion.INTERNET_EXPLORER_8;
                                break;
                            case 'FIREFOX':
                            case 'FF3':
                            case 'FF3.6':
                            default:
                                driverParameters = BrowserVersion.FIREFOX_3_6;
                                break;
                        }
                    }
                    testCase.logInfo("Emulating HtmlUnit browser '${driverParameters.getNickname()}'")
//                    initialiseDriver()
                    break;
                default:
                    break;
            }

        } catch (ClassNotFoundException cnfe) {
            //cnfe.printStackTrace()
            throw new Exception("The specified Browser '${parameters.browser}' cannot be found: $cnfe.message");
        } catch (ClassCastException cce) {
            //cce.printStackTrace()
            throw new Exception("The specified Browser '${parameters.browser}' isn't a WebDriver! $cce.message");
        } catch (Exception e) {
            //e.printStackTrace()
            throw new Exception("Unexpected error creating the Browser '${parameters.browser}': $e.message");
        }
    }

    /**
     * Initialises the webdriver instance or throws an exception
     *
     * @return an exception or an initialised driver
     */
    private initialiseDriver() {
        //if driver is null create it
        if (this.driver == null) {
            try {
                testCase.logInfo("Instantiating Driver instance")
                if (this.driverParameters != null) {
                    this.driver = this.driverType.driverClass.newInstance(this.driverParameters) as WebDriver
                } else {
                    this.driver = this.driverType.driverClass.newInstance() as WebDriver
                }

            } catch (ClassNotFoundException cnfe) {
                //cnfe.printStackTrace()
                throw new Exception("The specified Browser '${driverType.name}' cannot be found: $cnfe.message");
            } catch (ClassCastException cce) {
                //cce.printStackTrace()
                throw new Exception("The specified Browser '${driverType.name}' isn't a WebDriver! $cce.message");
            } catch (Exception e) {
                //e.printStackTrace()
                throw new Exception("Unexpected error creating the Browser '${driverType.name}': $e.message");
            }
        }
    }

    /**
     * Get a blade runner for the given GrassBlade.
     */
    protected WebDriverBladeRunner getBladeRunner(GrassBlade blade) {
        String operation = blade.operation;

        if (StringUtils.contains(operation, '.')) {
            operation = String.format("%s.%s", StringUtils.substringBeforeLast(operation, '.'),
                                               StringUtils.capitalize(StringUtils.substringAfterLast(operation, '.')));
        } else {
            operation = StringUtils.capitalize(operation);
        }

        return WebDriverBladeRunner.getBladeRunner(operation) as WebDriverBladeRunner;
    }

    /**
     * Execute the madcow step for a given test case.
     */
    public void execute(MadcowStep step) {

        //only execute if this is not a skipped test
        if (!step.testCase.ignoreTestCase) {

            WebDriverBladeRunner bladeRunner = getBladeRunner(step.blade) as WebDriverBladeRunner;
            try {
                initialiseDriverWithRetriesIfRequired();

                bladeRunner.execute(this, step);

                if (!driver.title?.equals(lastPageTitle)) {
                    lastPageTitle = driver.title;
                    if (lastPageTitle != '') {
                        testCase.logInfo("Current Page: $lastPageTitle");
                    }
                }

                //if pageSource not null and not equal to previous
                if (driver?.pageSource != null
                    && !driver?.pageSource?.equals(lastPageSource)) {
                    captureHtmlResults(step);
                }

            } catch (NoSuchElementException ignored) {
                step.result = MadcowStepResult.FAIL("Element '${step.blade.mappingSelectorType} : ${step.blade.mappingSelectorValue}' not found on the page!");
            } catch (e) {
                step.result = MadcowStepResult.FAIL("Unexpected Exception: $e");
            }
        } else {
            step.result = MadcowStepResult.NOT_YET_EXECUTED('Skipped!');
        }

    }

    /**
     * Attempts 3 retries of instantiating the driver and then fails if it still doesnt work.
     */
    protected void initialiseDriverWithRetriesIfRequired() {
        //do initialise of driver inside the first execution of the testCase
        if (driver == null) {
            while (retryCount <= 3) {
                retryCount++;
                try {
                    if (driver == null) {
                        initialiseDriver()
                    }
                } catch (Exception ex) {
                    testCase.logWarn("Failed to initialise driver! Retry number ${retryCount}... ")
                    if (retryCount >= 3) {
                        throw ex
                    }
                    testCase.logDebug("Exception was ${ex}")
                }
            }
        }
    }

    /**
     * Determine if the step runner has a blade runner capable of executing the step.
     * This is used during test 'compilation' to see if it can even be done.
     */
    public boolean hasBladeRunner(GrassBlade blade) {
        try {
            WebDriverBladeRunner bladeRunner = getBladeRunner(blade);
            if (bladeRunner == null) {
                testCase.logError("Blade Runner not found for ${blade.toString()}");
                return false;
            }

            return bladeRunner.isValidBladeToExecute(blade);

        } catch (GrassParseException gpe) {
            throw gpe;
        } catch (e) {
            testCase.logError("Blade Runner not found for ${blade.toString()}\n\nException: $e");
            return false;
        }
    }

    /**
     * Capture the html result file.
     */
    public void captureHtmlResults(MadcowStep step) {
        String originalPageSource = driver.pageSource
        String alteredPageSource = addBaseMetaTagToPageSource(originalPageSource)
        if (originalPageSource) {
            new File("${step.testCase.resultDirectory.path}/${step.sequenceNumberString}.html") << alteredPageSource;
            capturePNGScreenShot(step);
            lastPageSource = originalPageSource;
            step.result.hasResultFile = true;
        }
    }

    /**
     * Alter the retreived page source to use the FQDN in href and src links
     *
     * @param pageSource the page source as retrieved by webdriver
     * @return an altered version of the pageSource with FQDN's
     */
    private String addBaseMetaTagToPageSource(String pageSource) {
        try{
            //not already a base element
            if (!pageSource.contains("<base") &&
                driver?.currentUrl != null &&
                !(driver.currentUrl.equals("about:blank"))) {
                def baseURL = new URL(driver.currentUrl); //may need to use different url here
                return pageSource.replace("<head>",'<head><base href="'+baseURL+'"/>')
            }
        }catch(Exception e){
            return pageSource
        }
        return pageSource
    }

    private void capturePNGScreenShot(MadcowStep step){
        //HTML UNIT DOESN'T SUPPORT TAKING SCREENSHOTS AS NEVER RENDERS
        if (driver instanceof org.openqa.selenium.remote.RemoteWebDriver){
            File screenShot
            if (!(driver instanceof TakesScreenshot)) {
                WebDriver augmentedDriver = new Augmenter().augment(driver as RemoteWebDriver)
                screenShot = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE)
            } else {
                screenShot = ((TakesScreenshot) driver as RemoteWebDriver).getScreenshotAs(OutputType.FILE)
            }
            File saveTo = new File("${step.testCase.resultDirectory.path}/${step.sequenceNumberString}.png")
            FileUtils.copyFile(screenShot, saveTo)
            step.result.hasScreenshot = true;
        }
    }

    /**
     * Retrieve the default mappings selector this step runner.
     * This is used as the 'type' of selector when no type is given.
     * For WebDriver, this is 'htmlid'.
     */
    public String getDefaultSelector() {
        return 'htmlid';
    }

    public void finishTestCase() {
        if (driver != null){
            if (driverType == WebDriverType.CHROME
                || driverType == WebDriverType.PHANTOMJS) {
                driver.quit();
            } else {
                driver.close();
            }
        }
    }
}
