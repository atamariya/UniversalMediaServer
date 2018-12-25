package net.pms.dlna;

import net.pms.configuration.RendererConfiguration;
import net.pms.configuration.WebRender;

/**
 * This class represents live streams supported by youtube-dl.
 * @author Anand Tamariya
 *
 */
public class LiveStream extends YoutubeWebVideoStream {

    public LiveStream(String fluxName, String URL, String thumbURL) {
        super(fluxName, URL, thumbURL);
    }

    @Override
    public String getTranscodedFileURL(RendererConfiguration mediaRenderer) {
        // HLS is handled using Clappr in web - no transcoding required.
        if (mediaRenderer instanceof WebRender) {
            return getUrl();
        }
        return super.getTranscodedFileURL(mediaRenderer);
    }
}
