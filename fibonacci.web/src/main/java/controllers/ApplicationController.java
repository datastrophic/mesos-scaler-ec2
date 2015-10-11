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

import com.google.inject.Singleton;
import ninja.Result;
import ninja.Results;
import ninja.metrics.Timed;
import ninja.params.Param;


@Singleton
public class ApplicationController {


    @Timed
    public Result calculateFibonacci(@Param("number")long number){
        return Results.ok().json().render(fibonacci(number));
    }
    public static long fibonacci(long number) {
        int prev1=0, prev2=1;
        for (int i = 0; i < number; i++) {
            int savePrev1 = prev1;
            prev1 = prev2;
            prev2 = savePrev1 + prev2;
        }
        return prev1;
    }

    public static long fibonacciRecur(long number){
        if(number == 1 || number == 2){
            return 1;
        }
        return fibonacci(number-1) + fibonacci(number -2);
    }

}
