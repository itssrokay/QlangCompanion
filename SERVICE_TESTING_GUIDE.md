# Service Retrieval Testing Guide

## Overview
This implementation adds airline service retrieval functionality to the existing Quarkus + Langchain4j chat application. The system can now handle both weather queries and airline service queries.

## Architecture

### Files Created/Modified:

1. **Models**:
   - `src/main/java/org/acme/models/ServiceResponse.java` - Data model for service information

2. **Services**:
   - `src/main/java/org/acme/services/ServiceService.java` - Business logic for service retrieval with mock data

3. **Tools**:
   - `src/main/java/org/acme/tools/ServiceTool.java` - Exposes service functionality to the AI agent

4. **Agent**:
   - `src/main/java/org/acme/WeatherAgent.java` - Updated to include both weather and service tools

5. **Tests**:
   - `src/test/java/org/acme/ServiceToolTest.java` - Unit tests for service functionality
   - `src/test/java/org/acme/ChatResourceServiceTest.java` - Integration tests for chat endpoints

## Mock Data

The system includes mock service data for the following PNRs:

### PNR: BS8ND5 (Flight AB1234)
- Extra Baggage - $50.00 USD
- Seat Selection - $35.00 USD  
- In-flight Meal - $25.00 USD
- Priority Boarding - $15.00 USD

### PNR: 6X67GH (Flight LH456)
- Wheelchair Assistance - Complimentary
- Pet Transport - $100.00 USD
- Lounge Access - $45.00 USD

### PNR: HUG67D (Flight BA789)
- Fast Track Security - $20.00 USD
- Travel Insurance - $75.00 USD
- Car Rental - $120.00 USD

## Testing the Implementation

### 1. Run Unit Tests
```bash
./mvnw test -Dtest=ServiceToolTest
```

### 2. Run Integration Tests
```bash
./mvnw test -Dtest=ChatResourceServiceTest
```

### 3. Manual Testing via REST API

Start the application:
```bash
./mvnw compile quarkus:dev
```

#### Test Service Queries:

1. **Get all services for a PNR**:
   ```
   GET http://localhost:8080/chat?q=What services are available for PNR BS8ND5?&sessionId=test123
   ```

2. **Ask about specific service**:
   ```
   GET http://localhost:8080/chat?q=Is wheelchair assistance available for PNR 6X67GH?&sessionId=test456
   ```

3. **Test with invalid PNR**:
   ```
   GET http://localhost:8080/chat?q=What services are available for PNR INVALID?&sessionId=test789
   ```

4. **Test missing PNR (AI should ask for it)**:
   ```
   GET http://localhost:8080/chat?q=What services are available for my booking?&sessionId=test999
   ```

#### Test Weather Still Works:
```
GET http://localhost:8080/chat?q=What's the weather in Berlin?&sessionId=weather123
```

## Debug Output

The implementation includes extensive debug logging:

### ServiceService Debug Output:
- Input PNR received
- Number of services found
- Processing details for each service
- Final formatted response

### ServiceTool Debug Output:
- Tool invocation confirmation
- Parameter extraction verification
- Response length information

## Expected Behavior

1. **Valid PNR with Services**: Returns formatted list of available services with prices and descriptions
2. **Valid PNR without Services**: Returns message indicating no services available
3. **Invalid PNR**: Returns appropriate error message
4. **Missing PNR**: AI asks user to provide the PNR
5. **Specific Service Query**: AI retrieves all services first, then answers about the specific service
6. **Weather Queries**: Continue to work as before

## Key Features

1. **Tool Calling**: Uses Langchain4j's tool calling mechanism
2. **Parameter Extraction**: LLM automatically extracts PNR from user queries
3. **Mock Data**: No external API calls - uses in-memory mock data
4. **Debug Logging**: Comprehensive logging to understand data flow
5. **Error Handling**: Graceful handling of invalid PNRs
6. **Multi-Tool Support**: Both weather and service tools work together

## Data Flow

1. User sends query via REST API
2. ChatResource receives query and calls WeatherAgent
3. WeatherAgent (LLM) determines if service tool is needed
4. If service query detected, LLM extracts PNR and calls ServiceTool
5. ServiceTool calls ServiceService with extracted PNR
6. ServiceService looks up mock data and returns formatted response
7. Response flows back through the chain to user

This implementation follows the same pattern as your Python example but adapted for Java/Quarkus with proper dependency injection and tool registration.