package com.simonmarcos.lupos.views;

import com.simonmarcos.lupos.dao.DAOClient;
import com.simonmarcos.lupos.dao.impl.ClientDAOImpl;
import com.simonmarcos.lupos.model.Client;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import jdk.nashorn.internal.ir.BreakNode;

public class MenuClient extends javax.swing.JDialog {

    private DefaultTableModel dtm = null;

    //Variable que me guardara la lista de todos los clientes
    private List<Client> listClient = null;
    //_______________________________________________________
    //Variable que me guardara la lista de todos los clientes al filtrarlos
    private List<Client> listClientFinal = null;
    //_______________________________________________________

    public MenuClient(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setFocusable(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Menu Cliente");
        this.setearTableClient();
        this.getAllClientDB();
        this.setImgBtnSaveClient();
        this.setImgBtnClearFields();
        this.setImgBtnSearch();
        this.setImgBtnUpdate();

        pestanas.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                switch (pestanas.getSelectedIndex()) {
                    case 0:
                        setearTableClient();
                        getAllClientDB();
                        clearFieldsPestanaQuery();
                        break;
                    case 1:
                        clearFieldsRegisterClient();
                        break;
                    case 2:
                        clearFieldsUpdateClient();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    //______________________________________________________________________________________________________________
    private void setearTableClient() {
        dtm = new DefaultTableModel();
        String[] columns = {"Nro Clientes", "Apellido", "Nombre", "DNI", "Teléfono"};
        dtm.setColumnIdentifiers(columns);
        tableClients.setModel(dtm);

        //Codigo para aplicarle el formato personalizado al encabezado
        JTableHeader jth = tableClients.getTableHeader();
        jth.setDefaultRenderer(new HeaderManagement());
        tableClients.setTableHeader(jth);

        //Codigo para aplicarme los formatos personalizados
        tableClients.getColumnModel().getColumn(0).setCellRenderer(new CellManagement());
        tableClients.getColumnModel().getColumn(1).setCellRenderer(new CellManagement());
        tableClients.getColumnModel().getColumn(2).setCellRenderer(new CellManagement());
        tableClients.getColumnModel().getColumn(3).setCellRenderer(new CellManagement());
        tableClients.getColumnModel().getColumn(4).setCellRenderer(new CellManagement());

        //Codigo para especificar el tamaño de las celdas
        tableClients.setRowHeight(25);
        //Codigo para no poder escribir las celdas
        tableClients.setDefaultEditor(Object.class, null);
    }

    //Metodo que me obtendra todos los clientes de la DB y lo almacena en una variable global
    private void getAllClientDB() {
        DAOClient dao = new ClientDAOImpl();
        listClient = dao.toList();

        fillTableListClient(listClient);
    }

    //Metodo que me rellena la tabla, donde recibe una lista con los clientes.
    private void fillTableListClient(List<Client> list) {
        String[] fila = new String[5];
        list.stream().sorted().forEach(c -> {
            fila[0] = String.valueOf(c.getIdClient());
            fila[2] = c.getName();
            fila[1] = c.getLastName();
            fila[3] = String.valueOf(c.getDNI());
            fila[4] = c.getPhone();

            dtm.addRow(fila);
        });
        tableClients.setModel(dtm);
    }

    private void clearFieldsPestanaQuery() {
        txtSearchClient.setText("");
    }

    //Metodo que me filtrara la tabla, segun lo tipeado (nombre, apellido, dni)
    private void filterTableClient(String text) {
        listClientFinal = new ArrayList<>();
        if (listClient != null || listClient.size() > 0) {
            listClient.stream().forEach(c -> {
                String name = c.getName().toLowerCase();
                String lastName = c.getLastName().toLowerCase();
                String dni = String.valueOf(c.getDNI());
                if (name.contains(text.toLowerCase()) || lastName.contains(text.toLowerCase()) || dni.contains(text)) {
                    Client client = new Client();
                    client.setIdClient(c.getIdClient());
                    client.setName(name);
                    client.setLastName(lastName);
                    client.setDNI(c.getDNI());
                    client.setPhone(c.getPhone());
                    listClientFinal.add(c);
                }
            });
        }
        this.setearTableClient();
        this.fillTableListClient(listClientFinal);
    }

    //Metodo para obtener el cliente y enviarlo al otro JDialog
    private String getIdClientForSendHairCut() {
        //Ubicamos la fila en la cual realizamos el evento
        int rowSelected = tableClients.getSelectedRow();
        //Seleccionamos el DNI de la fila seleccionada
        String DNIClient = String.valueOf(tableClients.getValueAt(rowSelected, 0));
        return DNIClient;
    }
//______________________________________________________________________________________________________________
//METODOS PARA MANIPULAR LA SECCION DE REGISTRO DE CLIENTES

    private void clearFieldsRegisterClient() {
        txtName.setText("");
        txtLastName.setText("");
        txtDNI.setText("");
        txtPhone.setText("");
    }

    private void saveClient() {
        if (!txtName.getText().isEmpty() && !txtLastName.getText().isEmpty() && !txtDNI.getText().isEmpty()/* && !txtPhone.getText().isEmpty()*/) {

            DAOClient dao = new ClientDAOImpl();
            Client c = new Client();
            c.setName(txtName.getText());
            c.setLastName(txtLastName.getText());
            c.setDNI(Integer.parseInt(txtDNI.getText()));
            c.setPhone(txtPhone.getText());

            int r = dao.save(c);

            if (r == 1) {
                JOptionPane.showMessageDialog(this, "El cliente " + txtName.getText() + " se guardó correctamente.");
                this.clearFieldsRegisterClient();
            } else if (r == 0) {
                JOptionPane.showMessageDialog(this, "El cliente con el DNI " + txtDNI.getText() + " ya se encuentra.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos por favor.");
        }
    }

    //______________________________________________________________________________________________________________
    //METODOS PARA MANIPULAR LA SECCION DE MODIFICACION DE CLIENTES
    private void clearFieldsUpdateClient() {
        txtIdClientModif.setText("");
        txtNameModif.setText("");
        txtLastNameModif.setText("");
        txtDNIModif.setText("");
        txtPhoneModif.setText("");
        txtSearchClientModif.setText("");
    }

    private void clearFieldsUpdateClientWithoutSearch() {
        txtIdClientModif.setText("");
        txtNameModif.setText("");
        txtLastNameModif.setText("");
        txtDNIModif.setText("");
        txtPhoneModif.setText("");
    }

    private void updateClient() {

        if (!txtSearchClientModif.getText().isEmpty()) {
            DAOClient dao = new ClientDAOImpl();
            Client c = new Client();
            c.setName(txtNameModif.getText());
            c.setLastName(txtLastNameModif.getText());
            c.setDNI(Integer.parseInt(txtDNIModif.getText()));
            c.setPhone(txtPhoneModif.getText());

            int r = dao.modificar(Integer.parseInt(txtSearchClientModif.getText()), c);
            if (r == 1) {
                JOptionPane.showMessageDialog(this, "El cliente " + txtNameModif.getText() + " se modificó correctamente.");
                pestanas.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo realizar la modificación del cliente.");
            }
        }
    }

    //Metodo que me buscará de la lista general, el cliente que coincida con el DNI que ingresamos
    private void fillFieldsModif() {
        if (!txtSearchClientModif.getText().isEmpty() && txtSearchClientModif.getText().length() <= 11) {
            for (Client c : listClient) {
                if (String.valueOf(c.getDNI()).equalsIgnoreCase(txtSearchClientModif.getText())) {
                    lblDNINotFound.setText("");
                    txtIdClientModif.setText(String.valueOf(c.getIdClient()));
                    txtNameModif.setText(c.getName());
                    txtLastNameModif.setText(c.getLastName());
                    txtDNIModif.setText(String.valueOf(c.getDNI()));
                    txtPhoneModif.setText(c.getPhone());
                    break;
                } else {
                    lblDNINotFound.setText("El DNI no se encuentra registrado.");
                    clearFieldsUpdateClientWithoutSearch();
                }
            }
            /*DAOClient dao = new ClientDAOImpl();
            List<Client> list = dao.queryFilter(Integer.parseInt(txtSearchClientModif.getText()), "");
            if (list.size() > 0) {
                
                list.stream()
                        .forEach(c -> {
                            txtIdClientModif.setText(String.valueOf(c.getIdClient()));
                            txtNameModif.setText(c.getName());
                            txtLastNameModif.setText(c.getLastName());
                            txtDNIModif.setText(String.valueOf(c.getDNI()));
                            txtPhoneModif.setText(c.getPhone());
                        });
            } else {
                lblDNINotFound.setText("El DNI no se encuentra registrado.");
            }*/
        }
    }

    //______________________________________________________________________________________________________________
    //METODOS PARA COLOCAR IMAGENES A LOS BOTONES
    private void setImgBtnSaveClient() {
        try {
            Image imgSearch = ImageIO.read(getClass().getResource("/img/logoSave.png"));
            Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnSaveClient.getWidth(), btnSaveClient.getHeight(), Image.SCALE_DEFAULT));
            btnSaveClient.setIcon(iconSearch);

        } catch (IOException ex) {
            Logger.getLogger(MenuClient.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setImgBtnClearFields() {
        try {
            Image imgSearch = ImageIO.read(getClass().getResource("/img/logoClear.png"));
            Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnClearFields.getWidth(), btnClearFields.getHeight(), Image.SCALE_DEFAULT));
            btnClearFields.setIcon(iconSearch);

        } catch (IOException ex) {
            Logger.getLogger(MenuClient.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setImgBtnSearch() {
        try {
            Image imgSearch = ImageIO.read(getClass().getResource("/img/logoSearch.jpg"));
            Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnSearchClient.getWidth(), btnSearchClient.getHeight(), Image.SCALE_DEFAULT));
            btnSearchClient.setIcon(iconSearch);
            btnFilterClient.setIcon(iconSearch);

        } catch (IOException ex) {
            Logger.getLogger(MenuClient.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setImgBtnUpdate() {
        try {
            Image imgSearch = ImageIO.read(getClass().getResource("/img/logoUpdate.jpg"));
            Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnUpdateClient.getWidth(), btnUpdateClient.getHeight(), Image.SCALE_DEFAULT));
            btnUpdateClient.setIcon(iconSearch);

        } catch (IOException ex) {
            Logger.getLogger(MenuClient.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    //______________________________________________________________________________________________________________
    //METODOS PARA LOS EVENTOS RAPIDOS SOBRE LA TABLA
    private void quickModification() {
        //Ubicamos la fila en la cual realizamos el evento
        int rowSelected = tableClients.getSelectedRow();
        //Seleccionamos el DNI de la fila seleccionada
        String DNIClient = String.valueOf(tableClients.getValueAt(rowSelected, 3));

        pestanas.setSelectedIndex(2);
        txtSearchClientModif.setText(DNIClient);
        this.fillFieldsModif();
    }

    private void quickDelete() {
        //Ubicamos la fila en la cual realizamos el evento
        int rowSelected = tableClients.getSelectedRow();
        //Seleccionamos el DNI de la fila seleccionada
        String DNIClient = String.valueOf(tableClients.getValueAt(rowSelected, 3));

        int response = JOptionPane.showConfirmDialog(this, "¿Desea eliminar el cliente?");
        if (response == 0) {
            DAOClient dao = new ClientDAOImpl();
            int r = dao.delete(Integer.parseInt(DNIClient));
            if (r == 1) {
                listClient.removeIf(c -> String.valueOf(c.getDNI()).equalsIgnoreCase(DNIClient));
            }
            this.setearTableClient();
            this.fillTableListClient(listClient);
        }
    }

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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pestanas = new javax.swing.JTabbedPane();
        pestanaQuery = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableClients = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        txtSearchClient = new javax.swing.JTextField();
        btnFilterClient = new javax.swing.JButton();
        pestanaRegister = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtLastName = new javax.swing.JTextField();
        txtDNI = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        btnClearFields = new javax.swing.JButton();
        btnSaveClient = new javax.swing.JButton();
        pestanaUpdate = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        txtSearchClientModif = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtPhoneModif = new javax.swing.JTextField();
        txtDNIModif = new javax.swing.JTextField();
        txtLastNameModif = new javax.swing.JTextField();
        txtNameModif = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtIdClientModif = new javax.swing.JTextField();
        btnUpdateClient = new javax.swing.JButton();
        btnSearchClient = new javax.swing.JButton();
        lblDNINotFound = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        pestanas.setBackground(new java.awt.Color(0, 0, 0));
        pestanas.setForeground(new java.awt.Color(255, 255, 255));
        pestanas.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        pestanaQuery.setBackground(new java.awt.Color(255, 255, 255));

        tableClients.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tableClients.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tableClients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tableClients.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableClientsMouseClicked(evt);
            }
        });
        tableClients.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tableClientsKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(tableClients);

        jLabel11.setFont(new java.awt.Font("Arial", 3, 20)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("INGRESE NOMBRE/APELLIDO/DNI");
        jLabel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtSearchClient.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        txtSearchClient.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSearchClient.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
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

        btnFilterClient.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        btnFilterClient.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout pestanaQueryLayout = new javax.swing.GroupLayout(pestanaQuery);
        pestanaQuery.setLayout(pestanaQueryLayout);
        pestanaQueryLayout.setHorizontalGroup(
            pestanaQueryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pestanaQueryLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(pestanaQueryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pestanaQueryLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtSearchClient, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnFilterClient, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(74, 74, 74))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(2, 2, 2))
        );
        pestanaQueryLayout.setVerticalGroup(
            pestanaQueryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pestanaQueryLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(pestanaQueryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnFilterClient, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchClient)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pestanas.addTab("Consultar", pestanaQuery);

        jLabel2.setBackground(new java.awt.Color(153, 153, 153));
        jLabel2.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 51, 51));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Nombre:");
        jLabel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel3.setBackground(new java.awt.Color(153, 153, 153));
        jLabel3.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 51));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Apellido:");
        jLabel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setBackground(new java.awt.Color(153, 153, 153));
        jLabel1.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("DNI:");
        jLabel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel6.setBackground(new java.awt.Color(153, 153, 153));
        jLabel6.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Teléfono:");
        jLabel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtName.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNameKeyTyped(evt);
            }
        });

        txtLastName.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtLastName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLastName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtLastName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtLastNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtLastNameKeyTyped(evt);
            }
        });

        txtDNI.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtDNI.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDNI.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtDNI.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDNIKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDNIKeyTyped(evt);
            }
        });

        txtPhone.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtPhone.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPhone.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPhoneKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPhoneKeyTyped(evt);
            }
        });

        btnClearFields.setBackground(new java.awt.Color(255, 255, 255));
        btnClearFields.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnClearFields.setForeground(new java.awt.Color(255, 255, 255));
        btnClearFields.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        btnClearFields.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClearFields.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearFieldsActionPerformed(evt);
            }
        });

        btnSaveClient.setBackground(new java.awt.Color(255, 255, 255));
        btnSaveClient.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        btnSaveClient.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSaveClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveClientActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pestanaRegisterLayout = new javax.swing.GroupLayout(pestanaRegister);
        pestanaRegister.setLayout(pestanaRegisterLayout);
        pestanaRegisterLayout.setHorizontalGroup(
            pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pestanaRegisterLayout.createSequentialGroup()
                .addContainerGap(189, Short.MAX_VALUE)
                .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(pestanaRegisterLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(txtPhone))
                    .addGroup(pestanaRegisterLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(txtDNI))
                    .addGroup(pestanaRegisterLayout.createSequentialGroup()
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtName)
                            .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(57, 57, 57)
                .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSaveClient, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClearFields, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(123, 123, 123))
        );
        pestanaRegisterLayout.setVerticalGroup(
            pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pestanaRegisterLayout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pestanaRegisterLayout.createSequentialGroup()
                        .addComponent(btnClearFields, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(btnSaveClient, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pestanaRegisterLayout.createSequentialGroup()
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDNI, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pestanaRegisterLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(117, Short.MAX_VALUE))
        );

        pestanas.addTab("Registrar", pestanaRegister);

        jLabel35.setFont(new java.awt.Font("Arial", 3, 20)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("INGRESE DNI CLIENTE");
        jLabel35.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtSearchClientModif.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtSearchClientModif.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSearchClientModif.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtSearchClientModif.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchClientModifKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearchClientModifKeyTyped(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Nombre:");
        jLabel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel7.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Apellido:");
        jLabel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel8.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("DNI:");
        jLabel8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel9.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Teléfono:");
        jLabel9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtPhoneModif.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtPhoneModif.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPhoneModif.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtPhoneModif.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPhoneModifKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPhoneModifKeyTyped(evt);
            }
        });

        txtDNIModif.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtDNIModif.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDNIModif.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtDNIModif.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDNIModifKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDNIModifKeyTyped(evt);
            }
        });

        txtLastNameModif.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtLastNameModif.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLastNameModif.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtLastNameModif.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtLastNameModifKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtLastNameModifKeyTyped(evt);
            }
        });

        txtNameModif.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtNameModif.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtNameModif.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtNameModif.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNameModifKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNameModifKeyTyped(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Nro Cliente:");
        jLabel10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtIdClientModif.setBackground(new java.awt.Color(153, 153, 153));
        txtIdClientModif.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        txtIdClientModif.setForeground(new java.awt.Color(255, 255, 255));
        txtIdClientModif.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtIdClientModif.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtIdClientModif.setEnabled(false);

        btnUpdateClient.setBackground(new java.awt.Color(255, 255, 255));
        btnUpdateClient.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnUpdateClient.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdateClient.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        btnUpdateClient.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdateClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateClientActionPerformed(evt);
            }
        });

        btnSearchClient.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        btnSearchClient.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearchClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchClientActionPerformed(evt);
            }
        });

        lblDNINotFound.setFont(new java.awt.Font("Yu Gothic UI Semibold", 2, 18)); // NOI18N
        lblDNINotFound.setForeground(new java.awt.Color(255, 51, 0));

        javax.swing.GroupLayout pestanaUpdateLayout = new javax.swing.GroupLayout(pestanaUpdate);
        pestanaUpdate.setLayout(pestanaUpdateLayout);
        pestanaUpdateLayout.setHorizontalGroup(
            pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pestanaUpdateLayout.createSequentialGroup()
                .addContainerGap(113, Short.MAX_VALUE)
                .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pestanaUpdateLayout.createSequentialGroup()
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(92, 92, 92)
                                .addComponent(txtNameModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(92, 92, 92)
                                .addComponent(txtIdClientModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(92, 92, 92)
                                .addComponent(txtLastNameModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(235, 235, 235))
                    .addGroup(pestanaUpdateLayout.createSequentialGroup()
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(92, 92, 92)
                                .addComponent(txtDNIModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(92, 92, 92)
                                .addComponent(txtPhoneModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(90, 90, 90)
                        .addComponent(btnUpdateClient, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pestanaUpdateLayout.createSequentialGroup()
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblDNINotFound, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSearchClientModif, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearchClient, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(126, 126, 126))
        );
        pestanaUpdateLayout.setVerticalGroup(
            pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pestanaUpdateLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnSearchClient, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(txtSearchClientModif, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDNINotFound, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pestanaUpdateLayout.createSequentialGroup()
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdClientModif, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNameModif, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLastNameModif, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pestanaUpdateLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtDNIModif, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPhoneModif, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pestanaUpdateLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pestanaUpdateLayout.createSequentialGroup()
                        .addComponent(btnUpdateClient, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addGap(57, 57, 57))
        );

        pestanas.addTab("Modificar", pestanaUpdate);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pestanas)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pestanas)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyTyped
        validarCamposTextos(evt);
    }//GEN-LAST:event_txtNameKeyTyped

    private void txtLastNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLastNameKeyTyped
        validarCamposTextos(evt);
    }//GEN-LAST:event_txtLastNameKeyTyped

    private void txtDNIKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDNIKeyTyped
        validarCamposNumericos(evt);
    }//GEN-LAST:event_txtDNIKeyTyped

    private void txtPhoneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneKeyTyped
        validarCamposNumericos(evt);
    }//GEN-LAST:event_txtPhoneKeyTyped

    private void btnClearFieldsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearFieldsActionPerformed
        this.clearFieldsRegisterClient();
    }//GEN-LAST:event_btnClearFieldsActionPerformed

    private void txtSearchClientModifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchClientModifKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.fillFieldsModif();
        }
        if (evt.getKeyCode() == KeyEvent.VK_0) {
            if (evt.isAltDown()) {
                pestanas.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_txtSearchClientModifKeyPressed

    private void txtSearchClientModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchClientModifKeyTyped
        validarCamposNumericos(evt);
    }//GEN-LAST:event_txtSearchClientModifKeyTyped

    private void txtNameModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameModifKeyTyped
        validarCamposTextos(evt);
    }//GEN-LAST:event_txtNameModifKeyTyped

    private void txtLastNameModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLastNameModifKeyTyped
        validarCamposTextos(evt);
    }//GEN-LAST:event_txtLastNameModifKeyTyped

    private void txtDNIModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDNIModifKeyTyped
        validarCamposNumericos(evt);
    }//GEN-LAST:event_txtDNIModifKeyTyped

    private void txtPhoneModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneModifKeyTyped
        validarCamposNumericos(evt);
    }//GEN-LAST:event_txtPhoneModifKeyTyped

    private void btnUpdateClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateClientActionPerformed
        this.updateClient();
    }//GEN-LAST:event_btnUpdateClientActionPerformed

    private void tableClientsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableClientsKeyPressed
        if (evt.getKeyChar() == 'm' && evt.isAltDown()) {
            quickModification();
        }
        if (evt.getKeyChar() == 'd' && evt.isAltDown()) {
            quickDelete();
        }
        if (evt.isAltDown() && evt.getKeyChar() == 'c') {
            MenuHairCutsClient mhc = new MenuHairCutsClient(null, true, this.getIdClientForSendHairCut());
            mhc.setVisible(true);
        }
    }//GEN-LAST:event_tableClientsKeyPressed

    private void tableClientsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableClientsMouseClicked
        if (evt.getClickCount() == 2) {
            MenuHairCutsClient mhc = new MenuHairCutsClient(null, true, this.getIdClientForSendHairCut());
            mhc.setVisible(true);
        }
    }//GEN-LAST:event_tableClientsMouseClicked

    private void btnSaveClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveClientActionPerformed
        this.saveClient();
    }//GEN-LAST:event_btnSaveClientActionPerformed

    private void txtPhoneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.saveClient();
        }
    }//GEN-LAST:event_txtPhoneKeyPressed

    private void btnSearchClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchClientActionPerformed
        this.fillFieldsModif();
    }//GEN-LAST:event_btnSearchClientActionPerformed

    private void txtSearchClientKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchClientKeyTyped
        filterTableClient(txtSearchClient.getText());
    }//GEN-LAST:event_txtSearchClientKeyTyped

    private void txtSearchClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchClientActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchClientActionPerformed

    private void txtNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtLastName.requestFocus();
        }
    }//GEN-LAST:event_txtNameKeyPressed

    private void txtLastNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLastNameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtDNI.requestFocus();
        }
    }//GEN-LAST:event_txtLastNameKeyPressed

    private void txtDNIKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDNIKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtPhone.requestFocus();
        }
    }//GEN-LAST:event_txtDNIKeyPressed

    private void txtNameModifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameModifKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateClient();
        }
    }//GEN-LAST:event_txtNameModifKeyPressed

    private void txtLastNameModifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLastNameModifKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateClient();
        }
    }//GEN-LAST:event_txtLastNameModifKeyPressed

    private void txtDNIModifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDNIModifKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateClient();
        }
    }//GEN-LAST:event_txtDNIModifKeyPressed

    private void txtPhoneModifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneModifKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateClient();
        }
    }//GEN-LAST:event_txtPhoneModifKeyPressed

    private void txtNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyReleased

    }//GEN-LAST:event_txtNameKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearFields;
    private javax.swing.JButton btnFilterClient;
    private javax.swing.JButton btnSaveClient;
    private javax.swing.JButton btnSearchClient;
    private javax.swing.JButton btnUpdateClient;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblDNINotFound;
    private javax.swing.JPanel pestanaQuery;
    private javax.swing.JPanel pestanaRegister;
    private javax.swing.JPanel pestanaUpdate;
    private javax.swing.JTabbedPane pestanas;
    private javax.swing.JTable tableClients;
    private javax.swing.JTextField txtDNI;
    private javax.swing.JTextField txtDNIModif;
    private javax.swing.JTextField txtIdClientModif;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtLastNameModif;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNameModif;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtPhoneModif;
    private javax.swing.JTextField txtSearchClient;
    private javax.swing.JTextField txtSearchClientModif;
    // End of variables declaration//GEN-END:variables
}
