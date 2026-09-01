import { APIRequestContext, request } from '@playwright/test';

const API = process.env.API_URL ?? 'http://localhost:8084';

/** Hulpmiddelen om de UI-revisie te sturen. Handig bij het onderzoeken van een storing. */
export const ui = {
  async state(): Promise<{ revision: number; name: string }> {
    const context: APIRequestContext = await request.newContext();
    const response = await context.get(`${API}/api/ui-profile`);
    const body = await response.json();
    await context.dispose();
    return body;
  },

  async pin(revision: number): Promise<void> {
    const context = await request.newContext();
    await context.post(`${API}/api/test-support/ui/pin/${revision}`);
    await context.dispose();
  },

  async release(): Promise<void> {
    const context = await request.newContext();
    await context.post(`${API}/api/test-support/ui/release`);
    await context.dispose();
  },

  async reset(): Promise<void> {
    const context = await request.newContext();
    await context.post(`${API}/api/test-support/reset`);
    await context.dispose();
  }
};
