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

import java.net.MalformedURLException;
import java.net.URL;

import net.pms.configuration.RendererConfiguration;
import net.pms.network.HTTPResourceAuthenticator;

/**
 * TODO: Change all instance variables to private. For backwards compatibility
 * with external plugin code the variables have all been marked as deprecated
 * instead of changed to private, but this will surely change in the future.
 * When everything has been changed to private, the deprecated note can be
 * removed.
 */
public class WebStream extends WebStreamItem {
	public WebStream(String fluxName, String url, String thumbURL, int type) {
		super(type);

		try {
			URL tmpUrl = new URL(url);
			tmpUrl = HTTPResourceAuthenticator.concatenateUserInfo(tmpUrl);
			this.url = tmpUrl.toString();
		} catch (MalformedURLException e) {
			this.url = url;
		}

		try {
			URL tmpUrl = new URL(thumbURL);
			tmpUrl = HTTPResourceAuthenticator.concatenateUserInfo(tmpUrl);
			this.thumbURL = tmpUrl.toString();
		} catch (MalformedURLException e) {
			this.thumbURL = thumbURL;
		}

		this.title = fluxName;
	}

	@Override
	public String write() {
		return title + ">" + url + ">" + thumbURL + ">" + getSpecificType();
	}

	@Override
	public long length() {
		return DLNAMediaInfo.TRANS_SIZE;
	}

    public String getTranscodedFileURL(RendererConfiguration mediaRenderer) {
        return getUrl();
    }
}
