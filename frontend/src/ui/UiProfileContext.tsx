import { createContext, ReactNode, useContext, useEffect, useMemo, useState } from 'react';
import { api } from '../api';
import { UiProfile } from '../types';

const FALLBACK: UiProfile = {
  revision: 1,
  name: '2026.1 basis',
  summary: '',
  tokens: {
    testIdMode: 'stable',
    classSalt: '',
    cardTag: 'article',
    cardWrapper: 'none',
    searchButtonLabel: 'Zoeken',
    searchInputId: 'zoekterm',
    priceMaxId: 'prijs-max',
    viewingButtonLabel: 'Bezichtiging aanvragen',
    favouriteLabel: 'Bewaren',
    priceFormat: 'euro-symbol',
    galleryLayout: 'grid',
    resultCountText: '{n} woningen gevonden'
  },
  filterOrder: ['plaats', 'prijs', 'kamers', 'type', 'tuin'],
  driftEnabled: false,
  intervalMinutes: 15,
  releasedAt: '',
  nextReleaseAt: null,
  totalRevisions: 5,
  flakinessEnabled: false
};

interface UiHelpers {
  profile: UiProfile;
  /** Attributen voor een element. Levert niets op als de testids weg zijn. */
  testId: (name: string) => Record<string, string>;
  /** Classnaam volgens de huidige revisie. */
  cls: (...names: string[]) => string;
  token: (key: string, fallback?: string) => string;
  formatPrice: (amount: number) => string;
  reload: () => void;
}

const Context = createContext<UiHelpers | null>(null);

function camel(value: string): string {
  return value.replace(/-([a-z])/g, (_, letter: string) => letter.toUpperCase());
}

export function UiProfileProvider({ children }: { children: ReactNode }) {
  const [profile, setProfile] = useState<UiProfile>(FALLBACK);
  const [tick, setTick] = useState(0);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      try {
        const result = await api.get<UiProfile>('/ui-profile');
        if (!cancelled) {
          setProfile(result);
        }
      } catch {
        // De applicatie blijft werken op het basisprofiel.
      }
    }

    void load();
    // Een nieuwe release komt binnen zonder dat je de pagina ververst.
    const timer = window.setInterval(load, 20_000);
    return () => {
      cancelled = true;
      window.clearInterval(timer);
    };
  }, [tick]);

  const helpers = useMemo<UiHelpers>(() => {
    const mode = profile.tokens.testIdMode ?? 'stable';
    const salt = profile.tokens.classSalt ?? '';

    return {
      profile,
      testId: (name: string) => {
        if (mode === 'absent') {
          return {};
        }
        if (mode === 'renamed') {
          return { 'data-testid': `qa-${camel(name)}` };
        }
        return { 'data-testid': name };
      },
      cls: (...names: string[]) =>
        names
          .filter(Boolean)
          .map((name) => (salt ? `${name}--${salt}` : name))
          .join(' '),
      token: (key: string, fallback = '') => profile.tokens[key] ?? fallback,
      formatPrice: (amount: number) => {
        const rounded = Math.round(amount);
        const grouped = rounded.toLocaleString('nl-NL');
        switch (profile.tokens.priceFormat) {
          case 'euro-suffix':
            return `${grouped} euro`;
          case 'plain':
            return String(rounded);
          default:
            return `€ ${grouped}`;
        }
      },
      reload: () => setTick((value) => value + 1)
    };
  }, [profile]);

  return <Context.Provider value={helpers}>{children}</Context.Provider>;
}

export function useUi(): UiHelpers {
  const helpers = useContext(Context);
  if (!helpers) {
    throw new Error('useUi buiten UiProfileProvider gebruikt');
  }
  return helpers;
}
