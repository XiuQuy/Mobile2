package com.example.appxemphim;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface định nghĩa các endpoint API của TMDB.
 */
public interface TMDbApi {

    /**
     * Phương thức GET để lấy danh sách các bộ phim phổ biến.
     * @param apiKey API key được sử dụng để truy cập dịch vụ TMDB
     * @return Đối tượng gọi API để lấy phản hồi danh sách các bộ phim
     */
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);


    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getMovieVideos(
            @Path("movie_id") int movieId, // Sử dụng @Path để thêm giá trị động vào URL
            @Query("api_key") String apiKey // Sử dụng @Query để thêm tham số query vào URL
    );
}