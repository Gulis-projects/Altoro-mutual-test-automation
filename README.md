# Altoro Mutual - Playwright Test Suite

E2E test suite for [Altoro Mutual](https://demo.testfire.net) 
— a deliberately vulnerable banking application used for security testing practice.

## Tech Stack
- Playwright 1.49
- TypeScript
- Node.js

## Test Coverage
- ✅ Successful login with valid credentials
- ✅ Login rejection with invalid credentials
- ✅ Edge cases — empty username, empty password
- ❌ SQL Injection bypass (confirmed bug — see Issues)

## Known Bugs
- [SQL Injection bypasses authentication](link-to-your-github-issue)

## How to run
```bash
npm install
npx playwright install
npx playwright test
npx playwright test --ui
```

## Evidence
SQL injection vulnerability confirmed — attacker can login with:
- Username: `' OR '1'='1`
- Password: `' OR '1'='1`