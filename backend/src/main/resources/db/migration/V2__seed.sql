-- Makelaars
insert into agency (id, name, city, phone, email, brand_hue)
values ('a0000000-0000-0000-0000-000000000001', 'Van Doorn Makelaars', 'Breda', '076-5211234',
        'info@vandoorn.example', 210),
       ('a0000000-0000-0000-0000-000000000002', 'Kade & Kant Wonen', 'Rotterdam', '010-4123456',
        'contact@kadeenkant.example', 25),
       ('a0000000-0000-0000-0000-000000000003', 'Hofstede Makelaardij', 'Utrecht', '030-2334455',
        'hallo@hofstede.example', 140);

-- Vaste woningen waar de tests naar verwijzen.
insert into listing (id, reference, street, house_number, postal_code, city, district, latitude, longitude,
                     price, status, property_type, living_area_m2, plot_area_m2, rooms, bedrooms, build_year,
                     energy_label, has_garden, has_balcony, has_parking, service_costs, description, agency_id,
                     published_at)
values ('b0000000-0000-0000-0000-000000000001', 'HJ-2026-0001', 'Ginnekenweg', '212', '4835 NJ', 'Breda',
        'Ginneken', 51.573200, 4.775800, 545000.00, 'BESCHIKBAAR', 'TUSSENWONING', 128, 210, 5, 3, 1932,
        'C', true, false, false, null,
        'Karakteristieke jaren-dertig woning met authentieke details, een diepe achtertuin op het zuidwesten en een compleet vernieuwde keuken.',
        'a0000000-0000-0000-0000-000000000001', now() - interval '3 days'),

       ('b0000000-0000-0000-0000-000000000002', 'HJ-2026-0002', 'Wilhelminasingel', '18 B', '4818 AA', 'Breda',
        'Centrum', 51.585100, 4.784200, 389000.00, 'BESCHIKBAAR', 'APPARTEMENT', 86, null, 3, 2, 2004,
        'A', false, true, true, 185.00,
        'Licht hoekappartement op de vierde verdieping met een royaal balkon op het zuiden en eigen parkeerplaats in de kelder.',
        'a0000000-0000-0000-0000-000000000001', now() - interval '9 days'),

       ('b0000000-0000-0000-0000-000000000003', 'HJ-2026-0003', 'Schoolstraat', '7', '4813 EA', 'Breda',
        'Belcrum', 51.598400, 4.771500, 675000.00, 'ONDER_BOD', 'VRIJSTAAND', 176, 480, 6, 4, 1968,
        'B', true, false, true, null,
        'Vrijstaande woning op een royaal perceel met inpandige garage, uitbouw aan de achterzijde en een verzorgde tuin rondom.',
        'a0000000-0000-0000-0000-000000000001', now() - interval '21 days'),

       ('b0000000-0000-0000-0000-000000000004', 'HJ-2026-0004', 'Zwaanshals', '344', '3035 KJ', 'Rotterdam',
        'Noord', 51.933800, 4.472100, 425000.00, 'BESCHIKBAAR', 'BENEDENWONING', 95, 60, 4, 2, 1928,
        'D', true, false, false, null,
        'Ruime benedenwoning met eigen voordeur, een stadstuin van zes meter diep en een souterrain dat nu als werkkamer wordt gebruikt.',
        'a0000000-0000-0000-0000-000000000002', now() - interval '1 day'),

       ('b0000000-0000-0000-0000-000000000005', 'HJ-2026-0005', 'Nieuwe Binnenweg', '155 C', '3014 GK', 'Rotterdam',
        'Middelland', 51.917700, 4.460300, 298000.00, 'BESCHIKBAAR', 'APPARTEMENT', 68, null, 3, 1, 1994,
        'B', false, true, false, 142.50,
        'Instapklaar appartement op loopafstand van het Museumpark, met een frans balkon aan de voorzijde en een berging op de begane grond.',
        'a0000000-0000-0000-0000-000000000002', now() - interval '5 days'),

       ('b0000000-0000-0000-0000-000000000006', 'HJ-2026-0006', 'Biltstraat', '89', '3572 AV', 'Utrecht',
        'Vogelenbuurt', 52.096400, 5.128900, 815000.00, 'BESCHIKBAAR', 'TWEE_ONDER_EEN_KAP', 194, 320, 7, 5, 1976,
        'A+', true, true, true, null,
        'Royale twee-onder-een-kapwoning met vijf slaapkamers, zonnepanelen op het dak en een oprit voor twee auto''s.',
        'a0000000-0000-0000-0000-000000000003', now() - interval '12 days'),

       ('b0000000-0000-0000-0000-000000000007', 'HJ-2026-0007', 'Oudegracht', '312', '3511 PK', 'Utrecht',
        'Binnenstad', 52.089200, 5.119700, 1250000.00, 'VERKOCHT', 'APPARTEMENT', 142, null, 4, 3, 1890,
        'E', false, false, false, 310.00,
        'Monumentaal grachtenpand met werfkelder, hoge plafonds en originele schouwen. Verkocht onder voorbehoud van financiering.',
        'a0000000-0000-0000-0000-000000000003', now() - interval '40 days'),

       ('b0000000-0000-0000-0000-000000000008', 'HJ-2026-0008', 'Havendijk', '4', '4815 CA', 'Breda',
        'Havenkwartier', 51.591200, 4.780400, 259000.00, 'BESCHIKBAAR', 'APPARTEMENT', 54, null, 2, 1, 2019,
        'A++', false, true, false, 128.00,
        'Nieuwbouwappartement met vloerverwarming, drievoudig glas en een balkon met uitzicht over het water.',
        'a0000000-0000-0000-0000-000000000001', now() - interval '2 days');

