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
package net.pms.medialibrary.commons.enumarations;

public enum ConditionUnit {
	TIMESPAN_SECONDS, TIMESPAN_MINUTES, TIMESPAN_HOURS, TIMESPAN_DAYS, TIMESPAN_WEEKS, TIMESPAN_MONTHS, TIMESPAN_YEARS,

	FILESIZE_BYTE, FILESIZE_KILOBYTE, FILESIZE_MEGABYTE, FILESIZE_GIGABYTE, FILESIZE_TERABYTE,

	UNKNOWN
}
