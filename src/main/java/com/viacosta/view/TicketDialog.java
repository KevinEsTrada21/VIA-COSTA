package com.viacosta.view;

import com.viacosta.model.DetalleBoleta;
import com.viacosta.service.VentaService;
import com.viacosta.util.AlertHelper;
import com.viacosta.util.CurrencyFormatter;

public class TicketDialog extends javax.swing.JDialog {

    public TicketDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
    }

    public void setDatosVenta(VentaService.ResultadoVenta res) {
        lblEmpresa.setText("Via Costa");
        lblRuc.setText("RUC: 20601234567 - Chimbote");

        String tipoComp = res.getComprobante().getTipoComprobante().toUpperCase();
        lblTipoNumeroComprobante.setText(tipoComp + ": " + res.getComprobante().getNumeroComprobante());
        lblFechaEmision.setText("Fecha: " + res.getComprobante().getFechaEmisionFormateada());

        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------\n");
        sb.append("Pasajero   : ").append(res.getCliente().getNombreCompleto()).append("\n");
        sb.append("DNI        : ").append(res.getCliente().getDni()).append("\n");
        sb.append("Ruta       : ").append(res.getViaje().getRutaCompleta()).append("\n");
        sb.append("Salida     : ").append(res.getViaje().getFechaSalidaFormateada())
          .append(" ").append(res.getViaje().getHoraSalidaFormateada()).append("\n");
        sb.append("Bus        : ").append(res.getViaje().getPlacaBus()).append("\n");
        sb.append("Pago       : ").append(res.getCompra().getMetodoPago()).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Asientos:\n");
        for (DetalleBoleta d : res.getDetalles()) {
            sb.append(String.format("  * Asiento #%02d (%s) : %s\n",
                    d.getNumeroAsiento(),
                    d.getTipoAsiento(),
                    CurrencyFormatter.format(d.getPrecioUnitario())));
        }
        sb.append("--------------------------------------------------\n");
        txtAreaTicket.setText(sb.toString());
        lblTotal.setText("Total: " + CurrencyFormatter.format(res.getCompra().getTotal()));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlEncabezado = new javax.swing.JPanel();
        lblEmpresa = new javax.swing.JLabel();
        lblRuc = new javax.swing.JLabel();
        lblTipoNumeroComprobante = new javax.swing.JLabel();
        lblFechaEmision = new javax.swing.JLabel();
        scrollTicket = new javax.swing.JScrollPane();
        txtAreaTicket = new javax.swing.JTextArea();
        pnlPie = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        pnlBotones = new javax.swing.JPanel();
        btnImprimir = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Via Costa - Boleto de Viaje");
        setMinimumSize(new java.awt.Dimension(460, 540));

        pnlEncabezado.setBackground(new java.awt.Color(15, 23, 42));
        pnlEncabezado.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlEncabezado.setLayout(new java.awt.GridLayout(4, 1, 0, 4));

        lblEmpresa.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblEmpresa.setForeground(new java.awt.Color(56, 189, 248));
        lblEmpresa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEmpresa.setText("Via Costa");
        pnlEncabezado.add(lblEmpresa);

        lblRuc.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblRuc.setForeground(new java.awt.Color(203, 213, 225));
        lblRuc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRuc.setText("RUC: 20601234567 - Chimbote");
        pnlEncabezado.add(lblRuc);

        lblTipoNumeroComprobante.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTipoNumeroComprobante.setForeground(new java.awt.Color(16, 185, 129));
        lblTipoNumeroComprobante.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTipoNumeroComprobante.setText("BOLETA: B001-000001");
        pnlEncabezado.add(lblTipoNumeroComprobante);

        lblFechaEmision.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblFechaEmision.setForeground(new java.awt.Color(148, 163, 184));
        lblFechaEmision.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFechaEmision.setText("Fecha: --/--/----");
        pnlEncabezado.add(lblFechaEmision);

        getContentPane().add(pnlEncabezado, java.awt.BorderLayout.PAGE_START);

        txtAreaTicket.setEditable(false);
        txtAreaTicket.setBackground(new java.awt.Color(30, 41, 59));
        txtAreaTicket.setColumns(20);
        txtAreaTicket.setFont(new java.awt.Font("Consolas", 0, 13)); // NOI18N
        txtAreaTicket.setForeground(new java.awt.Color(248, 250, 252));
        txtAreaTicket.setRows(12);
        txtAreaTicket.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 15, 12, 15));
        scrollTicket.setViewportView(txtAreaTicket);

        getContentPane().add(scrollTicket, java.awt.BorderLayout.CENTER);

        pnlPie.setBackground(new java.awt.Color(15, 23, 42));
        pnlPie.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 15, 15, 15));
        pnlPie.setLayout(new java.awt.GridLayout(2, 1, 0, 10));

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(16, 185, 129));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal.setText("Total: S/ 0.00");
        pnlPie.add(lblTotal);

        pnlBotones.setBackground(new java.awt.Color(15, 23, 42));
        pnlBotones.setLayout(new java.awt.GridLayout(1, 2, 12, 0));

        btnImprimir.setBackground(new java.awt.Color(2, 132, 199));
        btnImprimir.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnImprimir.setForeground(new java.awt.Color(255, 255, 255));
        btnImprimir.setText("Imprimir");
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });
        pnlBotones.add(btnImprimir);

        btnCerrar.setBackground(new java.awt.Color(71, 85, 105));
        btnCerrar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(255, 255, 255));
        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        pnlBotones.add(btnCerrar);

        pnlPie.add(pnlBotones);

        getContentPane().add(pnlPie, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        AlertHelper.showInfo("Imprimir", "", "Boleto enviado a imprimir.");
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JLabel lblEmpresa;
    private javax.swing.JLabel lblFechaEmision;
    private javax.swing.JLabel lblRuc;
    private javax.swing.JLabel lblTipoNumeroComprobante;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlPie;
    private javax.swing.JScrollPane scrollTicket;
    private javax.swing.JTextArea txtAreaTicket;
    // End of variables declaration//GEN-END:variables
}
