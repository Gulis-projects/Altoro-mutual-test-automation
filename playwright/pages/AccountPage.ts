import { Page, Locator, expect } from '@playwright/test';

export class AccountPage {
  readonly page: Page;
  readonly welcomeHeading: Locator;
  readonly accountSelect: Locator;
  readonly accountDetailsTable: Locator;
  readonly transferFundsLink: Locator;
  readonly signOffLink: Locator;

  constructor(page: Page) {
    this.page = page;
    this.welcomeHeading = page.locator('h1').first();
    // #listAccounts is a <select> populated with all accounts; visible after login
    this.accountSelect = page.locator('#listAccounts');
    // The "View Account Details" table holds clickable account links
    this.accountDetailsTable = page.locator('table').filter({ hasText: 'View Account Details' });
    this.transferFundsLink = page.getByRole('link', { name: 'Transfer Funds' });
    this.signOffLink = page.getByRole('link', { name: 'Sign Off' });
  }

  async navigate(): Promise<void> {
    await this.page.goto('/bank/main.jsp');
  }

  async goToTransferFunds(): Promise<void> {
    await this.transferFundsLink.click();
  }

  async getWelcomeMessage(): Promise<string> {
    return (await this.welcomeHeading.textContent() ?? '').trim();
  }

  async getAccountLinks(): Promise<string[]> {
    return this.accountDetailsTable.getByRole('link').allTextContents();
  }

  async assertOnAccountSummaryPage(): Promise<void> {
    await expect(this.page).toHaveURL(/main\.jsp/);
    await expect(this.accountSelect).toBeVisible();
  }

  async assertWelcomeMessageContains(text: string): Promise<void> {
    await expect(this.welcomeHeading).toContainText(text);
  }

  async assertAccountsAreDisplayed(): Promise<void> {
    const links = await this.getAccountLinks();
    expect(links.filter(l => l.trim()).length).toBeGreaterThan(0);
  }

  async assertTransferLinkVisible(): Promise<void> {
    await expect(this.transferFundsLink).toBeVisible();
  }
}
