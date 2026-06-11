import { Page, Locator, expect } from '@playwright/test';

export class LoginPage {
  readonly page: Page;
  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly loginButton: Locator;
  readonly signOffLink: Locator;
  readonly errorMessage: Locator;

  constructor(page: Page) {
    this.page = page;
    this.usernameInput = page.locator('#uid');
    this.passwordInput = page.locator('#passw');
    this.loginButton = page.getByRole('button', { name: 'Login' });
    this.signOffLink = page.getByRole('link', { name: 'Sign Off' });
    this.errorMessage = page.locator('#_ctl0__ctl0_Content_Main_message');
  }

  async navigate(): Promise<void> {
    await this.page.goto('/bank/login.aspx');
  }

  async login(username: string, password: string): Promise<void> {
    await this.usernameInput.fill(username);
    await this.passwordInput.fill(password);
    await this.loginButton.click();
  }

  async logout(): Promise<void> {
    await this.signOffLink.click();
  }

  async assertLoginSuccessful(): Promise<void> {
    await expect(this.signOffLink).toBeVisible();
    await expect(this.page).toHaveURL(/main\.jsp/);
  }

  async assertLoginFailed(): Promise<void> {
    await expect(this.errorMessage).toBeVisible();
    await expect(this.page.getByRole('heading', { name: 'Login' })).toBeVisible();
  }

  async assertOnLoginPage(): Promise<void> {
    await expect(this.page).toHaveURL(/login/);
    await expect(this.loginButton).toBeVisible();
  }

  async assertLoggedOut(): Promise<void> {
    // Altoro Mutual redirects to index.jsp (not login page) after Sign Off
    await expect(this.signOffLink).not.toBeVisible();
  }
}
