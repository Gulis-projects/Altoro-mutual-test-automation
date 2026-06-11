import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';
import { AccountPage } from '../pages/AccountPage';

const USERNAME = process.env.APP_USERNAME ?? 'jsmith';
const PASSWORD = process.env.APP_PASSWORD ?? 'Demo1234';

// ── Access control (no login required) ────────────────────────────────────
test('account summary page redirects unauthenticated user to login', async ({ page }) => {
  const loginPage = new LoginPage(page);
  await page.goto('https://demo.testfire.net/bank/main.jsp');
  await loginPage.assertOnLoginPage();
});

// ── Authenticated account tests ────────────────────────────────────────────
test.describe('Account Summary', () => {
  let loginPage: LoginPage;
  let accountPage: AccountPage;

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);
    accountPage = new AccountPage(page);

    await loginPage.navigate();
    await loginPage.login(USERNAME, PASSWORD);
    await loginPage.assertLoginSuccessful();
  });

  test('account summary page displays after login', async () => {
    await accountPage.assertOnAccountSummaryPage();
  });

  test('welcome message contains the user full name', async () => {
    await accountPage.assertWelcomeMessageContains('John Smith');
  });

  test('at least one account is listed', async () => {
    await accountPage.assertAccountsAreDisplayed();
  });

  test('Transfer Funds navigation link is visible', async () => {
    await accountPage.assertTransferLinkVisible();
  });

  test('clicking Transfer Funds navigates to transfer page', async ({ page }) => {
    await accountPage.goToTransferFunds();
    await expect(page).toHaveURL(/transfer\.jsp/);
  });
});
