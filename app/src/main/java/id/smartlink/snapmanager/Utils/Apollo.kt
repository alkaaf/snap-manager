package id.smartlink.snapmanager.Utils

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

class Apollo {
    companion object {
        lateinit var ok: OkHttpClient
        lateinit var ap: ApolloClient
        var BASE_URL = "https://snapclean-graphql-fljnd6wana-uc.a.run.app/graphql-onta"
        fun getApollo(): ApolloClient {
            if (!::ok.isInitialized) {
                ok = OkHttpClient
                    .Builder()
                    .build()
            }
            if (!::ap.isInitialized) {
                ap = ApolloClient.builder()
                    .serverUrl(BASE_URL)
                    .okHttpClient(ok)
                    .build()
            }
            return ap
        }
    }
}