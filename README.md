# haystaq-competence-proj4 - HuisJacht

> Competence-dag, opdracht 4: **"UI-tests breken na kleine wijzigingen."**
> De opdracht staat in [MISSION.md](MISSION.md). Dit bestand beschrijft de applicatie.

HuisJacht is een woningplatform: zoeken en filteren in het aanbod, woningpagina's
met tientallen foto's en een videorondleiding, bezichtigingen aanvragen, en een
makelaarsdashboard om die aanvragen af te handelen.

En er is iets bijzonders aan de hand: **elk kwartier rolt er een nieuwe
UI-revisie uit.** Classnamen krijgen een hash, veld-id's worden hernoemd, labels
worden herschreven, kaarten worden anders opgebouwd en op enig moment verdwijnen
de `data-testid`-attributen. Het gedrag blijft precies gelijk. De testsuite niet.

## Stack

| Laag | Technologie |
| --- | --- |
| Backend | Java 21, Spring Boot 3.4, Spring Data JPA, Flyway |
| Database | PostgreSQL 16 (eigen database) |
| Frontend | React 18 + TypeScript, Vite, nginx |
| Tests | Playwright (TypeScript) |
| Architectuur | Domain Driven Design, modulaire monoliet met drie bounded contexts |

Zie [docs/architecture.md](docs/architecture.md) en [docs/ui-drift.md](docs/ui-drift.md).

## Snel starten

```bash
docker compose up -d --build
```

- UI: <http://localhost:3004>
- API: <http://localhost:8084/api/listings>
- Health: <http://localhost:8084/actuator/health>
- Postgres: `localhost:5436`, database/gebruiker/wachtwoord `huisjacht`

Poorten bezet of wil je rust tijdens het bouwen? Zie [.env.example](.env.example).

## Wat er in zit

| Scherm | Wat er gebeurt |
| --- | --- |
| Aanbod | zoeken op straat, plaats of wijk; filteren op plaats, prijs, kamers, type en tuin; sorteren; pagineren |
| Woningpagina | 8 tot 12 foto's per woning (grid of carousel), videorondleiding, kenmerkentabel, makelaarsblok, bewaren, bezichtiging aanvragen |
| Bewaard | woningen uit je localStorage |
| Makelaar | aanvragen bevestigen of afwijzen |

42 woningen, ruim 400 foto's. Alle beelden worden door de backend gegenereerd als
SVG: geen enkele mediabyte in de repository, en toch een volle site.

## De twee soorten falende tests

In `tests/specs` staan twee mappen, en ze falen om verschillende redenen:

| Map | Waarom het faalt | Wanneer |
| --- | --- | --- |
| `brittle/` | selectors die aan één UI-revisie vastzitten | deterministisch, zodra de UI drift |
| `flaky/` | vaste wachttijden, bewegende elementen, volgorde-afhankelijkheid | onvoorspelbaar, ook zonder drift |

Op revisie 1 zijn alle acht brittle tests groen. Eén release later zijn er zeven
stuk. De flaky tests falen soms wel en soms niet, ook als er niets verandert.

```bash
cd tests && npm install && npx playwright install chromium
npm run test:brittle
curl -X POST http://localhost:8084/api/test-support/ui/release
npm run test:brittle
```

```bash
npm run test:flaky -- --repeat-each=5
```

## De UI besturen

| Actie | Commando |
| --- | --- |
| Huidige revisie opvragen | `curl http://localhost:8084/api/ui-profile` |
| Alle releases bekijken | `curl http://localhost:8084/api/ui-profile/releases` |
| Volgende revisie uitrollen | `curl -X POST http://localhost:8084/api/test-support/ui/release` |
| Vastzetten op revisie 3 | `curl -X POST http://localhost:8084/api/test-support/ui/pin/3` |
| Database terugzetten | `curl -X POST http://localhost:8084/api/test-support/reset` |

De actieve revisie staat ook rechtsboven in de UI en als `data-ui-revision` op het
hoofdelement.

## Rust tijdens het bouwen

Een driftende UI terwijl je aan het ontwikkelen bent, is niet altijd handig. Maak
een `.env`:

```bash
UI_DRIFT_ENABLED=false
FLAKINESS_ENABLED=false
```

Daarmee staat de UI stil op revisie 1 en verdwijnen de wisselende responstijden,
de inschuivende banner en de bufferende videorondleiding. Zet ze weer aan zodra
je je oplossing wilt beproeven; zonder die twee is de opdracht namelijk niet
zo interessant.

## API in het kort

| Methode | Pad | Doel |
| --- | --- | --- |
| GET | `/api/listings` | Zoeken en filteren (term, city, minPrice, maxPrice, minRooms, propertyType, garden, status, sort, page, pageSize) |
| GET | `/api/listings/{reference}` | Woning met foto's, tour en makelaar |
| GET | `/api/listings/{reference}/slots?date=` | Beschikbare tijdvakken |
| POST | `/api/listings/{id}/status` | Status wijzigen |
| GET | `/api/media/photos/{id}?width=` | Gegenereerde foto (SVG) |
| GET/POST | `/api/viewings` | Bezichtigingsaanvragen |
| POST | `/api/viewings/{id}/decide` | Bevestigen, afwijzen of annuleren |
| GET | `/api/cities`, `/api/property-types` | Keuzelijsten |
| GET | `/api/ui-profile` | Het actieve UI-profiel |

Foutmeldingen zijn hier bewust duidelijk: `{"code":"viewing.slot_taken","message":"Dit
tijdvak is inmiddels gereserveerd. Kies een ander moment."}`.

## Zonder Docker draaien (optioneel)

```bash
docker compose up -d db
```

```bash
cd backend && DB_PORT=5436 DB_HOST=localhost mvn spring-boot:run
```

```bash
cd frontend && npm install && npm run dev
```
