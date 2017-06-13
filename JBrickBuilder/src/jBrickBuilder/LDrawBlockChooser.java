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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.GLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import bricksnspace.j3dgeom.Matrix3D;
import bricksnspace.ldraw3d.LDRenderedPart;
import bricksnspace.ldraw3d.LDrawGLDisplay;
import bricksnspace.ldrawlib.LDPrimitive;
import bricksnspace.ldrawlib.LDrawColor;
import bricksnspace.ldrawlib.LDrawPart;



/**
 * @author Mario Pascucci
 *
 */
public class LDrawBlockChooser extends JDialog implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = -797889591092019L;
	private LDrawGLDisplay preview;
	private LDRenderedPart currentPart = null;
	private JTable resTable;
	private JButton okButton;
	private JButton cancelButton;
	private BlockTableModel selListModel;
	private int userChoice;
	private final int sizex = 600,sizey = 300;
	private List<LDrawPart> blockList = new ArrayList<LDrawPart>();
	private JButton editButton;
	private JCheckBox previewEnable;
	private Set<String> unsavedPart;
	private static Color autoConn = new Color(255,128,128);
	private static Color autoConnSel = new Color(230,128,128);

	
	
	
	private class UnsavedCellRenderer extends JLabel implements TableCellRenderer {
		
		private static final long serialVersionUID = -838914192223454775L;

	    public UnsavedCellRenderer() {
	        setOpaque(true); //MUST do this for background to show up.
	    }

	    
	    public Component getTableCellRendererComponent(
	                            JTable table, Object mapid,
	                            boolean isSelected, boolean hasFocus,
	                            int row, int column) {
	    	
	    	if (unsavedPart.contains((String) mapid)) {
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
	
	
	
	
	public LDrawBlockChooser(Frame owner, String title, Set<String> unsaved, boolean modal) {
		
		super(owner, title,modal);
		
		unsavedPart = unsaved;
		Container root = getContentPane();
		root.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.ipadx = 2;
		gbc.ipady = 2;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		selListModel = new BlockTableModel();
		resTable = new JTable(selListModel);
		resTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		resTable.setAutoCreateRowSorter(true);
		resTable.getSelectionModel().addListSelectionListener(this);
		TableColumnModel tcm = resTable.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(100);
		tcm.getColumn(1).setPreferredWidth(300);
		tcm.getColumn(2).setPreferredWidth(50);
		tcm.getColumn(0).setCellRenderer(new UnsavedCellRenderer());
		//tcm.getColumn(3).setPreferredWidth(100);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(600,300));
		scrollPane.setViewportView(resTable);
		
		root.add(scrollPane,gbc);
		
		try {
			preview = new LDrawGLDisplay();
		} catch (GLException e) {
			Logger.getGlobal().log(Level.SEVERE,"OpenGL error", e);
		}
		JPanel previewPanel = new JPanel();
		previewPanel.setBorder(BorderFactory.createTitledBorder("Part preview"));
		preview.getCanvas().setPreferredSize(new Dimension(sizex,sizey));
		gbc.gridx = 0;
		gbc.gridy = 1;
		previewPanel.add(preview.getCanvas());
		root.add(previewPanel,gbc);
		//preview.enableAxis();

		gbc.gridx = 0;
		gbc.gridy = 2;

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		root.add(buttonPane, gbc);

		previewEnable = new JCheckBox("Preview");
		buttonPane.add(previewEnable);
		previewEnable.setSelected(true);
		previewEnable.addActionListener(this);		
		
		editButton = new JButton("Edit");
		buttonPane.add(editButton);
		editButton.addActionListener(this);
		
		okButton = new JButton("Insert");
		buttonPane.add(okButton);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);
		pack();

		//currentModel = currMod;
		
		// prevent exiting without setting appropriate values on userChoice
	    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			@Override
            public void windowClosing(WindowEvent ev) {
            	userChoice = JOptionPane.CANCEL_OPTION;
    			setVisible(false);
            }
            
			@Override
            public void windowActivated(WindowEvent e) {
            	blockList.clear();
            	for (LDrawPart p: LDrawPart.getAllCustomParts()) {
           			blockList.add(p);
            	}
            	selListModel.setParts(blockList);
            }
		});
	}
	
	

	public LDrawPart getSelected() {
		if (resTable.getSelectedRow() >= 0) {
			return selListModel.getPart(resTable.convertRowIndexToModel(resTable.getSelectedRow()));
		}
		return null;
	}


	
	public int getResponse() {
		return userChoice;
	}
	
	

	@Override
	public void actionPerformed(ActionEvent ev) {
		
		if (ev.getSource() == okButton) {
			userChoice = JOptionPane.OK_OPTION;
			setVisible(false);
		}
		else if (ev.getSource() == cancelButton) {
			userChoice = JOptionPane.CANCEL_OPTION;
			setVisible(false);
		}
		else if (ev.getSource() == editButton) {
			// dummy for "edit"
			userChoice = -42;
			setVisible(false);
		}
		
	}



	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		if (!previewEnable.isSelected())
			return;
		if (e.getValueIsAdjusting())
			return;
		if (resTable.getSelectedRow() < 0)
			return;
		String part = (String) selListModel.getValueAt(
				resTable.convertRowIndexToModel(resTable.getSelectedRow()),0);
		//System.out.println(part+ "-" + currentPart); // DB
//		if (currentPart != null && currentPart.getPlacedPart().getLdrawId().equals(part)) {
//			return;
//		}
		preview.disableAutoRedraw();
		preview.clearAllParts();
		try {
			currentPart = LDRenderedPart.newRenderedPart(
					LDPrimitive.newGlobalPart(part, LDrawColor.CURRENT, new Matrix3D()));
			float ratio = Math.max(currentPart.getSizeZ()/sizey, 
					Math.max(currentPart.getSizeX()/sizey,currentPart.getSizeY()/sizey));
			ratio *= 1.6f;
			preview.setZoom(ratio);
			preview.resetView();
			preview.rotateY(35);
			preview.rotateX(-30);
			preview.setOffsetx(currentPart.getCenterX());
			preview.setOffsety(currentPart.getCenterY());
			preview.enableAutoRedraw();
			preview.addRenderedPart(currentPart);
		} catch (OutOfMemoryError e1) {
			previewEnable.setSelected(false);
			currentPart = null;
		}
	}


}
