/*
	Copyright 2014 Mario Pascucci <mpascucci@gmail.com>
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import bricksnspace.appsettings.AppUIResolution;
import bricksnspace.ldrawlib.LDrawColor;

/**
 * @author mario
 *
 */
public class ColorToolbar extends JToolBar implements ActionListener {

	private static final long serialVersionUID = 2951730739098372722L;
	private int historySize;
	private int selected;
	//private JButton[] buttons;
	private int[] mruCounters;
	private int[] mruColors;
	private JButton mainButton;
	private LDrawColorDialog colorDialog;
	private ImageIcon empty,emptytr,mainb,mainbtr;
	private ColorChangeListener ccl = null;
	private String imageFolder;
	
	
	/**
	 * @param orientation
	 */
	public ColorToolbar(LDrawColorDialog colorDialog, int orientation, int history) {
		
		super(orientation);
		imageFolder = AppUIResolution.getImgDir();
		//empty = new ImageIcon(new BufferedImage(10,24,BufferedImage.TYPE_3BYTE_BGR));
		if (orientation == JToolBar.HORIZONTAL) {
			empty = new ImageIcon(this.getClass().getResource(imageFolder+"empty.png"));
			emptytr = new ImageIcon(this.getClass().getResource(imageFolder+"empty-tr.png"));
		}
		else {
			empty = new ImageIcon(this.getClass().getResource(imageFolder+"empty-v.png"));
			emptytr = new ImageIcon(this.getClass().getResource(imageFolder+"empty-tr-v.png"));
		}
		if (history < 1 || history > 20) 
			throw new IllegalArgumentException("History size must be in 1..10 colors");
		historySize = history;
		this.colorDialog = colorDialog;
		//buttons = new JButton[historySize];
		mruCounters = new int[historySize];
		mruColors = new int[historySize];
		mainb = new ImageIcon(this.getClass().getResource(imageFolder+"brick.png"));
		mainbtr = new ImageIcon(this.getClass().getResource(imageFolder+"brick-wire.png"));
		mainButton = new JButton(mainb);
		selected = this.colorDialog.getSelected();
		Color s = LDrawColor.getColorById(selected);
		if (s.getAlpha() < 255) { 
			// transparency
			mainButton.setIcon(mainbtr);
			mainButton.setBackground(new Color(s.getRGB()));
		}
		else {
			mainButton.setIcon(mainb);
			mainButton.setBackground(s);
		}
		mainButton.setToolTipText(this.colorDialog.getSelectedName());
		mainButton.setRolloverEnabled(false);
		add(mainButton);
		for (int i=0;i<historySize;i++) {
			JButton b = new JButton(empty);
			//b.setPreferredSize(mainButton.getPreferredSize());
			//b.setSize(mainButton.getSize());
			b.setEnabled(false);
			add(b);
			mruCounters[i] = 0;
			mruColors[i] = -1;
		}
		mainButton.addActionListener(this);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == mainButton) {
			colorDialog.setLocation(getLocationOnScreen().x-colorDialog.getSize().width,
					getLocationOnScreen().y+20);
			colorDialog.setVisible(true);
			if (colorDialog.getResponse() == JOptionPane.OK_OPTION) {
				selected = colorDialog.getSelected();
				Color s = LDrawColor.getColorById(selected);
				if (s.getAlpha() < 255) { 
					// transparency
					mainButton.setIcon(mainbtr);
					mainButton.setBackground(new Color(s.getRGB()));
				}
				else {
					mainButton.setIcon(mainb);
					mainButton.setBackground(s);
				}
				mainButton.setToolTipText(colorDialog.getSelectedName());
				// add a button
				int minUsed = 1000;
				int index = -1;
				// checks if color is already in list
				for (int i=0;i<historySize;i++) {
					if (mruColors[i] == selected) {
						// is in list, increment counter
						mruCounters[i]++;
						// no more action required
						index = -1;
						break;
					}
					// search for least used color in list
					if (minUsed > mruCounters[i]) {	 
						minUsed = mruCounters[i];
						index = i;
					}
				}
				if (index > -1) {
					// index is position of button to change
					JButton b = new JButton();
					b.addActionListener(this);
					Color c = LDrawColor.getColorById(selected);
					if (c.getAlpha() < 255) { 
						// transparency
						b.setIcon(emptytr);
						b.setBackground(new Color(c.getRGB()));
					}
					else {
						b.setIcon(empty);
						b.setBackground(c);
					}
					b.setToolTipText(colorDialog.getSelectedName());
					b.setActionCommand("color");
					b.setName(Integer.toString(selected));
					b.setRolloverEnabled(false);
					mruCounters[index] = 1;
					mruColors[index] = selected;
					remove(index+1);
					add(b,index+1);
				}
				if (ccl != null) {
					ccl.colorChanged(selected);
				}
			}

		}
		else if (e.getActionCommand().equalsIgnoreCase("color")) {
			JButton b = (JButton) e.getSource();
			selected = Integer.parseInt(b.getName());
			for (int i=0;i<historySize;i++) {
				if (mruColors[i] == selected) {
					mruCounters[i]++;
					break;
				}
			}
			colorDialog.setSelected(selected);
			Color s = LDrawColor.getColorById(selected);
			if (s.getAlpha() < 255) { 
				// transparency
				mainButton.setIcon(mainbtr);
				mainButton.setBackground(new Color(s.getRGB()));
			}
			else {
				mainButton.setIcon(mainb);
				mainButton.setBackground(s);
			}
			mainButton.setToolTipText(colorDialog.getSelectedName());
			if (ccl != null) {
				ccl.colorChanged(selected);
			}

		}
	}
	
	
	public void setColorChangeListener(ColorChangeListener cc) {
		
		if (cc == null) 
			throw new IllegalArgumentException("Color Change listener cannot be null");
		ccl = cc;
	}
	
	
	public int getSelected() {
		return selected;
	}

}
