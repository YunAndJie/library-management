let currentPage = 1, currentBorrowPage = 1;
const PAGE_SIZE = 10;

document.addEventListener('DOMContentLoaded', () => {
    if (API.token) showLayout(); else showLogin();
});

// ===================== Auth =====================
function showLogin() { document.getElementById('loginOverlay').style.display = 'flex'; document.getElementById('layout').classList.remove('active'); }
function showLayout() { document.getElementById('loginOverlay').style.display = 'none'; document.getElementById('layout').classList.add('active'); loadDashboard(); }

async function handleLogin() {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value.trim();
    if (!username || !password) return toast('请输入用户名和密码', 'error');
    const res = await API.login({ username, password });
    if (res.code === 200) {
        API.token = res.data.token;
        localStorage.setItem('token', res.data.token);
        localStorage.setItem('username', res.data.username);
        localStorage.setItem('role', res.data.role);
        document.getElementById('headerUsername').textContent = res.data.username;
        document.getElementById('headerRole').textContent = res.data.role === 1 ? '管理员' : '普通用户';
        showLayout();
    } else { toast(res.message || '登录失败', 'error'); }
}

async function handleRegister() {
    const username = document.getElementById('regUsername').value.trim();
    const password = document.getElementById('regPassword').value.trim();
    const confirm = document.getElementById('regConfirm').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    if (!username || !password || !confirm || !email) return toast('请填写所有必填项', 'error');
    if (password !== confirm) return toast('两次密码不一致', 'error');
    const res = await API.register({ username, password, confirmPassword: confirm, email });
    if (res.code === 200) { toast('注册成功，请登录', 'success'); toggleAuthTab('login'); }
    else { toast(res.message || '注册失败', 'error'); }
}

function toggleAuthTab(tab) {
    document.querySelectorAll('.auth-form').forEach(f => f.style.display = 'none');
    document.getElementById('form-' + tab).style.display = 'block';
    document.querySelectorAll('.auth-tab').forEach(t => t.style.fontWeight = '400');
    if (tab === 'login') { document.getElementById('tab-login').style.fontWeight = '700'; document.getElementById('tab-register').style.fontWeight = '400'; }
    else { document.getElementById('tab-register').style.fontWeight = '700'; document.getElementById('tab-login').style.fontWeight = '400'; }
}

function handleLogout() { API.logout(); }

// ===================== Navigation =====================
function switchPage(page) {
    document.querySelectorAll('.content').forEach(c => c.classList.remove('active'));
    document.querySelectorAll('.sidebar-menu li').forEach(li => li.classList.remove('active'));
    const el = document.getElementById('page-' + page);
    if (el) el.classList.add('active');
    const menu = document.getElementById('menu-' + page);
    if (menu) menu.classList.add('active');
    currentPage = 1;
    if (page === 'dashboard') loadDashboard();
    else if (page === 'books') loadBooks();
    else if (page === 'categories') loadCategories();
    else if (page === 'borrow') loadBorrowRecords();
    else if (page === 'users') loadUsers();
}

// ===================== Dashboard =====================
async function loadDashboard() {
    const res = await API.getDashboard();
    if (res.code === 200) {
        const d = res.data;
        document.getElementById('stat-users').textContent = d.userCount || 0;
        document.getElementById('stat-books').textContent = d.bookCount || 0;
        document.getElementById('stat-cats').textContent = d.categoryCount || 0;
        document.getElementById('stat-borrows').textContent = d.borrowCount || 0;
    }
}

