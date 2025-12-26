# SOAP API Documentation

## Overview

The eBank application now exposes a SOAP web service for account operations. This service provides an alternative to the REST API for clients that prefer or require SOAP-based communication.

## WSDL Location

Once the application is running, the WSDL is available at:
```
http://localhost:8080/ws/accounts.wsdl
```

## Namespace

All SOAP operations use the namespace:
```
http://ebank.com/soap/accounts
```

## Available Operations

### 1. GetAccountByRib

Retrieves account information by RIB (Bank Account Number).

**Request:**
```xml
<?xml version="1.0"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:acc="http://ebank.com/soap/accounts">
   <soapenv:Header/>
   <soapenv:Body>
      <acc:getAccountByRibRequest>
         <acc:rib>TEST123456789012345678901</acc:rib>
      </acc:getAccountByRibRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

**Response:**
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Body>
      <ns2:getAccountByRibResponse xmlns:ns2="http://ebank.com/soap/accounts">
         <ns2:account>
            <ns2:id>1</ns2:id>
            <ns2:rib>TEST123456789012345678901</ns2:rib>
            <ns2:balance>1000.00</ns2:balance>
            <ns2:status>OPEN</ns2:status>
            <ns2:createdAt>2024-01-15T10:30:00</ns2:createdAt>
            <ns2:clientName>John Doe</ns2:clientName>
         </ns2:account>
      </ns2:getAccountByRibResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

### 2. GetAccountById

Retrieves account information by ID.

**Request:**
```xml
<?xml version="1.0"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:acc="http://ebank.com/soap/accounts">
   <soapenv:Header/>
   <soapenv:Body>
      <acc:getAccountByIdRequest>
         <acc:id>1</acc:id>
      </acc:getAccountByIdRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### 3. CreateAccount

Creates a new bank account.

**Request:**
```xml
<?xml version="1.0"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:acc="http://ebank.com/soap/accounts">
   <soapenv:Header/>
   <soapenv:Body>
      <acc:createAccountRequest>
         <acc:rib>NEW123456789012345678901</acc:rib>
         <acc:identityNumber>AB123456</acc:identityNumber>
      </acc:createAccountRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

## Testing with cURL

```bash
# Get account by RIB
curl -X POST http://localhost:8080/ws \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:acc="http://ebank.com/soap/accounts">
   <soapenv:Header/>
   <soapenv:Body>
      <acc:getAccountByRibRequest>
         <acc:rib>TEST123456789012345678901</acc:rib>
      </acc:getAccountByRibRequest>
   </soapenv:Body>
</soapenv:Envelope>'
```

## Testing with SoapUI

1. Create a new SOAP Project in SoapUI
2. Use the WSDL URL: `http://localhost:8080/ws/accounts.wsdl`
3. SoapUI will automatically generate request templates for all operations
4. Fill in the required parameters and execute

## Error Handling

The service returns SOAP faults for errors:

**Example Fault (Account Not Found):**
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Body>
      <SOAP-ENV:Fault>
         <faultcode>SOAP-ENV:Server</faultcode>
         <faultstring>Compte non trouvé</faultstring>
      </SOAP-ENV:Fault>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

## Security

Currently, the SOAP endpoints are configured to allow unauthenticated access for testing purposes. In a production environment, you should:

1. Implement WS-Security authentication
2. Use HTTPS/TLS for encrypted communication
3. Add API keys or tokens for authorization
4. Implement rate limiting

## Schema Definition

The complete XSD schema is available at:
```
src/main/resources/xsd/account.xsd
```
