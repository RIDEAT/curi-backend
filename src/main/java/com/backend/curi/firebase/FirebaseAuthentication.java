package com.backend.curi.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

public class FirebaseAuthentication {

    public static FirebaseToken verifyAccessToken(String accessToken) throws FirebaseAuthException {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.verifyIdToken(accessToken);
    }

}
