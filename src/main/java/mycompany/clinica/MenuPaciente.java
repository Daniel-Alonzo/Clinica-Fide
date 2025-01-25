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
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.JOptionPane;

public class MenuPaciente extends javax.swing.JFrame {
    private String user;
    private int id;
    
    public MenuPaciente() {        
        initComponents();
        btnCerrarSesion.setToolTipText("Haz clic aquí para salir de tu cuenta de manera segura.");
        btnReprogramar.setToolTipText("Haz clic aquí para cambiar la fecha de tu consulta médica.");
        btnCompletarInfo.setToolTipText(" Haz clic aquí para agregar o actualizar tus datos personales.");
        btnGenerar.setToolTipText("Haz clic aquí para programar una nueva consulta médica.");
        btnPreinscripcion.setToolTipText("Haz clic aquí para ver las instrucciones y recetas proporcionadas por tu doctor.");
        setLocationRelativeTo(null);
        setTitle("Menu paciente");
        setResizable(false);
        ImageIcon originalImage = new ImageIcon("src/main/java/mycompany/clinica/img/Menu Paciente.png");
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
    }
    
    // Validación para reagendar cita
    public void validarReagenda() {
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT * FROM citas WHERE id_paciente = ? AND consultada = 'No'";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1, id);
            
            ResultSet resultado = sentencia.executeQuery();   
            
            int contador = 0;
            while (resultado.next()) {
                contador++;
            }
            System.out.println(id);
            System.out.println(contador);
            this.btnReprogramar.setEnabled(contador != 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    //Asigna el nombre del paciente en el título
    public void setUsuario(String user) {        
        Funciones f = new Funciones();
        
        this.user = user;        
        //Asignar nombre al MENU de acuerdo al nombre del paciente 
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT * FROM pacientes WHERE usuario = ?;";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setString(1, user);
            ResultSet resultado = sentencia.executeQuery();

            //Busca el nombre del usuario en la tabla
            while (resultado.next()) {
                id = resultado.getInt("id");
                String usuario = resultado.getString("usuario");
                String nombre = resultado.getString("nombre_completo");		

                if( usuario.equals(user) ) {					
                    lblPaciente.setText("Paciente: " + nombre);                    
                    break;                    
                }
            } //FIN DEL WHILE

            resultado.close();
            sentencia.close();
            
        } catch (SQLException ex) {
            //Logger.getLogger(MenuPaciente.class.getName()).log(Level.SEVERE, null, ex);
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                    + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
        } //FIN DEL TRY CATCH
    } //FIN DE LA FUNCIÓN
   
    public void mostrarFechaCita(int id) {
        setUsuario(user);
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT fecha FROM citas WHERE id_paciente = ? ";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1,id);
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
                jLabel3.setText(fechaFormateada);
            } else {
                jLabel3.setText("No hay citas programadas.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        this.validarReagenda();
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        lblPaciente = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        btnReprogramar = new javax.swing.JButton();
        btnGenerar = new javax.swing.JButton();
        btnPreinscripcion = new javax.swing.JButton();
        btnCompletarInfo = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnMute = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblPaciente.setFont(new java.awt.Font("Segoe UI", 3, 26)); // NOI18N
        lblPaciente.setForeground(new java.awt.Color(255, 255, 255));
        lblPaciente.setText("Paciente: Usuario");
        lblPaciente.setPreferredSize(new Dimension(275,36));

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

        btnReprogramar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnReprogramar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        btnReprogramar.setText("Reagendar cita");
        btnReprogramar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnReprogramarMouseEntered(evt);
            }
        });
        btnReprogramar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReprogramarActionPerformed(evt);
            }
        });

        btnGenerar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGenerar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
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

        btnPreinscripcion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPreinscripcion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/checklist.png"))); // NOI18N
        btnPreinscripcion.setText("Prescripcion medica");
        btnPreinscripcion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPreinscripcionMouseEntered(evt);
            }
        });
        btnPreinscripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreinscripcionActionPerformed(evt);
            }
        });

        btnCompletarInfo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCompletarInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/information.png"))); // NOI18N
        btnCompletarInfo.setText("Completar informacion");
        btnCompletarInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCompletarInfoMouseEntered(evt);
            }
        });
        btnCompletarInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompletarInfoActionPerformed(evt);
            }
        });

        jLabel2.setText("Citas proximas:");

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
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnCompletarInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPreinscripcion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnGenerar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnReprogramar, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(70, 70, 70)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(307, 307, 307)
                        .addComponent(btnMute)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCerrarSesion)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCerrarSesion)
                    .addComponent(btnMute))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(lblPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(btnGenerar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addComponent(btnReprogramar)
                        .addGap(12, 12, 12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(118, 118, 118)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(btnPreinscripcion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCompletarInfo)
                .addGap(87, 87, 87))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnReprogramarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReprogramarActionPerformed
        ReagendarCita reagendar = new ReagendarCita(id);
        reagendar.setId(id);
        reagendar.setUsuario(user);
        reagendar.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnReprogramarActionPerformed

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

    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarActionPerformed
        GenerarCita generar = new GenerarCita();
        generar.setId(id);
        generar.setUsuario(user);
        generar.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnGenerarActionPerformed

    private void btnPreinscripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreinscripcionActionPerformed
        RecetaMedica receta = new RecetaMedica(id);
        receta.setId(id);
        receta.setUser(user);
        receta.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnPreinscripcionActionPerformed

    private void btnCompletarInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompletarInfoActionPerformed
        CompletarInfo completar = new CompletarInfo(id);
        completar.setId(id);
        completar.setUsuario(user);
        completar.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnCompletarInfoActionPerformed
private Clip clip;
       private boolean isMuted = true;
    private void btnCerrarSesionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCerrarSesionMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("cerrar_sesion");
        }
    }//GEN-LAST:event_btnCerrarSesionMouseEntered

    private void btnReprogramarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnReprogramarMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("reagendar_cita");
        }
    }//GEN-LAST:event_btnReprogramarMouseEntered

    private void btnGenerarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGenerarMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("generar_cita");
        }
    }//GEN-LAST:event_btnGenerarMouseEntered

    private void btnPreinscripcionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPreinscripcionMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("prescripcion_medica");
        }
    }//GEN-LAST:event_btnPreinscripcionMouseEntered

    private void btnCompletarInfoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCompletarInfoMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("completar_informacion");
        }
    }//GEN-LAST:event_btnCompletarInfoMouseEntered

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
            java.util.logging.Logger.getLogger(MenuPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
    private javax.swing.JButton btnCompletarInfo;
    private javax.swing.JButton btnGenerar;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnPreinscripcion;
    private javax.swing.JButton btnReprogramar;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel lblPaciente;
    // End of variables declaration//GEN-END:variables
}
