import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8081';

function getToken() {
    const res = http.post(`${BASE_URL}/api/v1/auth/authenticate`, JSON.stringify({
        email: 'admin@wasel.ps',
        password: 'password123'
    }), { headers: { 'Content-Type': 'application/json' } });
    return res.json('token');
}

export function setup() {
    const token = getToken();
    return { token: token };
}

function getHeaders(token) {
    return { headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' } };
}

let incidentId = null;

// 1. GET all incidents (Read-heavy)
function getAllIncidents(data) {
    const res = http.get(`${BASE_URL}/api/v1/incidents`, getHeaders(data.token));
    check(res, { 'GET all - 200': (r) => r.status === 200 });
    sleep(0.5);
}

// 2. POST new incident (Write-heavy)
function createIncident(data) {
    const payload = JSON.stringify({
        description: `Load test ${Date.now()}`,
        category: 'ACCIDENT',
        severity: 'MEDIUM',
        latitude: 32.2,
        longitude: 35.3,
        checkpointId: 1,
    });
    const res = http.post(`${BASE_URL}/api/v1/incidents`, payload, getHeaders(data.token));
    check(res, { 'POST - 201': (r) => r.status === 201 });
    if (res.status === 201) {
        incidentId = res.json('id');
    }
    sleep(1);
}

// 3. Get incident by ID
function getIncidentById(data) {
    if (!incidentId) return;
    const res = http.get(`${BASE_URL}/api/v1/incidents/${incidentId}`, getHeaders(data.token));
    check(res, { 'GET by ID - 200': (r) => r.status === 200 });
    sleep(0.5);
}

// 4. Verify incident
function verifyIncident(data) {
    if (!incidentId) return;
    const res = http.patch(`${BASE_URL}/api/v1/incidents/${incidentId}/verify`, null, getHeaders(data.token));
    check(res, { 'Verify - 200': (r) => r.status === 200 });
    sleep(0.5);
}

// 5. Close incident
function closeIncident(data) {
    if (!incidentId) return;
    const res = http.patch(`${BASE_URL}/api/v1/incidents/${incidentId}/close`, null, getHeaders(data.token));
    check(res, { 'Close - 200': (r) => r.status === 200 });
    sleep(0.5);
}

// 6. Delete incident
function deleteIncident(data) {
    if (!incidentId) return;
    const res = http.del(`${BASE_URL}/api/v1/incidents/${incidentId}`, null, getHeaders(data.token));
    check(res, { 'Delete - 204': (r) => r.status === 204 });
    sleep(0.5);
}

// 7. Route API
function getRoute(data) {
    const res = http.get(`${BASE_URL}/api/v1/routes?originLat=32.2&originLon=35.3&destinationLat=31.5&destinationLon=34.5`, getHeaders(data.token));
    check(res, { 'Route - 200': (r) => r.status === 200 });
    sleep(0.5);
}

// 8. Filtering
function filteringTest(data) {
    const res = http.get(`${BASE_URL}/api/v1/incidents?category=ACCIDENT&severity=HIGH`, getHeaders(data.token));
    check(res, { 'Filter - 200': (r) => r.status === 200 });
    sleep(0.5);
}

// 9. Pagination
function paginationTest(data) {
    const res = http.get(`${BASE_URL}/api/v1/incidents?page=0&size=5`, getHeaders(data.token));
    check(res, { 'Pagination - 200': (r) => r.status === 200 });
    sleep(0.5);
}

// 10. Sorting
function sortingTest(data) {
    const res = http.get(`${BASE_URL}/api/v1/incidents?sortBy=createdAt&sortDirection=DESC`, getHeaders(data.token));
    check(res, { 'Sorting - 200': (r) => r.status === 200 });
    sleep(0.5);
}

// Mixed workload
export default function (data) {
    getAllIncidents(data);
    createIncident(data);
    getIncidentById(data);
    verifyIncident(data);
    closeIncident(data);
    deleteIncident(data);
    getRoute(data);
    filteringTest(data);
    paginationTest(data);
    sortingTest(data);
}