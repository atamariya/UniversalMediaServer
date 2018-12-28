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

import net.pms.formats.Format;
import net.pms.util.PlayerUtil;

public class VideosFeed extends Feed {
	@Override
	protected void manageItem() {
        WebStreamItem fi = null;
        /* Only youtube video needs special handling. Avoid unnecessary overhead for other
         * video urls.
         */
        if (getTempItemLink() != null && PlayerUtil.isYoutubeVideo(getTempItemLink()))
            fi = new YoutubeWebVideoStream(getTempItemTitle(), getTempItemLink(), getTempItemThumbURL());
        else
            fi = new WebVideoStream(getTempItemTitle(), getTempItemLink(), getTempItemThumbURL());
		addChild(fi);
	}

	public VideosFeed(String url) {
		super("" + System.currentTimeMillis(), url, Format.VIDEO);
	}
}
