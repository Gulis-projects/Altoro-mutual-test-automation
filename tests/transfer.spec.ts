import { test, expect } from '@playwright/test';

test.describe('Fund Transfer', () => {
  test('should transfer funds between accounts', async ({ page }) => {
    // Navigate to the transfer page
    await page.goto('/transfer');

    // Fill in transfer details
    await page.fill('input[name="fromAccount"]', 'Account 001');
    await page.fill('input[name="toAccount"]', 'Account 002');
    await page.fill('input[name="amount"]', '1000');
    await page.fill('input[name="description"]', 'Monthly transfer');

    // Submit the transfer
    await page.click('button[type="submit"]');

    // Verify success message
    await expect(page.locator('.success-message')).toBeVisible();
    await expect(page.locator('.success-message')).toContainText('Transfer completed successfully');
  });

  test('should validate insufficient funds', async ({ page }) => {
    // Navigate to the transfer page
    await page.goto('/transfer');

    // Attempt to transfer more than available
    await page.fill('input[name="fromAccount"]', 'Account 001');
    await page.fill('input[name="toAccount"]', 'Account 002');
    await page.fill('input[name="amount"]', '999999');

    // Submit the transfer
    await page.click('button[type="submit"]');

    // Verify error message
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('Insufficient funds');
  });

  test('should require valid account numbers', async ({ page }) => {
    // Navigate to the transfer page
    await page.goto('/transfer');

    // Submit without filling required fields
    await page.click('button[type="submit"]');

    // Verify validation error
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('Account number is required');
  });
});
