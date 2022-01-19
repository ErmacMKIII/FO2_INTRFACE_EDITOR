/*
 * Copyright (C) 2021 Alexander Stojanovich <coas91@rocketmail.com>
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
package rs.alexanderstojanovich.fo2ie.helper;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class ToggleButtonRenderer implements TableCellRenderer {

    private final JToggleButton toggleButton;
    private final ImageIcon enblIcon, disblIcon;

    public ToggleButtonRenderer(JToggleButton toggleButton, ImageIcon enblIcon, ImageIcon disblIcon) {
        this.toggleButton = toggleButton;
        this.enblIcon = enblIcon;
        this.disblIcon = disblIcon;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value != null) {
            boolean en = (boolean) value;
            toggleButton.setSelected(!en);
            toggleButton.setText(en ? "Disable" : "Enable");
            toggleButton.setIcon(en ? enblIcon : disblIcon);
        }
        return toggleButton;
    }

    public JToggleButton getToggleButton() {
        return toggleButton;
    }

}
