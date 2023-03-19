package org.waveapi.impltester;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JavaParserTest {
    private static final List<String> trueSignatures = new ArrayList<>();

    @BeforeAll
    public static void setup() {
        trueSignatures.add("org.waveapi.test.TestEnum.ELEMENT1");
        trueSignatures.add("org.waveapi.test.TestEnum.ELEMENT2");
        trueSignatures.add("org.waveapi.test.TestEnum.ELEMENT3");
        trueSignatures.add("org.waveapi.test.TestEnum#method(org.waveapi.impltester.Main) -> org.waveapi.test.TestEnum");

        trueSignatures.add("org.waveapi.test.TestClass.STATIC_FIELD -> int");
        trueSignatures.add("org.waveapi.test.TestClass#FIELD -> org.waveapi.impltester.Main");
        trueSignatures.add("org.waveapi.test.TestClass:ctor()");
        trueSignatures.add("org.waveapi.test.TestClass#voidMethodWithNoParameters() -> void");
        trueSignatures.add("org.waveapi.test.TestClass.staticVoidMethodWithNoParameters() -> void");
        trueSignatures.add("org.waveapi.test.TestClass#methodWithParametersReturningInt(int, int) -> int");
        trueSignatures.add("org.waveapi.test.TestClass.staticMethodWithParametersReturningClass(org.waveapi.impltester.Main, org.waveapi.impltester.Main) -> org.waveapi.impltester.Main");

        trueSignatures.add("org.waveapi.test.pkg1.TestClass2.A -> int");
        trueSignatures.add("org.waveapi.test.pkg1.TestClass2:ctor()");
        trueSignatures.add("org.waveapi.test.pkg1.TestClass2#foo1(int) -> void");
        trueSignatures.add("org.waveapi.test.pkg1.TestClass2.foo2() -> double");

        trueSignatures.add("org.waveapi.test.pkg1.pkg2.TestClass3#a -> org.waveapi.impltester.Main");
        trueSignatures.add("org.waveapi.test.pkg1.pkg2.TestClass3:ctor(float)");
        trueSignatures.add("org.waveapi.test.pkg1.pkg2.TestClass3#foo1() -> byte");
        trueSignatures.add("org.waveapi.test.pkg1.pkg2.TestClass3.foo2(byte, java.lang.String) -> void");

        trueSignatures.sort(Comparator.naturalOrder());
    }

    @Test
    public void testRecursiveSignatureParsing() {
        List<String> signatures = JavaParser.getSignaturesRecursively(new File("src/test/resources/"));
        signatures.sort(Comparator.naturalOrder());

        assertIterableEquals(signatures, trueSignatures);
    }
}
