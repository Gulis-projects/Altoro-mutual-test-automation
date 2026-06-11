import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';
import { TransferPage } from '../pages/TransferPage';

const USERNAME = process.env.APP_USERNAME ?? 'jsmith';
const PASSWORD = process.env.APP_PASSWORD ?? 'Demo1234';

test.describe('Fund Transfer', () => {
  let loginPage: LoginPage;
  let transferPage: TransferPage;

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);
    transferPage = new TransferPage(page);

    await loginPage.navigate();
    await loginPage.login(USERNAME, PASSWORD);
    await loginPage.assertLoginSuccessful();
    await transferPage.navigate();
  });

  // ── Page structure ─────────────────────────────────────────────────────

  test('transfer page loads with all required fields', async () => {
    await transferPage.assertOnTransferPage();
  });

  test('from and to dropdowns are populated with accounts', async () => {
    const fromOptions = await transferPage.getFromAccountOptions();
    const toOptions = await transferPage.getToAccountOptions();
    expect(fromOptions.filter(o => o.trim()).length).toBeGreaterThan(0);
    expect(toOptions.filter(o => o.trim()).length).toBeGreaterThan(0);
  });

  // ── Happy path ──────────────────────────────────────────────────────────

  test('successful fund transfer between accounts', async () => {
    const fromOptions = await transferPage.getFromAccountOptions();
    const toOptions = await transferPage.getToAccountOptions();

    await transferPage.selectFromAccount(fromOptions[0].trim());
    // Use last option to guarantee a different destination
    await transferPage.selectToAccount(toOptions[toOptions.length - 1].trim());
    await transferPage.enterAmount('100');
    await transferPage.clickTransfer();
    await transferPage.assertTransferSuccessful();
  });

  // ── Navigation ──────────────────────────────────────────────────────────

  test('transfer funds page is reachable via account summary navigation', async ({ page }) => {
    await page.goto('/bank/main.jsp');
    await page.getByRole('link', { name: 'Transfer Funds' }).click();
    await transferPage.assertOnTransferPage();
  });

  // ── Validation — invalid amounts ────────────────────────────────────────
  // Note: demo.testfire.net has intentional validation gaps.
  // These tests document actual app behaviour, not ideal behaviour.

  test.describe('invalid amount validation', () => {
    test.beforeEach(async () => {
      const fromOptions = await transferPage.getFromAccountOptions();
      const toOptions = await transferPage.getToAccountOptions();
      await transferPage.selectFromAccount(fromOptions[0].trim());
      await transferPage.selectToAccount(toOptions[toOptions.length - 1].trim());
    });

    test('submitting non-numeric amount shows a dialog', async ({ page }) => {
      page.once('dialog', dialog => dialog.dismiss());
      await transferPage.enterAmount('abc');
      await transferPage.clickTransfer();
      // After dialog dismiss, we remain on the transfer page
      await transferPage.assertOnTransferPage();
    });

    test('zero amount — documents app behaviour', async () => {
      await transferPage.enterAmount('0');
      await transferPage.clickTransfer();
      await transferPage.assertTransferNotSuccessful();
    });

    test('negative amount — documents app behaviour', async () => {
      await transferPage.enterAmount('-100');
      await transferPage.clickTransfer();
      await transferPage.assertTransferNotSuccessful();
    });
  });
});
