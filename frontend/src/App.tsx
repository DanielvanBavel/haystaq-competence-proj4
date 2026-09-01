import { NavLink, Navigate, Route, Routes } from 'react-router-dom';
import { Search } from './pages/Search';
import { ListingDetail } from './pages/ListingDetail';
import { Favourites } from './pages/Favourites';
import { AgencyDashboard } from './pages/AgencyDashboard';
import { useUi } from './ui/UiProfileContext';

export function App() {
  const ui = useUi();

  return (
    <div className={ui.cls('app')} data-ui-revision={ui.profile.revision}>
      <header className={ui.cls('topbar')}>
        <NavLink to="/" className={ui.cls('brand')}>HuisJacht</NavLink>
        <nav>
          <NavLink to="/" end className={({ isActive }) => ui.cls('tab', isActive ? 'tab--active' : '')}>
            Aanbod
          </NavLink>
          <NavLink to="/bewaard" className={({ isActive }) => ui.cls('tab', isActive ? 'tab--active' : '')}>
            Bewaard
          </NavLink>
          <NavLink to="/makelaar" className={({ isActive }) => ui.cls('tab', isActive ? 'tab--active' : '')}>
            Makelaar
          </NavLink>
        </nav>
        <span className={ui.cls('revision')} {...ui.testId('ui-revision')}>
          UI {ui.profile.revision}: {ui.profile.name}
        </span>
      </header>

      <main>
        <Routes>
          <Route path="/" element={<Search/>}/>
          <Route path="/woning/:reference" element={<ListingDetail/>}/>
          <Route path="/bewaard" element={<Favourites/>}/>
          <Route path="/makelaar" element={<AgencyDashboard/>}/>
          <Route path="*" element={<Navigate to="/" replace/>}/>
        </Routes>
      </main>
    </div>
  );
}
