/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mycompany.clinica;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class ReagendarCita extends javax.swing.JFrame {
    private ArrayList<String> citas = new ArrayList<>();
    
    private String user;
    private int id;
   
    public void setUsuario(String user) { this.user = user; }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    //CONSTRUCTOR
    public ReagendarCita(int id) {
        initComponents();
        btnVolver.setToolTipText("Volver a apartado anterior.");
        btnCerrarSesion.setToolTipText("Haz clic aquí para salir de tu cuenta de manera segura.");
        btnGenerar.setToolTipText("Haz clic aquí para reagendar la fecha de la cita seleccionada.");
        setLocationRelativeTo(null);
        setTitle("Reagendar cita");
        setResizable(false);
        ImageIcon originalImage = new ImageIcon("src/main/java/mycompany/clinica/img/Reagendar.png");
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
 
        //Válida que el usuario tenga citas pendientes
        Funciones f = new Funciones();
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT * FROM citas WHERE id_paciente = ? AND consultada = 'No'";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1, id);
            
            ResultSet resultado = sentencia.executeQuery();
            int c = 0;
            while (resultado.next()) {
                String sintoma = resultado.getString("sintomas");
                sintoma = (++c) + ".- " + sintoma;
                
                cboCita.addItem(sintoma);
                
                java.sql.Date fecha = resultado.getDate("fecha");
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                String fechaF = sdf.format(fecha);
                
                citas.add(fechaF);                
            }
            //Verifica que el usuario tenga citas programadas, si no tiene bloquea todas las opciones excpeto volver o cerrar sesión
            if( c < 1 ) {
                f.mostrarMensaje("¡Actualmente no tienes citas pendientes!");
                cboCita.setEnabled(false);
                txtFecha.setEnabled(false);
                btnGenerar.setEnabled(false);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                    + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
        }
        
        //Obtiene la fecha de la cita para ser más claro
        cboCita.addActionListener((ActionEvent e) -> {
            //txtFecha.setText( citas.get(cboCita.getSelectedIndex()) );
            java.util.Date fecha;
            try {
                fecha = new SimpleDateFormat("dd/MM/yyyy").parse(citas.get(cboCita.getSelectedIndex()));
                txtFecha.setDate(fecha);
            } catch (ParseException ex) {}            
            System.out.println(cboCita.getSelectedIndex() + ": " + citas.get(cboCita.getSelectedIndex()) );
        });
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        btnVolver = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnGenerar = new javax.swing.JButton();
        cboCita = new javax.swing.JComboBox<>();
        btnMute = new javax.swing.JButton();
        txtFecha = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Reagendar cita");

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

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Seleccione la cita a reagendar:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Fecha");

        btnGenerar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGenerar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        btnGenerar.setText("Generar cita");
        btnGenerar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnGenerarMouseEntered(evt);
            }
        });
        btnGenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarActionPerformed(evt);
            }
        });

        cboCita.setPreferredSize(new Dimension(72,24));
        cboCita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCitaActionPerformed(evt);
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMute)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboCita, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 114, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnGenerar)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCerrarSesion)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMute)
                    .addComponent(btnCerrarSesion)
                    .addComponent(btnVolver))
                .addGap(27, 27, 27)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboCita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addComponent(btnGenerar)
                .addGap(123, 123, 123))
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
            MenuPaciente menuP = new MenuPaciente();
            menuP.setId(id);
            menuP.setUsuario(user);
            menuP.mostrarFechaCita(id);
            menuP.setVisible(true);
            dispose();
        }
   
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarActionPerformed
        Funciones f = new Funciones();
        
        //Formato de la fecha (día, mes, año)
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");

        //Obtiene los datos del form
        String fecha = "";
        Calendar calendar = txtFecha.getCalendar();
                
        if( calendar != null )
            fecha = date.format(txtFecha.getCalendar().getTime());         
        String item = cboCita.getSelectedItem().toString();
        
        //Válida que la fecha esté en un valor correcto
        fecha = f.completarFecha(fecha);
        if( f.verificarFecha(fecha) == false ) {
            f.mostrarMensaje("Seleccione una fecha antes de continuar.");
            return;
        }
        
        //Actualiza la cita en la BD
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "UPDATE citas SET fecha = str_to_date(?, '%d/%m/%Y') WHERE sintomas = ?;";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setString(1, fecha);
            sentencia.setString(2, item);
            int filasAfectadas = sentencia.executeUpdate();
            System.out.println("Filas afectadas: " + filasAfectadas);
            sentencia.close();
            conexion.close();
            
            f.mostrarMensaje("¡Cita actualizada correctamente!");
            cboCita.setSelectedIndex(0);
            
        } catch (SQLException e) {
            e.printStackTrace();
            f.mostrarMensaje("¡Algo falló! Intente nuevamente.");
        }

    }//GEN-LAST:event_btnGenerarActionPerformed

    private void cboCitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCitaActionPerformed
    
    }//GEN-LAST:event_cboCitaActionPerformed
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

    private void btnGenerarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGenerarMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("generar_cita");
        }
    }//GEN-LAST:event_btnGenerarMouseEntered

    private void btnMuteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMuteMouseEntered
        
    }//GEN-LAST:event_btnMuteMouseEntered

    private void btnMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMuteActionPerformed
        isMuted = !isMuted;
        if (isMuted) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            btnMute.setText("Activar narrador");
        } else {
            btnMute.setText("Desactivar narrador");
        }
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
            java.util.logging.Logger.getLogger(ReagendarCita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReagendarCita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReagendarCita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReagendarCita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnGenerar;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cboCita;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private com.toedter.calendar.JDateChooser txtFecha;
    // End of variables declaration//GEN-END:variables
}
