/*
 * PS3 Media Server, for streaming any medias to your PS3.
 * Copyright (C) 2012  Ph.Waeber
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
package net.pms.medialibrary.dlna;

import java.util.ArrayList;

import net.pms.dlna.DLNAResource;
import net.pms.medialibrary.commons.dataobjects.DOMediaLibraryFolder;
import net.pms.medialibrary.storage.MediaLibraryStorage;

/**
 * The dlna root folder is the entry point to create the dlna tree shown on the renderer
 */
public class RootFolder extends MediaLibraryFolder {
	private MediaLibraryFolder rootFolder;
	private ArrayList<String> tags;

	/**
	 * Instantiates a new root folder.
	 */
	public RootFolder() {
		super(MediaLibraryStorage.getInstance().getMediaLibraryFolder(MediaLibraryStorage.getInstance().getRootFolderId(), MediaLibraryStorage.ALL_CHILDREN));
		setId("0");
	}

	public RootFolder(ArrayList<String> tags) {
		this();
		this.tags = tags;
	}

	/**
	 * Gets the root folder.
	 *
	 * @return the root folder
	 */
	public DOMediaLibraryFolder getRootFolder() {
		return rootFolder.getFolder();
	}

	public void setFolder(DLNAResource res) {
		// Nothing to do here
	}

	public void stopPlaying(DLNAResource res) {
		// Nothing to do here
	}

	public ArrayList<String> getTags() {
		return tags;
	}
}
