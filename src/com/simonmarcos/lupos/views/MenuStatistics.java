package com.simonmarcos.lupos.views;

import com.simonmarcos.lupos.dao.impl.TotalCutsDAOImpl;
import com.simonmarcos.lupos.model.TotalCuts;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public MenuStatistics(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setFocusable(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Menu Estadísticas");
        this.setDateChooser();
        this.setearField();
    }

    private void setDateChooser() {
        dateSince.setDate(new Date());
        dateUntil.setDate(new Date());
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupTypeSearch = new javax.swing.ButtonGroup();
        groupSearchAvanced = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
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
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(905, 532));

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(940, 565));

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
        dateSince.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        lblReport.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblReport.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblReport.setText("REPORTE");

        lblStatictisDriagram.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblStatictisDriagram.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatictisDriagram.setText("DIAGRAMA ESTADÍSTICO");

        dateUntil.setDateFormatString("yyyy/MM/dd");
        dateUntil.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        lblEarningsLupos.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        lblEarningsLupos.setForeground(new java.awt.Color(255, 0, 0));
        lblEarningsLupos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEarningsLupos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        lblCutsAdult.setBackground(new java.awt.Color(255, 255, 255));
        lblCutsAdult.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        lblCutsAdult.setForeground(new java.awt.Color(255, 0, 0));
        lblCutsAdult.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsAdult.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        lblCutsDrawing.setBackground(new java.awt.Color(255, 255, 255));
        lblCutsDrawing.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        lblCutsDrawing.setForeground(new java.awt.Color(255, 0, 0));
        lblCutsDrawing.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCutsDrawing.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        lblCutsBeard.setBackground(new java.awt.Color(255, 255, 255));
        lblCutsBeard.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
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
        rbEarningsLupos.setText("CONFIRMAR");

        groupSearchAvanced.add(rbCutsAdult);
        rbCutsAdult.setText("CONFIRMAR");

        groupSearchAvanced.add(rbCutsBeard);
        rbCutsBeard.setText("CONFIRMAR");

        groupSearchAvanced.add(rbCutsDrawing);
        rbCutsDrawing.setText("CONFIRMAR");

        jLabel9.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        jLabel9.setText("Ganancias Total:");

        lblEarningsTotal.setBackground(new java.awt.Color(255, 255, 255));
        lblEarningsTotal.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        lblEarningsTotal.setForeground(new java.awt.Color(255, 0, 0));
        lblEarningsTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEarningsTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        groupSearchAvanced.add(rbEarningsTotal);
        rbEarningsTotal.setText("CONFIRMAR");

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
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbCutsAdult)
                            .addComponent(rbEarningsLupos)
                            .addComponent(rbEarningsTotal))
                        .addContainerGap(229, Short.MAX_VALUE))
                    .addGroup(panelStatisticsLayout.createSequentialGroup()
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelStatisticsLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblCutsDrawing, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelStatisticsLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblCutsBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(73, 73, 73)
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbCutsDrawing)
                            .addComponent(rbCutsBeard))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(panelStatisticsLayout.createSequentialGroup()
                .addGap(297, 297, 297)
                .addComponent(btnOpenGraphics, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelStatisticsLayout.setVerticalGroup(
            panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatisticsLayout.createSequentialGroup()
                .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelStatisticsLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEarningsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelStatisticsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rbEarningsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelStatisticsLayout.createSequentialGroup()
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEarningsLupos, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCutsAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelStatisticsLayout.createSequentialGroup()
                        .addComponent(rbEarningsLupos, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(rbCutsAdult, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)))
                .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelStatisticsLayout.createSequentialGroup()
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCutsBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(panelStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCutsDrawing, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelStatisticsLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(rbCutsBeard, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(rbCutsDrawing, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(btnOpenGraphics, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 73, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panelStatistics, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 909, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(1, 1, 1))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelStatistics, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Estadísticas Lupo's", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 935, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 537, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Gastos General", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOpenGraphicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenGraphicsActionPerformed
        if (rbCutsAdult.isSelected() || rbCutsBeard.isSelected()
                || rbCutsDrawing.isSelected() || rbEarningsTotal.isSelected() || rbEarningsLupos.isSelected()) {
            this.generateReporAndStadisticGraphics(evt.getActionCommand());
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOpenGraphics;
    private javax.swing.JButton btnSearch;
    private com.toedter.calendar.JDateChooser dateSince;
    private com.toedter.calendar.JDateChooser dateUntil;
    private javax.swing.ButtonGroup groupSearchAvanced;
    private javax.swing.ButtonGroup groupTypeSearch;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox<String> jListYear;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblCutsAdult;
    private javax.swing.JLabel lblCutsBeard;
    private javax.swing.JLabel lblCutsDrawing;
    private javax.swing.JLabel lblEarningsLupos;
    private javax.swing.JLabel lblEarningsTotal;
    private javax.swing.JLabel lblReport;
    private javax.swing.JLabel lblStatictisDriagram;
    private javax.swing.JPanel panelStatistics;
    private javax.swing.JRadioButton rbCutsAdult;
    private javax.swing.JRadioButton rbCutsBeard;
    private javax.swing.JRadioButton rbCutsDrawing;
    private javax.swing.JRadioButton rbEarningsLupos;
    private javax.swing.JRadioButton rbEarningsTotal;
    private javax.swing.JRadioButton rbSearchDiagram;
    private javax.swing.JRadioButton rbSearchReport;
    // End of variables declaration//GEN-END:variables
}
