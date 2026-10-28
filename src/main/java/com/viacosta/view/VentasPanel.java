package com.viacosta.view;

import com.viacosta.model.AsientoEstadoFecha;
import com.viacosta.model.AsignacionBusRuta;
import com.viacosta.model.Cliente;
import com.viacosta.model.Empleado;
import com.viacosta.service.ClienteService;
import com.viacosta.service.VentaService;
import com.viacosta.service.ViajeService;
import com.viacosta.util.AlertHelper;
import com.viacosta.util.CurrencyFormatter;
import com.viacosta.util.FieldValidator;
import com.viacosta.util.SessionManager;
import com.viacosta.util.Validators;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class VentasPanel extends javax.swing.JPanel {

    private final ViajeService viajeService;
    private final ClienteService clienteService;
    private final VentaService ventaService;

    private final DefaultTableModel modeloSalidas;
    private final List<AsignacionBusRuta> salidasEnMemoria = new ArrayList<>();
    private AsignacionBusRuta salidaActual;
    private final List<AsientoEstadoFecha> asientosSeleccionados = new ArrayList<>();

    public VentasPanel() {
        this.viajeService = new ViajeService();
        this.clienteService = new ClienteService();
        this.ventaService = new VentaService();
        initComponents();

        modeloSalidas = (DefaultTableModel) tblSalidas.getModel();
        txtFechaViaje.setText(LocalDate.now().toString());

        configurarValidacionesCampos();

        tblSalidas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tblSalidas.getSelectedRow();
                if (fila >= 0 && fila < salidasEnMemoria.size()) {
                    seleccionarSalida(salidasEnMemoria.get(fila));
                }
            }
        });

        cargarSalidas();
    }

    private void configurarValidacionesCampos() {
        FieldValidator.soloNumeros(txtDni, 8);
        FieldValidator.soloLetras(txtNombre, 40);
        FieldValidator.soloLetras(txtApellido, 40);
        FieldValidator.soloNumeros(txtTelefono, 9);
        FieldValidator.sinEspacios(txtCorreo, 50);
        FieldValidator.longitudMaxima(txtDireccion, 80);
    }

    public final void cargarSalidas() {
        List<AsignacionBusRuta> lista = viajeService.listarSalidasVigentes();
        llenarTablaSalidas(lista);
    }

    private void llenarTablaSalidas(List<AsignacionBusRuta> lista) {
        salidasEnMemoria.clear();
        salidasEnMemoria.addAll(lista);
        modeloSalidas.setRowCount(0);

        for (AsignacionBusRuta s : lista) {
            modeloSalidas.addRow(new Object[]{
                s.getFechaSalidaFormateada(),
                s.getHoraSalidaFormateada(),
                s.getRutaCompleta(),
                s.getPlacaBus(),
                s.getAsientosDisponibles() + " libres"
            });
        }

        if (!salidasEnMemoria.isEmpty()) {
            tblSalidas.setRowSelectionInterval(0, 0);
        } else {
            salidaActual = null;
            lblSalidaSeleccionada.setText("No hay salidas para esta fecha.");
            pnlGrillaAsientos.removeAll();
            pnlGrillaAsientos.revalidate();
            pnlGrillaAsientos.repaint();
            asientosSeleccionados.clear();
            actualizarTotales();
        }
    }

    private void seleccionarSalida(AsignacionBusRuta salida) {
        this.salidaActual = salida;
        lblSalidaSeleccionada.setText(salida.getRutaCompleta()
                + " | " + salida.getFechaSalidaFormateada()
                + " - " + salida.getHoraSalidaFormateada()
                + " | Bus: " + salida.getPlacaBus());
        asientosSeleccionados.clear();
        actualizarTotales();
        renderizarMapaAsientos(salida.getIdAsignacion(), salida.getIdBus());
    }

    private void renderizarMapaAsientos(int idAsignacion, int idBus) {
        pnlGrillaAsientos.removeAll();
        List<AsientoEstadoFecha> lista = viajeService.obtenerAsientosParaViaje(idAsignacion, idBus);

        Map<Integer, AsientoEstadoFecha> mapaPorNumero = new HashMap<>();
        for (AsientoEstadoFecha a : lista) {
            mapaPorNumero.put(a.getNumeroAsiento(), a);
        }

        int numeroAsiento = 1;
        for (int fila = 0; fila < 8; fila++) {
            if (numeroAsiento <= 30) {
                pnlGrillaAsientos.add(crearBotonAsiento(mapaPorNumero.get(numeroAsiento++)));
            } else {
                pnlGrillaAsientos.add(new JLabel(""));
            }

            if (numeroAsiento <= 30) {
                pnlGrillaAsientos.add(crearBotonAsiento(mapaPorNumero.get(numeroAsiento++)));
            } else {
                pnlGrillaAsientos.add(new JLabel(""));
            }

            JLabel lblPasillo = new JLabel(fila == 0 ? "PASILLO" : "|", SwingConstants.CENTER);
            lblPasillo.setForeground(new Color(100, 116, 139));
            lblPasillo.setFont(new Font("Segoe UI", Font.BOLD, 10));
            pnlGrillaAsientos.add(lblPasillo);

            if (numeroAsiento <= 30) {
                pnlGrillaAsientoSegunExistencia(mapaPorNumero.get(numeroAsiento++));
            } else {
                pnlGrillaAsientos.add(new JLabel(""));
            }

            if (numeroAsiento <= 30) {
                pnlGrillaAsientoSegunExistencia(mapaPorNumero.get(numeroAsiento++));
            } else {
                pnlGrillaAsientos.add(new JLabel(""));
            }
        }

        pnlGrillaAsientos.revalidate();
        pnlGrillaAsientos.repaint();
    }

    private void pnlGrillaAsientoSegunExistencia(AsientoEstadoFecha aef) {
        if (aef != null) {
            pnlGrillaAsientos.add(crearBotonAsiento(aef));
        } else {
            pnlGrillaAsientos.add(new JLabel(""));
        }
    }

    private JButton crearBotonAsiento(AsientoEstadoFecha aef) {
        JButton btn = new JButton();
        if (aef == null) {
            btn.setEnabled(false);
            return btn;
        }

        btn.setText(String.format("%02d", aef.getNumeroAsiento()));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(52, 36));
        btn.setToolTipText("Asiento #" + aef.getNumeroAsiento() + " - S/ " + aef.getPrecioFinal());

        if (aef.isOcupado()) {
            btn.setBackground(new Color(220, 38, 38));
            btn.setEnabled(false);
        } else {
            btn.setBackground(new Color(16, 185, 129));
            btn.addActionListener(e -> {
                if (asientosSeleccionados.contains(aef)) {
                    asientosSeleccionados.remove(aef);
                    btn.setBackground(new Color(16, 185, 129));
                } else {
                    asientosSeleccionados.add(aef);
                    btn.setBackground(new Color(2, 132, 199));
                }
                actualizarTotales();
            });
        }
        return btn;
    }

    private void actualizarTotales() {
        if (asientosSeleccionados.isEmpty()) {
            lblAsientosSeleccionados.setText("Asientos: Ninguno");
            lblImporteTotal.setText("Total: S/ 0.00");
            btnEmitirBoleto.setEnabled(false);
        } else {
            StringBuilder sb = new StringBuilder("Asientos: ");
            BigDecimal total = BigDecimal.ZERO;
            for (int i = 0; i < asientosSeleccionados.size(); i++) {
                AsientoEstadoFecha a = asientosSeleccionados.get(i);
                if (i > 0) sb.append(", ");
                sb.append("#").append(a.getNumeroAsiento());
                total = total.add(a.getPrecioFinal());
            }
            lblAsientosSeleccionados.setText(sb.toString());
            lblImporteTotal.setText("Total: " + CurrencyFormatter.format(total));
            btnEmitirBoleto.setEnabled(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSuperior = new javax.swing.JPanel();
        pnlFiltros = new javax.swing.JPanel();
        lblRutaFiltro = new javax.swing.JLabel();
        cmbRutas = new javax.swing.JComboBox<>();
        lblFechaFiltro = new javax.swing.JLabel();
        txtFechaViaje = new javax.swing.JTextField();
        btnFiltrar = new javax.swing.JButton();
        btnVerTodas = new javax.swing.JButton();
        scrollSalidas = new javax.swing.JScrollPane();
        tblSalidas = new javax.swing.JTable();
        pnlCentral = new javax.swing.JPanel();
        pnlCabina = new javax.swing.JPanel();
        lblSalidaSeleccionada = new javax.swing.JLabel();
        pnlGrillaAsientos = new javax.swing.JPanel();
        lblLeyenda = new javax.swing.JLabel();
        pnlFormularioVenta = new javax.swing.JPanel();
        lblTituloPasajero = new javax.swing.JLabel();
        lblDni = new javax.swing.JLabel();
        pnlDniBusqueda = new javax.swing.JPanel();
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
        pnlComprobantePago = new javax.swing.JPanel();
        cmbTipoComprobante = new javax.swing.JComboBox<>();
        cmbMetodoPago = new javax.swing.JComboBox<>();
        lblAsientosSeleccionados = new javax.swing.JLabel();
        lblImporteTotal = new javax.swing.JLabel();
        pnlBotonesAccion = new javax.swing.JPanel();
        btnLimpiar = new javax.swing.JButton();
        btnEmitirBoleto = new javax.swing.JButton();

        setBackground(new java.awt.Color(15, 23, 42));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setLayout(new java.awt.BorderLayout(12, 12));

        pnlSuperior.setBackground(new java.awt.Color(30, 41, 59));
        pnlSuperior.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));
        pnlSuperior.setPreferredSize(new java.awt.Dimension(900, 165));
        pnlSuperior.setLayout(new java.awt.BorderLayout(0, 8));

        pnlFiltros.setBackground(new java.awt.Color(30, 41, 59));
        pnlFiltros.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 2));

        lblRutaFiltro.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblRutaFiltro.setForeground(new java.awt.Color(226, 232, 240));
        lblRutaFiltro.setText("Ruta:");
        pnlFiltros.add(lblRutaFiltro);

        cmbRutas.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        cmbRutas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todas", "Chimbote -> Huaraz", "Huaraz -> Chimbote" }));
        cmbRutas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbRutasActionPerformed(evt);
            }
        });
        pnlFiltros.add(cmbRutas);

        lblFechaFiltro.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblFechaFiltro.setForeground(new java.awt.Color(226, 232, 240));
        lblFechaFiltro.setText("Fecha:");
        pnlFiltros.add(lblFechaFiltro);

        txtFechaViaje.setColumns(10);
        txtFechaViaje.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlFiltros.add(txtFechaViaje);

        btnFiltrar.setBackground(new java.awt.Color(2, 132, 199));
        btnFiltrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnFiltrar.setForeground(new java.awt.Color(255, 255, 255));
        btnFiltrar.setText("Buscar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });
        pnlFiltros.add(btnFiltrar);

        btnVerTodas.setBackground(new java.awt.Color(71, 85, 105));
        btnVerTodas.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnVerTodas.setForeground(new java.awt.Color(255, 255, 255));
        btnVerTodas.setText("Ver Todos");
        btnVerTodas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerTodasActionPerformed(evt);
            }
        });
        pnlFiltros.add(btnVerTodas);

        pnlSuperior.add(pnlFiltros, java.awt.BorderLayout.PAGE_START);

        tblSalidas.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblSalidas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "Hora", "Ruta", "Bus", "Asientos Libres"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSalidas.setRowHeight(24);
        scrollSalidas.setViewportView(tblSalidas);

        pnlSuperior.add(scrollSalidas, java.awt.BorderLayout.CENTER);

        add(pnlSuperior, java.awt.BorderLayout.PAGE_START);

        pnlCentral.setBackground(new java.awt.Color(15, 23, 42));
        pnlCentral.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        pnlCabina.setBackground(new java.awt.Color(30, 41, 59));
        pnlCabina.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 15, 12, 15));
        pnlCabina.setLayout(new java.awt.BorderLayout(0, 10));

        lblSalidaSeleccionada.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSalidaSeleccionada.setForeground(new java.awt.Color(56, 189, 248));
        lblSalidaSeleccionada.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSalidaSeleccionada.setText("Selecciona un horario de la tabla");
        pnlCabina.add(lblSalidaSeleccionada, java.awt.BorderLayout.PAGE_START);

        pnlGrillaAsientos.setBackground(new java.awt.Color(15, 23, 42));
        pnlGrillaAsientos.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        pnlGrillaAsientos.setLayout(new java.awt.GridLayout(8, 5, 8, 6));
        pnlCabina.add(pnlGrillaAsientos, java.awt.BorderLayout.CENTER);

        lblLeyenda.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblLeyenda.setForeground(new java.awt.Color(203, 213, 225));
        lblLeyenda.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLeyenda.setText("[Verde = Libre]    [Azul = Seleccionado]    [Rojo = Ocupado]   (1-10: S/ 50 | 11-30: S/ 35)");
        pnlCabina.add(lblLeyenda, java.awt.BorderLayout.PAGE_END);

        pnlCentral.add(pnlCabina);

        pnlFormularioVenta.setBackground(new java.awt.Color(30, 41, 59));
        pnlFormularioVenta.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 18, 12, 18));
        pnlFormularioVenta.setLayout(new java.awt.GridLayout(17, 1, 0, 2));

        lblTituloPasajero.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblTituloPasajero.setForeground(new java.awt.Color(56, 189, 248));
        lblTituloPasajero.setText("Datos del Pasajero");
        pnlFormularioVenta.add(lblTituloPasajero);

        lblDni.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDni.setForeground(new java.awt.Color(203, 213, 225));
        lblDni.setText("DNI:");
        pnlFormularioVenta.add(lblDni);

        pnlDniBusqueda.setBackground(new java.awt.Color(30, 41, 59));
        pnlDniBusqueda.setLayout(new java.awt.BorderLayout(8, 0));

        txtDni.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlDniBusqueda.add(txtDni, java.awt.BorderLayout.CENTER);

        btnConsultarReniec.setBackground(new java.awt.Color(2, 132, 199));
        btnConsultarReniec.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnConsultarReniec.setForeground(new java.awt.Color(255, 255, 255));
        btnConsultarReniec.setText("Buscar DNI");
        btnConsultarReniec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarReniecActionPerformed(evt);
            }
        });
        pnlDniBusqueda.add(btnConsultarReniec, java.awt.BorderLayout.LINE_END);

        pnlFormularioVenta.add(pnlDniBusqueda);

        lblNombre.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNombre.setForeground(new java.awt.Color(203, 213, 225));
        lblNombre.setText("Nombres:");
        pnlFormularioVenta.add(lblNombre);

        txtNombre.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlFormularioVenta.add(txtNombre);

        lblApellido.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblApellido.setForeground(new java.awt.Color(203, 213, 225));
        lblApellido.setText("Apellidos:");
        pnlFormularioVenta.add(lblApellido);

        txtApellido.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlFormularioVenta.add(txtApellido);

        lblTelefono.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTelefono.setForeground(new java.awt.Color(203, 213, 225));
        lblTelefono.setText("Telefono:");
        pnlFormularioVenta.add(lblTelefono);

        txtTelefono.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlFormularioVenta.add(txtTelefono);

        lblCorreo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblCorreo.setForeground(new java.awt.Color(203, 213, 225));
        lblCorreo.setText("Correo:");
        pnlFormularioVenta.add(lblCorreo);

        txtCorreo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlFormularioVenta.add(txtCorreo);

        lblDireccion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDireccion.setForeground(new java.awt.Color(203, 213, 225));
        lblDireccion.setText("Direccion:");
        pnlFormularioVenta.add(lblDireccion);

        txtDireccion.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        pnlFormularioVenta.add(txtDireccion);

        pnlComprobantePago.setBackground(new java.awt.Color(30, 41, 59));
        pnlComprobantePago.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        cmbTipoComprobante.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cmbTipoComprobante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Boleta", "Factura" }));
        pnlComprobantePago.add(cmbTipoComprobante);

        cmbMetodoPago.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cmbMetodoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Tarjeta", "Yape / Plin" }));
        pnlComprobantePago.add(cmbMetodoPago);

        pnlFormularioVenta.add(pnlComprobantePago);

        lblAsientosSeleccionados.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblAsientosSeleccionados.setForeground(new java.awt.Color(56, 189, 248));
        lblAsientosSeleccionados.setText("Asientos: Ninguno");
        pnlFormularioVenta.add(lblAsientosSeleccionados);

        lblImporteTotal.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblImporteTotal.setForeground(new java.awt.Color(16, 185, 129));
        lblImporteTotal.setText("Total: S/ 0.00");
        pnlFormularioVenta.add(lblImporteTotal);

        pnlBotonesAccion.setBackground(new java.awt.Color(30, 41, 59));
        pnlBotonesAccion.setLayout(new java.awt.BorderLayout(10, 0));

        btnLimpiar.setBackground(new java.awt.Color(71, 85, 105));
        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });
        pnlBotonesAccion.add(btnLimpiar, java.awt.BorderLayout.LINE_START);

        btnEmitirBoleto.setBackground(new java.awt.Color(16, 185, 129));
        btnEmitirBoleto.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnEmitirBoleto.setForeground(new java.awt.Color(255, 255, 255));
        btnEmitirBoleto.setText("Vender Boleto");
        btnEmitirBoleto.setEnabled(false);
        btnEmitirBoleto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmitirBoletoActionPerformed(evt);
            }
        });
        pnlBotonesAccion.add(btnEmitirBoleto, java.awt.BorderLayout.CENTER);

        pnlFormularioVenta.add(pnlBotonesAccion);

        pnlCentral.add(pnlFormularioVenta);

        add(pnlCentral, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void cmbRutasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRutasActionPerformed
        filtrarSalidas();
    }//GEN-LAST:event_cmbRutasActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        filtrarSalidas();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnVerTodasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerTodasActionPerformed
        cmbRutas.setSelectedIndex(0);
        txtFechaViaje.setText("");
        cargarSalidas();
    }//GEN-LAST:event_btnVerTodasActionPerformed

    private void btnConsultarReniecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarReniecActionPerformed
        String dni = txtDni.getText() != null ? txtDni.getText().trim() : "";
        if (!Validators.esDniValido(dni)) {
            AlertHelper.showWarning("DNI Invalido", "", "Ingresa un DNI de 8 digitos.");
            txtDni.requestFocus();
            return;
        }

        btnConsultarReniec.setEnabled(false);
        btnConsultarReniec.setText("Buscando...");

        new Thread(() -> {
            Optional<Cliente> optCliente = clienteService.buscarPorDni(dni);
            if (optCliente.isEmpty()) {
                optCliente = clienteService.consultarEnReniec(dni);
            }

            final Optional<Cliente> finalCliente = optCliente;
            SwingUtilities.invokeLater(() -> {
                btnConsultarReniec.setEnabled(true);
                btnConsultarReniec.setText("Buscar DNI");

                if (finalCliente.isPresent()) {
                    Cliente c = finalCliente.get();
                    txtNombre.setText(c.getNombre());
                    txtApellido.setText(c.getApellido());
                    if (c.getTelefono() != null) txtTelefono.setText(c.getTelefono());
                    if (c.getCorreo() != null) txtCorreo.setText(c.getCorreo());
                    if (c.getDireccion() != null) txtDireccion.setText(c.getDireccion());
                } else {
                    AlertHelper.showInfo("Busqueda", "", "No se encontro el DNI. Ingresa los datos manualmente.");
                    txtNombre.requestFocus();
                }
            });
        }).start();
    }//GEN-LAST:event_btnConsultarReniecActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        txtDni.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        txtDni.requestFocus();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnEmitirBoletoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmitirBoletoActionPerformed
        if (salidaActual == null) {
            AlertHelper.showWarning("Aviso", "", "Selecciona un horario de salida.");
            return;
        }
        if (asientosSeleccionados.isEmpty()) {
            AlertHelper.showWarning("Aviso", "", "Selecciona al menos un asiento.");
            return;
        }

        String dni = txtDni.getText() != null ? txtDni.getText().trim() : "";
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String apellido = txtApellido.getText() != null ? txtApellido.getText().trim() : "";

        if (!Validators.esDniValido(dni)) {
            AlertHelper.showWarning("Aviso", "", "Ingresa un DNI valido de 8 digitos.");
            txtDni.requestFocus();
            return;
        }
        if (nombre.isEmpty() || apellido.isEmpty()) {
            AlertHelper.showWarning("Aviso", "", "Ingresa los nombres y apellidos del pasajero.");
            txtNombre.requestFocus();
            return;
        }

        Cliente cliente = new Cliente(0, dni, nombre, apellido, txtTelefono.getText(), txtDireccion.getText(), txtCorreo.getText());
        Empleado empleado = SessionManager.getUsuarioActual();
        String tipoComprobante = String.valueOf(cmbTipoComprobante.getSelectedItem());
        String metodoPago = String.valueOf(cmbMetodoPago.getSelectedItem());

        btnEmitirBoleto.setEnabled(false);
        btnEmitirBoleto.setText("Guardando...");

        new Thread(() -> {
            try {
                VentaService.ResultadoVenta resultado = ventaService.procesarVenta(
                        cliente,
                        empleado,
                        salidaActual,
                        new ArrayList<>(asientosSeleccionados),
                        tipoComprobante,
                        metodoPago
                );

                SwingUtilities.invokeLater(() -> {
                    btnEmitirBoleto.setEnabled(false);
                    btnEmitirBoleto.setText("Vender Boleto");
                    asientosSeleccionados.clear();
                    actualizarTotales();
                    renderizarMapaAsientos(salidaActual.getIdAsignacion(), salidaActual.getIdBus());
                    cargarSalidas();

                    Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                    TicketDialog dialog = new TicketDialog(parentFrame, true);
                    dialog.setDatosVenta(resultado);
                    dialog.setVisible(true);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    btnEmitirBoleto.setEnabled(true);
                    btnEmitirBoleto.setText("Vender Boleto");
                    AlertHelper.showError("Error", "No se pudo registrar la venta", e.getMessage());
                    renderizarMapaAsientos(salidaActual.getIdAsignacion(), salidaActual.getIdBus());
                });
            }
        }).start();
    }//GEN-LAST:event_btnEmitirBoletoActionPerformed

    private void filtrarSalidas() {
        String seleccion = String.valueOf(cmbRutas.getSelectedItem());
        String origen = null;
        String destino = null;

        if (seleccion != null && seleccion.contains("->")) {
            String[] partes = seleccion.split("->");
            origen = partes[0].trim();
            destino = partes[1].trim();
        }

        LocalDate fecha = null;
        String textoFecha = txtFechaViaje.getText() != null ? txtFechaViaje.getText().trim() : "";
        if (!textoFecha.isEmpty()) {
            try {
                fecha = LocalDate.parse(textoFecha);
            } catch (Exception ignored) {
            }
        }

        List<AsignacionBusRuta> res = viajeService.buscarSalidas(origen, destino, fecha);
        llenarTablaSalidas(res);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConsultarReniec;
    private javax.swing.JButton btnEmitirBoleto;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnVerTodas;
    private javax.swing.JComboBox<String> cmbMetodoPago;
    private javax.swing.JComboBox<String> cmbRutas;
    private javax.swing.JComboBox<String> cmbTipoComprobante;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblAsientosSeleccionados;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblFechaFiltro;
    private javax.swing.JLabel lblImporteTotal;
    private javax.swing.JLabel lblLeyenda;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblRutaFiltro;
    private javax.swing.JLabel lblSalidaSeleccionada;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTituloPasajero;
    private javax.swing.JPanel pnlBotonesAccion;
    private javax.swing.JPanel pnlCabina;
    private javax.swing.JPanel pnlCentral;
    private javax.swing.JPanel pnlComprobantePago;
    private javax.swing.JPanel pnlDniBusqueda;
    private javax.swing.JPanel pnlFiltros;
    private javax.swing.JPanel pnlFormularioVenta;
    private javax.swing.JPanel pnlGrillaAsientos;
    private javax.swing.JPanel pnlSuperior;
    private javax.swing.JScrollPane scrollSalidas;
    private javax.swing.JTable tblSalidas;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtFechaViaje;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
