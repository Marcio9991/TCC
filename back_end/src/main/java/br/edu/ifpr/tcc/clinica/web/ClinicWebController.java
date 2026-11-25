package br.edu.ifpr.tcc.clinica.web;

import br.edu.ifpr.tcc.clinica.web.form.Requests;
import br.edu.ifpr.tcc.clinica.domain.Address;
import br.edu.ifpr.tcc.clinica.domain.Appointment;
import br.edu.ifpr.tcc.clinica.domain.AppointmentStatus;
import br.edu.ifpr.tcc.clinica.domain.Patient;
import br.edu.ifpr.tcc.clinica.domain.Professional;
import br.edu.ifpr.tcc.clinica.domain.ServiceCatalog;
import br.edu.ifpr.tcc.clinica.service.AppointmentService;
import br.edu.ifpr.tcc.clinica.service.PatientService;
import br.edu.ifpr.tcc.clinica.service.ProfessionalService;
import br.edu.ifpr.tcc.clinica.service.ServiceCatalogService;
import br.edu.ifpr.tcc.clinica.web.form.ClinicForms;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClinicWebController {
    private final PatientService patients;
    private final ProfessionalService professionals;
    private final ServiceCatalogService services;
    private final AppointmentService appointments;

    public ClinicWebController(PatientService patients, ProfessionalService professionals,
            ServiceCatalogService services, AppointmentService appointments) {
        this.patients = patients;
        this.professionals = professionals;
        this.services = services;
        this.appointments = appointments;
    }

    @ModelAttribute
    public void activePage(Model model) {
        // View-specific handlers may override this attribute when needed.
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/")
    public String dashboard(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("patientCount", patients.list(null).size());
        model.addAttribute("professionalCount", professionals.list().size());
        model.addAttribute("serviceCount", services.list(true).size());
        model.addAttribute("todayAppointments", appointments.list(null, today));
        model.addAttribute("today", today);
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }

    @GetMapping("/patients")
    public String patientList(@RequestParam(required = false) String q, Model model) {
        return showPatients(model, q, new ClinicForms.Patient(), null);
    }
    @GetMapping("/patients/{id}/edit")
    public String editPatient(@PathVariable Long id, @RequestParam(required = false) String q, Model model) {
        Patient item = patients.get(id);
        ClinicForms.Patient form = new ClinicForms.Patient();
        form.setName(item.getName()); form.setPhone(item.getPhone()); form.setCpf(item.getCpf());
        form.setAge(item.getAge()); form.setAllergies(item.getAllergies());
        form.setAddress(item.getAddress() == null ? "" : item.getAddress().getLocation());
        return showPatients(model, q, form, id);
    }
    @PostMapping("/patients")
    public String createPatient(@Valid @ModelAttribute("patientForm") ClinicForms.Patient form, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) try {
            patients.create(new Requests.PatientRequest(form.getName(), form.getPhone(), form.getCpf(), form.getAge(), form.getAllergies(), form.getAddress()));
            redirect.addFlashAttribute("success", "Paciente cadastrado."); return "redirect:/patients";
        } catch (ResponseStatusException ex) { result.reject("operation", safeReason(ex)); }
        return showPatients(model, null, form, null);
    }
    @PostMapping("/patients/{id}")
    public String updatePatient(@PathVariable Long id, @Valid @ModelAttribute("patientForm") ClinicForms.Patient form,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) try {
            patients.update(id, new Requests.PatientRequest(form.getName(), form.getPhone(), form.getCpf(), form.getAge(), form.getAllergies(), form.getAddress()));
            redirect.addFlashAttribute("success", "Paciente atualizado."); return "redirect:/patients";
        } catch (ResponseStatusException ex) { result.reject("operation", safeReason(ex)); }
        return showPatients(model, null, form, id);
    }

    @GetMapping("/professionals")
    public String professionalList(Model model) { return showProfessionals(model, new ClinicForms.Professional(), null); }
    @GetMapping("/professionals/{id}/edit")
    public String editProfessional(@PathVariable Long id, Model model) {
        Professional item = professionals.get(id); ClinicForms.Professional form = new ClinicForms.Professional();
        form.setName(item.getName()); form.setPhone(item.getPhone()); form.setCpf(item.getCpf());
        form.setAddress(item.getAddress() == null ? "" : item.getAddress().getLocation());
        form.setServiceIds(item.getServices().stream().map(ServiceCatalog::getId).collect(Collectors.toSet()));
        return showProfessionals(model, form, id);
    }
    @PostMapping("/professionals")
    public String createProfessional(@Valid @ModelAttribute("professionalForm") ClinicForms.Professional form,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) try {
            professionals.create(new Requests.ProfessionalRequest(form.getName(), form.getPhone(), form.getCpf(), form.getAddress(), safeSet(form.getServiceIds())));
            redirect.addFlashAttribute("success", "Profissional cadastrado."); return "redirect:/professionals";
        } catch (ResponseStatusException ex) { result.reject("operation", safeReason(ex)); }
        return showProfessionals(model, form, null);
    }
    @PostMapping("/professionals/{id}")
    public String updateProfessional(@PathVariable Long id, @Valid @ModelAttribute("professionalForm") ClinicForms.Professional form,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) try {
            professionals.update(id, new Requests.ProfessionalRequest(form.getName(), form.getPhone(), form.getCpf(), form.getAddress(), safeSet(form.getServiceIds())));
            redirect.addFlashAttribute("success", "Profissional atualizado."); return "redirect:/professionals";
        } catch (ResponseStatusException ex) { result.reject("operation", safeReason(ex)); }
        return showProfessionals(model, form, id);
    }

    @GetMapping("/services")
    public String serviceList(Model model) { return showServices(model, new ClinicForms.Service(), null); }
    @GetMapping("/services/{id}/edit")
    public String editService(@PathVariable Long id, Model model) {
        ServiceCatalog item = services.get(id); ClinicForms.Service form = new ClinicForms.Service();
        form.setName(item.getName()); form.setType(item.getType()); form.setDurationMinutes(item.getDurationMinutes()); form.setActive(item.isActive());
        return showServices(model, form, id);
    }
    @PostMapping("/services")
    public String createService(@Valid @ModelAttribute("serviceForm") ClinicForms.Service form, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) try {
            services.create(new Requests.ServiceRequest(form.getName(), form.getType(), form.getDurationMinutes(), form.isActive()));
            redirect.addFlashAttribute("success", "Serviço cadastrado."); return "redirect:/services";
        } catch (ResponseStatusException ex) { result.reject("operation", safeReason(ex)); }
        return showServices(model, form, null);
    }
    @PostMapping("/services/{id}")
    public String updateService(@PathVariable Long id, @Valid @ModelAttribute("serviceForm") ClinicForms.Service form,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) try {
            services.update(id, new Requests.ServiceRequest(form.getName(), form.getType(), form.getDurationMinutes(), form.isActive()));
            redirect.addFlashAttribute("success", "Serviço atualizado."); return "redirect:/services";
        } catch (ResponseStatusException ex) { result.reject("operation", safeReason(ex)); }
        return showServices(model, form, id);
    }

    @GetMapping("/appointments")
    public String appointmentList(@RequestParam(required = false) Long patientId, @RequestParam(required = false) String date, Model model) {
        return showAppointments(model, new ClinicForms.Appointment(), null, patientId, parseDate(date));
    }
    @GetMapping("/appointments/{id}/edit")
    public String editAppointment(@PathVariable Long id, Model model) {
        Appointment item = appointments.get(id); ClinicForms.Appointment form = new ClinicForms.Appointment();
        form.setPatientId(item.getPatient().getId()); form.setProfessionalId(item.getProfessional().getId());
        form.setServiceId(item.getService().getId()); form.setScheduledAt(item.getScheduledAt()); form.setNotes(item.getNotes());
        return showAppointments(model, form, id, null, null);
    }
    @PostMapping("/appointments")
    public String createAppointment(@Valid @ModelAttribute("appointmentForm") ClinicForms.Appointment form, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) try {
            appointments.create(toRequest(form)); redirect.addFlashAttribute("success", "Agendamento criado."); return "redirect:/appointments";
        } catch (ResponseStatusException ex) { result.reject("operation", safeReason(ex)); }
        return showAppointments(model, form, null, null, null);
    }
    @PostMapping("/appointments/{id}")
    public String updateAppointment(@PathVariable Long id, @Valid @ModelAttribute("appointmentForm") ClinicForms.Appointment form,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) try {
            appointments.update(id, toRequest(form)); redirect.addFlashAttribute("success", "Agendamento atualizado."); return "redirect:/appointments";
        } catch (ResponseStatusException ex) { result.reject("operation", safeReason(ex)); }
        return showAppointments(model, form, id, null, null);
    }
    @PostMapping("/appointments/{id}/status")
    public String appointmentStatus(@PathVariable Long id, @RequestParam AppointmentStatus status, RedirectAttributes redirect) {
        try { appointments.updateStatus(id, status); redirect.addFlashAttribute("success", status == AppointmentStatus.CANCELLED ? "Agendamento cancelado." : "Agendamento atualizado."); }
        catch (ResponseStatusException ex) { redirect.addFlashAttribute("error", safeReason(ex)); }
        return "redirect:/appointments";
    }

    private String showPatients(Model model, String q, ClinicForms.Patient form, Long editId) {
        model.addAttribute("patients", patients.list(q)); model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("patientForm", form); model.addAttribute("editId", editId); model.addAttribute("activePage", "patients"); return "patients";
    }
    private String showProfessionals(Model model, ClinicForms.Professional form, Long editId) {
        model.addAttribute("professionals", professionals.list()); model.addAttribute("activeServices", services.list(true));
        model.addAttribute("professionalForm", form); model.addAttribute("editId", editId); model.addAttribute("activePage", "professionals"); return "professionals";
    }
    private String showServices(Model model, ClinicForms.Service form, Long editId) {
        model.addAttribute("services", services.list(false)); model.addAttribute("serviceForm", form);
        model.addAttribute("editId", editId); model.addAttribute("activePage", "services"); return "services";
    }
    private String showAppointments(Model model, ClinicForms.Appointment form, Long editId, Long patientId, LocalDate date) {
        model.addAttribute("appointments", appointments.list(patientId, date)); model.addAttribute("patients", patients.list(null));
        model.addAttribute("professionals", professionals.list()); model.addAttribute("activeServices", services.list(true));
        model.addAttribute("appointmentForm", form); model.addAttribute("editId", editId); model.addAttribute("filterPatientId", patientId);
        model.addAttribute("filterDate", date == null ? "" : date.toString()); model.addAttribute("activePage", "appointments"); return "appointments";
    }
    private Requests.AppointmentRequest toRequest(ClinicForms.Appointment form) {
        return new Requests.AppointmentRequest(form.getPatientId(), form.getProfessionalId(), form.getServiceId(), form.getScheduledAt(), form.getNotes());
    }
    private Set<Long> safeSet(Set<Long> ids) { return ids == null ? Set.of() : Set.copyOf(ids); }
    private LocalDate parseDate(String date) { return date == null || date.isBlank() ? null : LocalDate.parse(date); }
    private String safeReason(ResponseStatusException ex) { return ex.getReason() == null ? "Não foi possível concluir a operação." : ex.getReason(); }
}
