# Telemetry

- server - go
- client - kotlin


Command for generating client code:
- telemetry.proto needs to be copied to the client
- ./gradlew build


Command for generating server code:
- `protoc --go_out=go-server --go-grpc_out=go-server telemetry.proto`
