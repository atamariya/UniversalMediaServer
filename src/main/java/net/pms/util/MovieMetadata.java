package net.pms.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.enumeration.SearchType;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.results.ResultList;

import net.pms.PMS;
import net.pms.dlna.DLNAMediaInfo;

/**
 * Add movie metadata from TMDB.
 * 
 * @author Anand Tamariya
 *
 */
public class MovieMetadata {
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieMetadata.class);
    private static TheMovieDbApi api;
    
    static {
        try {
            api = new TheMovieDbApi("4cdddc892213dd24e5011fd710f8abf0");
        } catch (MovieDbException ex) {
            LOGGER.debug("Error initializing TMDB: " + ex.getMessage());
            LOGGER.trace("", ex);
        }
    }
    
    public static boolean getTitle(String title, DLNAMediaInfo media) {
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

}
