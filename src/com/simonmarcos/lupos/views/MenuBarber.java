package com.simonmarcos.lupos.views;

import com.simonmarcos.lupos.dao.DAOBarber;
import com.simonmarcos.lupos.dao.impl.BarberDAOImpl;
import com.simonmarcos.lupos.model.Barber;
import com.toedter.calendar.JTextFieldDateEditor;
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
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class MenuBarber extends javax.swing.JDialog {

    private DefaultTableModel dtm = null;

    //Variable que me guardara la lista de todos los barberos
    private List<Barber> listBarber = null;
    //_______________________________________________________
    //Variable que me guardara la lista de todos los barberos al filtrarlos
    private List<Barber> listBarberFinal = null;
    //_______________________________________________________

    public MenuBarber(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setFocusable(true);
        this.setResizable(false);
        this.setSize(1203, 560);
        this.setLocationRelativeTo(null);
        this.setTitle("Menu Barberos");
        this.setearTableBarber();
        this.getAllBarberDB();
        this.setImgBtnSaveBarber();
        this.setImgBtnClearFields();
        this.setImgBtnSearch();
        this.setImgBtnUpdate();
        this.setearDateEntry();
        pestanas.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                switch (pestanas.getSelectedIndex()) {
                    case 0:
                        setearTableBarber();
                        getAllBarberDB();
                        clearFieldsPestanaQuery();
                        break;
                    case 1:
                        clearFieldsRegisterBarber();
                        break;
                    case 2:
                        clearFieldsUpdateBarber();
                        break;
                    default:
                        break;
                }
            }

        });
    }

    //______________________________________________________________________________________________________________
    //METODOS PARA MANIPULAR LA SEECION DE CONSULTA DE BARBEROS
    private void setearTableBarber() {
        dtm = new DefaultTableModel();
        String[] columns = {"Nro Barbero", "Apellido", "Nombre", "DNI", "Teléfono", "Domicilio", "Cumpleaños", "Fecha Ingreso"};
        dtm.setColumnIdentifiers(columns);
        tableBarber.setModel(dtm);

        //Codigo para aplicarle el formato personalizado al encabezado
        JTableHeader jth = tableBarber.getTableHeader();
        jth.setDefaultRenderer(new HeaderManagement());
        tableBarber.setTableHeader(jth);

        //Codigo para aplicarme los formatos personalizados
        tableBarber.getColumnModel().getColumn(0).setCellRenderer(new CellManagement());
        tableBarber.getColumnModel().getColumn(1).setCellRenderer(new CellManagement());
        tableBarber.getColumnModel().getColumn(2).setCellRenderer(new CellManagement());
        tableBarber.getColumnModel().getColumn(3).setCellRenderer(new CellManagement());
        tableBarber.getColumnModel().getColumn(4).setCellRenderer(new CellManagement());
        tableBarber.getColumnModel().getColumn(5).setCellRenderer(new CellManagement());
        tableBarber.getColumnModel().getColumn(6).setCellRenderer(new CellManagement());
        tableBarber.getColumnModel().getColumn(7).setCellRenderer(new CellManagement());

        //Codigo para especificar el tamaño de las celdas
        tableBarber.setRowHeight(25);
        //Codigo para no poder escribir las celdas
        tableBarber.setDefaultEditor(Object.class, null);
    }

    //Metodo que me obtendra todos los barberos de la DB y lo almacena en una variable global
    private void getAllBarberDB() {
        DAOBarber dao = new BarberDAOImpl();
        listBarber = dao.toList();

        fillTableListBarber(listBarber);
    }

    //Metodo que me rellena la tabla, donde recibe una lista con los barberos.
    private void fillTableListBarber(List<Barber> list) {
        String[] fila = new String[8];
        list.stream().sorted().forEach(c -> {
            fila[0] = String.valueOf(c.getIdBarber());
            fila[2] = c.getName();
            fila[1] = c.getLastName();
            fila[3] = String.valueOf(c.getDni());
            fila[4] = c.getPhone();
            fila[5] = c.getAddress();
            fila[6] = String.valueOf(c.getBirthday());
            fila[7] = String.valueOf(c.getDateEntry());
            dtm.addRow(fila);
        });
        tableBarber.setModel(dtm);
    }

    private void clearFieldsPestanaQuery() {
        txtSearchBarber.setText("");
    }

    //Metodo que me filtrara la tabla, segun lo tipeado (nombre, apellido, dni)
    private void filterTableBarber(String text) {
        listBarberFinal = new ArrayList<>();
        if (listBarber != null || listBarber.size() > 0) {
            listBarber.stream().forEach(c -> {
                String name = c.getName().toLowerCase();
                String lastName = c.getLastName();
                String dni = String.valueOf(c.getDni());
                if (name.contains(text.toLowerCase()) || lastName.contains(text.toLowerCase()) || dni.contains(text)) {
                    Barber barber = new Barber();
                    barber.setIdBarber(c.getIdBarber());
                    barber.setName(name);
                    barber.setLastName(lastName);
                    barber.setDni(c.getDni());
                    barber.setPhone(c.getPhone());
                    barber.setDateEntry(c.getDateEntry());
                    listBarberFinal.add(c);
                }
            });
        }
        this.setearTableBarber();
        this.fillTableListBarber(listBarberFinal);
    }

    //Metodo para obtener el cliente y enviarlo al otro JDialog
    private String getIdBarberForSendHairCut() {
        //Ubicamos la fila en la cual realizamos el evento
        int rowSelected = tableBarber.getSelectedRow();
        //Seleccionamos el DNI de la fila seleccionada
        String idBarber = String.valueOf(tableBarber.getValueAt(rowSelected, 0));
        return idBarber;
    }

