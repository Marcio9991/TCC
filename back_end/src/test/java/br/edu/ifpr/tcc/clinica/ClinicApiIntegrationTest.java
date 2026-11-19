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
class ClinicApiIntegrationTest {
    @Autowired MockMvc mvc;

    @Test
    void apiRequiresAuthentication() throws Exception {
        mvc.perform(get("/api/patients")).andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanCreateAndSearchPatient() throws Exception {
        MvcResult login = mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"integration-test-password\"}"))
            .andExpect(status().isOk()).andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mvc.perform(post("/api/patients").with(csrf()).session(session).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Ana Teste\",\"phone\":\"41999990000\",\"cpf\":\"52998224725\",\"age\":31,\"allergies\":\"\"}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Ana Teste"));

        mvc.perform(get("/api/patients").param("q", "Ana").session(session))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].cpf").value("52998224725"));
    }

    @Test
    void invalidPatientDataReturnsFieldFeedback() throws Exception {
        MvcResult login = mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"integration-test-password\"}"))
            .andExpect(status().isOk()).andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mvc.perform(post("/api/patients").with(csrf()).session(session).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"phone\":\"x\",\"cpf\":\"123\",\"age\":150}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fields.name").exists())
            .andExpect(jsonPath("$.fields.cpf").exists());
    }
}
