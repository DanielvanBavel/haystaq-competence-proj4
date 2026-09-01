import { useEffect, useState } from 'react';
import { Photo } from '../types';
import { useUi } from '../ui/UiProfileContext';

/**
 * De galerij is grid of carousel, afhankelijk van de revisie. In carouselmodus
 * draait hij vanzelf door: het element waar je op wilde klikken staat er een
 * seconde later niet meer.
 */
export function Gallery({ photos }: { photos: Photo[] }) {
  const ui = useUi();
  const layout = ui.token('galleryLayout', 'grid');
  const [active, setActive] = useState(0);
  const [lightbox, setLightbox] = useState<Photo | null>(null);
  const [paused, setPaused] = useState(false);

  useEffect(() => {
    if (layout !== 'carousel' || paused || photos.length < 2 || !ui.profile.flakinessEnabled) {
      return;
    }
    const timer = window.setInterval(() => {
      setActive((current) => (current + 1) % photos.length);
    }, 4000);
    return () => window.clearInterval(timer);
  }, [layout, paused, photos.length, ui.profile.flakinessEnabled]);

  if (photos.length === 0) {
    return <p className={ui.cls('muted')}>Geen foto's beschikbaar.</p>;
  }

  if (layout === 'carousel') {
    const photo = photos[active];
    return (
      <div className={ui.cls('gallery', 'gallery--carousel')} {...ui.testId('gallery')}>
        <img
          src={`${photo.url}?width=1000`}
          alt={photo.caption}
          className={ui.cls('gallery__main')}
          onClick={() => setLightbox(photo)}
        />
        <div className={ui.cls('gallery__controls')}>
          <button type="button" onClick={() => setActive((current) => (current - 1 + photos.length) % photos.length)}
                  {...ui.testId('gallery-previous')}>
            Vorige
          </button>
          <span {...ui.testId('gallery-position')}>{active + 1} / {photos.length}</span>
          <button type="button" onClick={() => setActive((current) => (current + 1) % photos.length)}
                  {...ui.testId('gallery-next')}>
            Volgende
          </button>
          <button type="button" onClick={() => setPaused((value) => !value)}>
            {paused ? 'Doorgaan' : 'Pauzeren'}
          </button>
        </div>
        <p className={ui.cls('gallery__caption')}>{photo.room} — {photo.caption}</p>
        {lightbox ? <Lightbox photo={lightbox} onClose={() => setLightbox(null)}/> : null}
      </div>
    );
  }

  return (
    <div className={ui.cls('gallery', 'gallery--grid')} {...ui.testId('gallery')}>
      {photos.map((photo) => (
        <figure key={photo.id} className={ui.cls('gallery__item')} onClick={() => setLightbox(photo)}>
          <img src={`${photo.url}?width=600`} alt={photo.caption} loading="lazy"
               className={ui.cls('gallery__thumb')}/>
          <figcaption>{photo.room}</figcaption>
        </figure>
      ))}
      {lightbox ? <Lightbox photo={lightbox} onClose={() => setLightbox(null)}/> : null}
    </div>
  );
}

function Lightbox({ photo, onClose }: { photo: Photo; onClose: () => void }) {
  const ui = useUi();
  return (
    <div className={ui.cls('lightbox')} role="dialog" aria-modal="true" {...ui.testId('lightbox')}>
      <div className={ui.cls('lightbox__inner')}>
        <img src={`${photo.url}?width=1400`} alt={photo.caption}/>
        <p>{photo.room} — {photo.caption}</p>
        <button type="button" onClick={onClose} {...ui.testId('lightbox-close')}>Sluiten</button>
      </div>
    </div>
  );
}
