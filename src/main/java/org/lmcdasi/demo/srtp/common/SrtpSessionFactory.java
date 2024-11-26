package org.lmcdasi.demo.srtp.common;

import jakarta.annotation.Nonnull;
import org.lmcdasi.demo.srtp.jep454.srtp.SrtpSession;
import org.lmcdasi.demo.srtp.ApplicationProperties;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SrtpSessionFactory {
    private ApplicationProperties applicationProperties;
    private BeanFactory beanFactory;

    @Autowired
    public void setApplicationProperties(final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Autowired
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public @Nonnull SrtpSessionIfc buildSrtpSession(@Nonnull final String cipher) {
        if (applicationProperties.isUseJna()) {
            return buildJnaSrtpSession(cipher);
        } else if (applicationProperties.isUseBuiltinNative()) {
            return buildJep454SrtpSession(cipher);
        } else if (applicationProperties.isUseJni()) {
            return buildJniSrtpSession(cipher);
        }
        throw new InvalidPropertyException(SrtpSessionIfc.class, "application.use-....", "Wrong native setup");
    }

    private @Nonnull SrtpSessionIfc buildJnaSrtpSession(@Nonnull final String cipher) {
        final var jnaSrtpComponent = beanFactory.getBean(org.lmcdasi.demo.srtp.jna.srtp.SrtpComponent.class);
        return new org.lmcdasi.demo.srtp.jna.srtp.SrtpSession(jnaSrtpComponent.getLibSrtp(), cipher);
    }

    private @Nonnull SrtpSessionIfc buildJniSrtpSession(@Nonnull final String cipher) {
        return new org.lmcdasi.demo.srtp.jni.srtp.SrtpSession(cipher);
    }

    private @Nonnull SrtpSessionIfc buildJep454SrtpSession(@Nonnull final String cipher) {
        final var builtinNativeSrtpComponent = beanFactory.getBean(org.lmcdasi.demo.srtp.jep454.srtp.SrtpComponent.class);
        return new SrtpSession(builtinNativeSrtpComponent.getLibSrtpMehodMap(), cipher);
    }
}
