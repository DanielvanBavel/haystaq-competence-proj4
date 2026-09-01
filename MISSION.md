# Opdracht 4 - Tests die zichzelf repareren

**Probleem uit de praktijk:** *"UI-tests breken na kleine wijzigingen."*
Een frontend-collega hernoemt een class, verplaatst een knop of ruimt wat
attributen op. Functioneel verandert er niets. Toch is de halve suite rood, en
kost het een uur om alle selectors bij te werken.

**AI-richting:** een self-healing agent die met DOM-analyse en semantische
matching de gewijzigde locators zelf terugvindt en herstelt.

---

## 1. De situatie

HuisJacht draait (zie [README.md](README.md)) en er rolt **elk kwartier een nieuwe
UI-revisie** uit. Vijf releases wisselen elkaar af: classnamen krijgen een hash,
veld-id's worden hernoemd, knopteksten herschreven, kaarten anders opgebouwd, en
in revisie 4 zijn de `data-testid`-attributen verdwenen.

Er ligt een testsuite van dertien tests:

- `tests/specs/brittle/` - acht tests, geschreven op revisie 1. Groen zolang de
  UI niet verandert, en zeven ervan zijn stuk zodra hij dat wel doet.
- `tests/specs/flaky/` - vijf tests die soms falen en soms niet, ook zonder
  drift. Vaste wachttijden, een banner die inschuift, een carousel die doordraait
  en een test die ervan uitgaat dat hij als eerste draait.

Beide zijn echt. Er wordt niets kunstmatig rood gezet.

## 2. Wat je bouwt

Een **self-healing testlaag**. Minimale scope:

| Component | Wat het moet doen |
| --- | --- |
| **Falen herkennen** | Onderscheid maken tussen "de locator klopt niet meer" en "de applicatie doet iets fout". Dat is de kern: een self-healing agent die echte bugs wegpoetst, is gevaarlijk. |
| **DOM begrijpen** | De pagina ophalen en kandidaten vinden voor het element dat de test zocht: rol, tekst, positie, buren, attributen. |
| **Semantisch matchen** | "De knop die een bezichtiging aanvraagt" terugvinden, ook als hij nu "Bezichtiging inplannen" heet en een andere class heeft. |
| **Herstellen** | De test laten slagen met de nieuwe locator, en de wijziging vastleggen: welke locator, waarom, hoe zeker. |
| **Verantwoorden** | Een rapport per run: hersteld, niet hersteld, en wat je zou moeten nakijken. |
| **Flakiness apart houden** | De vijf flaky tests hebben geen andere selector nodig. Die hebben een ander soort reparatie nodig, of een gesprek. |

Voorstel voor MCP-tools:

```
run_tests(pattern?)              -> resultaat per test, met foutmelding en gebruikte selector
page_snapshot(url)               -> DOM met rollen, teksten, testids en classes
find_candidates(intent, url)     -> elementen die bij een omschrijving passen, met score
rewrite_locator(file, old, new)  -> past de spec aan
ui_revision()                    -> huidige revisie
ui_release() / ui_pin(n)         -> een release uitrollen of vastzetten
```

## 3. Aanpak in fases

Reken op ongeveer vier uur.

**Fase 0 - Voelen wat het probleem is (30 min)**
Pin revisie 1, draai `npm run test:brittle` (alles groen). Rol één release uit en
draai opnieuw. Repareer met de hand twee tests en klok hoe lang je erover doet.
Dat is je nulmeting.

**Fase 1 - Diagnose automatiseren (45 min)**
Laat de agent van elke gefaalde test bepalen *welk element* werd gezocht. De
selector zelf is daarvoor niet genoeg: `.listing-card__price` zegt weinig als die
class niet meer bestaat. Gebruik de testnaam, de assertie en de omliggende code.

**Fase 2 - Kandidaten vinden (60 min)**
Bouw `page_snapshot` en `find_candidates`. Laat de agent voor één gefaalde test de
drie beste kandidaten geven met een onderbouwing. Doe dit eerst op revisie 2
(classnamen gehasht), daarna op revisie 4 (testids weg).

