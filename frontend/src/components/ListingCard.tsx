import { createElement, ReactNode } from 'react';
import { Link } from 'react-router-dom';
import { ListingSummary } from '../types';
import { useUi } from '../ui/UiProfileContext';

const STATUS_LABEL: Record<string, string> = {
  BESCHIKBAAR: 'Beschikbaar',
  ONDER_BOD: 'Onder bod',
  VERKOCHT: 'Verkocht',
  INGETROKKEN: 'Ingetrokken'
};

const TYPE_LABEL: Record<string, string> = {
  APPARTEMENT: 'Appartement',
  TUSSENWONING: 'Tussenwoning',
  HOEKWONING: 'Hoekwoning',
  TWEE_ONDER_EEN_KAP: 'Twee-onder-een-kap',
  VRIJSTAAND: 'Vrijstaand',
  BENEDENWONING: 'Benedenwoning'
};

/**
 * De opbouw van deze kaart verandert per UI-revisie: de tag, de wrappers, de
 * volgorde van prijs en adres en de classnamen. De informatie blijft gelijk.
 */
export function ListingCard({ listing }: { listing: ListingSummary }) {
  const ui = useUi();
  const tag = ui.token('cardTag', 'article');
  const wrapper = ui.token('cardWrapper', 'none');
  const priceFirst = ui.profile.revision >= 5;

  const price = (
    <p className={ui.cls('listing-card__price')} {...ui.testId('listing-price')}>
      {ui.formatPrice(listing.price)}
    </p>
  );

  const address = (
    <h3 className={ui.cls('listing-card__address')} {...ui.testId('listing-address')}>
      {listing.street} {listing.houseNumber}
    </h3>
  );

  const body: ReactNode = (
    <>
      <div className={ui.cls('listing-card__media')}>
        {listing.coverPhotoUrl ? (
          <img
            src={`${listing.coverPhotoUrl}?width=640`}
            alt={`Vooraanzicht van ${listing.street} ${listing.houseNumber}`}
            loading="lazy"
            className={ui.cls('listing-card__photo')}
          />
        ) : (
          <div className={ui.cls('listing-card__photo', 'is-empty')}/>
        )}
        <span className={ui.cls('listing-card__badge')}>{STATUS_LABEL[listing.status] ?? listing.status}</span>
        <span className={ui.cls('listing-card__count')}>{listing.photoCount} foto's</span>
      </div>

      <div className={ui.cls('listing-card__body')}>
        {priceFirst ? price : address}
        {priceFirst ? address : price}
        <p className={ui.cls('listing-card__location')}>
          {listing.postalCode} {listing.city}
          {listing.district ? ` · ${listing.district}` : ''}
        </p>
        <ul className={ui.cls('listing-card__features')}>
          <li>{listing.livingAreaM2} m²</li>
          <li>{listing.rooms} kamers</li>
          <li>Label {listing.energyLabel}</li>
          <li>{TYPE_LABEL[listing.propertyType] ?? listing.propertyType}</li>
        </ul>
        <Link to={`/woning/${listing.reference}`} className={ui.cls('listing-card__link')}
              {...ui.testId('listing-link')}>
          Bekijk woning
        </Link>
      </div>
    </>
  );

  const card = createElement(
    tag,
    {
      className: ui.cls('listing-card'),
      'data-reference': listing.reference,
      ...ui.testId('listing-card')
    },
    body
  );

  if (wrapper === 'wrapped') {
    return <div className={ui.cls('listing-card__outer')}>{card}</div>;
  }
  if (wrapper === 'double') {
    return (
      <div className={ui.cls('listing-card__outer')}>
        <div className={ui.cls('listing-card__inner')}>{card}</div>
      </div>
    );
  }
  return <>{card}</>;
}
