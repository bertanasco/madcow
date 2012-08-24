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

import au.com.ps4impact.madcow.grass.GrassBlade
import au.com.ps4impact.madcow.runner.webdriver.WebDriverBladeRunner
import au.com.ps4impact.madcow.runner.webdriver.WebDriverStepRunner
import au.com.ps4impact.madcow.step.MadcowStep
import au.com.ps4impact.madcow.step.MadcowStepResult
import org.openqa.selenium.Keys
import au.com.ps4impact.madcow.runner.webdriver.driver.htmlunit.MadcowHtmlUnitDriver
import com.gargoylesoftware.htmlunit.html.HtmlPage
import au.com.ps4impact.madcow.runner.webdriver.driver.htmlunit.MadcowHtmlUnitWebElement
import com.gargoylesoftware.htmlunit.html.HtmlInput
import com.gargoylesoftware.htmlunit.html.HtmlTextArea

/**
 * The Value blade sets a value on an element
 *
 * @author Gavin Bunney
 */
class Value extends WebDriverBladeRunner {

    public void execute(WebDriverStepRunner stepRunner, MadcowStep step) {
        def element = findElement(stepRunner, step);

        if (stepRunner.driver instanceof MadcowHtmlUnitDriver) {
            def htmlUnitWebElement = (element as MadcowHtmlUnitWebElement).getElement();
            htmlUnitWebElement.focus();

            if (htmlUnitWebElement instanceof HtmlInput) {
                htmlUnitWebElement.setAttribute("value", "");
                htmlUnitWebElement.type(step.blade.parameters as String);
            } else if (htmlUnitWebElement instanceof HtmlTextArea) {
                (htmlUnitWebElement as HtmlTextArea).setText(step.blade.parameters as String);
            } else {
                element.sendKeys(step.blade.parameters as String);
            }

            // unfocus the element to fire onchange
            ((HtmlPage) htmlUnitWebElement.getPage()).setFocusedElement(null);
        } else {
            element.clear();
            element.sendKeys(step.blade.parameters as String, Keys.TAB);
        }

        step.result = MadcowStepResult.PASS();
    }

    protected Collection<GrassBlade.GrassBladeType> getSupportedBladeTypes() {
        return [GrassBlade.GrassBladeType.EQUATION];
    }

    protected Collection<WebDriverBladeRunner.BLADE_MAPPING_SELECTOR_TYPE> getSupportedSelectorTypes() {
        return [WebDriverBladeRunner.BLADE_MAPPING_SELECTOR_TYPE.HTMLID,
                WebDriverBladeRunner.BLADE_MAPPING_SELECTOR_TYPE.NAME,
                WebDriverBladeRunner.BLADE_MAPPING_SELECTOR_TYPE.XPATH];
    }

    protected boolean allowEmptyParameterValue() {
        return true;
    }
}
