# UI-drift: wat verandert er precies

De frontend bouwt zijn HTML op met een profiel dat hij bij de backend ophaalt
(`GET /api/ui-profile`). Elke revisie geeft andere waarden voor dezelfde tokens.
Het gedrag van de applicatie verandert nooit.

De frontend haalt het profiel elke 20 seconden opnieuw op: een nieuwe release
komt binnen zonder dat je de pagina ververst. Precies zoals bij een deploy achter
een CDN.

## De vijf revisies

| Rev | Naam | Wat er verandert |
| --- | --- | --- |
| 1 | 2026.1 basis | Uitgangssituatie. Nette testids, semantische classes (`listing-card__price`), knop "Zoeken", veld-id `zoekterm` en `prijs-max`. |
| 2 | 2026.2 designsysteem | Classes krijgen een hash: `listing-card__price--x7f2`. Kaart is een `div` in een extra wrapper. Knoppen heten anders ("Zoek woningen", "Plan een bezichtiging"). |
| 3 | 2026.3 formuliervernieuwing | Veld-id's naar camelCase (`searchTerm`, `priceMax`). Testids hernoemd naar `qa-listingPrice`. Filtervolgorde gewijzigd. Galerij wordt een carousel. Prijzen als "450.000 euro". |
| 4 | 2026.4 opschoning | **De testids zijn weg.** Kaart is een `li`. Nieuwe classhash `b91d`. Knop heet "Vind je woning". |
| 5 | 2026.5 mobile first | Kaart is een `section` met dubbele wrapper, prijs staat bóven het adres. Veld-id's `q` en `f_price_max`. Prijzen zonder opmaak. |

Na revisie 5 begint de cyclus opnieuw bij 1.

## De tokens

| Token | Betekenis | Waarden |
| --- | --- | --- |
| `testIdMode` | Hoe testids eruitzien | `stable`, `renamed` (`qa-camelCase`), `absent` |
| `classSalt` | Achtervoegsel op elke classnaam | leeg, `x7f2`, `b91d`, `c4a8` |
| `cardTag` | HTML-tag van een woningkaart | `article`, `div`, `li`, `section` |
| `cardWrapper` | Extra omhullende elementen | `none`, `wrapped`, `double` |
| `searchButtonLabel` | Tekst op de zoekknop | drie varianten |
| `searchInputId` / `priceMaxId` | Veld-id's | `zoekterm`/`searchTerm`/`q`, `prijs-max`/`priceMax`/`f_price_max` |
| `viewingButtonLabel` | Tekst op de bezichtigingsknop | drie varianten |
| `favouriteLabel` | Tekst op de bewaarknop | `Bewaren`, `Bewaar deze woning`, `Opslaan` |
| `priceFormat` | Prijsnotatie | `€ 450.000`, `450.000 euro`, `450000` |
| `galleryLayout` | Fotogalerij | `grid`, `carousel` |
| `resultCountText` | Tekst boven de resultaten | drie varianten |
| `filterOrder` | Volgorde van de filters | vijf permutaties |

Wat **niet** verandert: de URL-structuur, de API, de teksten van de woninggegevens
en de betekenis van elk element. Een test die op betekenis stuurt in plaats van op
vorm, overleeft alle vijf de revisies.

## Bronnen van flakiness

Los van de drift zitten er vier echte bronnen van nondeterminisme in:

| Bron | Waar | Bereik |
| --- | --- | --- |
| Wisselende responstijd op zoeken en media | `LatencyFilter` | 40 tot 1800 ms |
| "Nieuw op HuisJacht"-banner die inschuift en de lijst omlaag duwt | `Search.tsx` | na 1,2 tot 3,5 s |
| Carousel die automatisch doordraait | `Gallery.tsx` | elke 4 s, alleen bij `galleryLayout=carousel` |
| Videorondleiding die per fragment anders lang duurt | `VideoTour.tsx` | 1,75 tot 3,25 s per beeld |

Daarnaast is er een vijfde bron die niets met timing te maken heeft: een
bezichtiging die door een eerdere test is geboekt, maakt een tijdvak
onbeschikbaar. Een test die uitgaat van acht vrije tijdvakken faalt dan - maar
alleen als hij als tweede draait.

Alles uit te zetten met `FLAKINESS_ENABLED=false`.

## Besturen

```bash
curl http://localhost:8084/api/ui-profile
curl http://localhost:8084/api/ui-profile/releases
curl -X POST http://localhost:8084/api/test-support/ui/release
curl -X POST http://localhost:8084/api/test-support/ui/pin/4
```

In `.env`:

```bash
UI_DRIFT_ENABLED=false          # zet de drift stil
UI_DRIFT_INTERVAL_MINUTES=15    # of juist 2 voor een snelle demo
UI_START_REVISION=1
FLAKINESS_ENABLED=false
FLAKINESS_MAX_MS=1800
```
