const API = {
    base: '/api',
    token: localStorage.getItem('token') || '',

    headers() {
        const h = { 'Content-Type': 'application/json' };
        if (this.token) h['Authorization'] = 'Bearer ' + this.token;
        return h;
    },

    async request(method, url, body) {
        const opts = { method, headers: this.headers() };
        if (body) opts.body = JSON.stringify(body);
        const res = await fetch(this.base + url, opts);
        const data = await res.json();
        if (data.code === 401) { localStorage.clear(); location.reload(); }
        return data;
    },

    get(url) { return this.request('GET', url); },
    post(url, body) { return this.request('POST', url, body); },
    put(url, body) { return this.request('PUT', url, body); },
    del(url) { return this.request('DELETE', url); },

    // Auth
    login(body) { return this.post('/users/login', body); },
    register(body) { return this.post('/users/register', body); },
    logout() { this.post('/users/logout', {}); localStorage.clear(); location.reload(); },

    // Users
    getUsers(page, size) { return this.get(`/users/page?pageNum=${page}&pageSize=${size}`); },
    getUserCount() { return this.get('/users/count'); },
    updateUserStatus(id, status) { return this.put(`/users/${id}/status/${status}`); },
    deleteUser(id) { return this.del(`/users/${id}`); },

    // Books
    addBook(body) { return this.post('/books', body); },
    getBooks(page, size) { return this.get(`/books/page?pageNum=${page}&pageSize=${size}`); },
    searchBooks(kw) { return this.get(`/books/search?keyword=${encodeURIComponent(kw)}`); },
    getBookCount() { return this.get('/books/count'); },
    updateBook(id, body) { return this.put(`/books/${id}`, body); },
    deleteBook(id) { return this.del(`/books/${id}`); },

    // Categories
    getAllCategories() { return this.get('/categories'); },
    getCategoryCount() { return this.get('/categories/count'); },
    addCategory(body) { return this.post('/categories', body); },
    updateCategory(id, body) { return this.put(`/categories/${id}`, body); },
    deleteCategory(id) { return this.del(`/categories/${id}`); },

    // Borrow
    borrowBook(body) { return this.post('/borrow', body); },
    returnBook(id) { return this.put(`/borrow/${id}/return`); },
    getAllBorrowRecords() { return this.get('/borrow'); },
    getBorrowRecordsByUser(uid, page, size) { return this.get(`/borrow/user/${uid}?pageNum=${page}&pageSize=${size}`); },
    getBorrowCount() { return this.get('/borrow/count'); },

    // Admin
    getDashboard() { return this.get('/admin/dashboard'); }
};
