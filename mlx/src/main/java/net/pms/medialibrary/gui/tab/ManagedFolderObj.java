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
package net.pms.medialibrary.gui.tab;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.pms.Messages;
import net.pms.medialibrary.commons.dataobjects.DOFileImportTemplate;
import net.pms.medialibrary.commons.dataobjects.DOManagedFile;
import net.pms.medialibrary.commons.exceptions.InitialisationException;
import net.pms.medialibrary.gui.shared.EButton;
import net.pms.medialibrary.library.LibraryManager;

public class ManagedFolderObj {
	private List<ActionListener> removeListeners = new ArrayList<ActionListener>();
	private List<ActionListener> propertyChangedListeners = new ArrayList<ActionListener>();

	private JCheckBox cbWatch;
	private JTextField tfFolderPath;
	private JCheckBox cbVideo;
	private JCheckBox cbAudio;
	private JCheckBox cbPictures;
	private JButton bBrowse;
	private JButton bScan;
	private JButton bDelete;
	private JCheckBox cbSubFolders;
	private EButton bConfigureFileImportTemplate;
	private int index;
	private JCheckBox cbEnablePlugins;

	public ManagedFolderObj(JCheckBox cbWatch, JTextField tfFolderPath, JCheckBox cbVideo, EButton bConfigureFileImportTemplate, JCheckBox cbAudio, JCheckBox cbPictures, JButton bBrowse, JButton bScan,
			JButton bDelete, JCheckBox cbSubFolders, JCheckBox cbEnablePlugins, int index) {
		setCbWatch(cbWatch);
		setTextFieldFolderPath(tfFolderPath);
		setCheckBoxVideo(cbVideo);
		setButtonConfigureFileImportTemplate(bConfigureFileImportTemplate);
		setCheckBoxAudio(cbAudio);
		setCheckBoxPictures(cbPictures);
		setButtonBrowse(bBrowse);
		setButtonScan(bScan);
		setButtonDelete(bDelete);
		setCheckBoxSubFolders(cbSubFolders);
		setIndex(index);
		setCheckBoxEnablePlugins(cbEnablePlugins);
	}

	@Override
	public String toString() {
		return String.format("File=%s, Watch=%s, Subfolders=%s, Video=%s, Audio=%s, Pictures=%s", tfFolderPath.getText(), cbWatch.isSelected(), cbSubFolders
				.isSelected(), cbVideo.isSelected(), cbAudio.isSelected(), cbPictures.isSelected());
	}

