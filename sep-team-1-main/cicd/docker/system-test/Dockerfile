# Author: Tim-Florian Feulner

FROM markhobson/maven-chrome:jdk-18

RUN microdnf install firefox

RUN wget -nv https://packages.microsoft.com/yumrepos/edge/microsoft-edge-stable-107.0.1418.62-1.x86_64.rpm \
    && microdnf install libatomic \
    && microdnf clean all \
    && rpm -i microsoft-edge-stable-107.0.1418.62-1.x86_64.rpm \
    && rm -f microsoft-edge-stable-107.0.1418.62-1.x86_64.rpm

RUN groupadd --system --gid 1000 test
RUN useradd --system --gid test --uid 1000 --shell /bin/bash --create-home test
RUN chown -R test:test /root
RUN chmod -R u+w /root

USER test
WORKDIR /home/test
