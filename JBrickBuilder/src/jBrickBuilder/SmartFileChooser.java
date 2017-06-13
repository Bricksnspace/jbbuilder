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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;



public class SmartFileChooser extends JFileChooser {

	

	private static final long serialVersionUID = -3261325996886258566L;
	private String defaultExt;

	/** 
	 * A file save dialog with default extension handling
	 * @param defaultExt default extension with initial "."
	 */
	public SmartFileChooser(String defaultExt) {
		
		this.defaultExt = defaultExt;
	}


	
	public SmartFileChooser(String currentDirectoryPath, String defaultExt) {

		super(currentDirectoryPath);
		this.defaultExt = defaultExt;
	}

	

	public SmartFileChooser(File currentDirectory, String defaultExt) {

		super(currentDirectory);
		this.defaultExt = defaultExt;
	}

	

	public SmartFileChooser(FileSystemView fsv, String defaultExt) {

		super(fsv);
		this.defaultExt = defaultExt;
	}

	

	public SmartFileChooser(File currentDirectory, FileSystemView fsv, String defaultExt) {

		super(currentDirectory, fsv);
		this.defaultExt = defaultExt;
	}



	public SmartFileChooser(String currentDirectoryPath, FileSystemView fsv, String defaultExt) {
	
		super(currentDirectoryPath, fsv);
		this.defaultExt = defaultExt;
	}

	
	public void setExtension(String ext) {
		
		defaultExt = ext;
	}
	
	
	
	public String getExtension() {
		
		return defaultExt;
	}
	
	
	/* 
	 * From: http://stackoverflow.com/questions/3651494/jfilechooser-with-confirmation-dialog
	 * (non Javadoc)
	 * @see javax.swing.JFileChooser#approveSelection()
	 */
	
    @Override
    public void approveSelection(){
    	
        File f = getSelectedFile();
        String fn = f.getPath();
        if (!fn.toLowerCase().endsWith(defaultExt)) {
        	f = new File(fn + defaultExt);
        	//System.out.println(f.getPath());
        	setSelectedFile(f);
        }
        if(f.exists() && getDialogType() == SAVE_DIALOG){
            int result = JOptionPane.showConfirmDialog(this,"File exists, do you want to overwrite it?","Confirm overwrite",JOptionPane.YES_NO_CANCEL_OPTION);
            switch(result){
                case JOptionPane.YES_OPTION:
                    super.approveSelection();
                    return;
                case JOptionPane.NO_OPTION:
                    return;
                case JOptionPane.CLOSED_OPTION:
                    return;
                case JOptionPane.CANCEL_OPTION:
                    cancelSelection();
                    return;
            }
        }
        super.approveSelection();
    }        
	
}
