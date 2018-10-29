/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.pms.plugin.fileimport.thetvdb.configuration;

import java.io.IOException;
import java.util.Map;

import net.pms.configuration.BaseConfiguration;

/**
 *
 * @author Corey
 */
public class GlobalConfiguration extends BaseConfiguration {

	private static final String KEY_importLanguage = "importLanguage";

	/** The properties file path. */
	private String propertiesFilePath;
	private Map<String, String> supportedLanguages;

	/**
	 * Instantiates a new global configuration.
	 */
	public GlobalConfiguration() {
		propertiesFilePath = getGlobalConfigurationDirectory() + "TheTVDBImportPlugin.conf";
	}

	/**
	 * Save the current configuration.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void save() throws IOException {
		save(propertiesFilePath);
	}

	/**
	 * Load the last saved configuration.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void load() throws IOException {
		load(propertiesFilePath);
	}

	/**
	 * Gets the language to use when importing data
	 * 
	 * @return the language. e.g. en, de, fr
	 */
	public String getImportLanguage() {
		return getValue(KEY_importLanguage, "en");
	}

	/**
	 * Sets the language to use when importing data
	 */
	public void setImportLanguage(String importLanguage) {
		setValue(KEY_importLanguage, importLanguage);
	}

	/**
	 * Gets the supported languages.<br>
	 * Key=language abbreviation<br>
	 * Value=language to display
	 *
	 * @return the supported languages
	 */
	public Map<String, String> getSupportedLanguages() {
		return supportedLanguages;
	}

	/**
	 * Sets the supported languages.<br>
	 * Key=language abbreviation<br>
	 * Value=language to display
	 *
	 * @param supportedlanguages the supported languages
	 */
	public void setSupportedLanguages(Map<String, String> supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}
}
