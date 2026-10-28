package com.viacosta.view;

import com.viacosta.model.Empleado;
import com.viacosta.util.AlertHelper;
import com.viacosta.util.SessionManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MainFrame extends javax.swing.JFrame {

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private Timer clockTimer;

    public MainFrame() {
        initComponents();
        setLocationRelativeTo(null);
        configurarDatosUsuario();
        iniciarReloj();
        mostrarVentas();
    }

    private void configurarDatosUsuario() {
        Empleado usuario = SessionManager.getUsuarioActual();
        if (usuario != null) {
            lblNombreUsuario.setText(usuario.getNombreCompleto());
            lblRolUsuario.setText(usuario.getNombreRol() != null ? usuario.getNombreRol() : "Vendedor");
        } else {
            lblNombreUsuario.setText("Usuario");
            lblRolUsuario.setText("Vendedor");
        }
    }

    private void iniciarReloj() {
        lblReloj.setText(LocalDateTime.now().format(timeFormatter));
        clockTimer = new Timer(1000, e -> lblReloj.setText(LocalDateTime.now().format(timeFormatter)));
        clockTimer.start();
    }

    private void resaltarBoton(JButton activo) {
        JButton[] botones = {btnNavVentas, btnNavClientes, btnNavViajes, btnNavHistorial};
        for (JButton b : botones) {
            if (b == activo) {
                b.setBackground(new Color(2, 132, 199));
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(new Color(30, 41, 59));
                b.setForeground(new Color(203, 213, 225));
            }
        }
    }

    private void cambiarPanelCentral(JPanel panel) {
        pnlContenedorCentral.removeAll();
        pnlContenedorCentral.add(panel, BorderLayout.CENTER);
        pnlContenedorCentral.revalidate();
        pnlContenedorCentral.repaint();
    }

    public final void mostrarVentas() {
        resaltarBoton(btnNavVentas);
        lblTituloSeccion.setText("Venta de Pasajes");
        lblSubtituloSeccion.setText("Selecciona un horario, elige los asientos y registra al pasajero");
        cambiarPanelCentral(new VentasPanel());
    }

    public final void mostrarClientes() {
        resaltarBoton(btnNavClientes);
        lblTituloSeccion.setText("Clientes");
        lblSubtituloSeccion.setText("Busca, registra o actualiza los datos de los pasajeros");
        cambiarPanelCentral(new ClientesPanel());
    }

    public final void mostrarViajes() {
        resaltarBoton(btnNavViajes);
        lblTituloSeccion.setText("Horarios y Buses");
        lblSubtituloSeccion.setText("Lista de salidas programadas, rutas y buses");
        cambiarPanelCentral(new ViajesPanel());
    }

    public final void mostrarHistorial() {
        resaltarBoton(btnNavHistorial);
        lblTituloSeccion.setText("Historial de Ventas");
        lblSubtituloSeccion.setText("Boletas emitidas y pasajes vendidos");
        cambiarPanelCentral(new HistorialVentasPanel());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSidebar = new javax.swing.JPanel();
        pnlBrand = new javax.swing.JPanel();
        lblBrandTitle = new javax.swing.JLabel();
        lblBrandSubtitle = new javax.swing.JLabel();
        pnlMenu = new javax.swing.JPanel();
        btnNavVentas = new javax.swing.JButton();
        btnNavClientes = new javax.swing.JButton();
        btnNavViajes = new javax.swing.JButton();
        btnNavHistorial = new javax.swing.JButton();
        pnlUsuario = new javax.swing.JPanel();
        lblNombreUsuario = new javax.swing.JLabel();
        lblRolUsuario = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        pnlDerecho = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        pnlTitulos = new javax.swing.JPanel();
        lblTituloSeccion = new javax.swing.JLabel();
        lblSubtituloSeccion = new javax.swing.JLabel();
        lblReloj = new javax.swing.JLabel();
        pnlContenedorCentral = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Via Costa - Venta de Pasajes");
        setMinimumSize(new java.awt.Dimension(1180, 740));

        pnlSidebar.setBackground(new java.awt.Color(15, 23, 42));
        pnlSidebar.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 15, 20, 15));
        pnlSidebar.setPreferredSize(new java.awt.Dimension(250, 720));
        pnlSidebar.setLayout(new java.awt.BorderLayout(0, 25));

        pnlBrand.setBackground(new java.awt.Color(15, 23, 42));
        pnlBrand.setLayout(new java.awt.GridLayout(2, 1, 0, 4));

        lblBrandTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblBrandTitle.setForeground(new java.awt.Color(56, 189, 248));
        lblBrandTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBrandTitle.setText("VIA COSTA");
        pnlBrand.add(lblBrandTitle);

        lblBrandSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblBrandSubtitle.setForeground(new java.awt.Color(148, 163, 184));
        lblBrandSubtitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBrandSubtitle.setText("Venta de Pasajes");
        pnlBrand.add(lblBrandSubtitle);

        pnlSidebar.add(pnlBrand, java.awt.BorderLayout.PAGE_START);

        pnlMenu.setBackground(new java.awt.Color(15, 23, 42));
        pnlMenu.setLayout(new java.awt.GridLayout(6, 1, 0, 10));

        btnNavVentas.setBackground(new java.awt.Color(2, 132, 199));
        btnNavVentas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnNavVentas.setForeground(new java.awt.Color(255, 255, 255));
        btnNavVentas.setText("Venta de Pasajes");
        btnNavVentas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNavVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavVentasActionPerformed(evt);
            }
        });
        pnlMenu.add(btnNavVentas);

        btnNavClientes.setBackground(new java.awt.Color(30, 41, 59));
        btnNavClientes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnNavClientes.setForeground(new java.awt.Color(203, 213, 225));
        btnNavClientes.setText("Clientes");
        btnNavClientes.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNavClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavClientesActionPerformed(evt);
            }
        });
        pnlMenu.add(btnNavClientes);

        btnNavViajes.setBackground(new java.awt.Color(30, 41, 59));
        btnNavViajes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnNavViajes.setForeground(new java.awt.Color(203, 213, 225));
        btnNavViajes.setText("Horarios y Buses");
        btnNavViajes.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNavViajes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavViajesActionPerformed(evt);
            }
        });
        pnlMenu.add(btnNavViajes);

        btnNavHistorial.setBackground(new java.awt.Color(30, 41, 59));
        btnNavHistorial.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnNavHistorial.setForeground(new java.awt.Color(203, 213, 225));
        btnNavHistorial.setText("Historial de Ventas");
        btnNavHistorial.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNavHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavHistorialActionPerformed(evt);
            }
        });
        pnlMenu.add(btnNavHistorial);

        pnlSidebar.add(pnlMenu, java.awt.BorderLayout.CENTER);

        pnlUsuario.setBackground(new java.awt.Color(30, 41, 59));
        pnlUsuario.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        pnlUsuario.setLayout(new java.awt.GridLayout(3, 1, 0, 6));

        lblNombreUsuario.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblNombreUsuario.setForeground(new java.awt.Color(248, 250, 252));
        lblNombreUsuario.setText("Usuario");
        pnlUsuario.add(lblNombreUsuario);

        lblRolUsuario.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblRolUsuario.setForeground(new java.awt.Color(56, 189, 248));
        lblRolUsuario.setText("Administrador");
        pnlUsuario.add(lblRolUsuario);

        btnCerrarSesion.setBackground(new java.awt.Color(220, 38, 38));
        btnCerrarSesion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCerrarSesion.setForeground(new java.awt.Color(255, 255, 255));
        btnCerrarSesion.setText("Cerrar Sesion");
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });
        pnlUsuario.add(btnCerrarSesion);

        pnlSidebar.add(pnlUsuario, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(pnlSidebar, java.awt.BorderLayout.LINE_START);

        pnlDerecho.setBackground(new java.awt.Color(15, 23, 42));
        pnlDerecho.setLayout(new java.awt.BorderLayout());

        pnlHeader.setBackground(new java.awt.Color(30, 41, 59));
        pnlHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 20, 12, 20));
        pnlHeader.setLayout(new java.awt.BorderLayout());

        pnlTitulos.setBackground(new java.awt.Color(30, 41, 59));
        pnlTitulos.setLayout(new java.awt.GridLayout(2, 1, 0, 2));

        lblTituloSeccion.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTituloSeccion.setForeground(new java.awt.Color(248, 250, 252));
        lblTituloSeccion.setText("Venta de Pasajes");
        pnlTitulos.add(lblTituloSeccion);

        lblSubtituloSeccion.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblSubtituloSeccion.setForeground(new java.awt.Color(148, 163, 184));
        lblSubtituloSeccion.setText("Selecciona un horario, elige los asientos y registra al pasajero");
        pnlTitulos.add(lblSubtituloSeccion);

        pnlHeader.add(pnlTitulos, java.awt.BorderLayout.CENTER);

        lblReloj.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        lblReloj.setForeground(new java.awt.Color(203, 213, 225));
        lblReloj.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblReloj.setText("--/--/---- --:--:--");
        pnlHeader.add(lblReloj, java.awt.BorderLayout.LINE_END);

        pnlDerecho.add(pnlHeader, java.awt.BorderLayout.PAGE_START);

        pnlContenedorCentral.setBackground(new java.awt.Color(15, 23, 42));
        pnlContenedorCentral.setLayout(new java.awt.BorderLayout());
        pnlDerecho.add(pnlContenedorCentral, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlDerecho, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNavVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavVentasActionPerformed
        mostrarVentas();
    }//GEN-LAST:event_btnNavVentasActionPerformed

    private void btnNavClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavClientesActionPerformed
        mostrarClientes();
    }//GEN-LAST:event_btnNavClientesActionPerformed

    private void btnNavViajesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavViajesActionPerformed
        mostrarViajes();
    }//GEN-LAST:event_btnNavViajesActionPerformed

    private void btnNavHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavHistorialActionPerformed
        mostrarHistorial();
    }//GEN-LAST:event_btnNavHistorialActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        boolean confirmar = AlertHelper.showConfirmation("Cerrar Sesion", "¿Deseas cerrar sesion?", "");
        if (confirmar) {
            if (clockTimer != null) {
                clockTimer.stop();
            }
            SessionManager.cerrarSesion();
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnNavClientes;
    private javax.swing.JButton btnNavHistorial;
    private javax.swing.JButton btnNavVentas;
    private javax.swing.JButton btnNavViajes;
    private javax.swing.JLabel lblBrandSubtitle;
    private javax.swing.JLabel lblBrandTitle;
    private javax.swing.JLabel lblNombreUsuario;
    private javax.swing.JLabel lblReloj;
    private javax.swing.JLabel lblRolUsuario;
    private javax.swing.JLabel lblSubtituloSeccion;
    private javax.swing.JLabel lblTituloSeccion;
    private javax.swing.JPanel pnlBrand;
    private javax.swing.JPanel pnlContenedorCentral;
    private javax.swing.JPanel pnlDerecho;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlMenu;
    private javax.swing.JPanel pnlSidebar;
    private javax.swing.JPanel pnlTitulos;
    private javax.swing.JPanel pnlUsuario;
    // End of variables declaration//GEN-END:variables
}
