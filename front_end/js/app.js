const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];
const api = async (path, options = {}) => {
  const method = (options.method || 'GET').toUpperCase();
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  if (!['GET', 'HEAD', 'OPTIONS'].includes(method)) {
    const csrfResponse = await fetch('/api/auth/csrf', { credentials: 'same-origin' });
    const csrf = await csrfResponse.json();
    headers['X-XSRF-TOKEN'] = csrf.token;
  }
  const response = await fetch(path, { credentials: 'same-origin', ...options, headers });
  const text = await response.text();
  let body = null; try { body = text ? JSON.parse(text) : null; } catch { body = text; }
  if (!response.ok) {
    if (response.status === 401) showLogin();
    const message = body?.error || body?.message || (typeof body === 'string' ? body : `Erro ${response.status}`);
    throw new Error(message);
  }
  return body;
};
const escape = value => String(value ?? '').replace(/[&<>"']/g, ch => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[ch]));
const dateTime = value => value ? new Date(value).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' }) : '—';
const today = () => new Date().toLocaleDateString('en-CA');
let patients = [], professionals = [], services = [], appointments = [];
let toastTimer;
function showLogin(message = '') { $('#app-view').hidden = true; $('#login-view').hidden = false; $('#login-error').textContent = message; }
function showApp() { $('#login-view').hidden = true; $('#app-view').hidden = false; $('#today-label').textContent = new Date().toLocaleDateString('pt-BR', { dateStyle: 'full' }); loadAll().catch(showError); }
function notify(message, error = false) { const el = $('#toast'); el.textContent = message; el.classList.toggle('error', error); el.hidden = false; clearTimeout(toastTimer); toastTimer = setTimeout(() => el.hidden = true, 4200); }
function showError(error) { notify(error.message || 'Não foi possível concluir a operação.', true); }
async function checkSession() { try { await api('/api/patients'); showApp(); } catch (e) { showLogin(); } }
$('#login-form').addEventListener('submit', async e => { e.preventDefault(); const data = Object.fromEntries(new FormData(e.currentTarget)); try { await api('/api/auth/login', { method: 'POST', body: JSON.stringify(data) }); e.currentTarget.reset(); showApp(); } catch (err) { $('#login-error').textContent = err.message; } });
$('#logout-button').addEventListener('click', async () => { try { await api('/api/auth/logout', { method: 'POST' }); } finally { showLogin(); } });
async function loadAll() { [patients, professionals, services, appointments] = await Promise.all([api('/api/patients'), api('/api/professionals'), api('/api/services'), api('/api/appointments')]); renderPatients(); renderProfessionals(); renderServices(); renderAppointments(); renderDashboard(); fillSelects(); }
function renderDashboard() { const date = today(); const todayAppointments = appointments.filter(a => a.scheduledAt?.slice(0, 10) === date && a.status !== 'CANCELLED'); $('#stat-patients').textContent = patients.length; $('#stat-professionals').textContent = professionals.length; $('#stat-appointments').textContent = todayAppointments.length; $('#stat-date').textContent = new Date().toLocaleDateString('pt-BR', { dateStyle: 'long' }); $('#dashboard-appointments').innerHTML = table(['Horário', 'Paciente', 'Serviço', 'Profissional'], todayAppointments.slice(0, 6).map(a => [escape(dateTime(a.scheduledAt)), escape(a.patient?.name), escape(a.service?.name), escape(a.professional?.name)]), 'Nenhum atendimento agendado para hoje.'); }
function table(headers, rows, empty = 'Nenhum registro encontrado.') { if (!rows.length) return `<p class="empty-state">${empty}</p>`; return `<table class="data-table"><thead><tr>${headers.map(h => `<th>${h}</th>`).join('')}</tr></thead><tbody>${rows.map(row => `<tr>${row.map(cell => `<td>${cell ?? ''}</td>`).join('')}</tr>`).join('')}</tbody></table>`; }
function actionButtons(id, kind) { return `<div class="table-actions"><button type="button" data-edit="${kind}" data-id="${id}">Editar</button></div>`; }
function renderPatients(list = patients) { $('#patients-table').innerHTML = table(['Nome', 'CPF', 'Telefone', 'Idade', 'Ações'], list.map(p => [escape(p.name), escape(p.cpf), escape(p.phone), escape(p.age), actionButtons(p.id, 'patient')]), 'Nenhum paciente cadastrado.'); }
function renderProfessionals() { $('#professionals-table').innerHTML = table(['Nome', 'Telefone', 'Serviços', 'Ações'], professionals.map(p => [escape(p.name), escape(p.phone), (p.services || []).map(s => escape(s.name)).join(', ') || '—', actionButtons(p.id, 'professional')]), 'Nenhum profissional cadastrado.'); }
function renderServices() { $('#services-table').innerHTML = table(['Serviço', 'Tipo', 'Duração', 'Status', 'Ações'], services.map(s => [escape(s.name), escape(s.type), `${s.durationMinutes} min`, s.active ? '<span class="badge">Ativo</span>' : '<span class="badge cancelled">Inativo</span>', actionButtons(s.id, 'service')]), 'Nenhum serviço cadastrado.'); }
function renderAppointments(list = appointments) { $('#appointments-table').innerHTML = table(['Data / hora', 'Paciente', 'Serviço', 'Profissional', 'Status', 'Ações'], list.map(a => { let badge = a.status === 'CANCELLED' ? 'cancelled' : a.status === 'COMPLETED' ? 'completed' : ''; let status = `<span class="badge ${badge}">${a.status === 'SCHEDULED' ? 'Agendado' : a.status === 'CANCELLED' ? 'Cancelado' : 'Concluído'}</span>`; let action = a.status === 'SCHEDULED' ? `<div class="table-actions"><button type="button" data-edit="appointment" data-id="${a.id}">Editar</button><button type="button" data-complete="${a.id}">Concluir</button><button type="button" data-cancel="${a.id}">Cancelar</button></div>` : ''; return [escape(dateTime(a.scheduledAt)), escape(a.patient?.name), escape(a.service?.name), escape(a.professional?.name), status, action]; }), 'Nenhum agendamento encontrado.'); }
function fillSelects() { const options = (items, placeholder = 'Selecione') => `<option value="">${placeholder}</option>` + items.map(x => `<option value="${x.id}">${escape(x.name)}</option>`).join(''); $('#appointment-form [name=patientId]').innerHTML = options(patients); $('#appointment-patient-filter').innerHTML = '<option value="">Todos</option>' + patients.map(p => `<option value="${p.id}">${escape(p.name)}</option>`).join(''); $('#appointment-form [name=professionalId]').innerHTML = options(professionals); $('#appointment-form [name=serviceId]').innerHTML = options(services.filter(s => s.active)); $('#professional-service-ids').innerHTML = services.filter(s => s.active).map(s => `<option value="${s.id}">${escape(s.name)} · ${escape(s.type)}</option>`).join(''); }
function resetForm(id) { const form = $(`#${id}`); form.reset(); form.elements.id.value = ''; if (id === 'patient-form') $('#patient-form-title').textContent = 'Novo paciente'; if (id === 'professional-form') $('#professional-form-title').textContent = 'Novo profissional'; if (id === 'service-form') { $('#service-form-title').textContent = 'Novo serviço'; form.elements.active.checked = true } if (id === 'appointment-form') { $('#appointment-form-title').textContent = 'Novo agendamento'; $('#availability-hint').textContent = ''; } }
$$('[data-reset]').forEach(b => b.addEventListener('click', () => resetForm(b.dataset.reset)));
$('#patient-form').addEventListener('submit', async e => { e.preventDefault(); const f = e.currentTarget, d = Object.fromEntries(new FormData(f)); d.age = Number(d.age); const id = d.id; delete d.id; try { await api(id ? `/api/patients/${id}` : '/api/patients', { method: id ? 'PUT' : 'POST', body: JSON.stringify(d) }); notify(id ? 'Paciente atualizado.' : 'Paciente cadastrado.'); resetForm('patient-form'); await loadAll(); } catch (err) { showError(err); } });
$('#professional-form').addEventListener('submit', async e => { e.preventDefault(); const f = e.currentTarget, d = Object.fromEntries(new FormData(f)); d.serviceIds = [...f.elements.serviceIds.selectedOptions].map(o => Number(o.value)); const id = d.id; delete d.id; try { await api(id ? `/api/professionals/${id}` : '/api/professionals', { method: id ? 'PUT' : 'POST', body: JSON.stringify(d) }); notify(id ? 'Profissional atualizado.' : 'Profissional cadastrado.'); resetForm('professional-form'); await loadAll(); } catch (err) { showError(err); } });
$('#service-form').addEventListener('submit', async e => { e.preventDefault(); const f = e.currentTarget, d = Object.fromEntries(new FormData(f)); d.durationMinutes = Number(d.durationMinutes); d.active = f.elements.active.checked; const id = d.id; delete d.id; try { await api(id ? `/api/services/${id}` : '/api/services', { method: id ? 'PUT' : 'POST', body: JSON.stringify(d) }); notify(id ? 'Serviço atualizado.' : 'Serviço cadastrado.'); resetForm('service-form'); await loadAll(); } catch (err) { showError(err); } });
$('#appointment-form').addEventListener('submit', async e => { e.preventDefault(); const f = e.currentTarget, d = Object.fromEntries(new FormData(f)); d.patientId = Number(d.patientId); d.professionalId = Number(d.professionalId); d.serviceId = Number(d.serviceId); d.scheduledAt = d.scheduledAt; const id = d.id; delete d.id; try { await api(id ? `/api/appointments/${id}` : '/api/appointments', { method: id ? 'PUT' : 'POST', body: JSON.stringify(d) }); notify(id ? 'Agendamento atualizado.' : 'Agendamento criado.'); resetForm('appointment-form'); await loadAll(); } catch (err) { showError(err); } });
$('#patient-search-button').addEventListener('click', async () => { try { renderPatients(await api(`/api/patients?q=${encodeURIComponent($('#patient-search').value)}`)); } catch (e) { showError(e); } });
$('#patient-search').addEventListener('keydown', e => { if (e.key === 'Enter') { e.preventDefault(); $('#patient-search-button').click(); } });
$('#appointment-filter-button').addEventListener('click', () => { let list = [...appointments]; const date = $('#appointment-date-filter').value, pid = $('#appointment-patient-filter').value; if (date) list = list.filter(a => a.scheduledAt?.slice(0, 10) === date); if (pid) list = list.filter(a => String(a.patient?.id) === pid); renderAppointments(list); });
document.addEventListener('click', async e => {
  const edit = e.target.closest('[data-edit]');
  if (edit) {
    const itemId = Number(edit.dataset.id), kind = edit.dataset.edit;
    if (kind === 'patient') {
      const p = patients.find(x => x.id === itemId), f = $('#patient-form');
      for (const key of ['id', 'name', 'cpf', 'phone', 'age', 'allergies']) f.elements[key].value = p[key] ?? '';
      f.elements.address.value = p.address?.location ?? '';
      $('#patient-form-title').textContent = 'Editar paciente'; showPage('patients');
    }
    if (kind === 'professional') {
      const p = professionals.find(x => x.id === itemId), f = $('#professional-form');
      for (const key of ['id', 'name', 'cpf', 'phone']) f.elements[key].value = p[key] ?? '';
      f.elements.address.value = p.address?.location ?? '';
      [...f.elements.serviceIds.options].forEach(o => o.selected = (p.services || []).some(s => s.id === Number(o.value)));
      $('#professional-form-title').textContent = 'Editar profissional'; showPage('professionals');
    }
    if (kind === 'service') {
      const service = services.find(x => x.id === itemId), f = $('#service-form');
      for (const key of ['id', 'name', 'type', 'durationMinutes']) f.elements[key].value = service[key] ?? '';
      f.elements.active.checked = service.active; $('#service-form-title').textContent = 'Editar serviço'; showPage('services');
    }
    if (kind === 'appointment') {
      const a = appointments.find(x => x.id === itemId), f = $('#appointment-form');
      showPage('appointments'); f.elements.id.value = a.id; f.elements.patientId.value = a.patient.id;
      f.elements.professionalId.value = a.professional.id; f.elements.serviceId.value = a.service.id;
      f.elements.scheduledAt.value = a.scheduledAt.slice(0, 16); f.elements.notes.value = a.notes ?? '';
      $('#appointment-form-title').textContent = 'Editar agendamento';
    }
  }
  const cancel = e.target.closest('[data-cancel]');
  if (cancel && confirm('Deseja cancelar este agendamento?')) try { await api(`/api/appointments/${cancel.dataset.cancel}/status`, { method: 'PATCH', body: JSON.stringify({ status: 'CANCELLED' }) }); notify('Agendamento cancelado.'); await loadAll(); } catch (err) { showError(err); }
  const complete = e.target.closest('[data-complete]');
  if (complete && confirm('Marcar este atendimento como concluído?')) try { await api(`/api/appointments/${complete.dataset.complete}/status`, { method: 'PATCH', body: JSON.stringify({ status: 'COMPLETED' }) }); notify('Atendimento concluído.'); await loadAll(); } catch (err) { showError(err); }
  const go = e.target.closest('[data-go]'); if (go) showPage(go.dataset.go);
});
function showPage(name) { $$('.page').forEach(p => p.hidden = p.id !== `page-${name}`); $$('.nav-link').forEach(b => b.classList.toggle('active', b.dataset.page === name)); const labels = { dashboard: 'Visão geral', patients: 'Pacientes', appointments: 'Agendamentos', professionals: 'Profissionais', services: 'Serviços' }; $('#page-title').textContent = labels[name] || 'Clínica'; if (name === 'appointments') { fillSelects(); } window.scrollTo({ top: 0, behavior: 'smooth' }); }
$$('.nav-link').forEach(b => b.addEventListener('click', () => showPage(b.dataset.page)));
$('#appointment-form').addEventListener('change', async e => { if (!['professionalId', 'serviceId', 'scheduledAt'].includes(e.target.name)) return; const f = e.currentTarget, p = f.elements.professionalId.value, s = f.elements.serviceId.value, t = f.elements.scheduledAt.value, hint = $('#availability-hint'); if (!p || !s || !t) { hint.textContent = ''; return; } try { const r = await api(`/api/appointments/availability?professionalId=${p}&serviceId=${s}&startsAt=${encodeURIComponent(t)}`); hint.textContent = r.available ? 'Horário aparentemente disponível. A confirmação acontece ao salvar.' : 'Profissional não habilitado ou horário indisponível.'; hint.classList.toggle('unavailable', !r.available); } catch (err) { hint.textContent = err.message; hint.classList.add('unavailable'); } });
checkSession();
