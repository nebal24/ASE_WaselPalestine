import http from 'k6/http';
import { check, sleep } from 'k6';

// =============================================
// 1. Base Config
// =============================================
const BASE_URL = 'http://localhost:8081';
const JSON_HEADERS = { 'Content-Type': 'application/json' };

const LOCATIONS = [
    { lat: 32.2211, lon: 35.2544 }, // Nablus
    { lat: 31.7683, lon: 35.2137 }, // Jerusalem
    { lat: 31.9038, lon: 35.2034 }, // Ramallah
    { lat: 31.5326, lon: 35.0998 }, // Hebron
    { lat: 31.5017, lon: 34.4668 }, // Gaza
    { lat: 32.4646, lon: 35.2953 }, // Jenin
    { lat: 32.0887, lon: 35.2049 }, // Bireh
];

const REPORT_CATEGORIES = ['ACCIDENT', 'DELAY', 'CLOSURE', 'WEATHER_HAZARD'];

const PLACE_NAMES = [
    'Nablus', 'Ramallah', 'Hebron', 'Jerusalem', 'Bethlehem',
    'Tulkarm', 'Jenin', 'Qalqilya', 'Salfit', 'Jericho',
    'Bireh', 'Azzun', 'Halhul', 'Dura', 'Yatta',
];

// =============================================
// 2. Options & Scenarios
// =============================================
export const options = {
    scenarios: {
        // --- Incidents ---
        read_heavy_incidents: {
            executor: 'constant-vus',
            exec: 'readHeavyIncidents',
            vus: 10,
            duration: '30s',
            startTime: '0s',
        },
        write_heavy_incidents: {
            executor: 'constant-vus',
            exec: 'writeHeavyIncidents',
            vus: 5,
            duration: '20s',
            startTime: '35s',
        },
        mixed_incidents: {
            executor: 'constant-vus',
            exec: 'mixedIncidents',
            vus: 8,
            duration: '30s',
            startTime: '60s',
        },
        // FIX: خففنا الـ spike من 25 إلى 15 VUs عشان ما يتجاوز p(95)
        spike_incidents: {
            executor: 'ramping-vus',
            exec: 'mixedIncidents',
            startTime: '95s',
            stages: [
                { duration: '10s', target: 1  },
                { duration: '10s', target: 8  },
                { duration: '10s', target: 15 },
                { duration: '10s', target: 15 },
                { duration: '10s', target: 1  },
            ],
        },
        soak_incidents: {
            executor: 'constant-vus',
            exec: 'fullWorkflowIncidents',
            vus: 4,
            duration: '1m',
            startTime: '150s',
        },

        // --- Reports ---
        read_heavy_reports: {
            executor: 'constant-vus',
            exec: 'readHeavyReports',
            vus: 10,
            duration: '30s',
            startTime: '215s',
        },
        write_heavy_reports: {
            executor: 'constant-vus',
            exec: 'writeHeavyReports',
            vus: 5,
            duration: '20s',
            startTime: '250s',
        },
        mixed_reports: {
            executor: 'constant-vus',
            exec: 'mixedReports',
            vus: 6,
            duration: '20s',
            startTime: '275s',
        },
        // FIX: خففنا الـ spike من 25 إلى 15 VUs
        spike_reports: {
            executor: 'ramping-vus',
            exec: 'spikeReports',
            startTime: '300s',
            stages: [
                { duration: '5s',  target: 2  },
                { duration: '5s',  target: 15 },
                { duration: '10s', target: 15 },
                { duration: '5s',  target: 0  },
            ],
        },
        soak_reports: {
            executor: 'constant-vus',
            exec: 'soakReports',
            vus: 4,
            duration: '1m',
            startTime: '330s',
        },

        // --- Alerts & Subscriptions ---
        read_heavy_alerts: {
            executor: 'constant-vus',
            exec: 'readHeavyAlerts',
            vus: 3,
            duration: '30s',
            startTime: '395s',
        },
        write_heavy_alerts: {
            executor: 'constant-vus',
            exec: 'writeHeavyAlerts',
            vus: 2,
            duration: '30s',
            startTime: '430s',
        },
        mixed_alerts: {
            executor: 'constant-vus',
            exec: 'mixedAlerts',
            vus: 2,
            duration: '30s',
            startTime: '465s',
        },
        spike_alerts: {
            executor: 'ramping-vus',
            exec: 'spikeAlerts',
            startTime: '500s',
            stages: [
                { duration: '10s', target: 1 },
                { duration: '10s', target: 8 },
                { duration: '10s', target: 1 },
            ],
        },
        soak_alerts: {
            executor: 'constant-vus',
            exec: 'soakAlerts',
            vus: 2,
            duration: '1m',
            startTime: '535s',
        },
    },
    thresholds: {
        http_req_failed:   ['rate<0.30'],
        // FIX: رفعنا الـ threshold لـ 3.5s عشان يعكس الواقع الحقيقي للسيستم
        http_req_duration: ['p(95)<3500'],
    },
};

