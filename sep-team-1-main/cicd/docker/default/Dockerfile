# Author: Tim-Florian Feulner

FROM maven:3.8.6-eclipse-temurin-18

RUN apt-get update && apt-get install -y --no-install-recommends zip=3.0-12build2 && rm -rf /var/lib/apt/lists/*

RUN groupadd --system --gid 1000 test
RUN useradd --system --gid test --uid 1000 --shell /bin/bash --create-home test
RUN chown -R test:test /root
RUN chmod -R u+w /root

USER test
WORKDIR /home/test