-- Vulling: nog 34 woningen, zodat zoeken, filteren en pagineren iets voorstellen.
insert into listing (id, reference, street, house_number, postal_code, city, district, latitude, longitude,
                     price, status, property_type, living_area_m2, plot_area_m2, rooms, bedrooms, build_year,
                     energy_label, has_garden, has_balcony, has_parking, service_costs, description, agency_id,
                     published_at)
select gen_random_uuid(),
       'HJ-2026-' || lpad((100 + n)::text, 4, '0'),
       (array['Beukenlaan', 'Kastanjestraat', 'Molenweg', 'Zuidsingel', 'Parkhof', 'Vlietkade',
              'Lindenhof', 'Spoorlaan', 'Bakkersgang', 'Weverstraat'])[1 + (n % 10)],
       (1 + (n * 7) % 180)::text,
       (array['4811 AB', '3033 CD', '3512 EF', '4834 GH', '3021 JK'])[1 + (n % 5)],
       (array['Breda', 'Rotterdam', 'Utrecht', 'Breda', 'Rotterdam'])[1 + (n % 5)],
       (array['Zuid', 'Noord', 'West', 'Oost', 'Centrum'])[1 + (n % 5)],
       51.5 + (n % 40) * 0.012,
       4.4 + (n % 30) * 0.021,
       195000 + (n % 26) * 27500,
       (array['BESCHIKBAAR', 'BESCHIKBAAR', 'BESCHIKBAAR', 'ONDER_BOD', 'VERKOCHT'])[1 + (n % 5)],
       (array['APPARTEMENT', 'TUSSENWONING', 'HOEKWONING', 'TWEE_ONDER_EEN_KAP', 'VRIJSTAAND',
              'BENEDENWONING'])[1 + (n % 6)],
       55 + (n % 18) * 9,
       case when n % 3 = 0 then null else 120 + (n % 12) * 35 end,
       2 + (n % 6),
       1 + (n % 5),
       1930 + (n % 90),
       (array['A++', 'A+', 'A', 'B', 'C', 'D', 'E', 'F', 'G'])[1 + (n % 9)],
       n % 2 = 0,
       n % 3 = 0,
       n % 4 = 0,
       case when n % 6 = 0 then 95.00 + (n % 5) * 12 else null end,
       'Ruime woning met een praktische indeling, een lichte woonkamer en voldoende bergruimte. ' ||
       'De buurt heeft scholen, winkels en openbaar vervoer op korte afstand.',
       (array['a0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000002',
              'a0000000-0000-0000-0000-000000000003'])[1 + (n % 3)]::uuid,
       now() - (n || ' days')::interval
from generate_series(1, 34) as n;

-- Foto's: acht tot twaalf per woning, waarvan de eerste vijf in de videotour.
insert into listing_photo (id, listing_id, position, room, caption, hue, in_tour)
select gen_random_uuid(),
       l.id,
       p.position,
       (array['Voorzijde', 'Woonkamer', 'Keuken', 'Slaapkamer', 'Badkamer', 'Tuin', 'Zolder', 'Berging',
              'Hal', 'Achterzijde', 'Balkon', 'Plattegrond'])[p.position],
       (array['Vooraanzicht vanaf de straat', 'Woonkamer met zicht op de tuin', 'Open keuken met kookeiland',
              'Ruime slaapkamer op de eerste verdieping', 'Badkamer met inloopdouche', 'Achtertuin op het zuiden',
              'Zolder met dakkapel', 'Bergruimte', 'Entree en hal', 'Achtergevel', 'Balkon', 'Plattegrond begane grond'])[p.position],
       (17 * p.position + (abs(hashtext(l.reference)) % 360)) % 360,
       p.position <= 5
from listing l
         cross join lateral (
    select generate_series(1, 8 + (abs(hashtext(l.reference)) % 5)) as position
    ) p;
