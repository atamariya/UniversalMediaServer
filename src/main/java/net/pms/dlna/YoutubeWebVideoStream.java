/*
 * PS3 Media Server, for streaming any medias to your PS3.
 * Copyright (C) 2008  A.Brochard
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 2
 * of the License only.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.pms.dlna;

import net.pms.configuration.RendererConfiguration;
import net.pms.configuration.WebRender;
import net.pms.formats.Format;

/**
 * This class represents video stream supported by youtube-dl.
 * @author Anand Tamariya
 *
 */
public class YoutubeWebVideoStream extends WebStreamItem {
    public static final String PATTERN = "https://www.youtube.com";
	
    public YoutubeWebVideoStream(String fluxName, String URL, String thumbURL) {
	    super(Format.VIDEO);
	    this.title = fluxName;
	    this.url = URL;
	    this.thumbURL = thumbURL;
	}
    
    @Override
    public String mimeType() {
        return "video/mp4";
    }
    
    public String getTranscodedFileURL(RendererConfiguration mediaRenderer) {
        // HLS is handled using Clappr in web - no transcoding required.
        if (mediaRenderer instanceof WebRender) {
            return getUrl();
        }
        return super.getTranscodedFileURL(mediaRenderer);
    }
}
