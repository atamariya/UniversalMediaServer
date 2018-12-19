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
package net.pms.encoders;

import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import net.pms.Messages;
import net.pms.PMS;
import net.pms.configuration.DeviceConfiguration;
import net.pms.configuration.PmsConfiguration;
import net.pms.configuration.RendererConfiguration;
import net.pms.dlna.DLNAMediaInfo;
import net.pms.dlna.DLNAResource;
import net.pms.dlna.YoutubeWebVideoStream;
import net.pms.formats.Format;
import net.pms.io.OutputParams;
import net.pms.io.ProcessWrapper;
import net.pms.io.ProcessWrapperImpl;
import net.pms.io.SystemUtils;
import net.pms.network.HTTPResource;
import net.pms.newgui.GuiUtil;
import net.pms.util.FormLayoutUtil;
import net.pms.util.PlayerUtil;

/**
 * Use youtube-dl (https://github.com/rg3/youtube-dl) for playing youtube videos.
 *
 * @author atamariya@gmail.com
 */
public class YoutubeVideo extends Player {
	private static final Logger LOGGER = LoggerFactory.getLogger(YoutubeVideo.class);
	private static final String COL_SPEC = "left:pref, 3dlu, p, 3dlu, 0:grow";
	private static final String ROW_SPEC = "p, 3dlu, p, 3dlu, p";
	public static final String ID = "youtubeVideoPlayer";
	protected JTextField scale;
	protected JCheckBox experimentalCodecs;
	protected JCheckBox audioSyncEnabled;
	protected JTextField sampleRate;
	protected JCheckBox sampleRateOverride;
	SystemUtils registry = PMS.get().getRegistry();

	protected boolean videoRemux;

	public YoutubeVideo() {
	}

	@Override
	public int purpose() {
		return VIDEO_WEBSTREAM_PLAYER;
	}

	@Override
	public String id() {
		return ID;
	}

	@Override
	public String[] args() {
		return new String[]{};
	}

	@Override
	public String name() {
		return "Youtube";
	}

	@Override
	public int type() {
		return Format.VIDEO;
	}

	@Override
	public String mimeType() {
		return HTTPResource.VIDEO_TRANSCODE;
	}

	@Override
	public String executable() {
		return configuration.getPath("youtube-dl");
	}

	@Override
	public ProcessWrapper launchTranscode(DLNAResource dlna, DLNAMediaInfo media, OutputParams params) throws IOException {
	    PmsConfiguration prev = configuration;
        // Use device-specific pms conf
        configuration = (DeviceConfiguration) params.mediaRenderer;
        final String filename = dlna.getSystemName();
        params.waitbeforestart = 2000;
        params.manageFastStart();
        RendererConfiguration renderer = params.mediaRenderer;

		List<String> cmdList = new ArrayList<>();
		cmdList.add(executable());
		cmdList.add("-f");
		cmdList.add("mp4");
		cmdList.add("-o");
        cmdList.add("-");

		// Skip forward if necessary
		StringBuilder url = new StringBuilder(dlna.getSystemName());
		if (params.timeseek != 0) {
		    url.append("?t=");
		    url.append(String.valueOf(params.timeseek));
		}
        cmdList.add(url.toString());
        
        // Pass to process wrapper
        String[] cmdArray = new String[cmdList.size()];
        cmdList.toArray(cmdArray);

        cmdArray = finalizeTranscoderArgs(filename, dlna, media, params, cmdArray);

        ProcessWrapperImpl pw = new ProcessWrapperImpl(cmdArray, params);
        pw.runInNewThread();

        configuration = prev;
        return pw;
	}

	@Override
	public JComponent config() {
		// Apply the orientation for the locale
		ComponentOrientation orientation = ComponentOrientation.getOrientation(PMS.getLocale());
		String colSpec = FormLayoutUtil.getColSpec(COL_SPEC, orientation);
		FormLayout layout = new FormLayout(colSpec, ROW_SPEC);
		PanelBuilder builder = new PanelBuilder(layout);
		builder.border(Borders.EMPTY);
		builder.opaque(false);

		CellConstraints cc = new CellConstraints();

		JComponent cmp = builder.addSeparator(Messages.getString("NetworkTab.5"), FormLayoutUtil.flip(cc.xyw(1, 1, 5), colSpec, orientation));
		cmp = (JComponent) cmp.getComponent(0);
		cmp.setFont(cmp.getFont().deriveFont(Font.BOLD));

		experimentalCodecs = new JCheckBox(Messages.getString("VlcTrans.3"), configuration.isVlcExperimentalCodecs());
		experimentalCodecs.setContentAreaFilled(false);
		experimentalCodecs.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				configuration.setVlcExperimentalCodecs(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		builder.add(GuiUtil.getPreferredSizeComponent(experimentalCodecs), FormLayoutUtil.flip(cc.xy(1, 3), colSpec, orientation));

		audioSyncEnabled = new JCheckBox(Messages.getString("MEncoderVideo.2"), configuration.isVlcAudioSyncEnabled());
		audioSyncEnabled.setContentAreaFilled(false);
		audioSyncEnabled.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				configuration.setVlcAudioSyncEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		builder.add(GuiUtil.getPreferredSizeComponent(audioSyncEnabled), FormLayoutUtil.flip(cc.xy(1, 5), colSpec, orientation));

		JPanel panel = builder.getPanel();

		// Apply the orientation to the panel and all components in it
		panel.applyComponentOrientation(orientation);

		return panel;
	}

	@Override
	public boolean isCompatible(DLNAResource resource) {
	    return PlayerUtil.isWebVideo(resource) && PlayerUtil.isYoutubeVideo(resource);
	}
}
