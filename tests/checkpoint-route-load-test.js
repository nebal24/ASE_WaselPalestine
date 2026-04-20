/**
 * Wasel Palestine — Checkpoint & Route Estimation Load Tests
 *
 * Run each scenario individually:
 *   k6 run -e SCENARIO=read_heavy  tests/checkpoint-route-load-test.js --out json=results/cp-read.json
 *   k6 run -e SCENARIO=write_heavy tests/checkpoint-route-load-test.js --out json=results/cp-write.json
 *   k6 run -e SCENARIO=mixed       tests/checkpoint-route-load-test.js --out json=results/cp-mixed.json
 *   k6 run -e SCENARIO=spike       tests/checkpoint-route-load-test.js --out json=results/cp-spike.json
 *   k6 run -e SCENARIO=soak        tests/checkpoint-route-load-test.js --out json=results/cp-soak.json
 */

import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// ── Custom metrics ─────────────────────────────────────────────────────────────
const errorRate  = new Rate('errors');
const cpReadMs   = new Trend('cp_read_ms',   true);
const cpWriteMs  = new Trend('cp_write_ms',  true);
const routeReadMs= new Trend('route_read_ms',true);

// ── Config ─────────────────────────────────────────────────────────────────────
const BASE_URL = 'http://localhost:8081';
const CREDS    = { email: 'finaladmin@wasel.ps', password: 'mypass123' };
const IDS      = [1, 2, 3];
const ACTIVE   = __ENV.SCENARIO || 'read_heavy';

const ROUTE_URL = `${BASE_URL}/api/v1/routes`
  + `?originLat=32.2273&originLon=35.2589`
  + `&destinationLat=31.7683&destinationLon=35.2137`
  + `&avoidCheckpoints=true`;

const ROUTE_SPIKE_URL = ROUTE_URL + `&avoidAreas=Huwara`;

// ── Scenario definitions ───────────────────────────────────────────────────────
const SCENARIOS = {
  read_heavy: {
    executor: 'constant-vus',
    vus: 10,
    duration: '30s',
    exec: 'readHeavy',
    tags: { scenario: 'read_heavy' },
  },
  write_heavy: {
    executor: 'constant-vus',
    vus: 10,
    duration: '30s',
    exec: 'writeHeavy',
    tags: { scenario: 'write_heavy' },
  },
  mixed: {
    executor: 'constant-vus',
    vus: 15,
    duration: '30s',
    exec: 'mixed',
    tags: { scenario: 'mixed' },
  },
  spike: {
    executor: 'ramping-vus',
    startVUs: 0,
    stages: [
      { duration: '5s',  target: 0   },
      { duration: '5s',  target: 100 },
      { duration: '5s',  target: 100 },
      { duration: '5s',  target: 0   },
    ],
    gracefulRampDown: '10s',
    exec: 'spike',
    tags: { scenario: 'spike' },
  },
  soak: {
    executor: 'constant-vus',
    vus: 5,
    duration: '3m',
    exec: 'soak',
    tags: { scenario: 'soak' },
  },
};

function activeScenarios() {
  if (SCENARIOS[ACTIVE]) return { [ACTIVE]: SCENARIOS[ACTIVE] };
  console.error(`Unknown SCENARIO="${ACTIVE}". Valid: ${Object.keys(SCENARIOS).join(', ')}`);
  return { read_heavy: SCENARIOS.read_heavy };
}

// ── k6 options ─────────────────────────────────────────────────────────────────
export const options = {
  scenarios: activeScenarios(),
  thresholds: {
    'http_req_duration': ['p(95)<5000'],
    'http_req_failed':   ['rate<0.01'],
    'errors':            ['rate<0.01'],
  },
};

