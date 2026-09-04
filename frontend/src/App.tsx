import { Routes, Route } from 'react-router-dom'
import Accueil from './pages/Accueil'
import Inscription from './pages/Inscription'
import Connexion from './pages/Connexion'
import CGU from './pages/CGU'
import Confidentialite from './pages/Confidentialite'
import DashboardEntreprise from './pages/DashboardEntreprise'
import DashboardEtudiant from './pages/DashboardEtudiant'
import DashboardEcole from './pages/DashboardEcole'
import DashboardFreelance from './pages/DashboardFreelance'
import NotFound from './pages/NotFound'

function App() {
  return (
    <Routes>
      <Route path="/" element={<Accueil />} />
      <Route path="/inscription" element={<Inscription />} />
      <Route path="/connexion" element={<Connexion />} />
      <Route path="/cgu" element={<CGU />} />
      <Route path="/confidentialite" element={<Confidentialite />} />
      <Route path="/dashboard/entreprise" element={<DashboardEntreprise />} />
      <Route path="/dashboard/etudiant" element={<DashboardEtudiant />} />
      <Route path="/dashboard/ecole" element={<DashboardEcole />} />
      <Route path="/dashboard/freelance" element={<DashboardFreelance />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}

export default App