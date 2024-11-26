FROM ubuntu:jammy

ARG JAR_FILE
# comma-separated list of profiles
ARG SPRING_PROFILES=production

ENV JAVA_SPRING_PROFILES=${SPRING_PROFILES}
ENV APP_JAR=/home/spring/${JAR_FILE}
ENV JAVA_OPTIONS=""

VOLUME ["/tmp"]

RUN apt-get -qq update && apt-get -y install -qq gnupg curl

# add Azul's public key
RUN curl -s https://repos.azul.com/azul-repo.key | \
    gpg --dearmor -o /usr/share/keyrings/azul.gpg && \
    echo "deb [signed-by=/usr/share/keyrings/azul.gpg] https://repos.azul.com/zulu/deb stable main" | \
    tee /etc/apt/sources.list.d/zulu.list

# install Azul Zulu JDK 21 and libSrtp
RUN apt-get update && apt-get -qq install -y zulu21-jdk libsrtp2-1

ENV JAVA_HOME=/usr/lib/jvm/zulu-21

RUN apt-get purge -y --auto-remove curl \
    gnupg \
    manpages \
    manpages-dev \
    python*  && \
    rm -f ./zulu-repo_1.0.0-3_all.deb

# update the package sources
RUN apt-get update && apt-get upgrade -y

CMD ["bash"]
ENV PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin" JAVA_HOME="/usr/lib/jvm/zulu21-ca"

RUN apt-get -qq install -y tini && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    set -eux && \
    adduser --uid 1000 --disabled-password --gecos "" spring

WORKDIR /home/spring

COPY --chown=spring:spring ${JAR_FILE} ${JAR_FILE}
COPY --chown=spring:spring entrypoint.sh entrypoint.sh

RUN chgrp -R 0 /home/spring && \
    chmod -R g=u /home/spring && \
    chown -R spring:0 /home/spring && \
    chmod 555 /home/spring/entrypoint.sh && \
    chmod a-w .* && \
    chmod a-w *

USER spring

EXPOSE 9089/tcp 9099/tcp

ENTRYPOINT ["tini", "-g", "--", "/bin/sh", "entrypoint.sh"]