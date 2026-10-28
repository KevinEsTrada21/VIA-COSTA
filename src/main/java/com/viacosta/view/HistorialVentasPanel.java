package com.viacosta.view;

import com.viacosta.model.Compra;
import com.viacosta.model.DetalleBoleta;
import com.viacosta.service.VentaService;
import com.viacosta.util.AlertHelper;
import com.viacosta.util.CurrencyFormatter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class HistorialVentasPanel extends javax.swing.JPanel {

    private final VentaService ventaService;
    private final DefaultTableModel modeloCompras;
    private final DefaultTableModel modeloDetalles;
    private final List<Compra> comprasEnMemoria = new ArrayList<>();

    public HistorialVentasPanel() {
        this.ventaService = new VentaService();
        initComponents();

        modeloCompras = (DefaultTableModel) tblCompras.getModel();
        modeloDetalles = (DefaultTableModel) tblDetalles.getModel();

        tblCompras.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tblCompras.getSelectedRow();
                if (fila >= 0 && fila < comprasEnMemoria.size()) {
                    cargarDetallesCompra(comprasEnMemoria.get(fila));
                }
            }
        });

        cargarHistorial();
    }

    public final void cargarHistorial() {
        new Thread(() -> {
            List<Compra> compras = ventaService.listarHistorialVentas();
            BigDecimal recaudado = BigDecimal.ZERO;
            for (Compra c : compras) {
                if (c.getTotal() != null) {
                    recaudado = recaudado.add(c.getTotal());
                }
            }

            final BigDecimal finalRecaudado = recaudado;
            SwingUtilities.invokeLater(() -> {
                comprasEnMemoria.clear();
                comprasEnMemoria.addAll(compras);
                modeloCompras.setRowCount(0);
                modeloDetalles.setRowCount(0);

                for (Compra c : compras) {
                    String comp = (c.getTipoComprobante() != null ? c.getTipoComprobante() : "Boleta") + " "
                            + (c.getNumeroComprobante() != null ? c.getNumeroComprobante() : "-");
                    modeloCompras.addRow(new Object[]{
                        "#" + c.getIdCompra(),
                        comp,
                        c.getFechaCompraFormateada(),
                        c.getClienteNombre() + " (" + c.getClienteDni() + ")",
                        c.getEmpleadoNombre(),
                        c.getMetodoPago(),
                        CurrencyFormatter.format(c.getTotal())
                    });
                }

                lblTotalVentas.setText("Ventas: " + compras.size());
                lblMontoRecaudado.setText("Total: " + CurrencyFormatter.format(finalRecaudado));

                if (!compras.isEmpty()) {
                    tblCompras.setRowSelectionInterval(0, 0);
                }
            });
        }).start();
    }

    private void cargarDetallesCompra(Compra compra) {
        lblDetalleCompraTitulo.setText("Pasajes de la venta #" + compra.getIdCompra()
                + " (" + compra.getNumeroComprobante() + " - " + compra.getClienteNombre() + ")");
        new Thread(() -> {
            List<DetalleBoleta> detalles = ventaService.listarDetallesVenta(compra.getIdCompra());
            SwingUtilities.invokeLater(() -> {
                modeloDetalles.setRowCount(0);
                for (DetalleBoleta d : detalles) {
                    modeloDetalles.addRow(new Object[]{
                        "Asiento #" + String.format("%02d", d.getNumeroAsiento()),
                        d.getTipoAsiento(),
                        d.getOrigen() + " -> " + d.getDestino(),
                        d.getFechaViajeFormateada() + " " + d.getHoraViajeFormateada(),
                        CurrencyFormatter.format(d.getPrecioUnitario())
                    });
                }
            });
        }).start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSuperior = new javax.swing.JPanel();
        lblTotalVentas = new javax.swing.JLabel();
        lblMontoRecaudado = new javax.swing.JLabel();
        btnReimprimir = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        pnlTablas = new javax.swing.JPanel();
        scrollCompras = new javax.swing.JScrollPane();
        tblCompras = new javax.swing.JTable();
        pnlDetallesWrapper = new javax.swing.JPanel();
        lblDetalleCompraTitulo = new javax.swing.JLabel();
        scrollDetalles = new javax.swing.JScrollPane();
        tblDetalles = new javax.swing.JTable();

        setBackground(new java.awt.Color(15, 23, 42));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setLayout(new java.awt.BorderLayout(12, 12));

        pnlSuperior.setBackground(new java.awt.Color(30, 41, 59));
        pnlSuperior.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 15, 12, 15));
        pnlSuperior.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        lblTotalVentas.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblTotalVentas.setForeground(new java.awt.Color(56, 189, 248));
        lblTotalVentas.setText("Ventas: 0");
        pnlSuperior.add(lblTotalVentas);

        lblMontoRecaudado.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblMontoRecaudado.setForeground(new java.awt.Color(16, 185, 129));
        lblMontoRecaudado.setText("Total: S/ 0.00");
        pnlSuperior.add(lblMontoRecaudado);

        btnReimprimir.setBackground(new java.awt.Color(51, 65, 85));
        btnReimprimir.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnReimprimir.setForeground(new java.awt.Color(248, 250, 252));
        btnReimprimir.setText("Imprimir");
        btnReimprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReimprimirActionPerformed(evt);
            }
        });
        pnlSuperior.add(btnReimprimir);

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

        pnlTablas.setBackground(new java.awt.Color(15, 23, 42));
        pnlTablas.setLayout(new java.awt.GridLayout(2, 1, 0, 12));

        tblCompras.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblCompras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Comprobante", "Fecha", "Cliente", "Vendedor", "Pago", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCompras.setRowHeight(26);
        scrollCompras.setViewportView(tblCompras);

        pnlTablas.add(scrollCompras);

        pnlDetallesWrapper.setBackground(new java.awt.Color(30, 41, 59));
        pnlDetallesWrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));
        pnlDetallesWrapper.setLayout(new java.awt.BorderLayout(0, 8));

        lblDetalleCompraTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDetalleCompraTitulo.setForeground(new java.awt.Color(56, 189, 248));
        lblDetalleCompraTitulo.setText("Pasajes de la venta seleccionada:");
        pnlDetallesWrapper.add(lblDetalleCompraTitulo, java.awt.BorderLayout.PAGE_START);

        tblDetalles.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblDetalles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Asiento", "Tipo", "Ruta", "Salida", "Precio"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalles.setRowHeight(25);
        scrollDetalles.setViewportView(tblDetalles);

        pnlDetallesWrapper.add(scrollDetalles, java.awt.BorderLayout.CENTER);

        pnlTablas.add(pnlDetallesWrapper);

        add(pnlTablas, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        cargarHistorial();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnReimprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReimprimirActionPerformed
        int fila = tblCompras.getSelectedRow();
        if (fila < 0 || fila >= comprasEnMemoria.size()) {
            AlertHelper.showWarning("Aviso", "", "Selecciona una venta de la tabla.");
            return;
        }
        Compra c = comprasEnMemoria.get(fila);
        AlertHelper.showInfo("Imprimir", "", "Boleto " + c.getNumeroComprobante() + " enviado a imprimir.");
    }//GEN-LAST:event_btnReimprimirActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnReimprimir;
    private javax.swing.JLabel lblDetalleCompraTitulo;
    private javax.swing.JLabel lblMontoRecaudado;
    private javax.swing.JLabel lblTotalVentas;
    private javax.swing.JPanel pnlDetallesWrapper;
    private javax.swing.JPanel pnlSuperior;
    private javax.swing.JPanel pnlTablas;
    private javax.swing.JScrollPane scrollCompras;
    private javax.swing.JScrollPane scrollDetalles;
    private javax.swing.JTable tblCompras;
    private javax.swing.JTable tblDetalles;
    // End of variables declaration//GEN-END:variables
}
