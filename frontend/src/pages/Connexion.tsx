import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import Header from '../components/Header'

const connexionSchema = z.object({
  email: z.string().email('Adresse email invalide'),
  motDePasse: z.string().min(1, 'Le mot de passe est requis'),
})

type ConnexionForm = z.infer<typeof connexionSchema>

export default function Connexion() {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<ConnexionForm>({
    resolver: zodResolver(connexionSchema),
  })

  const onSubmit = (data: ConnexionForm) => {
    // TODO : brancher sur l'API backend une fois Supabase configuré
    console.log('Connexion :', data)
    alert('Backend pas encore branché — vérifie la console pour voir les données du formulaire.')
  }

  return (
    <div className="min-h-screen bg-white text-slate-900">
      <Header />

      <div className="mx-auto max-w-md px-6 py-16">
        <h1 className="text-2xl font-bold text-slate-900">Connexion</h1>
        <p className="mt-2 text-slate-600">Accédez à votre espace Talent d'Afrique.</p>

        <form onSubmit={handleSubmit(onSubmit)} className="mt-8 space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-700">Email</label>
            <input
              type="email"
              {...register('email')}
              className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            />
            {errors.email && <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700">Mot de passe</label>
            <input
              type="password"
              {...register('motDePasse')}
              className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            />
            {errors.motDePasse && (
              <p className="mt-1 text-sm text-red-600">{errors.motDePasse.message}</p>
            )}
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full rounded-lg bg-blue-700 py-2.5 font-medium text-white hover:bg-blue-800 disabled:opacity-50"
          >
            Se connecter
          </button>
        </form>
      </div>
    </div>
  )
}