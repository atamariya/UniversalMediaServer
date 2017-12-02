package net.pms.util;

import static net.pms.network.UPNPHelper.unescape;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;

import net.pms.PMS;
import net.pms.configuration.DeviceConfiguration;
import net.pms.dlna.DLNAResource;
import net.pms.dlna.RealFile;
import net.pms.dlna.virtual.VirtualVideoAction;
import net.pms.network.UPNPControl;
import net.pms.network.UPNPHelper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface BasicPlayer extends ActionListener {
	public class State {
		public int playback;
		public boolean mute;
		public int volume;
		public String position, duration;
		public String name, uri, metadata;
		public long buffer;
	}

	final static int STOPPED = 0;
	final static int PLAYING = 1;
	final static int PAUSED = 2;
	final static int NO_MEDIA_PRESENT = 3;

	final static int PLAYCONTROL = 1;
	final static int VOLUMECONTROL = 2;

	public void setURI(String uri, String metadata);

	public void pressPlay(String uri, String metadata);

	public void pressStop();

	public void play();

	public void pause();

	public void stop();

	public void next();

	public void prev();

	public void forward();

	public void rewind();

	public void mute();

	public void setVolume(int volume);

	public void add(int index, String uri, String name, String metadata, boolean select);

	public void remove(String uri);

	public void setBuffer(long mb);

	public State getState();

	public int getControls();

	public DefaultComboBoxModel getPlaylist();

	public void connect(ActionListener listener);

	public void disconnect(ActionListener listener);

	public void alert();

	public void start();

	public void reset();

	public void close();

	// An empty implementation with some basic funtionalities defined

	public static class Minimal implements BasicPlayer {

		public DeviceConfiguration renderer;
		protected State state;
		protected LinkedHashSet<ActionListener> listeners;

		public Minimal(DeviceConfiguration renderer) {
			this.renderer = renderer;
			state = new State();
			listeners = new LinkedHashSet<>();
			if (renderer.gui != null) {
				connect(renderer.gui);
			}
			reset();
		}

		@Override
		public void start() {
		}

		@Override
		public void reset() {
			state.playback = STOPPED;
			state.position = "";
			state.duration = "";
			state.name = " ";
			state.buffer = 0;
			alert();
		}

		@Override
		public void connect(ActionListener listener) {
			if (listener != null) {
				listeners.add(listener);
			}
		}

		@Override
		public void disconnect(ActionListener listener) {
			listeners.remove(listener);
			if (listeners.isEmpty()) {
				close();
			}
		}

		@Override
		public void alert() {
			for (ActionListener l : listeners) {
				l.actionPerformed(new ActionEvent(this, 0, null));
			}
		}

		@Override
		public BasicPlayer.State getState() {
			return state;
		}

		@Override
		public void close() {
			listeners.clear();
			renderer.setPlayer(null);
		}

		@Override
		public void setBuffer(long mb) {
			state.buffer = mb;
			alert();
		}

		@Override
		public void setURI(String uri, String metadata) {
		}

		@Override
		public void pressPlay(String uri, String metadata) {
		}

		@Override
		public void pressStop() {
		}

		@Override
		public void play() {
		}

		@Override
		public void pause() {
		}

		@Override
		public void stop() {
		}

		@Override
		public void next() {
		}

		@Override
		public void prev() {
		}

		@Override
		public void forward() {
		}

		@Override
		public void rewind() {
		}

		@Override
		public void mute() {
		}

		@Override
		public void setVolume(int volume) {
		}

		@Override
		public void add(int index, String uri, String name, String metadata, boolean select) {
		}

		@Override
		public void remove(String uri) {
		}

		@Override
		public int getControls() {
			return 0;
		}

		@Override
		public DefaultComboBoxModel getPlaylist() {
			return null;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
		}
	}

	// An abstract implementation with all internal playback/playlist logic included.
	// Ideally the entire state-machine resides here and subclasses just implement the
	// details of communicating with the target device.

	public static abstract class Logical extends Minimal {
		public Playlist playlist;
		protected boolean autoContinue, addAllSiblings, forceStop;
		protected int lastPlayback;
		protected int maxVol;
		protected DLNAResource resource;
		protected long seconds = 0;

		public Logical(DeviceConfiguration renderer) {
			super(renderer);
			playlist = new Playlist(this);
			lastPlayback = STOPPED;
			maxVol = renderer.getMaxVolume();
			autoContinue = true;//renderer.isAutoContinue();
			addAllSiblings = renderer.isAutoAddAll();
			forceStop = false;
//			alert();
			initAutoPlay(this);
		}

		@Override
		public abstract void setURI(String uri, String metadata);

		public synchronized Playlist.Item resolveURI(String uri, String metadata) {
			if (uri != null) {
				Playlist.Item item;
				if (metadata != null && metadata.startsWith("<DIDL")) {
					// If it looks real assume it's valid
					return new Playlist.Item(uri, null, metadata);
				} else if ((item = playlist.get(uri)) != null) {
					// We've played it before
					return item;
				} else {
					// It's new to us, find or create the resource as required.
					// Note: here metadata (if any) is actually the resource name
					DLNAResource d = DLNAResource.getValidResource(uri, metadata, renderer);
					resource = d;
					if (d != null) {
						return new Playlist.Item(d.getTranscodedFileURL(renderer), d.getDisplayName(), d.getDidlString(renderer));
					}
				}
			}
			return null;
		}

		@Override
		public void pressPlay(String uri, String metadata) {
			forceStop = false;
			if (state.playback == -1) {
				// unknown state, we assume it's stopped
				state.playback = STOPPED;
			}
//			if (state.playback == PLAYING) {// && uri != null && uri.equals(state.uri)) {
//				pause();
//				state.playback = PAUSED;
//			} else 
			{
				if (state.playback == STOPPED) {
					Playlist.Item item = playlist.resolve(uri);
					if (item != null) {
						uri = item.uri;
						metadata = item.metadata;
						state.name = item.name;
						state.duration = item.duration;
					}
				}
				// Outside "if" as media could be paused
				if (uri != null && !uri.equals(state.uri)) {
					setURI(uri, metadata);
				}
				play();
			}
		}

		@Override
		public void pressStop() {
			forceStop = true;
			stop();
			state.playback = STOPPED;
		}

		@Override
		public void next() {
			step(1);
		}

		@Override
		public void prev() {
			step(-1);
		}

		public void step(int n) {
			net.pms.dlna.Playlist playlist = PMS.get().getDynamicPls();
			if (playlist == null || !playlist.step(n)
					|| state.playback == STOPPED) {
				return;
			}
			
			if (state.playback == PLAYING || state.playback == NO_MEDIA_PRESENT) {
				DLNAResource r = playlist.getCurrent();
				pressPlay(r.getURL(""), null);
			}
		}

		@Override
		public void alert() {
			boolean stopping = state.playback == STOPPED && lastPlayback == NO_MEDIA_PRESENT;
			lastPlayback = state.playback;	
			super.alert();

			if (resource == null || !resource.getId().equals(DLNAResource.parseResourceId(state.uri))) {
				resolveURI(state.uri, null);
			}
			if (resource != null && resource.getMedia() != null && seconds > 0 && state.playback != -1) {
				// Capture the statistics
				double elapsed = seconds;
				if (state.playback == STOPPED && (resource.getMedia().getDurationInSeconds() - elapsed) < 1) {
					elapsed = 0;
					int count = resource.getMedia().getPlayCount() + 1;
					resource.getMedia().setPlayCount(count);
				}
				PMS.get().getDatabase().updateStatistics(resource, elapsed);
			}
			if (stopping) {
				// To play the next item in playlist, we must set status as playing
				state.playback = PLAYING;
				next();
			}
		}

		@Override
		public int getControls() {
			return renderer.controls;
		}

		@Override
		public DefaultComboBoxModel getPlaylist() {
			return playlist;
		}

		@Override
		public void add(int index, String uri, String name, String metadata, boolean select) {
			if (!StringUtils.isBlank(uri)) {
				if (addAllSiblings && DLNAResource.isResourceUrl(uri)) {
					DLNAResource d = PMS.getGlobalRepo().get(DLNAResource.parseResourceId(uri));
					if (d != null && d.getParent() != null) {
						List<DLNAResource> list = d.getParent().getChildren();
						addAll(index, list, list.indexOf(d));
						return;
					}
				}
				playlist.add(index, uri, name, metadata, select);
			}
		}

		public void addAll(int index, List<DLNAResource> list, int selIndex) {
			for (int i = 0; i < list.size(); i++) {
				DLNAResource r = list.get(i);
				if ((r instanceof VirtualVideoAction) || r.isFolder()) {
					// skip these
					continue;
				}
				playlist.add(index, r.getURL("", true), r.getDisplayName(), r.getDidlString(renderer), i == selIndex);
			}
		}

		@Override
		public void remove(String uri) {
			if (!StringUtils.isBlank(uri)) {
				playlist.remove(uri);
			}
		}

		public void clear() {
			playlist.removeAllElements();
		}

		private static void initAutoPlay(final Logical player) {
			String auto = player.renderer.getAutoPlay();
			if (StringUtils.isEmpty(auto)) {
				return;
			}
			String[] strs = auto.split(" ");
			for (String s : strs) {
				String[] tmp = s.split(":", 2);
				if (tmp.length != 2) {
					continue;
				}
				if (!player.renderer.getConfName().equalsIgnoreCase(tmp[0])) {
					continue;
				}
				final String folder = tmp[1];
				Runnable r = new Runnable() {
					@Override
					public void run() {
						while(PMS.get().getServer().getHost() == null) {
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
								return;
							}
						}
						RealFile f = new RealFile(new File(folder));
						f.discoverChildren();
						f.analyzeChildren(-1);
						player.addAll(-1, f.getChildren(), -1);
						// add a short delay here since player.add uses swing.invokelater
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
						}
						player.pressPlay(null, null);
					}
				};
				new Thread(r).start();
			}
		}

		public static class Playlist extends DefaultComboBoxModel {
			private static final long serialVersionUID = 5934677633834195753L;

			Logical player;

			public Playlist(Logical p) {
				player = p;
			}

			public Item get(String uri) {
				int index = getIndexOf(new Item(uri, null, null));
				if (index > -1) {
					return (Item) getElementAt(index);
				}
				return null;
			}

			public Item resolve(String uri) {
				Item item = null;
				try {
					Object selected = getSelectedItem();
					Item selectedItem = selected instanceof Item ? (Item) selected : null;
					String selectedName = selectedItem != null ? selectedItem.name : null;
					// See if we have a matching item for the "uri", which could be:
					item = (Item) (
						// An alias for the currently selected item
						StringUtils.isBlank(uri) || uri.equals(selectedName) ? selectedItem :
						// An item index, e.g. '$i$4'
						uri.startsWith("$i$") ? getElementAt(Integer.valueOf(uri.substring(3))) :
						// Or an actual uri
						get(uri));
				} catch (Exception e) {
				}
				return (item != null && isValid(item, player.renderer)) ? item : null;
			}

			public static boolean isValid(Item item, DeviceConfiguration renderer) {
				if (DLNAResource.isResourceUrl(item.uri)) {
					// Check existence for resource uris
					if (PMS.get().getGlobalRepo().exists(DLNAResource.parseResourceId(item.uri))) {
						return true;
					}
					// Repair the item if possible
					DLNAResource d = DLNAResource.getValidResource(item.uri, item.name, renderer);
					if (d != null) {
						item.uri = d.getURL("", true);
						item.metadata = d.getDidlString(renderer);
						item.duration = d.getMedia().getDurationString();
						return true;
					}
					return false;
				}
				// Assume non-resource uris are valid
				return true;
			}

			public void validate() {
				for (int i = getSize() - 1; i > -1; i--) {
					if (!isValid((Item) getElementAt(i), player.renderer)) {
						removeElementAt(i);
					}
				}
			}

			public void set(String uri, String name, String metadata) {
				add(0, uri, name, metadata, true);
			}

			public void add(final int index, final String uri, final String name, final String metadata, final boolean select) {
				if (!StringUtils.isBlank(uri)) {
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							Item item = resolve(uri);
							if (item == null) {
								item = new Item(uri, name, metadata);
								insertElementAt(item, index > -1 ? index : getSize());
							}
							if (select) {
								setSelectedItem(item);
							}
						}
					});
				}
			}

			public void remove(final String uri) {
				if (!StringUtils.isBlank(uri)) {
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							Item item = resolve(uri);
							if (item != null) {
								removeElement(item);
							}
						}
					});
				}
			}

			public boolean step(int n) {
				int i = (getIndexOf(getSelectedItem()) + n);
				boolean hasNext = false;
				// Don't step beyond last item
				if (i < getSize()) {
//					int i = (getIndexOf(getSelectedItem()) + getSize() + n) % getSize();
					setSelectedItem(getElementAt(i));
					hasNext = true;
				}
				return hasNext;
			}

			@Override
			protected void fireContentsChanged(Object source, int index0, int index1) {
				player.alert();
				super.fireContentsChanged(source, index0, index1);
			}

			@Override
			protected void fireIntervalAdded(Object source, int index0, int index1) {
				player.alert();
				super.fireIntervalAdded(source, index0, index1);
			}

			@Override
			protected void fireIntervalRemoved(Object source, int index0, int index1) {
				player.alert();
				super.fireIntervalRemoved(source, index0, index1);
			}

			public static class Item {
				public String duration;
				private static final Logger LOGGER = LoggerFactory.getLogger(Item.class);
				public String name, uri, metadata;
				static final Matcher dctitle = Pattern.compile("<dc:title>(.+)</dc:title>").matcher("");

				public Item(String uri, String name, String metadata) {
					this.uri = uri;
					this.name = name;
					this.metadata = metadata;
				}

				@Override
				public String toString() {
					if (StringUtils.isBlank(name)) {
						try {
							name = (! StringUtils.isEmpty(metadata) && dctitle.reset(unescape(metadata)).find()) ?
								dctitle.group(1) :
								new File(StringUtils.substringBefore(unescape(uri), "?")).getName();
						} catch (UnsupportedEncodingException e) {
							LOGGER.error("URL decoding error ", e);
						}
					}
					return name;
				}

				@Override
				public boolean equals(Object other) {
					// Playlist items can be duplicate	
					return super.equals(other);
//					return other == null ? false :
//						other == this ? true :
//						other instanceof Item ? ((Item)other).uri.equals(uri) :
//						other.toString().equals(uri);
				}

				@Override
				public int hashCode() {
					return uri.hashCode();
				}
			}
		}
	}
}
