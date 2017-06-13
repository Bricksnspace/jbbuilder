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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A component that shows a list of most recently used object in a popup menu
 * 
 * @author Mario Pascucci
 *
 */
public class PartMRUChooser extends JDialog implements ActionListener {

	private static final long serialVersionUID = 3382846872044958792L;
	private int size;
	private Deque<String> mruList = new ArrayDeque<String>();
	private Deque<JButton> itemList = new ArrayDeque<JButton>();
	private JPanel itemPane = new JPanel();
	private JButton cancelButton;
	private int response;
	private String selected;
		
	
	public PartMRUChooser(Frame frame, int size) {
		super(frame,true);
		setUndecorated(true);
		getRootPane().setBorder(BorderFactory.createRaisedBevelBorder());
		this.size = size;
		JButton dummy = new JButton("Empty");
		dummy.setPreferredSize(new Dimension(75, 75));
		dummy.setEnabled(false);
		itemPane.setLayout(new GridLayout(0, 4));
		
		JScrollPane sp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setViewportView(itemPane);
		//sp.setPreferredSize(new Dimension(340, 150));
		itemPane.add(dummy);
		getContentPane().add(sp, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane,BorderLayout.SOUTH);
		cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);
		
		response = JOptionPane.CANCEL_OPTION;
		pack();
	}
	

	public void addElement(String id, String name, Image img) {
		
		if (mruList.contains(id)) {
			// part is already in list
			return;
		}
		if (mruList.size() == 0) {
			// first insert, remove "No parts"
			itemPane.removeAll();
		}
		JButton item = new JButton(new ImageIcon(img));
		item.setName(id);
		item.setPreferredSize(new Dimension(75, 75));
		item.setToolTipText(name);
		item.addActionListener(this);
		item.setActionCommand("MRU");
		item.setMargin(new Insets(2, 2, 2, 2));
		// add new part to list
		itemList.addFirst(item);
		mruList.addFirst(id);
		if (itemList.size() > size) {
			// if list is too big, remove last part
			JButton deleted = itemList.removeLast();
			mruList.removeLast();
			itemPane.remove(deleted);
		}
		// add to first position
		itemPane.add(item,0);
		if (itemList.size() > 20) {
			itemPane.getParent().setPreferredSize(new Dimension(350, 390));
		}
		pack();
	}

	
	public int getResponse() {
		
		return response;
	}
	
	
	public String getSelected() {
		
		return selected;
	}
	

	
	public void showLocation(JComponent parent) {
		
//		selected = "";
		setLocation(parent.getLocationOnScreen().x+10,parent.getLocationOnScreen().y+10);
	}

	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() ==  cancelButton) {
			response = JOptionPane.CANCEL_OPTION;
			setVisible(false);
		}
		else if (e.getActionCommand().equals("MRU")) {
			JButton cb = (JButton) e.getSource();
			selected = cb.getName();
			response = JOptionPane.OK_OPTION;
			setVisible(false);
		}
		

	}
	
	

}
