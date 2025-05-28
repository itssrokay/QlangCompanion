# Airline Companion Multi-Agent System

A Quarkus-based multi-agent system for airline customer support using Azure OpenAI and LangChain4j.

## 🏗️ **Architecture Overview**

```
Customer Query → Airline Agent → Specialized Tools → Intent Layer → DAPI Layer → External APIs
```

### **Multi-Agent Design**
- **AirlineAgent**: Specialized agent with airline-specific tools (PNR, refund, rebooking, services)
- **Tool-Intent-DAPI Pattern**: Three-layer processing for API calls
- **Session Management**: Maintains conversation context across multiple turns
- **Azure OpenAI Integration**: Powered by GPT-4 for intelligent query processing

## 🚀 **Quick Start**

### **Prerequisites**
- Java 17+
- Maven 3.8+
- Azure OpenAI account with GPT-4 deployment

### **Environment Variables**
Create a `.env` file or set these environment variables:

```bash
# Azure OpenAI Configuration
AZURE_OPENAI_API_KEY=your_azure_openai_api_key
AZURE_OPENAI_ENDPOINT=https://your-resource.openai.azure.com/
AZURE_OPENAI_DEPLOYMENT_NAME=gpt-4
AZURE_OPENAI_DEPLOYMENT_NAME_MINI=gpt-4o-mini

# DAPI Configuration (External APIs)
DAPI_ENDPOINT=https://your-airline-api.com/api
DAPI_1AAUTH_TOKEN_AC=your_1aauth_token
DAPI_PURCHASE_ORDER=/orders/
DAPI_SERVICE_ORDER=/service-orders
DAPI_REFUND_ELIGIBILITIES=/refund-eligibilities
DAPI_AIR_BOUND_EXCHANGE=/air-bound-exchanges
DAPI_SEARCH_AIR_BOUND=/search-air-bounds
```

### **Running the Application**

```bash
# Development mode
./mvnw quarkus:dev

# Production build
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

## 📡 **API Endpoints**

### **Main Chat Interface**
```
GET /companion/chat?q={query}&sessionId={sessionId}
```

**Example:**
```bash
curl "http://localhost:8080/companion/chat?q=Check my booking BS8ND5 for WICK&sessionId=test123"
```

**Response:**
```json
{
    "chatId": "test123",
    "response": "<p>I'll help you check your booking...</p>",
    "responseType": "chat"
}
```

### **Health Check**
```
GET /companion/health
```

### **Test PNR Retrieval**
```
GET /companion/test-pnr?pnr={pnr}&lastName={lastName}
```

## 🛠️ **Available Tools & Capabilities**

### **1. PNR Retrieval**
- **Query**: "Check my booking BS8ND5 for WICK"
- **Tool**: `PnrTool`
- **Function**: Retrieves complete booking information
- **Required**: PNR code, passenger last name

### **2. Refund Processing**
- **Query**: "I want to request a refund for booking BS8ND5"
- **Tool**: `RefundTool`
- **Function**: Checks refund eligibility and processes requests
- **Required**: PNR code, passenger last name

### **3. Flight Rebooking**
- **Query**: "Change my flight BS8ND5 to March 30, 2024"
- **Tool**: `RebookingTool`
- **Function**: Searches alternatives and processes rebooking
- **Required**: PNR, new date, origin, destination

### **4. Service Management**
- **Query**: "What services are available for my booking BS8ND5?"
- **Tool**: `ServiceTool`, `AirlineServiceTool`
- **Function**: Retrieves available services for booking
- **Required**: PNR code

### **5. Weather Information**
- **Query**: "What's the weather like in New York?"
- **Tool**: `WeatherTool`
- **Function**: Provides weather information for destinations
- **Required**: City/location name

## 🔧 **System Architecture**

### **Tool-Intent-DAPI Pattern**

```
Tool Layer (AI Interface)
    ↓ Parameters extracted by LLM
Intent Layer (JSON Formatting)
    ↓ Structured data preparation
