package com.simonmarcos.lupos.views;

import com.simonmarcos.lupos.dao.DAOBarber;
import com.simonmarcos.lupos.dao.DAOClient;
import com.simonmarcos.lupos.dao.DAOCuts;
import com.simonmarcos.lupos.dao.DAOHairCut;
import com.simonmarcos.lupos.dao.impl.BarberDAOImpl;
import com.simonmarcos.lupos.dao.impl.ClientDAOImpl;
import com.simonmarcos.lupos.dao.impl.CutsDAOImpl;
import com.simonmarcos.lupos.dao.impl.HairCutDAOImpl;
import com.simonmarcos.lupos.model.Barber;
import com.simonmarcos.lupos.model.Client;
import com.simonmarcos.lupos.model.Cuts;
import com.simonmarcos.lupos.model.HairCut;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class MenuPrincipal extends javax.swing.JFrame {

    private DefaultTableModel dtm;
    private List<Barber> listAllBarber;

    public MenuPrincipal() {
        initComponents();
        //this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Lupo's Barber");
        this.dimensionWindows();
        this.setImgBtnClient();
        this.setImgBtnBarber();
        this.setImgBtnCuts();
        this.setImgPanelLogoLupos();
        this.setearTableBarberCuts();
        this.fillTableBarberCuts();
        this.fillListAllClient();
        this.fillListAllBarber();
        this.fillTableBarberHairCutsToday();
        this.getDateToday();
    }

    private void dimensionWindows() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        this.setSize(width, height);
        this.setLocation((width - this.getWidth()) / 2, (height - this.getHeight()) / 2);
    }

    private String getDateToday() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(ts);
    }

    private void clearAllFields() {
        checkCutAdult.setSelected(false);
        checkCutBoy.setSelected(false);
        checkCutBeard.setSelected(false);
        checkCutDrawing.setSelected(false);

        combolistAllClient.setSelectedIndex(0);
        combolistAllBarber.setSelectedIndex(0);
    }

    //_________________________________________________________________________________________________________
    //METODOS PARA GUARDAR EL CORTE EN LA BASE DE DATOS
    private void saveCuts() {
        DAOHairCut dao = new HairCutDAOImpl();

        String idClient = "";
        String nameClient = "";
        String idBarber = "";
        String nameBarber = "";
        String totalPrice = "";
        String totalPriceBarber = "";
        String cutsFinal = "";

        //--------------------------------------------------------------
        for (int i = 0; i < getIdClientForListClient().length; i++) {
            if (i == 0) {
                nameClient += getIdClientForListClient()[i];
            } else {
                idClient += getIdClientForListClient()[i];

            }
        }
        Client c = new Client();
        c.setIdClient(Integer.parseInt(idClient));

        //------------------------------------------------------------
        for (int i = 0; i < getIdBarberForListBarber().length; i++) {
            if (i == 0) {
                nameBarber += getIdBarberForListBarber()[i];
            } else {
                idBarber += getIdBarberForListBarber()[i];
            }
        }

        Barber b = new Barber();
        b.setIdBarber(Integer.parseInt(idBarber));

        //------------------------------------------------------------
        String getPrice[] = getPriceDependingCuts();
        for (int i = 0; i < getPrice.length; i++) {
            switch (i) {
                case 0:
                    totalPrice += getPrice[i];
                    break;
                case 1:
                    totalPriceBarber += getPrice[i];
                    break;
                default:
                    cutsFinal += getPrice[i];
                    break;
            }
        }

        HairCut h = new HairCut();
        h.setClient(c);
        h.setBarber(b);
        h.setCuts(cutsFinal);
        h.setDate(new java.sql.Timestamp(System.currentTimeMillis()));
        h.setPrice(Double.parseDouble(totalPrice));
        h.setPriceBarber(Double.parseDouble(totalPriceBarber));
        int r = dao.save(h);
        if (r == 1) {
            JOptionPane.showMessageDialog(this, "Barbero: " + nameBarber + " \nCliente: " + nameClient + " \nCorte: " + cutsFinal + " \nPrecio Total: $" + totalPrice + "\n\n¡¡MUCHAS GRACIAS!!");
            clearAllFields();
        }
    }

    //Metodo que me devolvera el precio total del corte segun los checks seleccionados.
    //Tambien extra, me devolvera los cortes que se realizo
    private String[] getPriceDependingCuts() {

        DAOCuts dao = new CutsDAOImpl();
        List<Cuts> listCuts = dao.toList();

        String cutsFinal = "";

        boolean cutAdultSelected = checkCutAdult.isSelected();
        boolean cutBoySelected = checkCutBoy.isSelected();
        boolean cutBeardSelected = checkCutBeard.isSelected();
        boolean cutDrawingSelected = checkCutDrawing.isSelected();

        double totalPrice = 0;
        double totalPriceBarber = 0;
        //Si solo esta seleccionado el check de CutsAdult
        if (cutAdultSelected && !cutBoySelected && !cutBeardSelected && !cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutAdult.getText())) {
                    totalPrice = c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutAdult.getText();
        }//Si solo esta seleccionado el cutAdult y el cutBeard
        else if (cutAdultSelected && !cutBoySelected && cutBeardSelected && !cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutAdult.getText()) || c.getType().equalsIgnoreCase(checkCutBeard.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutAdult.getText() + " / " + checkCutBeard.getText();
        } //Si solo esta seleccionado el cutAdult y el cutDrawing 
        else if (cutAdultSelected && !cutBoySelected && !cutBeardSelected && cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutAdult.getText()) || c.getType().equalsIgnoreCase(checkCutDrawing.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutAdult.getText() + " / " + checkCutDrawing.getText();
        } //Si solo esta seleccionado el cutAdult y el cutDrawing y cutBeard
        else if (cutAdultSelected && !cutBoySelected && cutBeardSelected && cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutAdult.getText()) || c.getType().equalsIgnoreCase(checkCutBeard.getText()) || c.getType().equalsIgnoreCase(checkCutDrawing.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutAdult.getText() + " / " + checkCutBeard.getText() + " / " + checkCutDrawing.getText();
        } //Si solo esta seleccionado el check de Boy 
        else if (!cutAdultSelected && cutBoySelected && !cutBeardSelected && !cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutBoy.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutBoy.getText();
        }//Si solo esta seleccionado el checkBoy y checkDrawing
        else if (!cutAdultSelected && cutBoySelected && !cutBeardSelected && cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutBoy.getText()) || c.getType().equalsIgnoreCase(checkCutDrawing.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutBoy.getText() + " / " + checkCutDrawing.getText();
        } //Si solo esta seleccionado el checkBeard
        else if (!cutAdultSelected && !cutBoySelected && cutBeardSelected && !cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutBeard.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutBeard.getText();
        }//En el caso de que todos los check esten seleccionados
        else if (cutAdultSelected && cutBoySelected && cutBeardSelected && cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutBoy.getText()) || c.getType().equalsIgnoreCase(checkCutDrawing.getText()) || c.getType().equalsIgnoreCase(checkCutAdult.getText()) || c.getType().equalsIgnoreCase(checkCutBeard.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutAdult.getText() + " / " + checkCutBeard.getText() + " / " + checkCutDrawing.getText() + " / " + checkCutBoy.getText();
        }
        String[] stringReturn = {String.valueOf(totalPrice), String.valueOf(totalPriceBarber), cutsFinal};
        return stringReturn;
    }

    //__________________________________________________________________________________________________________
    //METODOS PARA MANIPULAR LA TABLA DE LOS CORTES
    private void setearTableBarberCuts() {
        dtm = new DefaultTableModel();
        String[] columns = {"Barbero", "Cortes Adultos", "Cortes niños", "Barbas", "Dibujos", "Ganancias"};
        dtm.setColumnIdentifiers(columns);
        tableBarberCuts.setModel(dtm);

        //Codigo para aplicarle el formato personalizado al encabezado
        JTableHeader jth = tableBarberCuts.getTableHeader();
        jth.setDefaultRenderer(new HeaderManagement());
        tableBarberCuts.setTableHeader(jth);

        //Codigo para aplicarme los formatos personalizados
        tableBarberCuts.getColumnModel().getColumn(0).setCellRenderer(new CellManagement());
        tableBarberCuts.getColumnModel().getColumn(1).setCellRenderer(new CellManagement());
        tableBarberCuts.getColumnModel().getColumn(2).setCellRenderer(new CellManagement());
        tableBarberCuts.getColumnModel().getColumn(3).setCellRenderer(new CellManagement());
        tableBarberCuts.getColumnModel().getColumn(4).setCellRenderer(new CellManagement());
        tableBarberCuts.getColumnModel().getColumn(5).setCellRenderer(new CellManagement());

        //Codigo para especificar el tamaño de las celdas
        tableBarberCuts.setRowHeight(30);
        //Codigo para no poder escribir las celdas
        tableBarberCuts.setDefaultEditor(Object.class, null);
    }

    private void fillTableBarberCuts() {
        String[] fila = new String[6];
        listAllBarber = this.getAllBarber();
        listAllBarber.stream().sorted().forEach(b -> {
            fila[0] = b.getLastName() + " " + b.getName();
            fila[1] = "0";
            fila[2] = "0";
            fila[3] = "0";
            fila[4] = "0";
            fila[5] = "0";
            dtm.addRow(fila);
        });
        tableBarberCuts.setModel(dtm);
    }

    private List<Barber> getAllBarber() {
        DAOBarber dao = new BarberDAOImpl();
        return dao.toListByNameAndLastName();
    }

    private void fillTableBarberHairCutsToday() {

        int row = tableBarberCuts.getRowCount();
        DAOHairCut dao = new HairCutDAOImpl();
        List<HairCut> listHairCut = dao.queryFilterForDate(this.getDateToday());

        double earningsBoos = 0;

        //Recorremos los datos consultados de la base de datos
        for (HairCut hc : listHairCut) {
            //Accion para sumar todas las ganancias diarias
            earningsBoos += hc.getPrice();

            //Recorremos las filas de la tabla
            for (int i = 0; i < row; i++) {
                String cutsAdult = tableBarberCuts.getValueAt(i, 1).toString();
                String cutsBoy = tableBarberCuts.getValueAt(i, 2).toString();
                String cutsBeard = tableBarberCuts.getValueAt(i, 3).toString();
                String cutsDrawing = tableBarberCuts.getValueAt(i, 4).toString();
                String priceCuts = tableBarberCuts.getValueAt(i, 5).toString();

                int countCutsAdult;
                int countCutsBoy;
                int countCutsBeard;
                int countCutsDrawing;
                double totalPriceCuts;

                //Obtenemos el valor en esa fila
                String name = tableBarberCuts.getValueAt(i, 0).toString();

                //Un condicional para saber si coinciden los nombres
                if (name.equalsIgnoreCase((hc.getBarber().getLastName()) + " " + hc.getBarber().getName())) {
                    String[] cutsSplit = hc.getCuts().split(" / ");
                    for (int j = 0; j < cutsSplit.length; j++) {
                        String nameCuts = cutsSplit[j];
                        if (nameCuts.contains("Adulto")) {
                            countCutsAdult = Integer.parseInt(cutsAdult) + 1;
                            tableBarberCuts.setValueAt(String.valueOf(countCutsAdult), i, 1);
                        } else if (nameCuts.contains("Niño")) {
                            countCutsBoy = Integer.parseInt(cutsBoy) + 1;
                            tableBarberCuts.setValueAt(String.valueOf(countCutsBoy), i, 2);
                        } else if (nameCuts.contains("Barba")) {
                            countCutsBeard = Integer.parseInt(cutsBeard) + 1;
                            tableBarberCuts.setValueAt(String.valueOf(countCutsBeard), i, 3);
                        } else if (nameCuts.contains("Dibujo")) {
                            countCutsDrawing = Integer.parseInt(cutsDrawing) + 1;
                            tableBarberCuts.setValueAt(String.valueOf(countCutsDrawing), i, 4);
                        }
                        totalPriceCuts = Double.valueOf(priceCuts);
                        totalPriceCuts += hc.getPriceBarber();
                        tableBarberCuts.setValueAt(String.valueOf(String.valueOf(totalPriceCuts)), i, 5);
                    }

                }

            }
        }
        this.getAllCutsAndEarningTheDay(earningsBoos);
    }

    private void getAllCutsAndEarningTheDay(double earnings) {

        int row = tableBarberCuts.getRowCount();

        int countHairCutsAdult = 0;
        int countHairCutsBoys = 0;
        int countHairCutsBeader = 0;
        int countHairCutsDrawing = 0;
        double countEarnings = 0;

        for (int i = 0; i < row; i++) {
            countHairCutsAdult += Integer.parseInt(tableBarberCuts.getValueAt(i, 1).toString());
            countHairCutsBoys += Integer.parseInt(tableBarberCuts.getValueAt(i, 2).toString());
            countHairCutsBeader += Integer.parseInt(tableBarberCuts.getValueAt(i, 3).toString());
            countHairCutsDrawing += Integer.parseInt(tableBarberCuts.getValueAt(i, 4).toString());
            countEarnings += Double.parseDouble(tableBarberCuts.getValueAt(i, 5).toString());
        }

        lblCutsAdult.setText("Cortes Adultos: " + countHairCutsAdult);
        lblCutsBoy.setText("Cortes Niños: " + countHairCutsBoys);
        lblCutsBeard.setText("Barba: " + countHairCutsBeader);
        lblCutsDraw.setText("Dibujos: " + countHairCutsDrawing);
        lblEarningsTotal.setText("Ganancias Total: $ " + earnings);
        lblEarnings.setText("Ganancias: $ " + (earnings - countEarnings));
    }

    //______________________________________________________________________________________________________________
    //METODOS PARA MANIPULAR LOS JCOMBOBOX
    //Metodo que me rellenara el JComboBox de clientes con el nombre y codigo del cliente
    private void fillListAllClient() {
        combolistAllClient.removeAllItems();
        DAOClient dao = new ClientDAOImpl();
        dao.toList().stream().sorted().forEach(c -> {
            combolistAllClient.addItem(c.getLastName() + " " + c.getName() + " - " + c.getIdClient());
        });

        combolistAllClient.setSelectedItem("");
        AutoCompleteDecorator.decorate(combolistAllClient);
    }

    //Metodo que me rellenara el JComboBox de barberos con el nombre y codigo del barbero
    private void fillListAllBarber() {
        combolistAllBarber.removeAllItems();
        listAllBarber.stream().sorted().forEach(c -> {
            combolistAllBarber.addItem(c.getLastName() + " " + c.getName() + " - " + c.getIdBarber());
        });

        combolistAllBarber.setSelectedItem("");
        //Metodo para poder escribir y buscar en el JComboBox
        AutoCompleteDecorator.decorate(combolistAllBarber);
    }

    //Metodo para obtener el idClient
    private String[] getIdClientForListClient() {
        //Obtengo directamente el id del cliente de la lista.
        String[] client = (combolistAllClient.getSelectedItem().toString().split((" - ")));
        return client;
    }

    //Metodo para obtener el idBarber
    private String[] getIdBarberForListBarber() {
        //Obtengo directamente el id del cliente de la lista.
        String[] barber = (combolistAllBarber.getSelectedItem().toString().split((" - ")));
        return barber;
    }

    //______________________________________________________________________________________________________________
    //METODOS PARA COLOCAR IMAGENES A LOS BOTONES
    private void setImgBtnClient() {
        try {
            Image imgSearch = ImageIO.read(getClass().getResource("/img/logoClient.png"));
            Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnClient.getWidth(), btnClient.getHeight(), Image.SCALE_DEFAULT));
            btnClient.setIcon(iconSearch);
        } catch (IOException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setImgBtnBarber() {
        try {
            Image imgSearch = ImageIO.read(getClass().getResource("/img/logoBarber.png"));
            Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnBarber.getWidth(), btnBarber.getHeight(), Image.SCALE_DEFAULT));
            btnBarber.setIcon(iconSearch);
        } catch (IOException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setImgPanelLogoLupos() {
        try {
            Image imgSearch = ImageIO.read(getClass().getResource("/img/logoLupos.jpg"));
            Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(logoLuposBarber.getWidth(), logoLuposBarber.getHeight(), Image.SCALE_DEFAULT));
            logoLuposBarber.setIcon(iconSearch);
        } catch (IOException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setImgBtnCuts() {
        try {
            Image imgSearch = ImageIO.read(getClass().getResource("/img/logoCuts.jpg"));
            Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnCuts.getWidth(), btnCuts.getHeight(), Image.SCALE_DEFAULT));
            btnCuts.setIcon(iconSearch);
        } catch (IOException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //______________________________________________________________________________________________________________
    //______________________________________________________________________________________________________________
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        miLamina = new javax.swing.JPanel();
        imgLogo = new javax.swing.JLabel();
        btnCambiarTema1 = new javax.swing.JButton();
        btnCambiarTema2 = new javax.swing.JButton();
        miLamina2 = new javax.swing.JPanel();
        btnPromotora = new javax.swing.JButton();
        btnGaleria = new javax.swing.JButton();
        btnDetallePrestacion = new javax.swing.JButton();
        btnBeneficioMarca = new javax.swing.JButton();
        btnAntecedentes = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaCumpleanos = new javax.swing.JTable();
        jFrame2 = new javax.swing.JFrame();
        miLamina1 = new javax.swing.JPanel();
        imgLogo1 = new javax.swing.JLabel();
        btnCambiarTema3 = new javax.swing.JButton();
        btnCambiarTema4 = new javax.swing.JButton();
        miLamina3 = new javax.swing.JPanel();
        btnPromotora1 = new javax.swing.JButton();
        btnGaleria1 = new javax.swing.JButton();
        btnDetallePrestacion1 = new javax.swing.JButton();
        btnBeneficioMarca1 = new javax.swing.JButton();
        btnAntecedentes1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCumpleanos1 = new javax.swing.JTable();
        panelLogoLupos = new javax.swing.JPanel();
        logoLuposBarber = new javax.swing.JLabel();
        btnCambiarTema5 = new javax.swing.JButton();
        btnCambiarTema6 = new javax.swing.JButton();
        miLamina5 = new javax.swing.JPanel();
        btnBarber = new javax.swing.JButton();
        btnCuts = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnClient = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableBarberCuts = new javax.swing.JTable();
        combolistAllClient = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        combolistAllBarber = new javax.swing.JComboBox<>();
        btnConfirm = new javax.swing.JButton();
        checkCutAdult = new javax.swing.JCheckBox();
        checkCutBoy = new javax.swing.JCheckBox();
        checkCutBeard = new javax.swing.JCheckBox();
        checkCutDrawing = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        lblCutsBoy = new javax.swing.JLabel();
        lblCutsBeard = new javax.swing.JLabel();
        lblCutsDraw = new javax.swing.JLabel();
        lblEarningsTotal = new javax.swing.JLabel();
        lblCutsAdult = new javax.swing.JLabel();
        lblEarnings = new javax.swing.JLabel();

        jFrame1.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jFrame1.setBackground(new java.awt.Color(0, 0, 0));
        jFrame1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFrame1formKeyPressed(evt);
            }
        });

        miLamina.setBackground(new java.awt.Color(255, 255, 255));
        miLamina.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        imgLogo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(204, 204, 204), new java.awt.Color(0, 102, 51), new java.awt.Color(102, 102, 102), new java.awt.Color(153, 153, 153)));

        btnCambiarTema1.setBackground(new java.awt.Color(0, 153, 51));
        btnCambiarTema1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnCambiarTema1.setForeground(new java.awt.Color(255, 255, 255));
        btnCambiarTema1.setText("GUARDAR BASE DE DATOS");
        btnCambiarTema1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCambiarTema1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarTema1ActionPerformed(evt);
            }
        });

        btnCambiarTema2.setBackground(new java.awt.Color(0, 153, 51));
        btnCambiarTema2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnCambiarTema2.setForeground(new java.awt.Color(255, 255, 255));
        btnCambiarTema2.setText("CARGAR BASE DE DATAOS");
        btnCambiarTema2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCambiarTema2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarTema2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout miLaminaLayout = new javax.swing.GroupLayout(miLamina);
        miLamina.setLayout(miLaminaLayout);
        miLaminaLayout.setHorizontalGroup(
            miLaminaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miLaminaLayout.createSequentialGroup()
                .addComponent(imgLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(miLaminaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCambiarTema1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(btnCambiarTema2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addContainerGap())
        );
        miLaminaLayout.setVerticalGroup(
            miLaminaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miLaminaLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(imgLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miLaminaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCambiarTema1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCambiarTema2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        miLamina2.setBackground(new java.awt.Color(255, 255, 255));
        miLamina2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnPromotora.setBackground(new java.awt.Color(0, 102, 51));
        btnPromotora.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        btnPromotora.setForeground(new java.awt.Color(255, 255, 255));
        btnPromotora.setText("CLIENTES");
        btnPromotora.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPromotora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPromotoraActionPerformed(evt);
            }
        });

        btnGaleria.setBackground(new java.awt.Color(0, 102, 51));
        btnGaleria.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        btnGaleria.setForeground(new java.awt.Color(255, 255, 255));
        btnGaleria.setText("GALERIA");
        btnGaleria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGaleria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGaleriaActionPerformed(evt);
            }
        });

        btnDetallePrestacion.setBackground(new java.awt.Color(0, 102, 51));
        btnDetallePrestacion.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        btnDetallePrestacion.setForeground(new java.awt.Color(255, 255, 255));
        btnDetallePrestacion.setText("DETALLE DE PRESTACION");
        btnDetallePrestacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDetallePrestacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetallePrestacionActionPerformed(evt);
            }
        });

        btnBeneficioMarca.setBackground(new java.awt.Color(0, 102, 51));
        btnBeneficioMarca.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        btnBeneficioMarca.setForeground(new java.awt.Color(255, 255, 255));
        btnBeneficioMarca.setText("CONFIGURACIONES");
        btnBeneficioMarca.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBeneficioMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBeneficioMarcaActionPerformed(evt);
            }
        });

        btnAntecedentes.setBackground(new java.awt.Color(0, 102, 51));
        btnAntecedentes.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        btnAntecedentes.setForeground(new java.awt.Color(255, 255, 255));
        btnAntecedentes.setText("ANTECEDENTES");
        btnAntecedentes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAntecedentes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAntecedentesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout miLamina2Layout = new javax.swing.GroupLayout(miLamina2);
        miLamina2.setLayout(miLamina2Layout);
        miLamina2Layout.setHorizontalGroup(
            miLamina2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miLamina2Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(miLamina2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBeneficioMarca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDetallePrestacion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGaleria, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPromotora, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAntecedentes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(22, 22, 22))
        );
        miLamina2Layout.setVerticalGroup(
            miLamina2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miLamina2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPromotora, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGaleria, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAntecedentes, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDetallePrestacion, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBeneficioMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Arial", 3, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CUMPLEAÑOS");

        tablaCumpleanos.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tablaCumpleanos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tablaCumpleanos);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(119, 119, 119)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 545, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 732, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(48, 48, 48)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame1Layout.createSequentialGroup()
                .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(miLamina, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jFrame1Layout.createSequentialGroup()
                        .addComponent(miLamina2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame1Layout.createSequentialGroup()
                .addComponent(miLamina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(miLamina2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jFrame2.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jFrame2.setBackground(new java.awt.Color(0, 0, 0));
        jFrame2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFrame2formKeyPressed(evt);
            }
        });

        miLamina1.setBackground(new java.awt.Color(255, 255, 255));
        miLamina1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        imgLogo1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(204, 204, 204), new java.awt.Color(0, 102, 51), new java.awt.Color(102, 102, 102), new java.awt.Color(153, 153, 153)));

        btnCambiarTema3.setBackground(new java.awt.Color(0, 153, 51));
        btnCambiarTema3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnCambiarTema3.setForeground(new java.awt.Color(255, 255, 255));
        btnCambiarTema3.setText("GUARDAR BASE DE DATOS");
        btnCambiarTema3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCambiarTema3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarTema3ActionPerformed(evt);
            }
        });

        btnCambiarTema4.setBackground(new java.awt.Color(0, 153, 51));
        btnCambiarTema4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnCambiarTema4.setForeground(new java.awt.Color(255, 255, 255));
        btnCambiarTema4.setText("CARGAR BASE DE DATAOS");
        btnCambiarTema4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCambiarTema4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarTema4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout miLamina1Layout = new javax.swing.GroupLayout(miLamina1);
        miLamina1.setLayout(miLamina1Layout);
        miLamina1Layout.setHorizontalGroup(
            miLamina1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miLamina1Layout.createSequentialGroup()
                .addComponent(imgLogo1, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(miLamina1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCambiarTema3, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(btnCambiarTema4, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addContainerGap())
        );
        miLamina1Layout.setVerticalGroup(
            miLamina1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miLamina1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(imgLogo1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miLamina1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCambiarTema3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCambiarTema4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        miLamina3.setBackground(new java.awt.Color(255, 255, 255));
        miLamina3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnPromotora1.setBackground(new java.awt.Color(0, 102, 51));
        btnPromotora1.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        btnPromotora1.setForeground(new java.awt.Color(255, 255, 255));
        btnPromotora1.setText("CLIENTES");
        btnPromotora1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPromotora1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPromotora1ActionPerformed(evt);
            }
        });

        btnGaleria1.setBackground(new java.awt.Color(0, 102, 51));
        btnGaleria1.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        btnGaleria1.setForeground(new java.awt.Color(255, 255, 255));
        btnGaleria1.setText("GALERIA");
        btnGaleria1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGaleria1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGaleria1ActionPerformed(evt);
            }
        });

        btnDetallePrestacion1.setBackground(new java.awt.Color(0, 102, 51));
        btnDetallePrestacion1.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        btnDetallePrestacion1.setForeground(new java.awt.Color(255, 255, 255));
        btnDetallePrestacion1.setText("DETALLE DE PRESTACION");
        btnDetallePrestacion1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDetallePrestacion1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetallePrestacion1ActionPerformed(evt);
            }
        });

        btnBeneficioMarca1.setBackground(new java.awt.Color(0, 102, 51));
        btnBeneficioMarca1.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        btnBeneficioMarca1.setForeground(new java.awt.Color(255, 255, 255));
        btnBeneficioMarca1.setText("CONFIGURACIONES");
        btnBeneficioMarca1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBeneficioMarca1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBeneficioMarca1ActionPerformed(evt);
            }
        });

        btnAntecedentes1.setBackground(new java.awt.Color(0, 102, 51));
        btnAntecedentes1.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        btnAntecedentes1.setForeground(new java.awt.Color(255, 255, 255));
        btnAntecedentes1.setText("ANTECEDENTES");
        btnAntecedentes1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAntecedentes1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAntecedentes1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout miLamina3Layout = new javax.swing.GroupLayout(miLamina3);
        miLamina3.setLayout(miLamina3Layout);
        miLamina3Layout.setHorizontalGroup(
            miLamina3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miLamina3Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(miLamina3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBeneficioMarca1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDetallePrestacion1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGaleria1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPromotora1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAntecedentes1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(22, 22, 22))
        );
        miLamina3Layout.setVerticalGroup(
            miLamina3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miLamina3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPromotora1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGaleria1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAntecedentes1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDetallePrestacion1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBeneficioMarca1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Arial", 3, 36)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("CUMPLEAÑOS");

        tablaCumpleanos1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tablaCumpleanos1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tablaCumpleanos1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(119, 119, 119)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 545, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 732, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(48, 48, 48)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame2Layout.createSequentialGroup()
                .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(miLamina1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jFrame2Layout.createSequentialGroup()
                        .addComponent(miLamina3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jFrame2Layout.setVerticalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame2Layout.createSequentialGroup()
                .addComponent(miLamina1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(miLamina3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelLogoLupos.setBackground(new java.awt.Color(255, 255, 255));
        panelLogoLupos.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnCambiarTema5.setBackground(new java.awt.Color(0, 0, 0));
        btnCambiarTema5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnCambiarTema5.setForeground(new java.awt.Color(255, 255, 255));
        btnCambiarTema5.setText("GUARDAR BASE DE DATOS");
        btnCambiarTema5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCambiarTema5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarTema5ActionPerformed(evt);
            }
        });

        btnCambiarTema6.setBackground(new java.awt.Color(0, 0, 0));
        btnCambiarTema6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnCambiarTema6.setForeground(new java.awt.Color(255, 255, 255));
        btnCambiarTema6.setText("CARGAR BASE DE DATAOS");
        btnCambiarTema6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCambiarTema6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarTema6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLogoLuposLayout = new javax.swing.GroupLayout(panelLogoLupos);
        panelLogoLupos.setLayout(panelLogoLuposLayout);
        panelLogoLuposLayout.setHorizontalGroup(
            panelLogoLuposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLogoLuposLayout.createSequentialGroup()
                .addComponent(logoLuposBarber, javax.swing.GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLogoLuposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCambiarTema5, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(btnCambiarTema6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelLogoLuposLayout.setVerticalGroup(
            panelLogoLuposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoLuposBarber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLogoLuposLayout.createSequentialGroup()
                .addContainerGap(81, Short.MAX_VALUE)
                .addComponent(btnCambiarTema5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCambiarTema6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        miLamina5.setBackground(new java.awt.Color(255, 255, 255));
        miLamina5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnBarber.setBackground(new java.awt.Color(255, 255, 255));
        btnBarber.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        btnBarber.setForeground(new java.awt.Color(255, 255, 255));
        btnBarber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153), 4));
        btnBarber.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBarber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBarberActionPerformed(evt);
            }
        });

        btnCuts.setBackground(new java.awt.Color(255, 255, 255));
        btnCuts.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        btnCuts.setForeground(new java.awt.Color(255, 255, 255));
        btnCuts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153), 4));
        btnCuts.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCuts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCutsActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("BARBEROS");

        jLabel4.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("CLIENTES");

        jLabel5.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("CORTES");

        btnClient.setBackground(new java.awt.Color(255, 255, 255));
        btnClient.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153), 4));
        btnClient.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClientActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout miLamina5Layout = new javax.swing.GroupLayout(miLamina5);
        miLamina5.setLayout(miLamina5Layout);
        miLamina5Layout.setHorizontalGroup(
            miLamina5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(miLamina5Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(miLamina5Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(miLamina5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(miLamina5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnBarber, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(miLamina5Layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(24, 24, 24))
                        .addComponent(btnClient, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miLamina5Layout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(42, 42, 42)))
                    .addComponent(btnCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );
        miLamina5Layout.setVerticalGroup(
            miLamina5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, miLamina5Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(btnClient, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(btnBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(btnCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPanel3KeyTyped(evt);
            }
        });

        tableBarberCuts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(tableBarberCuts);

        combolistAllClient.setEditable(true);
        combolistAllClient.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        combolistAllClient.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel6.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("SELECCION DE CLIENTE");
        jLabel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));

        jLabel7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("SELECCION DE CORTES");
        jLabel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));

        jLabel8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("SELECCION DE BARBERO");
        jLabel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));

        combolistAllBarber.setEditable(true);
        combolistAllBarber.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        combolistAllBarber.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        combolistAllBarber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combolistAllBarberActionPerformed(evt);
            }
        });

        btnConfirm.setBackground(new java.awt.Color(0, 153, 204));
        btnConfirm.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        btnConfirm.setText("CONFIRMAR");
        btnConfirm.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        checkCutAdult.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        checkCutAdult.setText("Corte Adulto");
        checkCutAdult.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        checkCutBoy.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        checkCutBoy.setText("Corte Niño");
        checkCutBoy.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        checkCutBeard.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        checkCutBeard.setText("Barba");
        checkCutBeard.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        checkCutDrawing.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        checkCutDrawing.setText("Dibujo");
        checkCutDrawing.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButton1.setBackground(new java.awt.Color(0, 153, 204));
        jButton1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jButton1.setText("ACTUALZIAR");
        jButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lblCutsBoy.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblCutsBoy.setForeground(new java.awt.Color(204, 0, 0));
        lblCutsBoy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsBoy.setText("jLabel9");

        lblCutsBeard.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblCutsBeard.setForeground(new java.awt.Color(204, 0, 0));
        lblCutsBeard.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsBeard.setText("jLabel9");

        lblCutsDraw.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblCutsDraw.setForeground(new java.awt.Color(204, 0, 0));
        lblCutsDraw.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsDraw.setText("jLabel9");

        lblEarningsTotal.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblEarningsTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEarningsTotal.setText("jLabel9");
        lblEarningsTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0), 2));

        lblCutsAdult.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblCutsAdult.setForeground(new java.awt.Color(204, 0, 0));
        lblCutsAdult.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsAdult.setText("jLabel9");

        lblEarnings.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblEarnings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEarnings.setText("jLabel9");
        lblEarnings.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0), 2));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(lblCutsAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(lblCutsBoy, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(lblCutsBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblCutsDraw, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblEarningsTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                            .addComponent(lblEarnings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(combolistAllClient, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(combolistAllBarber, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(46, 46, 46)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(checkCutBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(checkCutAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(37, 37, 37)))
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(checkCutBoy, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(checkCutDrawing, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))))))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkCutBoy, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(checkCutAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkCutDrawing, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(checkCutBeard, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combolistAllBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(combolistAllClient, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEarnings, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEarningsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCutsBoy, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCutsBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCutsAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCutsDraw, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(miLamina5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelLogoLupos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelLogoLupos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(miLamina5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCambiarTema1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarTema1ActionPerformed

    }//GEN-LAST:event_btnCambiarTema1ActionPerformed

    private void btnCambiarTema2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarTema2ActionPerformed

    }//GEN-LAST:event_btnCambiarTema2ActionPerformed

    private void btnPromotoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPromotoraActionPerformed

    }//GEN-LAST:event_btnPromotoraActionPerformed

    private void btnGaleriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGaleriaActionPerformed

    }//GEN-LAST:event_btnGaleriaActionPerformed

    private void btnDetallePrestacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetallePrestacionActionPerformed

    }//GEN-LAST:event_btnDetallePrestacionActionPerformed

    private void btnBeneficioMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBeneficioMarcaActionPerformed

    }//GEN-LAST:event_btnBeneficioMarcaActionPerformed

    private void btnAntecedentesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAntecedentesActionPerformed

    }//GEN-LAST:event_btnAntecedentesActionPerformed

    private void jFrame1formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFrame1formKeyPressed

    }//GEN-LAST:event_jFrame1formKeyPressed

    private void btnCambiarTema3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarTema3ActionPerformed


    }//GEN-LAST:event_btnCambiarTema3ActionPerformed

    private void btnCambiarTema4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarTema4ActionPerformed

    }//GEN-LAST:event_btnCambiarTema4ActionPerformed

    private void btnPromotora1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPromotora1ActionPerformed

    }//GEN-LAST:event_btnPromotora1ActionPerformed

    private void btnGaleria1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGaleria1ActionPerformed

    }//GEN-LAST:event_btnGaleria1ActionPerformed

    private void btnDetallePrestacion1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetallePrestacion1ActionPerformed

    }//GEN-LAST:event_btnDetallePrestacion1ActionPerformed

    private void btnBeneficioMarca1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBeneficioMarca1ActionPerformed

    }//GEN-LAST:event_btnBeneficioMarca1ActionPerformed

    private void btnAntecedentes1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAntecedentes1ActionPerformed

    }//GEN-LAST:event_btnAntecedentes1ActionPerformed

    private void jFrame2formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFrame2formKeyPressed

    }//GEN-LAST:event_jFrame2formKeyPressed

    private void btnCambiarTema5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarTema5ActionPerformed

    }//GEN-LAST:event_btnCambiarTema5ActionPerformed

    private void btnCambiarTema6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarTema6ActionPerformed

    }//GEN-LAST:event_btnCambiarTema6ActionPerformed

    private void btnBarberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBarberActionPerformed
        MenuBarber mb = new MenuBarber(this, false);
        mb.setVisible(true);
    }//GEN-LAST:event_btnBarberActionPerformed

    private void btnCutsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCutsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCutsActionPerformed

    private void btnClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientActionPerformed
        MenuClient mc = new MenuClient(null, true);
        mc.setVisible(true);
    }//GEN-LAST:event_btnClientActionPerformed

    private void combolistAllBarberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combolistAllBarberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combolistAllBarberActionPerformed

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed

        if (!combolistAllClient.getSelectedItem().toString().isEmpty() && !combolistAllBarber.getSelectedItem().toString().isEmpty()) {
            boolean cutAdultSelected = checkCutAdult.isSelected();
            boolean cutBoySelected = checkCutBoy.isSelected();
            boolean cutBeardSelected = checkCutBeard.isSelected();
            boolean cutDrawingSelected = checkCutDrawing.isSelected();
            if (cutAdultSelected || cutBoySelected || cutBeardSelected || cutDrawingSelected) {
                saveCuts();
            } else {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un corte.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar el cliente/barbero.");
        }

    }//GEN-LAST:event_btnConfirmActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setearTableBarberCuts();
        this.fillTableBarberCuts();
        this.fillTableBarberHairCutsToday();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jPanel3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel3KeyTyped
        System.out.println(evt.getKeyChar());
    }//GEN-LAST:event_jPanel3KeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAntecedentes;
    private javax.swing.JButton btnAntecedentes1;
    private javax.swing.JButton btnBarber;
    private javax.swing.JButton btnBeneficioMarca;
    private javax.swing.JButton btnBeneficioMarca1;
    private javax.swing.JButton btnCambiarTema1;
    private javax.swing.JButton btnCambiarTema2;
    private javax.swing.JButton btnCambiarTema3;
    private javax.swing.JButton btnCambiarTema4;
    private javax.swing.JButton btnCambiarTema5;
    private javax.swing.JButton btnCambiarTema6;
    private javax.swing.JButton btnClient;
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnCuts;
    private javax.swing.JButton btnDetallePrestacion;
    private javax.swing.JButton btnDetallePrestacion1;
    private javax.swing.JButton btnGaleria;
    private javax.swing.JButton btnGaleria1;
    private javax.swing.JButton btnPromotora;
    private javax.swing.JButton btnPromotora1;
    private javax.swing.JCheckBox checkCutAdult;
    private javax.swing.JCheckBox checkCutBeard;
    private javax.swing.JCheckBox checkCutBoy;
    private javax.swing.JCheckBox checkCutDrawing;
    private javax.swing.JComboBox<String> combolistAllBarber;
    private javax.swing.JComboBox<String> combolistAllClient;
    private javax.swing.JLabel imgLogo;
    private javax.swing.JLabel imgLogo1;
    private javax.swing.JButton jButton1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCutsAdult;
    private javax.swing.JLabel lblCutsBeard;
    private javax.swing.JLabel lblCutsBoy;
    private javax.swing.JLabel lblCutsDraw;
    private javax.swing.JLabel lblEarnings;
    private javax.swing.JLabel lblEarningsTotal;
    private javax.swing.JLabel logoLuposBarber;
    private javax.swing.JPanel miLamina;
    private javax.swing.JPanel miLamina1;
    private javax.swing.JPanel miLamina2;
    private javax.swing.JPanel miLamina3;
    private javax.swing.JPanel miLamina5;
    private javax.swing.JPanel panelLogoLupos;
    private javax.swing.JTable tablaCumpleanos;
    private javax.swing.JTable tablaCumpleanos1;
    private javax.swing.JTable tableBarberCuts;
    // End of variables declaration//GEN-END:variables
}
