package com.example.appxemphim.data.remote;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Date;
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
//    private static final String URL_USER_API = "http://192.168.1.5:80/user/";

    private static final String URL_USER_API = "https://nhom17movieappapi.azurewebsites.net/";
    private static final String URL_YOUTUBE_DATA_API = "https://www.googleapis.com/youtube/v3/";
    public static final String API_KEY_TMDB = "64d0aa770962667c44935e02b31da40f";
    public static final String API_KEY_YOUTUBE_DATA = "AIzaSyBSBAJLOEJmynxkun7JBGJlPwjJTcnJQXI";

    //Create covert date
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .create();
    // Create logger
    private static final HttpLoggingInterceptor logger =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    // Create OkHttp Client
    private static final OkHttpClient.Builder okHttp =
            new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
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
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttp.build());
    private static final Retrofit.Builder builderUserApi = new Retrofit.Builder().baseUrl(URL_USER_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build());
    private static final Retrofit.Builder builderYoutubeApi = new Retrofit.Builder().baseUrl(URL_YOUTUBE_DATA_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build());
    public static <S> S buildTMDBService(Class<S> serviceType) {
        return builderTMDB.build().create(serviceType);
    }
    public static <S> S buildUserApiService(Class<S> serviceType) {
        return builderUserApi.build().create(serviceType);
    }
    public static <S> S buildYoutubeApiService(Class<S> serviceType) {
        return builderUserApi.build().create(serviceType);
    }
}
