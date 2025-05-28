package org.acme.intent;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RetrievePnrIntent {

    @Inject
    ObjectMapper objectMapper;

    public String retrievePnr(String pnr, String lastName) {
        Map<String, String> pnrInfo = new HashMap<>();
        pnrInfo.put("pnr", pnr);
        pnrInfo.put("lastName", lastName);
        
        try {
            return objectMapper.writeValueAsString(pnrInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize PNR info", e);
        }
    }
} 