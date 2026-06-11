import { Page, Locator, expect } from '@playwright/test';

export class TransferPage {
  readonly page: Page;
  readonly fromAccount: Locator;
  readonly toAccount: Locator;
  readonly amountInput: Locator;
  readonly transferButton: Locator;
  readonly confirmationSpan: Locator;

  constructor(page: Page) {
    this.page = page;
    this.fromAccount = page.locator('#fromAccount');
    this.toAccount = page.locator('#toAccount');
    this.amountInput = page.locator('#transferAmount');
    this.transferButton = page.locator('#transfer');
    // Server renders the result message into this span after form submit
    this.confirmationSpan = page.locator('#_ctl0__ctl0_Content_Main_postResp');
  }

  async navigate(): Promise<void> {
    await this.page.goto('/bank/transfer.jsp');
  }

  async getFromAccountOptions(): Promise<string[]> {
    return this.fromAccount.locator('option').allTextContents();
  }

  async getToAccountOptions(): Promise<string[]> {
    return this.toAccount.locator('option').allTextContents();
  }

  async selectFromAccount(labelText: string): Promise<void> {
    await this.fromAccount.selectOption({ label: labelText });
  }

  async selectToAccount(labelText: string): Promise<void> {
    await this.toAccount.selectOption({ label: labelText });
  }

  async enterAmount(amount: string): Promise<void> {
    await this.amountInput.fill(amount);
  }

  async clickTransfer(): Promise<void> {
    await this.transferButton.click();
  }

  async assertOnTransferPage(): Promise<void> {
    await expect(this.page).toHaveURL(/transfer\.jsp/);
    await expect(this.fromAccount).toBeVisible();
    await expect(this.toAccount).toBeVisible();
    await expect(this.amountInput).toBeVisible();
  }

  async assertTransferSuccessful(): Promise<void> {
    await expect(this.confirmationSpan).toContainText(/successfully transferred/i);
  }

  async assertTransferNotSuccessful(): Promise<void> {
    // Either the span is hidden, empty, or contains no success text
    const text = await this.confirmationSpan.textContent().catch(() => '');
    expect((text ?? '').toLowerCase()).not.toContain('successfully transferred');
  }
}
