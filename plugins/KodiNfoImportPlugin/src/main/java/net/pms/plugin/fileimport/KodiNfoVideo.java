package net.pms.plugin.fileimport;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Holds data coming from a Kodi NFO file for a video
 */
public class KodiNfoVideo {
	private String name;
	private String sortName;
	private String originalName;
	private int ratingPercent;
	private int ratingVoters;
	private int year;
	private String overview;
	private String coverUrl;
	private String certification;
	private String imdbId;
	private String trailerUrl;
	private List<String> genres;
	private String director;
	private List<String> actors;
	private String tagLine;
	private String episodeNumber;
	private String seasonNumber;
	private String firstAired;
	private String network;
	private String seriesName;
	private String writer;

	/**
	 * Gets the director.
	 *
	 * @return the director
	 */
	public String getDirector() {
		return director;
	}

	/**
	 * Sets the director.
	 *
	 * @param director the director to set
	 */
	public void setDirector(String director) {
		this.director = director;
	}

	/**
	 * Gets the genres.
	 *
	 * @return the genres
	 */
	public List<String> getGenres() {
		return genres;
	}

	/**
	 * Sets the genres.
	 *
	 * @param genres the genres to set
	 */
	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	/**
	 * Gets the trailer url.
	 *
	 * @return the trailerUrl
	 */
	public String getTrailerUrl() {
		return trailerUrl;
	}

	/**
	 * Sets the trailer url.
	 *
	 * @param trailerUrl the trailerUrl to set
	 */
	public void setTrailerUrl(String trailerUrl) {
		this.trailerUrl = trailerUrl;
	}

	/**
	 * Gets the imdb id.
	 *
	 * @return the imdbId
	 */
	public String getImdbId() {
		return imdbId;
	}

