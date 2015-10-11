/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;


import ninja.NinjaDocTester;
import ninja.Result;
import org.apache.commons.lang3.time.StopWatch;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ApiControllerDocTesterTest extends NinjaDocTester {


    public static final String CALCULATE_FIBONACCI = "/calculateFibonacci";

    @Test
    public void tesFibonacciCalculation(){
        Response response = makeRequest(
                Request.GET().url(
                        testServerUrl().path(CALCULATE_FIBONACCI).addQueryParameter("number", "10")));
        assertThat(response.httpStatus, equalTo(Result.SC_200_OK));
        assertThat(response.payload, equalTo("55"));
    }


    @Test
    public void testBigFibonacci(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Long result = ApplicationController.fibonacci(46);
        stopWatch.stop();
        System.out.println("Result["+result+"]. It took ["+stopWatch.getTime()+"]ms to calculate");
    }
}