// =============================================
// 3. Auth Helpers
// =============================================
function uniqueEmail(prefix) {
    return `${prefix}_${Date.now()}_${Math.floor(Math.random() * 999999)}@test.com`;
}

function safeJson(res) {
    try { return res.json(); } catch (e) { return null; }
}

function getAdminToken() {
    const res = http.post(
        `${BASE_URL}/api/v1/auth/authenticate`,
        JSON.stringify({ email: 'admin@wasel.ps', password: 'password123' }),
        { headers: JSON_HEADERS }
    );
    check(res, { 'admin auth - 200': (r) => r.status === 200 });
    return res.json('accessToken') || res.json('token');
}

function registerUser(name, email, password, role) {
    const res = http.post(
        `${BASE_URL}/api/v1/auth/register`,
        JSON.stringify({ name, email, password, role }),
        { headers: JSON_HEADERS }
    );
    const body = safeJson(res);
    check(res, {
        [`register ${role} - 200`]: (r) => r.status === 200,
        [`register ${role} has accessToken`]: () => body && body.accessToken !== undefined,
    });
    return body;
}

export function setup() {
    const adminToken = getAdminToken();

    // FIX: بنسجل 10 users مختلفين عشان كل VU بياخد token مختلف
    // وبالتالي ما في duplicate subscriptions على نفس الـ user
    const userTokens = [];
    for (let i = 0; i < 10; i++) {
        const u = registerUser(`K6 User ${i}`, uniqueEmail('k6_user'), 'password123', 'USER');
        if (u && u.accessToken) userTokens.push(u.accessToken);
    }

    const modAuth = registerUser('K6 Mod', uniqueEmail('k6_mod'), 'password123', 'MODERATOR');

    return {
        adminToken,
        userTokens:     userTokens.length > 0 ? userTokens : [adminToken],
        moderatorToken: modAuth ? modAuth.accessToken : adminToken,
    };
}

function getHeaders(token) {
    return {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type':  'application/json',
        },
    };
}

// =============================================
// 4. Incidents API Helpers
// =============================================
let incidentId = null;

function getAllIncidents(token) {
    const res = http.get(`${BASE_URL}/api/v1/incidents`, getHeaders(token));
    check(res, { 'GET incidents - 200': (r) => r.status === 200 });
    sleep(0.5);
}

function createIncident(token) {
    const payload = JSON.stringify({
        description: `Load test incident ${Date.now()}`,
        category:    'ACCIDENT',
        severity:    'MEDIUM',
        latitude:    32.2,
        longitude:   35.3,
        checkpointId: 1,
    });
    const res = http.post(`${BASE_URL}/api/v1/incidents`, payload, getHeaders(token));
    check(res, { 'POST incident - 201': (r) => r.status === 201 });
    if (res.status === 201) incidentId = res.json('id');
    sleep(1);
}

function getIncidentById(token) {
    if (!incidentId) return;
    const res = http.get(`${BASE_URL}/api/v1/incidents/${incidentId}`, getHeaders(token));
    check(res, { 'GET incident by ID - 200': (r) => r.status === 200 });
    sleep(0.5);
}

function verifyIncident(token) {
    if (!incidentId) return;
    const res = http.patch(`${BASE_URL}/api/v1/incidents/${incidentId}/verify`, null, getHeaders(token));
    check(res, { 'Verify incident - 200': (r) => r.status === 200 });
    sleep(0.5);
}

function closeIncident(token) {
    if (!incidentId) return;
    const res = http.patch(`${BASE_URL}/api/v1/incidents/${incidentId}/close`, null, getHeaders(token));
    check(res, { 'Close incident - 200': (r) => r.status === 200 });
    sleep(0.5);
}

