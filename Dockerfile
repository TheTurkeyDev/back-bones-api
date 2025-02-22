FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
ADD build/distributions/backbones.tar ./
RUN mv backbones/* ./
RUN chmod +x bin/backbones
ENTRYPOINT ["/bin/sh", "bin/backbones"]