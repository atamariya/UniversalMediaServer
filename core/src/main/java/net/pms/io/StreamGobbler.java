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
package net.pms.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamGobbler extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(StreamGobbler.class);
	BufferedReader in;
	private boolean logging;

	/**
	 * The stream consumer that reads and discards the stream
	 *
	 * @param in an ImputStream to be consumed
	 */
	public StreamGobbler(InputStream in) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.logging = false;
	}

	/**
	 * The stream consumer that reads and discards the stream
	 *
	 * @param in an ImputStream to be consumed
	 * @param enableLogging true if the stream should be logged to the TRACE level
	 */
	public StreamGobbler(InputStream in, boolean enableLogging) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.logging = enableLogging;
	}

	@Override
	public void run() {
		String line = null;
		try {
			while ((line = in.readLine()) != null) {
				if (logging && !line.startsWith("100")) {
					LOGGER.trace(line);
				}
			}
			in.close();
		} catch (IOException e) {
			LOGGER.trace("Caught exception", e);
		}

	}
}