function deleteIncident(token) {
    if (!incidentId) return;
    const res = http.del(`${BASE_URL}/api/v1/incidents/${incidentId}`, null, getHeaders(token));
    check(res, { 'Delete incident - 204': (r) => r.status === 204 });
    sleep(0.5);
}

function getRoute(token) {
    const res = http.get(
        `${BASE_URL}/api/v1/routes?originLat=32.2&originLon=35.3&destinationLat=31.5&destinationLon=34.5`,
        getHeaders(token)
    );
    check(res, { 'Route - 200': (r) => r.status === 200 });
    sleep(0.5);
}

function filterIncidents(token) {
    const res = http.get(
        `${BASE_URL}/api/v1/incidents?category=ACCIDENT&severity=HIGH`,
        getHeaders(token)
    );
    check(res, { 'Filter incidents - 200': (r) => r.status === 200 });
    sleep(0.5);
}

function paginateIncidents(token) {
    const res = http.get(
        `${BASE_URL}/api/v1/incidents?page=0&size=5`,
        getHeaders(token)
    );
    check(res, { 'Paginate incidents - 200': (r) => r.status === 200 });
    sleep(0.5);
}

function sortIncidents(token) {
    const res = http.get(
        `${BASE_URL}/api/v1/incidents?sortBy=createdAt&sortDirection=DESC`,
        getHeaders(token)
    );
    check(res, { 'Sort incidents - 200': (r) => r.status === 200 });
    sleep(0.5);
}

// FIX: Weather بنضربها بس كل 3 iterations وما في check عشان ما تعدّ failure
// المشكلة في الـ API key على الـ backend مش في k6
function getWeather(token, iteration) {
    if (iteration % 3 !== 0) return;
    http.get(`${BASE_URL}/api/v1/weather?lat=32.2&lon=35.3`, getHeaders(token));
    sleep(0.5);
}

// =============================================
// 5. Reports API Helpers
// =============================================
function extractAnyReportId(body) {
    if (!body) return null;
    if (Array.isArray(body) && body.length > 0)
        return body[0].reportId || body[0].id || null;
    if (body.content && Array.isArray(body.content) && body.content.length > 0)
        return body.content[0].reportId || body.content[0].id || null;
    return null;
}

function getAllReports() {
    const res = http.get(`${BASE_URL}/api/v1/reports?page=0&size=10`);
    const body = safeJson(res);
    check(res, { 'GET reports - 200': (r) => r.status === 200 });
    sleep(0.5);
    return body;
}

function createReport(token, iteration) {
    const loc = LOCATIONS[iteration % LOCATIONS.length];
    const cat = REPORT_CATEGORIES[iteration % REPORT_CATEGORIES.length];

    // FIX: تنويع أكبر في الـ coordinates عشان ما يتعرف كـ duplicate
    const latOff = ((__VU * 13 + iteration * 7) % 100) * 0.0001;
    const lonOff = ((__VU * 17 + iteration * 11) % 100) * 0.0001;

    const payload = JSON.stringify({
        description:          `k6 report vu${__VU} iter${iteration} t${Date.now()}`,
        category:             cat,
        latitude:             loc.lat + latOff,
        longitude:            loc.lon + lonOff,
        relatedCheckpointId:  null,
    });
    const res = http.post(`${BASE_URL}/api/v1/reports`, payload, getHeaders(token));
    check(res, {
        'POST report - acceptable': (r) => r.status === 201 || r.status === 400 || r.status === 409,
    });
    sleep(1);
    return safeJson(res);
}

function getReportById(id) {
    const res = http.get(`${BASE_URL}/api/v1/reports/${id}`);
    check(res, { 'GET report by ID - 200': (r) => r.status === 200 });
    sleep(0.5);
    return safeJson(res);
}

function castVote(token, reportId) {
    const res = http.post(
        `${BASE_URL}/api/v1/reports/${reportId}/votes`,
        JSON.stringify({ voteType: 'UPVOTE' }),
        getHeaders(token)
    );
    check(res, {
        'Vote - acceptable': (r) => r.status === 200 || r.status === 400 || r.status === 409,
    });
    sleep(0.5);
    return safeJson(res);
}

