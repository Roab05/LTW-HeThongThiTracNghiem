import './App.css';
import { BrowserRouter } from 'react-router-dom';
import { useEffect } from 'react';
import AppRouter from './app/AppRouter';
import { AuthProvider } from './context/AuthContext';
import { ensureSeeded } from './services/db';

function App() {
  useEffect(() => {
    ensureSeeded();
  }, []);

  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRouter />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
