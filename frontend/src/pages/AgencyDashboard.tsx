import { useCallback, useEffect, useState } from 'react';
import { api, ApiError } from '../api';
import { Viewing } from '../types';
import { useUi } from '../ui/UiProfileContext';

export function AgencyDashboard() {
  const ui = useUi();
  const [viewings, setViewings] = useState<Viewing[]>([]);
  const [message, setMessage] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      setViewings(await api.get<Viewing[]>('/viewings'));
    } catch (problem) {
      setMessage(problem instanceof ApiError ? problem.message : String(problem));
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  async function decide(viewing: Viewing, action: string) {
    setMessage(null);
    try {
      await api.post(`/viewings/${viewing.id}/decide`, { action });
      await load();
    } catch (problem) {
      setMessage(problem instanceof ApiError ? problem.message : String(problem));
    }
  }

  return (
    <section>
      <h1>Bezichtigingsaanvragen</h1>
      <button type="button" className={ui.cls('button', 'button--ghost')} onClick={() => void load()}>
        Verversen
      </button>
      {message ? <p className={ui.cls('error')} {...ui.testId('dashboard-message')}>{message}</p> : null}

      <table className={ui.cls('viewings')} {...ui.testId('viewing-table')}>
        <thead>
        <tr>
          <th>Woning</th>
          <th>Aanvrager</th>
          <th>Wanneer</th>
          <th>Status</th>
          <th/>
        </tr>
        </thead>
        <tbody>
        {viewings.map((viewing) => (
          <tr key={viewing.id}>
            <td>{viewing.listingReference}<div className={ui.cls('muted')}>{viewing.listingAddress}</div></td>
            <td>{viewing.name}<div className={ui.cls('muted')}>{viewing.email}</div></td>
            <td>{viewing.date} {viewing.slot}</td>
            <td {...ui.testId('viewing-status')}>{viewing.status}</td>
            <td className={ui.cls('viewings__actions')}>
              {viewing.status === 'AANGEVRAAGD' ? (
                <>
                  <button type="button" onClick={() => void decide(viewing, 'bevestigen')}>Bevestigen</button>
                  <button type="button" className={ui.cls('button', 'button--ghost')}
                          onClick={() => void decide(viewing, 'afwijzen')}>
                    Afwijzen
                  </button>
                </>
              ) : null}
            </td>
          </tr>
        ))}
        </tbody>
      </table>

      {viewings.length === 0 ? <p className={ui.cls('muted')}>Nog geen aanvragen.</p> : null}
    </section>
  );
}
