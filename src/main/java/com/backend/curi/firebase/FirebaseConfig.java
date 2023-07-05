package com.backend.curi.firebase;


import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init(){
        try{
            GoogleCredentials credentials = GoogleCredentials.fromStream(new ClassPathResource("serviceAccountKey.json").getInputStream());

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();

            FirebaseApp.initializeApp(options);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
