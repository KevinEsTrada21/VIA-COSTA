package com.viacosta.view;

import com.viacosta.model.Empleado;
import com.viacosta.service.AuthService;
import com.viacosta.util.FieldValidator;
import java.awt.Color;
import java.util.Optional;
import javax.swing.SwingUtilities;

public class LoginFrame extends javax.swing.JFrame {

    private final AuthService authService;

    public LoginFrame() {
        this.authService = new AuthService();
        initComponents();
        setLocationRelativeTo(null);
        FieldValidator.sinEspacios(txtUsuario, 25);
        FieldValidator.longitudMaxima(txtPassword, 30);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlIzquierdo = new javax.swing.JPanel();
        lblLogoTitulo = new javax.swing.JLabel();
        lblLogoSubtitulo = new javax.swing.JLabel();
        lblRutaPrincipal = new javax.swing.JLabel();
        pnlDerecho = new javax.swing.JPanel();
        lblBienvenida = new javax.swing.JLabel();
        lblSubBienvenida = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblMensaje = new javax.swing.JLabel();
        btnIngresar = new javax.swing.JButton();
        lblAccesoRapido = new javax.swing.JLabel();
        btnUsarAdmin = new javax.swing.JButton();
        btnUsarVendedor = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Via Costa - Iniciar Sesion");
        setMinimumSize(new java.awt.Dimension(820, 520));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridLayout(1, 2));

        pnlIzquierdo.setBackground(new java.awt.Color(15, 23, 42));
        pnlIzquierdo.setBorder(javax.swing.BorderFactory.createEmptyBorder(60, 35, 60, 35));
        pnlIzquierdo.setLayout(new java.awt.GridLayout(3, 1, 0, 15));

        lblLogoTitulo.setFont(new java.awt.Font("Segoe UI", 1, 32)); // NOI18N
        lblLogoTitulo.setForeground(new java.awt.Color(56, 189, 248));
        lblLogoTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogoTitulo.setText("VIA COSTA");
        pnlIzquierdo.add(lblLogoTitulo);

        lblLogoSubtitulo.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblLogoSubtitulo.setForeground(new java.awt.Color(203, 213, 225));
        lblLogoSubtitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogoSubtitulo.setText("Venta de Pasajes");
        pnlIzquierdo.add(lblLogoSubtitulo);

        lblRutaPrincipal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblRutaPrincipal.setForeground(new java.awt.Color(148, 163, 184));
        lblRutaPrincipal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRutaPrincipal.setText("Ruta: Chimbote - Huaraz");
        pnlIzquierdo.add(lblRutaPrincipal);

        getContentPane().add(pnlIzquierdo);

        pnlDerecho.setBackground(new java.awt.Color(30, 41, 59));
        pnlDerecho.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 40, 30, 40));
        pnlDerecho.setLayout(new java.awt.GridLayout(11, 1, 0, 6));

        lblBienvenida.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblBienvenida.setForeground(new java.awt.Color(248, 250, 252));
        lblBienvenida.setText("Iniciar Sesion");
        pnlDerecho.add(lblBienvenida);

        lblSubBienvenida.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblSubBienvenida.setForeground(new java.awt.Color(148, 163, 184));
        lblSubBienvenida.setText("Ingresa tu usuario y contrasena");
        pnlDerecho.add(lblSubBienvenida);

        lblUsuario.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblUsuario.setForeground(new java.awt.Color(226, 232, 240));
        lblUsuario.setText("Usuario:");
        pnlDerecho.add(lblUsuario);

        txtUsuario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtUsuario.setText("admin");
        pnlDerecho.add(txtUsuario);

        lblPassword.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblPassword.setForeground(new java.awt.Color(226, 232, 240));
        lblPassword.setText("Contrasena:");
        pnlDerecho.add(lblPassword);

        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPassword.setText("admin123");
        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });
        pnlDerecho.add(txtPassword);

        lblMensaje.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblMensaje.setForeground(new java.awt.Color(239, 68, 68));
        lblMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlDerecho.add(lblMensaje);

        btnIngresar.setBackground(new java.awt.Color(2, 132, 199));
        btnIngresar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnIngresar.setForeground(new java.awt.Color(255, 255, 255));
        btnIngresar.setText("Ingresar");
        btnIngresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIngresarActionPerformed(evt);
            }
        });
        pnlDerecho.add(btnIngresar);

        lblAccesoRapido.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblAccesoRapido.setForeground(new java.awt.Color(148, 163, 184));
        lblAccesoRapido.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAccesoRapido.setText("Usuarios de prueba:");
        pnlDerecho.add(lblAccesoRapido);

        btnUsarAdmin.setBackground(new java.awt.Color(51, 65, 85));
        btnUsarAdmin.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnUsarAdmin.setForeground(new java.awt.Color(248, 250, 252));
        btnUsarAdmin.setText("Admin (admin / admin123)");
        btnUsarAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsarAdminActionPerformed(evt);
            }
        });
        pnlDerecho.add(btnUsarAdmin);

        btnUsarVendedor.setBackground(new java.awt.Color(51, 65, 85));
        btnUsarVendedor.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnUsarVendedor.setForeground(new java.awt.Color(248, 250, 252));
        btnUsarVendedor.setText("Vendedor (vendedor / vendedor123)");
        btnUsarVendedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsarVendedorActionPerformed(evt);
            }
        });
        pnlDerecho.add(btnUsarVendedor);

        getContentPane().add(pnlDerecho);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnIngresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIngresarActionPerformed
        iniciarSesion();
    }//GEN-LAST:event_btnIngresarActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        iniciarSesion();
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void btnUsarAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsarAdminActionPerformed
        txtUsuario.setText("admin");
        txtPassword.setText("admin123");
    }//GEN-LAST:event_btnUsarAdminActionPerformed

    private void btnUsarVendedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsarVendedorActionPerformed
        txtUsuario.setText("vendedor");
        txtPassword.setText("vendedor123");
    }//GEN-LAST:event_btnUsarVendedorActionPerformed

    private void iniciarSesion() {
        String usuario = txtUsuario.getText() != null ? txtUsuario.getText().trim() : "";
        String pass = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || pass.isEmpty()) {
            lblMensaje.setForeground(new Color(239, 68, 68));
            lblMensaje.setText("Ingresa tu usuario y contrasena.");
            return;
        }

        btnIngresar.setEnabled(false);
        lblMensaje.setForeground(new Color(56, 189, 248));
        lblMensaje.setText("Ingresando...");

        new Thread(() -> {
            Optional<Empleado> empOpt = authService.autenticar(usuario, pass);
            SwingUtilities.invokeLater(() -> {
                btnIngresar.setEnabled(true);
                if (empOpt.isPresent()) {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                    this.dispose();
                } else {
                    lblMensaje.setForeground(new Color(239, 68, 68));
                    lblMensaje.setText("Usuario o contrasena incorrectos.");
                    txtPassword.setText("");
                    txtPassword.requestFocus();
                }
            });
        }).start();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIngresar;
    private javax.swing.JButton btnUsarAdmin;
    private javax.swing.JButton btnUsarVendedor;
    private javax.swing.JLabel lblAccesoRapido;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JLabel lblLogoSubtitulo;
    private javax.swing.JLabel lblLogoTitulo;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRutaPrincipal;
    private javax.swing.JLabel lblSubBienvenida;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnlDerecho;
    private javax.swing.JPanel pnlIzquierdo;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
