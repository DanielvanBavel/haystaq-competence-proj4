import { FormEvent, useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { api, ApiError } from '../api';
import { ListingDetail as Detail, Slot } from '../types';
import { useUi } from '../ui/UiProfileContext';
import { Gallery } from '../components/Gallery';
import { VideoTour } from '../components/VideoTour';

const TYPE_LABEL: Record<string, string> = {
  APPARTEMENT: 'Appartement',
  TUSSENWONING: 'Tussenwoning',
  HOEKWONING: 'Hoekwoning',
  TWEE_ONDER_EEN_KAP: 'Twee-onder-een-kap',
  VRIJSTAAND: 'Vrijstaand',
  BENEDENWONING: 'Benedenwoning'
};

function favourites(): string[] {
  try {
    return JSON.parse(window.localStorage.getItem('huisjacht-favorieten') ?? '[]') as string[];
  } catch {
    return [];
  }
}

export function ListingDetail() {
  const { reference } = useParams();
  const ui = useUi();
  const [detail, setDetail] = useState<Detail | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [saved, setSaved] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [slots, setSlots] = useState<Slot[]>([]);
  const [message, setMessage] = useState<string | null>(null);
  const [form, setForm] = useState({
    name: '',
    email: '',
    phone: '',
    date: new Date(Date.now() + 86_400_000).toISOString().slice(0, 10),
    slot: '',
    message: ''
  });

  const load = useCallback(async () => {
    if (!reference) {
      return;
    }
    try {
      setDetail(await api.get<Detail>(`/listings/${reference}`));
      setSaved(favourites().includes(reference));
    } catch (problem) {
      setError(problem instanceof ApiError ? problem.message : String(problem));
    }
  }, [reference]);

  useEffect(() => {
    void load();
  }, [load]);

  useEffect(() => {
    if (!dialogOpen || !reference) {
      return;
    }
    api.get<Slot[]>(`/listings/${reference}/slots?date=${form.date}`)
      .then((result) => {
        setSlots(result);
        const firstFree = result.find((slot) => slot.available);
        setForm((current) => ({ ...current, slot: firstFree ? firstFree.slot : '' }));
      })
      .catch(() => setSlots([]));
  }, [dialogOpen, form.date, reference]);

  function toggleFavourite() {
    if (!reference) {
      return;
    }
    const current = favourites();
    const next = current.includes(reference)
      ? current.filter((item) => item !== reference)
      : [...current, reference];
    window.localStorage.setItem('huisjacht-favorieten', JSON.stringify(next));
    setSaved(next.includes(reference));
  }

  async function submitViewing(event: FormEvent) {
    event.preventDefault();
    setMessage(null);
    try {
      await api.post('/viewings', {
        listingReference: reference,
        name: form.name,
        email: form.email,
        phone: form.phone || null,
        date: form.date,
        slot: form.slot,
        message: form.message || null
      });
      setMessage('Je aanvraag is verstuurd. De makelaar neemt contact met je op.');
      setDialogOpen(false);
    } catch (problem) {
      setMessage(problem instanceof ApiError ? problem.message : String(problem));
    }
  }

  if (error) {
    return <p className={ui.cls('error')} {...ui.testId('listing-error')}>{error}</p>;
  }
  if (!detail) {
    return <p className={ui.cls('muted')}>Bezig met laden...</p>;
  }

  const summary = detail.summary;

  return (
    <section className={ui.cls('detail')} data-reference={summary.reference}>
      <header className={ui.cls('detail__header')}>
        <div>
          <h1 {...ui.testId('detail-address')}>{summary.street} {summary.houseNumber}</h1>
          <p className={ui.cls('muted')}>{summary.postalCode} {summary.city}
            {summary.district ? ` · ${summary.district}` : ''}</p>
        </div>
        <div className={ui.cls('detail__price')}>
          <p {...ui.testId('detail-price')}>{ui.formatPrice(summary.price)}</p>
          <p className={ui.cls('muted')}>{ui.formatPrice(summary.pricePerSquareMetre)} per m²</p>
        </div>
      </header>

      <div className={ui.cls('detail__actions')}>
        <button type="button" className={ui.cls('button', 'button--primary')}
                onClick={() => setDialogOpen(true)} {...ui.testId('request-viewing')}>
          {ui.token('viewingButtonLabel', 'Bezichtiging aanvragen')}
        </button>
        <button type="button" className={ui.cls('button', 'button--ghost')} onClick={toggleFavourite}
                {...ui.testId('save-listing')}>
          {saved ? 'Bewaard' : ui.token('favouriteLabel', 'Bewaren')}
        </button>
        <span className={ui.cls('detail__status')} {...ui.testId('detail-status')}>{summary.status}</span>
      </div>

      {message ? <p className={ui.cls('notice')} {...ui.testId('viewing-message')}>{message}</p> : null}

      <Gallery photos={detail.photos}/>

      <h2>Videorondleiding</h2>
      <VideoTour frames={detail.tour}/>

      <h2>Omschrijving</h2>
      <p className={ui.cls('detail__description')} {...ui.testId('detail-description')}>{detail.description}</p>

      <h2>Kenmerken</h2>
      <table className={ui.cls('features')} {...ui.testId('features')}>
        <tbody>
        <tr><th>Woningtype</th><td>{TYPE_LABEL[summary.propertyType] ?? summary.propertyType}</td></tr>
        <tr><th>Woonoppervlakte</th><td>{summary.livingAreaM2} m²</td></tr>
        <tr><th>Perceel</th><td>{summary.plotAreaM2 ? `${summary.plotAreaM2} m²` : 'niet van toepassing'}</td></tr>
        <tr><th>Kamers</th><td>{summary.rooms} (waarvan {summary.bedrooms} slaapkamers)</td></tr>
        <tr><th>Bouwjaar</th><td>{summary.buildYear}</td></tr>
        <tr><th>Energielabel</th><td>{summary.energyLabel}</td></tr>
        <tr><th>Tuin</th><td>{summary.hasGarden ? 'ja' : 'nee'}</td></tr>
        <tr><th>Balkon</th><td>{summary.hasBalcony ? 'ja' : 'nee'}</td></tr>
        <tr><th>Parkeergelegenheid</th><td>{summary.hasParking ? 'ja' : 'nee'}</td></tr>
        {detail.serviceCosts !== null
          ? <tr><th>Servicekosten</th><td>{ui.formatPrice(detail.serviceCosts)} per maand</td></tr>
          : null}
        </tbody>
      </table>

      {detail.agency ? (
        <div className={ui.cls('agency')} {...ui.testId('agency')}>
          <h2>Makelaar</h2>
          <p><strong>{detail.agency.name}</strong> uit {detail.agency.city}</p>
          <p className={ui.cls('muted')}>{detail.agency.phone} · {detail.agency.email}</p>
        </div>
      ) : null}

      {dialogOpen ? (
        <div className={ui.cls('modal-backdrop')} role="dialog" aria-modal="true"
             {...ui.testId('viewing-dialog')}>
          <form className={ui.cls('modal')} onSubmit={submitViewing}>
            <h2>{ui.token('viewingButtonLabel', 'Bezichtiging aanvragen')}</h2>
            <label>
              Naam
              <input required value={form.name} {...ui.testId('viewing-name')}
                     onChange={(event) => setForm({ ...form, name: event.target.value })}/>
            </label>
            <label>
              E-mailadres
              <input required type="email" value={form.email} {...ui.testId('viewing-email')}
                     onChange={(event) => setForm({ ...form, email: event.target.value })}/>
            </label>
            <label>
              Telefoon
              <input value={form.phone}
                     onChange={(event) => setForm({ ...form, phone: event.target.value })}/>
            </label>
            <label>
              Datum
              <input type="date" value={form.date} {...ui.testId('viewing-date')}
                     onChange={(event) => setForm({ ...form, date: event.target.value })}/>
            </label>
            <label>
              Tijdvak
              <select value={form.slot} {...ui.testId('viewing-slot')}
                      onChange={(event) => setForm({ ...form, slot: event.target.value })}>
                {slots.filter((slot) => slot.available).map((slot) => (
                  <option key={slot.slot} value={slot.slot}>{slot.slot}</option>
                ))}
              </select>
            </label>
            <label>
              Vraag of opmerking
              <input value={form.message}
                     onChange={(event) => setForm({ ...form, message: event.target.value })}/>
            </label>
            <div className={ui.cls('modal__actions')}>
              <button type="button" className={ui.cls('button', 'button--ghost')}
                      onClick={() => setDialogOpen(false)}>
                Annuleren
              </button>
              <button type="submit" className={ui.cls('button', 'button--primary')}
                      {...ui.testId('viewing-submit')}>
                Aanvraag versturen
              </button>
            </div>
          </form>
        </div>
      ) : null}
    </section>
  );
}
