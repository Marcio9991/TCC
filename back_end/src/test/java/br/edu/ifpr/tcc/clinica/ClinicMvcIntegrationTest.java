package br.edu.ifpr.tcc.clinica;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import br.edu.ifpr.tcc.clinica.service.AppointmentService;
import br.edu.ifpr.tcc.clinica.service.PatientService;
import br.edu.ifpr.tcc.clinica.service.ProfessionalService;
import br.edu.ifpr.tcc.clinica.service.ServiceCatalogService;
import br.edu.ifpr.tcc.clinica.web.form.Requests.AppointmentRequest;
import br.edu.ifpr.tcc.clinica.web.form.Requests.PatientRequest;
import br.edu.ifpr.tcc.clinica.web.form.Requests.ProfessionalRequest;
import br.edu.ifpr.tcc.clinica.web.form.Requests.ServiceRequest;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "APP_ADMIN_PASSWORD=integration-test-password")
class ClinicMvcIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired PatientService patients;
    @Autowired ProfessionalService professionals;
    @Autowired ServiceCatalogService services;
    @Autowired AppointmentService appointments;

    @Test
    void protectedPagesRedirectToFormLogin() throws Exception {
        mvc.perform(get("/patients")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
        mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
        mvc.perform(get("/").with(user("admin"))).andExpect(status().isOk()).andExpect(view().name("dashboard"));
        mvc.perform(get("/patients").with(user("admin"))).andExpect(status().isOk()).andExpect(view().name("patients"));
        mvc.perform(get("/professionals").with(user("admin"))).andExpect(status().isOk()).andExpect(view().name("professionals"));
        mvc.perform(get("/services").with(user("admin"))).andExpect(status().isOk()).andExpect(view().name("services"));
        mvc.perform(get("/appointments").with(user("admin"))).andExpect(status().isOk()).andExpect(view().name("appointments"));
        var service = services.create(new ServiceRequest("Serviço renderização", "Facial", 30, true));
        professionals.create(new ProfessionalRequest("Profissional renderização", "41988886666",
                "22233344405", "", Set.of(service.getId())));
        mvc.perform(get("/professionals").with(user("admin"))).andExpect(status().isOk())
                .andExpect(content().string(containsString("Serviço renderização")));
    }

    @Test
    void userCanSignInAndCreateThenSearchPatient() throws Exception {
        MvcResult result = mvc.perform(post("/login").with(csrf())
                        .param("username", "admin").param("password", "integration-test-password"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/" )).andReturn();
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

        mvc.perform(post("/patients").session(session).with(csrf())
                        .param("name", "Ana Teste").param("phone", "41999990000")
                        .param("cpf", "52998224725").param("age", "31").param("allergies", ""))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/patients"));
        mvc.perform(get("/patients").session(session).param("q", "Ana"))
                .andExpect(status().isOk()).andExpect(view().name("patients"))
                .andExpect(content().string(containsString("Ana Teste")))
                .andExpect(content().string(containsString("52998224725")));
    }

    @Test
    void invalidPatientFormIsRenderedWithValidationFeedback() throws Exception {
        mvc.perform(post("/patients").with(user("admin")).with(csrf())
                        .param("name", "").param("phone", "x").param("cpf", "123").param("age", "150"))
                .andExpect(status().isOk()).andExpect(view().name("patients"))
                .andExpect(content().string(containsString("CPF inválido")))
                .andExpect(content().string(containsString("Informe um telefone válido")));
    }

    @Test
    void conflictingAppointmentIsShownAsFormError() throws Exception {
        var patient = patients.create(new PatientRequest("Paciente Agenda", "41988887777", "11144477735", 27, "", ""));
        var service = services.create(new ServiceRequest("Limpeza facial teste", "Facial", 60, true));
        var professional = professionals.create(new ProfessionalRequest("Profissional Agenda", "41988886666",
                "93541134780", "", Set.of(service.getId())));
        appointments.create(new AppointmentRequest(patient.getId(), professional.getId(), service.getId(),
                LocalDateTime.of(2026, 12, 1, 10, 0), ""));

        mvc.perform(post("/appointments").with(user("admin")).with(csrf())
                        .param("patientId", patient.getId().toString())
                        .param("professionalId", professional.getId().toString())
                        .param("serviceId", service.getId().toString())
                        .param("scheduledAt", "2026-12-01T10:30")
                        .param("notes", ""))
                .andExpect(status().isOk()).andExpect(view().name("appointments"))
                .andExpect(content().string(containsString("conflita com outro agendamento")));
    }
}
