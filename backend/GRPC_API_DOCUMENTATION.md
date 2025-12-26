# gRPC API Documentation

## Overview

The eBank application now exposes a gRPC service for high-performance account operations. gRPC provides efficient binary serialization and supports multiple programming languages.

## Connection Details

- **Host:** localhost
- **Port:** 9090
- **Protocol:** HTTP/2 (plaintext for development)

## Service Definition

**Package:** `ebank`  
**Service:** `AccountGrpcService`

## Protocol Buffer Definition

The complete `.proto` file is located at:
```
src/main/proto/account_service.proto
```

## Available RPCs

### 1. GetAccountByRib

Retrieves account information by RIB.

**Request:**
```protobuf
message GetAccountByRibRequest {
  string rib = 1;
}
```

**Response:**
```protobuf
message AccountResponse {
  int64 id = 1;
  string rib = 2;
  string balance = 3;
  string status = 4;
  string created_at = 5;
  string client_name = 6;
}
```

**Example using grpcurl:**
```bash
grpcurl -plaintext -d '{"rib": "TEST123456789012345678901"}' \
  localhost:9090 ebank.AccountGrpcService/GetAccountByRib
```

### 2. GetAccountById

Retrieves account information by ID.

**Request:**
```protobuf
message GetAccountByIdRequest {
  int64 id = 1;
}
```

**Example using grpcurl:**
```bash
grpcurl -plaintext -d '{"id": 1}' \
  localhost:9090 ebank.AccountGrpcService/GetAccountById
```

### 3. CreateAccount

Creates a new bank account.

**Request:**
```protobuf
message CreateAccountGrpcRequest {
  string rib = 1;
  string identity_number = 2;
}
```

**Example using grpcurl:**
```bash
grpcurl -plaintext -d '{"rib": "NEW123456789012345678901", "identity_number": "AB123456"}' \
  localhost:9090 ebank.AccountGrpcService/CreateAccount
```

## Testing with grpcurl

### Installation

**macOS:**
```bash
brew install grpcurl
```

**Linux:**
```bash
go install github.com/fullstorydev/grpcurl/cmd/grpcurl@latest
```

**Or download from:** https://github.com/fullstorydev/grpcurl/releases

### Common Commands

**List all services:**
```bash
grpcurl -plaintext localhost:9090 list
```

**Describe a service:**
```bash
grpcurl -plaintext localhost:9090 describe ebank.AccountGrpcService
```

**Call an RPC:**
```bash
grpcurl -plaintext -d '{"id": 1}' \
  localhost:9090 ebank.AccountGrpcService/GetAccountById
```

## Java Client Example

```java
import com.ebank.grpc.generated.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class AccountGrpcClient {
    
    public static void main(String[] args) {
        // Create a channel
        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 9090)
            .usePlaintext()
            .build();
        
        // Create a stub
        AccountGrpcServiceGrpc.AccountGrpcServiceBlockingStub stub = 
            AccountGrpcServiceGrpc.newBlockingStub(channel);
        
        try {
            // Call GetAccountById
            GetAccountByIdRequest request = GetAccountByIdRequest.newBuilder()
                .setId(1L)
                .build();
            
            AccountResponse response = stub.getAccountById(request);
            
            System.out.println("Account ID: " + response.getId());
            System.out.println("RIB: " + response.getRib());
            System.out.println("Balance: " + response.getBalance());
            System.out.println("Status: " + response.getStatus());
            System.out.println("Client: " + response.getClientName());
            
        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
        } finally {
            channel.shutdown();
        }
    }
}
```

## Python Client Example

First, install dependencies:
```bash
pip install grpcio grpcio-tools
```

Generate Python code from proto:
```bash
python -m grpc_tools.protoc -I src/main/proto \
  --python_out=. --grpc_python_out=. \
  src/main/proto/account_service.proto
```

Python client code:
```python
import grpc
from account_service_pb2 import GetAccountByIdRequest
from account_service_pb2_grpc import AccountGrpcServiceStub

def get_account(account_id):
    # Create a channel
    with grpc.insecure_channel('localhost:9090') as channel:
        # Create a stub
        stub = AccountGrpcServiceStub(channel)
        
        # Make the call
        request = GetAccountByIdRequest(id=account_id)
        try:
            response = stub.GetAccountById(request)
            print(f"Account ID: {response.id}")
            print(f"RIB: {response.rib}")
            print(f"Balance: {response.balance}")
            print(f"Status: {response.status}")
            print(f"Client: {response.client_name}")
        except grpc.RpcError as e:
            print(f"RPC failed: {e.code()} - {e.details()}")

if __name__ == '__main__':
    get_account(1)
```

## Error Handling

gRPC uses status codes for errors:

- **NOT_FOUND** (5): Account not found
- **INVALID_ARGUMENT** (3): Invalid request parameters
- **INTERNAL** (13): Internal server error

**Example error response:**
```
ERROR:
  Code: NotFound
  Message: Compte non trouvé: TEST123
```

## Performance Considerations

gRPC offers several advantages over REST:

- **Binary Protocol:** More efficient than JSON
- **HTTP/2:** Multiplexing, header compression
- **Streaming:** Supports bidirectional streaming (can be added later)
- **Code Generation:** Type-safe clients for multiple languages

## Security

Currently configured for **plaintext** communication (development only).

**For production:**

1. **Enable TLS:**
   ```properties
   grpc.server.security.enabled=true
   grpc.server.security.certificateChain=file:server.crt
   grpc.server.security.privateKey=file:server.key
   ```

2. **Add Authentication:**
   - Implement gRPC interceptors
   - Use JWT tokens in metadata
   - Configure mutual TLS (mTLS)

3. **Network Security:**
   - Use firewall rules
   - Implement rate limiting
   - Monitor and log all gRPC calls

## Monitoring

Enable gRPC metrics in `application.properties`:
```properties
grpc.server.enableReflection=true
management.endpoints.web.exposure.include=health,metrics
```

## Troubleshooting

**Connection refused:**
- Ensure the application is running
- Check port 9090 is not blocked by firewall
- Verify `grpc.server.port=9090` in application.properties

**Service not found:**
- Ensure proto files compiled successfully
- Check generated classes in `target/generated-sources/protobuf`
- Verify `@GrpcService` annotation on service implementation

**Method not implemented:**
- Ensure all RPC methods are implemented in `AccountGrpcServiceImpl`
- Check for compilation errors in the service class