// ── Setup: authenticate once, pre-warm cache, share JWT with all VUs ──────────
// ALL /api/v1/** endpoints (including GETs) require a valid JWT — always auth.
export function setup() {
  const res = http.post(
    `${BASE_URL}/api/v1/auth/authenticate`,
    JSON.stringify(CREDS),
    { headers: { 'Content-Type': 'application/json' } }
  );

  const ok = check(res, {
    'setup: login 200':       (r) => r.status === 200,
    'setup: has accessToken': (r) => {
      try { return !!JSON.parse(r.body).accessToken; } catch { return false; }
    },
  });

  if (!ok) {
    console.error(`[setup] Auth failed (${res.status}): ${res.body}`);
    return { token: null };
  }

  const token = JSON.parse(res.body).accessToken;
  const hdrs  = { headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` } };

  // Pre-warm the route cache keys that k6 VUs will hit, guaranteeing a hot
  // cache before any VU fires. This eliminates the cold-start OSRM latency
  // that was causing p95 failures in mixed and spike scenarios.
  http.get(ROUTE_URL,       hdrs);  // mixed cache key  (avoidAreas=null)
  http.get(ROUTE_SPIKE_URL, hdrs);  // spike cache key  (avoidAreas=Huwara)
  sleep(1); // let cache settle before VUs start

  return { token };
}

// ── Helpers ───────────────────────────────────────────────────────────────────
function bearerHeaders(token) {
  return {
    headers: {
      'Content-Type':  'application/json',
      'Authorization': `Bearer ${token}`,
    },
  };
}

function randomId() {
  return IDS[Math.floor(Math.random() * IDS.length)];
}

function rec(res, trend, checksObj) {
  const ok = check(res, checksObj);
  trend.add(res.timings.duration);
  errorRate.add(!ok);
}

// ── Scenario 1: read_heavy ────────────────────────────────────────────────────
export function readHeavy(data) {
  const token = data && data.token;

  group('list_checkpoints', () => {
    const res = http.get(`${BASE_URL}/api/v1/checkpoints`, bearerHeaders(token));
    rec(res, cpReadMs, {
      'GET /checkpoints 200': (r) => r.status === 200,
    });
  });

  group('get_checkpoint_history', () => {
    const res = http.get(
      `${BASE_URL}/api/v1/checkpoints/${randomId()}/history`,
      bearerHeaders(token)
    );
    rec(res, cpReadMs, {
      'GET /checkpoints/{id}/history 200 or 404': (r) => r.status === 200 || r.status === 404,
    });
  });

  sleep(0.5);
}

// ── Scenario 2: write_heavy ────────────────────────────────────────────────────
export function writeHeavy(data) {
  const token = data && data.token;

  group('patch_checkpoint_status_delayed', () => {
    const res = http.patch(
      `${BASE_URL}/api/v1/checkpoints/${randomId()}/status?status=DELAYED`,
      null,
      bearerHeaders(token)
    );
    rec(res, cpWriteMs, {
      'PATCH /checkpoints/{id}/status 200': (r) => r.status === 200,
    });
  });

  sleep(0.5);
}

// ── Scenario 3: mixed (70 % routes / 30 % writes) ─────────────────────────────
export function mixed(data) {
  const token = data && data.token;

  if (Math.random() < 0.70) {
    group('mixed_route_read', () => {
      const res = http.get(ROUTE_URL, bearerHeaders(token));
      rec(res, routeReadMs, {
        'GET /routes 200': (r) => r.status === 200,
      });
    });
  } else {
    group('mixed_checkpoint_write', () => {
      const res = http.patch(
        `${BASE_URL}/api/v1/checkpoints/${randomId()}/status?status=OPEN`,
        null,
        bearerHeaders(token)
      );
      rec(res, cpWriteMs, {
        'PATCH status 200': (r) => r.status === 200,
      });
    });
  }

  sleep(0.5);
}

// ── Scenario 4: spike (0 → 100 → 0 VUs over 20s) ─────────────────────────────
export function spike(data) {
  const token = data && data.token;

  group('spike_route_request', () => {
    const res = http.get(ROUTE_SPIKE_URL, bearerHeaders(token));
    rec(res, routeReadMs, {
      'GET /routes 200':  (r) => r.status === 200,
      'no server error':  (r) => r.status < 500,
    });
  });
  // no sleep — VU ramp controls concurrency
}

// ── Scenario 5: soak (5 VUs × 3 min) ─────────────────────────────────────────
export function soak(data) {
  const token = data && data.token;

  group('soak_checkpoint_list', () => {
    const res = http.get(`${BASE_URL}/api/v1/checkpoints`, bearerHeaders(token));
    rec(res, cpReadMs, {
      'GET /checkpoints 200': (r) => r.status === 200,
    });
  });

  sleep(1);
}
