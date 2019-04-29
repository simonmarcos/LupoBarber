package com.simonmarcos.lupos.views;

import com.simonmarcos.lupos.dao.DAOHairCut;
import com.simonmarcos.lupos.dao.impl.HairCutDAOImpl;
import com.simonmarcos.lupos.model.Barber;
import com.simonmarcos.lupos.model.HairCut;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class MenuHairCutsClient extends javax.swing.JDialog {

    private DefaultTableModel dtm = null;
    private List<HairCut> listHairCut;
    private List<HairCut> listHairCutFinal;
    private String idClient = "";

    public MenuHairCutsClient(java.awt.Frame parent, boolean modal, String idClient) {
        super(parent, modal);
        initComponents();
        this.setFocusable(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Menu Cortes de Pelos");
        this.idClient = idClient;
        this.setearTableHairCuts();
        this.getAllHairCutsDeterminateClient();
        this.requestFocus();
    }

    //_____________________ METODOS PARA MANIPULAR LA TABLA DE CORTE DE PELOS __________________________
    private void setearTableHairCuts() {
        dtm = new DefaultTableModel();
        String[] columns = {"Id Corte", "Fecha Corte", "Barbero", "Tipo Corte", "Precio"};
        dtm.setColumnIdentifiers(columns);
        tableHairCutsClient.setModel(dtm);

        //Codigo para aplicarle el formato personalizado al encabezado
        JTableHeader jth = tableHairCutsClient.getTableHeader();
        jth.setDefaultRenderer(new HeaderManagement());
        tableHairCutsClient.setTableHeader(jth);

        //Codigo para aplicarme los formatos personalizados
        tableHairCutsClient.getColumnModel().getColumn(0).setCellRenderer(new CellManagement());
        tableHairCutsClient.getColumnModel().getColumn(1).setCellRenderer(new CellManagement());
        tableHairCutsClient.getColumnModel().getColumn(2).setCellRenderer(new CellManagement());
        tableHairCutsClient.getColumnModel().getColumn(3).setCellRenderer(new CellManagement());
        tableHairCutsClient.getColumnModel().getColumn(4).setCellRenderer(new CellManagement());

        //Codigo para especificar el tamaño de las celdas
        tableHairCutsClient.setRowHeight(25);
        //Codigo para no poder escribir las celdas
        tableHairCutsClient.setDefaultEditor(Object.class, null);
    }

    //Metodo que me obtendra todos los clientes de la DB y lo almacena en una variable global
    private void getAllHairCutsDeterminateClient() {
        DAOHairCut dao = new HairCutDAOImpl();
        listHairCut = dao.queryFilter(Integer.parseInt(idClient.trim()), "Cliente");
        fillTableListHairCuts(listHairCut);
        lblCountCuts.setText("Cortes realizados: " + listHairCut.size());
    }

    //Metodo que me rellena la tabla, donde recibe una lista con los clientes.
    private void fillTableListHairCuts(List<HairCut> list) {
        if (list.size() > 0) {
            String[] fila = new String[5];
            list.stream().sorted().forEach(hairCut -> {
                fila[0] = String.valueOf(hairCut.getIdHairCut());
                fila[1] = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(hairCut.getDate());
                fila[2] = hairCut.getBarber().getLastName() + " " + hairCut.getBarber().getName();
                fila[3] = hairCut.getCuts();
                fila[4] = String.valueOf("$ " + hairCut.getPrice());

                dtm.addRow(fila);
            });
            tableHairCutsClient.setModel(dtm);
            lblCountCuts.setText("Cortes realizados: " + list.size());
        }
    }

    //_______________________________________________________________________________________________________
    //Metodo que me filtrara la tabla, segun lo tipeado (nombre, apellido, corte)
    private void filterTableHairCuts(String text) {
        listHairCutFinal = new ArrayList<>();
        if (listHairCut != null || listHairCut.size() > 0) {
            listHairCut.stream().forEach(h -> {
                String name = h.getBarber().getName().toLowerCase();
                String lastName = h.getBarber().getLastName().toLowerCase();
                String typeCut = h.getCuts().toLowerCase();
                String date = h.getDate().toString().toLowerCase();
                System.out.println(text);
                if (date.contains(text) || name.contains(text.toLowerCase()) || lastName.contains(text.toLowerCase()) || typeCut.contains(text.toLowerCase())) {
                    HairCut hairCut = new HairCut();
                    hairCut.setDate(h.getDate());

                    Barber barber = new Barber();
                    barber.setName(name);
                    barber.setLastName(lastName);
                    hairCut.setBarber(barber);

                    hairCut.setCuts(h.getCuts());
                    hairCut.setPrice(h.getPrice());

                    listHairCutFinal.add(h);
                }
            });
        }
        this.setearTableHairCuts();
        this.fillTableListHairCuts(listHairCutFinal);
    }

    private void deleteHairCut() {
        DAOHairCut dao = new HairCutDAOImpl();
        int rowSelected = tableHairCutsClient.getSelectedRow();
        int idHairCut = Integer.parseInt(tableHairCutsClient.getValueAt(rowSelected, 0).toString());
        int r = dao.delete(idHairCut);
        if (r == 1) {
            JOptionPane.showMessageDialog(this, "Se eliminó el corte de pelo");
            this.setearTableHairCuts();
            this.getAllHairCutsDeterminateClient();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar.");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtSearchClient = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        lblCountCuts = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableHairCutsClient = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPanel1KeyTyped(evt);
            }
        });

        txtSearchClient.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        txtSearchClient.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSearchClient.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtSearchClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchClientActionPerformed(evt);
            }
        });
        txtSearchClient.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearchClientKeyTyped(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 3, 20)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("INGRESE NOMBRE/APELLIDO");
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblCountCuts.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        lblCountCuts.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblCountCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtSearchClient, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchClient, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCountCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tableHairCutsClient.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tableHairCutsClient.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tableHairCutsClient.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tableHairCutsClientKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tableHairCutsClient);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 891, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchClientActionPerformed
        this.filterTableHairCuts(txtSearchClient.getText());
    }//GEN-LAST:event_txtSearchClientActionPerformed

    private void jPanel1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyTyped

    }//GEN-LAST:event_jPanel1KeyTyped

    private void txtSearchClientKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchClientKeyTyped
        filterTableHairCuts(txtSearchClient.getText());
    }//GEN-LAST:event_txtSearchClientKeyTyped

    private void tableHairCutsClientKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableHairCutsClientKeyPressed
        if (evt.getKeyChar() == 'd' || evt.getKeyChar() == 'D' && evt.isAltDown()) {
            deleteHairCut();
        }
    }//GEN-LAST:event_tableHairCutsClientKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCountCuts;
    private javax.swing.JTable tableHairCutsClient;
    private javax.swing.JTextField txtSearchClient;
    // End of variables declaration//GEN-END:variables
}
