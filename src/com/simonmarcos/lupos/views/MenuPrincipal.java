package com.simonmarcos.lupos.views;

import com.simonmarcos.lupos.dao.DAOBarber;
import com.simonmarcos.lupos.dao.DAOClient;
import com.simonmarcos.lupos.dao.DAOCuts;
import com.simonmarcos.lupos.dao.DAOHairCut;
import com.simonmarcos.lupos.dao.DAOTotalCuts;
import com.simonmarcos.lupos.dao.impl.BarberDAOImpl;
import com.simonmarcos.lupos.dao.impl.ClientDAOImpl;
import com.simonmarcos.lupos.dao.impl.CutsDAOImpl;
import com.simonmarcos.lupos.dao.impl.HairCutDAOImpl;
import com.simonmarcos.lupos.dao.impl.TotalCutsDAOImpl;
import com.simonmarcos.lupos.model.Barber;
import com.simonmarcos.lupos.model.Client;
import com.simonmarcos.lupos.model.Cuts;
import com.simonmarcos.lupos.model.HairCut;
import com.simonmarcos.lupos.model.TotalCuts;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class MenuPrincipal extends javax.swing.JFrame {

    private DefaultTableModel dtm;
    private List<Barber> listAllBarber;
    private double prizeCuts = 0;
    private Map<Integer, String> mapIdBarbers = null;

    public MenuPrincipal() {
        initComponents();
        //this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Lupo's Barber");
        prizeCuts = new CutsDAOImpl().getPrize();
        this.dimensionWindows();
        this.setImgBtnClient();
        this.setImgBtnBarber();
        this.setImgBtnStadistics();
        this.setImgPanelLogoLupos();
        this.setearTableBarberCuts();
        this.fillTableBarberCuts();
        this.fillListAllClient();
        this.fillListAllBarber();
        this.fillTableBarberHairCutsToday();
        this.getDateToday();
        this.setearTittles();
        this.calculateBirthDay();

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                saveAllCutsOfDay();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
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
        checkCutBeard.setSelected(false);
        checkCutDrawing.setSelected(false);

        combolistAllClient.setSelectedIndex(0);
        combolistAllBarber.setSelectedIndex(0);
    }

    private void setearTittles() {
        lblClientSection.setToolTipText("Aquí podremos ver todos los clientes para luego seleccionar el buscado");
        lblBarberSection.setToolTipText("Aquí podremos ver todos los barberos para luego seleccionar el buscado");
        lblCutsSection.setToolTipText("Aquí podremos tildar el corte correspondiente");
        btnConfirm.setToolTipText("Botón para confirmar el corte realizado");
        btnUpdate.setToolTipText("Botón para actualizar la tabla de peluqueros y campos de cortes y ganancias");
        btnSaveTotalCuts.setToolTipText("Botón para guardar todos los cortes y las ganancias del día");
        lblEarnings.setToolTipText("Ganancias solamente que obtuvo el negocio en el día");
        lblEarningsTotal.setToolTipText("Ganancias total que obtuvo el negocio en el día");
        btnClient.setToolTipText("Menu con todas las acciones para los clientes");
        btnBarber.setToolTipText("Menu con todas las acciones para los barberos");
        btnCuts.setToolTipText("Menu con todas las acciones para las estadísticas del negocio");
    }

    private void calculateBirthDay() {
        listAllBarber.forEach(b -> {
            java.sql.Date nowDate = new java.sql.Date(new java.util.Date().getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.format(nowDate);
            if (b.getBirthday().toString().compareTo(nowDate.toString()) == 0) {
                JOptionPane.showMessageDialog(this, "Hoy es el cumpleaños de " + b.getLastName().toUpperCase() + " " + b.getName().toUpperCase() + ".");
            }
        });
    }

    //_________________________________________________________________________________________________________
    //METODO PARA GUARDAR TODOS LOS CORTES DEL DIA CON LAS GANANCIAS EN LA BASE DE DATOS
    //Este metodo me guardara todos los cortes diarios
    private void saveAllCutsOfDay() {
        DAOTotalCuts daoDelete = new TotalCutsDAOImpl();
        TotalCuts tc = new TotalCuts();
        //Obtenemos los datos de cada label
        tc.setCutsAdult(Integer.parseInt(lblCutsAdult.getText().split(": ")[1].trim()));
        tc.setCutsBeard(Integer.parseInt(lblCutsBeard.getText().split(": ")[1].trim()));
        tc.setCutsDrawing(Integer.parseInt(lblCutsDraw.getText().split(": ")[1].trim()));
        tc.setDate(new java.sql.Date(new java.util.Date().getTime()));
        tc.setEarningsTotal(Double.parseDouble(lblEarningsTotal.getText().split(": ")[1].trim()));
        tc.setEarningsLupos(Double.parseDouble(lblEarnings.getText().split(": ")[1].trim()));

        //Primero eliminamos los datos en la fecha actual si es que los hay
        int rDelete = daoDelete.deleteByDate(tc.getDate().toString());
        //Luego lo agregamos en la base de datos en el caso de que no se haya producido ningun error al eliminar
        if (rDelete != 3) {
            //Comprobamos que las ganancias sean mayor a 0 para recien guardar en la base de datos
            if (Double.parseDouble(lblEarningsTotal.getText().split(": ")[1].trim()) > 0) {
                DAOTotalCuts daoSave = new TotalCutsDAOImpl();
                int rSave = daoSave.save(tc);
                if (rSave == 1) {
                    //this.savePrizeBarberOfDay();
                    JOptionPane.showMessageDialog(null, "Los datos se guardaron correctamente.");
                }
            }
        }
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
            } else if (i == 1) {
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
            updateTableHairCutsBarber();
        }
    }

    //Metodo que me devolvera el precio total del corte segun los checks seleccionados.
    //Tambien extra, me devolvera los cortes que se realizo
    private String[] getPriceDependingCuts() {

        DAOCuts dao = new CutsDAOImpl();
        List<Cuts> listCuts = dao.toList();

        String cutsFinal = "";

        boolean cutAdultSelected = checkCutAdult.isSelected();
        boolean cutBeardSelected = checkCutBeard.isSelected();
        boolean cutDrawingSelected = checkCutDrawing.isSelected();

        double totalPrice = 0;
        double totalPriceBarber = 0;
        //Si solo esta seleccionado el check de CutsAdult
        if (cutAdultSelected && !cutBeardSelected && !cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutAdult.getText())) {
                    totalPrice = c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutAdult.getText();
        }//Si solo esta seleccionado el cutAdult y el cutBeard
        else if (cutAdultSelected && cutBeardSelected && !cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutAdult.getText()) || c.getType().equalsIgnoreCase(checkCutBeard.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutAdult.getText() + " / " + checkCutBeard.getText();
        } //Si solo esta seleccionado el cutAdult y el cutDrawing 
        else if (cutAdultSelected && !cutBeardSelected && cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutAdult.getText()) || c.getType().equalsIgnoreCase(checkCutDrawing.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutAdult.getText() + " / " + checkCutDrawing.getText();
        } //Si solo esta seleccionado el cutAdult y el cutDrawing y cutBeard
        else if (cutAdultSelected && cutBeardSelected && cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutAdult.getText()) || c.getType().equalsIgnoreCase(checkCutBeard.getText()) || c.getType().equalsIgnoreCase(checkCutDrawing.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutAdult.getText() + " / " + checkCutBeard.getText() + " / " + checkCutDrawing.getText();

        }//Si solo esta seleccionado el  checkDrawing
        else if (!cutAdultSelected && !cutBeardSelected && cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutDrawing.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutDrawing.getText();
        } //Si solo esta seleccionado el checkBeard
        else if (!cutAdultSelected && cutBeardSelected && !cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutBeard.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutBeard.getText();
        } //Si esta seleccionado el checkBeard y checkDrawing
        else if (!cutAdultSelected && cutBeardSelected && cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutBeard.getText()) || c.getType().equalsIgnoreCase(checkCutDrawing.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutBeard.getText() + " / " + checkCutDrawing.getText();
        }//En el caso de que todos los check esten seleccionados
        else if (cutAdultSelected && cutBeardSelected && cutDrawingSelected) {
            for (Cuts c : listCuts) {
                if (c.getType().equalsIgnoreCase(checkCutDrawing.getText()) || c.getType().equalsIgnoreCase(checkCutAdult.getText()) || c.getType().equalsIgnoreCase(checkCutBeard.getText())) {
                    totalPrice += c.getPrice();
                    totalPriceBarber += c.getPriceBarber();
                }
            }
            cutsFinal += checkCutAdult.getText() + " / " + checkCutBeard.getText() + " / " + checkCutDrawing.getText();
        }
        String[] stringReturn = {String.valueOf(totalPrice), String.valueOf(totalPriceBarber), cutsFinal};
        return stringReturn;
    }

    //__________________________________________________________________________________________________________
    //METODOS PARA MANIPULAR LA TABLA DE LOS CORTES
    private void setearTableBarberCuts() {
        dtm = new DefaultTableModel();
        String[] columns = {"Barbero", "Cortes", "Barbas", "Dibujos", "Ganancias"};
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

        //Codigo para especificar el tamaño de las celdas
        tableBarberCuts.setRowHeight(30);
        //Codigo para no poder escribir las celdas
        tableBarberCuts.setDefaultEditor(Object.class, null);
    }

    private void fillTableBarberCuts() {
        String[] fila = new String[5];
        listAllBarber = this.getAllBarber();
        listAllBarber.stream().sorted().forEach(b -> {
            mapIdBarbers = new HashMap();
            mapIdBarbers.put(b.getIdBarber(), b.getLastName() + " " + b.getName());
            fila[0] = b.getLastName() + " " + b.getName();
            fila[1] = "0";
            fila[2] = "0";
            fila[3] = "0";
            fila[4] = "0";
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

        double earningsBoss = 0;

        //Recorremos los datos consultados de la base de datos
        for (HairCut hc : listHairCut) {
            //Accion para sumar todas las ganancias diarias
            earningsBoss += hc.getPrice();

            //Recorremos las filas de la tabla
            for (int i = 0; i < row; i++) {
                String cutsAdult = tableBarberCuts.getValueAt(i, 1).toString();
                String cutsBeard = tableBarberCuts.getValueAt(i, 2).toString();
                String cutsDrawing = tableBarberCuts.getValueAt(i, 3).toString();
                String priceCuts = tableBarberCuts.getValueAt(i, 4).toString();

                int countCutsAdult = 0;
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
                        if (nameCuts.contains("Corte")) {
                            countCutsAdult = Integer.parseInt(cutsAdult) + 1;
                            tableBarberCuts.setValueAt(String.valueOf(countCutsAdult), i, 1);
                        } else if (nameCuts.contains("Barba")) {
                            countCutsBeard = Integer.parseInt(cutsBeard) + 1;
                            tableBarberCuts.setValueAt(String.valueOf(countCutsBeard), i, 2);
                        } else if (nameCuts.contains("Dibujo")) {
                            countCutsDrawing = Integer.parseInt(cutsDrawing) + 1;
                            tableBarberCuts.setValueAt(String.valueOf(countCutsDrawing), i, 3);
                        }
                        totalPriceCuts = Double.valueOf(priceCuts);
                        totalPriceCuts += hc.getPriceBarber();
                        tableBarberCuts.setValueAt(String.valueOf(totalPriceCuts), i, 4);
                    }
                }
            }
        }
        this.checkCutsHigher10();
        this.getAllCutsAndEarningTheDay(earningsBoss);
    }

    //Metodo para verificar si en cortes va 10 y darle el premio
    private void checkCutsHigher10() {
        int row = tableBarberCuts.getRowCount();
        for (int i = 0; i < row; i++) {
            int countCutsAdult = Integer.parseInt(tableBarberCuts.getValueAt(i, 1).toString());
            double totalEarningsBarber = Double.valueOf(tableBarberCuts.getValueAt(i, 4).toString());
            int countPrize = (countCutsAdult / 10) * 10;
            totalEarningsBarber += countPrize;

            tableBarberCuts.setValueAt(String.valueOf(totalEarningsBarber), i, 4);
        }
    }

    //Metodo para guardar el premio de cada barbero en la base de datos
    private void savePrizeBarberOfDay() {
        int row = tableBarberCuts.getRowCount();
        String dateToday = new SimpleDateFormat("yyyy/MM/dd").format(new java.sql.Timestamp(System.currentTimeMillis()));
        //Primero eliminamos este dato guardado en el caso de que ya lo hayamos guardado anteriormente 
        DAOHairCut daoDelete = new HairCutDAOImpl();
        int rDelete = daoDelete.deleteByDate(dateToday);
        if (rDelete != 3) {
            for (int i = 0; i < row; i++) {
                //Obtenemos los campos necesarios de la tabla que vamos a guardar en la base de datos por cada barbero
                String nameBarber = tableBarberCuts.getValueAt(i, 0).toString();
                int countCutsAdult = Integer.parseInt(tableBarberCuts.getValueAt(i, 1).toString());
                int idBarber = 0;
                for (Map.Entry<Integer, String> entry : mapIdBarbers.entrySet()) {
                    if (nameBarber.equalsIgnoreCase(entry.getValue())) {
                        idBarber = entry.getKey();
                    }
                }
                double totalEarningsBarber = Double.valueOf(tableBarberCuts.getValueAt(i, 4).toString());
                int countPrize = (countCutsAdult / 10) * 10;

                HairCut hc = new HairCut();

                Barber b = new Barber();
                b.setIdBarber(idBarber);
                hc.setBarber(b);

                hc.setDate(new java.sql.Timestamp(System.currentTimeMillis()));
                hc.setPriceBarber(countPrize);
                //Lo guardamos en la base de datoss
                DAOHairCut daoSave = new HairCutDAOImpl();
                daoSave.savePrizeBarber(hc);
            }
        }

    }

    private void updateTableHairCutsBarber() {
        this.setearTableBarberCuts();
        this.fillTableBarberCuts();
        this.fillTableBarberHairCutsToday();
    }

    private void getAllCutsAndEarningTheDay(double earnings) {

        int row = tableBarberCuts.getRowCount();

        int countHairCutsAdult = 0;
        int countHairCutsBeader = 0;
        int countHairCutsDrawing = 0;
        double countEarnings = 0;

        for (int i = 0; i < row; i++) {
            countHairCutsAdult += Integer.parseInt(tableBarberCuts.getValueAt(i, 1).toString());
            countHairCutsBeader += Integer.parseInt(tableBarberCuts.getValueAt(i, 2).toString());
            countHairCutsDrawing += Integer.parseInt(tableBarberCuts.getValueAt(i, 3).toString());
            countEarnings += Double.parseDouble(tableBarberCuts.getValueAt(i, 4).toString());
        }

        lblCutsAdult.setText("Cortes Adultos: " + countHairCutsAdult);
        lblCutsBeard.setText("Barba: " + countHairCutsBeader);
        lblCutsDraw.setText("Dibujos: " + countHairCutsDrawing);
        lblEarningsTotal.setText("Ganancias Total: " + earnings);
        lblEarnings.setText("Ganancias: " + (earnings - countEarnings));
    }

    //______________________________________________________________________________________________________________
    //METODOS PARA MANIPULAR LOS JCOMBOBOX
    //Metodo que me rellenara el JComboBox de clientes con el nombre y codigo del cliente
    private void fillListAllClient() {
        combolistAllClient.removeAllItems();
        DAOClient dao = new ClientDAOImpl();
        dao.toList().stream().sorted().forEach(c -> {
            java.sql.Date nowDate = new java.sql.Date(new java.util.Date().getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.format(nowDate);
            if (c.getBirthday().toString().compareTo(nowDate.toString()) == 0 && c.getBirthday() != null) {
                combolistAllClient.addItem(c.getLastName() + " " + c.getName() + " - " + c.getIdClient() + " - CUMPLE");
            } else {
                combolistAllClient.addItem(c.getLastName() + " " + c.getName() + " - " + c.getIdClient());
            }
        });

        combolistAllClient.setSelectedItem("");
        AutoCompleteDecorator.decorate(combolistAllClient);
    }
    //Metodo que me rellenara el JComboBox de barberos con el nombre y codigo del barbero

    private void fillListAllBarber() {
        listAllBarber = this.getAllBarber();
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

        Image imgSearch = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/logoClient.png"));
        Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnClient.getWidth(), btnClient.getHeight(), Image.SCALE_DEFAULT));
        btnClient.setIcon(iconSearch);

    }

    private void setImgBtnBarber() {

        Image imgSearch = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/logoBarber.png"));
        Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnBarber.getWidth(), btnBarber.getHeight(), Image.SCALE_DEFAULT));
        btnBarber.setIcon(iconSearch);

    }

    private void setImgPanelLogoLupos() {

        Image imgSearch = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/logoLupos.jpg"));
        Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(logoLuposBarber.getWidth(), logoLuposBarber.getHeight(), Image.SCALE_DEFAULT));
        logoLuposBarber.setIcon(iconSearch);

    }

    private void setImgBtnStadistics() {

        Image imgSearch = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/statistics.png"));
        Icon iconSearch = new ImageIcon(imgSearch.getScaledInstance(btnCuts.getWidth(), btnCuts.getHeight(), Image.SCALE_DEFAULT));
        btnCuts.setIcon(iconSearch);

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
        jButton1 = new javax.swing.JButton();
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
        lblClientSection = new javax.swing.JLabel();
        lblCutsSection = new javax.swing.JLabel();
        lblBarberSection = new javax.swing.JLabel();
        combolistAllBarber = new javax.swing.JComboBox<>();
        btnConfirm = new javax.swing.JButton();
        checkCutAdult = new javax.swing.JCheckBox();
        checkCutBeard = new javax.swing.JCheckBox();
        checkCutDrawing = new javax.swing.JCheckBox();
        btnSaveTotalCuts = new javax.swing.JButton();
        lblCutsBeard = new javax.swing.JLabel();
        lblCutsDraw = new javax.swing.JLabel();
        lblEarningsTotal = new javax.swing.JLabel();
        lblCutsAdult = new javax.swing.JLabel();
        lblEarnings = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();

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

        jButton1.setBackground(new java.awt.Color(0, 153, 204));
        jButton1.setFont(new java.awt.Font("Arial Unicode MS", 3, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("CORTES");
        jButton1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLogoLuposLayout = new javax.swing.GroupLayout(panelLogoLupos);
        panelLogoLupos.setLayout(panelLogoLuposLayout);
        panelLogoLuposLayout.setHorizontalGroup(
            panelLogoLuposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLogoLuposLayout.createSequentialGroup()
                .addComponent(logoLuposBarber, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
                .addGap(50, 50, 50)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
        );
        panelLogoLuposLayout.setVerticalGroup(
            panelLogoLuposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoLuposBarber, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLogoLuposLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        jLabel5.setText("ESTADISTICAS");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

        lblClientSection.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblClientSection.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblClientSection.setText("SELECCION DE CLIENTE");
        lblClientSection.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));

        lblCutsSection.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblCutsSection.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsSection.setText("SELECCION DE CORTES");
        lblCutsSection.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));

        lblBarberSection.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblBarberSection.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBarberSection.setText("SELECCION DE BARBERO");
        lblBarberSection.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));

        combolistAllBarber.setEditable(true);
        combolistAllBarber.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        combolistAllBarber.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        combolistAllBarber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combolistAllBarberActionPerformed(evt);
            }
        });

        btnConfirm.setBackground(new java.awt.Color(153, 153, 153));
        btnConfirm.setFont(new java.awt.Font("Arial Unicode MS", 3, 22)); // NOI18N
        btnConfirm.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirm.setText("CONFIRMAR");
        btnConfirm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        checkCutAdult.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        checkCutAdult.setText("Corte");
        checkCutAdult.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        checkCutBeard.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        checkCutBeard.setText("Barba");
        checkCutBeard.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        checkCutDrawing.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        checkCutDrawing.setText("Dibujo");
        checkCutDrawing.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnSaveTotalCuts.setBackground(new java.awt.Color(0, 153, 204));
        btnSaveTotalCuts.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        btnSaveTotalCuts.setForeground(new java.awt.Color(255, 255, 255));
        btnSaveTotalCuts.setText("GUARDAR");
        btnSaveTotalCuts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnSaveTotalCuts.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSaveTotalCuts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveTotalCutsActionPerformed(evt);
            }
        });

        lblCutsBeard.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblCutsBeard.setForeground(new java.awt.Color(204, 0, 0));
        lblCutsBeard.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsBeard.setText("jLabel9");

        lblCutsDraw.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblCutsDraw.setForeground(new java.awt.Color(204, 0, 0));
        lblCutsDraw.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsDraw.setText("jLabel9");

        lblEarningsTotal.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblEarningsTotal.setForeground(new java.awt.Color(204, 0, 0));
        lblEarningsTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEarningsTotal.setText("jLabel9");
        lblEarningsTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0), 2));

        lblCutsAdult.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblCutsAdult.setForeground(new java.awt.Color(204, 0, 0));
        lblCutsAdult.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsAdult.setText("jLabel9");

        lblEarnings.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblEarnings.setForeground(new java.awt.Color(204, 0, 0));
        lblEarnings.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEarnings.setText("jLabel9");
        lblEarnings.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0), 2));

        btnUpdate.setBackground(new java.awt.Color(0, 153, 204));
        btnUpdate.setFont(new java.awt.Font("Arial Unicode MS", 3, 18)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("ACTUALIZAR");
        btnUpdate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(combolistAllClient, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblClientSection, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(combolistAllBarber, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblBarberSection, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(68, 68, 68)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(lblCutsSection, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                                .addComponent(checkCutBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(checkCutDrawing, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(checkCutAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(107, 107, 107))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnSaveTotalCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(124, 124, 124))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lblCutsAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(lblCutsBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblCutsDraw, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblEarnings, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                            .addComponent(lblEarningsTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblCutsSection, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkCutAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkCutDrawing, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(checkCutBeard, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(lblBarberSection, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(combolistAllBarber, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblClientSection, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnConfirm, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(combolistAllClient, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCutsBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCutsAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCutsDraw, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEarnings, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSaveTotalCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEarningsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))))
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

    private void btnBarberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBarberActionPerformed
        MenuBarber mb = new MenuBarber(this, false);
        mb.setVisible(true);
//        while (true) {
//            String pass = JOptionPane.showInputDialog(this, "Ingrese la contraseña para ingresar");
//            if (pass != null) {
//                if (pass.equals("lupos")) {
//                    MenuBarber mb = new MenuBarber(this, false);
//                    mb.setVisible(true);
//                    break;
//                } else {
//                    JOptionPane.showMessageDialog(this, "Contraseña incorrecta, intente nuevamente.");
//                }
//            } else {
//                break;
//            }
//        }
    }//GEN-LAST:event_btnBarberActionPerformed

    private void btnCutsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCutsActionPerformed

        while (true) {
            String pass = JOptionPane.showInputDialog(this, "Ingrese la contraseña para ingresar");
            if (pass != null) {
                if (pass.equals("lupos")) {
                    MenuStatistics mc = new MenuStatistics(this, false);
                    mc.setVisible(true);
                    break;
                } else {
                    JOptionPane.showMessageDialog(this, "Contraseña incorrecta, intente nuevamente.");
                }
            } else {
                break;
            }
        }
    }//GEN-LAST:event_btnCutsActionPerformed

    private void btnClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientActionPerformed
        MenuClient mc = new MenuClient(null, true);
        mc.setVisible(true);
//        while (true) {
//            String pass = JOptionPane.showInputDialog(this, "Ingrese la contraseña para ingresar");
//            if (pass != null) {
//                if (pass.equals("lupos")) {
//                    MenuClient mc = new MenuClient(null, true);
//                    mc.setVisible(true);
//                    break;
//                } else {
//                    JOptionPane.showMessageDialog(this, "Contraseña incorrecta, intente nuevamente.");
//                }
//            } else {
//                break;
//            }
//        }
    }//GEN-LAST:event_btnClientActionPerformed

    private void combolistAllBarberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combolistAllBarberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combolistAllBarberActionPerformed

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed

        if (!combolistAllClient.getSelectedItem().toString().isEmpty() && !combolistAllBarber.getSelectedItem().toString().isEmpty()) {
            boolean cutAdultSelected = checkCutAdult.isSelected();
            boolean cutBeardSelected = checkCutBeard.isSelected();
            boolean cutDrawingSelected = checkCutDrawing.isSelected();
            if (cutAdultSelected || cutBeardSelected || cutDrawingSelected) {
                saveCuts();
                this.updateTableHairCutsBarber();
            } else {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un corte.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar el cliente/barbero.");
        }

    }//GEN-LAST:event_btnConfirmActionPerformed

    private void btnSaveTotalCutsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveTotalCutsActionPerformed
        saveAllCutsOfDay();
    }//GEN-LAST:event_btnSaveTotalCutsActionPerformed

    private void jPanel3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel3KeyTyped

    }//GEN-LAST:event_jPanel3KeyTyped

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        this.fillListAllBarber();
        this.fillListAllClient();
        this.updateTableHairCutsBarber();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        MenuCuts mc = new MenuCuts(this, false);
        mc.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

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
    private javax.swing.JButton btnClient;
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnCuts;
    private javax.swing.JButton btnDetallePrestacion;
    private javax.swing.JButton btnDetallePrestacion1;
    private javax.swing.JButton btnGaleria;
    private javax.swing.JButton btnGaleria1;
    private javax.swing.JButton btnPromotora;
    private javax.swing.JButton btnPromotora1;
    private javax.swing.JButton btnSaveTotalCuts;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JCheckBox checkCutAdult;
    private javax.swing.JCheckBox checkCutBeard;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblBarberSection;
    private javax.swing.JLabel lblClientSection;
    private javax.swing.JLabel lblCutsAdult;
    private javax.swing.JLabel lblCutsBeard;
    private javax.swing.JLabel lblCutsDraw;
    private javax.swing.JLabel lblCutsSection;
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
