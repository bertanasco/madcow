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



package au.com.ps4impact.madcow.runner.webdriver.blade

import au.com.ps4impact.madcow.MadcowTestCase
import au.com.ps4impact.madcow.config.MadcowConfig
import au.com.ps4impact.madcow.grass.GrassBlade
import au.com.ps4impact.madcow.mappings.MadcowMappings
import au.com.ps4impact.madcow.runner.webdriver.WebDriverStepRunner
import au.com.ps4impact.madcow.step.MadcowStep
import au.com.ps4impact.madcow.util.ResourceFinder

/**
 * Test for the SelectCheckbox BladeRunner.
 *
 * @author Paul Bevis
 */
class IsRadioButtonSelectedTest extends GroovyTestCase {

    MadcowTestCase testCase;
    def isRadioButtonSelected;
    String testHtmlFilePath;

    void setUp() {
        super.setUp();

        testCase = new MadcowTestCase('IsRadioButtonSelectedTest', new MadcowConfig(), []);
        isRadioButtonSelected = new IsRadioButtonSelected();
        testHtmlFilePath = ResourceFinder.locateFileOnClasspath(this.class.classLoader, 'test.html', 'html').absolutePath;
    }

    protected verifyIsSelectedExecution(GrassBlade blade, boolean shouldPass) {
        (testCase.stepRunner as WebDriverStepRunner).driver.get("file://${testHtmlFilePath}");
        MadcowStep step = new MadcowStep(testCase, blade, null);
        testCase.stepRunner.execute(step);
        assertEquals(shouldPass, step.result.passed());
    }

    void testCheckboxByHtmlId() {
        // defaults to html id
        GrassBlade blade = new GrassBlade('aRadioButtonId.isRadioButtonSelected', testCase.grassParser);
        verifyIsSelectedExecution(blade, true);
         blade = new GrassBlade('aRadioButtonIdUnSelected.isRadioButtonSelected', testCase.grassParser);
        verifyIsSelectedExecution(blade, false);

        // explicit id
        MadcowMappings.addMapping(testCase, 'mapping', ['id': 'aRadioButtonId']);
        blade = new GrassBlade('mapping.isRadioButtonSelected', testCase.grassParser);
        verifyIsSelectedExecution(blade, true);
    }

    void testCheckboxByName() {
        MadcowMappings.addMapping(testCase, 'aRadioButtonName', ['name': 'aRadioButtonName']);
        GrassBlade blade = new GrassBlade('aRadioButtonName.isRadioButtonSelected', testCase.grassParser);
        verifyIsSelectedExecution(blade, true);
    }

    void testCheckboxByXPath() {
        MadcowMappings.addMapping(testCase, 'aRadioButtonXPath', ['xpath': '//input[@id=\'aRadioButtonId\']']);
        GrassBlade blade = new GrassBlade('aRadioButtonXPath.isRadioButtonSelected', testCase.grassParser);
        verifyIsSelectedExecution(blade, true);
    }
    
    void testCheckboxDoesNotExist() {
        GrassBlade blade = new GrassBlade('aRadioButtonThatDoesntExist.isRadioButtonSelected', testCase.grassParser);
        verifyIsSelectedExecution(blade, false);
    }

    void testDefaultMappingSelector() {
        GrassBlade blade = new GrassBlade('testsite_menu_createAddress.isRadioButtonSelected', testCase.grassParser);
        assertTrue(isRadioButtonSelected.isValidBladeToExecute(blade));
    }

    void testMappingSelectorInvalidRequired() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.isRadioButtonSelected', testCase.grassParser);
            blade.mappingSelectorType = 'invalidOne';
            assertFalse(isRadioButtonSelected.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals('Unsupported mapping selector type \'invalidOne\'. Only [ID, TEXT, NAME, XPATH, CSS] are supported.', e.message);
        }
    }

    void testMappingSelectorRequired() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.isRadioButtonSelected', testCase.grassParser);
            blade.mappingSelectorType = null;
            assertFalse(isRadioButtonSelected.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals('Mapping selector must be supplied. One of [ID, TEXT, NAME, XPATH, CSS] are supported.', e.message);
        }
    }

    void testEquationNotSupported() {
        try {
            GrassBlade blade = new GrassBlade('testsite_menu_createAddress.isRadioButtonSelected = yeah yeah', testCase.grassParser);
            assertFalse(isRadioButtonSelected.isValidBladeToExecute(blade));
            fail('should always exception');
        } catch (e) {
            assertEquals('Unsupported grass format. Only grass blades of type \'[STATEMENT]\' are supported.', e.message);
        }
    }
}