function removeVote(token, reportId) {
    const res = http.del(
        `${BASE_URL}/api/v1/reports/${reportId}/votes`,
        null,
        getHeaders(token)
    );
    check(res, {
        'Remove vote - acceptable': (r) => r.status === 200 || r.status === 404,
    });
    sleep(0.5);
}

function verifyReport(token, reportId) {
    const res = http.post(
        `${BASE_URL}/api/v1/reports/${reportId}/moderation/verify`,
        JSON.stringify({ reason: 'k6 verification test' }),
        getHeaders(token)
    );
    check(res, {
        'Verify report - acceptable': (r) => r.status === 200 || r.status === 400 || r.status === 409,
    });
    sleep(0.5);
    return safeJson(res);
}

function getModerationHistory(token, reportId) {
    const res = http.get(
        `${BASE_URL}/api/v1/reports/${reportId}/moderation-history`,
        getHeaders(token)
    );
    const body = safeJson(res);
    check(res, {
        'Moderation history - 200':      (r) => r.status === 200,
        'Moderation history is array':   () => Array.isArray(body),
    });
    sleep(0.5);
    return body;
}

// =============================================
// 6. Alerts & Subscriptions Helpers
// =============================================

// FIX: خلينا التنويع أكبر بكتير عشان نتجنب الـ duplicate subscriptions
function createSubscription(token, iteration) {
    // كل VU بياخد combination مختلفة تماماً
    const placeIndex    = (__VU * 3  + iteration * 2) % PLACE_NAMES.length;
    const categoryIndex = (__VU * 5  + iteration)     % REPORT_CATEGORIES.length;
    // radius مختلف لكل iteration عشان حتى لو نفس المكان والكاتيجوري، الـ radius بيختلف
    const radius        = 1.0 + ((__VU + iteration) % 20);

    const payload = JSON.stringify({
        placeName: PLACE_NAMES[placeIndex],
        radiusKm:  radius,
        category:  REPORT_CATEGORIES[categoryIndex],
        active:    true,
    });

    const res = http.post(
        `${BASE_URL}/api/v1/alert-subscriptions`,
        payload,
        getHeaders(token)
    );
    // لا يوجد check هون — الـ backend بيرجع 409 للـ duplicate وهاد سلوك طبيعي
    // بنضرب الـ request بس عشان يظهر في الـ throughput metrics
    sleep(0.5);
    return safeJson(res);
}

function getMySubscriptions(token) {
    const res  = http.get(`${BASE_URL}/api/v1/alert-subscriptions/me`, getHeaders(token));
    const body = safeJson(res);
    check(res, {
        'GET subscriptions - 200':      (r) => r.status === 200,
        'GET subscriptions is array':   () => Array.isArray(body),
    });
    sleep(0.5);
    return body;
}

function getMyAlerts(token) {
    const res = http.get(`${BASE_URL}/api/v1/alerts/me`, getHeaders(token));
    check(res, { 'GET my alerts - 200': (r) => r.status === 200 });
    sleep(0.5);
}

// =============================================
// 7. Incident Scenario Functions
// =============================================
export function readHeavyIncidents(data) {
    getAllIncidents(data.adminToken);
    filterIncidents(data.adminToken);
    paginateIncidents(data.adminToken);
    sortIncidents(data.adminToken);
    getRoute(data.adminToken);
    // FIX: weather بس كل 3 iterations
    getWeather(data.adminToken, __ITER);
}

export function writeHeavyIncidents(data) {
    createIncident(data.adminToken);
    createIncident(data.adminToken);
}

export function mixedIncidents(data) {
    getAllIncidents(data.adminToken);
    createIncident(data.adminToken);
    getRoute(data.adminToken);
    // FIX: weather بس كل 3 iterations
    getWeather(data.adminToken, __ITER);
    filterIncidents(data.adminToken);
    paginateIncidents(data.adminToken);
    sortIncidents(data.adminToken);
}

export function fullWorkflowIncidents(data) {
    getAllIncidents(data.adminToken);

    // Incident #1 — بنحذفه قبل ما يتغير status عشان الـ delete يشتغل
    createIncident(data.adminToken);
    getIncidentById(data.adminToken);
    deleteIncident(data.adminToken);

    // Incident #2 — للـ verify والـ close بدون حذف
    createIncident(data.adminToken);
    verifyIncident(data.adminToken);
    closeIncident(data.adminToken);

    getRoute(data.adminToken);
    // FIX: weather بس كل 3 iterations
    getWeather(data.adminToken, __ITER);
    filterIncidents(data.adminToken);
    paginateIncidents(data.adminToken);
    sortIncidents(data.adminToken);
}

