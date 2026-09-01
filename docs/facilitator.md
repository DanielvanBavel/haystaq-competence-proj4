# Facilitator - achtergrond bij opdracht 4

> Voor de begeleiding van de dag.

## Wat er gebeurt als je niets doet

De applicatie start op revisie 1 en zet elk kwartier een nieuwe revisie live. Na
vijf kwartier is de cyclus rond. Deelnemers merken dat doordat hun net groene
suite ineens rood is, zonder dat zij iets hebben aangepast.

Dat is bedoeld. Maar het is ook vervelend als iemand net aan het bouwen is. Geef
aan het begin van de dag door:

```bash
curl -X POST http://localhost:8084/api/test-support/ui/pin/1
```

en in `.env`:

```bash
UI_DRIFT_ENABLED=false
```

Laat ze de drift pas aanzetten als hun agent iets kan.

## Verwachte uitkomsten per revisie

Gemeten met de meegeleverde brittle suite (acht tests):

| Revisie | Groen | Waarom de rest faalt |
| --- | --- | --- |
| 1 | 8 | uitgangssituatie |
| 2 | 1 | classhash `--x7f2` breekt alle CSS-selectors; knoptekst "Zoeken" bestaat niet meer |
| 3 | 1 | daarbovenop hernoemde veld-id's en testids, carousel in plaats van grid |
| 4 | 1 | testids helemaal weg |
| 5 | 1 | prijs boven adres, andere tag en veld-id's |

De test die op alle revisies blijft slagen (`bezoeker filtert op woningtype`)
gebruikt `[class*="filters"] select` en `nth(2)`. Aardig detail voor de nabespreking:
hij overleeft de hash, maar hij is nog steeds fout - vanaf revisie 3 staat het
woningtype níét meer op de derde plek, dus hij selecteert het verkeerde filter en
merkt dat niet. Groen betekent niet goed.

## De flaky tests

Vijf tests, vier oorzaken:

| Test | Oorzaak | Juiste oplossing |
| --- | --- | --- |
| zoekresultaten staan er na een seconde | vaste `waitForTimeout` tegen een backend die 40-1800 ms doet | wachten op het element, niet op de klok |
| bezoeker klikt de eerste woning aan | de "nieuw op HuisJacht"-banner schuift na 1,2-3,5 s in en duwt de lijst omlaag | wachten tot de lijst stabiel is, of de banner afwachten |
| foto in de galerij openen | vanaf revisie 3 draait de carousel elke 4 s door | pauzeren, of op een vast fragment sturen |
| eerste vrije tijdvak kiezen | verwacht acht vrije tijdvakken, maar de brittle suite heeft er al een geboekt | isolatie: eerst `POST /api/test-support/reset` |
| videorondleiding staat op het tweede beeld | elk fragment duurt 1,75-3,25 s ("buffering") | op de tekst wachten in plaats van op de tijd |

In een run van 25 (`--repeat-each=5`) faalden er bij ons drie. Op een langzamere
laptop zijn dat er meer. Laat deelnemers `npm run test:flaky -- --repeat-each=5`
draaien voordat ze conclusies trekken over één run.

## Het belangrijkste gesprek van de dag

Zet halverwege een woning op verkocht:

```bash
curl -s http://localhost:8084/api/listings/HJ-2026-0001 | head -c 200
curl -X POST http://localhost:8084/api/listings/<id>/status \
  -H 'content-type: application/json' -d '{"status":"VERKOCHT"}'
```

De bezichtigingsknop werkt dan nog steeds, maar de aanvraag wordt geweigerd met
`listing.not_viewable`. Een self-healing agent die alleen naar selectors kijkt,
gaat op zoek naar een "betere" knop en verbergt daarmee een functionele
wijziging. Dat is het gevaar van deze techniek, en het is precies waar het
gesprek over moet gaan.

## Ingrepen tijdens de dag

| Wat | Hoe |
| --- | --- |
| Snellere drift voor een demo | `UI_DRIFT_INTERVAL_MINUTES=2` in `.env`, daarna `docker compose up -d api` |
| Alles stil | `UI_DRIFT_ENABLED=false` en `FLAKINESS_ENABLED=false` |
| Eén stap verder | `curl -X POST .../test-support/ui/release` |
| Terug naar het begin | `curl -X POST .../test-support/ui/pin/1` en `.../test-support/reset` |
| Extra chaos | `FLAKINESS_MAX_MS=3000` |

## Tijdsindeling

- Laat iedereen eerst met de hand twee kapotte selectors repareren. Zonder dat
  gevoel is de winst van de agent niet te beoordelen.
- Vraag na fase 2 welke informatie zij zelf gebruiken om een element terug te
  vinden. Meestal: tekst, positie, buren, rol. Dat is precies wat de agent nodig
  heeft.
- Bewaar de "verkochte woning" voor het laatste uur.
