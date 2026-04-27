/* ════════════════════════════════════════════
   BITWISE BANKING — App Logic
   ════════════════════════════════════════════ */

const API = '';

/* ── Navigation ─────────────────────────────── */

function navigate(sectionId, tab) {
  document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
  document.querySelectorAll('.nav-link').forEach(a => a.classList.remove('active'));

  const sec = document.getElementById('section-' + sectionId);
  if (sec) sec.classList.add('active');

  document.querySelectorAll(`[data-section="${sectionId}"]`).forEach(a => a.classList.add('active'));

  if (tab) showTab(tab);

  if (sectionId === 'dashboard')  carregarDashboard();
  if (sectionId === 'usuarios')   carregarUsuarios();
  if (sectionId === 'contas')     { carregarContas(); popularSelects(); }
  if (sectionId === 'transacoes') popularSelects();
}

document.querySelectorAll('.nav-link').forEach(a => {
  a.addEventListener('click', e => {
    e.preventDefault();
    const tab = a.dataset.tab || null;
    navigate(a.dataset.section, tab);
    if (window.innerWidth <= 768) closeSidebar();
  });
});

function refreshPage() {
  const active = document.querySelector('.section.active');
  if (!active) return;
  const id = active.id.replace('section-', '');
  navigate(id);
}

/* ── Sidebar toggle (mobile) ─────────────────── */

function toggleSidebar() {
  document.getElementById('sidebar').classList.toggle('open');
}

function closeSidebar() {
  document.getElementById('sidebar').classList.remove('open');
}

/* ── Tabs (Transações) ───────────────────────── */

function showTab(tabId) {
  document.querySelectorAll('.tx-panel').forEach(p => p.style.display = 'none');
  document.querySelectorAll('.tx-tab').forEach(t => t.classList.remove('active'));

  const panel = document.getElementById('tab-' + tabId);
  if (panel) panel.style.display = 'block';

  const tab = document.querySelector(`.tx-tab[data-tab="${tabId}"]`);
  if (tab) tab.classList.add('active');
}

/* ── Toast ───────────────────────────────────── */

let toastTimer;

function toast(msg, type = 'info') {
  const el = document.getElementById('toast');
  clearTimeout(toastTimer);

  const icons = {
    success: `<svg class="toast-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>`,
    error:   `<svg class="toast-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>`,
    info:    `<svg class="toast-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12" y2="16"/></svg>`,
  };

  el.innerHTML = (icons[type] || '') + `<span>${msg}</span>`;
  el.className = `toast ${type} show`;
  toastTimer = setTimeout(() => el.classList.remove('show'), 3800);
}

/* ── Modal ───────────────────────────────────── */

function showModal(title, message, onConfirm) {
  document.getElementById('modal-title').textContent   = title;
  document.getElementById('modal-message').textContent = message;
  document.getElementById('modal-overlay').style.display = 'flex';
  document.getElementById('modal-confirm').onclick = () => { closeModal(); onConfirm(); };
}

function closeModal() {
  document.getElementById('modal-overlay').style.display = 'none';
}

function overlayClick(e) {
  if (e.target === document.getElementById('modal-overlay')) closeModal();
}

/* ── Utils ───────────────────────────────────── */

function fmtBRL(v) {
  return (parseFloat(v) || 0).toLocaleString('pt-BR', { style:'currency', currency:'BRL' });
}

function initials(name) {
  return (name || '?').split(' ').slice(0,2).map(w => w[0]).join('').toUpperCase();
}

function togglePwd(id, btn) {
  const inp = document.getElementById(id);
  inp.type = inp.type === 'password' ? 'text' : 'password';
  btn.style.opacity = inp.type === 'text' ? '1' : '';
}

async function apiFetch(url, opts = {}) {
  const res = await fetch(API + url, {
    headers: { 'Content-Type': 'application/json', ...opts.headers },
    ...opts,
  });

  if (res.status === 204) return null;

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    if (data && typeof data === 'object' && !data.message && !data.error) {
      throw new Error(Object.values(data)[0] || `Erro ${res.status}`);
    }
    throw new Error(data?.message || data?.error || `Erro ${res.status}`);
  }
  return data;
}

function nomeByUserId(uid, usuarios) {
  return (usuarios || []).find(u => u.id == uid)?.nome ?? `Usuário #${uid}`;
}

/* ════════════════════════════════════════════
   DASHBOARD
   ════════════════════════════════════════════ */

