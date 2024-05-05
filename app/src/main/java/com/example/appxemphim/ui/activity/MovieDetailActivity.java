package com.example.appxemphim.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.CreditMovieService;
import com.example.appxemphim.data.remote.CreditTvService;
import com.example.appxemphim.data.remote.DetailMovieService;
import com.example.appxemphim.data.remote.DetailTvService;
import com.example.appxemphim.data.remote.ReviewVideoService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.TrailerService;
import com.example.appxemphim.data.remote.TrailerTvService;
import com.example.appxemphim.data.remote.YoutubeService;
import com.example.appxemphim.model.History;
import com.example.appxemphim.model.InformationMovie;
import com.example.appxemphim.model.MovieResponse;
import com.example.appxemphim.model.ProductionCompanies;
import com.example.appxemphim.model.ReviewVideo;
import com.example.appxemphim.model.YoutubeVideoItem;
import com.example.appxemphim.model.YoutubeVideoResponse;
import com.example.appxemphim.ui.adapter.CastAdapter;
import com.example.appxemphim.ui.adapter.CrewAdapter;
import com.example.appxemphim.ui.adapter.MoreTrailerAdapter;
import com.example.appxemphim.ui.adapter.ProductionCompanyAdapter;
import com.example.appxemphim.ui.adapter.ReviewVideoAdapter;
import com.example.appxemphim.ui.adapter.SeasonAdapter;
import com.example.appxemphim.ui.viewmodel.CreditsResponse;
import com.example.appxemphim.ui.viewmodel.DetailMovieResponse;
import com.example.appxemphim.ui.viewmodel.DetailTvResponse;
import com.example.appxemphim.ui.viewmodel.Trailer;
import com.example.appxemphim.ui.viewmodel.TrailerResponse;
import com.example.appxemphim.util.AddHistoryLoader;
import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = "MovieDetailActivity";
    private ImageView imgDetail;
    private TextView txtNameMovie, txtStat, txtAdult, txtDescription, txtLanguage, txtSpokenLanguage, txtStatus, txtVote, txtPopularity, txtBudget, txtRevenue, txtHomepage, txtFirstAirDay,txtEpisodeRunTime;
    private YouTubePlayerView youTubePlayerView;
    private RecyclerView production_companies, recyclerViewCast, recyclerViewCrew,recyclerSeason;
    private RatingBar rb;
    private RecyclerView recyclerViewMoreVideo, recyclerViewReviewVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String tag = "TMDB_MOVIE";
        int movieId = 76479;

        // Nhận Intent
        Intent intent = getIntent();
        // Nhận dữ liệu từ Intent
        if (intent != null) {
            tag = intent.getStringExtra("tag");
            String movieIdString = intent.getStringExtra("movieId");
            movieId = Integer.parseInt(Objects.requireNonNull(movieIdString));
            String title = intent.getStringExtra("title");
            String imgLink = intent.getStringExtra("imgLink");
            //Call method saveHistory here
            saveHistory(movieIdString, title, tag, imgLink);
        }else{
            finish();
        }

        String apiKey = ServiceApiBuilder.API_KEY_TMDB;
        if (tag.equals("TMDB_MOVIE")) {
        setContentView(R.layout.activity_movie_detail);
        ImageView btnBack = findViewById(R.id.imgBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });
        recyclerViewMoreVideo = findViewById(R.id.recyclerViewMoreVideo);
        recyclerViewReviewVideo = findViewById(R.id.recyclerViewReviewVideo);
        loadReviewVideo(String.valueOf(movieId));

        txtNameMovie = findViewById(R.id.txtNameMovie);
        imgDetail = findViewById(R.id.imgDetail);
        txtStat = findViewById(R.id.txtStat);
        txtDescription = findViewById(R.id.txtDescription);
        txtLanguage = findViewById(R.id.txtLanguage);
        txtSpokenLanguage = findViewById(R.id.txtSpokenLanguage);
        txtAdult = findViewById(R.id.txtAdult);
        youTubePlayerView = findViewById(R.id.youtubePlayer);
        txtStatus = findViewById(R.id.txtStatus);
        production_companies = findViewById(R.id.list_view_companies);
        txtVote = findViewById(R.id.txtVote);
        rb = findViewById(R.id.ratingBar);
        txtPopularity = findViewById(R.id.txtPopularity);
        txtBudget = findViewById(R.id.txtBudget);
        txtRevenue = findViewById(R.id.txtRevenue);
        txtHomepage = findViewById(R.id.txtHomepage);
        recyclerViewCast = findViewById(R.id.recyclerViewCast);
        recyclerViewCrew = findViewById(R.id.recyclerViewCrew);

            Log.d("Báo tag","tag là movie");

            DetailMovieService apiService = ServiceApiBuilder.buildTMDBService(DetailMovieService.class);

            String selectedLanguage = LanguageManager.getSelectedLanguage(this);
            Call<DetailMovieResponse> call = apiService.getDetailMovie(movieId,selectedLanguage, apiKey);
            TrailerService service = ServiceApiBuilder.buildTMDBService(TrailerService.class);
            Call<TrailerResponse> call1 = service.getTrailers(movieId, apiKey);
            CreditMovieService credit = ServiceApiBuilder.buildTMDBService(CreditMovieService.class);
            Call<CreditsResponse> call2 = credit.getMovieCredits(movieId, apiKey);
            call.enqueue(new Callback<DetailMovieResponse>() {
                @Override
                public void onResponse(Call<DetailMovieResponse> call, Response<DetailMovieResponse> response) {
                    if (response.isSuccessful()) {
                        DetailMovieResponse movieDetailResponse = response.body();

                        String posterPath = movieDetailResponse.getPosterPath();
                        Picasso.get().load("https://image.tmdb.org/t/p/w500" + posterPath).into(imgDetail);
                        String releaseDate = movieDetailResponse.getReleaseDate();
                        String movieTitle = movieDetailResponse.getTitle();
                        txtNameMovie.setText(movieTitle);

                        String formattedGenres = movieDetailResponse.getFormattedGenres();
                        String overview = movieDetailResponse.getOverview();
                        txtDescription.setText(overview);
                        String language = movieDetailResponse.getOriginalLanguage();
                        txtLanguage.setText(new Locale(language).getDisplayLanguage(getResources().getConfiguration().getLocales().get(0)));
                        String spokenLanguage = movieDetailResponse.getFormattedSpokenLanguages();
                        txtSpokenLanguage.setText(spokenLanguage);
                        int time = movieDetailResponse.getRuntime();
                        String runtime = convertTime(time);
                        txtStat.setText(releaseDate + " • " + formattedGenres + " • " + runtime);
                        String adult = movieDetailResponse.isAdult() ? getString(R.string.yes) : getString(R.string.no);
                        txtAdult.setText(adult);
                        String status = movieDetailResponse.getStatus();
                        txtStatus.setText(status);
                        production_companies.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this));
                        List<DetailMovieResponse.ProductionCompany> pc = movieDetailResponse.getProductionCompanies();
                        List<ProductionCompanies> productionCompanies = convertToProductionCompanies(pc);
                        ProductionCompanyAdapter adapter = new ProductionCompanyAdapter(MovieDetailActivity.this, productionCompanies);
                        adapter = new ProductionCompanyAdapter(MovieDetailActivity.this, productionCompanies);
                        production_companies.setAdapter(adapter);
//
                        txtVote.setText("Vote average: " + movieDetailResponse.getVoteAverage() + " - " + "Vote count: " + movieDetailResponse.getVoteCount());
                        float rating = (float) (movieDetailResponse.getVoteAverage() / 2);
                        rb.setRating(rating);
                        txtPopularity.setText(String.valueOf(movieDetailResponse.getPopularity()));
                        DecimalFormat decimalFormat = new DecimalFormat("#,###");
                        String formattedBudget = decimalFormat.format(movieDetailResponse.getBudget());
                        String formattedRevenue = decimalFormat.format(movieDetailResponse.getRevenue());
                        txtBudget.setText(formattedBudget + " $");
                        txtRevenue.setText(formattedRevenue + " $");
                        txtHomepage.setText(movieDetailResponse.getHomepage());

                    } else {
                        // Xử lý khi gặp lỗi
                    }
                }

                @Override
                public void onFailure(Call<DetailMovieResponse> call, Throwable t) {
                    // Xử lý khi gặp lỗi
                }
            });
            call1.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    if (response.isSuccessful()) {
                        TrailerResponse trailerResponse = response.body();
                        if (trailerResponse != null && !trailerResponse.getResults().isEmpty()) {
                            String key = trailerResponse.getResults().get(0).getKey();
                            getLifecycle().addObserver(youTubePlayerView);
                            youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
                                youTubePlayer.loadVideo(key, 0);
                                youTubePlayer.pause();
                            });
                            loadListVideoTrailerMovie(trailerResponse.getResults());
                        }
                    } else {
                        // Xử lý khi gặp lỗi

                    }
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {

                }
            });
            call2.enqueue((new Callback<CreditsResponse>() {
                @Override
                public void onResponse(Call<CreditsResponse> call, Response<CreditsResponse> response) {
                    if (response.isSuccessful()) {
                        CreditsResponse creditsResponse = response.body();
                        List<CreditsResponse.Cast> casts = creditsResponse.getCast();
                        CastAdapter castAdapter = new CastAdapter(MovieDetailActivity.this, casts);
                        recyclerViewCast.setAdapter(castAdapter);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewCast.setLayoutManager(layoutManager);
                        List<CreditsResponse.Crew> crews = creditsResponse.getCrew();
                        CrewAdapter crewAdapter = new CrewAdapter(MovieDetailActivity.this, crews);
                        recyclerViewCrew.setAdapter(crewAdapter);
                        LinearLayoutManager layoutManager1 = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewCrew.setLayoutManager(layoutManager1);
                    }
                }

                @Override
                public void onFailure(Call<CreditsResponse> call, Throwable t) {

                }
            }));
        } else {
            setContentView(R.layout.activity_tv_detail);
            recyclerViewMoreVideo = findViewById(R.id.recyclerViewMoreVideo);
            recyclerViewReviewVideo = findViewById(R.id.recyclerViewReviewVideo);
            loadReviewVideo(String.valueOf(movieId));

            imgDetail=findViewById(R.id.imgDetail);
            txtNameMovie=findViewById(R.id.txtNameMovie);
            txtStat=findViewById(R.id.txtStat);
            rb=findViewById(R.id.ratingBar);
            txtVote=findViewById(R.id.txtVote);
            txtStatus=findViewById(R.id.txtStatus);
            txtAdult=findViewById(R.id.txtAdult);
            txtFirstAirDay=findViewById(R.id.txtFirstAirDay);
            txtDescription=findViewById(R.id.txtDescription);
            txtSpokenLanguage=findViewById(R.id.txtSpokenLanguage);
            txtLanguage=findViewById(R.id.txtLanguage);
            youTubePlayerView=findViewById(R.id.youtubePlayer);
            recyclerViewCrew=findViewById(R.id.recyclerViewCrew);
            recyclerViewCast=findViewById(R.id.recyclerViewCast);
            production_companies=findViewById(R.id.list_view_companies);
            txtEpisodeRunTime=findViewById(R.id.txtEpisodeRunTime);
            txtPopularity=findViewById(R.id.txtPopularity);
            txtHomepage=findViewById(R.id.txtHomepage);
            recyclerSeason=findViewById(R.id.recyclerSeason);
            Log.d("Báo tag","tag là tv");
            DetailTvService apiService = ServiceApiBuilder.buildTMDBService(DetailTvService.class);
            Call<DetailTvResponse> call = apiService.getDetailTv(movieId, apiKey);
            TrailerTvService service = ServiceApiBuilder.buildTMDBService(TrailerTvService.class);
            Call<TrailerResponse> call1 = service.getTrailers(movieId, apiKey);
            CreditTvService credit = ServiceApiBuilder.buildTMDBService(CreditTvService.class);
            Call<CreditsResponse> call2 = credit.getTvCredits(movieId, apiKey);
            call.enqueue(new Callback<DetailTvResponse>() {
                @Override
                public void onResponse(Call<DetailTvResponse> call, Response<DetailTvResponse> response) {
                    if (response.isSuccessful()) {
                        DetailTvResponse tvDetailResponse = response.body();
                        Picasso.get().load("https://image.tmdb.org/t/p/w500" + tvDetailResponse.getPosterPath()).into(imgDetail);
                        txtNameMovie.setText(tvDetailResponse.getName());
                        txtStat.setText(tvDetailResponse.getFormattedGenres());
                        rb.setRating(tvDetailResponse.getVoteAverage()/2);
                        txtVote.setText("Vote average: "+ tvDetailResponse.getVoteAverage()+" - "+"Vote count: "+tvDetailResponse.getVoteCount());
                        txtStatus.setText(tvDetailResponse.getStatus());
                        txtAdult.setText(String.valueOf(tvDetailResponse.isAdult()));
                        txtFirstAirDay.setText(tvDetailResponse.getFirstAirDate());
                        txtDescription.setText(tvDetailResponse.getOverview());
                        txtSpokenLanguage.setText(tvDetailResponse.getFormattedSpokenLanguages());
                        txtLanguage.setText(tvDetailResponse.getOriginalLanguage());
                        production_companies.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this));
                        List<DetailTvResponse.ProductionCompany> pc = tvDetailResponse.getProductionCompanies();
                        List<ProductionCompanies> productionCompanies = convertToProductionCompaniesTv(pc);
                        ProductionCompanyAdapter adapter = new ProductionCompanyAdapter(MovieDetailActivity.this, productionCompanies);
                        adapter = new ProductionCompanyAdapter(MovieDetailActivity.this, productionCompanies);
                        production_companies.setAdapter(adapter);
                        txtPopularity.setText(String.valueOf(tvDetailResponse.getPopularity()));
                        txtHomepage.setText(tvDetailResponse.getHomepage());
                        List<DetailTvResponse.Season> seasons = tvDetailResponse.getSeasons();
                        SeasonAdapter seasonAdapter = new SeasonAdapter(seasons);
                        recyclerSeason.setAdapter(seasonAdapter);
                        LinearLayoutManager layoutManager1 = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.VERTICAL, false);
                        recyclerSeason.setLayoutManager(layoutManager1);
                        txtEpisodeRunTime.setText(String.valueOf(tvDetailResponse.getFirstEpisodeRunTime())+" minutes");
                    }
                }

                @Override
                public void onFailure(Call<DetailTvResponse> call, Throwable t) {

                }
            });
            call1.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().getResults().isEmpty()){
                        TrailerResponse trailerResponse=response.body();
                        String key = trailerResponse.getResults().get(0).getKey();
                        getLifecycle().addObserver(youTubePlayerView);
                        youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
                            youTubePlayer.loadVideo(key, 0);
                            youTubePlayer.pause();
                        });
                        loadListVideoTrailerMovie(trailerResponse.getResults());
                    }

                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {

                }
            });
            call2.enqueue((new Callback<CreditsResponse>() {
                @Override
                public void onResponse(Call<CreditsResponse> call, Response<CreditsResponse> response) {
                    if(response.isSuccessful()){
                        CreditsResponse creditsResponse= response.body();

                        List<CreditsResponse.Crew> crews = creditsResponse.getCrew();
                        CrewAdapter crewAdapter = new CrewAdapter(MovieDetailActivity.this, crews);
                        recyclerViewCrew.setAdapter(crewAdapter);
                        LinearLayoutManager layoutManager1 = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewCrew.setLayoutManager(layoutManager1);
                        List<CreditsResponse.Cast> casts = creditsResponse.getCast();
                        CastAdapter castAdapter = new CastAdapter(MovieDetailActivity.this, casts);
                        recyclerViewCast.setAdapter(castAdapter);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewCast.setLayoutManager(layoutManager);
                    }
                }

                @Override
                public void onFailure(Call<CreditsResponse> call, Throwable t) {

                }
            }));
        }
    }

    public String convertTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        String formattedTime = hours + "h" + minutes + "m";
        return formattedTime;
    }
    private List<ProductionCompanies> convertToProductionCompanies(List<DetailMovieResponse.ProductionCompany> companies) {
        List<ProductionCompanies> productionCompanies = new ArrayList<>();
        for (DetailMovieResponse.ProductionCompany company : companies) {
            ProductionCompanies newCompany = new ProductionCompanies(0, null, null, null);
            newCompany.setId(company.getId());
            newCompany.setLogoPath(company.getLogoPath());
            newCompany.setName(company.getName());
            newCompany.setOriginCountry(company.getOriginCountry());
            productionCompanies.add(newCompany);
        }
        return productionCompanies;
    }
    private List<ProductionCompanies> convertToProductionCompaniesTv(List<DetailTvResponse.ProductionCompany> companies) {
        List<ProductionCompanies> productionCompanies = new ArrayList<>();
        for (DetailTvResponse.ProductionCompany company : companies) {
            ProductionCompanies newCompany = new ProductionCompanies(0, null, null, null);
            newCompany.setId(company.getId());
            newCompany.setLogoPath(company.getLogoPath());
            newCompany.setName(company.getName());
            newCompany.setOriginCountry(company.getOriginCountry());
            productionCompanies.add(newCompany);
        }
        return productionCompanies;
    }
    public static void sendIntent(Context context, String movieId, String tag, String title, String imgLink){
        Intent intent =  new Intent(context, MovieDetailActivity.class);
        intent.putExtra("movieId", movieId);
        intent.putExtra("tag", tag);
        intent.putExtra("title", title);
        intent.putExtra("imgLink", imgLink);
        context.startActivity(intent);
    }
    LoaderManager.LoaderCallbacks<Void> loaderAddHistory =  new LoaderManager.LoaderCallbacks<Void>() {
        @NonNull
        @Override
        public Loader<Void> onCreateLoader(int id, @Nullable Bundle args) {
            assert args != null;
            String historyJson = args.getString("historyJson");
            Gson json = new Gson();
            History history = json.fromJson(historyJson, History.class);
            return new AddHistoryLoader(MovieDetailActivity.this, history);
        }
        @Override
        public void onLoadFinished(@NonNull Loader<Void> loader, Void data) {}
        @Override
        public void onLoaderReset(@NonNull Loader<Void> loader) {}
    };

    private void saveHistory(String movieId, String title, String tag, String imgLink){
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(calendar.getTime());

        InformationMovie informationMovie = new InformationMovie();
        informationMovie.setMovieId(movieId);
        informationMovie.setTitle(title);
        informationMovie.setTag(tag);
        informationMovie.setImageLink(imgLink);

        History history = new History();
        history.setUserId(sharedPreferences.getInt("userId", -1));
        history.setSecondsCount(0);
        history.setWatchedDate(formattedDate);
        history.setInformationMovie(informationMovie);

        Gson gson = new Gson();
        String historyJson = gson.toJson(history);
        Bundle bundle = new Bundle();
        bundle.putString("historyJson", historyJson);
        LoaderManager.getInstance(this).restartLoader(3, bundle, loaderAddHistory);
    }

    private void loadListVideoTrailerMovie(List<Trailer> trailerList){
        ArrayList<String> arrId = new ArrayList<>();
        for(Trailer trailer: trailerList){
            if(trailer.getSite().equals("YouTube")){
                arrId.add(trailer.getKey());
            }
        }
        getVideoYoutubeInfo(arrId.toArray(new String[0]));
    }

    public void getVideoYoutubeInfo(String[] videoId) {
        String[] part = {"snippet", "statistics", "contentDetails"};
        YoutubeService youtubeService = ServiceApiBuilder.buildYoutubeApiService(YoutubeService.class);
        Call<YoutubeVideoResponse> call = youtubeService.getVideoInfo(
                part,
                videoId,
                ServiceApiBuilder.API_KEY_YOUTUBE_DATA
        );
        call.enqueue(new Callback<YoutubeVideoResponse>() {
            @Override
            public void onResponse(Call<YoutubeVideoResponse> call, Response<YoutubeVideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<YoutubeVideoItem> items = response.body().getItems();
                    if (items != null && !items.isEmpty()) {
                        MoreTrailerAdapter moreTrailerAdapter = new MoreTrailerAdapter(MovieDetailActivity.this, items);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewMoreVideo.setLayoutManager(layoutManager);
                        recyclerViewMoreVideo.setAdapter(moreTrailerAdapter);
                    }
                } else {
                    Toast.makeText(MovieDetailActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<YoutubeVideoResponse> call, Throwable t) {
                Log.e("API_ERROR", "Network error", t);
            }
        });
    }

    public void loadReviewVideo(String movieId){
        ReviewVideoService reviewVideoService = ServiceApiBuilder.buildUserApiService(ReviewVideoService.class);
        Call<List<ReviewVideo>> call =  reviewVideoService.getAllReviewVideoByMovieId(movieId);
        call.enqueue(new Callback<List<ReviewVideo>>() {
            @Override
            public void onResponse(Call<List<ReviewVideo>> call, Response<List<ReviewVideo>> response) {
                if(response.isSuccessful() && response.body() != null && !response.body().isEmpty()){
                    List<String> listVideoId = new ArrayList<>();
                    for (ReviewVideo reviewVideo : response.body()){
                        listVideoId.add(reviewVideo.getInformationReviewVideo().getMovieId());
                    }
                    getVideoYoutubeReviewVideo(listVideoId.toArray(new String[0]));
                }else {
                    findViewById(R.id.tv_review_video).setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<ReviewVideo>> call, Throwable t) {

            }
        });
    }

    public void getVideoYoutubeReviewVideo(String[] videoId) {
        String[] part = {"snippet", "statistics", "contentDetails"};
        YoutubeService youtubeService = ServiceApiBuilder.buildYoutubeApiService(YoutubeService.class);
        Call<YoutubeVideoResponse> call = youtubeService.getVideoInfo(
                part,
                videoId,
                ServiceApiBuilder.API_KEY_YOUTUBE_DATA
        );
        call.enqueue(new Callback<YoutubeVideoResponse>() {
            @Override
            public void onResponse(Call<YoutubeVideoResponse> call, Response<YoutubeVideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<YoutubeVideoItem> items = response.body().getItems();
                    if (items != null && !items.isEmpty()) {
                        ReviewVideoAdapter reviewVideoAdapter = new ReviewVideoAdapter(MovieDetailActivity.this, items);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewReviewVideo.setLayoutManager(layoutManager);
                        recyclerViewReviewVideo.setAdapter(reviewVideoAdapter);
                    }
                } else {
                    Toast.makeText(MovieDetailActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<YoutubeVideoResponse> call, Throwable t) {
                Log.e("API_ERROR", "Network error", t);
            }
        });
    }




}
