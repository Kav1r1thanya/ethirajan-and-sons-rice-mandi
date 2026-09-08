// security.js
(function() {
    const role = localStorage.getItem('userRole');
    const path = window.location.pathname;

    // 1. Kick out if not logged in at all
    if (!role && !path.includes('login.html')) {
        window.location.href = 'login.html';
        return;
    }

    // 2. Define EXACTLY what Staff CANNOT see
    // Add any missing files here exactly as they appear in your folder
    const adminOnlyPages = [
        'inventory.html', 
        'statistics.html', 
        'suppliers.html', 
        'customers.html', 
        'debt.html',
        'customer-profile.html'
    ];

    const isRestricted = adminOnlyPages.some(page => path.includes(page));

    // 3. The Bouncer Logic
    if (role === 'STAFF' && isRestricted) {
        alert("⛔ ACCESS DENIED: Admin Credentials Required.");
        window.location.href = 'index.html'; 
    }
})();