DAPI Layer (API Client)
    ↓ External API calls
Response flows back through layers
```

**Example Flow for PNR Retrieval:**
1. User: "Check booking BS8ND5 for WICK"
2. AirlineAgent identifies this as PNR retrieval
3. `PnrTool` extracts parameters: pnr="BS8ND5", lastName="WICK"
4. `RetrievePnrIntent` formats: `{"pnr": "BS8ND5", "lastName": "WICK"}`
5. `RetrievePnrApi` makes external API call
6. Response flows back with booking details

### **Session Management**
- Each conversation has a unique `sessionId`
- Context is maintained across multiple queries
- Supports follow-up questions and multi-turn conversations

## 🎯 **Example Conversations**

### **PNR Retrieval**
```
User: Check my booking BS8ND5 for John WICK
Agent: I'll retrieve your booking information...

[Calls PnrTool → RetrievePnrIntent → RetrievePnrApi]

Agent: Here are your booking details:
- PNR: BS8ND5
- Passenger: John WICK
- Flight: AB1234 (JFK → LAX)
- Date: March 25, 2024
- Status: Confirmed
```

### **Refund Request**
```
User: I need a refund for my cancelled flight BS8ND5
Agent: I'll check the refund eligibility for your booking...

[Calls RefundTool → RefundIntent → RefundApi]

Agent: Your booking is eligible for a full refund.
Processing time: 5-7 business days.
Refund amount: $350.00
```

### **Service Inquiry**
```
User: What additional services can I add to booking BS8ND5?
Agent: Let me check available services for your booking...

[Calls ServiceTool → RetrieveServicesIntent → RetrieveServicesApi]

Agent: Available services:
- Seat upgrade ($50)
- Extra baggage ($30)
- Priority boarding ($25)
- In-flight meals ($20)
```

## 🧪 **Testing**

### **Manual Testing**
1. Start the application: `./mvnw quarkus:dev`
2. Visit: `http://localhost:8080/companion/health`
3. Test PNR retrieval: `http://localhost:8080/companion/test-pnr?pnr=BS8ND5&lastName=WICK`
4. Test chat: `http://localhost:8080/companion/chat?q=Check my booking BS8ND5 for WICK`

### **Expected Behavior**
- System should respond with properly formatted HTML
- Azure OpenAI integration should process queries intelligently
- Tool calls should be logged in console
- Mock API responses should be returned (until real DAPI integration)

## 🔧 **Configuration**

### **Azure OpenAI Settings**
- Model: GPT-4 (configurable)
- Temperature: 0.7 (default)
- Max tokens: 2000 (default)
- Tools: All airline-specific tools enabled

### **DAPI Integration**
Currently using mock responses. To enable real DAPI calls:
1. Set proper `DAPI_ENDPOINT` and authentication
2. Update API classes in `org.acme.api` package
3. Configure SSL certificates if required

## 📝 **Development Notes**

### **Current Status**
- ✅ Azure OpenAI integration working
- ✅ Multi-agent architecture implemented
- ✅ Tool-Intent-DAPI pattern established
- ✅ PNR retrieval flow complete
- ✅ Session management working
- ⚠️ Using mock API responses (configurable)

### **Next Steps**
1. Test real DAPI integration
2. Add more specialized agents (FlightSearchAgent, CustomerServiceAgent)
3. Implement proper error handling
4. Add input validation
5. Create comprehensive test suite

## 🚀 **Deployment**

### **Docker**
```bash
# Build image
./mvnw clean package -Dquarkus.container-image.build=true

# Run container
docker run -i --rm -p 8080:8080 your-app:latest
```

### **Environment Variables for Production**
Ensure all Azure OpenAI and DAPI environment variables are properly set in your deployment environment.

## 📞 **Support**

For issues or questions:
1. Check the logs for detailed error information
2. Verify Azure OpenAI configuration
3. Test with simple queries first
4. Review tool call flows in console output
