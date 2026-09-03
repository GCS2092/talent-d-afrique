import { useState } from 'react'

type ProfileType = 'etudiant' | 'entreprise' | 'ecole' | 'freelance'

const profiles: { id: ProfileType; label: string; description: string }[] = [
  {
    id: 'etudiant',
    label: 'Étudiant / Candidat',
    description: 'Trouvez un stage, un CDD ou un CDI adapté à votre profil.',
  },
  {
    id: 'entreprise',
    label: 'Entreprise',
    description: 'Publiez vos offres et trouvez les meilleurs profils, triés par compatibilité.',
  },
  {
    id: 'ecole',
    label: 'École',
    description: 'Orientez vos étudiants vers les opportunités les plus adaptées.',
  },
  {
    id: 'freelance',
    label: 'Freelance',
    description: 'Trouvez des missions correspondant à vos compétences et disponibilités.',
  },
]

function App() {
  const [selectedProfile, setSelectedProfile] = useState<ProfileType | null>(null)

  return (
    <div className="min-h-screen bg-white text-slate-900">
      {/* Header */}
      <header className="border-b border-slate-100">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
          <span className="text-xl font-bold text-blue-700">
            Talent<span className="text-orange-500">Afrique</span>
          </span>
          <nav className="hidden gap-6 text-sm font-medium text-slate-600 sm:flex">
            <a href="#" className="hover:text-blue-700">Offres</a>
            <a href="#" className="hover:text-blue-700">Entreprises</a>
            <a href="#" className="hover:text-blue-700">À propos</a>
          </nav>
          <div className="flex gap-3">
            <button className="rounded-lg px-4 py-2 text-sm font-medium text-blue-700 hover:bg-blue-50">
              Connexion
            </button>
            <button className="rounded-lg bg-orange-500 px-4 py-2 text-sm font-medium text-white hover:bg-orange-600">
              Créer un compte
            </button>
          </div>
        </div>
      </header>

      {/* Hero */}
      <section className="mx-auto max-w-3xl px-6 py-20 text-center">
        <h1 className="text-4xl font-bold tracking-tight text-slate-900 sm:text-5xl">
          Le bon talent, la bonne opportunité,{' '}
          <span className="text-blue-700">au bon moment</span>
        </h1>
        <p className="mt-4 text-lg text-slate-600">
          Talent d'Afrique connecte étudiants, entreprises, écoles et freelances
          grâce à un moteur de matching intelligent — fini le tri à l'aveugle.
        </p>
      </section>

      {/* Sélecteur de profil */}
      <section className="mx-auto max-w-5xl px-6 pb-24">
        <h2 className="mb-6 text-center text-sm font-semibold uppercase tracking-wide text-slate-500">
          Je suis…
        </h2>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {profiles.map((profile) => {
            const isSelected = selectedProfile === profile.id
            return (
              <button
                key={profile.id}
                onClick={() => setSelectedProfile(profile.id)}
                className={`rounded-2xl border p-5 text-left transition ${
                  isSelected
                    ? 'border-orange-500 bg-orange-50 shadow-sm'
                    : 'border-slate-200 hover:border-blue-300 hover:shadow-sm'
                }`}
              >
                <h3 className="font-semibold text-blue-700">{profile.label}</h3>
                <p className="mt-2 text-sm text-slate-600">{profile.description}</p>
              </button>
            )
          })}
        </div>

        {selectedProfile && (
          <div className="mt-8 text-center">
            <button className="rounded-lg bg-blue-700 px-6 py-3 font-medium text-white hover:bg-blue-800">
              Continuer en tant que {profiles.find((p) => p.id === selectedProfile)?.label}
            </button>
          </div>
        )}
      </section>
    </div>
  )
}

export default App