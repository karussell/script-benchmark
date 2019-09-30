/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.graphhopper;

import org.codehaus.janino.ScriptEvaluator;
import org.openjdk.jmh.annotations.Benchmark;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MyBenchmark {

    public static void main(String[] args) throws Exception {
//        System.out.println(se.getMethod(0).invoke(null, 5));
        System.out.println();
    }

    private static final int CHAINS = 100;
    private static final List<Double> list = new ArrayList<>();
    private static final ChainedMultiply root;
    private static final ScriptEvaluator se = new ScriptEvaluator();
    private static final Script script;

    static {
        for (int i = 0; i < CHAINS; i++) {
            list.add(i + 1.0);
        }

        ChainedMultiply tmpStart = new ChainedMultiply();
        root = tmpStart;
        for (int i = 0; i <= CHAINS; i++) {
            tmpStart.var = i + 1.0;
            tmpStart.multiply = new ChainedMultiply();
            tmpStart = tmpStart.multiply;
        }
        // last operation is no-op
        tmpStart.multiply = new ChainedMultiply() {
            @Override
            double multiply() {
                return 1;
            }
        };

        try {
//            se.setOverrideMethod(new boolean[]{false});
//            se.setStaticMethod(new boolean[]{true});
//            se.setMethodNames(new String[]{"mymethod"});
//            se.setParameters(new String[]{"calls"}, new Class<?>[]{int.class});
//            se.setReturnTypes(new Class[]{double.class});
//            se.cook("double result = 1;for (int i = 0; i < calls; i++) { result *= (i + 1.0); }; return result;");
            se.createFastEvaluator("double result = 1;for (int i = 0; i < calls; i++) { result *= (i + 1.0); }; return result;",
                    Script.class, new String[]{"calls"});
            script = (Script) se.getMethod().getDeclaringClass().getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static class ChainedMultiply {
        double var = 1;
        ChainedMultiply multiply;

        double multiply() {
            return var * multiply.multiply();
        }
    }

    @Benchmark
    public double testMethodChained() {
        return root.multiply();
    }

    @Benchmark
    public double testLoop() {
        double result = 1;
        for (Double value : list) {
            result *= (value + 1.0);
        }
        return result;
    }

    @Benchmark
    public double testRawListAccessLoop() {
        double result = 1;
        for (int i = 0; i < list.size(); i++) {
            result *= list.get(i);
        }
        return result;
    }

    @Benchmark
    public double testRawValueLoop() {
        double result = 1;
        for (int i = 0; i < CHAINS; i++) {
            result *= (i + 1.0);
        }
        return result;
    }

    @Benchmark
    public double testScript() throws InvocationTargetException, IllegalAccessException {
//        return (double) se.getMethod(0).invoke(null, CHAINS);
        return script.mymethod(CHAINS);
    }
}
