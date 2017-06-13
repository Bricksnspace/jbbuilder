/*
	Copyright 2013-2015 Mario Pascucci <mpascucci@gmail.com>
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
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import bricksnspace.appsettings.AppVersion;




public class AboutDialog extends JDialog implements ActionListener {

	
	
	private static final long serialVersionUID = -4693987158080226643L;
	private JButton okButton;
	//private URI uri;
	private JButton thanksButton;
	//private JButton otherSoftware;
	
	
	public AboutDialog(JFrame owner, String title, ImageIcon icn) {
		
		super (owner, title, true);
		setLocationByPlatform(true);
		//setPreferredSize(new Dimension(700,300));
		Container pane = getContentPane();

		JPanel body = new JPanel();
		body.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
		JLabel prog = new JLabel("JBrickBuilder",SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 24);
		prog.setFont(font);
		body.add(prog);
		JLabel img = new JLabel(icn,SwingConstants.CENTER);
		img.setBorder(BorderFactory.createEtchedBorder());
		img.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(img);
		prog = new JLabel("Version: "+AppVersion.myVersion()+"b",
				SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(prog);
		
		body.add(new JSeparator(SwingConstants.HORIZONTAL));
		
//		prog = new JLabel(new ImageIcon(jBrickBuilder.class.getResource("images/rb-icon.png")));
//		prog.setBorder(BorderFactory.createLineBorder(Color.white, 3));
//		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
//		body.add(prog);
//		
//		prog = new JLabel("<html><center>JBrickBuilder is offered free to the AFOL community</html>",
//				SwingConstants.CENTER);
//		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
//		body.add(prog);
//
//		body.add(new JSeparator(SwingConstants.HORIZONTAL));

		prog = new JLabel("© 2014-2017 Mario Pascucci <mpascucci@gmail.com>",SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		body.add(prog);
		
//		JButton urlButton = new JButton();
//	    urlButton.setText("<HTML><FONT color=\"#000099\"><U>https://sourceforge.net/projects/jbrickbuilder/</U></FONT></HTML>");
//	    urlButton.setHorizontalAlignment(SwingConstants.CENTER);
//	    urlButton.setBorderPainted(false);
//	    urlButton.setOpaque(false);
//	    urlButton.setBackground(Color.WHITE);
//	    try {
//			uri = new URI("https://sourceforge.net/projects/jbrickbuilder/");
//		} catch (URISyntaxException e1) {
//			uri = null;
//		}
//	    urlButton.setToolTipText(uri.toString());
//	    urlButton.addActionListener(new ActionListener() {
//			
//	    	// from: http://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel
//	    	
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				
//				if (Desktop.isDesktopSupported()) {
//					try {
//				        Desktop.getDesktop().browse(uri);
//				    } catch (IOException ex) {}
//				}
//			}
//		});
//		
//	    urlButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//	    body.add(urlButton);
	    
		body.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		prog = new JLabel("<html><small>JBrickBuilder is NOT related, linked,<br/>sponsored or supported by LEGO® Group</small></html>",
				SwingConstants.CENTER);
		prog.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		body.add(prog);
		
		pane.add(body,BorderLayout.CENTER);
		
		// ok button
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pane.add(buttonPane, BorderLayout.SOUTH);

		thanksButton = new JButton("Thanks");
		buttonPane.add(thanksButton);
		thanksButton.addActionListener(this);

//		otherSoftware = new JButton("More...");
//		buttonPane.add(otherSoftware);
//		otherSoftware.addActionListener(this);
		
		okButton = new JButton("OK");
		buttonPane.add(okButton);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		
		pack();
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ev) {

		if (ev.getSource() == okButton) {
			setVisible(false);
		}
		else if (ev.getSource() == thanksButton) {
			JOptionPane.showMessageDialog(this,
					"Thanks to:\n"
					+ "-JOGL (a Java implementation of OpenGL API http://jogamp.org/)\n"
					+ "-LDraw LEGO® bricks part libraries (http://www.ldraw.org/)\n"
					+ "-Eclipse (http://www.eclipse.org/eclipse/)\n"
					+ "-Open Icon Library (http://sourceforge.net/projects/openiconlibrary/)\n"
					+ "-LEGO® for inspiring people (http://www.lego.com)",
					"Thanks",
					JOptionPane.INFORMATION_MESSAGE, 
					new ImageIcon(jBrickBuilder.class.getResource("images/star.png")));
		}
	}


	
}
