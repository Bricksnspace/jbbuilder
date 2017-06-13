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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import bricksnspace.appsettings.AppSettings;
import bricksnspace.ldeditor.LDEditor;
import bricksnspace.ldrawlib.LDrawPartType;

/**
 * Asks for model metadata (author, name, description, type, comments)
 * 
 * @author Mario Pascucci
 *
 */
public class ModelDataDialog extends JDialog implements ActionListener {

	
	private static final long serialVersionUID = 6639434900754310510L;
	private JButton okButton;
	private JButton cancelButton;
	private JTextField descr;
	private JTextField author;
	private JComboBox<String> type;
	private JCheckBox license;
	private LDEditor model;

	
	
	
	public ModelDataDialog(Frame frame, String title, boolean modal, LDEditor m) {
		
		super(frame, title, modal);
		
		model = m;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		buttonPane.add(cancelButton);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;

		gbc.gridy = 0;
		gbc.gridx = 0;
		JLabel lab = new JLabel("Model description:");
		lab.setHorizontalAlignment(JLabel.RIGHT);
		contentPanel.add(lab,gbc);
		descr = new JTextField(m.getDescription());
		gbc.gridx = 1;
		contentPanel.add(descr,gbc);
		
		gbc.gridy++;
		gbc.gridx = 0;
		lab = new JLabel("Model author and e-mail:");
		lab.setHorizontalAlignment(JLabel.RIGHT);
		contentPanel.add(lab,gbc);
		author = new JTextField(m.getAuthor());
		gbc.gridx = 1;
		contentPanel.add(author,gbc);
		
		gbc.gridy++;
		gbc.gridx = 0;
		lab = new JLabel("Model type:");
		lab.setHorizontalAlignment(JLabel.RIGHT);
		contentPanel.add(lab,gbc);
		type = new JComboBox<String>();
		type.addItem("Unofficial Model");
		type.addItem("Unofficial Submodel");
		type.setSelectedIndex(0);
		gbc.gridx = 1;
		contentPanel.add(type,gbc);
		
		gbc.gridy++;
		license = new JCheckBox("Add license notice (see Options)");
		license.setSelected(false);
		gbc.gridx = 1;
		contentPanel.add(license, gbc);
		
		pack();

	}
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == cancelButton) {
			setVisible(false);
		}
		else if (e.getSource() == okButton) {
			setVisible(false);
			model.setAuthor(author.getText());
			model.setDescription(descr.getText());
			if (license.isSelected()) {
				model.setLicense(AppSettings.get("0 !LICENSE " +MySettings.LICENSE));
			}
			else {
				model.setLicense("");
			}
			switch (type.getSelectedIndex()) {
			case 0:
				model.setPartType(LDrawPartType.MODEL);
				break;
			case 1:
				model.setPartType(LDrawPartType.SUBMODEL);
				break;
			default:
				model.setPartType(LDrawPartType.MODEL);
			}
		}
		
	}

}
