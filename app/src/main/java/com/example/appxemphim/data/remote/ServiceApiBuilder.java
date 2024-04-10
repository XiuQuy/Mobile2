package com.example.appxemphim.data.remote;

import android.os.Build;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceApiBuilder {
    private static final String URL_TMDB = "https://api.themoviedb.org/3/";
    private static final String URL_USER_API = "https://nhom17movieappapi.azurewebsites.net/";

    // Create logger
    private static final HttpLoggingInterceptor logger =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    // Create OkHttp Client
    private static final OkHttpClient.Builder okHttp =
            new OkHttpClient.Builder()
                    .readTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();

                            request = request.newBuilder()
                                    .addHeader("x-device-type", Build.DEVICE)
                                    .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                                    .build();

                            return chain.proceed(request);
                        }
                    })
                    .addInterceptor(logger);

    private static final Retrofit.Builder builderTMDB = new Retrofit.Builder().baseUrl(URL_TMDB)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build());
    private static final Retrofit.Builder builderUserApi = new Retrofit.Builder().baseUrl(URL_USER_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build());


    public static <S> S buildTMDBService(Class<S> serviceType) {
        return builderTMDB.build().create(serviceType);
    }

    public static <S> S buildUserApiService(Class<S> serviceType) {
        return builderUserApi.build().create(serviceType);
    }
}
