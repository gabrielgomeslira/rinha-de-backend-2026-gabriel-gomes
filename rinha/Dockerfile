FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY . .
RUN sh ./gradlew installDist --no-daemon

FROM eclipse-temurin:21-jdk AS data
WORKDIR /data
COPY --from=build /src/build/install/rinha/lib ./lib
COPY resources/references.json.gz ./references.json.gz
RUN java -Xmx3g -cp "lib/*" gabriel.dev.build.Preprocess references.json.gz references.bin

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=build /src/build/install/rinha/lib ./lib
COPY --from=data /data/references.bin ./references.bin
ENV JAVA_OPTS="-Xmx96m -XX:+UseSerialGC -XX:MaxMetaspaceSize=48m -XX:ReservedCodeCacheSize=24m -Xss512k"
ENV REFERENCES_BIN="/app/references.bin"
EXPOSE 9999
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -cp \"lib/*\" gabriel.dev.http.Server"]
