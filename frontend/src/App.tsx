import { Routes, Route } from 'react-router-dom'
import Accueil from './pages/Accueil'
import Inscription from './pages/Inscription'
import Connexion from './pages/Connexion'

function App() {
  return (
    <Routes>
      <Route path="/" element={<Accueil />} />
      <Route path="/inscription" element={<Inscription />} />
      <Route path="/connexion" element={<Connexion />} />
    </Routes>
  )
}

export default App