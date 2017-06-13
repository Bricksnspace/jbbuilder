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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.GLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import bricksnspace.j3dgeom.Matrix3D;
import bricksnspace.ldraw3d.LDRenderedPart;
import bricksnspace.ldraw3d.LDrawGLDisplay;
import bricksnspace.ldrawlib.ConnectionPoint;
import bricksnspace.ldrawlib.LDFlexPart;
import bricksnspace.ldrawlib.LDPrimitive;
import bricksnspace.ldrawlib.LDrawColor;
import bricksnspace.ldrawlib.LDrawLib;
import bricksnspace.ldrawlib.LDrawPart;
import bricksnspace.ldrawlib.LDrawPartCategory;



/**
 * @author Mario Pascucci
 *
 */
public class LDrawPartChooser extends JDialog implements ActionListener, ListSelectionListener, DocumentListener {

	private static final long serialVersionUID = -797889575391092019L;
	private JTextField query;
	private LDrawLib ldr;
	private LDrawGLDisplay preview;
	private JTable resTable;
	private JButton okButton;
	private JButton cancelButton;
	private LdrTableModel selListModel;
	private int userChoice;
	private final int size = 300;
	private JButton resetButton;
	private BufferedImage selected;
	private JList<LDrawPartCategory> catList;
	private JButton flexButton;
	private boolean useFlexPart;
	private static Color fileConn = new Color(128,255,128);
	private static Color fileConnSel = new Color(128,230,128);
	private static Color autoConn = new Color(128,255,255);
	private static Color autoConnSel = new Color(128,230,230);

	
	
	private class ConnCellRenderer extends JLabel implements TableCellRenderer {
		
		private static final long serialVersionUID = -8389141922988094775L;

	    public ConnCellRenderer() {
	        setOpaque(true); //MUST do this for background to show up.
	    }

	    
	    public Component getTableCellRendererComponent(
	                            JTable table, Object mapid,
	                            boolean isSelected, boolean hasFocus,
	                            int row, int column) {
	    	
	    	if (ConnectionPoint.existsConnectionFile((String) mapid)) {
	    		if (isSelected)
	    			setBackground(fileConnSel);
	    		else 
	    			setBackground(fileConn);
	    	}
	    	else if (ConnectionPoint.isAutoconnChecked((String) mapid)) {
	    		if (isSelected)
	    			setBackground(autoConnSel);
	    		else 
	    			setBackground(autoConn);	    		
	    	}
	    	else {
	    		if (isSelected) 
	    			setBackground(table.getSelectionBackground());
	    		else 
	    			setBackground(table.getBackground());
	    	}
	    	setText((String)mapid);
	    	setFont(table.getFont());
	        return this;
	    }

	}
	
	
	
	
	public LDrawPartChooser(Frame owner, String title, boolean modal, LDrawLib ldl) {
		
		super(owner, title,modal);
		
		ldr = ldl;
		Container root = getContentPane();
		root.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		query = new JTextField(30);
		addWindowListener(new WindowAdapter() {
			public void windowGainedFocus(WindowEvent e) {
				query.requestFocusInWindow();
			}
		});
		query.setBorder(BorderFactory.createTitledBorder("Search terms"));
		query.getDocument().addDocumentListener(this);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.ipadx = 2;
		gbc.ipady = 2;
		gbc.weightx = 0.2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);

		root.add(query,gbc);
		resetButton = new JButton("Clear");
		resetButton.addActionListener(this);
		resetButton.setToolTipText("Clear search and reset table");

		gbc.gridx = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		root.add(resetButton,gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		JLabel l = new JLabel("<html><p>Search hints: "
				+ "'brick*' returns 'brick' 'bricks' 'bricked'...</p>"
				+ "<p>'brick -duplo' exclude word 'Duplo'</p>"
				+ "<p>'brick +axle' must include word 'axle'</p>"
				+ "</html>");
		root.add(l,gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		selListModel = new LdrTableModel();
		resTable = new JTable(selListModel);
		resTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		resTable.setAutoCreateRowSorter(true);
		resTable.getSelectionModel().addListSelectionListener(this);
		TableColumnModel tcm = resTable.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(100);
		tcm.getColumn(1).setPreferredWidth(300);
		tcm.getColumn(2).setPreferredWidth(100);
		tcm.getColumn(3).setPreferredWidth(100);
		resTable.getColumnModel().getColumn(0).setCellRenderer(new ConnCellRenderer());

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridheight = 4;
		gbc.gridwidth = 2;

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(600,300));
		scrollPane.setViewportView(resTable);
		
		root.add(scrollPane,gbc);

		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 3;
		gbc.weighty = 0.4;
		gbc.fill = GridBagConstraints.BOTH;
		
		catList = new JList<LDrawPartCategory>(LDrawPartCategory.values());
		//catList.setVisibleRowCount(10);
		catList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		catList.addListSelectionListener(this);
		JScrollPane cl = new JScrollPane();
		cl.setViewportView(catList);
		root.add(cl,gbc);

		try {
			preview = new LDrawGLDisplay();
		} catch (GLException e) {
			Logger.getGlobal().log(Level.SEVERE,"OpenGL error", e);			
		}
		JPanel previewPanel = new JPanel();
		previewPanel.setBorder(BorderFactory.createTitledBorder("Part preview"));
		preview.getCanvas().setPreferredSize(new Dimension(size,size));
		gbc.gridx = 3;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		previewPanel.add(preview.getCanvas());
		root.add(previewPanel,gbc);

		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.weighty = 0.0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		root.add(buttonPane, gbc);

		okButton = new JButton("OK");
		buttonPane.add(okButton);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);

