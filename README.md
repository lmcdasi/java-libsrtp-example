Sample project that uses JNI, JNA or JDK21 java.lang.foreign to access the
'C' library libSrtp https://github.com/cisco/libsrtp api's

JNI performance ~90% higher that the others when running the srtp_unprotect.

In order to build:

-- install libsrtp2
-- install g++ 
-- install a JDK version that supports FFM API (at least even in preview mode).
-- set the JAVA_HOME in script jni/compile.sh

To run look at the src/main/resource/script/run.sh and manually copy/paste
the commands.
