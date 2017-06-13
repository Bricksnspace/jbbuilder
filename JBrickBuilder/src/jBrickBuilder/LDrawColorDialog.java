/*
	Copyright 2014-2017 Mario Pascucci <mpascucci@gmail.com>
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

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import bricksnspace.appsettings.AppSettings;
import bricksnspace.ldrawlib.LDrawColor;
import bricksnspace.ldrawlib.LDrawColorType;

/**
 * @author Mario Pascucci
 *
 */
public class LDrawColorDialog extends JDialog implements ActionListener {

	
	private static final long serialVersionUID = 2256121120370657271L;
	
	private Map<Integer,JButton> buttons = new HashMap<Integer,JButton>();
	private JButton cancelButton;
	private int response;
	private int selected;
	private String selectedName;
	private ImageIcon selectedIcon;
	

	public LDrawColorDialog(Frame owner, String title, boolean modal, InputStream colors) throws FileNotFoundException, XMLStreamException {
		super(owner, title, modal);
		createDialog(colors);
	}


	public LDrawColorDialog(Dialog owner, String title, boolean modal, InputStream colors) throws FileNotFoundException, XMLStreamException {
		super(owner, title, modal);
		createDialog(colors);
	}


	public LDrawColorDialog(String title, boolean modal, InputStream colors) throws FileNotFoundException, XMLStreamException {
		super();
		setTitle(title);
		setModal(modal);
		createDialog(colors);
	}


	// TODO : qui
	private void createDialog(InputStream colors) throws FileNotFoundException, XMLStreamException {
		
		setUndecorated(true);
		selectedIcon = new ImageIcon(this.getClass().getResource("images/check-ok.png"));
		XMLInputFactory xmlFact = XMLInputFactory.newInstance();
		xmlFact.setProperty(XMLInputFactory.IS_COALESCING,true);
		XMLEventReader xer = xmlFact.createXMLEventReader(colors);
		Container pane = getContentPane();
		getRootPane().setBorder(BorderFactory.createRaisedBevelBorder());
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		boolean isDoc = false;
		boolean isColors = false;
		Map<LDrawColorType,JPanel> currPanel = new HashMap<LDrawColorType,JPanel>();
		Map<LDrawColorType,JPanel> legacyPanel = new HashMap<LDrawColorType,JPanel>();
		while (xer.hasNext()) {
			XMLEvent e = xer.nextEvent();
			switch (e.getEventType()) {
			case XMLEvent.START_DOCUMENT:
				isDoc = true;
				break;
			case XMLEvent.START_ELEMENT:
				String tag = e.asStartElement().getName().getLocalPart();
				if (tag.equals("colorgroups") && isDoc) {
					isColors = true;
				}
				else if (tag.equals("color") && isColors) {
					// read color
					boolean isCurrent = false;
					String inUse = e.asStartElement().getAttributeByName(new QName("inuse")).getValue().trim();
					isCurrent = inUse.equals("1");
					if (!AppSettings.getBool(MySettings.ALLCOLORS)) {
						// ignore retired/rare colors
						if (!isCurrent)
							break;
					}
					String colorName = e.asStartElement().getAttributeByName(new QName("name")).getValue().trim();
					int id = Integer.parseInt(e.asStartElement().getAttributeByName(new QName("id")).getValue().trim());
					if (!LDrawColor.isOfficialColor(id)) {
						Logger.getGlobal().log(Level.WARNING, "LDraw color id ("+id+") not found: you have obsolete/old LDraw library");
						break;
					}
					LDrawColorType group = LDrawColorType.valueOf(e.asStartElement().getAttributeByName(new QName("group")).getValue().trim());
					// checks if color group exists
					JButton cb = new JButton();
					cb.setMinimumSize(new Dimension(20,20));
					cb.setPreferredSize(new Dimension(20,20));
//					if (isCurrent 
//							|| LDrawColor.getById(id).getType() == LDrawColorType.INTERNAL
//							|| LDrawColor.getById(id).getType() == LDrawColorType.RUBBER
//							) {
//						cb.setToolTipText(colorName);
//					}
//					else {
//						cb.setToolTipText(colorName+" (Legacy)");
//					}
					//cb.setBorderPainted(false);
					cb.setRolloverEnabled(false);
					cb.addActionListener(this);
					cb.setBackground(LDrawColor.getColorById(id));
					cb.setActionCommand("color");
					cb.setName(Integer.toString(id));
					buttons.put(id, cb);
					if (isCurrent 
							|| LDrawColor.getById(id).getType() == LDrawColorType.INTERNAL
							|| LDrawColor.getById(id).getType() == LDrawColorType.RUBBER
							) {
						if (!currPanel.containsKey(group)) {
							JPanel p = new JPanel();
							//p.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
							p.setLayout(new GridLayout(0, 20));
							currPanel.put(group,p);
						}
						cb.setToolTipText(colorName);
						currPanel.get(group).add(cb);
					}
					else {
						if (!legacyPanel.containsKey(group)) {
							JPanel p = new JPanel();
							p.setBorder(BorderFactory.createTitledBorder("Old/Legacy/Rare"));
							p.setLayout(new GridLayout(0, 19));
							legacyPanel.put(group,p);
						}
						cb.setToolTipText(colorName+" (Legacy)");
						legacyPanel.get(group).add(cb);
					}
				}
				break;
			case XMLEvent.END_ELEMENT:
				String endtag = e.asEndElement().getName().getLocalPart();
				if (endtag.equals("colorgroups")) {
					isColors = false;
				}
				break;
			}
		}
		
		xer.close();
		for (LDrawColorType c:LDrawColorType.values()) {
			if (currPanel.containsKey(c) || legacyPanel.containsKey(c)) {
				JPanel p = new JPanel();
				p.setBorder(BorderFactory.createTitledBorder(c.toString()));
				p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
				if (currPanel.containsKey(c)) {
					p.add(currPanel.get(c));
				}
//				if (currPanel.containsKey(c) && legacyPanel.containsKey(c)) {
//					p.add(new JSeparator(SwingConstants.HORIZONTAL));
//				}
				if (legacyPanel.containsKey(c)) {
					p.add(legacyPanel.get(c));
				}
				pane.add(p);
				
			}
		}
		setSelected(LDrawColor.RED);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pane.add(buttonPane);
		buttons.get(selected).setIcon(selectedIcon);
		cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(this);
		
		response = JOptionPane.CANCEL_OPTION;
		pack();

	}

	
	public int getResponse() {
		
		return response;
	}
	
	
	public int getSelected() {
		
		return selected;
	}
	

	
	public String getSelectedName() {
		
		return selectedName;
	}
	
	
	public void setSelected(int color) {
		
		JButton b = buttons.get(selected);
		if (b != null)
			b.setIcon(null);
		selected = color;
		b = buttons.get(selected);
		if (b == null)
			return;
		selectedName  = b.getToolTipText();
		b.setIcon(selectedIcon);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() ==  cancelButton) {
			response = JOptionPane.CANCEL_OPTION;
			setVisible(false);
		}
		else if (e.getActionCommand().equalsIgnoreCase("color")) {
			JButton cb = (JButton) e.getSource();
			try {
				JButton b = buttons.get(selected);
				b.setIcon(null);
				selected = Integer.parseInt(cb.getName());
				selectedName  = cb.getToolTipText();
				cb.setIcon(selectedIcon);
			} catch (NumberFormatException e1) {
				selected = 0;
			}
			response = JOptionPane.OK_OPTION;
			setVisible(false);
		}
		
	}

}
