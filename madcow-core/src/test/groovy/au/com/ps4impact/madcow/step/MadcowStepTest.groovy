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

package au.com.ps4impact.madcow.step

import au.com.ps4impact.madcow.MadcowTestCase
import au.com.ps4impact.madcow.mock.MockMadcowConfig

/**
 * Test class for MadcowStep.
 *
 * @author Gavin Bunney
 */
class MadcowStepTest extends GroovyTestCase {

    void testSequenceNumberFormat() {
        def step = new MadcowStep(new MadcowTestCase('StepTest', MockMadcowConfig.getMadcowConfig()), null, null);
        step.sequenceNumber = 377;
        assertEquals('0000377', step.getSequenceNumberString());
    }

    void testToString() {
        assertToString(new MadcowStep(new MadcowTestCase('StepTest', MockMadcowConfig.getMadcowConfig()), null, null), "[testCase: StepTest, blade: null, parent: null, children: []]") ;
    }
}
