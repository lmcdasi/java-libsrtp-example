#!/bin/sh

export JAVA_HOME="/usr/lib/jvm/msopenjdk-21-amd64"

#g++ -shared -o libsrtpjni.so -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -I"/usr/include/srtp2" -I"./" \
#    -fPIC SrtpComponent.cpp -lsrtp2

g++ -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -I"/usr/include/srtp2" -I"./"\
    -fPIC -Wall -pedantic -g -O3 -fexpensive-optimizations -funroll-loops -c\
    -o SrtpComponent.o SrtpComponent.cpp
g++ -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -I"/usr/include/srtp2" -I"./"\
    -fPIC -Wall -pedantic -g -O3 -fexpensive-optimizations -funroll-loops -c\
    -o SrtpContext.o SrtpContext.cpp
g++ -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -I"/usr/include/srtp2" -I"./"\
    -fPIC -Wall -pedantic -g -O3 -fexpensive-optimizations -funroll-loops -c\
    -o SrtpSession.o SrtpSession.cpp

g++ -shared -o libsrtpjni.so SrtpComponent.o SrtpContext.o SrtpSession.o -lsrtp2

cp libsrtpjni.so ../src/main/resources/native/libsrtpjni.so

