# Airline Tool Calling Testing Guide

## Overview
This implementation provides a comprehensive mock testing harness for LLM tool calling in a Quarkus airline customer support application. The system follows the exact pattern you requested: Intent Functions → API Handlers → Formatted Responses.

## Architecture

### File Structure Created:

```
src/main/java/org/acme/
├── intent/                     # Intent functions (parameter formatting)
│   ├── RetrievePnrIntent.java
│   ├── RefundIntent.java
│   ├── RebookingIntent.java
│   └── RetrieveServicesIntent.java
├── api/                        # API handlers (mock API calls)
│   ├── RetrievePnrApi.java
│   ├── RefundApi.java
│   ├── RebookingApi.java
│   └── RetrieveServicesApi.java
├── tools/                      # Tool implementations
│   ├── PnrTool.java
│   ├── RefundTool.java
│   ├── RebookingTool.java
│   ├── AirlineServiceTool.java
│   ├── WeatherTool.java        # Existing
│   └── ServiceTool.java        # Existing
├── AirlineAgent.java           # Comprehensive agent
└── ChatResource.java           # Updated REST endpoint
```

## Tool Call Flow

Each tool follows this exact pattern:

1. **User Query** → LLM extracts parameters
2. **Tool Called** → Receives extracted parameters
3. **Intent Function** → Formats parameters into JSON payload
4. **API Handler** → Processes payload, returns mock response
5. **Response Formatting** → Converts API response for LLM consumption
6. **Final Response** → Returned to user

## Available Tools

### 1. PnrTool - `retrievePnr(pnr, lastName)`
- **Purpose**: Retrieve complete booking details
- **Parameters**: PNR, Last Name
- **Mock Response**: Flight details, passenger info, booking status

### 2. RefundTool - `refund(pnr, lastName)`
- **Purpose**: Check refund eligibility and process refunds
- **Parameters**: PNR, Last Name  
- **Mock Response**: Refund amount, policy, processing time

### 3. RebookingTool - `rebooking(pnr, lastName, newDepartureDate, newFlightNumber)`
- **Purpose**: Change flight dates and flight numbers
- **Parameters**: PNR, Last Name, New Date, New Flight Number
- **Mock Response**: New booking details, fees, confirmation

### 4. AirlineServiceTool - `retrieveServices(pnr)`
- **Purpose**: Get available services for a booking
- **Parameters**: PNR only
- **Mock Response**: List of available services with prices

### 5. WeatherTool - `getWeather(city)` (Existing)
- **Purpose**: Get weather information
- **Parameters**: City name
- **Mock Response**: Weather conditions

### 6. ServiceTool - `getServices(pnr)` (Existing Legacy)
- **Purpose**: Alternative service lookup
- **Parameters**: PNR only
- **Mock Response**: Service information

## Debug Logging

Every component provides extensive debug logging:

### Intent Functions Log:
- Function name called
- Input parameters received
- Formatted JSON payload
- Timestamp information

### API Handlers Log:
- API handler name
- Received payload
- Extracted parameters
- Mock API response generated

### Tools Log:
- Tool invocation
- LLM extracted parameters
- Step-by-step flow (Intent → API → Format)
- Final response to LLM

### ChatResource Logs:
- Incoming query and session
- Agent calls
- Response details

## Testing the Implementation

### 1. Run Unit Tests
```bash
# Test individual tools
./mvnw test -Dtest=AirlineToolsTest

# Test service functionality (existing)
./mvnw test -Dtest=ServiceToolTest

# Test chat integration
./mvnw test -Dtest=ChatResourceServiceTest
```

### 2. Start the Application
```bash
./mvnw compile quarkus:dev
```

### 3. Test Tool Calling via REST API

#### PNR Retrieval:
```
GET http://localhost:8080/chat?q=Get booking details for PNR BS8ND5 and last name WICK&sessionId=test1
```

#### Refund Processing:
```
GET http://localhost:8080/chat?q=I want to cancel and get refund for PNR BS8ND5 last name WICK&sessionId=test2
```

#### Flight Rebooking:
```
GET http://localhost:8080/chat?q=Rebook PNR BS8ND5 last name WICK to flight CD5678 on 2024-04-15&sessionId=test3
```

#### Service Retrieval:
```
GET http://localhost:8080/chat?q=What services are available for PNR BS8ND5?&sessionId=test4
```

#### Weather (Still Works):
```
GET http://localhost:8080/chat?q=What's the weather in Berlin?&sessionId=test5
```

#### Missing Parameters (LLM will ask):
```
GET http://localhost:8080/chat?q=I want to check my booking&sessionId=test6
```

## Expected Debug Output

When you make a request, you'll see detailed logs like:

```
=== CHAT RESOURCE CALLED ===
Received Query: Get booking details for PNR BS8ND5 and last name WICK
Session ID: test1

=== CALLING AIRLINE AGENT ===
Session ID: test1
User Query: Get booking details for PNR BS8ND5 and last name WICK

=== PNR TOOL CALLED ===
Tool: retrievePnr
LLM Extracted Parameters:
  - pnr: BS8ND5
  - lastName: WICK

--- STEP 1: CALLING INTENT FUNCTION ---
=== RETRIEVE_PNR INTENT FUNCTION CALLED ===
Function: retrievePnr
Input Parameters:
  - pnr: BS8ND5
  - lastName: WICK
Formatted Payload:
{"pnr":"BS8ND5","lastName":"WICK","action":"retrieve_pnr","timestamp":1703123456789}

--- STEP 2: CALLING API HANDLER ---
=== RETRIEVE_PNR API HANDLER CALLED ===
API Handler: retrievePnrDapi
Received Payload:
{"pnr":"BS8ND5","lastName":"WICK","action":"retrieve_pnr","timestamp":1703123456789}
Extracted Parameters:
  - pnr: BS8ND5
  - lastName: WICK
Mock API Response:
{JSON response with flight details}

--- STEP 3: FORMATTING RESPONSE FOR LLM ---
Final Response to LLM:
PNR Details Retrieved Successfully:
{formatted response}
```

## Key Features

✅ **Complete Tool Call Flow**: Intent → API → Response  
✅ **No Real API Calls**: All responses are mocked  
✅ **Comprehensive Logging**: Every step is logged  
✅ **Parameter Extraction**: LLM handles all parameter extraction  
✅ **Error Handling**: Graceful handling of missing parameters  
✅ **Multiple Tools**: 6 different tools available  
✅ **Session Management**: Proper session handling  
✅ **JSON Payloads**: Proper JSON formatting throughout  

## Testing Scenarios

1. **Happy Path**: All parameters provided, successful responses
2. **Missing Parameters**: LLM asks for missing information
3. **Different PNRs**: Test with various PNR formats
4. **Multiple Tools**: Test different tool combinations
5. **Session Continuity**: Test conversation flow
6. **Error Cases**: Test with invalid inputs

## Mock Data

The system uses realistic mock data:
- **PNRs**: BS8ND5, 6X67GH, HUG67D
- **Flights**: AB1234, LH456, BA789
- **Routes**: JFK-LAX, various international routes
- **Services**: Baggage, seats, meals, boarding
- **Prices**: Realistic USD pricing

This implementation provides a complete testing harness for verifying LLM tool calling integration without any external API dependencies. 