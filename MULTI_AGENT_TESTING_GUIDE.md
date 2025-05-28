# Multi-Agent System Testing Guide

## Overview

This guide provides comprehensive testing instructions for the multi-agent system built with Quarkus and LangChain4j. The system includes specialized agents for different domains, orchestrated by a central ManagerAgent.

## System Architecture

### Agents
1. **ManagerAgent** - Central orchestrator that delegates to specialized agents
2. **WeatherAgent** - Handles weather, air quality, forecasts, and UV index
3. **TravelAgent** - Manages destination suggestions and local attractions
4. **AirlineAgent** - Processes airline bookings, PNR, refunds, and rebooking

### Tools by Agent

#### WeatherAgent Tools
- `getWeather(location)` - Current weather conditions
- `getAQI(location)` - Air Quality Index information
- `getForecast(location, days)` - Weather forecast (1-7 days)
- `getUVIndex(location)` - UV index and sun protection advice

#### TravelAgent Tools
- `suggestDestinations(theme, budget, duration)` - Personalized destination recommendations
- `getLocalAttractions(location)` - Local attractions and points of interest

#### AirlineAgent Tools
- `retrievePnr(pnr, lastName)` - Get booking details
- `refund(pnr, lastName)` - Process refund requests
- `rebooking(pnr, lastName, newDate, newFlight)` - Change flight bookings
- `retrieveServices(pnr)` - Get available airline services

## Testing Methods

### 1. Unit Tests

Run the comprehensive test suite:

```bash
./mvnw test -Dtest=MultiAgentSystemTest
```

This tests:
- Individual tool functionality
- Agent integration
- Manager delegation
- Complex multi-agent queries

### 2. Manual API Testing

Start the application:
```bash
./mvnw quarkus:dev
```

#### Weather Queries
```bash
# Basic weather
curl "http://localhost:8080/chat?q=What's the weather in Berlin?"

# Comprehensive weather info
curl "http://localhost:8080/chat?q=Give me weather, air quality, and UV index for Mumbai"

# Weather forecast
curl "http://localhost:8080/chat?q=What's the 5-day forecast for Tokyo?"
```

#### Travel Queries
```bash
# Destination suggestions
curl "http://localhost:8080/chat?q=Suggest adventure destinations for a 10-day mid-range budget trip"

# Local attractions
curl "http://localhost:8080/chat?q=What are the top attractions in Paris?"

# Complex travel planning
curl "http://localhost:8080/chat?q=I want a luxury beach vacation for 7 days, what do you recommend?"
```

#### Airline Queries
```bash
# PNR retrieval
curl "http://localhost:8080/chat?q=Get booking details for PNR BS8ND5 and last name WICK"

# Refund request
curl "http://localhost:8080/chat?q=I want to refund my booking BS8ND5 for passenger WICK"

# Rebooking
curl "http://localhost:8080/chat?q=Rebook PNR BS8ND5 for WICK to flight AB5678 on 2024-04-15"
```

#### Multi-Agent Queries
```bash
# Weather + Travel
curl "http://localhost:8080/chat?q=I'm planning a trip to Bali. What's the weather like and what attractions should I visit?"

# Travel + Airline
curl "http://localhost:8080/chat?q=I have booking BS8ND5, what services are available and suggest similar destinations for future travel"

# All agents
curl "http://localhost:8080/chat?q=I'm traveling to London next week. Check my booking BS8ND5, tell me the weather forecast, and suggest attractions to visit"
```

### 3. Agent Specialization Testing

Test each agent directly to verify specialization:

#### WeatherAgent Direct Testing
```bash
# Should use multiple weather tools
curl "http://localhost:8080/chat?q=Give me complete weather information for Sydney including air quality and UV warnings"
```

#### TravelAgent Direct Testing
```bash
# Should provide comprehensive travel advice
curl "http://localhost:8080/chat?q=Plan a cultural trip to Japan for 2 weeks with luxury budget"
```

#### AirlineAgent Direct Testing
```bash
# Should handle complex airline operations
curl "http://localhost:8080/chat?q=Check PNR BS8ND5 for WICK, get available services, and process a refund if eligible"
```

## Expected Behaviors

### Manager Agent Delegation
The ManagerAgent should:
1. Analyze the query to determine appropriate agent(s)
2. Delegate to specialized agents
3. Combine responses when multiple agents are needed
4. Provide clear, unified responses

### Debug Output
Each interaction produces detailed debug logs showing:
- Query analysis and agent selection
- Tool calls with parameters
- API responses and data processing
- Final response formatting

### Tool Call Flow
1. User query → ManagerAgent
2. ManagerAgent analyzes and delegates → SpecializedAgent
3. SpecializedAgent calls appropriate tools
4. Tools process and return data
5. Response flows back through agents to user

## Sample Test Scenarios

### Scenario 1: Weather Planning
**Query**: "I'm going to Berlin tomorrow, what should I expect weather-wise?"
**Expected**: WeatherAgent delegation, multiple weather tools called, comprehensive weather advice

### Scenario 2: Travel Planning
**Query**: "Suggest beach destinations for a family vacation, 7 days, mid-range budget"
**Expected**: TravelAgent delegation, destination suggestions with family-friendly options

### Scenario 3: Airline Management
**Query**: "Check my booking BS8ND5 for WICK and see what services are available"
**Expected**: AirlineAgent delegation, PNR retrieval and services lookup

### Scenario 4: Complex Multi-Domain
**Query**: "I'm planning a trip to Tokyo. Check the weather, suggest attractions, and help me understand my flight options for PNR BS8ND5"
**Expected**: Multiple agent delegation, coordinated response combining weather, travel, and airline information

## Troubleshooting

### Common Issues
1. **Agent not found**: Ensure all agents are properly injected
2. **Tool not working**: Check tool implementation and debug logs
3. **Delegation failing**: Verify manager agent tool configurations
4. **Response incomplete**: Check if query requires multiple agents

### Debug Tips
1. Monitor console output for detailed tool call logs
2. Check session IDs for conversation continuity
3. Verify tool parameters are correctly extracted
4. Ensure mock data is appropriate for test scenarios

## Performance Considerations

- Each agent delegation adds latency
- Complex queries may require multiple tool calls
- Session management maintains conversation context
- Mock data ensures fast, predictable responses

## Extending the System

To add new agents:
1. Create agent interface with `@RegisterAiService`
2. Implement specialized tools for the domain
3. Create delegation tool for ManagerAgent
4. Update ManagerAgent system message
5. Add comprehensive tests

This multi-agent architecture provides a scalable foundation for complex AI-powered applications with clear separation of concerns and comprehensive testing capabilities. 