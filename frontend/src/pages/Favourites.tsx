import { useEffect, useState } from 'react';
import { api } from '../api';
import { ListingDetail } from '../types';
import { useUi } from '../ui/UiProfileContext';
import { ListingCard } from '../components/ListingCard';

export function Favourites() {
  const ui = useUi();
  const [listings, setListings] = useState<ListingDetail[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let references: string[] = [];
    try {
      references = JSON.parse(window.localStorage.getItem('huisjacht-favorieten') ?? '[]') as string[];
    } catch {
      references = [];
    }

    Promise.all(references.map((reference) =>
      api.get<ListingDetail>(`/listings/${reference}`).catch(() => null)))
      .then((results) => setListings(results.filter((item): item is ListingDetail => item !== null)))
      .finally(() => setLoading(false));
  }, []);

  return (
    <section>
      <h1>Bewaarde woningen</h1>
      {loading ? <p className={ui.cls('muted')}>Bezig met laden...</p> : null}
      <div className={ui.cls('results')} {...ui.testId('favourites')}>
        {listings.map((detail) => (
          <ListingCard key={detail.summary.id} listing={detail.summary}/>
        ))}
      </div>
      {!loading && listings.length === 0 ? (
        <p className={ui.cls('muted')} {...ui.testId('no-favourites')}>
          Je hebt nog geen woningen bewaard.
        </p>
      ) : null}
    </section>
  );
}