	public void setCbWatch(JCheckBox cbWatch) {
		this.cbWatch = cbWatch;

		cbWatch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				firePropertyChanged();
			}
		});
	}

	public JCheckBox getCbWatch() {
		return cbWatch;
	}

	public void setTextFieldFolderPath(JTextField folderPath) {
		this.tfFolderPath = folderPath;

		tfFolderPath.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				firePropertyChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				firePropertyChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				firePropertyChanged();
			}
		});

		if (folderPath != null) {
			folderPath.addCaretListener(new CaretListener() {

				@Override
				public void caretUpdate(CaretEvent e) {
					updateTextFieldFolderPathToolTip();
				}
			});

			folderPath.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					updateTextFieldFolderPathToolTip();
				}
			});
		}
	}

	public DOManagedFile getManagedFolder() {
		return new DOManagedFile(cbWatch.isSelected(), tfFolderPath.getText(), cbVideo.isSelected(), cbAudio.isSelected(), cbPictures.isSelected(),
				cbSubFolders.isSelected(), cbEnablePlugins.isSelected(), (DOFileImportTemplate) bConfigureFileImportTemplate.getUserObject());
	}

	public JTextField getTextFieldFolderPath() {
		return tfFolderPath;
	}

	public void setCheckBoxVideo(JCheckBox cbVideo) {
		this.cbVideo = cbVideo;

		cbVideo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				firePropertyChanged();
			}
		});
	}

	public JCheckBox getCheckBoxVideo() {
		return cbVideo;
	}

	public void setCheckBoxAudio(JCheckBox cbAudio) {
		this.cbAudio = cbAudio;

		cbAudio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				firePropertyChanged();
			}
		});
	}

	public JCheckBox getCheckBoxAudio() {
		return cbAudio;
	}

	public void setCheckBoxPictures(JCheckBox cbPictures) {
		this.cbPictures = cbPictures;

		cbPictures.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				firePropertyChanged();
			}
		});
	}

	public JCheckBox getCheckBoxPictures() {
		return cbPictures;
	}

	public void setButtonBrowse(JButton bBrowse) {
		this.bBrowse = bBrowse;

		if (bBrowse != null)
			bBrowse.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = null;
					File f = new File(tfFolderPath.getText());
					if (f.isDirectory()) {
						chooser = new JFileChooser(f.getAbsoluteFile());
					} else {
						chooser = new JFileChooser();
					}

					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnVal = chooser.showDialog((Component) e.getSource(), Messages.getString("FoldTab.28"));
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String folderPath = chooser.getSelectedFile().getAbsolutePath();
						tfFolderPath.setText(folderPath);
					}
				}
			});
	}

	public JButton getButtonBrowse() {
		return bBrowse;
	}

	public void setButtonScan(JButton bScan) {
		this.bScan = bScan;
		if (bScan != null) {
			bScan.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					scanFolder();
				}
			});
		}
	}

	private void scanFolder() {
		File f = new File(tfFolderPath.getText());
		if (f.isDirectory()) {
			try {
				LibraryManager.getInstance().scanFolder(getManagedFolder());
			} catch (InitialisationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(tfFolderPath.getTopLevelAncestor(), String.format(Messages.getString("ML.Messages.FolderDoesNotExist"), tfFolderPath.getText()));
		}
	}

	public JButton getbButtonScan() {
		return bScan;
	}

	public void setButtonDelete(JButton bDelete) {
		this.bDelete = bDelete;
		if (bDelete != null) {
			bDelete.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					fireRemove();
				}
			});
		}
	}

	public JButton getButtonDelete() {
		return bDelete;
	}

	public void setCheckBoxSubFolders(JCheckBox cbSubFolders) {
		this.cbSubFolders = cbSubFolders;

		cbSubFolders.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				firePropertyChanged();
			}
		});
	}

	public JCheckBox getCheckBoxSubFolders() {
		return cbSubFolders;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setButtonConfigureFileImportTemplate(EButton bConfigureFileImportTemplate) {
		this.bConfigureFileImportTemplate = bConfigureFileImportTemplate;
	}

	public EButton getButtonConfigureFileImportTemplate() {
		return bConfigureFileImportTemplate;
	}

	public DOFileImportTemplate getFileImportTemplate() {
		return bConfigureFileImportTemplate.getUserObject() instanceof DOFileImportTemplate ? (DOFileImportTemplate) bConfigureFileImportTemplate.getUserObject() : null;
	}

	public JCheckBox getCheckBoxEnablePlugins() {
		return cbEnablePlugins;
	}

	public void setCheckBoxEnablePlugins(JCheckBox cbEnablePlugins) {
		this.cbEnablePlugins = cbEnablePlugins;
		cbEnablePlugins.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (((JCheckBox) e.getSource()).isSelected()) {
					bConfigureFileImportTemplate.setEnabled(true);
				} else {
					bConfigureFileImportTemplate.setEnabled(false);
				}

				firePropertyChanged();
			}
		});
	}

	private void updateTextFieldFolderPathToolTip() {
		if (!tfFolderPath.getText().equals("") && tfFolderPath.getPreferredSize().width > tfFolderPath.getSize().width) {
			tfFolderPath.setToolTipText(tfFolderPath.getText());
		} else {
			tfFolderPath.setToolTipText(null);
		}
	}

	public void addRemoveListener(ActionListener removeListener) {
		if (!removeListeners.contains(removeListener)) {
			removeListeners.add(removeListener);
		}
	}

	private void fireRemove() {
		for (ActionListener l : removeListeners) {
			l.actionPerformed(new ActionEvent(this, 753, "Delete"));
		}
	}

	public void addPropertyChangedListener(ActionListener propertyChangedListener) {
		if (!propertyChangedListeners.contains(propertyChangedListener)) {
			propertyChangedListeners.add(propertyChangedListener);
		}
	}

	private void firePropertyChanged() {
		for (ActionListener l : propertyChangedListeners) {
			l.actionPerformed(new ActionEvent(this, 754, "PropertyChanged"));
		}
	}
}
