package com.simonmarcos.lupos.views;

import com.simonmarcos.lupos.dao.DAOHairCut;
import com.simonmarcos.lupos.dao.impl.ClientDAOImpl;
import com.simonmarcos.lupos.dao.impl.HairCutDAOImpl;
import com.simonmarcos.lupos.model.Barber;
import com.simonmarcos.lupos.model.Client;
import com.simonmarcos.lupos.model.HairCut;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class MenuHairCutsBarber extends javax.swing.JDialog {

    private DefaultTableModel dtm = null;
    private List<HairCut> listHairCut;
    private List<HairCut> listHairCutFinal;
    private List<Client> listClient;
    private String idBarber = "";

    public MenuHairCutsBarber(java.awt.Frame parent, boolean modal, String idBarber) {
        super(parent, modal);
        initComponents();
        this.setFocusable(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Menu Cortes de Pelos");
        this.idBarber = idBarber;
        this.setearTableHairCuts();
        this.getAllHairCutsDeterminateBarber();

    }

    //_____________________ METODOS PARA MANIPULAR LA TABLA DE CORTE DE PELOS __________________________
    private void setearTableHairCuts() {
        dtm = new DefaultTableModel();
        String[] columns = {"Fecha Corte", "Cliente", "Tipo Corte", "Precio"};
        dtm.setColumnIdentifiers(columns);
        tableHairCutsBarber.setModel(dtm);

        //Codigo para aplicarle el formato personalizado al encabezado
        JTableHeader jth = tableHairCutsBarber.getTableHeader();
        jth.setDefaultRenderer(new HeaderManagement());
        tableHairCutsBarber.setTableHeader(jth);

        //Codigo para aplicarme los formatos personalizados
        tableHairCutsBarber.getColumnModel().getColumn(0).setCellRenderer(new CellManagement());
        tableHairCutsBarber.getColumnModel().getColumn(1).setCellRenderer(new CellManagement());
        tableHairCutsBarber.getColumnModel().getColumn(2).setCellRenderer(new CellManagement());
        tableHairCutsBarber.getColumnModel().getColumn(3).setCellRenderer(new CellManagement());

        //Codigo para especificar el tamaño de las celdas
        tableHairCutsBarber.setRowHeight(25);
        //Codigo para no poder escribir las celdas
        tableHairCutsBarber.setDefaultEditor(Object.class, null);
    }

    //Metodo que me agregara los resultados finales en la ultima fila de la tabla
    private void setearRowWithFinalDate() {

        int row = tableHairCutsBarber.getRowCount();
        double priceHairCuts = 0;
        for (int i = 0; i < row; i++) {
            priceHairCuts += Double.parseDouble(tableHairCutsBarber.getValueAt(i, 3).toString());
        }

        String[] fila = new String[4];
        fila[0] = "";
        fila[1] = "";
        fila[2] = "Ganancias Total";
        fila[3] = "$ " + String.valueOf(priceHairCuts);

        dtm.addRow(fila);
        tableHairCutsBarber.setModel(dtm);
    }

    //Metodo que me obtendra todos los barberos de la DB y lo almacena en una variable global
    private void getAllHairCutsDeterminateBarber() {
        DAOHairCut dao = new HairCutDAOImpl();
        listHairCut = dao.queryFilter(Integer.parseInt(idBarber.trim()), "Barbero");
        fillTableListHairCuts(listHairCut);
        lblCountCuts.setText("Cortes realizados: " + listHairCut.size());
    }

    //Metodo que me rellena la tabla, donde recibe una lista con los clientes.
    private void fillTableListHairCuts(List<HairCut> list) {
        if (list.size() > 0 || list != null) {
            String[] fila = new String[4];

            //Buscamos todos los clientes
            this.getAllClient();
            list.stream().forEach(hairCut -> {
                fila[0] = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(hairCut.getDate());

                //Llamamos al metodo y obtenemos un string con el nombre y apellido correspondiente al ID
                String[] info = this.searchClientByID(hairCut.getClient().getIdClient());
                fila[1] = info[1] + " " + info[0];

                fila[2] = hairCut.getCuts();
                fila[3] = String.valueOf(hairCut.getPriceBarber());

                dtm.addRow(fila);
            });
            tableHairCutsBarber.setModel(dtm);
            lblCountCuts.setText("Cortes realizados: " + list.size());
            setearRowWithFinalDate();
        } else {
        }
    }

    //Metodo para obtener todos los clientes
    private void getAllClient() {
        listClient = new ClientDAOImpl().toList();
    }

    //Metodo que me buscara el cliente segun el id, para luego comparar el id pasado y el id de la lista de clientes
    //para luego devolver un String con su nombre y apellido
    private String[] searchClientByID(int idClient) {
        String[] info = new String[2];
        for (Client c : listClient) {
            if (c.getIdClient() == idClient) {
                info[0] = c.getName();
                info[1] = c.getLastName();
                break;
            }
        }
        return info;
    }

    //_______________________________________________________________________________________________________
    //Metodo que me filtrara la tabla, segun lo tipeado (nombre, apellido, corte)
    private void filterTableHairCuts(String text) {
        listHairCutFinal = new ArrayList<>();
        if (listHairCut != null || listHairCut.size() > 0) {
            listHairCut.stream().forEach(h -> {
                String name = searchClientByID(h.getClient().getIdClient())[0].toLowerCase();
                String lastName = searchClientByID(h.getClient().getIdClient())[1].toLowerCase();
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
                    hairCut.setPrice(h.getPriceBarber());

                    listHairCutFinal.add(h);
                }
            });
        }
        this.setearTableHairCuts();
        this.fillTableListHairCuts(listHairCutFinal);
    }

    //Metodo que me buscara el barbero dependiendo la fecha de corte
    private void getListForDateSinceAndUntil() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateSinceString = sdf.format(dateSince.getDate());
        String dateUntilString = sdf.format(dateUntil.getDate());

        DAOHairCut dao = new HairCutDAOImpl();

        this.setearTableHairCuts();
        this.fillTableListHairCuts(dao.queryFilterForDateBetwen(Integer.parseInt(idBarber), dateSinceString, dateUntilString));

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtSearchBarber = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        lblCountCuts = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableHairCutsBarber = new javax.swing.JTable();
        dateUntil = new com.toedter.calendar.JDateChooser();
        dateSince = new com.toedter.calendar.JDateChooser();
        btnSearchBarberDate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPanel1KeyTyped(evt);
            }
        });

        txtSearchBarber.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        txtSearchBarber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSearchBarber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtSearchBarber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchBarberActionPerformed(evt);
            }
        });
        txtSearchBarber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearchBarberKeyTyped(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 3, 20)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("INGRESE NOMBRE/APELLIDO/CORTE");
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
                        .addComponent(txtSearchBarber)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCountCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tableHairCutsBarber.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tableHairCutsBarber.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableHairCutsBarber);

        dateSince.setDateFormatString("dd/MM/yyyy");

        btnSearchBarberDate.setText("BUSCAR");
        btnSearchBarberDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchBarberDateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 891, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(dateSince, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(btnSearchBarberDate, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dateUntil, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dateSince, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dateUntil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSearchBarberDate, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))
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
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchBarberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchBarberActionPerformed
        this.filterTableHairCuts(txtSearchBarber.getText());
    }//GEN-LAST:event_txtSearchBarberActionPerformed

    private void jPanel1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyTyped

    }//GEN-LAST:event_jPanel1KeyTyped

    private void txtSearchBarberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchBarberKeyTyped
        filterTableHairCuts(txtSearchBarber.getText());
    }//GEN-LAST:event_txtSearchBarberKeyTyped

    private void btnSearchBarberDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchBarberDateActionPerformed
        this.getListForDateSinceAndUntil();
    }//GEN-LAST:event_btnSearchBarberDateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearchBarberDate;
    private com.toedter.calendar.JDateChooser dateSince;
    private com.toedter.calendar.JDateChooser dateUntil;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCountCuts;
    private javax.swing.JTable tableHairCutsBarber;
    private javax.swing.JTextField txtSearchBarber;
    // End of variables declaration//GEN-END:variables
}
