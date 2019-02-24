package network

import io.ktor.client.HttpClient
import io.ktor.client.features.ExpectSuccess
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.takeFrom
import kotlinx.serialization.json.JSON
import sample.LogLevel
import sample.log

interface PhotoApi {
    suspend fun getRandom(): PhotoResponse
}

class PhotoApiService(private val clientId: String) :
    PhotoApi {
    //    val client = HttpClient { install(ExpectSuccess) }
    val endPoint = "https://api.unsplash.com"
    val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(JSON.nonstrict).apply {
                setMapper(PhotoResponse::class, PhotoResponse.serializer())
            }
        }
        install(ExpectSuccess)
    }


    val TAG = PhotoApiService::class.toString()

    override suspend fun getRandom(): PhotoResponse = client.get {
        log(LogLevel.DEBUG, TAG, "Getting random photo from API")
        apiUrl("photos/random", endPoint)
    }

    private fun HttpRequestBuilder.apiUrl(path: String, endPoint: String) {
        url {
            takeFrom(endPoint)
            encodedPath = path
            parameters["client_id"] = clientId
        }
    }
}