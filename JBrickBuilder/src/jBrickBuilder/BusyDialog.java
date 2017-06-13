/*
	Copyright 2013-2014 Mario Pascucci <mpascucci@gmail.com>
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;


public class BusyDialog extends JDialog implements ActionListener {

	@SuppressWarnings("rawtypes")
	private SwingWorker task;
	private static final long serialVersionUID = 5600670512283134040L;
	private boolean progress = false;
	private JLabel msg;
	private JProgressBar pgr;
	private ImageIcon[] animIcn;
	private static int icnFrame = 0;

	public BusyDialog(JFrame owner, String title, boolean modal, boolean progress, ImageIcon[] icn) {
		
		super(owner,title,modal);
		this.progress = progress;
		animIcn = icn;
		setLocationByPlatform(true);
		getRootPane().setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		msg = new JLabel("...                ");
		msg.setIcon(animIcn[0]);
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(msg);
		if (progress) {
			// add a progress bar
			pgr = new JProgressBar(SwingConstants.HORIZONTAL,0,100);
			pgr.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
			pgr.setMinimum(0);
			pgr.setMaximum(100);
			pgr.setStringPainted(true);
			getContentPane().add(pgr);
		}
		pack();
	}

	
	@SuppressWarnings("rawtypes")
	public void setTask(SwingWorker task) {
		
		this.task = task;
	}
	
	
	public void setMsg(String txt) {
		
		msg.setText(txt);
	}
	
	public void setIcon(ImageIcon icn) {
		
		msg.setIcon(icn);
	}
	
	public void setProgress(int val) {
		
		if (progress) {
			pgr.setValue(val);
		}
	}
	
	
	public void actionPerformed(ActionEvent e) {
		
		if (task.isDone()) {
			setVisible(false);
			((Timer)e.getSource()).stop();
			return;
		}
		else {
			if (!isVisible()) {
				setVisible(true);
			}
		}
		msg.setIcon(animIcn[icnFrame]);
		if (progress)
			pgr.setValue(task.getProgress());
		icnFrame = (icnFrame +1) % animIcn.length;
		pack();
	}
	
}
