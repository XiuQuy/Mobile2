package com.example.appxemphim.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.CreditMovieService;
import com.example.appxemphim.data.remote.CreditTvService;
import com.example.appxemphim.data.remote.DetailMovieService;
import com.example.appxemphim.data.remote.DetailTvService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.TrailerService;
import com.example.appxemphim.data.remote.TrailerTvService;
import com.example.appxemphim.model.ProductionCompanies;
import com.example.appxemphim.ui.adapter.CastAdapter;
import com.example.appxemphim.ui.adapter.CrewAdapter;
import com.example.appxemphim.ui.adapter.ProductionCompanyAdapter;
import com.example.appxemphim.ui.adapter.SeasonAdapter;
import com.example.appxemphim.ui.viewmodel.CreditsResponse;
import com.example.appxemphim.ui.viewmodel.DetailMovieResponse;
import com.example.appxemphim.ui.viewmodel.DetailTvResponse;
import com.example.appxemphim.ui.viewmodel.TrailerResponse;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
            movieId = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("movieId")));
        }else{
            finish();
        }

        String apiKey = ServiceApiBuilder.API_KEY_TMDB;
        if (tag.equals("TMDB_MOVIE")) {
        setContentView(R.layout.activity_movie_detail);
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
            Call<DetailMovieResponse> call = apiService.getDetailMovie(movieId, apiKey);
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
                        txtLanguage.setText(language);
                        String spokenLanguage = movieDetailResponse.getFormattedSpokenLanguages();
                        txtSpokenLanguage.setText(spokenLanguage);
                        int time = movieDetailResponse.getRuntime();
                        String runtime = convertTime(time);
                        txtStat.setText(releaseDate + " • " + formattedGenres + " • " + runtime);
                        String adult = movieDetailResponse.isAdult() ? "true" : "false";
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
                        if (trailerResponse != null) {
                            String key = trailerResponse.getResults().get(0).getKey();
                            getLifecycle().addObserver(youTubePlayerView);
                            youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
                                youTubePlayer.loadVideo(key, 0);
                                youTubePlayer.pause();
                            });
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
                    if (response.isSuccessful()){
                        TrailerResponse trailerResponse=response.body();
                        String key = trailerResponse.getResults().get(0).getKey();
                        getLifecycle().addObserver(youTubePlayerView);
                        youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
                            youTubePlayer.loadVideo(key, 0);
                            youTubePlayer.pause();
                        });
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
}
