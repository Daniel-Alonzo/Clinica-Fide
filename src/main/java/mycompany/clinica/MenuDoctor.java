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
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.JOptionPane;

public class MenuDoctor extends javax.swing.JFrame {
   private int id;
   private String user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public MenuDoctor() {
        initComponents();
        btnCerrarSesion.setToolTipText("Haz clic aquí para salir de tu cuenta de manera segura.");
        btnPacientes.setToolTipText("Haz clic aquí para ver la lista de pacientes consultados y por consultar.");
        btnHistorialMedico.setToolTipText("Haz clic aquí para revisar el historial médico completo de un paciente.");
        btnHistorialConsultas.setToolTipText("Haz clic aquí para ver las consultas previas de un paciente.");
        btnConsulta.setToolTipText("Haz clic aquí para atender a un paciente en una consulta médica.");
        setLocationRelativeTo(null);
        setTitle("Menu Doctor");
        setResizable(false);
        ImageIcon originalImage = new ImageIcon("src/main/java/mycompany/clinica/img/Menu  Docotr.png");
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
        
        mostrarFechaCita(); 
    }
    
    //Asigna el nombre del paciente en el título
    public void setUsuario(String user) {        
        this.user = user;
        
        //Asignar nombre al MENU de acuerdo al nombre del paciente 
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT nombre_completo FROM medicos "
                    + "WHERE usuario = ?;";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setString(1, user);
            ResultSet resultado = sentencia.executeQuery();

            //Busca el nombre del usuario en la tabla
            while (resultado.next()) {
                String nombre = resultado.getString("nombre_completo");		
                lblDoctor.setText("Doctor: " + nombre);                          
            } //FIN DEL WHILE

            resultado.close();
            sentencia.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(MenuPaciente.class.getName()).log(Level.SEVERE, null, ex);
        } //FIN DEL TRY CATCH
    } //FIN DE LA FUNCIÓN

    //Muestra la fecha de la cita más cercana
    private void mostrarFechaCita() {
        Funciones f = new Funciones();
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT fecha FROM citas WHERE consultada='No'";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();

            // Variables para rastrear la fecha más próxima
            Date fechaProxima = null;

            while (resultado.next()) {
                Date fecha = resultado.getDate("fecha");

                // Comprobar si la fecha es la más próxima
                if (fechaProxima == null || fecha.before(fechaProxima)) {
                    fechaProxima = fecha;
                }
            }

            resultado.close();
            sentencia.close();

            // Verificar si hay alguna fecha de cita programada
            if (fechaProxima != null) {
                DateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy"); // Formato con el mes en letras
                String fechaFormateada = dateFormat.format(fechaProxima);
                lblCita.setText(fechaFormateada);
            } else {
                lblCita.setText("No hay citas programadas.");
            }

        } catch (SQLException e) {
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                    + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
        }
    } //FIN MÉTODO

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblDoctor = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btnPacientes = new javax.swing.JButton();
        btnHistorialMedico = new javax.swing.JButton();
        btnHistorialConsultas = new javax.swing.JButton();
        btnConsulta = new javax.swing.JButton();
        lblCita = new javax.swing.JLabel();
        btnMute = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblDoctor.setFont(new java.awt.Font("Segoe UI", 3, 26)); // NOI18N
        lblDoctor.setForeground(new java.awt.Color(255, 255, 255));
        lblDoctor.setText("Doctor: Usuario");
        lblDoctor.setPreferredSize(new Dimension(260,36));

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

        jLabel2.setText("Citas proximas:");

        btnPacientes.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPacientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pacients.png"))); // NOI18N
        btnPacientes.setText("Pacientes");
        btnPacientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPacientesMouseEntered(evt);
            }
        });
        btnPacientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPacientesActionPerformed(evt);
            }
        });

        btnHistorialMedico.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHistorialMedico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/medic_history.png"))); // NOI18N
        btnHistorialMedico.setText("Historial medico");
        btnHistorialMedico.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHistorialMedicoMouseEntered(evt);
            }
        });
        btnHistorialMedico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistorialMedicoActionPerformed(evt);
            }
        });

        btnHistorialConsultas.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHistorialConsultas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/consultation_history.png"))); // NOI18N
        btnHistorialConsultas.setText("Historial consultas");
        btnHistorialConsultas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHistorialConsultasMouseEntered(evt);
            }
        });
        btnHistorialConsultas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistorialConsultasActionPerformed(evt);
            }
        });

        btnConsulta.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnConsulta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/consultation.png"))); // NOI18N
        btnConsulta.setText("Consulta");
        btnConsulta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnConsultaMouseEntered(evt);
            }
        });
        btnConsulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultaActionPerformed(evt);
            }
        });

        lblCita.setText("Tiene citas proximas:");

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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCita)
                            .addComponent(jLabel2)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnMute)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btnPacientes, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnHistorialMedico)
                                        .addGap(16, 16, 16)
                                        .addComponent(btnHistorialConsultas)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnConsulta, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnCerrarSesion, javax.swing.GroupLayout.Alignment.TRAILING))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMute)
                    .addComponent(btnCerrarSesion))
                .addGap(32, 32, 32)
                .addComponent(lblDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPacientes)
                    .addComponent(btnHistorialMedico)
                    .addComponent(btnHistorialConsultas)
                    .addComponent(btnConsulta))
                .addGap(77, 77, 77)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCita)
                .addContainerGap(111, Short.MAX_VALUE))
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

    private void btnPacientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPacientesActionPerformed
        Pacientes pacientes = new Pacientes();
        pacientes.setUser(user);
        pacientes.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnPacientesActionPerformed

    private void btnHistorialMedicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistorialMedicoActionPerformed
        HistorialMedico historialM = new HistorialMedico();
        historialM.setUser(user);
        historialM.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnHistorialMedicoActionPerformed

    private void btnHistorialConsultasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistorialConsultasActionPerformed
        HistorialConsulta historialC = new HistorialConsulta();
        historialC.setUser(user);
        historialC.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnHistorialConsultasActionPerformed

    private void btnConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultaActionPerformed
        Consulta consulta = new Consulta();
        consulta.setUser(user);
        consulta.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnConsultaActionPerformed
private Clip clip;
       private boolean isMuted = true;
    private void btnCerrarSesionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCerrarSesionMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("cerrar_sesion");
        }
    }//GEN-LAST:event_btnCerrarSesionMouseEntered

    private void btnPacientesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPacientesMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("pacientes");
        }
    }//GEN-LAST:event_btnPacientesMouseEntered

    private void btnHistorialMedicoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHistorialMedicoMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("historial_medico");
        }
    }//GEN-LAST:event_btnHistorialMedicoMouseEntered

    private void btnHistorialConsultasMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHistorialConsultasMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("historial_consultas");
        }
    }//GEN-LAST:event_btnHistorialConsultasMouseEntered

    private void btnConsultaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnConsultaMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("consulta");
        }
    }//GEN-LAST:event_btnConsultaMouseEntered

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
            java.util.logging.Logger.getLogger(MenuDoctor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuDoctor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuDoctor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuDoctor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuDoctor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnConsulta;
    private javax.swing.JButton btnHistorialConsultas;
    private javax.swing.JButton btnHistorialMedico;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnPacientes;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblCita;
    private javax.swing.JLabel lblDoctor;
    // End of variables declaration//GEN-END:variables
}