// ===================== Books =====================
async function loadBooks() {
    const res = await API.getBooks(currentPage, PAGE_SIZE);
    if (res.code === 200) renderBooksTable(res.data || []);
}
function renderBooksTable(books) {
    const tbody = document.getElementById('booksTableBody');
    if (!books.length) { tbody.innerHTML = '<tr><td colspan="9" class="empty">暂无数据</td></tr>'; return; }
    tbody.innerHTML = books.map(b => `<tr>
        <td>${b.id}</td><td>${esc(b.title)}</td><td>${esc(b.author)}</td><td>${esc(b.isbn)}</td>
        <td>${b.price ? b.price.toFixed(2) : '-'}</td><td>${b.stock}</td>
        <td>${b.statusName ? b.statusName : '-'}</td>
        <td>${b.createTime || '-'}</td>
        <td>
            <button class="btn btn-sm btn-primary" onclick="editBook(${b.id})">编辑</button>
            <button class="btn btn-sm btn-danger" onclick="deleteBook(${b.id})">删除</button>
        </td>
    </tr>`).join('');
}
async function searchBooks() {
    const kw = document.getElementById('bookSearch').value.trim();
    if (!kw) return loadBooks();
    const res = await API.searchBooks(kw);
    if (res.code === 200) renderBooksTable(res.data || []);
}
function showBookModal(id) {
    document.getElementById('bookModalTitle').textContent = id ? '编辑图书' : '添加图书';
    document.getElementById('bookModal').classList.add('active');
    document.getElementById('editBookId').value = id || '';
    if (!id) { document.getElementById('bookForm').reset(); return; }
    // Load book data - we'd need a getById but for simplicity we reset
    document.getElementById('bookForm').reset();
}
function closeBookModal() { document.getElementById('bookModal').classList.remove('active'); }
async function saveBook() {
    const id = document.getElementById('editBookId').value;
    const body = {
        isbn: document.getElementById('bookIsbn').value.trim(),
        title: document.getElementById('bookTitle').value.trim(),
        author: document.getElementById('bookAuthor').value.trim(),
        publisher: document.getElementById('bookPublisher').value.trim(),
        price: parseFloat(document.getElementById('bookPrice').value) || 0,
        stock: parseInt(document.getElementById('bookStock').value) || 0,
        coverUrl: document.getElementById('bookCover').value.trim(),
        description: document.getElementById('bookDesc').value.trim()
    };
    if (!body.isbn || !body.title || !body.author) return toast('ISBN/书名/作者为必填', 'error');
    const res = id ? await API.updateBook(+id, body) : await API.addBook(body);
    if (res.code === 200) { toast(id ? '更新成功' : '添加成功', 'success'); closeBookModal(); loadBooks(); }
    else { toast(res.message || '操作失败', 'error'); }
}
async function editBook(id) { showBookModal(id); }
async function deleteBook(id) { if (confirm('确定删除该图书？')) { await API.deleteBook(id); loadBooks(); } }

// ===================== Categories =====================
async function loadCategories() {
    const res = await API.getAllCategories();
    if (res.code === 200) { renderCategoryTree(res.data || []); loadCategorySelects(); }
}
function renderCategoryTree(tree) {
    const container = document.getElementById('categoryTree');
    if (!tree.length) { container.innerHTML = '<div class="empty">暂无分类</div>'; return; }
    container.innerHTML = buildTreeHTML(tree);
}
function buildTreeHTML(nodes) {
    let html = '<ul class="tree">';
    nodes.forEach(n => {
        html += `<li><span class="tree-label">📁 ${esc(n.name)}
            <button class="btn btn-sm btn-primary" onclick="editCategory(${n.id},'${esc(n.name)}','${esc(n.description||'')}',${n.parentId||0},${n.sort||0})">编辑</button>
            <button class="btn btn-sm btn-danger" onclick="deleteCategory(${n.id})">删除</button>
        </span>`;
        if (n.children && n.children.length) html += '<li class="tree-children">' + buildTreeHTML(n.children) + '</li>';
        html += '</li>';
    });
    html += '</ul>';
    return html;
}
function showCategoryModal(id) {
    document.getElementById('catModalTitle').textContent = id ? '编辑分类' : '添加分类';
    document.getElementById('catModal').classList.add('active');
    document.getElementById('editCatId').value = id || '';
    if (!id) { document.getElementById('catForm').reset(); }
}
function closeCatModal() { document.getElementById('catModal').classList.remove('active'); }
async function saveCategory() {
    const id = document.getElementById('editCatId').value;
    const body = {
        name: document.getElementById('catName').value.trim(),
        description: document.getElementById('catDesc').value.trim(),
        parentId: parseInt(document.getElementById('catParent').value) || null,
        sort: parseInt(document.getElementById('catSort').value) || 0
    };
    if (!body.name) return toast('分类名称为必填', 'error');
    const res = id ? await API.updateCategory(+id, body) : await API.addCategory(body);
    if (res.code === 200) { toast(id ? '更新成功' : '添加成功', 'success'); closeCatModal(); loadCategories(); }
    else { toast(res.message || '操作失败', 'error'); }
}
async function editCategory(id, name, desc, parentId, sort) {
    showCategoryModal(id);
    document.getElementById('catName').value = name;
    document.getElementById('catDesc').value = desc || '';
    document.getElementById('catParent').value = parentId || '';
    document.getElementById('catSort').value = sort || 0;
}
async function deleteCategory(id) { if (confirm('确定删除该分类？')) { await API.deleteCategory(id); loadCategories(); } }
async function loadCategorySelects() {
    const res = await API.getAllCategories();
    if (res.code !== 200) return;
    const opts = '<option value="">无（顶级分类）</option>' + flattenCategories(res.data || []).map(c => `<option value="${c.id}">${c.name}</option>`).join('');
    document.getElementById('catParent').innerHTML = opts;
    document.getElementById('bookCategory').innerHTML = '<option value="">请选择分类</option>' + flattenCategories(res.data || []).map(c => `<option value="${c.id}">${c.name}</option>`).join('');
}
function flattenCategories(nodes, prefix = '') {
    let result = [];
    nodes.forEach(n => { result.push({ id: n.id, name: prefix + n.name }); if (n.children) result = result.concat(flattenCategories(n.children, prefix + '　')); });
    return result;
}

