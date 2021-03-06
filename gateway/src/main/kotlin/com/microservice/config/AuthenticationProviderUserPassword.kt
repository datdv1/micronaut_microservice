package com.microservice.config

import com.microservice.fetcher.UsernameFetcher
import com.microservice.model.UserModel
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import org.reactivestreams.Publisher
import javax.inject.Singleton

@Singleton
class AuthenticationProviderUserPassword(
    private val usernameFetcher: UsernameFetcher
): AuthenticationProvider {



    override fun authenticate(
        httpRequest: HttpRequest<*>?,
        authenticationRequest: AuthenticationRequest<*, *>?
    ): Publisher<AuthenticationResponse> {
        val userName = authenticationRequest?.identity.toString()
        val password = authenticationRequest?.secret.toString()

        val userModel: UserModel = usernameFetcher.getByUsername(userName)

        return Flowable.create({ emitter: FlowableEmitter<AuthenticationResponse> ->
            if (userName == userModel.userName && password == userModel.password) {
                emitter.onNext(UserDetails(userName, arrayListOf("ROLE_ADMIN")))
                emitter.onComplete()
            } else {
                emitter.onError(AuthenticationException(AuthenticationFailed()))
            }
        }, BackpressureStrategy.ERROR)
    }
}