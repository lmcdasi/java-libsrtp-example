package org.lmcdasi.demo.srtp.jep454.srtp;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.lmcdasi.demo.srtp.common.SrtpErrStatus;
import org.lmcdasi.demo.srtp.condition.UseBuiltinNativeCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import org.lmcdasi.demo.srtp.ApplicationProperties;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
@Conditional(UseBuiltinNativeCondition.class)
public class SrtpComponent {
	private final static Logger LOGGER = LoggerFactory.getLogger(SrtpComponent.class);

	private ApplicationProperties applicationProperties;
	private Arena srtpArena;
	@Getter
	private LibSrtpMehodMap libSrtpMehodMap;
	private SymbolLookup libSrtp;


	@Getter
	public static class LibSrtpMehodMap {
		private final MethodHandle srtp_create;
		private final MethodHandle srtp_crypto_policy_set_rtp_default;
		private final MethodHandle srtp_crypto_policy_set_aes_cm_128_hmac_sha1_32;
		private final MethodHandle srtp_crypto_policy_set_aes_cm_256_hmac_sha1_32;
		private final MethodHandle srtp_crypto_policy_set_aes_cm_256_hmac_sha1_80;
		private final MethodHandle srtp_dealloc;
		private final MethodHandle srtp_unprotect;

		LibSrtpMehodMap(@Nonnull final MethodHandle srtpCreateMethodHandler,
						@Nonnull final MethodHandle srtpCryptoPolicySetRtpDefault,
						@Nonnull final MethodHandle srtpCryptoPolicySetAesCm128HmacSha132,
						@Nonnull final MethodHandle srtpCryptoPolicySetAesCm256HmacSha132,
						@Nonnull final MethodHandle srtpCryptoPolicySetAesCm256HmacSha180,
						@Nonnull final MethodHandle srtpDealloc,
						@Nonnull final MethodHandle srtpUnprotect) {
			srtp_create = srtpCreateMethodHandler;
			srtp_crypto_policy_set_rtp_default = srtpCryptoPolicySetRtpDefault;
			srtp_crypto_policy_set_aes_cm_128_hmac_sha1_32 = srtpCryptoPolicySetAesCm128HmacSha132;
			srtp_crypto_policy_set_aes_cm_256_hmac_sha1_32 = srtpCryptoPolicySetAesCm256HmacSha132;
			srtp_crypto_policy_set_aes_cm_256_hmac_sha1_80 = srtpCryptoPolicySetAesCm256HmacSha180;
			srtp_dealloc = srtpDealloc;
			srtp_unprotect = srtpUnprotect;
		}
	}
	
	@Autowired
	public void setApplicationProperties(final ApplicationProperties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}
	
	@PostConstruct
	void init() {
		LOGGER.info("SrtpComponent init");
		configLibSrtp();

		final var linker = Linker.nativeLinker();
		srtpInit(linker);

		final var srtpCreateMethodHandler = getSrtpCreateMethodHandler(linker);
		final var srtpCryptoPolicySetRtpDefault = getSrtpCryptoPolicySetRtpDefault(linker);
		final var srtpCryptoPpolicySetAesCm128HmacSha132 = getSrtpCryptoPpolicySetAesCm128HmacSha132(linker);
		final var srtpCryptoPolicySetAesCm256HmacSha132 = getSrtpCryptoPolicySetAesCm256HmacSha132(linker);
		final var srtpCryptoPolicySetAesCm256HmacSha180 = getSrtpCryptoPolicySetAesCm256HmacSha180(linker);
		final var srtpDeallocMethodHandler = getSrtpDealloc(linker);
		final var srtpUnprotect = getSrtpUnprotect(linker);

		libSrtpMehodMap = new LibSrtpMehodMap(srtpCreateMethodHandler, srtpCryptoPolicySetRtpDefault,
				srtpCryptoPpolicySetAesCm128HmacSha132, srtpCryptoPolicySetAesCm256HmacSha132,
				srtpCryptoPolicySetAesCm256HmacSha180, srtpDeallocMethodHandler,
				srtpUnprotect);
	}
	
	@PreDestroy
	void unload() {
		try {
			srtpArena.close();
		} catch (final Exception e) {
			LOGGER.error("Abnormal close of srtp Arena");
		}
	}

	private void configLibSrtp() {
		srtpArena = Arena.ofShared();

		final var soSrtpPath = Path.of("/usr/lib/x86_64-linux-gnu/libsrtp2.so.1");
		libSrtp = SymbolLookup.libraryLookup(soSrtpPath, srtpArena);
	}

