package com.viacosta.view;

import com.viacosta.model.AsignacionBusRuta;
import com.viacosta.model.Bus;
import com.viacosta.model.Ruta;
import com.viacosta.service.ViajeService;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class ViajesPanel extends javax.swing.JPanel {

    private final ViajeService viajeService;
    private final DefaultTableModel modeloTabla;
    private final DefaultListModel<String> modeloRutas;
    private final DefaultListModel<String> modeloBuses;

    public ViajesPanel() {
        this.viajeService = new ViajeService();
        initComponents();

        modeloTabla = (DefaultTableModel) tblSalidas.getModel();
        modeloRutas = new DefaultListModel<>();
        modeloBuses = new DefaultListModel<>();
        lstRutas.setModel(modeloRutas);
        lstBuses.setModel(modeloBuses);

        cargarDatos();
    }

    public final void cargarDatos() {
        new Thread(() -> {
            List<AsignacionBusRuta> salidas = viajeService.listarSalidasVigentes();
            List<Ruta> rutas = viajeService.listarRutas();
            List<Bus> buses = viajeService.listarBuses();

            SwingUtilities.invokeLater(() -> {
                modeloTabla.setRowCount(0);
                for (AsignacionBusRuta s : salidas) {
                    int tot = s.getCapacidadTotal();
                    int lib = s.getAsientosDisponibles();
                    int ocu = tot - lib;
                    modeloTabla.addRow(new Object[]{
                        s.getIdAsignacion(),
                        s.getFechaSalidaFormateada(),
                        s.getHoraSalidaFormateada(),
                        s.getRutaCompleta(),
                        s.getPlacaBus(),
                        tot + " asientos",
                        lib + " libres",
                        ocu + " ocupados"
                    });
                }

                lblTotalSalidas.setText("Salidas: " + salidas.size());
                lblTotalBuses.setText("Buses: " + buses.size());
                lblTotalRutas.setText("Rutas: " + rutas.size());

                modeloRutas.clear();
                for (Ruta r : rutas) {
                    modeloRutas.addElement(r.getRutaCompleta() + " (" + r.getDuracionHoras() + " horas)");
                }

                modeloBuses.clear();
                for (Bus b : buses) {
                    modeloBuses.addElement("Placa: " + b.getPlaca() + " - " + b.getModelo() + " (" + b.getCapacidadAsientos() + " asientos)");
                }
            });
        }).start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSuperior = new javax.swing.JPanel();
        lblTotalSalidas = new javax.swing.JLabel();
        lblTotalBuses = new javax.swing.JLabel();
        lblTotalRutas = new javax.swing.JLabel();
        btnActualizar = new javax.swing.JButton();
        scrollTablaSalidas = new javax.swing.JScrollPane();
        tblSalidas = new javax.swing.JTable();
        pnlInferior = new javax.swing.JPanel();
        pnlRutasBox = new javax.swing.JPanel();
        lblTituloRutas = new javax.swing.JLabel();
        scrollRutas = new javax.swing.JScrollPane();
        lstRutas = new javax.swing.JList<>();
        pnlBusesBox = new javax.swing.JPanel();
        lblTituloBuses = new javax.swing.JLabel();
        scrollBuses = new javax.swing.JScrollPane();
        lstBuses = new javax.swing.JList<>();

        setBackground(new java.awt.Color(15, 23, 42));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setLayout(new java.awt.BorderLayout(12, 12));

        pnlSuperior.setBackground(new java.awt.Color(30, 41, 59));
        pnlSuperior.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 15, 12, 15));
        pnlSuperior.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        lblTotalSalidas.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblTotalSalidas.setForeground(new java.awt.Color(56, 189, 248));
        lblTotalSalidas.setText("Salidas: 0");
        pnlSuperior.add(lblTotalSalidas);

        lblTotalBuses.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblTotalBuses.setForeground(new java.awt.Color(16, 185, 129));
        lblTotalBuses.setText("Buses: 0");
        pnlSuperior.add(lblTotalBuses);

        lblTotalRutas.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblTotalRutas.setForeground(new java.awt.Color(250, 204, 21));
        lblTotalRutas.setText("Rutas: 0");
        pnlSuperior.add(lblTotalRutas);

        btnActualizar.setBackground(new java.awt.Color(2, 132, 199));
        btnActualizar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });
        pnlSuperior.add(btnActualizar);

        add(pnlSuperior, java.awt.BorderLayout.PAGE_START);

        tblSalidas.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblSalidas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Fecha", "Hora", "Ruta", "Bus", "Capacidad", "Libres", "Ocupados"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSalidas.setRowHeight(26);
        scrollTablaSalidas.setViewportView(tblSalidas);

        add(scrollTablaSalidas, java.awt.BorderLayout.CENTER);

        pnlInferior.setBackground(new java.awt.Color(15, 23, 42));
        pnlInferior.setPreferredSize(new java.awt.Dimension(800, 160));
        pnlInferior.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        pnlRutasBox.setBackground(new java.awt.Color(30, 41, 59));
        pnlRutasBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 10, 8, 10));
        pnlRutasBox.setLayout(new java.awt.BorderLayout(0, 6));

        lblTituloRutas.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTituloRutas.setForeground(new java.awt.Color(56, 189, 248));
        lblTituloRutas.setText("Rutas");
        pnlRutasBox.add(lblTituloRutas, java.awt.BorderLayout.PAGE_START);

        lstRutas.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        scrollRutas.setViewportView(lstRutas);

        pnlRutasBox.add(scrollRutas, java.awt.BorderLayout.CENTER);

        pnlInferior.add(pnlRutasBox);

        pnlBusesBox.setBackground(new java.awt.Color(30, 41, 59));
        pnlBusesBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 10, 8, 10));
        pnlBusesBox.setLayout(new java.awt.BorderLayout(0, 6));

        lblTituloBuses.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTituloBuses.setForeground(new java.awt.Color(56, 189, 248));
        lblTituloBuses.setText("Buses");
        pnlBusesBox.add(lblTituloBuses, java.awt.BorderLayout.PAGE_START);

        lstBuses.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        scrollBuses.setViewportView(lstBuses);

        pnlBusesBox.add(scrollBuses, java.awt.BorderLayout.CENTER);

        pnlInferior.add(pnlBusesBox);

        add(pnlInferior, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        cargarDatos();
    }//GEN-LAST:event_btnActualizarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JLabel lblTituloBuses;
    private javax.swing.JLabel lblTituloRutas;
    private javax.swing.JLabel lblTotalBuses;
    private javax.swing.JLabel lblTotalRutas;
    private javax.swing.JLabel lblTotalSalidas;
    private javax.swing.JList<String> lstBuses;
    private javax.swing.JList<String> lstRutas;
    private javax.swing.JPanel pnlBusesBox;
    private javax.swing.JPanel pnlInferior;
    private javax.swing.JPanel pnlRutasBox;
    private javax.swing.JPanel pnlSuperior;
    private javax.swing.JScrollPane scrollBuses;
    private javax.swing.JScrollPane scrollRutas;
    private javax.swing.JScrollPane scrollTablaSalidas;
    private javax.swing.JTable tblSalidas;
    // End of variables declaration//GEN-END:variables
}
