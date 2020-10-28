package com.piso12.indoorex.services.retrofit

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.piso12.indoorex.helpers.ContextHelper
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class RefreshTokenAuthenticator : Authenticator {

    private var mContext: Context

    constructor(context: Context) {
        mContext = context
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        var task = FirebaseAuth.getInstance().currentUser!!.getIdToken(true)
        var tokenResult = Tasks.await(task)
        ContextHelper.setFirebaseJwtToken(mContext, tokenResult.token)
        return newRequestWithAccessToken(response.request(), tokenResult.token)
    }

    private fun newRequestWithAccessToken(request: Request, accessToken: String?): Request {
        return request
            .newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }

}