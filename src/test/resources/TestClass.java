package org.waveapi.test;

import org.waveapi.impltester.Main;

public class TestClass {
    public static int STATIC_FIELD;
    public Main FIELD;

    public TestClass() {

    }

    public void voidMethodWithNoParameters() {

    }

    public static void staticVoidMethodWithNoParameters() {

    }

    public int methodWithParametersReturningInt(int a, int b) {
        return 0;
    }

    public static Main staticMethodWithParametersReturningClass(Main a, Main b) {
        return null;
    }
}