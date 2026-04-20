import http from 'k6/http';
import { check, sleep } from 'k6';

// =========================
// 1) Base config
// =========================
const BASE_URL = 'http://localhost:8081';
const JSON_HEADERS = { 'Content-Type': 'application/json' };

const LOCATIONS = [
  { lat: 32.2211, lon: 35.2544 }, // Nablus
  { lat: 31.7683, lon: 35.2137 }, // Jerusalem
  { lat: 31.9038, lon: 35.2034 }, // Ramallah
  { lat: 31.5326, lon: 35.0998 }, // Hebron
  { lat: 31.5017, lon: 34.4668 }, // Gaza
];

const REPORT_CATEGORIES = ['ACCIDENT', 'DELAY', 'CLOSURE', 'WEATHER_HAZARD'];

const PLACE_NAMES = [
  'Nablus',
  'Ramallah',
  'Hebron',
  'Jerusalem',
  'Bethlehem',
  'Tulkarm',
  'Jenin',
  'Qalqilya',
  'Salfit',
  'Jericho',
];

// =========================
// 2) Official scenarios
// =========================
export const options = {
  scenarios: {
    read_heavy: {
      executor: 'constant-vus',
      exec: 'readHeavyScenario',
      vus: 3,
      duration: '30s',
    },
    write_heavy: {
      executor: 'constant-vus',
      exec: 'writeHeavyScenario',
      vus: 2,
      duration: '30s',
      startTime: '35s',
    },
    mixed_workload: {
      executor: 'constant-vus',
      exec: 'mixedScenario',
      vus: 2,
      duration: '30s',
      startTime: '70s',
    },
    spike_test: {
      executor: 'ramping-vus',
      exec: 'spikeScenario',
      startTime: '105s',
      stages: [
        { duration: '10s', target: 1 },
        { duration: '10s', target: 8 },
        { duration: '10s', target: 1 },
      ],
    },
    soak_test: {
      executor: 'constant-vus',
      exec: 'soakScenario',
      vus: 2,
      duration: '1m',
      startTime: '140s',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.30'],
    http_req_duration: ['p(95)<2000'],
  },
};

// =========================
// 3) Helpers
// =========================
function authHeaders(token) {
  return {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  };
}

function safeJson(res) {
  try {
    return res.json();
  } catch (e) {
    return null;
  }
}

function uniqueEmail(prefix) {
  return `${prefix}_${Date.now()}_${Math.floor(Math.random() * 100000)}@test.com`;
}

// =========================
// 4) Auth setup
// =========================
function registerUser(name, email, password, role) {
  const payload = JSON.stringify({
    name,
    email,
    password,
    role,
  });

  const res = http.post(`${BASE_URL}/api/v1/auth/register`, payload, {
    headers: JSON_HEADERS,
  });

  const body = safeJson(res);

  check(res, {
    [`register ${role} status is 200`]: (r) => r.status === 200,
    [`register ${role} has accessToken`]: () =>
      body && body.accessToken !== undefined,
  });

  return body;
}

export function setup() {
  const password = 'password123';

  const userAuth = registerUser(
    'K6 User',
    uniqueEmail('k6_user'),
    password,
    'USER'
  );

  const moderatorAuth = registerUser(
    'K6 Moderator',
    uniqueEmail('k6_mod'),
    password,
    'MODERATOR'
  );

  return {
    userToken: userAuth ? userAuth.accessToken : null,
    moderatorToken: moderatorAuth ? moderatorAuth.accessToken : null,
  };
}

// =========================
// 5) Endpoint functions
// =========================
function getAllReports() {
  const res = http.get(`${BASE_URL}/api/v1/reports?page=0&size=10`);
  const body = safeJson(res);

  check(res, {
    'get all reports status is 200': (r) => r.status === 200,
    'get all reports valid json': () => body !== null,
  });

  return body;
}

function extractAnyReportId(reportListBody) {
  if (!reportListBody) return null;

  if (Array.isArray(reportListBody) && reportListBody.length > 0) {
    return reportListBody[0].reportId || reportListBody[0].id || null;
  }

  if (
    reportListBody.content &&
    Array.isArray(reportListBody.content) &&
    reportListBody.content.length > 0
  ) {
    return (
      reportListBody.content[0].reportId ||
      reportListBody.content[0].id ||
      null
    );
  }

  return null;
}

function createReport(userToken, iteration) {
  const location = LOCATIONS[iteration % LOCATIONS.length];
  const category = REPORT_CATEGORIES[iteration % REPORT_CATEGORIES.length];

  const latOffset = ((__VU + iteration) % 10) * 0.001;
  const lonOffset = ((__VU + iteration) % 10) * 0.001;

  const payload = JSON.stringify({
    description: `k6 report vu ${__VU} iter ${iteration} time ${Date.now()}`,
    category: category,
    latitude: location.lat + latOffset,
    longitude: location.lon + lonOffset,
    relatedCheckpointId: null,
  });

  const res = http.post(
    `${BASE_URL}/api/v1/reports`,
    payload,
    authHeaders(userToken)
  );

  check(res, {
    'create report status acceptable': (r) =>
      r.status === 201 || r.status === 400 || r.status === 409,
  });

  return safeJson(res);
}

function getReportById(reportId) {
  const res = http.get(`${BASE_URL}/api/v1/reports/${reportId}`);
  const body = safeJson(res);

  check(res, {
    'get report by id status is 200': (r) => r.status === 200,
  });

  return body;
}

function castVote(userToken, reportId) {
  const payload = JSON.stringify({
    voteType: 'UPVOTE',
  });

  const res = http.post(
    `${BASE_URL}/api/v1/reports/${reportId}/votes`,
    payload,
    authHeaders(userToken)
  );

  const body = safeJson(res);

  check(res, {
    'vote status acceptable': (r) =>
      r.status === 200 || r.status === 400 || r.status === 409,
    'vote body when success': () =>
      res.status !== 200 || (body && body.score !== undefined),
  });

  return body;
}

function removeVote(userToken, reportId) {
  const res = http.del(
    `${BASE_URL}/api/v1/reports/${reportId}/votes`,
    null,
    authHeaders(userToken)
  );

  check(res, {
    'remove vote status acceptable': (r) => r.status === 200 || r.status === 404,
  });

  return res;
}

function verifyReport(modToken, reportId) {
  const payload = JSON.stringify({
    reason: 'k6 verification test',
  });

  const res = http.post(
    `${BASE_URL}/api/v1/reports/${reportId}/moderation/verify`,
    payload,
    authHeaders(modToken)
  );

  check(res, {
    'verify report status acceptable': (r) =>
      r.status === 200 || r.status === 400 || r.status === 409,
  });

  return safeJson(res);
}

function getModerationHistory(userToken, reportId) {
  const res = http.get(
    `${BASE_URL}/api/v1/reports/${reportId}/moderation-history`,
    authHeaders(userToken)
  );

  const body = safeJson(res);

  check(res, {
    'moderation history status is 200': (r) => r.status === 200,
    'moderation history array': () => Array.isArray(body),
  });

  return body;
}

function createSubscription(userToken, iteration) {
  const payload = JSON.stringify({
    placeName: PLACE_NAMES[(iteration + __VU) % PLACE_NAMES.length],
    radiusKm: 5.0,
    category: REPORT_CATEGORIES[iteration % REPORT_CATEGORIES.length],
    active: true,
  });

  const params = authHeaders(userToken);

  const res = http.post(
    `${BASE_URL}/api/v1/alert-subscriptions`,
    payload,
    params
  );

  check(res, {
    'create subscription status acceptable': (r) =>
      r.status === 201 || r.status === 400 || r.status === 409,
  });

  return safeJson(res);
}

function getMySubscriptions(userToken) {
  const res = http.get(
    `${BASE_URL}/api/v1/alert-subscriptions/me`,
    authHeaders(userToken)
  );

  const body = safeJson(res);

  check(res, {
    'get my subscriptions status is 200': (r) => r.status === 200,
    'get my subscriptions array': () => Array.isArray(body),
  });

  return body;
}

// =========================
// 6) Scenario functions
// =========================
export function readHeavyScenario(data) {
  const reports = getAllReports();
  sleep(1);

  const reportId = extractAnyReportId(reports);
  if (reportId) {
    getReportById(reportId);
    sleep(1);

    getModerationHistory(data.userToken, reportId);
    sleep(1);
  }

  getMySubscriptions(data.userToken);
  sleep(1);
}

export function writeHeavyScenario(data) {
  const iteration = __ITER;

  let created = null;

  // not every loop creates a report, to reduce anti-spam collisions
  if (iteration % 4 === 0) {
    created = createReport(data.userToken, iteration);
    sleep(1);
  }

  if (created && created.reportId) {
    castVote(data.userToken, created.reportId);
    sleep(1);

    removeVote(data.userToken, created.reportId);
    sleep(1);

    verifyReport(data.moderatorToken, created.reportId);
    sleep(1);
  }

  createSubscription(data.userToken, iteration);
  sleep(1);
}

export function mixedScenario(data) {
  const iteration = __ITER;

  const reports = getAllReports();
  sleep(1);

  let created = null;
  if (iteration % 5 === 0) {
    created = createReport(data.userToken, iteration);
    sleep(1);
  }

  const reportId =
    created && created.reportId
      ? created.reportId
      : extractAnyReportId(reports);

  if (reportId) {
    getReportById(reportId);
    sleep(1);

    castVote(data.userToken, reportId);
    sleep(1);

    verifyReport(data.moderatorToken, reportId);
    sleep(1);

    getModerationHistory(data.userToken, reportId);
    sleep(1);
  }

  createSubscription(data.userToken, iteration);
  sleep(1);

  getMySubscriptions(data.userToken);
  sleep(1);
}

export function spikeScenario(data) {
  const reports = getAllReports();
  const reportId = extractAnyReportId(reports);

  if (reportId) {
    castVote(data.userToken, reportId);
  }

  sleep(0.5);
}

export function soakScenario(data) {
  const iteration = __ITER;

  getAllReports();
  sleep(1);

  if (iteration % 6 === 0) {
    createReport(data.userToken, iteration);
    sleep(1);
  }

  getMySubscriptions(data.userToken);
  sleep(1);
}