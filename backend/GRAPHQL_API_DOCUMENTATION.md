# GraphQL API Documentation

## Overview

The eBank application now exposes a GraphQL API for flexible, efficient account operations. GraphQL allows clients to request exactly the data they need with a single query.

## GraphQL Endpoint

- **URL:** `http://localhost:8080/graphql`
- **Method:** POST
- **Content-Type:** `application/json`

## GraphiQL Interactive Interface

GraphiQL is enabled for easy testing and exploration:

- **URL:** `http://localhost:8080/graphiql`
- **Features:**
  - Interactive query editor with autocomplete
  - Real-time syntax validation
  - Documentation explorer
  - Query history

Just open the URL in your browser to start exploring!

## Schema Overview

### Queries

#### getAccountById
Retrieve account information by ID.

**Query:**
```graphql
query {
  getAccountById(id: 1) {
    id
    rib
    balance
    status
    createdAt
    clientName
  }
}
```

**Response:**
```json
{
  "data": {
    "getAccountById": {
      "id": "1",
      "rib": "TEST123456789012345678901",
      "balance": "1000.00",
      "status": "OPEN",
      "createdAt": "2024-01-15T10:30:00",
      "clientName": "John Doe"
    }
  }
}
```

#### get AccountByRib
Retrieve account information by RIB (Bank Account Number).

**Query:**
```graphql
query {
  getAccountByRib(rib: "TEST123456789012345678901") {
    id
    rib
    balance
    status
    clientName
  }
}
```

**Field Selection:**
GraphQL allows you to request only the fields you need:
```graphql
query {
  getAccountByRib(rib: "TEST123456789012345678901") {
    balance
    status
  }
}
```

#### getAccountsByClientId
Retrieve all accounts for a specific client.

**Query:**
```graphql
query {
  getAccountsByClientId(clientId: 5) {
    id
    rib
    balance
    status
  }
}
```

### Mutations

#### createAccount
Create a new bank account.

**Mutation:**
```graphql
mutation {
  createAccount(input: {
    rib: "NEW123456789012345678901"
    identityNumber: "AB123456"
  }) {
    id
    rib
    balance
    status
    clientName
  }
}
```

**Response:**
```json
{
  "data": {
    "createAccount": {
      "id": "10",
      "rib": "NEW123456789012345678901",
      "balance": "0.00",
      "status": "OPEN",
      "clientName": "Jane Smith"
    }
  }
}
```

## Testing with cURL

**Execute a Query:**
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ getAccountById(id: 1) { id rib balance status clientName } }"
  }'
```

**Execute a Mutation:**
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { createAccount(input: { rib: \"NEW123456789012345678901\", identityNumber: \"AB123456\" }) { id rib balance status } }"
  }'
```

**With Variables:**
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query GetAccount($id: ID!) { getAccountById(id: $id) { id rib balance } }",
    "variables": { "id": "1" }
  }'
```

## Advanced GraphQL Features

### Aliases
Query the same field with different arguments:
```graphql
query {
  account1: getAccountById(id: 1) {
    rib
    balance
  }
  account2: getAccountById(id: 2) {
    rib
    balance
  }
}
```

### Fragments
Reuse common field selections:
```graphql
query {
  getAccountById(id: 1) {
    ...accountFields
  }
  getAccountByRib(rib: "TEST123") {
    ...accountFields
  }
}

fragment accountFields on Account {
  id
  rib
  balance
  status
  clientName
}
```

### Inline Fragments & Type Conditions
Not applicable for this simple schema, but useful for complex schemas with interfaces and unions.

## Error Handling

GraphQL returns errors in a structured format:

**Example Error (Account Not Found):**
```json
{
  "errors": [
    {
      "message": "Compte non trouvé",
      "locations": [
        {
          "line": 2,
          "column": 3
        }
      ],
      "path": [
        "getAccountById"
      ],
      "extensions": {
        "classification": "DataFetchingException"
      }
    }
  ],
  "data": {
    "getAccountById": null
  }
}
```

**Validation Errors:**
```json
{
  "errors": [
    {
      "message": "Validation error of type FieldUndefined: Field 'invalidField' is undefined",
      "locations": [
        {
          "line": 2,
          "column": 5
        }
      ],
      "extensions": {
        "classification": "ValidationError"
      }
    }
  ]
}
```

## Client Libraries

### JavaScript (Apollo Client)

```javascript
import { ApolloClient, InMemoryCache, gql } from '@apollo/client';

