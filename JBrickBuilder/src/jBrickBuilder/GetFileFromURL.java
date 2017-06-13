/*
	Copyright 2013-2014 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BrickUtils

	BrickUtils is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BrickUtils is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with BrickUtils.  If not, see <http://www.gnu.org/licenses/>.

*/


package jBrickBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.SwingWorker;

import bricksnspace.busydialog.BusyDialog;



public class GetFileFromURL extends SwingWorker<Integer, Void> {

	private BusyDialog dlg;
	private URL updateUrl;
	private File dest;
	
	
	
	GetFileFromURL(URL url, File dest, BusyDialog dialog) {
		
		this.dlg = dialog;
		this.updateUrl = url;
		this.dest = dest;
	}
	
	
	@Override
	protected Integer doInBackground() throws IOException {

		// getting file
		dlg.setMsg("Downloading '"+dest.getName()+"'");
		File tempFile = new File(dest.getName()+".tmp");
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection connect = (HttpURLConnection) updateUrl.openConnection();
		int res = connect.getResponseCode();
		int tries = 0;
		while (res>=300 && res<400) {
			// it is a redirect
			updateUrl = new URL(connect.getHeaderField("Location"));
			//System.out.println(updateUrl);
			// get new connection
			connect = (HttpURLConnection) updateUrl.openConnection();
			res = connect.getResponseCode();
			tries++;
			if (tries > 4) {
				throw new IOException("Too many redirect, aborted");
			}
		}
		int fileLen = connect.getContentLength();
		//System.out.println(fileLen);
		byte[] buffer = new byte[4096];
		if (fileLen != 0) {
			FileOutputStream temp = new FileOutputStream(tempFile);
			InputStream remoteFile = connect.getInputStream();
			int r;
			int total = 0;
			while ((r = remoteFile.read(buffer)) > 0) {
				temp.write(buffer, 0, r);
				total += r;
				if ((total % 50000) < 4095) {
					setProgress(total/(fileLen/100));
				}
			}
			temp.close();
			dest.delete();
			tempFile.renameTo(dest);
			remoteFile.close();
		}
		return fileLen;
	}

	
}
