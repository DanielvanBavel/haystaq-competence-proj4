export interface UiProfile {
  revision: number;
  name: string;
  summary: string;
  tokens: Record<string, string>;
  filterOrder: string[];
  driftEnabled: boolean;
  intervalMinutes: number;
  releasedAt: string;
  nextReleaseAt: string | null;
  totalRevisions: number;
  flakinessEnabled: boolean;
}

export interface ListingSummary {
  id: string;
  reference: string;
  street: string;
  houseNumber: string;
  postalCode: string;
  city: string;
  district: string | null;
  price: number;
  status: string;
  propertyType: string;
  livingAreaM2: number;
  plotAreaM2: number | null;
  rooms: number;
  bedrooms: number;
  buildYear: number;
  energyLabel: string;
  hasGarden: boolean;
  hasBalcony: boolean;
  hasParking: boolean;
  pricePerSquareMetre: number;
  photoCount: number;
  coverPhotoUrl: string | null;
  publishedAt: string;
}

export interface Photo {
  id: string;
  position: number;
  room: string;
  caption: string;
  inTour: boolean;
  url: string;
}

export interface Agency {
  id: string;
  name: string;
  city: string;
  phone: string;
  email: string;
  brandHue: number;
}

export interface ListingDetail {
  summary: ListingSummary;
  description: string;
  serviceCosts: number | null;
  latitude: number;
  longitude: number;
  agency: Agency | null;
  photos: Photo[];
  tour: Photo[];
}

export interface SearchResult {
  results: ListingSummary[];
  totalResults: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface Slot {
  slot: string;
  available: boolean;
  reason: string | null;
}

export interface Viewing {
  id: string;
  listingReference: string | null;
  listingAddress: string | null;
  name: string;
  email: string;
  phone: string | null;
  date: string;
  slot: string;
  message: string | null;
  status: string;
  createdAt: string;
}
