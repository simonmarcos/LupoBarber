package com.simonmarcos.lupos.views;

import com.simonmarcos.lupos.dao.DAO;
import com.simonmarcos.lupos.dao.impl.CutsDAOImpl;
import com.simonmarcos.lupos.model.Cuts;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class MenuCuts extends javax.swing.JDialog {

    private List<Cuts> listCuts = null;

    public MenuCuts(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setFocusable(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Menu Cliente");
        this.fillListAllCuts();
    }

    private void clearFields() {
        txtType.setText("");
        txtPrice.setText("");
        txtPriceBarber.setText("");
        txtPrize.setText("");
    }

    private void getAllCuts() {
        DAO dao = new CutsDAOImpl();
        listCuts = new ArrayList<>();
        listCuts = dao.toList();
    }

    private void fillListAllCuts() {
        listAllCuts.removeAllItems();
        this.getAllCuts();
        for (Cuts listCut : listCuts) {
            listAllCuts.addItem(listCut.getType() + " - " + listCut.getIdCuts());
        }
    }

    private void saveCuts() {
        if (!txtType.getText().isEmpty() && !txtPrice.getText().isEmpty() && !txtPriceBarber.getText().isEmpty()) {
            DAO dao = new CutsDAOImpl();
            Cuts c = null;
            if (!txtPrize.getText().isEmpty()) {
                c = new Cuts(txtType.getText(), Double.parseDouble(txtPrice.getText()), Double.parseDouble(txtPriceBarber.getText()), Double.parseDouble(txtPrize.getText()));
            } else {
                c = new Cuts(txtType.getText(), Double.parseDouble(txtPrice.getText()), Double.parseDouble(txtPriceBarber.getText()), 0);
            }

            int r = dao.save(c);
            if (r == 1) {
                JOptionPane.showMessageDialog(this, "Se guardó correctamente.");
                clearFields();
                fillListAllCuts();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe completar los campos obligatorios.");
        }
    }

    private void fillField() {
        int idCuts = Integer.parseInt(listAllCuts.getSelectedItem().toString().split(" - ")[1]);
        DAO dao = new CutsDAOImpl();
        List<Cuts> cuts = dao.queryFilter(idCuts, "");
        cuts.forEach(c -> {
            txtType.setText(c.getType());
            txtPrice.setText(String.valueOf(c.getPrice()));
            txtPriceBarber.setText(String.valueOf(c.getPriceBarber()));
            txtPrize.setText(String.valueOf(c.getPrize()));
        });
    }

    private void updateCuts() {
        int idCuts = Integer.parseInt(listAllCuts.getSelectedItem().toString().split(" - ")[1]);
        DAO dao = new CutsDAOImpl();
        Cuts c = null;
        if (!txtPrize.getText().isEmpty()) {
            c = new Cuts(txtType.getText(), Double.parseDouble(txtPrice.getText()), Double.parseDouble(txtPriceBarber.getText()), Double.parseDouble(txtPrize.getText()));
        } else {
            c = new Cuts(txtType.getText(), Double.parseDouble(txtPrice.getText()), Double.parseDouble(txtPriceBarber.getText()), 0);
        }
        int r = dao.modificar(idCuts, c);
        if (r == 1) {
            JOptionPane.showMessageDialog(this, "Se modificó correctamente.");
            checkUpdate.setSelected(false);
            clearFields();
            fillListAllCuts();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnSaveCuts = new javax.swing.JButton();
        txtPrize = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtPriceBarber = new javax.swing.JTextField();
        txtPrice = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtType = new javax.swing.JTextField();
        checkUpdate = new javax.swing.JCheckBox();
        listAllCuts = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnSaveCuts.setBackground(new java.awt.Color(0, 153, 204));
        btnSaveCuts.setFont(new java.awt.Font("Arial Unicode MS", 3, 14)); // NOI18N
        btnSaveCuts.setForeground(new java.awt.Color(255, 255, 255));
        btnSaveCuts.setText("MODIFICAR");
        btnSaveCuts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnSaveCuts.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSaveCuts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveCutsActionPerformed(evt);
            }
        });

        txtPrize.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtPrize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPrize.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtPrize.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPrizeKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPrizeKeyTyped(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(153, 153, 153));
        jLabel4.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Premio:");
        jLabel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setBackground(new java.awt.Color(153, 153, 153));
        jLabel2.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 51, 51));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Precio Barbero:");
        jLabel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtPriceBarber.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtPriceBarber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPriceBarber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtPriceBarber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPriceBarberKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPriceBarberKeyTyped(evt);
            }
        });

        txtPrice.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtPrice.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPrice.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPriceKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPriceKeyTyped(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(153, 153, 153));
        jLabel1.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Precio:");
        jLabel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel3.setBackground(new java.awt.Color(153, 153, 153));
        jLabel3.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 51));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Nombre:");
        jLabel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtType.setBackground(new java.awt.Color(102, 102, 102));
        txtType.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtType.setForeground(new java.awt.Color(255, 255, 255));
        txtType.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtType.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtType.setEnabled(false);
        txtType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTypeKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTypeKeyTyped(evt);
            }
        });

        checkUpdate.setText("MODIFICAR");
        checkUpdate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkUpdateMouseClicked(evt);
            }
        });

        listAllCuts.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        listAllCuts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(txtPrize))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(txtPrice))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(txtPriceBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(txtType, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(listAllCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkUpdate)))
                .addContainerGap(45, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnSaveCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(273, 273, 273))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkUpdate)
                    .addComponent(listAllCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtType, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPriceBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(txtPrize))
                .addGap(18, 18, 18)
                .addComponent(btnSaveCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTypeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTypeKeyTyped
        validarCamposTextos(evt);
    }//GEN-LAST:event_txtTypeKeyTyped

    private void txtTypeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTypeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtPrice.requestFocus();
        }
    }//GEN-LAST:event_txtTypeKeyPressed

    private void txtPriceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPriceKeyTyped
        
    }//GEN-LAST:event_txtPriceKeyTyped

    private void txtPriceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPriceKeyPressed

    }//GEN-LAST:event_txtPriceKeyPressed

    private void txtPriceBarberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPriceBarberKeyTyped

    }//GEN-LAST:event_txtPriceBarberKeyTyped

    private void txtPriceBarberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPriceBarberKeyPressed
      
    }//GEN-LAST:event_txtPriceBarberKeyPressed

    private void txtPrizeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrizeKeyTyped
        
    }//GEN-LAST:event_txtPrizeKeyTyped

    private void txtPrizeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrizeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            saveCuts();
        }
    }//GEN-LAST:event_txtPrizeKeyPressed

    private void btnSaveCutsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveCutsActionPerformed
        updateCuts();
    }//GEN-LAST:event_btnSaveCutsActionPerformed

    private void checkUpdateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkUpdateMouseClicked
        if (checkUpdate.isSelected()) {
            fillField();
        }
    }//GEN-LAST:event_checkUpdateMouseClicked

    //______________________________________________________________________________________________________________
    private void validarCamposTextos(KeyEvent evt) {
        char c = evt.getKeyChar();
        if (Character.isDigit(c)) {
            getToolkit().beep();
            evt.consume();
        }
    }

    private void validarCamposNumericos(KeyEvent evt) {
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9')) {
            evt.consume();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSaveCuts;
    private javax.swing.JCheckBox checkUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox<String> listAllCuts;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtPriceBarber;
    private javax.swing.JTextField txtPrize;
    private javax.swing.JTextField txtType;
    // End of variables declaration//GEN-END:variables
}
