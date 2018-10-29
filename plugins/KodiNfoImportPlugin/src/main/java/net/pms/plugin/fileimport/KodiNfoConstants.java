package net.pms.plugin.fileimport;

/**
 * Class holding constants used by the Kodi NFO import plugin.
 */
public class KodiNfoConstants {
	public static final String NFO_FILE_EXTENSION = ".nfo";

	/**
	 * Available video tags.
	 */
	public static enum VideoTag {
		EpisodeNumber, SeasonNumber, FirstAired, Network, SeriesName, Actor, Writer
	}
}