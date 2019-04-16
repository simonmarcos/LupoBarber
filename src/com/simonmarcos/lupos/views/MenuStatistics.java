package com.simonmarcos.lupos.views;

import com.simonmarcos.lupos.dao.DAO;
import com.simonmarcos.lupos.dao.DAOExpenses;
import com.simonmarcos.lupos.dao.DAOTotalCuts;
import com.simonmarcos.lupos.dao.impl.ExpensesDAOImpl;
import com.simonmarcos.lupos.dao.impl.TotalCutsDAOImpl;
import com.simonmarcos.lupos.model.Expenses;
import com.simonmarcos.lupos.model.TotalCuts;
import com.sun.glass.events.KeyEvent;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class MenuStatistics extends javax.swing.JDialog {

    private String[] listMonth = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    private Map<Integer, Integer> mapCutsAdult = null;
    private Map<Integer, Integer> mapCutsBoy = null;
    private Map<Integer, Integer> mapCutsBeard = null;
    private Map<Integer, Integer> mapCutsDrawing = null;
    private Map<Integer, Double> mapMonthAndEarningsTotal = null;
    private Map<Integer, Double> mapMonthAndEarningsLupos = null;

    private DefaultTableModel dtm = null;

    private List<Expenses> listExpenses = new ArrayList<>();
    private List<Expenses> listExpensesFinal = null;
    private String[] arrayExpensesPersonal = {"Auto", "Boliche", "Comida", "Nafta", "Otros"};
    private String[] arrayExpensesBarberia = {"Alquiler", "Arreglos", "Internet", "Insumos", "Luz", "Otros"};
    private String[] arrayExpensesEmpleados = {"Sueldo", "Otros"};

    public MenuStatistics(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setFocusable(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Menu Estadísticas");
        this.setDateChooser();
        this.setearField();
        this.setearTableListExpenses();

        //Codigo para cuando hacemos click en cada pestaña
        pestanas.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                switch (pestanas.getSelectedIndex()) {
                    case 0:
                        setearFielExpenses();
                        break;
                    case 1:
                        setearTableListExpenses();
                        getAllExpenses();
                        fillTableListExpenses(listExpenses);
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }
        });

        //Codigo para cuando hacemos click en la lista de la categorias de gastos
        listCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listCategory.getSelectedItem().toString().equalsIgnoreCase("Personal")) {
                    listTypeExpenses.setEnabled(true);
                    setearListTypeExpenses(arrayExpensesPersonal);
                } else if (listCategory.getSelectedItem().toString().equalsIgnoreCase("Barbería")) {
                    listTypeExpenses.setEnabled(true);
                    setearListTypeExpenses(arrayExpensesBarberia);
                } else {
                    listTypeExpenses.setEnabled(true);
                    setearListTypeExpenses(arrayExpensesEmpleados);
                }
            }
        });
    }

    private void setDateChooser() {
        dateSince.setDate(new Date());
        dateUntil.setDate(new Date());
        dateExpenses.setDate(new Date());
        dateSinceEarningsTotal.setDate(new Date());
        dateUntilEarningsTotal.setDate(new Date());

        dateSince.setDateFormatString("dd-MM-yyyy");
        JTextFieldDateEditor editorDateSince = (JTextFieldDateEditor) dateSince.getDateEditor();
        editorDateSince.setEditable(false);
        editorDateSince.setHorizontalAlignment(JTextField.CENTER);

        dateUntil.setDateFormatString("dd-MM-yyyy");
        JTextFieldDateEditor editorDateUntil = (JTextFieldDateEditor) dateUntil.getDateEditor();
        editorDateUntil.setEditable(false);
        editorDateUntil.setHorizontalAlignment(JTextField.CENTER);

        dateSinceEarningsTotal.setDateFormatString("dd-MM-yyyy");
        JTextFieldDateEditor editorDateSinceEarningsTotal = (JTextFieldDateEditor) dateSinceEarningsTotal.getDateEditor();
        editorDateSinceEarningsTotal.setEditable(false);
        editorDateSinceEarningsTotal.setHorizontalAlignment(JTextField.CENTER);

        dateUntilEarningsTotal.setDateFormatString("dd-MM-yyyy");
        JTextFieldDateEditor editorDateUntilEarningsTotal = (JTextFieldDateEditor) dateUntilEarningsTotal.getDateEditor();
        editorDateUntilEarningsTotal.setEditable(false);
        editorDateUntilEarningsTotal.setHorizontalAlignment(JTextField.CENTER);

        dateExpenses.setDateFormatString("dd-MM-yyyy");
        JTextFieldDateEditor editorDateExpenses = (JTextFieldDateEditor) dateExpenses.getDateEditor();
        editorDateExpenses.setEditable(false);
        editorDateExpenses.setHorizontalAlignment(JTextField.CENTER);

        dateSinceExpenses.setDateFormatString("dd-MM-yyyy");
        JTextFieldDateEditor editorDateExpensesSince = (JTextFieldDateEditor) dateSinceExpenses.getDateEditor();
        editorDateExpensesSince.setEditable(false);
        editorDateExpensesSince.setHorizontalAlignment(JTextField.CENTER);

        dateUntilExpenses.setDateFormatString("dd-MM-yyyy");
        JTextFieldDateEditor editorDateExpensesUntil = (JTextFieldDateEditor) dateUntilExpenses.getDateEditor();
        editorDateExpensesUntil.setEditable(false);
        editorDateExpensesUntil.setHorizontalAlignment(JTextField.CENTER);
    }

    private void setearField() {
        lblCutsAdult.setText("");
        lblCutsBeard.setText("");
        lblCutsDrawing.setText("");
        lblEarningsLupos.setText("");
        lblEarningsTotal.setText("");
        lblStatictisDriagram.setToolTipText("Esta sección nos permitirá generar un reporte y un diagrama con información correspondiente al año especificado");
        lblReport.setToolTipText("Esta sección nos permitirá generar un reporte con información correspondiente a las fechas 'Desde' y 'Hasta' ");
        dateSince.setToolTipText("Ingrese la fecha 'Desde'");
        dateUntil.setToolTipText("Ingrese la fecha 'Hasta'");
        btnSearch.setToolTipText("Botón para generar reporte.");
        rbSearchReport.setToolTipText("Confirmar búsqueda");
        rbSearchDiagram.setToolTipText("Confirmar búsqueda");
        btnOpenGraphics.setToolTipText("Botón para generar diagrama gráfico");
    }

    private void setearListTypeExpenses(String[] arrays) {
        listTypeExpenses.removeAllItems();
        for (int i = 0; i < arrays.length; i++) {
            listTypeExpenses.addItem(arrays[i]);
        }
    }

    private void setearFielExpenses() {
        textAreaDescription.setText("");
        txtValueExpenses.setText("");
    }

    //Metodo que me devolvera el nombre del mes segun el numero
    private String getStringMonth(String month) {
        switch (month) {
            case "1":
                return "Enero";
            case "2":
                return "Febrero";
            case "3":
                return "Marzo";
            case "4":
                return "Abril";
            case "5":
                return "Mayo";
            case "6":
                return "Junio";
            case "7":
                return "Julio";
            case "8":
                return "Agosto";
            case "9":
                return "Septiembre";
            case "10":
                return "Octubre";
            case "11":
                return "Noviembre";
            case "12":
                return "Diciembre";

        }
        return "";
    }

    //Metodo que me devolvera el nombre del mes segun el numero
    private int getIntMonth(String month) {
        switch (month) {
            case "Enero":
                return 1;
            case "Febrero":
                return 2;
            case "Marzo":
                return 3;
            case "Abril":
                return 4;
            case "Mayo":
                return 5;
            case "Junio":
                return 6;
            case "Julio":
                return 7;
            case "Agosto":
                return 8;
            case "Septiembre":
                return 9;
            case "Octubre":
                return 10;
            case "Noviembre":
                return 11;
            case "Diciembre":
                return 12;

        }
        return 0;
    }

    //Metodo que me obtendra un map con el mes y las ganancias de ese mes en el año especificado
    private void setAllMaps() {
        String year = jListYear.getSelectedItem().toString();

        //Inicializamos todas las variables que me guardaran todos los datos obtenidos de la base de datos
        mapCutsAdult = new HashMap<>();
        mapCutsBoy = new HashMap<>();
        mapCutsBeard = new HashMap<>();
        mapCutsDrawing = new HashMap<>();
        mapMonthAndEarningsLupos = new HashMap<>();
        mapMonthAndEarningsTotal = new HashMap<>();

        //DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (int i = 0; i < listMonth.length; i++) {
            int cutsAdult = 0;
            int cutsBoy = 0;
            int cutsBeard = 0;
            int cutsDrawing = 0;
            double earningsLupos = 0;
            double earningsTotal = 0;
            //Obtenemos de la base de datos una lista con todos los registros de el año especificado y el mes
            List<TotalCuts> listTC = new TotalCutsDAOImpl().queryGetByMonthAndYear(i + 1, Integer.parseInt(year));
            //Recorremos los datos obtenidos de ese mes y lo asignamos a las variables
            for (TotalCuts tc : listTC) {
                cutsAdult += tc.getCutsAdult();
                cutsBoy += tc.getCutsBoy();
                cutsBeard += tc.getCutsBeard();
                cutsDrawing += tc.getCutsDrawing();
                earningsLupos += tc.getEarningsLupos();
                earningsTotal += tc.getEarningsTotal();
            }
            mapCutsAdult.put(i + 1, cutsAdult);
            mapCutsBoy.put(i + 1, cutsBoy);
            mapCutsBeard.put(i + 1, cutsBeard);
            mapCutsDrawing.put(i + 1, cutsDrawing);
            mapMonthAndEarningsLupos.put(i + 1, earningsLupos);
            mapMonthAndEarningsTotal.put(i + 1, earningsTotal);
        }
    }

    private void generateGraphics() {
        DefaultCategoryDataset dc = new DefaultCategoryDataset();
        JFreeChart jf = null;
        //Dependiendo el radio button seleccionado recorremos el map y creamos la grafica
        if (rbEarningsLupos.isSelected()) {
            for (Map.Entry<Integer, Double> e : mapMonthAndEarningsLupos.entrySet()) {
                dc.setValue(e.getValue(), getStringMonth(e.getKey().toString()), "");
            }
            jf = ChartFactory.createBarChart3D("Diagrama Ganancias Lupos", "Mes", "Ganancia", dc, PlotOrientation.VERTICAL, true, true, true);
        } else if (rbEarningsTotal.isSelected()) {
            for (Map.Entry<Integer, Double> e : mapMonthAndEarningsTotal.entrySet()) {
                dc.setValue(e.getValue(), getStringMonth(e.getKey().toString()), "");
            }
            jf = ChartFactory.createBarChart3D("Diagrama Ganancias Total", "Mes", "Ganancia", dc, PlotOrientation.VERTICAL, true, true, true);
        } else if (rbCutsAdult.isSelected()) {
            for (Map.Entry<Integer, Integer> e : mapCutsAdult.entrySet()) {
                dc.setValue(e.getValue(), getStringMonth(e.getKey().toString()), "");
            }
            jf = ChartFactory.createBarChart3D("Diagrama Cortes", "Mes", "Cortes", dc, PlotOrientation.VERTICAL, true, true, true);
        } else if (rbCutsBeard.isSelected()) {
            for (Map.Entry<Integer, Integer> e : mapCutsBeard.entrySet()) {
                dc.setValue(e.getValue(), getStringMonth(e.getKey().toString()), "");
            }
            jf = ChartFactory.createBarChart3D("Diagrama de Barbas", "Mes", "Cortes", dc, PlotOrientation.VERTICAL, true, true, true);
        } else if (rbCutsDrawing.isSelected()) {
            for (Map.Entry<Integer, Integer> e : mapCutsDrawing.entrySet()) {
                dc.setValue(e.getValue(), getStringMonth(e.getKey().toString()), "");
            }
            jf = ChartFactory.createBarChart3D("Diagrama de Dibujos", "Mes", "Cortes", dc, PlotOrientation.VERTICAL, true, true, true);
        }
        ChartFrame cf = new ChartFrame("", jf);
        cf.setSize(1000, 600);
        cf.setLocationRelativeTo(null);
        cf.setResizable(false);
        cf.setVisible(true);
    }

    private void generateReport() {
        int cutsAdult = 0;
        int cutsBoy = 0;
        int cutsBeard = 0;
        int cutsDrawing = 0;
        double earningsTotal = 0;
        double earningsLupos = 0;
        //Dependiendo el radio button seleccionado recorremos el map y lo ponemos en los label

        for (Map.Entry<Integer, Integer> e : mapCutsAdult.entrySet()) {
            cutsAdult += e.getValue();
        }

        for (Map.Entry<Integer, Integer> e : mapCutsBoy.entrySet()) {
            cutsBoy += e.getValue();
        }

        for (Map.Entry<Integer, Integer> e : mapCutsBeard.entrySet()) {
            cutsBeard += e.getValue();
        }

        for (Map.Entry<Integer, Integer> e : mapCutsDrawing.entrySet()) {
            cutsDrawing += e.getValue();
        }
        for (Map.Entry<Integer, Double> e : mapMonthAndEarningsLupos.entrySet()) {
            earningsLupos += e.getValue();
        }
        for (Map.Entry<Integer, Double> e : mapMonthAndEarningsTotal.entrySet()) {
            earningsTotal += e.getValue();
        }
        lblCutsAdult.setText(String.valueOf(cutsAdult));
        lblCutsBeard.setText(String.valueOf(cutsBeard));
        lblCutsDrawing.setText(String.valueOf(cutsDrawing));
        lblEarningsLupos.setText("$ " + String.valueOf(earningsLupos));
        lblEarningsTotal.setText("$ " + String.valueOf(earningsTotal));
    }

    //Metodo que me abrira la ventada de la estadisticas por el año
    private void generateReporAndStadisticGraphics(String btn) {
        if (rbSearchDiagram.isSelected()) {
            this.setAllMaps();
            try {
                //Dependiendo el boton que apriete el cliente haremos una accion
                if (btn.equalsIgnoreCase("Abrir Grafico")) {
                    this.generateGraphics();
                } else if (btn.equalsIgnoreCase("Buscar")) {
                    this.generateReport();
                }
            } catch (Exception e) {
            }
        } else if (rbSearchReport.isSelected()) {
            this.generateReportBySinceAndUntil();
        }
    }

    //Metodo que me generara el reporte cuando le indicamos una fecha desde hasta
    private void generateReportBySinceAndUntil() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateSinceString = sdf.format(dateSince.getDate());
        String dateUntilString = sdf.format(dateUntil.getDate());

        List<TotalCuts> listTC = new TotalCutsDAOImpl().queryGetBySinceAndUntil(dateSinceString, dateUntilString);
        int cutsAdult = 0;
        int cutsBoy = 0;
        int cutsBeard = 0;
        int cutsDrawing = 0;
        double earningsTotal = 0;
        double earningsLupos = 0;
        //Dependiendo el radio button seleccionado recorremos el map y lo ponemos en los label

        for (TotalCuts tc : listTC) {
            cutsAdult += tc.getCutsAdult();
            cutsBoy += tc.getCutsBoy();
            cutsBeard += tc.getCutsBeard();
            cutsDrawing += tc.getCutsDrawing();
            earningsLupos += tc.getEarningsLupos();
            earningsTotal += tc.getEarningsTotal();
        }

        lblCutsAdult.setText(String.valueOf(cutsAdult));
        lblCutsBeard.setText(String.valueOf(cutsBeard));
        lblCutsDrawing.setText(String.valueOf(cutsDrawing));
        lblEarningsLupos.setText("$ " + String.valueOf(earningsLupos));
        lblEarningsTotal.setText("$ " + String.valueOf(earningsTotal));
    }

    private void saveExpenses() {
        if (!txtValueExpenses.getText().isEmpty()) {
            DAO dao = new ExpensesDAOImpl();
            Expenses e = new Expenses();
            e.setCategory(listCategory.getSelectedItem().toString());
            e.setType(listTypeExpenses.getSelectedItem().toString());
            e.setDate(new java.sql.Date(dateExpenses.getDate().getTime()));
            e.setDescription(textAreaDescription.getText());
            e.setValue(Double.parseDouble(txtValueExpenses.getText()));

            int r = dao.save(e);
            if (r == 1) {
                JOptionPane.showMessageDialog(this, "Se registró correctamente.");
                this.setearFielExpenses();
                pestanas.setSelectedIndex(1);
            } else {
                JOptionPane.showMessageDialog(this, "Se produjo un inconveniente, por favor intente nuevamente.");
            }
        }
    }

    //Metodo que me rellena la tabla, donde recibe una lista con los clientes.
    private void fillTableListExpenses(List<Expenses> list) {
        String[] fila = new String[6];
        list.stream().forEach(e -> {
            fila[0] = String.valueOf(e.getIdExpenses());
            fila[1] = e.getCategory();
            fila[2] = e.getType();
            fila[3] = e.getDate().toString();
            fila[4] = e.getDescription();
            fila[5] = String.valueOf(e.getValue());

            dtm.addRow(fila);
        });
        tableListExpenses.setModel(dtm);
        setearRowWithFinalDate();
    }

    //Metodo para obtener todos los clientes
    private void getAllExpenses() {
        listExpenses = new ExpensesDAOImpl().toList();
    }

    private void getValueTotalExpensesANDTotalCuts() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateSinceString = sdf.format(dateSinceEarningsTotal.getDate());
        String dateUntilString = sdf.format(dateUntilEarningsTotal.getDate());

        DAOExpenses daoExpenses = new ExpensesDAOImpl();
        double valueExpenses = daoExpenses.getPriceTotalExpenses(dateSinceString, dateUntilString);

        DAOTotalCuts daoTotalCuts = new TotalCutsDAOImpl();
        double valueTotalCuts = daoTotalCuts.getPriceTotalHairCut(dateSinceString, dateUntilString);

        lblEarningsTotalGeneral.setText("$ " + valueTotalCuts);
        lblExpesesTotal.setText("$ " + valueExpenses);
        lblEarningDefinitivas.setText("$ " + (valueTotalCuts - valueExpenses));
    }

    //Metodo que me buscara el barbero dependiendo la fecha de corte
    private void getListForDateSinceAndUntil() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateSinceString = sdf.format(dateSinceExpenses.getDate());
        String dateUntilString = sdf.format(dateUntilExpenses.getDate());

        DAOExpenses dao = new ExpensesDAOImpl();

        this.setearTableListExpenses();
        this.fillTableListExpenses(dao.queryFilterForDateBetwen(dateSinceString, dateUntilString));
    }

    private void setearTableListExpenses() {
        dtm = new DefaultTableModel();
        String[] columns = {"Id Gasto", "Categoría", "Tipo", "Fecha", "Descripción", "Valor"};
        dtm.setColumnIdentifiers(columns);
        tableListExpenses.setModel(dtm);

        //Codigo para aplicarle el formato personalizado al encabezado
        JTableHeader jth = tableListExpenses.getTableHeader();
        jth.setDefaultRenderer(new HeaderManagement());
        tableListExpenses.setTableHeader(jth);

        //Codigo para aplicarme los formatos personalizados
        tableListExpenses.getColumnModel().getColumn(0).setCellRenderer(new CellManagement());
        tableListExpenses.getColumnModel().getColumn(1).setCellRenderer(new CellManagement());
        tableListExpenses.getColumnModel().getColumn(2).setCellRenderer(new CellManagement());
        tableListExpenses.getColumnModel().getColumn(3).setCellRenderer(new CellManagement());
        tableListExpenses.getColumnModel().getColumn(4).setCellRenderer(new CellManagement());
        tableListExpenses.getColumnModel().getColumn(5).setCellRenderer(new CellManagement());

        //Codigo para especificar el tamaño de las celdas
        tableListExpenses.setRowHeight(25);
        //Codigo para no poder escribir las celdas
        tableListExpenses.setDefaultEditor(Object.class, null);
    }

    //_______________________________________________________________________________________________________
    //Metodo que me filtrara la tabla, segun lo tipeado
    private void filterTableListExpenses(String text) {
        //Lista que me guardara los elementos que contengan lo ingresado
        listExpensesFinal = new ArrayList<>();
        List<Expenses> list = this.getElementsOfTable();
        if (list != null || list.size() > 0) {
            //Recorremos la lista de todos los clientes
            list.stream().forEach(h -> {
                //Obtenemos algunos datos de este elemento
                String category = h.getCategory().toLowerCase().toString();
                String type = h.getType().toLowerCase().toString();
                String description = h.getDescription().toLowerCase().toString();
                String date = h.getDate().toString().toLowerCase();
                //En el caso de el tipo, fecha o descripcion contenga lo tipeado, guardamos el GASTO en una nueva lista
                if (date.contains(text) || category.contains(text.toLowerCase()) || description.contains(text.toLowerCase()) || type.contains(text.toLowerCase())) {
                    Expenses e = new Expenses();
                    e.setIdExpenses(h.getIdExpenses());
                    e.setCategory(h.getCategory());
                    e.setType(h.getType());
                    e.setDate(h.getDate());
                    e.setDescription(h.getDescription());
                    e.setValue(h.getValue());
                    listExpensesFinal.add(e);
                }
            });
        }
        this.setearTableListExpenses();
        this.fillTableListExpenses(listExpensesFinal);
    }

    //Metodo que me obtendrá solamente todos los registros que existan actualmente en la tabla
    private List<Expenses> getElementsOfTable() {
        int row = tableListExpenses.getRowCount();
        List<Expenses> list = new ArrayList<>();
        for (int i = 0; i < row - 1; i++) {
            try {
                Expenses e = new Expenses();
                e.setIdExpenses(Integer.parseInt(tableListExpenses.getValueAt(i, 0).toString()));
                e.setCategory(tableListExpenses.getValueAt(i, 1).toString());
                e.setType(tableListExpenses.getValueAt(i, 2).toString());

                //Convertir el string en java.sql.Date
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date parsed = format.parse(tableListExpenses.getValueAt(i, 3).toString());
                java.sql.Date sql = new java.sql.Date(parsed.getTime());
                e.setDate(sql);

                e.setDescription(tableListExpenses.getValueAt(i, 4).toString());
                e.setValue(Double.parseDouble(tableListExpenses.getValueAt(i, 5).toString()));

                list.add(e);
            } catch (ParseException ex) {
                Logger.getLogger(MenuStatistics.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return list;
    }

    //_______________________________________________________________________________________________________
    //Metodo que me agregara los resultados finales en la ultima fila de la tabla
    private void setearRowWithFinalDate() {

        int row = tableListExpenses.getRowCount();
        double valueTotalExpenses = 0;
        if (row > 0) {
            for (int i = 0; i < row; i++) {
                valueTotalExpenses += Double.parseDouble(tableListExpenses.getValueAt(i, 5).toString());
            }

            String[] fila = new String[6];
            fila[0] = "";
            fila[1] = "";
            fila[2] = "";
            fila[3] = "";
            fila[4] = "Gastos Total";
            fila[5] = "$ " + String.valueOf(valueTotalExpenses);

            dtm.addRow(fila);
            tableListExpenses.setModel(dtm);
        }

    }

    //______________________________________________________________________________________________________________
    private void validarCamposTextos(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (Character.isDigit(c)) {
            getToolkit().beep();
            evt.consume();
        }
    }

    private void validarCamposNumericos(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9')) {
            evt.consume();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupTypeSearch = new javax.swing.ButtonGroup();
        groupSearchAvanced = new javax.swing.ButtonGroup();
        pestanas = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        listCategory = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaDescription = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtValueExpenses = new javax.swing.JTextField();
        btnSaveExpenses = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        dateExpenses = new com.toedter.calendar.JDateChooser();
        listTypeExpenses = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        txtSearchExpenses = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        lblCountCuts = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableListExpenses = new javax.swing.JTable();
        dateUntilExpenses = new com.toedter.calendar.JDateChooser();
        dateSinceExpenses = new com.toedter.calendar.JDateChooser();
        btnSearchExpenses = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jListYear = new javax.swing.JComboBox<>();
        btnSearch = new javax.swing.JButton();
        dateSince = new com.toedter.calendar.JDateChooser();
        lblReport = new javax.swing.JLabel();
        lblStatictisDriagram = new javax.swing.JLabel();
        dateUntil = new com.toedter.calendar.JDateChooser();
        rbSearchReport = new javax.swing.JRadioButton();
        rbSearchDiagram = new javax.swing.JRadioButton();
        panelStatistics = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblEarningsLupos = new javax.swing.JLabel();
        lblCutsAdult = new javax.swing.JLabel();
        lblCutsDrawing = new javax.swing.JLabel();
        lblCutsBeard = new javax.swing.JLabel();
        btnOpenGraphics = new javax.swing.JButton();
        rbEarningsLupos = new javax.swing.JRadioButton();
        rbCutsAdult = new javax.swing.JRadioButton();
        rbCutsBeard = new javax.swing.JRadioButton();
        rbCutsDrawing = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        lblEarningsTotal = new javax.swing.JLabel();
        rbEarningsTotal = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        dateSinceEarningsTotal = new com.toedter.calendar.JDateChooser();
        dateUntilEarningsTotal = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblEarningsTotalGeneral = new javax.swing.JLabel();
        lblExpesesTotal = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        btnCalculateValueTotal = new javax.swing.JButton();
        lblEarningDefinitivas = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pestanas.setBackground(new java.awt.Color(0, 0, 0));
        pestanas.setForeground(new java.awt.Color(255, 255, 255));
        pestanas.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        pestanas.setPreferredSize(new java.awt.Dimension(940, 565));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("TIPO");

        listCategory.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        listCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Barbería", "Empleados", "Personal" }));
        listCategory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        listCategory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listCategoryMouseClicked(evt);
            }
        });

        textAreaDescription.setColumns(20);
        textAreaDescription.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        textAreaDescription.setRows(5);
        textAreaDescription.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        textAreaDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textAreaDescriptionKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(textAreaDescription);

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("DESCRIPCION");

        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("VALOR");

        txtValueExpenses.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        txtValueExpenses.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtValueExpenses.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        txtValueExpenses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtValueExpensesKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtValueExpensesKeyTyped(evt);
            }
        });

        btnSaveExpenses.setBackground(new java.awt.Color(0, 153, 204));
        btnSaveExpenses.setFont(new java.awt.Font("Arial Unicode MS", 3, 14)); // NOI18N
        btnSaveExpenses.setForeground(new java.awt.Color(255, 255, 255));
        btnSaveExpenses.setText("GUARDAR");
        btnSaveExpenses.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnSaveExpenses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveExpensesActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("FECHA");

        dateExpenses.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        listTypeExpenses.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        listTypeExpenses.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "- - -" }));
        listTypeExpenses.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        listTypeExpenses.setEnabled(false);

        jLabel10.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("GASTO");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(296, 296, 296)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(11, 11, 11)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtValueExpenses, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE))
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dateExpenses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(listCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(listTypeExpenses, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(269, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnSaveExpenses, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(351, 351, 351))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(listCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(listTypeExpenses, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateExpenses, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtValueExpenses, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(btnSaveExpenses, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );

        pestanas.addTab("Gastos General", jPanel1);

        jPanel8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPanel8KeyTyped(evt);
            }
        });

        txtSearchExpenses.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        txtSearchExpenses.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSearchExpenses.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtSearchExpenses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchExpensesActionPerformed(evt);
            }
        });
        txtSearchExpenses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearchExpensesKeyTyped(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Arial", 3, 20)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("BUSCAR POR:");
        jLabel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblCountCuts.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        lblCountCuts.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jButton1.setBackground(new java.awt.Color(0, 153, 204));
        jButton1.setFont(new java.awt.Font("Arial Unicode MS", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("MOSTRAR TODOS");
        jButton1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblCountCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtSearchExpenses, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchExpenses, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(lblCountCuts, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tableListExpenses.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tableListExpenses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(tableListExpenses);

        dateUntilExpenses.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        dateSinceExpenses.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        btnSearchExpenses.setBackground(new java.awt.Color(0, 153, 204));
        btnSearchExpenses.setFont(new java.awt.Font("Arial Unicode MS", 1, 12)); // NOI18N
        btnSearchExpenses.setForeground(new java.awt.Color(255, 255, 255));
        btnSearchExpenses.setText("BUSCAR");
        btnSearchExpenses.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnSearchExpenses.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearchExpenses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchExpensesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 931, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(dateSinceExpenses, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(btnSearchExpenses, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dateUntilExpenses, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dateSinceExpenses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dateUntilExpenses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSearchExpenses, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pestanas.addTab("Lista Gastos", jPanel4);

        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jListYear.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jListYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2019", "2020", "2021", "2022" }));

        btnSearch.setBackground(new java.awt.Color(0, 153, 204));
        btnSearch.setFont(new java.awt.Font("Arial Unicode MS", 3, 22)); // NOI18N
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("BUSCAR");
        btnSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        dateSince.setDateFormatString("yyyy/MM/dd");
        dateSince.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        lblReport.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblReport.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblReport.setText("REPORTE");

        lblStatictisDriagram.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblStatictisDriagram.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatictisDriagram.setText("DIAGRAMA ESTADÍSTICO");

        dateUntil.setDateFormatString("yyyy/MM/dd");
        dateUntil.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        groupTypeSearch.add(rbSearchReport);
        rbSearchReport.setText("CONFIRMAR");
        rbSearchReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbSearchReportActionPerformed(evt);
            }
        });

        groupTypeSearch.add(rbSearchDiagram);
        rbSearchDiagram.setText("CONFIRMAR");
        rbSearchDiagram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbSearchDiagramActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblReport, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dateSince, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                            .addComponent(dateUntil, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(rbSearchReport)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(88, 88, 88)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jListYear, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStatictisDriagram, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(rbSearchDiagram)
                        .addGap(104, 104, 104))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatictisDriagram, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jListYear, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(lblReport, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateSince, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateUntil, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbSearchReport)
                    .addComponent(rbSearchDiagram))
                .addGap(8, 8, 8))
        );

        panelStatistics.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelStatistics.setPreferredSize(new java.awt.Dimension(905, 391));

        jLabel3.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel3.setText("Ganancias Lupos:");

        jLabel4.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel4.setText("Cortes");

        jLabel6.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel6.setText("Dibujos:");

        jLabel7.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel7.setText("Barbas:");

        lblEarningsLupos.setBackground(new java.awt.Color(255, 255, 255));
        lblEarningsLupos.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblEarningsLupos.setForeground(new java.awt.Color(255, 0, 0));
        lblEarningsLupos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEarningsLupos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        lblCutsAdult.setBackground(new java.awt.Color(255, 255, 255));
        lblCutsAdult.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblCutsAdult.setForeground(new java.awt.Color(255, 0, 0));
        lblCutsAdult.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsAdult.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        lblCutsDrawing.setBackground(new java.awt.Color(255, 255, 255));
        lblCutsDrawing.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblCutsDrawing.setForeground(new java.awt.Color(255, 0, 0));
        lblCutsDrawing.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsDrawing.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        lblCutsBeard.setBackground(new java.awt.Color(255, 255, 255));
        lblCutsBeard.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblCutsBeard.setForeground(new java.awt.Color(255, 0, 0));
        lblCutsBeard.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsBeard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        btnOpenGraphics.setBackground(new java.awt.Color(0, 153, 204));
        btnOpenGraphics.setFont(new java.awt.Font("Arial Unicode MS", 3, 14)); // NOI18N
        btnOpenGraphics.setForeground(new java.awt.Color(255, 255, 255));
        btnOpenGraphics.setText("ABRIR GRAFICO");
        btnOpenGraphics.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnOpenGraphics.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOpenGraphics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenGraphicsActionPerformed(evt);
            }
        });

        groupSearchAvanced.add(rbEarningsLupos);
        rbEarningsLupos.setText("GRAFICO GANANCIAS LUPO");

        groupSearchAvanced.add(rbCutsAdult);
        rbCutsAdult.setText("GRAFICO CORTES");

        groupSearchAvanced.add(rbCutsBeard);
        rbCutsBeard.setText("GRAFICO BARBAS");

        groupSearchAvanced.add(rbCutsDrawing);
        rbCutsDrawing.setText("GRAFICO DIBUJOS");

        jLabel9.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel9.setText("Ganancias Total:");

        lblEarningsTotal.setBackground(new java.awt.Color(255, 255, 255));
        lblEarningsTotal.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblEarningsTotal.setForeground(new java.awt.Color(255, 0, 0));
        lblEarningsTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEarningsTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        groupSearchAvanced.add(rbEarningsTotal);
        rbEarningsTotal.setText("GRAFICO GANANCIAS TOTAL");

        javax.swing.GroupLayout panelStatisticsLayout = new javax.swing.GroupLayout(panelStatistics);
        panelStatistics.setLayout(panelStatisticsLayout);
        panelStatisticsLayout.setHorizontalGroup(
            panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatisticsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelStatisticsLayout.createSequentialGroup()
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelStatisticsLayout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblCutsAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelStatisticsLayout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblEarningsLupos, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelStatisticsLayout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblEarningsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(73, 73, 73)
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rbCutsAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rbEarningsLupos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rbEarningsTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
                        .addContainerGap(118, Short.MAX_VALUE))
                    .addGroup(panelStatisticsLayout.createSequentialGroup()
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelStatisticsLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblCutsBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(73, 73, 73)
                                .addComponent(rbCutsBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelStatisticsLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblCutsDrawing, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(73, 73, 73)
                                .addComponent(rbCutsDrawing))
                            .addGroup(panelStatisticsLayout.createSequentialGroup()
                                .addGap(287, 287, 287)
                                .addComponent(btnOpenGraphics, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        panelStatisticsLayout.setVerticalGroup(
            panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatisticsLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblEarningsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(rbEarningsTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblEarningsLupos, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(rbEarningsLupos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCutsAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelStatisticsLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(rbCutsAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelStatisticsLayout.createSequentialGroup()
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCutsBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3))
                    .addGroup(panelStatisticsLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(rbCutsBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelStatisticsLayout.createSequentialGroup()
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCutsDrawing, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelStatisticsLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(rbCutsDrawing, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15)
                .addComponent(btnOpenGraphics, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 53, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelStatistics, javax.swing.GroupLayout.DEFAULT_SIZE, 935, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelStatistics, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pestanas.addTab("Estadísticas Cortes", jPanel3);

        dateSinceEarningsTotal.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        dateSinceEarningsTotal.setDateFormatString("yyyy/MM/dd");
        dateSinceEarningsTotal.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        dateUntilEarningsTotal.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        dateUntilEarningsTotal.setDateFormatString("yyyy/MM/dd");
        dateUntilEarningsTotal.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel11.setText("Ganancias Lupos:");

        jLabel13.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel13.setText("Gastos Total:");

        lblEarningsTotalGeneral.setBackground(new java.awt.Color(255, 255, 255));
        lblEarningsTotalGeneral.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblEarningsTotalGeneral.setForeground(new java.awt.Color(255, 0, 0));
        lblEarningsTotalGeneral.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEarningsTotalGeneral.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        lblExpesesTotal.setBackground(new java.awt.Color(255, 255, 255));
        lblExpesesTotal.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblExpesesTotal.setForeground(new java.awt.Color(255, 0, 0));
        lblExpesesTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblExpesesTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel14.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel14.setText("Ganancias definitivas:");

        jButton2.setText("IMPRIMIR REPORTE");

        btnCalculateValueTotal.setBackground(new java.awt.Color(0, 153, 204));
        btnCalculateValueTotal.setFont(new java.awt.Font("Arial Unicode MS", 3, 22)); // NOI18N
        btnCalculateValueTotal.setForeground(new java.awt.Color(255, 255, 255));
        btnCalculateValueTotal.setText("BUSCAR");
        btnCalculateValueTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnCalculateValueTotal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCalculateValueTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateValueTotalActionPerformed(evt);
            }
        });

        lblEarningDefinitivas.setBackground(new java.awt.Color(0, 0, 0));
        lblEarningDefinitivas.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblEarningDefinitivas.setForeground(new java.awt.Color(255, 0, 0));
        lblEarningDefinitivas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEarningDefinitivas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(223, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(329, 329, 329))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(dateSinceEarningsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dateUntilEarningsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCalculateValueTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblEarningsTotalGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblExpesesTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblEarningDefinitivas, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(206, 206, 206))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCalculateValueTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(dateSinceEarningsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dateUntilEarningsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(47, 47, 47)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEarningsTotalGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblExpesesTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEarningDefinitivas, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(117, Short.MAX_VALUE))
        );

        pestanas.addTab("Estadísticas General", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pestanas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pestanas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOpenGraphicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenGraphicsActionPerformed
        if (rbSearchDiagram.isSelected()) {
            if (rbCutsAdult.isSelected() || rbCutsBeard.isSelected()
                    || rbCutsDrawing.isSelected() || rbEarningsTotal.isSelected() || rbEarningsLupos.isSelected()) {
                this.generateReporAndStadisticGraphics(evt.getActionCommand());
            }
        }

    }//GEN-LAST:event_btnOpenGraphicsActionPerformed

    private void rbSearchDiagramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbSearchDiagramActionPerformed
        setearField();
    }//GEN-LAST:event_rbSearchDiagramActionPerformed

    private void rbSearchReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbSearchReportActionPerformed
        this.setearField();
    }//GEN-LAST:event_rbSearchReportActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        this.generateReporAndStadisticGraphics(evt.getActionCommand());
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnSaveExpensesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveExpensesActionPerformed
        this.saveExpenses();
    }//GEN-LAST:event_btnSaveExpensesActionPerformed

    private void txtValueExpensesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValueExpensesKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.saveExpenses();
        }
    }//GEN-LAST:event_txtValueExpensesKeyPressed

    private void txtValueExpensesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValueExpensesKeyTyped
        validarCamposNumericos(evt);
    }//GEN-LAST:event_txtValueExpensesKeyTyped

    private void textAreaDescriptionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaDescriptionKeyTyped

    }//GEN-LAST:event_textAreaDescriptionKeyTyped

    private void txtSearchExpensesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchExpensesActionPerformed

    }//GEN-LAST:event_txtSearchExpensesActionPerformed

    private void txtSearchExpensesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchExpensesKeyTyped
        filterTableListExpenses(txtSearchExpenses.getText());
    }//GEN-LAST:event_txtSearchExpensesKeyTyped

    private void jPanel8KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel8KeyTyped

    }//GEN-LAST:event_jPanel8KeyTyped

    private void btnSearchExpensesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchExpensesActionPerformed
        this.getListForDateSinceAndUntil();
    }//GEN-LAST:event_btnSearchExpensesActionPerformed

    private void listCategoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listCategoryMouseClicked
    }//GEN-LAST:event_listCategoryMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setearTableListExpenses();
        this.fillTableListExpenses(listExpenses);
        txtSearchExpenses.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnCalculateValueTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateValueTotalActionPerformed
        this.getValueTotalExpensesANDTotalCuts();
    }//GEN-LAST:event_btnCalculateValueTotalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalculateValueTotal;
    private javax.swing.JButton btnOpenGraphics;
    private javax.swing.JButton btnSaveExpenses;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchExpenses;
    private com.toedter.calendar.JDateChooser dateExpenses;
    private com.toedter.calendar.JDateChooser dateSince;
    private com.toedter.calendar.JDateChooser dateSinceEarningsTotal;
    private com.toedter.calendar.JDateChooser dateSinceExpenses;
    private com.toedter.calendar.JDateChooser dateUntil;
    private com.toedter.calendar.JDateChooser dateUntilEarningsTotal;
    private com.toedter.calendar.JDateChooser dateUntilExpenses;
    private javax.swing.ButtonGroup groupSearchAvanced;
    private javax.swing.ButtonGroup groupTypeSearch;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox<String> jListYear;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCountCuts;
    private javax.swing.JLabel lblCutsAdult;
    private javax.swing.JLabel lblCutsBeard;
    private javax.swing.JLabel lblCutsDrawing;
    private javax.swing.JLabel lblEarningDefinitivas;
    private javax.swing.JLabel lblEarningsLupos;
    private javax.swing.JLabel lblEarningsTotal;
    private javax.swing.JLabel lblEarningsTotalGeneral;
    private javax.swing.JLabel lblExpesesTotal;
    private javax.swing.JLabel lblReport;
    private javax.swing.JLabel lblStatictisDriagram;
    private javax.swing.JComboBox<String> listCategory;
    private javax.swing.JComboBox<String> listTypeExpenses;
    private javax.swing.JPanel panelStatistics;
    private javax.swing.JTabbedPane pestanas;
    private javax.swing.JRadioButton rbCutsAdult;
    private javax.swing.JRadioButton rbCutsBeard;
    private javax.swing.JRadioButton rbCutsDrawing;
    private javax.swing.JRadioButton rbEarningsLupos;
    private javax.swing.JRadioButton rbEarningsTotal;
    private javax.swing.JRadioButton rbSearchDiagram;
    private javax.swing.JRadioButton rbSearchReport;
    private javax.swing.JTable tableListExpenses;
    private javax.swing.JTextArea textAreaDescription;
    private javax.swing.JTextField txtSearchExpenses;
    private javax.swing.JTextField txtValueExpenses;
    // End of variables declaration//GEN-END:variables
}