async function carregarDashboard() {
  try {
    const [usuarios, contas] = await Promise.all([
      apiFetch('/usuarios'),
      apiFetch('/accounts'),
    ]);

    document.getElementById('stat-usuarios').textContent = usuarios.length;
    document.getElementById('stat-contas').textContent   = contas.length;

    const total = contas.reduce((s, c) => s + (parseFloat(c.balance) || 0), 0);
    document.getElementById('stat-saldo').textContent    = fmtBRL(total);

    const box = document.getElementById('dashboard-contas');

    if (!contas.length) {
      box.innerHTML = emptyState('Nenhuma conta cadastrada ainda');
      return;
    }

    const rows = [...contas].reverse().slice(0, 6).map(c => `
      <tr>
        <td><span class="chip chip-blue">#${c.id}</span></td>
        <td>
          <div style="display:flex;align-items:center;gap:10px">
            <div class="avatar" style="width:30px;height:30px;border-radius:8px;font-size:.75rem">${initials(nomeByUserId(c.usuarioId, usuarios))}</div>
            <span>${nomeByUserId(c.usuarioId, usuarios)}</span>
          </div>
        </td>
        <td class="td-mono balance-text">${fmtBRL(c.balance)}</td>
      </tr>`).join('');

    box.innerHTML = `
      <div class="table-wrap">
        <table>
          <thead><tr><th>Conta</th><th>Titular</th><th>Saldo</th></tr></thead>
          <tbody>${rows}</tbody>
        </table>
      </div>`;
  } catch (e) {
    toast('Erro ao carregar dashboard: ' + e.message, 'error');
  }
}

/* ════════════════════════════════════════════
   USUÁRIOS
   ════════════════════════════════════════════ */

async function carregarUsuarios() {
  const box = document.getElementById('lista-usuarios');
  box.innerHTML = loadingHTML();
  try {
    const list = await apiFetch('/usuarios');
    if (!list.length) { box.innerHTML = emptyState('Nenhum usuário cadastrado'); return; }
    box.innerHTML = list.map(u => `
      <div class="user-row">
        <div class="avatar">${initials(u.nome)}</div>
        <div class="user-meta">
          <strong>${u.nome}</strong>
          <small>${u.email}</small>
        </div>
        <span class="chip chip-purple">${(u.accounts||[]).length} conta(s)</span>
      </div>`).join('');
  } catch (e) { box.innerHTML = errHTML(e.message); }
}

async function criarUsuario(e) {
  e.preventDefault();
  const btn = document.getElementById('btn-criar-usuario');
  setLoading(btn, true, 'Criando...');
  try {
    await apiFetch('/usuarios', {
      method: 'POST',
      body: JSON.stringify({
        nome:  document.getElementById('u-nome').value.trim(),
        email: document.getElementById('u-email').value.trim(),
        senha: document.getElementById('u-senha').value,
      }),
    });
    toast('Usuário criado com sucesso!', 'success');
    e.target.reset();
    carregarUsuarios();
    popularSelects();
  } catch (err) {
    toast('Erro: ' + err.message, 'error');
  } finally {
    setLoading(btn, false, 'Criar usuário');
  }
}

/* ════════════════════════════════════════════
   CONTAS
   ════════════════════════════════════════════ */

async function carregarContas() {
  const box = document.getElementById('lista-contas');
  box.innerHTML = loadingHTML();
  try {
    const [contas, usuarios] = await Promise.all([apiFetch('/accounts'), apiFetch('/usuarios')]);
    if (!contas.length) { box.innerHTML = emptyState('Nenhuma conta cadastrada'); return; }
    const rows = contas.map(c => `
      <tr>
        <td><span class="chip chip-blue">#${c.id}</span></td>
        <td>
          <div style="display:flex;align-items:center;gap:10px">
            <div class="avatar" style="width:30px;height:30px;border-radius:8px;font-size:.75rem">${initials(nomeByUserId(c.usuarioId,usuarios))}</div>
            <span>${nomeByUserId(c.usuarioId,usuarios)}</span>
          </div>
        </td>
        <td class="td-mono balance-text">${fmtBRL(c.balance)}</td>
        <td>
          <button class="btn-icon-del" title="Excluir conta" onclick="confirmarDeletar(${c.id})">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14H6L5 6"/><path d="M10 11v6M14 11v6"/><path d="M9 6V4h6v2"/>
            </svg>
          </button>
        </td>
      </tr>`).join('');
    box.innerHTML = `
      <div class="table-wrap">
        <table>
          <thead><tr><th>Conta</th><th>Titular</th><th>Saldo</th><th></th></tr></thead>
          <tbody>${rows}</tbody>
        </table>
      </div>`;
  } catch (e) { box.innerHTML = errHTML(e.message); }
}

async function criarConta(e) {
  e.preventDefault();
  const btn = document.getElementById('btn-criar-conta');
  setLoading(btn, true, 'Criando...');
  try {
    await apiFetch('/accounts', {
      method: 'POST',
      body: JSON.stringify({
        usuarioId:      parseInt(document.getElementById('c-usuario').value),
        initialBalance: parseFloat(document.getElementById('c-saldo').value) || 0,
      }),
    });
    toast('Conta criada com sucesso!', 'success');
    e.target.reset();
    carregarContas();
    popularSelects();
  } catch (err) {
    toast('Erro: ' + err.message, 'error');
  } finally {
    setLoading(btn, false, 'Criar conta');
  }
}