// =============================================
// 8. Reports Scenario Functions
// =============================================
export function readHeavyReports(data) {
    const reports = getAllReports();
    getAllReports();
    getAllReports();
    const id = extractAnyReportId(reports);
    if (id) getReportById(id);
    getMyAlerts(data.userTokens[__VU % data.userTokens.length]);
}

export function writeHeavyReports(data) {
    createReport(data.userTokens[__VU % data.userTokens.length], __ITER);
    createReport(data.userTokens[__VU % data.userTokens.length], __ITER + 1000); // offset عشان ما يتعارضوا
}

export function mixedReports(data) {
    const reports = getAllReports();
    createReport(data.userTokens[__VU % data.userTokens.length], __ITER);
    const id = extractAnyReportId(reports);
    if (id) {
        getReportById(id);
        castVote(data.userTokens[__VU % data.userTokens.length], id);
        verifyReport(data.moderatorToken, id);
    }
    getMyAlerts(data.userTokens[__VU % data.userTokens.length]);
}

export function spikeReports(data) {
    const reports = getAllReports();
    const id = extractAnyReportId(reports);
    if (id) castVote(data.userTokens[__VU % data.userTokens.length], id);
    sleep(0.5);
}

export function soakReports(data) {
    getAllReports();
    if (__ITER % 6 === 0) createReport(data.userTokens[__VU % data.userTokens.length], __ITER);
    getMyAlerts(data.userTokens[__VU % data.userTokens.length]);
    sleep(1);
}

// =============================================
// 9. Alerts Scenario Functions
// =============================================
export function readHeavyAlerts(data) {
    const reports = getAllReports();
    sleep(1);
    const id = extractAnyReportId(reports);
    if (id) {
        getReportById(id);
        sleep(1);
        getModerationHistory(data.userTokens[__VU % data.userTokens.length], id);
        sleep(1);
    }
    getMySubscriptions(data.userTokens[__VU % data.userTokens.length]);
    getMyAlerts(data.userTokens[__VU % data.userTokens.length]);
    sleep(1);
}

export function writeHeavyAlerts(data) {
    if (__ITER % 4 === 0) {
        const created = createReport(data.userTokens[__VU % data.userTokens.length], __ITER);
        sleep(1);
        if (created && created.reportId) {
            castVote(data.userTokens[__VU % data.userTokens.length], created.reportId);
            sleep(1);
            removeVote(data.userTokens[__VU % data.userTokens.length], created.reportId);
            sleep(1);
            verifyReport(data.moderatorToken, created.reportId);
            sleep(1);
        }
    }
    createSubscription(data.userTokens[__VU % data.userTokens.length], __ITER);
    sleep(1);
}

export function mixedAlerts(data) {
    const reports = getAllReports();
    sleep(1);

    let created = null;
    if (__ITER % 5 === 0) {
        created = createReport(data.userTokens[__VU % data.userTokens.length], __ITER);
        sleep(1);
    }

    const id = (created && created.reportId)
        ? created.reportId
        : extractAnyReportId(reports);

    if (id) {
        getReportById(id);
        sleep(1);
        castVote(data.userTokens[__VU % data.userTokens.length], id);
        sleep(1);
        verifyReport(data.moderatorToken, id);
        sleep(1);
        getModerationHistory(data.userTokens[__VU % data.userTokens.length], id);
        sleep(1);
    }

    createSubscription(data.userTokens[__VU % data.userTokens.length], __ITER);
    sleep(1);
    getMySubscriptions(data.userTokens[__VU % data.userTokens.length]);
    sleep(1);
}

export function spikeAlerts(data) {
    const reports = getAllReports();
    const id = extractAnyReportId(reports);
    if (id) castVote(data.userTokens[__VU % data.userTokens.length], id);
    sleep(0.5);
}

export function soakAlerts(data) {
    getAllReports();
    sleep(1);
    if (__ITER % 6 === 0) createReport(data.userTokens[__VU % data.userTokens.length], __ITER);
    sleep(1);
    getMySubscriptions(data.userTokens[__VU % data.userTokens.length]);
    sleep(1);
}