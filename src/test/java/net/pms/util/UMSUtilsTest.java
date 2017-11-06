package net.pms.util;

import static org.assertj.core.api.Assertions.assertThat;
import net.pms.PMS;
import net.pms.configuration.PmsConfiguration;
import net.pms.formats.Format;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class UMSUtilsTest {
	/**
	 * Set up testing conditions before running the tests.
	 * @throws ConfigurationException
	 */
//	@Before
	public final void setUp() throws ConfigurationException {
		// Silence all log messages from the PMS code that is being tested
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		context.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.OFF);
		PMS.get();
		PMS.setConfiguration(new PmsConfiguration(false));
	}

	@Test
	public void testPlayedDurationStr() throws Exception {
		assertThat(UMSUtils.playedDurationStr("01:23:45:67.89", "01:23:45:67")).isEqualTo("23:45:67 / 23:45:67");
		assertThat(UMSUtils.playedDurationStr("01:23", "01:23:45")).isEqualTo("1:23 / 1:23:45");
		assertThat(UMSUtils.playedDurationStr(":12", "59")).isEqualTo("0:12 / 0:59");
		
		long millis = 7489780;
		String out = StringUtil.convertTimeToString(millis);
		assertThat(out).isEqualTo("02:04:49");
	}
	
	@Test
	public void testFiledetection() throws Exception {
		String mimeType = "video/x-ms-wmv";
		String ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".wmv");
		
		String mime = Format.getMimetype("as" + ext);
		assertThat(mime).isEqualTo(mimeType);
		
		mimeType = "video/x-matroska";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".mkv");
		
		mimeType = "video/x-mkv";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".mkv");

		mimeType = "video/mkv";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".mkv");

		mime = Format.getMimetype("as" + ext);
		assertThat(mime).isEqualTo("video/mkv");
		
		mimeType = "audio/x-ogg";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".ogg");

		mimeType = "audio/ogg";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".ogg");

		mime = Format.getMimetype("as" + ext);
		assertThat(mime).isEqualTo(mimeType);

		mimeType = "audio/x-flac";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".flac");
		
		mime = Format.getMimetype("as" + ext);
		assertThat(mime).isEqualTo(mimeType);
		
		mimeType = "audio/flac";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".flac");
		
		mimeType = "audio/mpeg";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".mp3");
		
		mime = Format.getMimetype("as" + ext);
		assertThat(mime).isEqualTo(mimeType);
		
		mimeType = "audio/x-mp4";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".m4a");
		
		mimeType = "audio/x-m4a";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".m4a");
		
		mimeType = "audio/mp4";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".m4a");
		
		mime = Format.getMimetype("as" + ext);
		assertThat(mime).isEqualTo(mimeType);
		
		mimeType = "audio/x-wav";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".wav");
		
		mime = Format.getMimetype("as" + ext);
		assertThat(mime).isEqualTo(mimeType);
		
		mimeType = "audio/wav";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".wav");
		
		// Denon
		mimeType = "audio/L16";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".l16");
		
		mime = Format.getMimetype("as" + ext);
		assertThat(mime).isEqualTo(mimeType);
		
		// Xbox
		mimeType = "video/mpeg";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".mp4");
		
		mime = Format.getMimetype("as" + ext);
		assertThat(mime).isEqualTo("video/mp4");
		
		mime = Format.getMimetype("as.avi");
		assertThat(mime).isEqualTo("video/avi");
		
		mimeType = "audio/mpeg";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".mp3");
		
		mime = Format.getMimetype("as" + ext);
		assertThat(mime).isEqualTo(mimeType);
		
		mimeType = "audio/x-ms-wma";
		ext = Format.getExtension(mimeType);
		assertThat(ext).isEqualTo(".wma");
		
		mime = Format.getMimetype("as" + ext);
		assertThat(mime).isEqualTo(mimeType);
		
		// Unsupported mime types
		mimeType = "application/pdf";
		boolean supported = Format.isSupportedMimetype(mimeType);
		assertThat(supported).isEqualTo(false);
	}
}
