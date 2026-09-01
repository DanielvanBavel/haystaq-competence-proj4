# De bestaande testsuite

Dertien Playwright-tests, in twee smaken.

```
tests/
├── playwright.config.ts     geen retries: flakiness moet zichtbaar blijven
├── support/ui.ts            revisie opvragen, vastzetten, uitrollen, resetten
├── specs/brittle/           acht tests met selectors uit revisie 1
└── specs/flaky/             vijf tests die soms falen
```

## Draaien

De applicatie moet draaien (`docker compose up -d --build`).

```bash
cd tests && npm install && npx playwright install chromium
```

```bash
npm run test:brittle
npm run test:flaky -- --repeat-each=5
npm test
```

Zonder Node op je laptop:

```bash
docker run --rm --network host -v "%cd%\tests:/work" -w /work mcr.microsoft.com/playwright:v1.49.1-noble bash -lc "npm install && npx playwright test"
```

## De brittle tests

Geschreven toen revisie 1 live stond, met precies de selectors die je in de
praktijk tegenkomt:

| Test | Selector die breekt |
| --- | --- |
| zoekt op plaatsnaam | `#zoekterm`, `button:has-text("Zoeken")`, `.listing-card` |
| filtert op maximale prijs | `#prijs-max`, `.listing-card__price` |
| eerste resultaat toont adres en prijs | `.listing-card__address`, `.listing-card__price` |
| filtert op woningtype | `[class*="filters"] select` met `nth(2)` |
| opent een woning | `.listing-card__link` |
| bekijkt de fotogalerij | `.gallery__thumb`, `.lightbox` |
| vraagt een bezichtiging aan | `button:has-text("Bezichtiging aanvragen")` |
| bewaart een woning | `button:has-text("Bewaren")` |

Op revisie 1 zijn ze alle acht groen. Probeer het:

```bash
curl -X POST http://localhost:8084/api/test-support/ui/pin/1 && npm run test:brittle
curl -X POST http://localhost:8084/api/test-support/ui/release && npm run test:brittle
```

## De flaky tests

Die falen zonder dat er iets verandert. Draai ze een paar keer voordat je iets
concludeert:

```bash
npm run test:flaky -- --repeat-each=5
```

`playwright.config.ts` staat bewust op `retries: 0`. Zet dat niet aan: met retries
lijkt alles in orde en verdwijnt precies het signaal waar deze opdracht over gaat.

## Afspraken voor nieuwe of herstelde tests

- Wachten op gedrag, niet op de klok.
- Elke test maakt zijn eigen uitgangssituatie (`POST /api/test-support/reset` waar
  het uitmaakt).
- Een herstelde locator komt met een reden: waarom is dit hetzelfde element?
- Een test die groen is doordat hij niets meer controleert, telt niet.
