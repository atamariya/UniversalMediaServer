package net.pms.dlna;

import net.pms.configuration.RendererConfiguration;
import net.pms.configuration.WebRender;
import net.pms.formats.Format;

/**
 * This class represents live streams supported by youtube-dl. This is not a subclass of YoutubeWebVideoStream
 * as this uses chunked transfer (check in RequestV2). This is not a subclass of WebVideoStream as URL handling
 * is conditional here (getTranscodedFileURL()).
 * @author Anand Tamariya
 *
 */
public class LiveStream extends WebStreamItem {

    public LiveStream(String fluxName, String URL, String thumbURL) {
    	super(Format.VIDEO);
    	setTitle(fluxName);
    	setUrl(URL);
    	setThumbURL(thumbURL);
    }

    @Override
    public String getTranscodedFileURL(RendererConfiguration mediaRenderer) {
        // HLS is handled using Clappr in web - no transcoding required.
        if (mediaRenderer instanceof WebRender) {
            return getUrl();
        }
        return super.getTranscodedFileURL(mediaRenderer);
    }
    
    @Override
    public String mimeType() {
        return "video/mpeg";
    }
}
