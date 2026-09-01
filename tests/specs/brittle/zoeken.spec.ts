import { expect, test } from '@playwright/test';

/**
 * Deze tests zijn geschreven toen UI-revisie 1 live stond. Ze werkten prima.
 * Ze gebruiken CSS-selectors op classnamen, veld-id's en exacte knopteksten -
 * precies zoals het in veel projecten gaat.
 */
test.describe('zoeken en filteren', () => {

  test('bezoeker zoekt op plaatsnaam', async ({ page }) => {
    await page.goto('/');
    await page.fill('#zoekterm', 'Breda');
    await page.click('button:has-text("Zoeken")');

    await expect(page.locator('[data-testid="result-count"]')).toContainText('woningen gevonden');
    await expect(page.locator('.listing-card')).not.toHaveCount(0);
  });

  test('bezoeker filtert op maximale prijs', async ({ page }) => {
    await page.goto('/');
    await page.fill('#prijs-max', '400000');

    const prices = page.locator('.listing-card__price');
    await expect(prices.first()).toBeVisible();

    // Wachten tot het filter is verwerkt: de duurste woning zakt onder de grens.
    await expect.poll(async () => {
      const values = await prices.allTextContents();
      return Math.max(...values.map((value) => Number(value.replace(/[^0-9]/g, ''))));
    }, { timeout: 10_000 }).toBeLessThanOrEqual(400000);
  });

  test('eerste zoekresultaat toont adres en prijs', async ({ page }) => {
    await page.goto('/');

    const first = page.locator('.listing-card').first();
    await expect(first.locator('.listing-card__address')).toBeVisible();
    await expect(first.locator('.listing-card__price')).toContainText('€');
  });

  test('bezoeker filtert op woningtype', async ({ page }) => {
    await page.goto('/');
    // Het derde filter is het woningtype.
    await page.locator('[class*="filters"] select').nth(2).selectOption('APPARTEMENT');
    await expect(page.locator('[data-testid="result-count"]')).toBeVisible();
  });
});
