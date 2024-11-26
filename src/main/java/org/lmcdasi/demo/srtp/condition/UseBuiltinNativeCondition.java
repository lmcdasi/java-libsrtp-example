package org.lmcdasi.demo.srtp.condition;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.lmcdasi.demo.srtp.condition.ExclusiveProperties.*;

public class UseBuiltinNativeCondition extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        final var useBuiltinNative = Boolean.
                parseBoolean(context.getEnvironment().getProperty(APPLICATION_USE_BUILTIN_NATIVE, "false"));
        final var useJna = Boolean.
                parseBoolean(context.getEnvironment().getProperty(APPLICATION_USE_JNA, "false"));
        final var useJni = Boolean.
                parseBoolean(context.getEnvironment().getProperty(APPLICATION_USE_JNI, "false"));

        if (useBuiltinNative && !useJna && !useJni) {
            return ConditionOutcome.match(STR."Matched '\{APPLICATION_USE_BUILTIN_NATIVE}'");
        }
        return ConditionOutcome.noMatch(STR."Only '\{APPLICATION_USE_BUILTIN_NATIVE}' is allowed.");
    }
}
