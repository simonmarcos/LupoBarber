package com.simonmarcos.lupos;

import com.simonmarcos.lupos.views.MenuPrincipal;
import java.util.Map;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class LuposBarber {

    public static void main(String[] args) {

        MenuPrincipal mp = new MenuPrincipal();
        mp.setVisible(true);
        mp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /*double marzo = new HairCutDAOImpl().queryGetEarningsTotal("2019-03-01", "2019-03-31", 2);
        double abril = new HairCutDAOImpl().queryGetEarningsTotal("2019-04-01", "2019-04-30", 2);

        Map<Double, String> listaGanancias = new HashMap<>();
        listaGanancias.put(marzo, "Marzo");
        listaGanancias.put(abril, "Abril");
        generarBarrar(listaGanancias);*/
    }

    
}
