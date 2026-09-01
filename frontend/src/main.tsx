import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { App } from './App';
import { UiProfileProvider } from './ui/UiProfileContext';
import './styles.css';

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <BrowserRouter>
      <UiProfileProvider>
        <App/>
      </UiProfileProvider>
    </BrowserRouter>
  </React.StrictMode>
);
