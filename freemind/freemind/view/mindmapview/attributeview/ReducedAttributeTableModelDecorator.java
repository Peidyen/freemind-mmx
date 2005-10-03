/*
 * Created on 10.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.view.mindmapview.attributeview;

import java.util.Vector;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import freemind.modes.MindMapNode;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeRegistryTableModel;
import freemind.modes.attributes.AttributeTableModel;
import freemind.view.mindmapview.NodeView;

/**
 * @author Dimitri Polivaev
 * 10.07.2005
 */
class ReducedAttributeTableModelDecorator extends AbstractTableModel implements AttributeTableModel, ChangeListener, TableModelListener, AncestorListener{
    private AttributeTableModel concreteModel;
    private AttributeRegistryTableModel registryTable;
    private Vector index = null;
    private int visibleRowCount;
    ReducedAttributeTableModelDecorator(AttributeView attributeView, AttributeTableModel model, AttributeRegistryTableModel registryTable) {
        super();
        this.concreteModel = model;        
        this.registryTable = registryTable;
        stateChanged(null);
        attributeView.getNodeView().addAncestorListener(this);        
    }
    private void addListeners() {
        concreteModel.addTableModelListener(this);
        this.registryTable.addChangeListener(this);
    }
    private void removeListeners() {
        concreteModel.removeTableModelListener(this);
        this.registryTable.removeChangeListener(this);
    }
    private Vector getIndex() {
        if(index == null && this.registryTable.getVisibleElementsNumber() > 0)
            index = new Vector(this.concreteModel.getRowCount(), 10);
        return index;
    }
    public int getColumnCount() {
        return 2;
    }
    public int getRowCount() {
        return visibleRowCount;
    }
    public Object getValueAt(int row, int col) {
        return concreteModel.getValueAt(calcRow(row), col);
    }
    
    public boolean isCellEditable(int row, int col) {
        if(concreteModel.isCellEditable(row, col)){
            return col == 1;
        }
        return false;
    }

    private int calcRow(int row){
        return ((Integer) index.get(row)).intValue();
    }
    public void setValueAt(Object o, int row, int col) {
        concreteModel.setValueAt(o, calcRow(row), col);
    	fireTableCellUpdated(row,col);
    }
    
    public MindMapNode getNode() {
        return concreteModel.getNode();
    }
    /* (non-Javadoc)
     * @see freemind.modes.attributes.AttributeTableModel#insertRow(int, freemind.modes.attributes.Attribute)
     */
    public void insertRow(int index, Attribute newAttribute) {
        throw new Error();
    }
    /* (non-Javadoc)
     * @see freemind.modes.attributes.AttributeTableModel#addRow(freemind.modes.attributes.Attribute)
     */
    public void addRow(Attribute newAttribute) {
        throw new Error();
    }
    /* (non-Javadoc)
     * @see freemind.modes.attributes.AttributeTableModel#removeRow(int)
     */
    public Object removeRow(int index) {
        throw new Error();
    }
    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        Vector index = getIndex();
        if(index != null){
            visibleRowCount= 0;
            index.clear();
            for(int i = 0; i < concreteModel.getRowCount(); i++){
                String name = (String)concreteModel.getValueAt(i, 0);
                if(registryTable.getElement(name).isVisible().booleanValue()){
                    index.add(new Integer(i));
                    visibleRowCount++;
                }
            }
        fireTableStructureChanged();    
        }
        
    }
    public AttributeRegistryTableModel getRegistryTable() {
        return registryTable;
    }
    public Class getColumnClass(int columnIndex) {
        return concreteModel.getColumnClass(columnIndex);
    }
    public String getColumnName(int columnIndex) {
        return concreteModel.getColumnName(columnIndex);
    }
    /* (non-Javadoc)
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    public void tableChanged(TableModelEvent e) {
        fireTableDataChanged();        
    }
    
    public int getColumnWidth(int col) {
        return concreteModel.getColumnWidth(col);
    }
    
    public void setColumnWidth(int col, int width) {
        concreteModel.setColumnWidth(col, width);
    }
    /* (non-Javadoc)
     * @see javax.swing.event.AncestorListener#ancestorAdded(javax.swing.event.AncestorEvent)
     */
    public void ancestorAdded(AncestorEvent event) {
        addListeners();
    }
    /* (non-Javadoc)
     * @see javax.swing.event.AncestorListener#ancestorRemoved(javax.swing.event.AncestorEvent)
     */
    public void ancestorRemoved(AncestorEvent event) {
        removeListeners();
    }
    /* (non-Javadoc)
     * @see javax.swing.event.AncestorListener#ancestorMoved(javax.swing.event.AncestorEvent)
     */
    public void ancestorMoved(AncestorEvent event) {
    }
}
