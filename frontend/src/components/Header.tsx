import { Link } from 'react-router-dom'

export default function Header() {
  return (
    <header className="border-b border-slate-100">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
        <Link to="/" className="text-xl font-bold text-blue-700">
          Talent<span className="text-orange-500">Afrique</span>
        </Link>
        <nav className="hidden gap-6 text-sm font-medium text-slate-600 sm:flex">
          <a href="#" className="hover:text-blue-700">Offres</a>
          <a href="#" className="hover:text-blue-700">Entreprises</a>
          <a href="#" className="hover:text-blue-700">À propos</a>
        </nav>
        <div className="flex gap-3">
          <Link
            to="/connexion"
            className="rounded-lg px-4 py-2 text-sm font-medium text-blue-700 hover:bg-blue-50"
          >
            Connexion
          </Link>
          <Link
            to="/inscription"
            className="rounded-lg bg-orange-500 px-4 py-2 text-sm font-medium text-white hover:bg-orange-600"
          >
            Créer un compte
          </Link>
        </div>
      </div>
    </header>
  )
}