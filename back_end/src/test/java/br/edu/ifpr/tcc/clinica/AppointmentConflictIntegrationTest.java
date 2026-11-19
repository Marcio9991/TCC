package br.edu.ifpr.tcc.clinica;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "APP_ADMIN_PASSWORD=integration-test-password")
class AppointmentConflictIntegrationTest {
    @Autowired
    MockMvc mvc;

    @Test
    void overlappingAppointmentsForProfessionalAreRejected() throws Exception {
        MvcResult login = mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"integration-test-password\"}"))
                .andExpect(status().isOk()).andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        MvcResult patientResult = mvc.perform(post("/api/patients").with(csrf()).session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Paciente Agenda\",\"phone\":\"41988887777\",\"cpf\":\"11144477735\",\"age\":27}"))
                .andExpect(status().isCreated()).andReturn();
        long patientId = ((Number) com.jayway.jsonpath.JsonPath.read(patientResult.getResponse().getContentAsString(),
                "$.id")).longValue();

        MvcResult serviceResult = mvc.perform(post("/api/services").with(csrf()).session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"name\":\"Limpeza facial teste\",\"type\":\"Facial\",\"durationMinutes\":60,\"active\":true}"))
                .andExpect(status().isCreated()).andReturn();
        long serviceId = ((Number) com.jayway.jsonpath.JsonPath.read(serviceResult.getResponse().getContentAsString(),
                "$.id")).longValue();

        MvcResult professionalResult = mvc.perform(post("/api/professionals").with(csrf()).session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"name\":\"Profissional Agenda\",\"phone\":\"41988886666\",\"cpf\":\"93541134780\",\"serviceIds\":["
                                + serviceId + "]}"))
                .andExpect(status().isCreated()).andReturn();
        long professionalId = ((Number) com.jayway.jsonpath.JsonPath
                .read(professionalResult.getResponse().getContentAsString(), "$.id")).longValue();

        String first = "{\"patientId\":" + patientId + ",\"professionalId\":" + professionalId + ",\"serviceId\":"
                + serviceId + ",\"scheduledAt\":\"2026-12-01T10:00:00\"}";
        String overlapping = "{\"patientId\":" + patientId + ",\"professionalId\":" + professionalId + ",\"serviceId\":"
                + serviceId + ",\"scheduledAt\":\"2026-12-01T10:30:00\"}";

        mvc.perform(post("/api/appointments").with(csrf()).session(session).contentType(MediaType.APPLICATION_JSON)
                .content(first))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/appointments").with(csrf()).session(session).contentType(MediaType.APPLICATION_JSON)
                .content(overlapping))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("conflita")));
    }
}
