import { expect, test } from '@playwright/test';

/**
 * Deze tests slagen meestal. Meestal.
 *
 * Ze gebruiken vaste wachttijden, klikken op elementen die bewegen en gaan uit
 * van een volgorde die niet gegarandeerd is. Het gedrag van de applicatie is
 * niet fout; de tests gaan uit van timing die er niet is.
 */
test.describe('bekende wispelturige tests', () => {

  test('zoekresultaten staan er na een seconde', async ({ page }) => {
    await page.goto('/');
    // De backend heeft wisselende responstijden.
    await page.waitForTimeout(600);
    await expect(page.locator('[data-testid="listing-card"]').first()).toBeVisible({ timeout: 500 });
  });

  test('bezoeker klikt de eerste woning aan', async ({ page }) => {
    await page.goto('/');
    await page.waitForTimeout(1500);
    // Als de "nieuw op HuisJacht"-balk net inschuift, verspringt de lijst.
    await page.locator('[data-testid="listing-link"]').first().click();
    await expect(page.locator('[data-testid="detail-address"]')).toBeVisible({ timeout: 2000 });
  });

  test('foto in de galerij openen', async ({ page }) => {
    await page.goto('/woning/HJ-2026-0003');
    await page.waitForTimeout(800);
    // Bij een carousel is dit een ander element dan een seconde geleden.
    await page.locator('[data-testid="gallery"] img').first().click();
    await expect(page.locator('[class*="lightbox"]').first()).toBeVisible({ timeout: 1500 });
  });

  test('eerste vrije tijdvak kiezen', async ({ page }) => {
    // Deze test gaat ervan uit dat er nog niets geboekt is voor deze woning.
    await page.goto('/woning/HJ-2026-0001');
    await page.locator('[data-testid="request-viewing"]').click();
    await page.waitForTimeout(700);

    const slots = page.locator('[data-testid="viewing-slot"] option');
    // Welke tijdvakken vrij zijn hangt af van het tijdstip waarop je test.
    await expect(slots).toHaveCount(8);
  });

  test('videorondleiding staat op het tweede beeld', async ({ page }) => {
    await page.goto('/woning/HJ-2026-0001');
    await page.locator('[data-testid="tour-play"]').click();
    await page.waitForTimeout(2600);
    await expect(page.locator('[data-testid="tour-chapter"]')).toContainText('2/5');
  });
});