	/**
	 * Sets the imdb id.
	 *
	 * @param imdbId the imdbId to set
	 */
	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}

	/**
	 * Gets the certification.
	 *
	 * @return the certification
	 */
	public String getCertification() {
		return certification;
	}

	/**
	 * Sets the certification.
	 *
	 * @param certification the certification to set
	 */
	public void setCertification(String certification) {
		this.certification = certification;
	}

	/**
	 * Gets the cover url.
	 *
	 * @return the coverUrl
	 */
	public String getCoverUrl() {
		return coverUrl;
	}

	/**
	 * Sets the cover url.
	 *
	 * @param coverUrl the coverUrl to set
	 */
	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	/**
	 * Gets the overview.
	 *
	 * @return the overview
	 */
	public String getOverview() {
		return overview;
	}

	/**
	 * Sets the overview.
	 *
	 * @param overview the overview to set
	 */
	public void setOverview(String overview) {
		this.overview = overview;
	}

	/**
	 * Gets the year.
	 *
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Sets the year.
	 *
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Gets the rating voters.
	 *
	 * @return the ratingVoters
	 */
	public int getRatingVoters() {
		return ratingVoters;
	}

	/**
	 * Sets the rating voters.
	 *
	 * @param ratingVoters the ratingVoters to set
	 */
	public void setRatingVoters(int ratingVoters) {
		this.ratingVoters = ratingVoters;
	}

	/**
	 * Gets the rating percent.
	 *
	 * @return the ratingPercent
	 */
	public int getRatingPercent() {
		return ratingPercent;
	}

	/**
	 * Sets the rating percent.
	 *
	 * @param ratingPercent the ratingPercent to set
	 */
	public void setRatingPercent(int ratingPercent) {
		this.ratingPercent = ratingPercent;
	}

	/**
	 * Gets the original name.
	 *
	 * @return the originalName
	 */
	public String getOriginalName() {
		return originalName;
	}

	/**
	 * Sets the original name.
	 *
	 * @param originalName the originalName to set
	 */
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	/**
	 * Gets the sort name.
	 *
	 * @return the sortName
	 */
	public String getSortName() {
		return sortName;
	}

	/**
	 * Sets the sort name.
	 *
	 * @param sortName the sortName to set
	 */
	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the actors.
	 *
	 * @return the actors
	 */
	public List<String> getActors() {
		return actors;
	}

	/**
	 * Sets the actors.
	 *
	 * @param actors the new actors
	 */
	public void setActors(List<String> actors) {
		this.actors = actors;
	}

	/**
	 * Gets the tag line.
	 *
	 * @return the tag line
	 */
	public String getTagLine() {
		return tagLine;
	}

	/**
	 * Sets the tag line.
	 *
	 * @param tagLine the new tag line
	 */
	public void setTagLine(String tagLine) {
		this.tagLine = tagLine;
	}

	/**
	 * Gets the episode number.
	 *
	 * @return the episode number
	 */
	public String getEpisodeNumber() {
		return episodeNumber;
	}

	/**
	 * Sets the episode number.
	 *
	 * @param episodeNumber the new episode number
	 */
	public void setEpisodeNumber(String episodeNumber) {
		this.episodeNumber = episodeNumber;
	}

	/**
	 * Gets the season number.
	 *
	 * @return the season number
	 */
	public String getSeasonNumber() {
		return seasonNumber;
	}

	/**
	 * Sets the season number.
	 *
	 * @param seasonNumber the new season number
	 */
	public void setSeasonNumber(String seasonNumber) {
		this.seasonNumber = seasonNumber;
	}

	/**
	 * Gets the first aired.
	 *
	 * @return the first aired
	 */
	public String getFirstAired() {
		return firstAired;
	}

	/**
	 * Sets the first aired.
	 *
	 * @param firstAired the new first aired
	 */
	public void setFirstAired(String firstAired) {
		this.firstAired = firstAired;
	}

	/**
	 * Gets the network.
	 *
	 * @return the network
	 */
	public String getNetwork() {
		return network;
	}

	/**
	 * Sets the network.
	 *
	 * @param network the new network
	 */
	public void setNetwork(String network) {
		this.network = network;
	}

	/**
	 * Gets the series name.
	 *
	 * @return the series name
	 */
	public String getSeriesName() {
		return seriesName;
	}

	/**
	 * Sets the series name.
	 *
	 * @param seriesName the new series name
	 */
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	/**
	 * Gets the writer.
	 *
	 * @return the writer
	 */
	public String getWriter() {
		return writer;
	}

	/**
	 * Sets the writer.
	 *
	 * @param writer the new writer
	 */
	public void setWriter(String writer) {
		this.writer = writer;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(actors)
				.append(certification)
				.append(coverUrl)
				.append(director)
				.append(genres)
				.append(imdbId)
				.append(name)
				.append(originalName)
				.append(overview)
				.append(ratingPercent)
				.append(ratingVoters)
				.append(sortName)
				.append(trailerUrl)
				.append(year)
				.append(tagLine)
				.append(episodeNumber)
				.append(seasonNumber)
				.append(firstAired)
				.append(network)
				.append(seriesName)
				.append(writer)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof KodiNfoVideo) {
			final KodiNfoVideo other = (KodiNfoVideo) obj;
			return new EqualsBuilder()
					.append(actors, other.actors)
					.append(certification, other.certification)
					.append(coverUrl, other.coverUrl)
					.append(director, other.director)
					.append(genres, other.genres)
					.append(imdbId, other.imdbId)
					.append(name, other.name)
					.append(originalName, other.originalName)
					.append(overview, other.overview)
					.append(ratingPercent, other.ratingPercent)
					.append(ratingVoters, other.ratingVoters)
					.append(sortName, other.sortName)
					.append(trailerUrl, other.trailerUrl)
					.append(year, other.year)
					.append(tagLine, other.tagLine)
					.append(episodeNumber, other.episodeNumber)
					.append(seasonNumber, other.seasonNumber)
					.append(firstAired, other.firstAired)
					.append(network, other.network)
					.append(seriesName, other.seriesName)
					.append(writer, other.writer)
					.isEquals();
		} else {
			return false;
		}
	}
}