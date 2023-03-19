package org.waveapi.impltester;

import org.jboss.forge.roaster.model.Parameter;
import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.EnumConstantSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.MemberSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import java.util.List;

public class SignatureGenerator {
    public static String generateMethodSignature(MethodSource<?> method) {
        if (method.isConstructor())
            return generateConstructorSignature(method);

        String qualifiedName = generateQualifiedName(method);
        String params = generateParameterString(method.getParameters());
        String returnType = generateTypeString(method.getReturnType());

        return qualifiedName + "(" + params + ") -> " + returnType;
    }

    private static String generateConstructorSignature(MethodSource<?> constructor) {
        String clazz = constructor.getOrigin().getQualifiedName();
        String params = generateParameterString(constructor.getParameters());

        return clazz + ":ctor(" + params + ")";
    }

    public static String generateFieldSignature(FieldSource<?> field) {
        String qualifiedName = generateQualifiedName(field);
        String type = generateTypeString(field.getType());

        return qualifiedName + " -> " + type;
    }

    public static String generateEnumConstantSignature(EnumConstantSource constant) {
        String clazz = constant.getOrigin().getQualifiedName();
        String name = constant.getName();

        return clazz + "." + name;
    }


    private static String generateTypeString(Type<?> type) {
        String typeString = "void";

        if (!type.isType("void"))
            typeString = type.getQualifiedName();

        return typeString;
    }

    private static String generateQualifiedName(MemberSource<?, ?> member) {
        String clazz = member.getOrigin().getQualifiedName();
        String name = member.getName();
        char c = member.isStatic() ? '.' : '#';

        return clazz + c + name;
    }

    private static String generateParameterString(List<? extends Parameter<?>> params) {
        StringBuilder sb = new StringBuilder();

        for (Parameter<?> param : params) {
            if (!sb.isEmpty())
                sb.append(", ");

            sb.append(param.getType().getQualifiedName());
        }

        return sb.toString();
    }
}