	private void srtpInit(final Linker linker) {
		libSrtp.find("srtp_init").ifPresentOrElse(
				memorySegment -> {
					final var methodHandler = linker.downcallHandle(memorySegment,
							FunctionDescriptor.of(ValueLayout.JAVA_INT), Linker.Option.isTrivial());
					try {
						final var status = (int) methodHandler.invoke();

						LOGGER.info("srtp_init status {}.",	Arrays.stream(SrtpErrStatus.values()).
								filter(e -> e.getValue() == status).findFirst().get());

						if (applicationProperties.isDebugSrtp()) {
							try {
								setSrtpDebug(linker, libSrtp);
							} catch (final Exception e) {
								LOGGER.warn("Failed to set srtp log callback. Continue without srtp logs.", e);
							}
						}
					} catch (final Throwable e) {
						throw new BeanCreationException("Unable to init srtp library", e);
					}
				},
				() -> {
					throw new BeanCreationException("Unable to find srtp_init.");
				}
		);
	}

	private MethodHandle getSrtpCreateMethodHandler(final Linker linker) {
		AtomicReference<MethodHandle> srtpCreateMethodHandler = new AtomicReference<>();
		libSrtp.find("srtp_create").ifPresentOrElse(
				memorySegment -> {
					srtpCreateMethodHandler.set(linker.downcallHandle(memorySegment,
							FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS),
							Linker.Option.isTrivial()));
				},
				() -> {
					throw new BeanCreationException("Unable to find srtp_create.");
				}
		);
		return srtpCreateMethodHandler.get();
	}

	private MethodHandle getSrtpCryptoPolicySetRtpDefault(@Nonnull final Linker linker) {
		AtomicReference<MethodHandle> srtpCryptoPolicySetRtpDefault = new AtomicReference<>();
		libSrtp.find("srtp_crypto_policy_set_rtp_default").ifPresentOrElse(
				memorySegment -> {
					srtpCryptoPolicySetRtpDefault.set(linker.downcallHandle(memorySegment,
							FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
							Linker.Option.isTrivial()));
				},
				() -> {}
		);
		return srtpCryptoPolicySetRtpDefault.get();
	}

	private MethodHandle getSrtpCryptoPpolicySetAesCm128HmacSha132(@Nonnull final Linker linker) {
		AtomicReference<MethodHandle> srtpCryptoPpolicySetAesCm128HmacSha132 = new AtomicReference<>();
		libSrtp.find("srtp_crypto_policy_set_aes_cm_128_hmac_sha1_32").ifPresentOrElse(
				memorySegment -> {
					srtpCryptoPpolicySetAesCm128HmacSha132.set(linker.downcallHandle(memorySegment,
							FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
							Linker.Option.isTrivial()));
				},
				() -> {
					throw new BeanCreationException("Unable to find srtp_crypto_policy_set_aes_cm_128_hmac_sha1_32.");
				}
		);
		return srtpCryptoPpolicySetAesCm128HmacSha132.get();
	}

	private MethodHandle getSrtpCryptoPolicySetAesCm256HmacSha132(@Nonnull final Linker linker) {
		AtomicReference<MethodHandle> srtpCryptoPolicySetAesCm256HmacSha132 = new AtomicReference<>();
		libSrtp.find("srtp_crypto_policy_set_aes_cm_256_hmac_sha1_32").ifPresentOrElse(
				memorySegment -> {
					srtpCryptoPolicySetAesCm256HmacSha132.set(linker.downcallHandle(memorySegment,
							FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
							Linker.Option.isTrivial()));
				},
				() -> {
					throw new BeanCreationException("Unable to find srtp_crypto_policy_set_aes_cm_256_hmac_sha1_32");
				}
		);
		return srtpCryptoPolicySetAesCm256HmacSha132.get();
	}

	private MethodHandle getSrtpCryptoPolicySetAesCm256HmacSha180(@Nonnull final Linker linker) {
		AtomicReference<MethodHandle> srtpCryptoPolicySetAesCm256HmacSha180 = new AtomicReference<>();
		libSrtp.find("srtp_crypto_policy_set_aes_cm_256_hmac_sha1_80").ifPresentOrElse(
				memorySegment -> {
					srtpCryptoPolicySetAesCm256HmacSha180.set(linker.downcallHandle(memorySegment,
							FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
							Linker.Option.isTrivial()));
				},
				() -> {
					throw new BeanCreationException("Unable to find srtp_crypto_policy_set_aes_cm_256_hmac_sha1_80");
				}
		);
		return srtpCryptoPolicySetAesCm256HmacSha180.get();
	}

	private MethodHandle getSrtpDealloc(@Nonnull final Linker linker) {
		AtomicReference<MethodHandle> srtpDealloc = new AtomicReference<>();
		libSrtp.find("srtp_dealloc").ifPresentOrElse(
				memorySegment -> {
					srtpDealloc.set(linker.downcallHandle(memorySegment,
							FunctionDescriptor.of(ValueLayout.ADDRESS),
							Linker.Option.isTrivial()));
				},
				() -> {
					throw new BeanCreationException("Unable to find srtp_dealloc");
				}
		);
		return srtpDealloc.get();
	}

	private MethodHandle getSrtpUnprotect(@Nonnull final Linker linker) {
		AtomicReference<MethodHandle> srtpUnprotect = new AtomicReference<>();
		libSrtp.find("srtp_unprotect").ifPresentOrElse(
				memorySegment -> {
					srtpUnprotect.set(linker.downcallHandle(memorySegment,
							FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS,
									ValueLayout.ADDRESS), Linker.Option.isTrivial()));
				},
				() -> {
					throw new BeanCreationException("Unable to find srtp_unprotect");
				}
		);
		return srtpUnprotect.get();
	}
	
	private void setSrtpDebug(@Nonnull final Linker linker, @Nonnull final SymbolLookup libSrtp) throws NoSuchMethodException, IllegalAccessException {
		final var logCallbackDescription = FunctionDescriptor.of(
				ValueLayout.ADDRESS.withTargetLayout(ValueLayout.JAVA_INT),
				ValueLayout.ADDRESS.withTargetLayout(ValueLayout.JAVA_INT),
				ValueLayout.ADDRESS.withTargetLayout(ValueLayout.ADDRESS),
				ValueLayout.ADDRESS.withTargetLayout(ValueLayout.ADDRESS)).dropReturnLayout();
		final var logCallbackHandler = MethodHandles.lookup().findStatic(SrtpLogCallback.class, "callback",
				logCallbackDescription.toMethodType());
		final var comparFunc = linker.upcallStub(logCallbackHandler, logCallbackDescription, srtpArena);
		
		libSrtp.find("srtp_install_log_handler").ifPresentOrElse(
			memorySegment -> {
				final var srtpInstallLogHandlerFuntionDesc = FunctionDescriptor.of(
							ValueLayout.JAVA_INT, 
							ValueLayout.ADDRESS,
							ValueLayout.ADDRESS);
				final var methodHandler = linker.downcallHandle(memorySegment, srtpInstallLogHandlerFuntionDesc, Linker.Option.isTrivial());
				try {
					final var status = (int) methodHandler.invoke(comparFunc, MemorySegment.NULL);
					LOGGER.info("srtp_install_log_handler status {}.", 
							Arrays.stream(SrtpErrStatus.values()).filter(e -> e.getValue() == status).findFirst().get());
				} catch (final Throwable e) {
					LOGGER.warn("Failed to invoke srtp_install_log_handler.", e);
				}
			},
			()-> {
					LOGGER.warn("No srtp_install_log_handler found. Unable to set srtp log callback.");
			}
		);
		
		libSrtp.find("srtp_set_debug_module").ifPresentOrElse(
			memorySegment -> {
				final var srtpSetDebugModuleFunctionDesc = FunctionDescriptor.of(
							ValueLayout.JAVA_INT,
							ValueLayout.ADDRESS,
							ValueLayout.JAVA_INT);
				final var methodHandler = linker.downcallHandle(memorySegment, srtpSetDebugModuleFunctionDesc, Linker.Option.isTrivial());
				final var modNameMemorySegment = srtpArena.allocateUtf8String("srtp");
				try {
					final var status = (int) methodHandler.invoke(modNameMemorySegment, 1);
					LOGGER.info("srtp_set_debug_module status {}.", 
							Arrays.stream(SrtpErrStatus.values()).filter(e -> e.getValue() == status).findFirst().get());
				} catch (final Throwable e) {
					LOGGER.warn("Failed to set srtp debug module.", e);
				}
			},
			() -> {
				LOGGER.warn("No srtp_set_debug_module found. Unable to set srtp debug module.");
			}
		);

	}
}