//______________________________________________________________________________________________________________
    //METODOS PARA MANIPULAR LA SECCION DE REGISTRO DE BARBEROS
    private void clearFieldsRegisterBarber() {
        txtName.setText("");
        txtLastName.setText("");
        txtDNI.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        dateEntry.setDate(null);
        birthday.setDate(null);
    }

    private void saveBarber() {
        if (!txtName.getText().isEmpty() && !txtLastName.getText().isEmpty() && !txtDNI.getText().isEmpty() && !txtPhone.getText().isEmpty() && !dateEntry.getDate().toString().isEmpty()) {

            DAOBarber dao = new BarberDAOImpl();
            Barber b = new Barber();
            b.setName(txtName.getText());
            b.setLastName(txtLastName.getText());
            b.setDni(Integer.parseInt(txtDNI.getText()));
            b.setPhone(txtPhone.getText());
            b.setDateEntry(new java.sql.Date(dateEntry.getDate().getTime()));
            b.setAddress(txtAddress.getText());
            b.setBirthday(new java.sql.Date(birthday.getDate().getTime()));
            int r = dao.save(b);

            if (r == 1) {
                JOptionPane.showMessageDialog(this, "El barbero " + txtName.getText() + " se guardó correctamente.");
                this.clearFieldsRegisterBarber();
            } else if (r == 0) {
                JOptionPane.showMessageDialog(this, "El barbero con el DNI " + txtDNI.getText() + " ya se encuentra.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos por favor.");
        }
    }

    //______________________________________________________________________________________________________________
    //METODOS PARA MANIPULAR LA SECCION DE MODIFICACION DE BARBEROS
    private void clearFieldsUpdateBarber() {
        txtIdBarberModif.setText("");
        txtNameModif.setText("");
        txtLastNameModif.setText("");
        txtDNIModif.setText("");
        txtPhoneModif.setText("");
        txtSearchBarberModif.setText("");
        txtAddressModif.setText("");
        dateEntryModif.setDate(null);
        birthdayModif.setDate(null);
    }

    private void clearFieldsUpdateBarberWithoutSearch() {
        txtIdBarberModif.setText("");
        txtNameModif.setText("");
        txtLastNameModif.setText("");
        txtDNIModif.setText("");
        txtPhoneModif.setText("");
        txtAddressModif.setText("");
        dateEntryModif.setDate(null);
        birthdayModif.setDate(null);
    }

    private void updateBarber() {

        if (!txtSearchBarberModif.getText().isEmpty()) {
            DAOBarber dao = new BarberDAOImpl();
            Barber b = new Barber();
            b.setName(txtNameModif.getText());
            b.setLastName(txtLastNameModif.getText());
            b.setDni(Integer.parseInt(txtDNIModif.getText()));
            b.setPhone(txtPhoneModif.getText());
            b.setDateEntry(new java.sql.Date(dateEntryModif.getDate().getTime()));
            b.setAddress(txtAddressModif.getText());
            b.setBirthday(new java.sql.Date(birthday.getDate().getTime()));
            int r = dao.modificar(Integer.parseInt(txtSearchBarberModif.getText()), b);
            if (r == 1) {
                JOptionPane.showMessageDialog(this, "El barbero " + txtNameModif.getText() + " se modificó correctamente.");
                pestanas.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo realizar la modificación del barbero.");
            }
        }
    }

    private void fillFieldsModif() {
        if (!txtSearchBarberModif.getText().isEmpty()) {
            for (Barber b : listBarber) {
                if (String.valueOf(b.getDni()).equalsIgnoreCase(txtSearchBarberModif.getText())) {
                    lblDNINotFound.setText("");
                    txtIdBarberModif.setText(String.valueOf(b.getIdBarber()));
                    txtNameModif.setText(b.getName());
                    txtLastNameModif.setText(b.getLastName());
                    txtDNIModif.setText(String.valueOf(b.getDni()));
                    txtPhoneModif.setText(b.getPhone());
                    dateEntryModif.setDate(b.getDateEntry());
                    txtAddressModif.setText(b.getAddress());
                    birthday.setDate(b.getBirthday());
                }
            }
        }
    }

    //______________________________________________________________________________________________________________
    //METODOS PARA COLOCAR IMAGENES A LOS BOTONES
    private void setImgBtnSaveBarber() {
        try {
            Image imgSearch = ImageIO.read(getClass().getResource("/img/logoSave.png"));
            Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnSaveBarber.getWidth(), btnSaveBarber.getHeight(), Image.SCALE_DEFAULT));
            btnSaveBarber.setIcon(iconSearch);

        } catch (IOException ex) {
            Logger.getLogger(MenuBarber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setImgBtnClearFields() {
        try {
            Image imgSearch = ImageIO.read(getClass().getResource("/img/logoClear.png"));
            Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnClearFields.getWidth(), btnClearFields.getHeight(), Image.SCALE_DEFAULT));
            btnClearFields.setIcon(iconSearch);

        } catch (IOException ex) {
            Logger.getLogger(MenuBarber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setImgBtnSearch() {
        try {
            Image imgSearch = ImageIO.read(getClass().getResource("/img/logoSearch.jpg"));
            Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnSearchBarber.getWidth(), btnSearchBarber.getHeight(), Image.SCALE_DEFAULT));
            btnSearchBarber.setIcon(iconSearch);
            btnFilterBarber.setIcon(iconSearch);

        } catch (IOException ex) {
            Logger.getLogger(MenuBarber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setImgBtnUpdate() {
        try {
            Image imgSearch = ImageIO.read(getClass().getResource("/img/logoUpdate.jpg"));
            Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnUpdateBarber.getWidth(), btnUpdateBarber.getHeight(), Image.SCALE_DEFAULT));
            btnUpdateBarber.setIcon(iconSearch);

        } catch (IOException ex) {
            Logger.getLogger(MenuBarber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //______________________________________________________________________________________________________________
    //METODOS PARA LOS EVENTOS RAPIDOS SOBRE LA TABLA
    private void quickModification() {
        //Ubicamos la fila en la cual realizamos el evento
        int rowSelected = tableBarber.getSelectedRow();
        //Seleccionamos el DNI de la fila seleccionada
        String DNIBarber = String.valueOf(tableBarber.getValueAt(rowSelected, 3));
        pestanas.setSelectedIndex(2);
        txtSearchBarberModif.setText(DNIBarber);
        this.fillFieldsModif();
    }

    private void quickDelete() {
        //Ubicamos la fila en la cual realizamos el evento
        int rowSelected = tableBarber.getSelectedRow();
        //Seleccionamos el DNI de la fila seleccionada
        String DNIBarber = String.valueOf(tableBarber.getValueAt(rowSelected, 3));

        int response = JOptionPane.showConfirmDialog(this, "¿Desea eliminar el barbero?");
        if (response == 0) {
            DAOBarber dao = new BarberDAOImpl();
            int r = dao.delete(Integer.parseInt(DNIBarber));
            if (r == 1) {
                listBarber.removeIf(c -> String.valueOf(c.getDni()).equalsIgnoreCase(DNIBarber));
            }
            this.setearTableBarber();
            this.fillTableListBarber(listBarber);
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

    private void setearDateEntry() {

        dateEntry.setDateFormatString("dd-MM-yyyy");
        dateEntryModif.setDateFormatString("dd-MM-yyyy");
        birthday.setDateFormatString("dd-MM-yyyy");
        birthdayModif.setDateFormatString("dd-MM-yyyy");

        JTextFieldDateEditor editorDateEntry = (JTextFieldDateEditor) dateEntry.getDateEditor();
        editorDateEntry.setEditable(false);
        editorDateEntry.setHorizontalAlignment(JTextField.CENTER);

        JTextFieldDateEditor editorDateEntryModif = (JTextFieldDateEditor) dateEntryModif.getDateEditor();
        editorDateEntryModif.setEditable(false);
        editorDateEntryModif.setHorizontalAlignment(JTextField.CENTER);

        JTextFieldDateEditor editorBirthdayModif = (JTextFieldDateEditor) birthdayModif.getDateEditor();
        editorBirthdayModif.setEditable(false);
        editorBirthdayModif.setHorizontalAlignment(JTextField.CENTER);

        JTextFieldDateEditor editorBirthday = (JTextFieldDateEditor) birthday.getDateEditor();
        editorBirthday.setEditable(false);
        editorBirthday.setHorizontalAlignment(JTextField.CENTER);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pestanas = new javax.swing.JTabbedPane();
        pestanaQuery = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableBarber = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        txtSearchBarber = new javax.swing.JTextField();
        btnFilterBarber = new javax.swing.JButton();
        pestanaRegister = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtLastName = new javax.swing.JTextField();
        txtDNI = new javax.swing.JTextField();
        btnClearFields = new javax.swing.JButton();
        btnSaveBarber = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        dateEntry = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        birthday = new com.toedter.calendar.JDateChooser();
        pestanaUpdate = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        txtSearchBarberModif = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtDNIModif = new javax.swing.JTextField();
        txtLastNameModif = new javax.swing.JTextField();
        txtNameModif = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtIdBarberModif = new javax.swing.JTextField();
        btnUpdateBarber = new javax.swing.JButton();
        btnSearchBarber = new javax.swing.JButton();
        lblDNINotFound = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        dateEntryModif = new com.toedter.calendar.JDateChooser();
        jLabel15 = new javax.swing.JLabel();
        txtAddressModif = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtPhoneModif = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        birthdayModif = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pestanas.setBackground(new java.awt.Color(0, 0, 0));
        pestanas.setForeground(new java.awt.Color(255, 255, 255));
        pestanas.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        pestanaQuery.setBackground(new java.awt.Color(255, 255, 255));
        pestanaQuery.setPreferredSize(new java.awt.Dimension(1169, 200));

        tableBarber.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tableBarber.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tableBarber.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tableBarber.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableBarberMouseClicked(evt);
            }
        });
        tableBarber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tableBarberKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(tableBarber);

        jLabel11.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("INGRESE NOMBRE/APELLIDO");
        jLabel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtSearchBarber.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        txtSearchBarber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSearchBarber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
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

        btnFilterBarber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        btnFilterBarber.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout pestanaQueryLayout = new javax.swing.GroupLayout(pestanaQuery);
        pestanaQuery.setLayout(pestanaQueryLayout);
        pestanaQueryLayout.setHorizontalGroup(
            pestanaQueryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pestanaQueryLayout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearchBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFilterBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(101, Short.MAX_VALUE))
            .addComponent(jScrollPane3)
        );
        pestanaQueryLayout.setVerticalGroup(
            pestanaQueryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pestanaQueryLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(pestanaQueryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnFilterBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchBarber)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pestanas.addTab("Consultar", pestanaQuery);

        pestanaRegister.setMaximumSize(new java.awt.Dimension(1100, 600));

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

        txtName.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNameKeyPressed(evt);
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

        btnSaveBarber.setBackground(new java.awt.Color(255, 255, 255));
        btnSaveBarber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        btnSaveBarber.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSaveBarber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveBarberActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(153, 153, 153));
        jLabel13.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(51, 51, 51));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Fecha ingreso:");
        jLabel13.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        dateEntry.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        dateEntry.setDateFormatString("dd-mm-yyyy");
        dateEntry.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N

        jLabel14.setBackground(new java.awt.Color(153, 153, 153));
        jLabel14.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(51, 51, 51));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Domicilio:");
        jLabel14.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtAddress.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtAddress.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtAddress.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtAddress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAddressKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtAddressKeyTyped(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(153, 153, 153));
        jLabel16.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Teléfono:");
        jLabel16.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

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

        jLabel18.setBackground(new java.awt.Color(153, 153, 153));
        jLabel18.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(51, 51, 51));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Fecha Nacimiento:");
        jLabel18.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        birthday.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        birthday.setDateFormatString("dd-MM-yyyy");
        birthday.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N

        javax.swing.GroupLayout pestanaRegisterLayout = new javax.swing.GroupLayout(pestanaRegister);
        pestanaRegister.setLayout(pestanaRegisterLayout);
        pestanaRegisterLayout.setHorizontalGroup(
            pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pestanaRegisterLayout.createSequentialGroup()
                .addContainerGap(166, Short.MAX_VALUE)
                .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(pestanaRegisterLayout.createSequentialGroup()
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(txtPhone))
                        .addGroup(pestanaRegisterLayout.createSequentialGroup()
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(txtAddress))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pestanaRegisterLayout.createSequentialGroup()
                            .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(10, 10, 10)
                            .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtName)
                                .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(pestanaRegisterLayout.createSequentialGroup()
                            .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(10, 10, 10)
                            .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(dateEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtDNI, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pestanaRegisterLayout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(birthday, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(71, 71, 71)
                .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClearFields, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSaveBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(123, 123, 123))
        );
        pestanaRegisterLayout.setVerticalGroup(
            pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pestanaRegisterLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pestanaRegisterLayout.createSequentialGroup()
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDNI, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dateEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pestanaRegisterLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pestanaRegisterLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pestanaRegisterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(birthday, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pestanaRegisterLayout.createSequentialGroup()
                        .addComponent(btnClearFields, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSaveBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(458, Short.MAX_VALUE))
        );

        pestanas.addTab("Registrar", pestanaRegister);

        jLabel35.setFont(new java.awt.Font("Arial", 3, 24)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("INGRESE DNI BARBERO");
        jLabel35.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtSearchBarberModif.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtSearchBarberModif.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSearchBarberModif.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtSearchBarberModif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchBarberModifActionPerformed(evt);
            }
        });
        txtSearchBarberModif.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchBarberModifKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearchBarberModifKeyTyped(evt);
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
        jLabel10.setText("Nro Barbero");
        jLabel10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtIdBarberModif.setBackground(new java.awt.Color(153, 153, 153));
        txtIdBarberModif.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        txtIdBarberModif.setForeground(new java.awt.Color(255, 255, 255));
        txtIdBarberModif.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtIdBarberModif.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtIdBarberModif.setEnabled(false);

        btnUpdateBarber.setBackground(new java.awt.Color(255, 255, 255));
        btnUpdateBarber.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnUpdateBarber.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdateBarber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        btnUpdateBarber.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdateBarber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateBarberActionPerformed(evt);
            }
        });

        btnSearchBarber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        btnSearchBarber.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearchBarber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchBarberActionPerformed(evt);
            }
        });

        lblDNINotFound.setFont(new java.awt.Font("Yu Gothic UI Semibold", 2, 18)); // NOI18N
        lblDNINotFound.setForeground(new java.awt.Color(255, 51, 0));

        jLabel12.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Fecha ingreso:");
        jLabel12.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        dateEntryModif.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        dateEntryModif.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        dateEntryModif.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dateEntryModifKeyTyped(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Domicilio:");
        jLabel15.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtAddressModif.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        txtAddressModif.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtAddressModif.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        txtAddressModif.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAddressModifKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtAddressModifKeyTyped(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Teléfono:");
        jLabel17.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

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

        jLabel19.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Fecha Nacimiento:");
        jLabel19.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        birthdayModif.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204)));
        birthdayModif.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        birthdayModif.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                birthdayModifKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout pestanaUpdateLayout = new javax.swing.GroupLayout(pestanaUpdate);
        pestanaUpdate.setLayout(pestanaUpdateLayout);
        pestanaUpdateLayout.setHorizontalGroup(
            pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pestanaUpdateLayout.createSequentialGroup()
                .addContainerGap(104, Short.MAX_VALUE)
                .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pestanaUpdateLayout.createSequentialGroup()
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(92, 92, 92)
                                .addComponent(txtNameModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(92, 92, 92)
                                .addComponent(txtIdBarberModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(92, 92, 92)
                                .addComponent(txtLastNameModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(235, 235, 235))
                    .addGroup(pestanaUpdateLayout.createSequentialGroup()
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblDNINotFound, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSearchBarberModif, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearchBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pestanaUpdateLayout.createSequentialGroup()
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(birthdayModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtAddressModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(pestanaUpdateLayout.createSequentialGroup()
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(92, 92, 92)
                                    .addComponent(txtDNIModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pestanaUpdateLayout.createSequentialGroup()
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(92, 92, 92)
                                    .addComponent(dateEntryModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pestanaUpdateLayout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtPhoneModif, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(90, 90, 90)
                        .addComponent(btnUpdateBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(126, 126, 126))
        );
        pestanaUpdateLayout.setVerticalGroup(
            pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pestanaUpdateLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnSearchBarber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(txtSearchBarberModif, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDNINotFound, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pestanaUpdateLayout.createSequentialGroup()
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdBarberModif, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNameModif, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLastNameModif, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pestanaUpdateLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtDNIModif, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dateEntryModif, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAddressModif, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPhoneModif, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pestanaUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(birthdayModif, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnUpdateBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(454, Short.MAX_VALUE))
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

    private void txtAddressModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAddressModifKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddressModifKeyTyped

    private void txtAddressModifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAddressModifKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateBarber();
        }
    }//GEN-LAST:event_txtAddressModifKeyPressed

    private void btnSearchBarberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchBarberActionPerformed
        this.fillFieldsModif();
    }//GEN-LAST:event_btnSearchBarberActionPerformed

    private void btnUpdateBarberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateBarberActionPerformed
        this.updateBarber();
    }//GEN-LAST:event_btnUpdateBarberActionPerformed

    private void txtNameModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameModifKeyTyped
        validarCamposTextos(evt);
    }//GEN-LAST:event_txtNameModifKeyTyped

    private void txtNameModifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameModifKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateBarber();
        }
    }//GEN-LAST:event_txtNameModifKeyPressed

    private void txtLastNameModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLastNameModifKeyTyped
        validarCamposTextos(evt);
    }//GEN-LAST:event_txtLastNameModifKeyTyped

    private void txtLastNameModifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLastNameModifKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateBarber();
        }
    }//GEN-LAST:event_txtLastNameModifKeyPressed

    private void txtDNIModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDNIModifKeyTyped
        validarCamposNumericos(evt);
    }//GEN-LAST:event_txtDNIModifKeyTyped

    private void txtDNIModifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDNIModifKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateBarber();
        }
    }//GEN-LAST:event_txtDNIModifKeyPressed

    private void txtSearchBarberModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchBarberModifKeyTyped
        validarCamposNumericos(evt);
    }//GEN-LAST:event_txtSearchBarberModifKeyTyped

    private void txtSearchBarberModifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchBarberModifKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.fillFieldsModif();
        }
        if (evt.getKeyCode() == KeyEvent.VK_0) {
            if (evt.isAltDown()) {
                pestanas.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_txtSearchBarberModifKeyPressed

    private void txtPhoneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneKeyTyped

    private void txtPhoneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            saveBarber();
        }
    }//GEN-LAST:event_txtPhoneKeyPressed

    private void txtAddressKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAddressKeyTyped

    }//GEN-LAST:event_txtAddressKeyTyped

    private void txtAddressKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAddressKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            saveBarber();
        }
    }//GEN-LAST:event_txtAddressKeyPressed

    private void btnSaveBarberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveBarberActionPerformed
        this.saveBarber();
    }//GEN-LAST:event_btnSaveBarberActionPerformed

    private void btnClearFieldsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearFieldsActionPerformed
        this.clearFieldsRegisterBarber();
    }//GEN-LAST:event_btnClearFieldsActionPerformed

    private void txtDNIKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDNIKeyTyped
        validarCamposNumericos(evt);
    }//GEN-LAST:event_txtDNIKeyTyped

    private void txtDNIKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDNIKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            saveBarber();
        }
    }//GEN-LAST:event_txtDNIKeyPressed

    private void txtLastNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLastNameKeyTyped
        validarCamposTextos(evt);
    }//GEN-LAST:event_txtLastNameKeyTyped

    private void txtLastNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLastNameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            saveBarber();
        }
    }//GEN-LAST:event_txtLastNameKeyPressed

    private void txtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyTyped
        validarCamposTextos(evt);
    }//GEN-LAST:event_txtNameKeyTyped

    private void txtNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            saveBarber();
        }
    }//GEN-LAST:event_txtNameKeyPressed

    private void txtSearchBarberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchBarberKeyTyped
        filterTableBarber(txtSearchBarber.getText());
    }//GEN-LAST:event_txtSearchBarberKeyTyped

    private void txtSearchBarberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchBarberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchBarberActionPerformed

    private void tableBarberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableBarberKeyPressed
        if (evt.getKeyChar() == 'm' && evt.isAltDown()) {
            quickModification();
        }
        if (evt.getKeyChar() == 'd' && evt.isAltDown()) {
            quickDelete();
        }
        if (evt.isAltDown() && evt.getKeyChar() == 'c') {
            MenuHairCutsBarber mhb = new MenuHairCutsBarber(null, true, this.getIdBarberForSendHairCut());
            mhb.setVisible(true);
        }
    }//GEN-LAST:event_tableBarberKeyPressed

    private void tableBarberMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableBarberMouseClicked
        if (evt.getClickCount() == 2) {
            MenuHairCutsBarber mhb = new MenuHairCutsBarber(null, true, this.getIdBarberForSendHairCut());
            mhb.setVisible(true);
        }
    }//GEN-LAST:event_tableBarberMouseClicked

    private void txtPhoneModifKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneModifKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateBarber();
        }
    }//GEN-LAST:event_txtPhoneModifKeyPressed

    private void txtPhoneModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneModifKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneModifKeyTyped

    private void dateEntryModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dateEntryModifKeyTyped
        char c = evt.getKeyChar();
        if (Character.isDigit(c)) {
            getToolkit().beep();
            evt.consume();
        }
        char c1 = evt.getKeyChar();
        if ((c1 < '0' || c1 > '9')) {
            evt.consume();
        }
    }//GEN-LAST:event_dateEntryModifKeyTyped

    private void txtSearchBarberModifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchBarberModifActionPerformed
        this.fillFieldsModif();
    }//GEN-LAST:event_txtSearchBarberModifActionPerformed

    private void birthdayModifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_birthdayModifKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_birthdayModifKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser birthday;
    private com.toedter.calendar.JDateChooser birthdayModif;
    private javax.swing.JButton btnClearFields;
    private javax.swing.JButton btnFilterBarber;
    private javax.swing.JButton btnSaveBarber;
    private javax.swing.JButton btnSearchBarber;
    private javax.swing.JButton btnUpdateBarber;
    private com.toedter.calendar.JDateChooser dateEntry;
    private com.toedter.calendar.JDateChooser dateEntryModif;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblDNINotFound;
    private javax.swing.JPanel pestanaQuery;
    private javax.swing.JPanel pestanaRegister;
    private javax.swing.JPanel pestanaUpdate;
    private javax.swing.JTabbedPane pestanas;
    private javax.swing.JTable tableBarber;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtAddressModif;
    private javax.swing.JTextField txtDNI;
    private javax.swing.JTextField txtDNIModif;
    private javax.swing.JTextField txtIdBarberModif;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtLastNameModif;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNameModif;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtPhoneModif;
    private javax.swing.JTextField txtSearchBarber;
    private javax.swing.JTextField txtSearchBarberModif;
    // End of variables declaration//GEN-END:variables
}
