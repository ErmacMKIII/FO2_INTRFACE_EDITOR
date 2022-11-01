/*
 * Copyright (C) 2022 Alexander Stojanovich <coas91@rocketmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rs.alexanderstojanovich.fo2ie.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import rs.alexanderstojanovich.fo2ie.helper.ButtonEditor;
import rs.alexanderstojanovich.fo2ie.helper.ButtonRenderer;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public abstract class ActionTable extends JFrame {
    
    private static ActionTable instance;
    
    private final JTable actionTable = new JTable();
    private final JScrollPane spAction = new JScrollPane(actionTable);
    private final JButton btnClose = new JButton("Close");
    
    public static ActionTable getInstance(GUI gui) {
        if (instance == null) {
            instance = new ActionTable() {
                @Override
                public void execute() {
                    gui.buildModuleComponents();
                    gui.updateBaseFeaturePreview();
                    gui.updateComponentsPreview();
                    gui.updateDerivedFeaturePreview();
                }
            };
        }
        
        return instance;
    }
    
    public ActionTable() {
        this.setTitle("Add feature");
        this.setType(Window.Type.POPUP);
        this.setAlwaysOnTop(true);
        this.setIconImages(GUI.ICONS);
        this.setPreferredSize(new Dimension(600, 450));        
        this.actionTable.setRowHeight(24);        
        initPosition();
    }

    // Center the GUI window into center of the screen
    private void initPosition() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }
    
    private void undoAction() {
        final int srow = actionTable.getSelectedRow();
        final int scol = actionTable.getSelectedColumn();
        
        Object uuid = actionTable.getValueAt(srow, scol - 4);
        
        Action actKey = null;
        for (Action action : GUI.ACTIONS) {
            if (action.getUniqueId().equals(uuid)) {
                actKey = action;
                break;
            }
        }        
        
        if (actKey != null) {
            actKey.undo();
            GUI.ACTIONS.remove(actKey);
            DefaultTableModel model = (DefaultTableModel) actionTable.getModel();
            model.removeRow(srow);
            execute();
        }
        
    }
    
    private void display() {
        final DefaultTableModel actTblMdl = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return (column == 4);
            }
        };

        /*
        TableRowSorter<DefaultTableModel> sort = new TableRowSorter<>(compTblMdl);
        txtFldSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String str = txtFldSearch.getText();
                if (str.trim().length() == 0) {
                    sort.setRowFilter(null);
                } else {
                    sort.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String str = txtFldSearch.getText();
                if (str.trim().length() == 0) {
                    sort.setRowFilter(null);
                } else {
                    sort.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        tblComps.setRowSorter(sort);
         */
        actTblMdl.addColumn("Unique Id");
        actTblMdl.addColumn("Description");
        actTblMdl.addColumn("Type");
        actTblMdl.addColumn("Timestamp");
        actTblMdl.addColumn("Undo");
        
        final ButtonEditor undoEdit = new ButtonEditor(new JButton("Undo"));
        undoEdit.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undoAction();
            }            
        });
        final ButtonRenderer undoRend = new ButtonRenderer(undoEdit.getButton());        
        
        for (Action action : GUI.ACTIONS) {
            Object[] row = {action.getUniqueId(), action.getDescription(), action.getType().toString(), action.getTimestamp().toString()};
            actTblMdl.addRow(row);
        }
        
        actionTable.getTableHeader().setReorderingAllowed(false);
        actionTable.setRowSelectionAllowed(false);
        actionTable.setColumnSelectionAllowed(false);
        actionTable.setCellSelectionEnabled(false);
        actionTable.setModel(actTblMdl);
        
        TableColumn propCol = actionTable.getColumn("Undo");
        propCol.setCellEditor(undoEdit);
        propCol.setCellRenderer(undoRend);
    }
    
    public void popUp() {
        this.setTitle("Action Table");
        this.getContentPane().removeAll(); // removes all the components                
        
        this.setLayout(new BorderLayout());
        this.getContentPane().add(spAction, BorderLayout.CENTER);
        this.getContentPane().add(btnClose, BorderLayout.SOUTH);        
        
        this.actionTable.setPreferredScrollableViewportSize(new Dimension(60, 45));
        this.actionTable.setFillsViewportHeight(true);
        this.spAction.setVisible(true);
        
        this.btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        display();
    }
    
    public abstract void execute();
}
