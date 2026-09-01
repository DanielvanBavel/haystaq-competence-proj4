import { useEffect, useState } from 'react';
import { Photo } from '../types';
import { useUi } from '../ui/UiProfileContext';

const FRAME_MS = 2500;

/**
 * De videorondleiding: een reeks beelden die als film wordt afgespeeld, met
 * afspeelknop, voortgangsbalk en hoofdstukken. Geen externe media nodig.
 */
export function VideoTour({ frames }: { frames: Photo[] }) {
  const ui = useUi();
  const [playing, setPlaying] = useState(false);
  const [index, setIndex] = useState(0);
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    if (!playing || frames.length === 0) {
      return;
    }
    const started = Date.now();
    // Een videospeler buffert; niet elk fragment duurt even lang.
    const duration = ui.profile.flakinessEnabled
      ? FRAME_MS * (0.7 + Math.random() * 0.6)
      : FRAME_MS;
    const timer = window.setInterval(() => {
      const elapsed = Date.now() - started;
      const ratio = Math.min(1, elapsed / duration);
      setProgress(ratio);
      if (ratio >= 1) {
        setProgress(0);
        setIndex((current) => {
          const next = current + 1;
          if (next >= frames.length) {
            setPlaying(false);
            return 0;
          }
          return next;
        });
      }
    }, 100);
    return () => window.clearInterval(timer);
  }, [playing, index, frames.length, ui.profile.flakinessEnabled]);

  if (frames.length === 0) {
    return null;
  }

  const frame = frames[index];
  const total = ((index + progress) / frames.length) * 100;

  return (
    <div className={ui.cls('tour')} {...ui.testId('video-tour')}>
      <div className={ui.cls('tour__stage')}>
        <img src={`${frame.url}?width=1000`} alt={`Rondleiding: ${frame.caption}`}/>
        {!playing ? (
          <button type="button" className={ui.cls('tour__play')} onClick={() => setPlaying(true)}
                  {...ui.testId('tour-play')}>
            Rondleiding starten
          </button>
        ) : (
          <button type="button" className={ui.cls('tour__pause')} onClick={() => setPlaying(false)}
                  {...ui.testId('tour-pause')}>
            Pauzeren
          </button>
        )}
      </div>
      <div className={ui.cls('tour__progress')} {...ui.testId('tour-progress')}>
        <span style={{ width: `${total}%` }}/>
      </div>
      <p className={ui.cls('tour__chapter')} {...ui.testId('tour-chapter')}>
        {index + 1}/{frames.length} — {frame.room}
      </p>
    </div>
  );
}
