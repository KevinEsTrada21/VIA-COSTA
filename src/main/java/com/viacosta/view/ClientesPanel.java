package com.viacosta.view;

import com.viacosta.model.Cliente;
import com.viacosta.service.ClienteService;
import com.viacosta.util.AlertHelper;
import com.viacosta.util.FieldValidator;
import com.viacosta.util.Validators;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class ClientesPanel extends javax.swing.JPanel {

    private final ClienteService clienteService;
    private final DefaultTableModel modeloClientes;
    private final List<Cliente> clientesEnMemoria = new ArrayList<>();
    private Cliente clienteSeleccionado;

    public ClientesPanel() {
        this.clienteService = new ClienteService();
        initComponents();

        modeloClientes = (DefaultTableModel) tblClientes.getModel();

        configurarValidacionesCampos();

        tblClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tblClientes.getSelectedRow();
                if (fila >= 0 && fila < clientesEnMemoria.size()) {
                    clienteSeleccionado = clientesEnMemoria.get(fila);
                    mostrarEnFormulario(clienteSeleccionado);
                    btnEliminar.setEnabled(true);
                } else {
                    clienteSeleccionado = null;
                    btnEliminar.setEnabled(false);
                }
            }
        });

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                cargarClientes(txtBuscar.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                cargarClientes(txtBuscar.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                cargarClientes(txtBuscar.getText());
            }
        });

        cargarClientes("");
    }

    private void configurarValidacionesCampos() {
        FieldValidator.soloNumeros(txtDni, 8);
        FieldValidator.soloLetras(txtNombre, 40);
        FieldValidator.soloLetras(txtApellido, 40);
        FieldValidator.soloNumeros(txtTelefono, 9);
        FieldValidator.sinEspacios(txtCorreo, 50);
        FieldValidator.longitudMaxima(txtDireccion, 80);
        FieldValidator.longitudMaxima(txtBuscar, 50);
    }

    public final void cargarClientes(String filtro) {
        new Thread(() -> {
            List<Cliente> lista = clienteService.listarClientes(filtro);
            SwingUtilities.invokeLater(() -> {
                clientesEnMemoria.clear();
                clientesEnMemoria.addAll(lista);
                modeloClientes.setRowCount(0);
                for (Cliente c : lista) {
                    modeloClientes.addRow(new Object[]{
                        c.getDni(),
                        c.getNombre(),
                        c.getApellido(),
                        c.getTelefono() != null ? c.getTelefono() : "-",
                        c.getCorreo() != null ? c.getCorreo() : "-",
                        c.getDireccion() != null ? c.getDireccion() : "-"
                    });
                }
                lblTotalClientes.setText("Total: " + lista.size());
            });
        }).start();
    }

    private void mostrarEnFormulario(Cliente c) {
        txtDni.setText(c.getDni());
        txtNombre.setText(c.getNombre());
        txtApellido.setText(c.getApellido());
        txtTelefono.setText(c.getTelefono() != null ? c.getTelefono() : "");
        txtCorreo.setText(c.getCorreo() != null ? c.getCorreo() : "");
        txtDireccion.setText(c.getDireccion() != null ? c.getDireccion() : "");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlIzquierdo = new javax.swing.JPanel();
        pnlBusqueda = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        lblTotalClientes = new javax.swing.JLabel();
        scrollClientes = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        pnlFormulario = new javax.swing.JPanel();
        lblTituloForm = new javax.swing.JLabel();
        lblDni = new javax.swing.JLabel();
        pnlDniRow = new javax.swing.JPanel();
        txtDni = new javax.swing.JTextField();
        btnConsultarReniec = new javax.swing.JButton();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblApellido = new javax.swing.JLabel();
        txtApellido = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        lblCorreo = new javax.swing.JLabel();
        txtCorreo = new javax.swing.JTextField();
        lblDireccion = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        pnlBotonesForm = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();

        setBackground(new java.awt.Color(15, 23, 42));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setLayout(new java.awt.BorderLayout(15, 0));

        pnlIzquierdo.setBackground(new java.awt.Color(15, 23, 42));
        pnlIzquierdo.setLayout(new java.awt.BorderLayout(0, 12));

        pnlBusqueda.setBackground(new java.awt.Color(30, 41, 59));
        pnlBusqueda.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        pnlBusqueda.setLayout(new java.awt.BorderLayout(12, 0));

        lblBuscar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblBuscar.setForeground(new java.awt.Color(226, 232, 240));
        lblBuscar.setText("Buscar:");
        pnlBusqueda.add(lblBuscar, java.awt.BorderLayout.LINE_START);

        txtBuscar.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlBusqueda.add(txtBuscar, java.awt.BorderLayout.CENTER);

        lblTotalClientes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalClientes.setForeground(new java.awt.Color(56, 189, 248));
        lblTotalClientes.setText("Total: 0");
        pnlBusqueda.add(lblTotalClientes, java.awt.BorderLayout.LINE_END);

        pnlIzquierdo.add(pnlBusqueda, java.awt.BorderLayout.PAGE_START);

        tblClientes.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "DNI", "Nombres", "Apellidos", "Telefono", "Correo", "Direccion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblClientes.setRowHeight(26);
        scrollClientes.setViewportView(tblClientes);

        pnlIzquierdo.add(scrollClientes, java.awt.BorderLayout.CENTER);

        add(pnlIzquierdo, java.awt.BorderLayout.CENTER);

        pnlFormulario.setBackground(new java.awt.Color(30, 41, 59));
        pnlFormulario.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20));
        pnlFormulario.setPreferredSize(new java.awt.Dimension(340, 500));
        pnlFormulario.setLayout(new java.awt.GridLayout(14, 1, 0, 4));

        lblTituloForm.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTituloForm.setForeground(new java.awt.Color(56, 189, 248));
        lblTituloForm.setText("Datos del Cliente");
        pnlFormulario.add(lblTituloForm);

        lblDni.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDni.setForeground(new java.awt.Color(203, 213, 225));
        lblDni.setText("DNI:");
        pnlFormulario.add(lblDni);

        pnlDniRow.setBackground(new java.awt.Color(30, 41, 59));
        pnlDniRow.setLayout(new java.awt.BorderLayout(8, 0));

        txtDni.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlDniRow.add(txtDni, java.awt.BorderLayout.CENTER);

        btnConsultarReniec.setBackground(new java.awt.Color(2, 132, 199));
        btnConsultarReniec.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnConsultarReniec.setForeground(new java.awt.Color(255, 255, 255));
        btnConsultarReniec.setText("Buscar");
        btnConsultarReniec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarReniecActionPerformed(evt);
            }
        });
        pnlDniRow.add(btnConsultarReniec, java.awt.BorderLayout.LINE_END);

        pnlFormulario.add(pnlDniRow);

        lblNombre.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNombre.setForeground(new java.awt.Color(203, 213, 225));
        lblNombre.setText("Nombres:");
        pnlFormulario.add(lblNombre);

        txtNombre.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlFormulario.add(txtNombre);

        lblApellido.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblApellido.setForeground(new java.awt.Color(203, 213, 225));
        lblApellido.setText("Apellidos:");
        pnlFormulario.add(lblApellido);

        txtApellido.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlFormulario.add(txtApellido);

        lblTelefono.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTelefono.setForeground(new java.awt.Color(203, 213, 225));
        lblTelefono.setText("Telefono:");
        pnlFormulario.add(lblTelefono);

        txtTelefono.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlFormulario.add(txtTelefono);

        lblCorreo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblCorreo.setForeground(new java.awt.Color(203, 213, 225));
        lblCorreo.setText("Correo:");
        pnlFormulario.add(lblCorreo);

        txtCorreo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlFormulario.add(txtCorreo);

        lblDireccion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDireccion.setForeground(new java.awt.Color(203, 213, 225));
        lblDireccion.setText("Direccion:");
        pnlFormulario.add(lblDireccion);

        txtDireccion.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlFormulario.add(txtDireccion);

        pnlBotonesForm.setBackground(new java.awt.Color(30, 41, 59));
        pnlBotonesForm.setLayout(new java.awt.GridLayout(1, 3, 6, 0));

        btnGuardar.setBackground(new java.awt.Color(16, 185, 129));
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        pnlBotonesForm.add(btnGuardar);

        btnEliminar.setBackground(new java.awt.Color(239, 68, 68));
        btnEliminar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setText("Eliminar");
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        pnlBotonesForm.add(btnEliminar);

        btnLimpiar.setBackground(new java.awt.Color(71, 85, 105));
        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });
        pnlBotonesForm.add(btnLimpiar);

        pnlFormulario.add(pnlBotonesForm);

        add(pnlFormulario, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void btnConsultarReniecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarReniecActionPerformed
        String dni = txtDni.getText() != null ? txtDni.getText().trim() : "";
        if (!Validators.esDniValido(dni)) {
            AlertHelper.showWarning("Aviso", "", "Ingresa un DNI de 8 digitos.");
            txtDni.requestFocus();
            return;
        }

        btnConsultarReniec.setEnabled(false);
        btnConsultarReniec.setText("...");

        new Thread(() -> {
            Optional<Cliente> opt = clienteService.buscarPorDni(dni);
            if (opt.isEmpty()) {
                opt = clienteService.consultarEnReniec(dni);
            }

            final Optional<Cliente> finalOpt = opt;
            SwingUtilities.invokeLater(() -> {
                btnConsultarReniec.setEnabled(true);
                btnConsultarReniec.setText("Buscar");
                if (finalOpt.isPresent()) {
                    Cliente c = finalOpt.get();
                    txtNombre.setText(c.getNombre());
                    txtApellido.setText(c.getApellido());
                    if (c.getTelefono() != null) txtTelefono.setText(c.getTelefono());
                    if (c.getCorreo() != null) txtCorreo.setText(c.getCorreo());
                    if (c.getDireccion() != null) txtDireccion.setText(c.getDireccion());
                } else {
                    AlertHelper.showInfo("Busqueda", "", "Ingresa los nombres y apellidos manualmente.");
                }
            });
        }).start();
    }//GEN-LAST:event_btnConsultarReniecActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        String dni = txtDni.getText() != null ? txtDni.getText().trim() : "";
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String apellido = txtApellido.getText() != null ? txtApellido.getText().trim() : "";

        if (!Validators.esDniValido(dni)) {
            AlertHelper.showWarning("Aviso", "", "El DNI debe tener 8 digitos.");
            txtDni.requestFocus();
            return;
        }
        if (nombre.isEmpty() || apellido.isEmpty()) {
            AlertHelper.showWarning("Aviso", "", "Ingresa los nombres y apellidos del cliente.");
            txtNombre.requestFocus();
            return;
        }

        Cliente cliente = new Cliente(
                clienteSeleccionado != null ? clienteSeleccionado.getIdCliente() : 0,
                dni,
                nombre,
                apellido,
                txtTelefono.getText(),
                txtDireccion.getText(),
                txtCorreo.getText()
        );

        btnGuardar.setEnabled(false);
        new Thread(() -> {
            try {
                clienteService.guardarCliente(cliente);
                SwingUtilities.invokeLater(() -> {
                    btnGuardar.setEnabled(true);
                    AlertHelper.showInfo("Guardado", "", "Cliente guardado correctamente.");
                    limpiarFormulario();
                    cargarClientes(txtBuscar.getText());
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    btnGuardar.setEnabled(true);
                    AlertHelper.showError("Error", "", e.getMessage());
                });
            }
        }).start();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if (clienteSeleccionado == null) return;

        boolean confirmar = AlertHelper.showConfirmation("Eliminar",
                "¿Deseas eliminar a " + clienteSeleccionado.getNombreCompleto() + "?", "");
        if (confirmar) {
            new Thread(() -> {
                boolean ok = clienteService.eliminarCliente(clienteSeleccionado.getIdCliente());
                SwingUtilities.invokeLater(() -> {
                    if (ok) {
                        AlertHelper.showInfo("Eliminado", "", "Cliente eliminado.");
                        limpiarFormulario();
                        cargarClientes(txtBuscar.getText());
                    } else {
                        AlertHelper.showError("Error", "", "No se puede eliminar porque ya tiene pasajes registrados.");
                    }
                });
            }).start();
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarFormulario();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void limpiarFormulario() {
        clienteSeleccionado = null;
        tblClientes.clearSelection();
        txtDni.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        btnEliminar.setEnabled(false);
        txtDni.requestFocus();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConsultarReniec;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTituloForm;
    private javax.swing.JLabel lblTotalClientes;
    private javax.swing.JPanel pnlBotonesForm;
    private javax.swing.JPanel pnlBusqueda;
    private javax.swing.JPanel pnlDniRow;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlIzquierdo;
    private javax.swing.JScrollPane scrollClientes;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
