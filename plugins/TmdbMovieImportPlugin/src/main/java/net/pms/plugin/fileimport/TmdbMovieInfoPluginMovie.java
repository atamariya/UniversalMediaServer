package net.pms.plugin.fileimport;

import com.omertron.themoviedbapi.model.movie.MovieInfo;

public class TmdbMovieInfoPluginMovie {
	private MovieInfo movie;

	public TmdbMovieInfoPluginMovie(MovieInfo movie) {
		this.movie = movie;
	}

	public MovieInfo getMovie() {
		return movie;
	}

	@Override
	public String toString() {
		String res = "";
		if (movie != null) {
			String yearString = "";
			if (movie.getReleaseDate() != null) {
				yearString = String.format(" (%s)", movie.getReleaseDate().substring(0, 4));
			}
			res = String.format("%s%s", movie.getTitle(), yearString);
		}
		return res;
	}
}
