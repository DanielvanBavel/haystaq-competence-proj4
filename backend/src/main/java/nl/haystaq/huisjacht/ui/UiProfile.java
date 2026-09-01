package nl.haystaq.huisjacht.ui;

import java.util.List;
import java.util.Map;

/**
 * Een UI-profiel beschrijft hoe de frontend zijn HTML opbouwt: welke testids er
 * zijn, hoe classes heten, welke tag een kaart gebruikt, hoe labels luiden en in
 * welke volgorde de filters staan.
 * <p>
 * Het gedrag van de applicatie verandert niet. Alleen de vorm. Precies zoals een
 * frontend-collega die "even een kleine wijziging" doorvoert.
 */
public record UiProfile(
        int revision,
        String name,
        String summary,
        Map<String, String> tokens,
        List<String> filterOrder) {

    /**
     * Vijf opeenvolgende releases. Revisie 1 is waar de bestaande tests op
     * geschreven zijn.
     */
    public static final List<UiProfile> RELEASES = List.of(
            new UiProfile(1, "2026.1 basis",
                    "De uitgangssituatie: nette testids, semantische classes.",
                    Map.ofEntries(
                            Map.entry("testIdMode", "stable"),
                            Map.entry("classSalt", ""),
                            Map.entry("cardTag", "article"),
                            Map.entry("cardWrapper", "none"),
                            Map.entry("searchButtonLabel", "Zoeken"),
                            Map.entry("searchInputId", "zoekterm"),
                            Map.entry("priceMaxId", "prijs-max"),
                            Map.entry("viewingButtonLabel", "Bezichtiging aanvragen"),
                            Map.entry("favouriteLabel", "Bewaren"),
                            Map.entry("priceFormat", "euro-symbol"),
                            Map.entry("galleryLayout", "grid"),
                            Map.entry("resultCountText", "{n} woningen gevonden")),
                    List.of("plaats", "prijs", "kamers", "type", "tuin")),

            new UiProfile(2, "2026.2 designsysteem",
                    "Classes komen uit het designsysteem en krijgen een hash. Labels zijn herschreven.",
                    Map.ofEntries(
                            Map.entry("testIdMode", "stable"),
                            Map.entry("classSalt", "x7f2"),
                            Map.entry("cardTag", "div"),
                            Map.entry("cardWrapper", "wrapped"),
                            Map.entry("searchButtonLabel", "Zoek woningen"),
                            Map.entry("searchInputId", "zoekterm"),
                            Map.entry("priceMaxId", "prijs-max"),
                            Map.entry("viewingButtonLabel", "Plan een bezichtiging"),
                            Map.entry("favouriteLabel", "Bewaar deze woning"),
                            Map.entry("priceFormat", "euro-symbol"),
                            Map.entry("galleryLayout", "grid"),
                            Map.entry("resultCountText", "{n} resultaten")),
                    List.of("plaats", "prijs", "kamers", "type", "tuin")),

            new UiProfile(3, "2026.3 formuliervernieuwing",
                    "Alle veld-id's zijn hernoemd naar camelCase en de filtervolgorde is gewijzigd.",
                    Map.ofEntries(
                            Map.entry("testIdMode", "renamed"),
                            Map.entry("classSalt", "x7f2"),
                            Map.entry("cardTag", "div"),
                            Map.entry("cardWrapper", "wrapped"),
                            Map.entry("searchButtonLabel", "Zoek woningen"),
                            Map.entry("searchInputId", "searchTerm"),
                            Map.entry("priceMaxId", "priceMax"),
                            Map.entry("viewingButtonLabel", "Plan een bezichtiging"),
                            Map.entry("favouriteLabel", "Bewaar deze woning"),
                            Map.entry("priceFormat", "euro-suffix"),
                            Map.entry("galleryLayout", "carousel"),
                            Map.entry("resultCountText", "Gevonden: {n}")),
                    List.of("prijs", "plaats", "type", "kamers", "tuin")),

            new UiProfile(4, "2026.4 opschoning",
                    "De testids zijn tijdens een opschoonactie verdwenen. Niemand die het merkte.",
                    Map.ofEntries(
                            Map.entry("testIdMode", "absent"),
                            Map.entry("classSalt", "b91d"),
                            Map.entry("cardTag", "li"),
                            Map.entry("cardWrapper", "wrapped"),
                            Map.entry("searchButtonLabel", "Vind je woning"),
                            Map.entry("searchInputId", "searchTerm"),
                            Map.entry("priceMaxId", "priceMax"),
                            Map.entry("viewingButtonLabel", "Bezichtiging inplannen"),
                            Map.entry("favouriteLabel", "Opslaan"),
                            Map.entry("priceFormat", "euro-suffix"),
                            Map.entry("galleryLayout", "carousel"),
                            Map.entry("resultCountText", "Gevonden: {n}")),
                    List.of("prijs", "type", "plaats", "tuin", "kamers")),

            new UiProfile(5, "2026.5 mobile first",
                    "Kaarten zijn opnieuw opgebouwd, prijs staat nu boven het adres.",
                    Map.ofEntries(
                            Map.entry("testIdMode", "renamed"),
                            Map.entry("classSalt", "c4a8"),
                            Map.entry("cardTag", "section"),
                            Map.entry("cardWrapper", "double"),
                            Map.entry("searchButtonLabel", "Vind je woning"),
                            Map.entry("searchInputId", "q"),
                            Map.entry("priceMaxId", "f_price_max"),
                            Map.entry("viewingButtonLabel", "Bezichtiging inplannen"),
                            Map.entry("favouriteLabel", "Opslaan"),
                            Map.entry("priceFormat", "plain"),
                            Map.entry("galleryLayout", "carousel"),
                            Map.entry("resultCountText", "{n} treffers")),
                    List.of("plaats", "type", "prijs", "tuin", "kamers")));

    public static UiProfile byRevision(int revision) {
        return RELEASES.stream()
                .filter(profile -> profile.revision() == revision)
                .findFirst()
                .orElse(RELEASES.get(0));
    }
}
