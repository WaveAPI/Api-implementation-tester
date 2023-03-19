package org.waveapi.impltester;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.impl.JavaClassImpl;
import org.jboss.forge.roaster.model.impl.JavaEnumImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SignatureGeneratorTest {
    private static JavaClassImpl clazz;
    private static JavaEnumImpl enum_;

    @BeforeAll
    public static void setup() {
        try {
            clazz = Roaster.parse(JavaClassImpl.class, new File("src/test/resources/TestClass.java"));
            enum_ = Roaster.parse(JavaEnumImpl.class, new File("src/test/resources/TestEnum.java"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testClassConstructor() {
        String ctor = generateClassMethodSignature("TestClass");
        assertEquals("org.waveapi.test.TestClass:ctor()", ctor);
    }

    @Test
    public void testVoidClassMethods() {
        String voidMethodWithNoParameters = generateClassMethodSignature("voidMethodWithNoParameters");
        String staticVoidMethodWithNoParameters = generateClassMethodSignature("staticVoidMethodWithNoParameters");

        assertEquals("org.waveapi.test.TestClass#voidMethodWithNoParameters() -> void", voidMethodWithNoParameters);
        assertEquals("org.waveapi.test.TestClass.staticVoidMethodWithNoParameters() -> void", staticVoidMethodWithNoParameters);
    }

    @Test
    public void testClassMethodsWithReturn() {
        String methodWithParametersReturningInt = generateClassMethodSignature("methodWithParametersReturningInt", "int", "int");
        String staticMethodWithParametersReturningClass = generateClassMethodSignature("staticMethodWithParametersReturningClass", "Main", "Main");

        assertEquals("org.waveapi.test.TestClass#methodWithParametersReturningInt(int, int) -> int", methodWithParametersReturningInt);
        assertEquals("org.waveapi.test.TestClass.staticMethodWithParametersReturningClass(org.waveapi.impltester.Main, org.waveapi.impltester.Main) -> org.waveapi.impltester.Main", staticMethodWithParametersReturningClass);
    }

    @Test
    public void testClassFields() {
        String staticField = generateClassFieldSignature("STATIC_FIELD");
        String field = generateClassFieldSignature("FIELD");

        assertEquals("org.waveapi.test.TestClass.STATIC_FIELD -> int", staticField);
        assertEquals("org.waveapi.test.TestClass#FIELD -> org.waveapi.impltester.Main", field);
    }


    @Test
    public void testEnumElements() {
        String element1 = generateEnumConstantSignature("ELEMENT1");
        String element2 = generateEnumConstantSignature("ELEMENT2");
        String element3 = generateEnumConstantSignature("ELEMENT3");

        assertEquals("org.waveapi.test.TestEnum.ELEMENT1", element1);
        assertEquals("org.waveapi.test.TestEnum.ELEMENT2", element2);
        assertEquals("org.waveapi.test.TestEnum.ELEMENT3", element3);
    }

    @Test
    public void testEnumMethod() {
        String method = generateEnumMethodSignature("method", "Main");
        assertEquals("org.waveapi.test.TestEnum#method(org.waveapi.impltester.Main) -> org.waveapi.test.TestEnum", method);
    }


    private String generateClassMethodSignature(String method, String... params) {
        return SignatureGenerator.generateMethodSignature(clazz.getMethod(method, params));
    }

    private String generateClassFieldSignature(String field) {
        return SignatureGenerator.generateFieldSignature(clazz.getField(field));
    }

    private String generateEnumConstantSignature(String constant) {
        return SignatureGenerator.generateEnumConstantSignature(enum_.getEnumConstant(constant));
    }

    private String generateEnumMethodSignature(String method, String... params) {
        return SignatureGenerator.generateMethodSignature(enum_.getMethod(method, params));
    }
}
