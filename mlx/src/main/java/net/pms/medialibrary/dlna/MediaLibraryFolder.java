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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pms.dlna.CueFolder;
import net.pms.dlna.DLNAResource;
import net.pms.dlna.DVDISOFile;
import net.pms.dlna.PlaylistFolder;
import net.pms.dlna.RarredFile;
import net.pms.dlna.SubSelect;
import net.pms.dlna.ZippedFile;
import net.pms.dlna.virtual.TranscodeVirtualFolder;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.medialibrary.commons.dataobjects.DOFileInfo;
import net.pms.medialibrary.commons.dataobjects.DOFolder;
import net.pms.medialibrary.commons.dataobjects.DOMediaLibraryFolder;
import net.pms.medialibrary.commons.dataobjects.DOSpecialFolder;
import net.pms.medialibrary.commons.dataobjects.DOVideoFileInfo;
import net.pms.medialibrary.commons.dataobjects.FileDisplayProperties;
import net.pms.medialibrary.commons.enumarations.FileDisplayType;
import net.pms.medialibrary.commons.enumarations.FileType;
import net.pms.medialibrary.storage.MediaLibraryStorage;

/**
 * When adding a file as a folder, this class is being used
 */
public class MediaLibraryFolder extends VirtualFolder {
	private static final Logger log = LoggerFactory.getLogger(MediaLibraryFolder.class);
	private DOMediaLibraryFolder folder;
	private boolean isUpdating = false;

