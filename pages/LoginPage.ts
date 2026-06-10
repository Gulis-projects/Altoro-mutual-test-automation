// pages/TransferPage.ts
import { Page, Locator } from '@playwright/test';

export class TransferPage {
    
    readonly page: Page;
    readonly fromAccount: Locator;
    readonly toAccount: Locator;
    readonly amount: Locator;
    readonly transferButton: Locator;
    readonly confirmationMsg: Locator;
    readonly errorMsg: Locator;

    constructor(page: Page) {
        this.page = page;
        this.fromAccount = page.locator('#fromAccount');
        this.toAccount = page.locator('#toAccount');
        this.amount = page.locator('#amount');
        this.transferButton = page.locator('#transfer');
        this.confirmationMsg = page.locator('.confirmation');
        this.errorMsg = page.locator('.error');
    }

    async navigateToTransfer(): Promise<void> {
        await this.page.click('text=Transfer Funds');
    }

    async selectFromAccount(accountId: string): Promise<void> {
        await this.fromAccount.selectOption(accountId);
    }

    async selectToAccount(accountId: string): Promise<void> {
        await this.toAccount.selectOption(accountId);
    }

    async enterAmount(amount: string): Promise<void> {
        await this.amount.fill(amount);
    }

    async clickTransfer(): Promise<void> {
        await this.transferButton.click();
    }

    async getConfirmationMessage(): Promise<string> {
        return await this.confirmationMsg.textContent() ?? '';
    }

    async getErrorMessage(): Promise<string> {
        return await this.errorMsg.textContent() ?? '';
    }

    async transferFunds(
        fromAcc: string, 
        toAcc: string, 
        amount: string
    ): Promise<void> {
        await this.navigateToTransfer();
        await this.selectFromAccount(fromAcc);
        await this.selectToAccount(toAcc);
        await this.enterAmount(amount);
        await this.clickTransfer();
    }
}