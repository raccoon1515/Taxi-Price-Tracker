FROM ghcr.io/graalvm/native-image:java11 as builder

WORKDIR tpt-native-build
#RUN curl -L -o musl.tar.gz https://github.com/gradinac/musl-bundle-example/releases/download/v1.0/musl.tar.gz && \
#    tar -xvzf musl.tar.gz
COPY . .
RUN ./gradlew nativeCompile

FROM gcr.io/distroless/base
WORKDIR tpt
COPY --from=builder /tpt-native-build/build/native/nativeCompile/TaxiPriceTracker .
ENTRYPOINT ["/tpt/TaxiPriceTracker"]
