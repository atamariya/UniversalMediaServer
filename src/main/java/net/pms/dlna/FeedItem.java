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

import java.io.IOException;
import java.io.InputStream;

public class FeedItem extends WebStreamItem {
	@Override
	protected String getThumbnailURL() {
		if (thumbURL == null) {
			return null;
		}
		return super.getThumbnailURL();
	}

	@Override
	public String getThumbnailContentType() {
		if (thumbURL != null && thumbURL.toLowerCase().endsWith(".jpg")) {
			return JPEG_TYPEMIME;
		} else {
			return super.getThumbnailContentType();
		}
	}

	private long length;

	public FeedItem(String title, String itemURL, String thumbURL, DLNAMediaInfo media, int type) {
		super(type);
		this.title = title;
		this.url = itemURL;
		this.thumbURL = thumbURL;
		this.setMedia(media);
	}

	@Override
	public InputStream getInputStream() {
		InputStream i = null;
        try {
            i = downloadAndSend(url, true);
            if (i != null) {
                length = i.available();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return i;
	}

	@Override
	public long length() {
		return length;
	}

	public void parse(String content) {
	}
	
}