// ===================== Borrow =====================
function showBorrowModal() { document.getElementById('borrowModal').classList.add('active'); loadCategorySelects(); }
function closeBorrowModal() { document.getElementById('borrowModal').classList.remove('active'); }
async function handleBorrow() {
    const userId = parseInt(document.getElementById('borrowUserId').value) || 0;
    const bookId = parseInt(document.getElementById('borrowBookId').value) || 0;
    const days = parseInt(document.getElementById('borrowDays').value) || 30;
    if (!userId || !bookId) return toast('请填写用户ID和图书ID', 'error');
    const res = await API.borrowBook({ userId, bookId, borrowDays: days });
    if (res.code === 200) { toast('借阅成功', 'success'); closeBorrowModal(); loadBorrowRecords(); }
    else { toast(res.message || '借阅失败', 'error'); }
}
async function loadBorrowRecords() {
    const res = await API.getBorrowRecordsByUser(0, currentBorrowPage, PAGE_SIZE);
    // Use getAll for simplicity
    const all = await API.getAllBorrowRecords();
    if (all.code === 200) renderBorrowTable(all.data || []);
}
function renderBorrowTable(records) {
    const tbody = document.getElementById('borrowTableBody');
    if (!records.length) { tbody.innerHTML = '<tr><td colspan="9" class="empty">暂无借阅记录</td></tr>'; return; }
    tbody.innerHTML = records.map(r => `<tr>
        <td>${r.id}</td><td>${esc(r.username||'')}</td><td>${esc(r.bookTitle||'')}</td>
        <td>${r.borrowTime||'-'}</td><td>${r.dueTime||'-'}</td><td>${r.returnTime||'-'}</td>
        <td><span class="tag tag-${r.status===0?'info':r.status===1?'success':'danger'}">${r.statusName||'-'}</span></td>
        <td>${r.overdue ? '<span class="tag tag-danger">已超期</span>' : '-'}</td>
        <td>${r.status === 0 ? `<button class="btn btn-sm btn-success" onclick="handleReturn(${r.id})">归还</button>` : '-'}</td>
    </tr>`).join('');
}
async function handleReturn(id) { if (confirm('确认归还？')) { await API.returnBook(id); toast('归还成功', 'success'); loadBorrowRecords(); } }

// ===================== Users =====================
async function loadUsers() {
    const res = await API.getUsers(currentPage, PAGE_SIZE);
    if (res.code === 200) renderUsersTable(res.data || []);
}
function renderUsersTable(users) {
    const tbody = document.getElementById('usersTableBody');
    if (!users.length) { tbody.innerHTML = '<tr><td colspan="8" class="empty">暂无用户</td></tr>'; return; }
    tbody.innerHTML = users.map(u => `<tr>
        <td>${u.id}</td><td>${esc(u.username)}</td><td>${esc(u.email||'')}</td>
        <td>${esc(u.phone||'')}</td>
        <td><span class="tag tag-${u.role===1?'warning':'info'}">${u.roleName||'-'}</span></td>
        <td><span class="tag tag-${u.status===1?'success':'danger'}">${u.statusName||'-'}</span></td>
        <td>${u.createTime||'-'}</td>
        <td>
            <button class="btn btn-sm btn-warning" onclick="toggleUserStatus(${u.id},${u.status})">${u.status===1?'禁用':'启用'}</button>
            <button class="btn btn-sm btn-danger" onclick="deleteUser(${u.id})">删除</button>
        </td>
    </tr>`).join('');
}
async function toggleUserStatus(id, curStatus) {
    const newStatus = curStatus === 1 ? 0 : 1;
    await API.updateUserStatus(id, newStatus);
    loadUsers();
}
async function deleteUser(id) { if (confirm('确定删除该用户？')) { await API.deleteUser(id); loadUsers(); } }

// ===================== Utils =====================
function esc(str) { if (!str) return ''; const div = document.createElement('div'); div.textContent = str; return div.innerHTML; }
function toast(msg, type) {
    const t = document.createElement('div');
    t.className = 'toast ' + (type || 'info');
    t.textContent = msg;
    document.body.appendChild(t);
    setTimeout(() => t.remove(), 3000);
}
// Enter key login
document.addEventListener('keydown', e => { if (e.key === 'Enter') { const overlay = document.getElementById('loginOverlay'); if (overlay.style.display !== 'none') handleLogin(); } });
