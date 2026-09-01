-- HuisJacht - woningaanbod
-- Contexten: makelaars, aanbod, bezichtigingen. Geen foreign keys tussen
-- contexten heen; die relatie bewaakt de applicatielaag.

create table agency (
    id         uuid primary key,
    name       varchar(120) not null unique,
    city       varchar(80)  not null,
    phone      varchar(20)  not null,
    email      varchar(160) not null,
    brand_hue  integer      not null default 210 check (brand_hue between 0 and 359)
);

create table listing (
    id                uuid primary key,
    reference         varchar(16)   not null unique,
    street            varchar(120)  not null,
    house_number      varchar(10)   not null,
    postal_code       varchar(8)    not null,
    city              varchar(80)   not null,
    district          varchar(80),
    latitude          numeric(9, 6) not null,
    longitude         numeric(9, 6) not null,
    price             numeric(12, 2) not null check (price > 0),
    status            varchar(12)   not null check (status in ('BESCHIKBAAR', 'ONDER_BOD', 'VERKOCHT', 'INGETROKKEN')),
    property_type     varchar(20)   not null check (property_type in
        ('APPARTEMENT', 'TUSSENWONING', 'HOEKWONING', 'TWEE_ONDER_EEN_KAP', 'VRIJSTAAND', 'BENEDENWONING')),
    living_area_m2    integer       not null check (living_area_m2 > 0),
    plot_area_m2      integer       check (plot_area_m2 is null or plot_area_m2 > 0),
    rooms             integer       not null check (rooms between 1 and 20),
    bedrooms          integer       not null check (bedrooms between 0 and 15),
    build_year        integer       not null check (build_year between 1600 and 2100),
    energy_label      varchar(3)    not null check (energy_label in ('A++', 'A+', 'A', 'B', 'C', 'D', 'E', 'F', 'G')),
    has_garden        boolean       not null default false,
    has_balcony       boolean       not null default false,
    has_parking       boolean       not null default false,
    service_costs     numeric(8, 2),
    description       text          not null,
    agency_id         uuid          not null,
    published_at      timestamptz   not null default now(),
    version           bigint        not null default 0
);

create index listing_city_idx on listing (city);
create index listing_price_idx on listing (price);
create index listing_status_idx on listing (status);

create table listing_photo (
    id         uuid primary key,
    listing_id uuid         not null references listing (id) on delete cascade,
    position   integer      not null,
    room       varchar(40)  not null,
    caption    varchar(160) not null,
    hue        integer      not null check (hue between 0 and 359),
    in_tour    boolean      not null default false,
    unique (listing_id, position)
);

create index listing_photo_listing_idx on listing_photo (listing_id);

create table viewing_request (
    id             uuid primary key,
    listing_id     uuid         not null,
    requester_name varchar(120) not null,
    email          varchar(160) not null,
    phone          varchar(20),
    preferred_date date         not null,
    preferred_slot varchar(11)  not null,
    message        varchar(500),
    status         varchar(12)  not null check (status in ('AANGEVRAAGD', 'BEVESTIGD', 'AFGEWEZEN', 'GEANNULEERD')),
    created_at     timestamptz  not null default now(),
    version        bigint       not null default 0
);

create index viewing_listing_idx on viewing_request (listing_id);
create index viewing_email_idx on viewing_request (email);
