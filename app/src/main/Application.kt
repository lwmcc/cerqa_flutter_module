import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(AmplifyOutputs(R.raw.amplify_outputs), applicationContext)

            // TODO: log

        } catch (error: AmplifyException) {

            // TODO: log
        }
    }
}