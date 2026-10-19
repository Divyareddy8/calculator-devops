package com.spe.calculator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CalculatorControllerIT {

    @Autowired
    private MockMvc mvc;

    @Test
    void addEndpointReturnsJson() throws Exception {
        mvc.perform(get("/api/v1/add").param("a", "2").param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("add"))
                .andExpect(jsonPath("$.a").value(2))
                .andExpect(jsonPath("$.b").value(3))
                .andExpect(jsonPath("$.result").value(5));
    }

    @Test
    void subtractEndpoint() throws Exception {
        mvc.perform(get("/api/v1/subtract").param("a", "10").param("b", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(6));
    }

    @Test
    void multiplyEndpoint() throws Exception {
        mvc.perform(get("/api/v1/multiply").param("a", "6").param("b", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(42));
    }

    @Test
    void divideEndpoint() throws Exception {
        mvc.perform(get("/api/v1/divide").param("a", "9").param("b", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(4.5));
    }

    @Test
    void divideByZeroReturns400() throws Exception {
        mvc.perform(get("/api/v1/divide").param("a", "1").param("b", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Division by zero is not allowed"));
    }

    @Test
    void nonNumericParameterReturns400() throws Exception {
        mvc.perform(get("/api/v1/add").param("a", "abc").param("b", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void missingParameterReturns400() throws Exception {
        mvc.perform(get("/api/v1/multiply").param("a", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required parameter 'b'"));
    }

    @Test
    void actuatorHealthIsUp() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
