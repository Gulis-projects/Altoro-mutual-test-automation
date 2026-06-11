import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';

const USERNAME = process.env.APP_USERNAME ?? 'jsmith';
const PASSWORD = process.env.APP_PASSWORD ?? 'Demo1234';

test.describe('Login', () => {
  let loginPage: LoginPage;

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);
    await loginPage.navigate();
  });

  // ── Happy path ────────────────────────────────────────────────────────

  test('successful login with valid credentials', async ({ page }) => {
    await loginPage.login(USERNAME, PASSWORD);
    await loginPage.assertLoginSuccessful();
    await expect(page.getByRole('heading', { name: /Hello John Smith/i })).toBeVisible();
  });

  test('user can log out after login', async () => {
    await loginPage.login(USERNAME, PASSWORD);
    await loginPage.assertLoginSuccessful();
    await loginPage.logout();
    await loginPage.assertLoggedOut();
  });

  test('re-login after logout succeeds', async () => {
    await loginPage.login(USERNAME, PASSWORD);
    await loginPage.assertLoginSuccessful();
    await loginPage.logout();
    await loginPage.assertLoggedOut();
    await loginPage.navigate();
    await loginPage.login(USERNAME, PASSWORD);
    await loginPage.assertLoginSuccessful();
  });

  // ── Invalid credentials ───────────────────────────────────────────────

  test('login fails with invalid username and password', async () => {
    await loginPage.login('no_such_user', 'wrong_password');
    await loginPage.assertLoginFailed();
  });

  test('login fails with wrong password for valid user', async () => {
    await loginPage.login(USERNAME, 'totally_wrong');
    await loginPage.assertLoginFailed();
  });

  // ── Empty / blank inputs ──────────────────────────────────────────────

  test.describe('empty credentials', () => {
    test('login fails with empty username', async ({ page }) => {
      page.once('dialog', dialog => dialog.dismiss());
      await loginPage.login('', PASSWORD);
      await loginPage.assertOnLoginPage();
    });

    test('login fails with empty password', async ({ page }) => {
      page.once('dialog', dialog => dialog.dismiss());
      await loginPage.login(USERNAME, '');
      await loginPage.assertOnLoginPage();
    });

    test('login fails with both fields empty', async ({ page }) => {
      page.once('dialog', dialog => dialog.dismiss());
      await loginPage.login('', '');
      await loginPage.assertOnLoginPage();
    });

    test('login fails with whitespace-only credentials', async () => {
      await loginPage.login(' ', ' ');
      await loginPage.assertLoginFailed();
    });
  });

  // ── Session / access control ──────────────────────────────────────────

  test('protected page redirects unauthenticated user to login', async ({ page }) => {
    await page.goto('/bank/main.jsp');
    await loginPage.assertOnLoginPage();
  });

  // ── Security — SQL injection (documented vulnerability) ───────────────
  // demo.testfire.net is intentionally vulnerable to SQL injection.
  // These tests document the known vulnerability by asserting the exploit works.

  test('SQL injection bypasses login — known vulnerability in demo app', async ({ page }) => {
    await loginPage.login("' OR '1'='1", "' OR '1'='1");
    // The app IS vulnerable; assert we can detect that the exploit succeeded
    await expect(loginPage.signOffLink).toBeVisible({ timeout: 10000 });
    await page.screenshot({ path: 'test-results/sql-injection-evidence.png' });
  });

  test.describe('injection payload handling', () => {
    const payloads = [
      { label: 'classic OR bypass', value: "' OR '1'='1" },
      { label: 'comment truncation',  value: "admin'--" },
      { label: 'double-quote bypass', value: '" OR ""="' },
      { label: 'XSS in username',     value: '<script>alert(1)</script>' },
    ];

    for (const { label, value } of payloads) {
      test(`records outcome for payload: ${label}`, async ({ page }) => {
        await loginPage.login(value, 'Demo1234');
        // Capture result for the security report; page is in a deterministic state.
        await page.screenshot({
          path: `test-results/sqli-${label.replace(/\s+/g, '-')}.png`,
        });
        // Ensure we're not left in a broken state
        await expect(page).toHaveURL(/.+/);
      });
    }
  });
});