	/**
	 * Instantiates a new media library folder.
	 *
	 * @param folder the folder
	 */
	public MediaLibraryFolder(DOMediaLibraryFolder folder) {
		super(folder.getName(), null);

		setFolder(folder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.pms.dlna.DLNAResource#isRefreshNeeded()
	 */
	@Override
	public boolean isRefreshNeeded() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.pms.dlna.DLNAResource#isTranscodeFolderAvailable()
	 */
	@Override
	public boolean isTranscodeFolderAvailable() {
		return folder.getDisplayProperties() != null && folder.getDisplayProperties().isShowTranscodeFolder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.pms.dlna.DLNAResource#isLiveSubtitleFolderAvailable()
	 */
	@Override
	public boolean isLiveSubtitleFolderAvailable() {
		return folder.getDisplayProperties() != null && folder.getDisplayProperties().isShowLiveSubtitleFolder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.pms.dlna.virtual.VirtualFolder#isFolder()
	 */
	@Override
	public boolean isFolder() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.pms.dlna.DLNAResource#discoverChildren()
	 */
	@Override
	public void discoverChildren() {
		refreshChildren();
	}

	/**
	 * Adds the child folder.
	 *
	 * @param f the f
	 */
	private void addChildFolder(DOFolder f) {
		if (f instanceof DOMediaLibraryFolder) {
			addChild(new MediaLibraryFolder((DOMediaLibraryFolder) f));
		} else if (f instanceof DOSpecialFolder) {
			addChild(((DOSpecialFolder) f).getSpecialFolderImplementation().getDLNAResource());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.pms.dlna.DLNAResource#refreshChildren()
	 */
	@Override
	public void doRefreshChildren() {
		if (isUpdating) {
			return;
		}
		isUpdating = true;

		if (log.isDebugEnabled()) {
			log.debug(String.format("Start refreshing children for folder '%s' (%s)", getName(), getId()));
		}

		boolean isFolderRefreshed = false;
		try {
			// Make sure the media library folder is up to date
			updateFolder();

			// Get the folders that will be displayed (from the media library folder)
			List<DOFolder> foldersToAdd = getFolder().getChildFolders();

			// Get the files that will be displayed (form the DB)
			FileDisplayProperties fileDisplayProperties = getFolder().getDisplayProperties();
			List<DOFileInfo> filesToAdd = new ArrayList<DOFileInfo>();
			if (getFolder().isDisplayItems() && getFolder().getFileType() == FileType.VIDEO) {
				List<DOVideoFileInfo> videoFiles = MediaLibraryStorage.getInstance().getVideoFileInfo(getFolder().getInheritedFilter(), fileDisplayProperties.isSortAscending(), fileDisplayProperties.getSortType(), folder.getMaxFiles(), fileDisplayProperties.getSortOption(), true);

				// Only add files which physically exist on the disk
				for (DOVideoFileInfo videoFile : videoFiles) {
					if (new File(videoFile.getFilePath()).exists()) {
						filesToAdd.add(videoFile);
					}
				}
			}

			// Determine what files and folders have to be added (folderIndex and fileIndex) and from which index in the currently
			// displayed DLNAResources have to be removed (currentPosition)
			short fileIndex = 0;
			short folderIndex = 0;
			int currentPosition = 0;
			List<DLNAResource> resourcesToRemove = new ArrayList<>();
			for (DLNAResource currentResource : getChildren()) {
				if (currentResource.isFolder()) {
					// The current resource is a folder
					DOFolder folderToAdd = foldersToAdd.size() > folderIndex ? foldersToAdd.get(folderIndex) : null;

					if (currentResource instanceof MediaLibraryFolder) {
						// The current folder is a media library folder (which has been configured by the user)
						if (!(folderToAdd instanceof DOMediaLibraryFolder)) {
							break;
						}

						// Check if the folder has changed
						DOMediaLibraryFolder mediaLibraryFolderToAdd = (DOMediaLibraryFolder) folderToAdd;
						MediaLibraryFolder currentMediaLibraryFolder = (MediaLibraryFolder) currentResource;
						if (!mediaLibraryFolderToAdd.equals(currentMediaLibraryFolder.getFolder())) {
							break;
						}

						folderIndex++;

					} else if (currentResource instanceof TranscodeVirtualFolder) {
						// The current folder is the transcode or subtitles folder (which has been automatically added by UMS)
						// Move on, nothing to do here
						if (!isTranscodeFolderAvailable()) {
							resourcesToRemove.add(currentResource);
						}
					} else if (currentResource instanceof SubSelect) {
						// The current folder is the transcode or subtitles folder (which has been automatically added by UMS)
						// Move on, nothing to do here
						if (!isLiveSubtitleFolderAvailable()) {
							resourcesToRemove.add(currentResource);
						}
					} else {
						// If we are neither of the above, we probably are a special folder (which has been configured by the user)
						if (!currentResource.getName().equals(folderToAdd.getName())) {
							// if the special folder has changed
							// TODO: Improve.. How to check if this is actually a special folder??
							break;
						}

						folderIndex++;
					}

				} else {
					// The current resource is a file
					MediaLibraryRealFile mediaLibraryRealFile = (MediaLibraryRealFile) currentResource;
					DOFileInfo fileToAdd = filesToAdd.get(fileIndex);
					if (!mediaLibraryRealFile.getFileInfo().equals(fileToAdd)) {
						break;
					}

					fileIndex++;
				}

				currentPosition++;
			}

			// Remove all currently displayed DLNAResources after the computed currentPosition
			while (currentPosition < getChildren().size()) {
				getChildren().remove(currentPosition);
				isFolderRefreshed = true;
			}
			// Remove DLNA resources which have been automatically added by UMS (transcode and subtitles folders)
			for (DLNAResource resouceToRemove : resourcesToRemove) {
				getChildren().remove(resouceToRemove);
			}

			// Add folders and files
			while (folderIndex < foldersToAdd.size()) {
				addChildFolder(foldersToAdd.get(folderIndex++));
				isFolderRefreshed = true;
			}
			while (fileIndex < filesToAdd.size()) {
				manageFile(filesToAdd.get(fileIndex++));
				isFolderRefreshed = true;
			}

			// Make sure the transcode and subtitles folder are being displayed if they have been configured and files
			// are contained in the folder
			if (filesToAdd.size() > 0) {
				if (isLiveSubtitleFolderAvailable()) {
					getSubSelector(true);
				}
				if (isTranscodeFolderAvailable()) {
					getTranscodeFolder(true);
				}
			}
		} finally {
			isUpdating = false;
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("Finished refreshing children for folder '%s' (%s). Refreshed=%s", getName(), getId(), isFolderRefreshed));
		}
	}

	/**
	 * This method decides if a zip, rar, iso, m3u or cue folder should be added
	 *
	 * @param fileInfo the file info to check
	 */
	private void manageFile(DOFileInfo fileInfo) {
		File f = new File(fileInfo.getFilePath());
		if ((f.isFile() || f.isDirectory()) && !f.isHidden()) {
			DLNAResource fileToAdd = new MediaLibraryRealFile(fileInfo, getFolder().getDisplayProperties(), getFolder().getFileType(), isTranscodeFolderAvailable());

			if (getFolder().getDisplayProperties().getFileDisplayType() == FileDisplayType.FOLDER) {
				// add the child as a MediaLibraryRealFile anyway when it has to be displayed as a folder
				addChild(fileToAdd);
			} else {
				if (f.getName().toLowerCase().endsWith(".zip") || f.getName().toLowerCase().endsWith(".cbz")) {
					addChild(new ZippedFile(f));
				} else if (f.getName().toLowerCase().endsWith(".rar") || f.getName().toLowerCase().endsWith(".cbr")) {
					addChild(new RarredFile(f));
				} else if ((f.getName().toLowerCase().endsWith(".iso") || f.getName().toLowerCase().endsWith(".img")) || (f.isDirectory() && f.getName().toUpperCase().equals("VIDEO_TS"))) {
					addChild(new DVDISOFile(f));
				} else if (f.getName().toLowerCase().endsWith(".m3u") || f.getName().toLowerCase().endsWith(".m3u8") || f.getName().toLowerCase().endsWith(".pls")) {
					addChild(new PlaylistFolder(f));
				} else if (f.getName().toLowerCase().endsWith(".cue")) {
					addChild(new CueFolder(f));
				} else {
					addChild(fileToAdd);
				}
			}
		} else {
			log.warn(String.format("Don't add file '%s' to folder '%s' because %s",
					f.getAbsolutePath(), getName(), f.isHidden() ? "it is hidden" : "it does not exist"));
		}
	}

	/**
	 * * Updates the folder if the one retrieved from the DB is more recent then the used one.
	 *
	 * @return true if the folder has been updated
	 */
	private void updateFolder() {
		DOMediaLibraryFolder newFolder = MediaLibraryStorage.getInstance().getMediaLibraryFolder(getFolder().getId(), MediaLibraryStorage.ALL_CHILDREN);
		if (getFolder().getParentFolder() != null
				&& getFolder().getParentFolder().getChildFolders() != null
				&& getFolder().getParentFolder().getChildFolders().contains(getFolder())) {
			getFolder().getParentFolder().getChildFolders().remove(getFolder());
		}
		newFolder.setParentFolder(getFolder().getParentFolder());
		setFolder(newFolder);
	}

	/**
	 * Sets the folder.
	 *
	 * @param folder the new folder
	 */
	public void setFolder(DOMediaLibraryFolder folder) {
		name = folder.getName();
		this.folder = folder;
	}

	/**
	 * Gets the folder.
	 *
	 * @return the folder
	 */
	public DOMediaLibraryFolder getFolder() {
		return folder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.pms.dlna.DLNAResource#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MediaLibraryFolder)) {
			return false;
		}

		MediaLibraryFolder compObj = (MediaLibraryFolder) obj;
		if (getFolder().equals(compObj.getFolder())) {
			return true;
		}
		return false;
	}

}
