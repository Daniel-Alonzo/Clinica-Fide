/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mycompany.clinica;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Pagos extends javax.swing.JFrame {
    private int ids;
    
    HashMap<String, Integer> preciosMedicamentos = new HashMap<>();
    int costoTotal = 0;
    
    //CONSTRUCTOR
    public Pagos() {        
        initComponents();
        btnVolver.setToolTipText("Volver a apartado anterior.");
        btnCerrarSesion.setToolTipText("Haz clic aquí para salir de tu cuenta de manera segura.");
        btnPagar.setToolTipText("Haz clic aquí para generar el paga del paciente consultado.");
        btnFactura.setToolTipText("Haz clic aquí para generar la factura del paciente consultado.");
        setLocationRelativeTo(null);
        setTitle("Pagos");
        setResizable(false);
        ImageIcon originalImage = new ImageIcon("src/main/java/mycompany/clinica/img/Pagos.png");
        Image scaledImage = originalImage.getImage().getScaledInstance(jPanel1.getWidth(), jPanel1.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel background = new JLabel("", scaledIcon, JLabel.CENTER);
        jPanel1.add(background);
        background.setBounds(0, 0, jPanel1.getWidth(), jPanel1.getHeight());
        
        // En caso de cierre accidental
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Funciones.confirmarCierre() == 0) dispose();
           }
        });
        
        cboNombres.setEnabled(false);
        btnFactura.setEnabled(false);
        
        // COLOCA LOS PRECIOS DE MEDICAMENTOS (CREO) Y RAGA POR FAVOR IDENTA EL P*TO CODIGO
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT * FROM medicamentos";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();
            
            while (resultado.next()) {
                String medicamentos = resultado.getString("nombre");
                int precio = resultado.getInt("precio");
                preciosMedicamentos.put(medicamentos, precio);
            } //FIN DEL WHILE             
            
            resultado.close();
            sentencia.close();     
        } catch (SQLException e) {
            e.printStackTrace();
        } //FIN DEL TRY CATCH
        
        txtPaciente.getDocument().addDocumentListener(new DocumentListener() {
            @Override //Al escribir texto
            public void insertUpdate(DocumentEvent e) { actualizarUsuarios(txtPaciente.getText()); }
            @Override //Al eliminar texto
            public void removeUpdate(DocumentEvent e) { actualizarUsuarios(txtPaciente.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { }
        });

    } //FIN CONSTRUCTOR
    
    //Actualiza el INVENTARIO de la farmacia
    private void actualizarUsuarios(String nombre) {
        Funciones f = new Funciones();
        cboNombres.removeAllItems();
        cboNombres.addItem("Seleccionar paciente...");
        btnFactura.setEnabled(false);
        
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            
            if( !nombre.isEmpty() ) {                                
                LocalDate hoy = LocalDate.now();     
                
                String consulta = "SELECT p.id, p.nombre_completo FROM citas c INNER JOIN pacientes p ON c.id_paciente = p.id " +
                    "WHERE c.consultada = 'Si' AND DATE(c.fecha) = ? AND p.nombre_completo LIKE ? AND c.pagada = 'No';";
                try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                    sentencia.setString(1, hoy.toString());
                    sentencia.setString(2, nombre + "%");
                    ResultSet resultado = sentencia.executeQuery();
                    
                    int c = 0;
                    while (resultado.next()) {                        
                        ids = resultado.getInt("p.id");
                        String nombreComp = resultado.getString("p.nombre_completo");
                        //nombrePaciente = nombreComp;
                        cboNombres.addItem((++c) + ". " + nombreComp);
                    } //FIN WHILE
                    
                    if( c > 0 ) cboNombres.setEnabled(true);
                    
                    resultado.close();
                } //FIN TRY
            } 
            else {
                cboNombres.setEnabled(false);
            }                        
            
        } catch (SQLException e) {
            //e.printStackTrace();
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                    + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
        } //FIN DEL TRY CATCH
    } //FIN FUNCIÓN


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        btnVolver = new javax.swing.JButton();
        txtPaciente = new javax.swing.JTextField();
        btnPagar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnFactura = new javax.swing.JButton();
        cboMetodoPago = new javax.swing.JComboBox<>();
        cboNombres = new javax.swing.JComboBox<>();
        btnMute = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 30)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Pago del Cliente");

        btnCerrarSesion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCerrarSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/log_out.png"))); // NOI18N
        btnCerrarSesion.setText("Cerrar sesion");
        btnCerrarSesion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCerrarSesionMouseEntered(evt);
            }
        });
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });

        btnVolver.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnVolver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/backward.png"))); // NOI18N
        btnVolver.setText("Volver atrás");
        btnVolver.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnVolverMouseEntered(evt);
            }
        });
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        btnPagar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPagar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pay_money.png"))); // NOI18N
        btnPagar.setText("Pagar");
        btnPagar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPagarMouseEntered(evt);
            }
        });
        btnPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Nombre del cliente:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Método de pago:");

        btnFactura.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnFactura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/bill.png"))); // NOI18N
        btnFactura.setText("Factura");
        btnFactura.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnFacturaMouseEntered(evt);
            }
        });
        btnFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFacturaActionPerformed(evt);
            }
        });

        cboMetodoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Credito", "Debito", "Cheque" }));
        cboMetodoPago.setPreferredSize(new Dimension(86,24));

        cboNombres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboNombresActionPerformed(evt);
            }
        });

        btnMute.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMute.setText("Activar narrador");
        btnMute.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMuteMouseEntered(evt);
            }
        });
        btnMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMuteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(227, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(cboMetodoPago, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(btnPagar)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnFactura))))
                        .addGap(72, 72, 72))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnMute)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCerrarSesion)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMute)
                    .addComponent(btnCerrarSesion)
                    .addComponent(btnVolver))
                .addGap(31, 31, 31)
                .addComponent(jLabel4)
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboNombres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(5, 5, 5)
                        .addComponent(cboMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnPagar)
                            .addComponent(btnFactura))))
                .addContainerGap(150, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        int opcion = JOptionPane.showConfirmDialog(null, "¿Estás seguro que deseas cerrar sesión?", "Cerrar sesión", JOptionPane.YES_NO_OPTION);
    
         if (opcion == JOptionPane.YES_OPTION) {
             // Cerrar sesión
            InicioSesion inicio = new InicioSesion();
            inicio.setVisible(true);
            dispose(); // Cierra la ventana actual
        }else {
        // No hacer nada, el usuario decidió no cerrar sesión
        }
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
         
         if (Funciones.confirmarVolver() == JOptionPane.YES_OPTION) {
            MenuAdmin menuA = new MenuAdmin();
            menuA.setVisible(true);
            dispose();     
         }else {
        // No hacer nada, el usuario decidió no cerrar sesión
        }
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFacturaActionPerformed
        String metodo_pago;
        
        if(cboNombres.getSelectedIndex() < 1) {
            Funciones f = new Funciones();
            f.mostrarMensaje("¡Busque y seleccione un paciente para continuar!");
            return;
        }
        
        String paciente = cboNombres.getSelectedItem().toString();
        
        //Crea el frame de factura del paciente seleccionado
        metodo_pago = cboMetodoPago.getSelectedItem().toString();
        Facturas facturas = new Facturas(paciente, costoTotal, metodo_pago);
        facturas.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnFacturaActionPerformed

    private void btnPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarActionPerformed
        Funciones f = new Funciones();         
        
        if(cboNombres.getSelectedIndex() < 1) {           
            f.mostrarMensaje("¡Busque y seleccione un paciente para continuar!");
            return;
        }
                
        String[] medicamentos = null;
        costoTotal = 0;
        
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT * FROM consultas WHERE id_paciente = ?; ";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1,ids);
            ResultSet resultado = sentencia.executeQuery();
            
            while (resultado.next()) {
                String medicamento_comprados = resultado.getString("medicamentos");
                medicamentos = medicamento_comprados.split(",");
            }            
            
            resultado.close();
            sentencia.close();
            
            conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            consulta = "UPDATE citas SET pagada = 'Si' WHERE id_paciente = ?; ";
            sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1,ids);
            sentencia.executeUpdate();            
            
            for (String medicamento : medicamentos) {
                if (preciosMedicamentos.containsKey(medicamento)) costoTotal += preciosMedicamentos.get(medicamento);                
                f.mostrarMensaje("Pago Total: $" + costoTotal);
            } //Fin FOR EACH     
            
        } catch (SQLException e) {
            e.printStackTrace();
            f.mostrarMensaje("¡Algo falló! Intente nuevamente.");
        } //FIN TRY CATCH
        
        btnFactura.setEnabled(true);
    }//GEN-LAST:event_btnPagarActionPerformed

    private void cboNombresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboNombresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboNombresActionPerformed
   private Clip clip;
   private boolean isMuted = true;
    private void btnVolverMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVolverMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("volver");
        }
    }//GEN-LAST:event_btnVolverMouseEntered

    private void btnCerrarSesionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCerrarSesionMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("cerrar_sesion");
        }
    }//GEN-LAST:event_btnCerrarSesionMouseEntered

    private void btnPagarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPagarMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("pagar");
        }
    }//GEN-LAST:event_btnPagarMouseEntered

    private void btnFacturaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFacturaMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("factura");
        }
    }//GEN-LAST:event_btnFacturaMouseEntered

    private void btnMuteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMuteMouseEntered
        // TODO add your handling code here:
        isMuted = !isMuted;
        if (isMuted) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            btnMute.setText("Activar narrador");
        } else {
            btnMute.setText("Desactivar narrador");
        }
    }//GEN-LAST:event_btnMuteMouseEntered

    private void btnMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMuteActionPerformed

    }//GEN-LAST:event_btnMuteActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Pagos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Pagos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Pagos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Pagos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Pagos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnFactura;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnPagar;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cboMetodoPago;
    private javax.swing.JComboBox<String> cboNombres;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtPaciente;
    // End of variables declaration//GEN-END:variables
}
