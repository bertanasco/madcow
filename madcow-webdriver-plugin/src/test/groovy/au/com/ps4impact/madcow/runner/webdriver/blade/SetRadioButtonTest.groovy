/*
 * Copyright 2015 4impact, Brisbane, Australia
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



package au.com.ps4impact.madcow.runner.webdriver.blade

import au.com.ps4impact.madcow.MadcowTestCase
import au.com.ps4impact.madcow.config.MadcowConfig
import au.com.ps4impact.madcow.grass.GrassBlade
import au.com.ps4impact.madcow.mappings.MadcowMappings
import au.com.ps4impact.madcow.runner.webdriver.WebDriverStepRunner
import au.com.ps4impact.madcow.step.MadcowStep
import au.com.ps4impact.madcow.util.ResourceFinder

/**
 * Test for the Set Radio Button BladeRunner.
 *
 * @author Gavin Bunney
 */
class SetRadioButtonTest extends GroovyTestCase {

    MadcowTestCase testCase;
    def setRadioButton;
    String testHtmlFilePath;

    void setUp() {
        super.setUp();

        testCase = new MadcowTestCase('SetRadioButtonTest', new MadcowConfig(), []);
        setRadioButton = new SetRadioButton();
        testHtmlFilePath = ResourceFinder.locateFileOnClasspath(this.class.classLoader, 'test.html', 'html').absolutePath;
    }

    protected verifyRadioExecution(GrassBlade blade, boolean shouldPass) {
        (testCase.stepRunner as WebDriverStepRunner).driver.get("file://${testHtmlFilePath}");
        MadcowStep step = new MadcowStep(testCase, blade, null);
        testCase.stepRunner.execute(step);
        assertEquals(shouldPass, step.result.passed());
    }

    void testRadioButtonByHtmlId() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('enabledRadioInput.setRadioButton', testCase.grassParser);
        verifyRadioExecution(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'enabledRadioInputMapped', ['id': 'enabledRadioInput']);
        blade = new GrassBlade('enabledRadioInputMapped.setRadioButton', testCase.grassParser);
        verifyRadioExecution(blade, true);
    }

    void testRadioButtonByName() {
        MadcowMappings.addMapping(testCase, 'aRadioName', ['name': 'enabledRadioInput']);
        GrassBlade blade = new GrassBlade('aRadioName.setRadioButton', testCase.grassParser);
        verifyRadioExecution(blade, true);
    }

    void testRadioButtonByXPath() {
        MadcowMappings.addMapping(testCase, 'aRadioXPath', ['xpath': '//input[@id=\'enabledRadioInput\']']);
        GrassBlade blade = new GrassBlade('aRadioXPath.setRadioButton', testCase.grassParser);
        verifyRadioExecution(blade, true);
    }

    void testRadioButtonDoesNotExist() {
        GrassBlade blade = new GrassBlade('aRadioThatDoesntExist.setRadioButton', testCase.grassParser);
        verifyRadioExecution(blade, false);
    }

    void testDefaultMappingSelector() {
        GrassBlade blade = new GrassBlade('testsite_menu_createAddress.setRadioButton', testCase.grassParser);
        assertTrue(setRadioButton.isValidBladeToExecute(blade));
    }

    void testRadioButtonByHtmlIdOffScreen() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aRadioOffScreenId.setRadioButton', testCase.grassParser);
        verifyRadioExecution(blade, true);

        // explicit htmlid
        MadcowMappings.addMapping(testCase, 'aRadioOffScreenId', ['id': 'aRadioOffScreenId']);
        blade = new GrassBlade('aRadioOffScreenId.setRadioButton', testCase.grassParser);
        verifyRadioExecution(blade, true);
    }

    void testMappingSelectorInvalidRequired() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.setRadioButton', testCase.grassParser);
            blade.mappingSelectorType = 'invalidOne';
            assertFalse(setRadioButton.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals('Unsupported mapping selector type \'invalidOne\'. Only [HTMLID, TEXT, NAME, XPATH, CSS] are supported.', e.message);
        }
    }
}
