import { FormEvent, useEffect, useState } from 'react';
import { api } from '../api';
import { ListingSummary, SearchResult } from '../types';
import { useUi } from '../ui/UiProfileContext';
import { ListingCard } from '../components/ListingCard';

interface Filters {
  term: string;
  city: string;
  maxPrice: string;
  minRooms: string;
  propertyType: string;
  garden: boolean;
  sort: string;
}

const EMPTY: Filters = {
  term: '',
  city: '',
  maxPrice: '',
  minRooms: '',
  propertyType: '',
  garden: false,
  sort: 'nieuwste'
};

export function Search() {
  const ui = useUi();
  const [filters, setFilters] = useState<Filters>(EMPTY);
  const [result, setResult] = useState<SearchResult | null>(null);
  const [cities, setCities] = useState<string[]>([]);
  const [types, setTypes] = useState<{ value: string; label: string }[]>([]);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [banner, setBanner] = useState<ListingSummary | null>(null);

  useEffect(() => {
    api.get<string[]>('/cities').then(setCities).catch(() => setCities([]));
    api.get<{ value: string; label: string }[]>('/property-types').then(setTypes).catch(() => setTypes([]));
  }, []);

  useEffect(() => {
    setLoading(true);
    const params = new URLSearchParams({ page: String(page), pageSize: '9' });
    if (filters.term) params.set('term', filters.term);
    if (filters.city) params.set('city', filters.city);
    if (filters.maxPrice) params.set('maxPrice', filters.maxPrice);
    if (filters.minRooms) params.set('minRooms', filters.minRooms);
    if (filters.propertyType) params.set('propertyType', filters.propertyType);
    if (filters.garden) params.set('garden', 'true');
    if (filters.sort) params.set('sort', filters.sort);

    // De backend antwoordt met wisselende snelheid. Zonder deze bewaking kan het
    // antwoord op een oudere zoekopdracht een nieuwer resultaat overschrijven.
    let current = true;
    api.get<SearchResult>(`/listings?${params.toString()}`)
      .then((response) => {
        if (current) {
          setResult(response);
        }
      })
      .catch(() => {
        if (current) {
          setResult(null);
        }
      })
      .finally(() => {
        if (current) {
          setLoading(false);
        }
      });

    return () => {
      current = false;
    };
  }, [filters, page]);

  // "Nieuw op HuisJacht" schuift na een paar seconden in beeld en duwt de
  // resultaten naar beneden. Handig voor de bezoeker, vervelend voor een klik
  // die net op dat moment plaatsvindt.
  useEffect(() => {
    if (!ui.profile.flakinessEnabled || !result || result.results.length === 0) {
      return;
    }
    setBanner(null);
    const delay = 1200 + Math.random() * 2300;
    const timer = window.setTimeout(() => setBanner(result.results[0]), delay);
    return () => window.clearTimeout(timer);
  }, [result, ui.profile.flakinessEnabled]);

  function update(field: keyof Filters, value: string | boolean) {
    setPage(0);
    setFilters((current) => ({ ...current, [field]: value }));
  }

  function submit(event: FormEvent) {
    event.preventDefault();
    setPage(0);
  }

  const filterFields: Record<string, JSX.Element> = {
    plaats: (
      <label key="plaats" className={ui.cls('filter')}>
        Plaats
        <select value={filters.city} onChange={(event) => update('city', event.target.value)}
                {...ui.testId('filter-city')}>
          <option value="">Alle plaatsen</option>
          {cities.map((city) => (
            <option key={city} value={city}>{city}</option>
          ))}
        </select>
      </label>
    ),
    prijs: (
      <label key="prijs" className={ui.cls('filter')}>
        Maximale prijs
        <input
          id={ui.token('priceMaxId', 'prijs-max')}
          type="number"
          step="25000"
          value={filters.maxPrice}
          onChange={(event) => update('maxPrice', event.target.value)}
          {...ui.testId('filter-max-price')}
        />
      </label>
    ),
    kamers: (
      <label key="kamers" className={ui.cls('filter')}>
        Aantal kamers vanaf
        <select value={filters.minRooms} onChange={(event) => update('minRooms', event.target.value)}
                {...ui.testId('filter-rooms')}>
          <option value="">Maakt niet uit</option>
          {[2, 3, 4, 5, 6].map((rooms) => (
            <option key={rooms} value={rooms}>{rooms} of meer</option>
          ))}
        </select>
      </label>
    ),
    type: (
      <label key="type" className={ui.cls('filter')}>
        Woningtype
        <select value={filters.propertyType} onChange={(event) => update('propertyType', event.target.value)}
                {...ui.testId('filter-type')}>
          <option value="">Alle types</option>
          {types.map((type) => (
            <option key={type.value} value={type.value}>{type.label}</option>
          ))}
        </select>
      </label>
    ),
    tuin: (
      <label key="tuin" className={ui.cls('filter', 'filter--check')}>
        <input type="checkbox" checked={filters.garden}
               onChange={(event) => update('garden', event.target.checked)}
               {...ui.testId('filter-garden')}/>
        Met tuin
      </label>
    )
  };

  const countText = ui.token('resultCountText', '{n} woningen gevonden')
    .replace('{n}', String(result?.totalResults ?? 0));

  return (
    <section>
      <h1>Woningaanbod</h1>

      <form className={ui.cls('searchbar')} onSubmit={submit} {...ui.testId('search-form')}>
        <label className={ui.cls('filter', 'filter--term')}>
          Zoeken
          <input
            id={ui.token('searchInputId', 'zoekterm')}
            type="search"
            placeholder="Straat, plaats of wijk"
            value={filters.term}
            onChange={(event) => update('term', event.target.value)}
            {...ui.testId('search-input')}
          />
        </label>
        <button type="submit" className={ui.cls('button', 'button--primary')} {...ui.testId('search-submit')}>
          {ui.token('searchButtonLabel', 'Zoeken')}
        </button>
      </form>

      <div className={ui.cls('filters')} {...ui.testId('filter-panel')}>
        {ui.profile.filterOrder.map((name) => filterFields[name]).filter(Boolean)}
        <label className={ui.cls('filter')}>
          Sorteren
          <select value={filters.sort} onChange={(event) => update('sort', event.target.value)}
                  {...ui.testId('sort')}>
            <option value="nieuwste">Nieuwste eerst</option>
            <option value="prijs-oplopend">Prijs oplopend</option>
            <option value="prijs-aflopend">Prijs aflopend</option>
            <option value="oppervlakte">Grootste eerst</option>
          </select>
        </label>
      </div>

      <p className={ui.cls('result-count')} {...ui.testId('result-count')}>{countText}</p>

      {banner ? (
        <aside className={ui.cls('new-banner')} {...ui.testId('new-listing-banner')}>
          Nieuw op HuisJacht: {banner.street} {banner.houseNumber} in {banner.city}
          <button type="button" className={ui.cls('button', 'button--ghost')} onClick={() => setBanner(null)}>
            Sluiten
          </button>
        </aside>
      ) : null}

      {loading ? <p className={ui.cls('muted')}>Bezig met zoeken...</p> : null}

      <div className={ui.cls('results')} {...ui.testId('results')}>
        {(result?.results ?? []).map((listing) => (
          <ListingCard key={listing.id} listing={listing}/>
        ))}
      </div>

      {result && result.totalResults === 0 && !loading ? (
        <p className={ui.cls('muted')} {...ui.testId('no-results')}>Geen woningen gevonden.</p>
      ) : null}

      {result && result.totalPages > 1 ? (
        <nav className={ui.cls('pagination')} {...ui.testId('pagination')}>
          <button type="button" disabled={page === 0} onClick={() => setPage((value) => value - 1)}>
            Vorige
          </button>
          <span>Pagina {result.page + 1} van {result.totalPages}</span>
          <button type="button" disabled={page + 1 >= result.totalPages}
                  onClick={() => setPage((value) => value + 1)}>
            Volgende
          </button>
        </nav>
      ) : null}
    </section>
  );
}
