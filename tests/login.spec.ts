import { test, expect } from '@playwright/test';

test.describe('Login Tests', () => {

    test('Successful login with valid credentials', async ({ page }) => {
        await page.goto('https://demo.testfire.net/');
        await page.getByRole('link', { name: 'Sign In' }).click();
        await page.locator('#uid').fill('jsmith');
        await page.locator('#passw').fill('Demo1234');
        await page.getByRole('button', { name: 'Login' }).click();
        // assert that the user is logged in by checking for a specific element on the page
        await expect(page.getByRole('heading', { name: 'Hello John Smith' })).toBeVisible();
        await expect(page.getByRole('link', { name: 'Sign Off' })).toBeVisible();
    });

    test('Unsuccessful login with invalid credentials', async ({ page }) => {
        await page.goto('https://demo.testfire.net/');
        await page.getByRole('link', { name: 'Sign In' }).click();
        await page.locator('#uid').fill('invalidUser');
        await page.locator('#passw').fill('invalidPass');
        await page.getByRole('button', { name: 'Login' }).click();
        // assert that an error message is displayed
        await expect(page.locator('#_ctl0__ctl0_Content_Main_message')).toBeVisible();
        await expect(page.getByRole('heading', { name: 'Login' })).toBeVisible();
    });

    test.describe('Edge Cases', () => {

        const emptyCredentials = [
            { username: '', password: '' },
            { username: 'jsmith', password: '' },
            { username: '', password: 'Demo1234' },
        ];

        emptyCredentials.forEach(({ username, password }) => {
            test(`Login attempt with username: "${username}" and password: "${password}"`, async ({ page }) => {
                await page.goto('https://demo.testfire.net/');
                await page.getByRole('link', { name: 'Sign In' }).click();
                await page.locator('#uid').fill(username);
                await page.locator('#passw').fill(password);

                page.once('dialog', async dialog => {
                    expect(dialog.message()).toContain('You must enter a valid');
                    await dialog.dismiss();
                });
                await page.getByRole('button', { name: 'Login' }).click();
                await expect(page.getByRole('heading', { name: 'Login' })).toBeVisible();
            });
        });

        test('should not login with empty username and password', async ({ page }) => {
            await page.goto('https://demo.testfire.net/');
            await page.getByRole('link', { name: 'Sign In' }).click();
            await page.locator('#uid').fill('');
            await page.locator('#passw').fill('');

            page.once('dialog', async dialog => {
                expect(dialog.message()).toContain('You must enter a valid');
                await dialog.dismiss();
            });
            await page.getByRole('button', { name: 'Login' }).click();
            await expect(page.getByRole('heading', { name: 'Login' })).toBeVisible();
        });

        test('SQL injection should not bypass login', async ({ page }) => {
            await page.goto('https://demo.testfire.net/bank/login.aspx');

            // Fake username with SQL injection — NOT a real user
            await page.locator('#uid').fill("' OR '1'='1");
            await page.locator('#passw').fill("' OR '1'='1");

            await page.getByRole('button', { name: 'Login' }).click();

            // Should NOT login — if it does, that's the bug
            await expect(page.getByRole('heading', { name: 'Login' })).toBeVisible();

            //Take screenshot for debugging purposes
            await page.screenshot({ path: 'test-results/sql-injection-evidence.png' });

            // This should NOT appear if vulnerable
            await expect(page.getByRole('link', { name: 'Sign Out' })).not.toBeVisible();
        });

        // test('should not login with SQL injection attempt', async ({ page }) => {
        //   await page.goto('https://demo.testfire.net/');
        //   await page.getByRole('link', { name: 'Sign In' }).click();
        //   await page.locator('#uid').fill("jsmith' OR '1'='1");
        //   await page.locator('#passw').fill("Demo1234' OR '1'='1");
        //   await page.getByRole('button', { name: 'Login' }).click();
        //   // assert that an error message is displayed
        //   await expect(page.locator('#_ctl0__ctl0_Content_Main_message')).toBeVisible();
        //   await expect(page.getByRole('heading',{name: 'Login'})).toBeVisible();
        // });
    });
});