		flexButton = new JButton("Flex part");
		buttonPane.add(flexButton);
		flexButton.setEnabled(false);
		flexButton.addActionListener(this);

		cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);
		pack();

		// prevent exiting without setting appropriate values on userChoice
	    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				query.requestFocusInWindow();
    			selected = null;
			}
            public void windowClosing(WindowEvent ev) {
            	userChoice = JOptionPane.CANCEL_OPTION;
    			setVisible(false);
            }
		});
	}
	
	

	public LDrawPart getSelected() {
		if (resTable.getSelectedRow() >= 0) {
			return selListModel.getPart(resTable.convertRowIndexToModel(resTable.getSelectedRow()));
		}
		return null;
	}

	
	public BufferedImage getSelImage() {
		return selected;
	}

	
	public int getResponse() {
		return userChoice;
	}
	
	
	
	public boolean isFlexPart() {
		return useFlexPart;
	}
	
	

	@Override
	public void actionPerformed(ActionEvent ev) {
		
		if (ev.getSource() == okButton || ev.getSource() == flexButton) {
			userChoice = JOptionPane.OK_OPTION;
			setVisible(false);
			selected = preview.getScreenShot();
			if (ev.getSource() == flexButton) {
				useFlexPart = true;
			}
			else {
				useFlexPart = false;
			}
		}
		else if (ev.getSource() == resetButton) {
			resTable.getRowSorter().setSortKeys(null);
			query.setText("");
			query.requestFocusInWindow();
		}
		else if (ev.getSource() == cancelButton) {
			userChoice = JOptionPane.CANCEL_OPTION;
			setVisible(false);
		}
		
	}



	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		if (e.getValueIsAdjusting())
			return;
		if (e.getSource() == resTable.getSelectionModel()) {
			if (resTable.getSelectedRow() < 0)
				return;
			String part = (String) selListModel.getValueAt(
					resTable.convertRowIndexToModel(resTable.getSelectedRow()),0);
			flexButton.setEnabled(LDFlexPart.isFlexPart(part));
			//System.out.println(part+ "-" + currentPart); // DB
			preview.disableAutoRedraw();
			preview.clearAllParts();
			LDRenderedPart currentPart = LDRenderedPart.newRenderedPart(
					LDPrimitive.newGlobalPart(part, LDrawColor.LTGRAY, new Matrix3D()));
			double diagxz;
			float angle = 20f;
			if (currentPart.getSizeZ() > currentPart.getSizeX()) {
				angle = 70;
			}
			diagxz = Math.sqrt(currentPart.getSizeX()*currentPart.getSizeX() +
				currentPart.getSizeZ()*currentPart.getSizeZ());
			double diagxy = Math.sqrt(currentPart.getSizeX()*currentPart.getSizeX() +
					currentPart.getSizeY()*currentPart.getSizeY());
			float diag = (float) Math.max(diagxz,diagxy);
			float ratio = (float) (diag/(size-50f));
			preview.resetView();
			preview.rotateY(angle);
			preview.rotateX(-30);
			preview.setOrigin(currentPart.getCenterX(), currentPart.getCenterY(), currentPart.getCenterZ());
			preview.setZoom(ratio);
			preview.enableAutoRedraw();
			preview.addRenderedPart(currentPart);
		}
		else if (e.getSource() == catList) {
			if (catList.getSelectedIndex() == -1) {
				return;
			}
			LDrawPartCategory lc = (LDrawPartCategory) catList.getSelectedValue();
			try {
				selListModel.setParts(ldr.getLdrDB().getByCategory(lc));
			}
			catch (SQLException ex) {
				return;
			}
		}
	}


	
	private void updateList() {
		
		// if there are two or more not blank character do a search
		String q = (query.getText()).trim();		
		if (q.length() >= 2) {
			ArrayList<LDrawPart> list;
			try {
				list = ldr.getLdrDB().getFTS(q,100);
				if (list.size() == 0) {
					list = ldr.getLdrDB().getFTS(q+"*", 100);
				}
			} catch (SQLException e1) {
				return;
			}
			selListModel.setParts(list);
		}
		query.requestFocusInWindow();
	}

	

	
	

	@Override
	public void insertUpdate(DocumentEvent e) {
		
		updateList();
	}



	@Override
	public void removeUpdate(DocumentEvent e) {
		
		updateList();
	}



	@Override
	public void changedUpdate(DocumentEvent e) {
		
		updateList();
	}

}
