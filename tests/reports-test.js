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

    return res.json('accessToken');
}

export function setup() {
    return {
        tokens: [
            'eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInRva2VuVHlwZSI6ImFjY2VzcyIsInVzZXJJZCI6MTEsInN1YiI6InVzZXJAZ21haWwuY29tIiwiaWF0IjoxNzc2NzA3NTI4LCJleHAiOjE3NzY3OTM5Mjh9.U820ajy4yIgYXY35FgzrFGxaNzFr9EbtPnk4vZCyjkM',
            'eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInRva2VuVHlwZSI6ImFjY2VzcyIsInVzZXJJZCI6Nywic3ViIjoiQWZuYW5AZ21haWwuY29tIiwiaWF0IjoxNzc2NzA3NTU0LCJleHAiOjE3NzY3OTM5NTR9.ME-wYilFrx1wl6T8-k0acBPMJcQWRVU9G1h7A7iz62E',
            'eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInRva2VuVHlwZSI6ImFjY2VzcyIsInVzZXJJZCI6MjgsInN1YiI6IlVzZXIxMUB3YXNlbC5wcyIsImlhdCI6MTc3NjcwNzYwOCwiZXhwIjoxNzc2Nzk0MDA4fQ.F0MApN-kGanaBt8ZtVAR59hfpin9790MjwfPKeriE7k'
        ]
    };
}

function getHeaders(data) {
    const tokens = data.tokens;
    const randomToken = tokens[Math.floor(Math.random() * tokens.length)];

    return {
        headers: {
            'Authorization': `Bearer ${randomToken}`,
            'Content-Type': 'application/json'
        }
    };
}

// ==========================
// REPORTS HELPERS
// ==========================

function getAllReports(data) {
    const res = http.get(`${BASE_URL}/api/v1/reports`, getHeaders(data));

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

    const res = http.post(`${BASE_URL}/api/v1/reports`, payload, getHeaders(data));

    check(res, {
        'reports POST - success': (r) => r.status === 201 || r.status === 400,
    });

    sleep(1);
}

function getReportById(data) {
    const res = http.get(`${BASE_URL}/api/v1/reports/1`, getHeaders(data));

    check(res, {
        'reports GET by ID - 200': (r) => r.status === 200,
    });

    sleep(0.5);
}

function getMyAlerts(data) {
    const res = http.get(`${BASE_URL}/api/v1/alerts/me`, getHeaders(data));

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