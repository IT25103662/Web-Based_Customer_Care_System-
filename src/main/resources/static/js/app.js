// LankaConnect CCMS Global Application Utilities

const App = {
    // Current authenticated user
    currentUser: null,

    // Initialize page authentication check
    async initAuth(allowedRoles = []) {
        try {
            const res = await fetch('/api/auth/current-user');
            if (!res.ok) {
                // If not logged in and not on login page, redirect
                if (!window.location.pathname.endsWith('login.html') && !window.location.pathname.endsWith('index.html')) {
                    window.location.href = '/login.html';
                }
                return null;
            }
            const user = await res.json();
            App.currentUser = user;

            // Role guard
            if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
                App.showToast('Access denied for this page. Redirecting to your dashboard...', 'warning');
                setTimeout(() => App.redirectUserByRole(user.role), 1500);
                return null;
            }

            App.renderNavbarUser(user);
            return user;
        } catch (err) {
            console.error("Auth check failed:", err);
            return null;
        }
    },

    // Render user profile snippet in top navbar
    renderNavbarUser(user) {
        const userEl = document.getElementById('navbarUserSnippet');
        if (!userEl) return;

        let loyaltyHtml = '';
        if (user.role === 'CUSTOMER' && user.loyaltyTier) {
            const badgeClass = user.loyaltyTier.toLowerCase() === 'gold' ? 'badge-gold' :
                               user.loyaltyTier.toLowerCase() === 'silver' ? 'badge-silver' : 'badge-bronze';
            loyaltyHtml = `<span class="${badgeClass} me-2"><i class="bi bi-award-fill me-1"></i>${user.loyaltyTier} MEMBER</span>`;
        }

        userEl.innerHTML = `
            <div class="d-flex align-items-center">
                ${loyaltyHtml}
                <div class="me-3 text-end d-none d-md-block">
                    <div class="fw-bold text-dark" style="font-size: 0.9rem;">${user.fullName}</div>
                    <small class="text-muted" style="font-size: 0.75rem;">${user.role} ${user.department ? '• ' + user.department : ''}</small>
                </div>
                <div class="dropdown">
                    <button class="btn btn-light rounded-circle p-2 border shadow-sm" type="button" data-bs-toggle="dropdown">
                        <i class="bi bi-person-circle fs-5 text-primary"></i>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end shadow border-0">
                        <li class="dropdown-header text-dark fw-bold">${user.username}</li>
                        <li><span class="dropdown-item-text small text-muted">${user.email}</span></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="javascript:void(0)" onclick="App.logout()"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
                    </ul>
                </div>
            </div>
        `;
    },

    // Redirect to respective dashboard
    redirectUserByRole(role) {
        switch (role) {
            case 'CUSTOMER':
                window.location.href = '/customer-dashboard.html';
                break;
            case 'AGENT':
                window.location.href = '/agent-dashboard.html';
                break;
            case 'STAFF':
                window.location.href = '/staff-dashboard.html';
                break;
            case 'MANAGER':
                window.location.href = '/manager-dashboard.html';
                break;
            case 'ADMIN':
                window.location.href = '/admin-dashboard.html';
                break;
            default:
                window.location.href = '/customer-dashboard.html';
        }
    },

    // Logout
    async logout() {
        try {
            await fetch('/api/auth/logout', { method: 'POST' });
        } catch (e) {
            console.error(e);
        }
        window.location.href = '/login.html';
    },

    // Toast notification
    showToast(message, type = 'success') {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
            container.style.zIndex = '9999';
            document.body.appendChild(container);
        }

        const bgClass = type === 'success' ? 'bg-success text-white' :
                        type === 'danger' || type === 'error' ? 'bg-danger text-white' :
                        type === 'warning' ? 'bg-warning text-dark' : 'bg-primary text-white';

        const toastId = 'toast_' + Date.now();
        const toastHtml = `
            <div id="${toastId}" class="toast align-items-center ${bgClass} border-0 shadow-lg" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-body fw-medium">
                        <i class="bi bi-${type === 'success' ? 'check-circle-fill' : type === 'warning' ? 'exclamation-triangle-fill' : 'info-circle-fill'} me-2"></i>
                        ${message}
                    </div>
                    <button type="button" class="btn-close ${type !== 'warning' ? 'btn-close-white' : ''} me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', toastHtml);
        const toastElement = document.getElementById(toastId);
        const bsToast = new bootstrap.Toast(toastElement, { delay: 4000 });
        bsToast.show();
    },

    // Format ISO Date
    formatDate(dateStr) {
        if (!dateStr) return 'N/A';
        const d = new Date(dateStr);
        return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    },

    // Render Status Pill
    renderStatus(status) {
        if (!status) return '';
        const s = status.toLowerCase();
        let icon = 'circle-fill';
        if (s.includes('resolved') || s.includes('closed')) icon = 'check-circle-fill';
        if (s.includes('investigation') || s.includes('progress')) icon = 'hourglass-split';
        if (s.includes('escalated')) icon = 'exclamation-triangle-fill';

        return `<span class="status-pill status-${s}"><i class="bi bi-${icon}" style="font-size: 0.65rem;"></i>${status.replace('_', ' ')}</span>`;
    },

    // Render Priority Pill
    renderPriority(priority) {
        if (!priority) return '';
        const p = priority.toUpperCase();
        const color = p === 'CRITICAL' ? 'bg-danger' :
                      p === 'HIGH' ? 'bg-warning text-dark' :
                      p === 'MEDIUM' ? 'bg-info text-dark' : 'bg-secondary';
        return `<span class="badge ${color}">${p}</span>`;
    }
};
