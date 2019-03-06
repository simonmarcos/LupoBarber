package com.simonmarcos.lupos.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CellManagement extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {

        Color colorFondo = null;
        Color colorFondoPorDefecto = new Color(192, 192, 192);
        Color colorFondoSeleccion = new Color(0, 153, 204);

        if (selected) {
            this.setBackground(colorFondoSeleccion);
        } else {
            this.setBackground(Color.white);
        }

        this.setHorizontalAlignment(JLabel.CENTER);
        this.setFont(new Font("Arial", 1, 14));

        this.setText((String) value);

        return this;
    }
}