const client = new ApolloClient({
  uri: 'http://localhost:8080/graphql',
  cache: new InMemoryCache()
});

// Query
client.query({
  query: gql`
    query GetAccount($id: ID!) {
      getAccountById(id: $id) {
        id
        rib
        balance
        status
        clientName
      }
    }
  `,
  variables: { id: '1' }
}).then(result => console.log(result));

// Mutation
client.mutate({
  mutation: gql`
    mutation CreateAccount($input: CreateAccountInput!) {
      createAccount(input: $input) {
        id
        rib
        balance
      }
    }
  `,
  variables: {
    input: {
      rib: 'NEW123456789012345678901',
      identityNumber: 'AB123456'
    }
  }
}).then(result => console.log(result));
```

### Java (Spring GraphQL Client)

```java
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

public class GraphQLClientExample {
    
    public static void main(String[] args) {
        WebClient webClient = WebClient.create("http://localhost:8080/graphql");
        HttpGraphQlClient graphQlClient = HttpGraphQlClient.create(webClient);
        
        String query = """
            query GetAccount($id: ID!) {
                getAccountById(id: $id) {
                    id
                    rib
                    balance
                    status
                    clientName
                }
            }
        """;
        
        graphQlClient.document(query)
            .variable("id", "1")
            .retrieve("getAccountById")
            .toEntity(AccountDTO.class)
            .subscribe(account -> {
                System.out.println("Account: " + account);
            });
    }
}
```

### Python (gql)

```python
from gql import gql, Client
from gql.transport.requests import RequestsHTTPTransport

# Create a transport
transport = RequestsHTTPTransport(
    url='http://localhost:8080/graphql',
    headers={'Content-Type': 'application/json'}
)

# Create a client
client = Client(transport=transport, fetch_schema_from_transport=True)

# Execute a query
query = gql('''
    query GetAccount($id: ID!) {
        getAccountById(id: $id) {
            id
            rib
            balance
            status
            clientName
        }
    }
''')

result = client.execute(query, variable_values={'id': '1'})
print(result)
```

## Performance Considerations

### Advantages of GraphQL

- **No Over-fetching:** Request only the fields you need
- **No Under-fetching:** Get all related data in one request
- **Strongly Typed:** Schema provides type safety and documentation
- **Single Endpoint:** Simpler API surface

### N+1 Query Problem

For queries that return lists, consider using DataLoader to batch database queries:
```java
@Bean
public DataLoader<Long, AccountDTO> accountDataLoader(AccountService accountService) {
    return DataLoader.newDataLoader(ids -> 
        CompletableFuture.supplyAsync(() -> accountService.getAccountsByIds(ids))
    );
}
```

## Security

Currently configured for **unauthenticated access** (development only).

**For production:**

1. **Add Authentication:**
   - Use GraphQL context to pass JWT tokens
   - Implement security directives in schema
   - Validate permissions in resolvers

2. **Query Complexity Analysis:**
   ```properties
   spring.graphql.query.max-query-depth=10
   spring.graphql.query.max-query-complexity=100
   ```

3. **Rate Limiting:**
   - Implement per-user request limits
   - Monitor query complexity
   - Set timeouts for long-running queries

## Introspection

GraphQL supports introspection for schema exploration:

```graphql
query {
  __schema {
    types {
      name
      description
    }
  }
}
```

**Disable in production:**
```properties
spring.graphql.schema.introspection.enabled=false
```

## Schema Documentation

The complete GraphQL schema is available at:
```
src/main/resources/graphql/schema.graphqls
```

## Monitoring

Enable GraphQL metrics:
```properties
management.metrics.enable.graphql=true
```

Monitor:
- Query execution time
- Error rates
- Field resolution performance
- Query complexity

## Troubleshooting

**GraphiQL not loading:**
- Ensure `spring.graphql.graphiql.enabled=true`
- Check URL: `http://localhost:8080/graphiql`
- Verify security configuration allows `/graphiql`

**Schema not found:**
- Ensure `schema.graphqls` is in `src/main/resources/graphql/`
- Check for syntax errors in schema
- Verify file extension is `.graphqls`

**Resolver not found:**
- Ensure `@QueryMapping`/`@MutationMapping` annotations
- Verify method names match schema fields
- Check `@Argument` parameter names match schema
