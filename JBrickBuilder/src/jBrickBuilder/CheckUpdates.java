/*
	Copyright 2015 Mario Pascucci <mpascucci@gmail.com>
	This file is part of JBrickBuilder

	JBrickBuilder is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	JBrickBuilder is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with JBrickBuilder.  If not, see <http://www.gnu.org/licenses/>.

*/


package jBrickBuilder;



import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;



/**
 * Simple update checker
 * 
 * @author Mario Pascucci
 *
 */
public class CheckUpdates {

	
	/**
	 * gets a version from an URL
	 * 
	 * version is a plain text file with a string representing an integer
	 * 
	 * @param updateUrl
	 * @return version read from url
	 */
	public static int getUrlVersion(String updateUrl) {

		URL updUrl;
		try {
			updUrl = new URL(updateUrl);
		} catch (MalformedURLException e1) {
			Logger.getGlobal().log(Level.WARNING, "[getUrlVersion] Malformed URL for update checks.", e1);
			return -1;
		}
		URLConnection connect = null;
		InputStream urlStream = null;
		try {
			HttpURLConnection.setFollowRedirects(false);
			connect = updUrl.openConnection();
			urlStream = connect.getInputStream();
		}
		catch (IOException ex) {
			// probably no Internet connection is available...
			return -1;
		}
		if (connect.getContentLength() == 0) {
			// no updates
			return -1;
		}
		InputStreamReader isr = new InputStreamReader(urlStream);
		String readVer = "";
		try {
			int c;
			do {
				c = isr.read();
				if (c != -1) {
					readVer += (char)c;
				}
			} while (c != -1);
			readVer = readVer.trim();
			try {
				int ver = Integer.parseInt(readVer);
				return ver;
			} 
			catch (NumberFormatException e) {
				return -1;
			}
		}
		catch (IOException e) {
			Logger.getGlobal().log(Level.WARNING, "[getUrlVersion] Unable to read remote version.", e);
			return -1;
		}
	}
	
	
	/**
	 * gets version from a zipfile
	 * Version number is inside a text file called VERSION as a plain integer
	 * 
	 * @param zf
	 * @return version from zipfile
	 */
	public static int getZipVersion(String zf) {
		
		ZipFile z;
		try {
			z = new ZipFile(zf);
		} catch (IOException e) {
			Logger.getGlobal().log(Level.WARNING, "[getZipVersion] Unable to read zipfile.", e);
			return -1;
		}
		ZipEntry ze = z.getEntry("VERSION");
		if (ze == null) {
			try {
				z.close();
			} catch (IOException e) {
			}
			return -1;
		}
		String readVer;
		try {
			InputStream is = z.getInputStream(ze);
			readVer = "";
			int c;
			do {
				c = is.read();
				if (c != -1) {
					readVer += (char)c;
				}
			} while (c != -1);
			z.close();
		} catch (IOException e) {
			Logger.getGlobal().log(Level.WARNING, "[getZipVersion] Unable to read version file.", e);
			return -1;
		}
		readVer = readVer.trim();
		try {
			int ver = Integer.parseInt(readVer);
			return ver;
		} 
		catch (NumberFormatException e) {
			return -1;
		}
	}
	
}
