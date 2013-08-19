/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.table;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class MyTableModel extends AbstractTableModel{
    
    String[] columnNames = {"AUV","ForceValue"};
    
    public MyTableModel() {
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getColumnCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getRowCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column].toString();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex < 1) {
            return false;
        } else {
            return true;
        }
    }
}
