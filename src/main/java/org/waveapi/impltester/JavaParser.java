package org.waveapi.impltester;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.impl.AbstractJavaSourceMemberHolder;
import org.jboss.forge.roaster.model.impl.JavaEnumImpl;
import org.jboss.forge.roaster.model.source.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaParser {
    public static List<String> getSignaturesRecursively(File directory) {
        if (directory.isDirectory()) {
            List<String> signatures = new ArrayList<>();

            for (File file : directory.listFiles()) {
                List<String> sigs = getSignaturesRecursively(file);
                signatures.addAll(sigs);
            }

            return signatures;
        }

        return getSignatures(directory);
    }

    private static List<String> getSignatures(File javaFile) {
        if (!javaFile.getName().endsWith(".java"))
            return new ArrayList<>();

        try {
            JavaType<?> java = Roaster.parse(javaFile);

            if (java instanceof AbstractJavaSourceMemberHolder<?> holder) {
                List<String> sigs = getMemberSignatures(holder);

                if (java instanceof JavaEnumImpl enum_)
                    sigs.addAll(getEnumConstantsSignatures(enum_));

                return sigs;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>();
    }

    private static List<String> getMemberSignatures(AbstractJavaSourceMemberHolder<?> holder) {
        List<String> signatures = new ArrayList<>();

        for (MemberSource<?, ?> member : holder.getMembers()) {
            String sig;

            if (member instanceof MethodSource<?> method) {
                sig = SignatureGenerator.generateMethodSignature(method);
            } else if (member instanceof FieldSource<?> field && field.isPublic()) {
                sig = SignatureGenerator.generateFieldSignature(field);
            } else
                continue;

//            Main.LOGGER.info(sig);
            signatures.add(sig);
        }

        return signatures;
    }

    private static List<String> getEnumConstantsSignatures(JavaEnumImpl enum_) {
        List<String> signatures = new ArrayList<>();

        for (EnumConstantSource constant : enum_.getEnumConstants())
            signatures.add(SignatureGenerator.generateEnumConstantSignature(constant));

        return signatures;
    }
}
