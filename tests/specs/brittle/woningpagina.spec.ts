import { expect, test } from '@playwright/test';

test.describe('woningpagina', () => {

  test('bezoeker opent een woning vanaf het overzicht', async ({ page }) => {
    await page.goto('/');
    await page.locator('.listing-card__link').first().click();

    await expect(page).toHaveURL(/\/woning\/HJ-\d{4}-\d{4}/);
    await expect(page.locator('[data-testid="detail-price"]')).toContainText('€');
  });

  test('bezoeker bekijkt de fotogalerij', async ({ page }) => {
    await page.goto('/woning/HJ-2026-0001');

    // In de galerij staan de foto's als grid; de derde is de keuken.
    const thumbs = page.locator('.gallery__thumb');
    await expect(thumbs.first()).toBeVisible();
    await thumbs.nth(2).click();

    await expect(page.locator('.lightbox')).toBeVisible();
    await page.click('[data-testid="lightbox-close"]');
  });

  test('bezoeker vraagt een bezichtiging aan', async ({ page }) => {
    await page.goto('/woning/HJ-2026-0001');
    await page.click('button:has-text("Bezichtiging aanvragen")');

    await expect(page.locator('[data-testid="viewing-dialog"]')).toBeVisible();
    await page.fill('[data-testid="viewing-name"]', 'Pieter Jansen');
    await page.fill('[data-testid="viewing-email"]', `pieter+${Date.now()}@example.com`);
    // De tijdvakken worden opgehaald zodra de dialoog opent.
    await expect(page.locator('[data-testid="viewing-slot"] option').first()).toBeAttached();
    await page.click('[data-testid="viewing-submit"]');

    await expect(page.locator('[data-testid="viewing-message"]')).toContainText('verstuurd');
  });

  test('bezoeker bewaart een woning', async ({ page }) => {
    await page.goto('/woning/HJ-2026-0002');
    await page.click('button:has-text("Bewaren")');
    await expect(page.locator('[data-testid="save-listing"]')).toHaveText('Bewaard');
  });
});
