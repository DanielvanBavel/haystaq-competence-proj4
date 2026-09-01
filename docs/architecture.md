# Architectuur

HuisJacht is een modulaire monoliet volgens Domain Driven Design, met daarnaast
één component dat niets met het domein te maken heeft: de UI-drift-engine.

```
nl.haystaq.huisjacht
├── shared          waarde-objecten, domeinfout, foutafhandeling, latency-filter
├── makelaars       makelaarskantoren
├── aanbod          woningen, foto's en de gegenereerde media
├── bezichtigingen  aanvragen voor een rondleiding
└── ui              het UI-profiel en de periodieke release
```

Elk context heeft dezelfde indeling: `domain`, `application`, `infrastructure`,
`api`.

## Aggregates

| Context | Aggregate root | Bevat | Belangrijkste regels |
| --- | --- | --- | --- |
| makelaars | `Agency` | - | naam uniek |
| aanbod | `Listing` | `ListingPhoto`, `Address` | postcodeformaat, prijs per m², verkocht blijft verkocht, bezichtigen mag alleen bij beschikbaar of onder bod |
| bezichtigingen | `ViewingRequest` | - | datum in de toekomst, maximaal 30 dagen vooruit, niet op zondag, vast tijdvak, maximaal twee openstaande aanvragen per e-mailadres per woning |

## Poorten tussen contexten

| Poort (consument) | Adapter (leverancier) | Waarvoor |
| --- | --- | --- |
| `aanbod.domain.AgencyDirectory` | `makelaars.application.AgencyDirectoryAdapter` | makelaarsblok op de woningpagina |
| `bezichtigingen.domain.ListingDirectory` | `aanbod.application.ListingDirectoryAdapter` | bestaat de woning en mag er bezichtigd worden |

Zoals in de andere opdrachten: de consument definieert de poort, de leverancier
levert de adapter, en het aggregate zelf blijft binnen zijn eigen context.

## Media zonder media

Er staat geen enkele afbeelding in de repository. `MediaController` genereert per
foto een SVG op basis van de opgeslagen kleurtoon, het vertrek en de positie. Elke
woning heeft 8 tot 12 foto's, waarvan de eerste vijf de videorondleiding vormen.
Dat levert een site op die aanvoelt als een echt woningplatform, zonder dat er
honderden megabytes aan JPEG's in git staan.

De rondleiding is geen mp4 maar een reeks beelden met afspeelknop, voortgangsbalk
en hoofdstukken. Voor de tester maakt dat weinig uit: het is een speler met
knoppen die van staat verandert.

## De UI-drift-engine

`UiProfileService` houdt bij welke van de vijf revisies live staat en zet er elk
kwartier een nieuwe op (`@Scheduled`). De frontend haalt het profiel op en gebruikt
het in `UiProfileContext`:

```tsx
const ui = useUi();
<button className={ui.cls('button', 'button--primary')} {...ui.testId('search-submit')}>
  {ui.token('searchButtonLabel', 'Zoeken')}
</button>
```

- `ui.cls(...)` plakt de classhash van de revisie erachter.
- `ui.testId(...)` levert een testid, een hernoemd testid, of niets.
- `ui.token(...)` levert labels en veld-id's.

De stylesheet selecteert daarom op *delen* van classnamen
(`[class*="listing-card__price"]`). Dat is lelijk, en het is precies waarom
CSS-selectors in tests zo fragiel zijn.

## Foutmodel

| Situatie | HTTP | Body |
| --- | --- | --- |
| Waarde afgekeurd | 400 | `{"code":"address.postal_code","message":"..."}` |
| Toestand verhindert de actie | 409 | `{"code":"viewing.slot_taken","message":"..."}` |
| Onbekend | 404 | `{"code":"listing.not_found","message":"..."}` |
| Onverwacht | 500 | `{"code":"internal_error","ref":"a1b2c3d4"}` |

Anders dan in opdracht 1 zijn de meldingen hier bruikbaar: het gaat in deze
opdracht om de UI, niet om raadselachtige API-fouten.

## Database

Eén PostgreSQL-database, per context een eigen groep tabellen, geen foreign keys
tussen contexten. Migraties met Flyway (`V1__schema.sql`, `V2__seed.sql`); de seed
maakt 42 woningen en ruim 400 fotorecords aan met `generate_series`.
