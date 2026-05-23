const API_BASE = '/api';

function getToken() {
    return localStorage.getItem('jwt_token');
}

function getUserInfo() {
    return JSON.parse(localStorage.getItem('user_info') || '{}');
}

function setAuthAction(token, userInfo) {
    localStorage.setItem('jwt_token', token);
    localStorage.setItem('user_info', JSON.stringify(userInfo));
}

function logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_info');
    window.location.href = '/index.html';
}

function getAuthHeaders() {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json'
    };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

async function fetchAPI(endpoint, method = 'GET', body = null) {
    try {
        const options = {
            method,
            headers: getAuthHeaders()
        };
        if (body) {
            options.body = JSON.stringify(body);
        }
        console.log(`🔗 API Call: ${method} ${endpoint}`, body);
        const response = await fetch(`${API_BASE}${endpoint}`, options);
        
        if (!response.ok) {
            console.error(`❌ API Error - Status: ${response.status}`);
            if (response.status === 401) {
                logout();
            }
            // Try to get error message from response
            try {
                const errorData = await response.json();
                console.error('Error details:', errorData);
                throw new Error(errorData.message || errorData.error || `HTTP ${response.status}`);
            } catch (e) {
                throw new Error(`API Error: ${response.status} - ${response.statusText}`);
            }
        }
        
        // If empty response
        if (response.headers.get("content-length") === "0" || response.status === 204) {
            console.log('✅ API Response: Empty (204)');
            return null;
        }
        const data = await response.json();
        console.log('✅ API Response:', data);
        return data;
    } catch (error) {
        console.error('🔴 Fetch Error:', error.message);
        throw error;
    }
}

function checkAccess(allowedRole) {
    const token = getToken();
    const user = getUserInfo();
    
    if (!token) {
        // No token - redirect to login
        if (window.location.pathname !== '/index.html' && window.location.pathname !== '/') {
            window.location.href = '/index.html';
        }
    } else if (allowedRole && user.role !== allowedRole) {
        // Token exists but wrong role - redirect based on actual role
        if (user.role === 'ROLE_CITIZEN') window.location.href = '/citizen.html';
        else if (user.role === 'ROLE_OFFICER') window.location.href = '/officer.html';
        else if (user.role === 'ROLE_ADMIN') window.location.href = '/admin.html';
    }
}
