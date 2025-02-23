FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
ADD build/distributions/back-bones.tar ./
RUN mv back-bones/* ./
RUN chmod +x bin/back-bones
ENTRYPOINT ["/bin/sh", "bin/back-bones"]