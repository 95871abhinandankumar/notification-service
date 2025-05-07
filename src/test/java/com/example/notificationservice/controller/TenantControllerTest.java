package com.example.notificationservice.controller;

import com.example.notificationservice.dto.TenantOnboardingRequest;
import com.example.notificationservice.dto.TenantOnboardingResponse;
import com.example.notificationservice.exception.TenantAlreadyExistsException;
import com.example.notificationservice.exception.TenantNotFoundException;
import com.example.notificationservice.service.TenantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantController.class)
class TenantControllerTest {
    private static final Logger log = LoggerFactory.getLogger(TenantControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TenantService tenantService;

    @Test
    void onboardTenant_Success() throws Exception {
        log.info("Testing successful tenant onboarding");
        
        // Prepare test data
        TenantOnboardingRequest request = new TenantOnboardingRequest();
        request.setName("Test Tenant");
        request.setTenantIdentifier("test_tenant");
        log.debug("Request data: {}", request);

        TenantOnboardingResponse response = new TenantOnboardingResponse();
        response.setTenantIdentifier("test_tenant");
        response.setName("Test Tenant");
        response.setSchemaName("tenant_test_tenant");
        response.setStatus("ACTIVE");
        response.setCreatedAt(LocalDateTime.now());
        log.debug("Expected response: {}", response);

        // Mock service behavior
        when(tenantService.onboardNewTenant(any(TenantOnboardingRequest.class)))
                .thenReturn(response);

        // Perform test
        String responseJson = mockMvc.perform(post("/api/v1/tenants/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantIdentifier").value("test_tenant"))
                .andExpect(jsonPath("$.name").value("Test Tenant"))
                .andExpect(jsonPath("$.schemaName").value("tenant_test_tenant"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        log.debug("Actual response: {}", responseJson);

        // Verify service call
        verify(tenantService, times(1)).onboardNewTenant(any(TenantOnboardingRequest.class));
        log.info("Test completed successfully");
    }

    @Test
    void onboardTenant_WhenTenantExists_ReturnsConflict() throws Exception {
        log.info("Testing tenant onboarding with existing tenant");
        
        // Prepare test data
        TenantOnboardingRequest request = new TenantOnboardingRequest();
        request.setName("Test Tenant");
        request.setTenantIdentifier("test_tenant");
        log.debug("Request data: {}", request);

        // Mock service behavior
        when(tenantService.onboardNewTenant(any(TenantOnboardingRequest.class)))
                .thenThrow(new TenantAlreadyExistsException("Tenant already exists"));

        // Perform test
        String responseJson = mockMvc.perform(post("/api/v1/tenants/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        log.debug("Error response: {}", responseJson);

        // Verify service call
        verify(tenantService, times(1)).onboardNewTenant(any(TenantOnboardingRequest.class));
        log.info("Test completed successfully");
    }

    @Test
    void getTenant_Success() throws Exception {
        log.info("Testing successful tenant retrieval");
        
        // Prepare test data
        String tenantIdentifier = "test_tenant";
        TenantOnboardingResponse response = new TenantOnboardingResponse();
        response.setTenantIdentifier(tenantIdentifier);
        response.setName("Test Tenant");
        response.setSchemaName("tenant_test_tenant");
        response.setStatus("ACTIVE");
        response.setCreatedAt(LocalDateTime.now());
        log.debug("Expected response: {}", response);

        // Mock service behavior
        when(tenantService.getTenant(eq(tenantIdentifier)))
                .thenReturn(response);

        // Perform test
        String responseJson = mockMvc.perform(get("/api/v1/tenants/{tenantIdentifier}", tenantIdentifier))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantIdentifier").value(tenantIdentifier))
                .andExpect(jsonPath("$.name").value("Test Tenant"))
                .andExpect(jsonPath("$.schemaName").value("tenant_test_tenant"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        log.debug("Actual response: {}", responseJson);

        // Verify service call
        verify(tenantService, times(1)).getTenant(tenantIdentifier);
        log.info("Test completed successfully");
    }

    @Test
    void getTenant_WhenNotFound_ReturnsNotFound() throws Exception {
        log.info("Testing tenant retrieval with non-existent tenant");
        
        // Prepare test data
        String tenantIdentifier = "non_existent_tenant";

        // Mock service behavior
        when(tenantService.getTenant(eq(tenantIdentifier)))
                .thenThrow(new TenantNotFoundException("Tenant not found"));

        // Perform test
        String responseJson = mockMvc.perform(get("/api/v1/tenants/{tenantIdentifier}", tenantIdentifier))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        log.debug("Error response: {}", responseJson);

        // Verify service call
        verify(tenantService, times(1)).getTenant(tenantIdentifier);
        log.info("Test completed successfully");
    }

    @Test
    void activateTenant_Success() throws Exception {
        log.info("Testing successful tenant activation");
        
        // Prepare test data
        String tenantIdentifier = "test_tenant";

        // Perform test
        String responseJson = mockMvc.perform(post("/api/v1/tenants/{tenantIdentifier}/activate", tenantIdentifier))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        log.debug("Response: {}", responseJson);

        // Verify service call
        verify(tenantService, times(1)).activateTenant(tenantIdentifier);
        log.info("Test completed successfully");
    }

    @Test
    void activateTenant_WhenNotFound_ReturnsNotFound() throws Exception {
        log.info("Testing tenant activation with non-existent tenant");
        
        // Prepare test data
        String tenantIdentifier = "non_existent_tenant";

        // Mock service behavior
        doThrow(new TenantNotFoundException("Tenant not found"))
                .when(tenantService).activateTenant(tenantIdentifier);

        // Perform test
        String responseJson = mockMvc.perform(post("/api/v1/tenants/{tenantIdentifier}/activate", tenantIdentifier))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        log.debug("Error response: {}", responseJson);

        // Verify service call
        verify(tenantService, times(1)).activateTenant(tenantIdentifier);
        log.info("Test completed successfully");
    }

    @Test
    void deactivateTenant_Success() throws Exception {
        log.info("Testing successful tenant deactivation");
        
        // Prepare test data
        String tenantIdentifier = "test_tenant";

        // Perform test
        String responseJson = mockMvc.perform(post("/api/v1/tenants/{tenantIdentifier}/deactivate", tenantIdentifier))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        log.debug("Response: {}", responseJson);

        // Verify service call
        verify(tenantService, times(1)).deactivateTenant(tenantIdentifier);
        log.info("Test completed successfully");
    }

    @Test
    void deactivateTenant_WhenNotFound_ReturnsNotFound() throws Exception {
        log.info("Testing tenant deactivation with non-existent tenant");
        
        // Prepare test data
        String tenantIdentifier = "non_existent_tenant";

        // Mock service behavior
        doThrow(new TenantNotFoundException("Tenant not found"))
                .when(tenantService).deactivateTenant(tenantIdentifier);

        // Perform test
        String responseJson = mockMvc.perform(post("/api/v1/tenants/{tenantIdentifier}/deactivate", tenantIdentifier))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        log.debug("Error response: {}", responseJson);

        // Verify service call
        verify(tenantService, times(1)).deactivateTenant(tenantIdentifier);
        log.info("Test completed successfully");
    }
} 