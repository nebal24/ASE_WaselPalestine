import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8081';

export const options = {
    scenarios: {
        read_heavy_reports: {
            executor: 'constant-vus',
            vus: 10,
            duration: '30s',
            exec: 'readHeavyReports',
        },
        write_heavy_reports: {
            executor: 'constant-vus',
            vus: 5,
            duration: '20s',
            exec: 'writeHeavyReports',
            startTime: '31s',
        },
        mixed_reports: {
            executor: 'constant-vus',
            vus: 6,
            duration: '20s',
            exec: 'mixedReports',
            startTime: '52s',
        },
        spike_reports: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '5s', target: 2 },
                { duration: '5s', target: 25 },
                { duration: '10s', target: 25 },
                { duration: '5s', target: 0 },
            ],
            exec: 'spikeReports',
            startTime: '73s',
        },
        soak_reports: {
            executor: 'constant-vus',
            vus: 4,
            duration: '1m',
            exec: 'soakReports',
            startTime: '99s',
        },
    },
};

function getToken() {
    const res = http.post(`${BASE_URL}/api/v1/auth/authenticate`, JSON.stringify({
        email: 'admin@wasel.ps',
        password: 'password123'
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    check(res, {
        'auth login - 200': (r) => r.status === 200,
    });

    return res.json('token');
}

export function setup() {
    const token = getToken();
    return { token };
}

function getHeaders(token) {
    return {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    };
}

// ==========================
// REPORTS HELPERS
// ==========================

function getAllReports(data) {
    const res = http.get(`${BASE_URL}/api/v1/reports`, getHeaders(data.token));

    check(res, {
        'reports GET all - 200': (r) => r.status === 200,
    });

    sleep(0.5);
}

function createReport(data) {
    const payload = JSON.stringify({
        description: `Load test report ${Date.now()}`,
        category: 'DELAY',
        latitude: 31.89,
        longitude: 35.2
    });

    const res = http.post(`${BASE_URL}/api/v1/reports`, payload, getHeaders(data.token));

    check(res, {
        'reports POST - success': (r) => r.status === 201 || r.status === 400,
    });

    sleep(1);
}

function getReportById(data) {
    const res = http.get(`${BASE_URL}/api/v1/reports/1`, getHeaders(data.token));

    check(res, {
        'reports GET by ID - 200': (r) => r.status === 200,
    });

    sleep(0.5);
}

function getMyAlerts(data) {
    const res = http.get(`${BASE_URL}/api/v1/alerts/me`, getHeaders(data.token));

    check(res, {
        'alerts GET my alerts - 200': (r) => r.status === 200,
    });

    sleep(0.5);
}

// ==========================
// SCENARIOS
// ==========================

export function readHeavyReports(data) {
    getAllReports(data);
    getAllReports(data);
    getAllReports(data);
    getReportById(data);
}

export function writeHeavyReports(data) {
    createReport(data);
    createReport(data);
}

export function mixedReports(data) {
    getAllReports(data);
    createReport(data);
    getReportById(data);
    getMyAlerts(data);
}

export function spikeReports(data) {
    createReport(data);
    getAllReports(data);
}

export function soakReports(data) {
    getAllReports(data);
    getReportById(data);
    sleep(1);
}