**Fase 3 - Herstellen (45 min)**
Laat de agent de spec aanpassen en opnieuw draaien tot groen. Belangrijk: laat
hem de oude locator in een commentaarregel of een logbestand bewaren. Een stille
herschrijving is niet te reviewen.

**Fase 4 - De grens bewaken (45 min)**
Zet de applicatie op revisie 3 en zet daarnaast één woning op `VERKOCHT` via
`POST /api/listings/{id}/status`. Nu faalt er een test omdat het gedrag anders is,
niet de UI. Herkent je agent het verschil? Dit is het belangrijkste onderdeel van
de opdracht.

**Fase 5 - Demo (15 min)**
Nieuwe release, agent erop, suite groen, plus het rapport met wat er is
aangepast en waarom.

## 4. Definition of done

- [ ] De acht brittle tests draaien groen op minimaal drie van de vijf revisies,
      na tussenkomst van de agent.
- [ ] De agent laat per herstelde locator zien: oud, nieuw, en waarom.
- [ ] De agent past niets aan als de applicatie zich functioneel anders gedraagt,
      en zegt dat er een echt verschil is.
- [ ] De flaky tests worden apart behandeld en niet met een langere timeout
      "opgelost" zonder uitleg.
- [ ] Een collega kan na een release één commando draaien en snapt uit het
      rapport wat er is gebeurd.
- [ ] Alles staat in deze repo, in een branch met een pull request.

## 5. Over de flaky tests

De vijf tests in `tests/specs/flaky` falen om vier verschillende redenen:
wisselende responstijden, een element dat verspringt, een element dat doordraait,
en een test die van volgorde afhangt. Bepaal per test wat de oorzaak is en of je
de test aanpast, de applicatie, of geen van beide.

Let op: één van de oorzaken is een **echte bug geweest** in deze applicatie (een
traag antwoord van een oudere zoekopdracht dat een nieuwer resultaat overschreef).
Die is inmiddels opgelost. Dat is precies waarom "gewoon een retry erop" een
gevaarlijk antwoord is: dan had niemand die bug gevonden.

Een goede aanpak onderscheidt:

1. de test wacht verkeerd (aanpassen in de test);
2. de applicatie is instabiel (aanpassen in de applicatie);
3. de test gaat uit van een toestand die hij niet zelf heeft gemaakt (isolatie).

## 6. Stretch goals

- Laat de agent na elke release automatisch draaien en een pull request openen
  met de herstelde selectors.
- Bouw een "locator-bibliotheek" die intenties (`prijs op de kaart`) koppelt aan
  actuele selectors, en laat de tests die gebruiken.
- Laat de agent voorstellen doen aan de frontend: welke elementen zouden een
  stabiele `data-testid` moeten krijgen? Lever dat als diff.
- Meet de kwaliteit: hoeveel herstelde locators zijn ook echt correct? Laat de
  agent zichzelf beoordelen op revisie 5 en controleer met de hand.
- Detecteer flakiness automatisch: draai de suite tien keer en laat de agent
  classificeren welke tests instabiel zijn en waarom.

## 7. Valkuilen

- **Alles groen willen krijgen.** Een test die groen is doordat hij niets meer
  controleert, is erger dan een rode test.
- **Alleen op tekst matchen.** Labels veranderen ook. Combineer rol, positie,
  buren en tekst.
- **De flaky tests met retries dichtplakken.** Dan verdwijnt het signaal.
- **Vergeten dat de UI onder je handen verandert.** Zet hem vast met
  `ui/pin/{n}` terwijl je werkt.
- **Denken dat testids de oplossing zijn.** In revisie 4 zijn ze er niet. De
  vraag is juist wat je doet als je ze niet hebt.

## 8. Wat je oplevert

1. Werkende code in deze repo: MCP-server, agent of skill.
2. Het herstelrapport van minimaal twee revisies.
3. Een demo van maximaal 10 minuten.
4. Eén alinea: hoeveel selectors herstelde de agent correct, waar zat hij ernaast,
   en zou je hem in een echte pijplijn durven zetten?
