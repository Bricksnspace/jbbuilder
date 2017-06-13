/*
	Copyright 2013-2014 Mario Pascucci <mpascucci@gmail.com>
	This file is part of JBrickBuilder.

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

import java.util.List;

import javax.swing.table.AbstractTableModel;

import bricksnspace.ldrawlib.LDrawPart;

/*
 * table model to display Ldraw part search results
 */
public class LdrTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3167823145975112568L;
	private List<LDrawPart> parts;

	private String[] columnNames = {
			"LDraw part ID",
			"Description",
			"Category",
			"Keywords",
			};
	
	/* 
	 * sets whole data model for table
	 */
	public void setParts(List<LDrawPart> parts) {
		this.parts = parts;
		fireTableDataChanged();
	}

	
	@Override
	public String getColumnName(int col) {
        return columnNames[col];
    }

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {

		return String.class;
    }


	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	

	@Override
	public int getRowCount() {
		if (parts != null)
			return parts.size();
		else
			return 0;
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
        return false;
    }
	
	public LDrawPart getPart(int idx) {
		return parts.get(idx);
	}
	
	
	
	@Override
	public Object getValueAt(int arg0, int arg1) {
		if (getRowCount() == 0)
			return "";
		//id,ldrid,name,category,keywords
		switch (arg1) {
		case 0:
			return parts.get(arg0).getLdrawId();
		case 1:
			return parts.get(arg0).getDescription();
		case 2:
			return parts.get(arg0).getCategory();
		case 3:
			return parts.get(arg0).getKeywords();
		}
		return null;
	}

}