function confirmarDeletar(id) {
  showModal(
    'Excluir conta',
    `Tem certeza que deseja excluir a conta #${id}? Esta ação não pode ser desfeita.`,
    async () => {
      try {
        await apiFetch(`/accounts/${id}`, { method: 'DELETE' });
        toast('Conta excluída.', 'success');
        carregarContas();
        popularSelects();
      } catch (err) { toast('Erro: ' + err.message, 'error'); }
    }
  );
}

/* ════════════════════════════════════════════
   SELECTS
   ════════════════════════════════════════════ */

async function popularSelects() {
  try {
    const [usuarios, contas] = await Promise.all([apiFetch('/usuarios'), apiFetch('/accounts')]);

    const selU = document.getElementById('c-usuario');
    if (selU) {
      const v = selU.value;
      selU.innerHTML = '<option value="">Selecione um usuário…</option>'
        + usuarios.map(u => `<option value="${u.id}">${u.nome}</option>`).join('');
      if (v) selU.value = v;
    }

    const opts = '<option value="">Selecione uma conta…</option>'
      + contas.map(c => `<option value="${c.id}">Conta #${c.id} — ${nomeByUserId(c.usuarioId,usuarios)} (${fmtBRL(c.balance)})</option>`).join('');

    ['dep-conta','saq-conta','trf-origem','trf-destino'].forEach(id => {
      const el = document.getElementById(id);
      if (el) el.innerHTML = opts;
    });
  } catch { /* silent */ }
}

/* ════════════════════════════════════════════
   TRANSAÇÕES
   ════════════════════════════════════════════ */

async function realizarDeposito(e) {
  e.preventDefault();
  const id    = document.getElementById('dep-conta').value;
  const valor = parseFloat(document.getElementById('dep-valor').value);
  const btn   = e.target.querySelector('button[type=submit]');
  setLoading(btn, true, 'Processando…');
  try {
    const res = await apiFetch(`/accounts/${id}/deposits`, {
      method: 'POST',
      body: JSON.stringify({ amount: valor }),
    });
    toast(`Depósito de ${fmtBRL(valor)} realizado! Saldo: ${fmtBRL(res.balance)}`, 'success');
    e.target.reset();
    popularSelects();
  } catch (err) { toast('Erro: ' + err.message, 'error'); }
  finally { setLoading(btn, false, 'Confirmar depósito'); }
}

async function realizarSaque(e) {
  e.preventDefault();
  const id    = document.getElementById('saq-conta').value;
  const valor = parseFloat(document.getElementById('saq-valor').value);
  const btn   = e.target.querySelector('button[type=submit]');
  setLoading(btn, true, 'Processando…');
  try {
    const res = await apiFetch(`/accounts/${id}/withdrawals`, {
      method: 'POST',
      body: JSON.stringify({ amount: valor }),
    });
    toast(`Saque de ${fmtBRL(valor)} realizado! Saldo: ${fmtBRL(res.balance)}`, 'success');
    e.target.reset();
    popularSelects();
  } catch (err) { toast('Erro: ' + err.message, 'error'); }
  finally { setLoading(btn, false, 'Confirmar saque'); }
}

async function realizarTransferencia(e) {
  e.preventDefault();
  const from  = document.getElementById('trf-origem').value;
  const to    = document.getElementById('trf-destino').value;
  const valor = parseFloat(document.getElementById('trf-valor').value);
  const btn   = e.target.querySelector('button[type=submit]');

  if (from === to) { toast('Origem e destino não podem ser iguais.', 'error'); return; }

  setLoading(btn, true, 'Processando…');
  try {
    await apiFetch('/accounts/transfers', {
      method: 'POST',
      body: JSON.stringify({ fromAccountId: parseInt(from), toAccountId: parseInt(to), amount: valor }),
    });
    toast(`Transferência de ${fmtBRL(valor)} realizada!`, 'success');
    e.target.reset();
    popularSelects();
  } catch (err) { toast('Erro: ' + err.message, 'error'); }
  finally { setLoading(btn, false, 'Confirmar transferência'); }
}

/* ════════════════════════════════════════════
   HELPERS
   ════════════════════════════════════════════ */

function setLoading(btn, loading, label) {
  btn.disabled    = loading;
  btn.textContent = label;
}

function loadingHTML() {
  return `<div class="loading-state"><div class="spinner"></div><span>Carregando…</span></div>`;
}

function emptyState(msg) {
  return `
    <div class="empty-state">
      <svg class="empty-svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
        <circle cx="12" cy="12" r="10"/><line x1="8" y1="15" x2="16" y2="15"/>
        <line x1="9" y1="9" x2="9.01" y2="9"/><line x1="15" y1="9" x2="15.01" y2="9"/>
      </svg>
      <span>${msg}</span>
    </div>`;
}

function errHTML(msg) {
  return `<div class="empty-state" style="color:var(--red)"><span>⚠ ${msg}</span></div>`;
}

/* ── Init ──────────────────────────────────── */

document.addEventListener('DOMContentLoaded', carregarDashboard);
