package net.pms.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.enumeration.SearchType;
import com.omertron.themoviedbapi.model.Genre;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.model.tv.TVBasic;
import com.omertron.themoviedbapi.model.tv.TVEpisodeInfo;
import com.omertron.themoviedbapi.results.ResultList;

import net.pms.PMS;
import net.pms.dlna.DLNAMediaInfo;

/**
 * Add movie metadata from TMDB. viz. IMDB id, title, year, thumbnail and genre.
 * 
 * @author Anand Tamariya
 *
 */
public class MovieMetadata {
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieMetadata.class);
    private static TheMovieDbApi api;
    private static Map<Integer, String> genres = new HashMap<>();
    private static Pattern pattern = Pattern.compile("s(\\d+)e(\\d+)", Pattern.CASE_INSENSITIVE);

    static {
        try {
            api = new TheMovieDbApi("4cdddc892213dd24e5011fd710f8abf0");
            ResultList<Genre> genreMovieList = api.getGenreMovieList(PMS.getLocale().getLanguage());
            for (Genre genre : genreMovieList.getResults()) {
                genres.put(genre.getId(), genre.getName());
            }
            genreMovieList = api.getGenreTVList(PMS.getLocale().getLanguage());
            for (Genre genre : genreMovieList.getResults()) {
                genres.put(genre.getId(), genre.getName());
            }
        } catch (MovieDbException ex) {
            LOGGER.debug("Error initializing TMDB: " + ex.getMessage());
            LOGGER.trace("", ex);
        }
    }
    
    public static boolean getTitle(String title, DLNAMediaInfo media) {
        boolean result = false;
        if (StringUtils.isEmpty(title))
            return result;
        
        String imdbId = ImdbUtil.extractImdb(title);
        if (!StringUtils.isEmpty(imdbId)) {
            // TMDB might not have IMDB id - use id from filename
            media.setImdbId(imdbId);
            // Remove imdbId from search string
            title = ImdbUtil.cleanName(FilenameUtils.getBaseName(title));
        }

        result = getTVTitle(title, media);
        if (!result)
            result = getMovieTitle(title, media);
        return result;
    }
    
    private static boolean getMovieTitle(String title, DLNAMediaInfo media) {
        boolean result = false;
        if (StringUtils.isEmpty(title))
            return result;
        
        try {
            title = title.replaceAll("[^A-Za-z0-9]"," ");
            // search for the title
            ResultList<MovieInfo> movies = api.searchMovie(title, 0, PMS.getLocale().getLanguage(), true, 0,
                    0, SearchType.PHRASE);
            int size = movies.getTotalResults();

            if (movies != null && size > 0) {
                // we've found at least one result
                result = true;

                // use the first one
                MovieInfo movie = movies.getResults().get(0);
//                api.getMovieInfo(movies.getResults().get(0).getId(),
//                        PMS.getLocale().getLanguage(), "casts,trailers");

                String moviesStr = String.format("Movie matched for '%s' on TMDb has id=%s, name='%s'", title,
                        movies.getResults().get(0).getId(), movies.getResults().get(0).getTitle());
                for (int j = 0; j < movies.getResults().size(); j++) {
                    MovieInfo tmpMovie = movies.getResults().get(j);
                    moviesStr += String.format("id=%s, name='%s';", tmpMovie.getId(), tmpMovie.getTitle());
                    
                    // Exact match is given preference
                    String str = tmpMovie.getTitle();
                    if (str.equalsIgnoreCase(title)) {
                        movie = tmpMovie;
                        break;
                    }
                }
                moviesStr = moviesStr.substring(0, moviesStr.length() - 2);
                LOGGER.debug(moviesStr);

                // set the polling interval to the min value and reset nbRetriesDone if we could execute the
                // query
                // currentPollingIntervalMs = MIN_POLLING_INTERVAL_MS;
                // nbRetriesDone = 0;

                media.setFileTitleFromMetadata(movie.getTitle());
                media.setYear(movie.getReleaseDate());
                String url = "http://image.tmdb.org/t/p/original" + movie.getPosterPath();
                media.setThumb(getImage(url));
                media.setGenre(getGenre(movie.getGenreIds()));
                if (movie.getImdbID() != null)
                    media.setImdbId(movie.getImdbID());
            }
        } catch (Exception e) {
            LOGGER.debug("Error while querying TMDB: " + e.getMessage());
            LOGGER.trace("", e);
        }
        
        return result;
    }
    
    private static boolean getTVTitle(String title, DLNAMediaInfo media) {
        boolean result = false;
        if (StringUtils.isEmpty(title))
            return result;
        
        try {
            // Extract season and episode number from title
            int seasonNumber = 1;
            int episodeNumber = 1;
            Matcher matcher = pattern.matcher(title);
            if (matcher.find()) {
                seasonNumber = Integer.parseInt(matcher.group(1));
                episodeNumber = Integer.parseInt(matcher.group(2));
                title = matcher.replaceAll("");
            } else {
                return result;
            }
            
            title = title.replaceAll("[^A-Za-z0-9]"," ");
            // search for the title
            ResultList<TVBasic> shows = api.searchTV(title, 0, PMS.getLocale().getLanguage(), 0, SearchType.PHRASE);
            int size = shows.getTotalResults();

            if (shows != null && size > 0) {
                // we've found at least one result
                result = true;

                // use the first one
                TVBasic show = shows.getResults().get(0);
                String moviesStr = String.format("Movie matched for '%s' on TMDb has id=%s, name='%s'", title,
                        shows.getResults().get(0).getId(), shows.getResults().get(0).getName());
                for (int j = 0; j < shows.getResults().size(); j++) {
                    TVBasic tmpMovie = shows.getResults().get(j);
                    moviesStr += String.format(" id=%s, name='%s';", tmpMovie.getId(), tmpMovie.getName());
                    
                    // Exact match is given preference
                    String str = tmpMovie.getName();
                    if (str.equalsIgnoreCase(title)) {
                        show = tmpMovie;
                        break;
                    }
                }
                moviesStr = moviesStr.substring(0, moviesStr.length() - 2);
                LOGGER.debug(moviesStr);

                TVEpisodeInfo episode = api.getEpisodeInfo(show.getId(), seasonNumber, episodeNumber, null, new String[] {null});

                media.setFileTitleFromMetadata(String.format("[S%dE%d] %s - %s", seasonNumber, episodeNumber, show.getName(), episode.getName()));
                media.setYear(show.getFirstAirDate());
                String baseUrl = "http://image.tmdb.org/t/p/original";
                byte[] image = null;
                if (episode.getStillPath() == null) {
                    image = getImage(baseUrl + show.getPosterPath());
                } else {
                    image = getImage(baseUrl + episode.getStillPath());
                }
                media.setThumb(image);
                media.setGenre(getGenre(show.getGenreIds()));
                if (episode.getExternalIDs().getImdbId() != null)
                    media.setImdbId(episode.getExternalIDs().getImdbId());
            }
        } catch (Exception e) {
            LOGGER.debug("Error while querying TMDB: " + e.getMessage());
            LOGGER.trace("", e);
        }
        
        return result;
    }

    public static byte[] getImage(String path) {
        byte[] response = null;
        
        try {
            URL url = new URL(path);
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            response = out.toByteArray();
        } catch (Exception e) {
            LOGGER.debug("Error while fetching image: " + e.getMessage());
            LOGGER.trace("", e);
        }
        return response;
    }

    private static String getGenre(List<Integer> ids) {
        if (ids == null || ids.isEmpty())
            return null;
        
        StringBuilder genre = new StringBuilder();
        for (Integer id : ids) {
            genre.append(genres.get(id));
            genre.append(",");
        }
        
        if (genre.length() == 0)
            return null;
        return genre.substring(0, genre.length() - 1);
    }
}
