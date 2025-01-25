/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mycompany.clinica;

import java.awt.Color;
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
import java.time.format.DateTimeFormatter;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class Pacientes extends javax.swing.JFrame {
    private String user;
    
    void setUser(String user) { this.user = user; }
    
    public Pacientes() {
        initComponents();
        btnVolver.setToolTipText("Volver a apartado anterior.");
        btnCerrarSesion.setToolTipText("Haz clic aquí para salir de tu cuenta de manera segura.");
        btnEliminar.setToolTipText("Haz clic aquí para eliminar al paciente de manera definitiva.");
        setLocationRelativeTo(null);
        setTitle("Pacientes");
        setResizable(false);
        ImageIcon originalImage = new ImageIcon("src/main/java/mycompany/clinica/img/Pacientes.png");
        Image scaledImage = originalImage.getImage().getScaledInstance(jPanel3.getWidth(), jPanel3.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel background = new JLabel("", scaledIcon, JLabel.CENTER);
        jPanel3.add(background);
        background.setBounds(0, 0, jPanel3.getWidth(), jPanel3.getHeight());
        
        // En caso de cierre accidental
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Funciones.confirmarCierre() == 0) dispose();
           }
        });
        
        //Coloca los pacientes del doctor del día de hoy
        Funciones f = new Funciones();               
        try{
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            //Se hacen dos LEFT JOIN, el primero es la tabla PACIENTES CON CITAS y despues CITAS con CONSULTAS
            String consulta = "SELECT ci.fecha, ci.area_atencion, ci.sintomas, CASE WHEN co.id_paciente IS NOT NULL THEN 'Si'"
                    + " ELSE 'No' END AS consultada, p.nombre_completo, co.diagnostico, co.tratamientos, co.medicamentos"
                    + " FROM citas ci LEFT JOIN pacientes p ON ci.id_paciente = p.id LEFT JOIN consultas co ON ci.id_paciente = co.id_paciente;";
            
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            ResultSet res = sentencia.executeQuery();           
            
            String txtA1 = "";
            String txtA2 = "";
            int c1 = 0, c2 = 0;
            while (res.next()) {
                String nombre = res.getString("p.nombre_completo");
                java.sql.Date fecha = res.getDate("ci.fecha");
                java.text.SimpleDateFormat formato = new java.text.SimpleDateFormat("dd/MM/yyyy");
                String fechaF = formato.format(fecha);
                String area = res.getString("ci.area_atencion");
                String sintomas = res.getString("ci.sintomas");   
                String consultada = res.getString("consultada");                                
                
                // Dependiendo de si la CITA de la consulta ya se realizó, se pondrá como consultada o pendiente
                if( consultada.equals("Si") ) {      
                    String diagnostico = res.getString("co.diagnostico");
                    String tratamiento = res.getString("co.tratamientos");
                    String medicamento = res.getString("co.medicamentos");
                    
                    txtA1 += "Consulta #" + (++c1) + "\n - Nombre: " + nombre + "\n - Fecha: " + fechaF + "\n - Area de atención: "
                        + area + "\n - Sintomas: " + sintomas + "\n - Diagnostico: " + diagnostico + "\n - Tratamiento: " + tratamiento
                        + "\n - Medicamentos recetados: " + medicamento + "\n\n";
                } else {
                    txtA2 += "Cita pendiente #" + (++c2) + "\n - Nombre: " + nombre + "\n - Fecha: " + fechaF + "\n - Area de atención: "
                        + area + "\n - Sintomas: " + sintomas + "\n\n";
                }
            } //FIN DEL WHILE            
            
            txtArea1.setText(txtA1);
            txtArea2.setText(txtA2);
            
            res.close();
            sentencia.close();
            
        } catch(SQLException e) {
            //e.printStackTrace();
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                    + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
        } //FIN DEL TRY CATCH
    }

    
    //Verifica si la fecha es después de la fecha actual
    boolean verificarFecha(String fecha) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaActual = LocalDate.now();        
        LocalDate fechaIngresada = LocalDate.parse(fecha, formato);     

        // Valida si la fecha ingresada es igual o anterior a la fecha actual
        return !fechaIngresada.isAfter(fechaActual);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        btnVolver = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtArea1 = new javax.swing.JTextArea();
        txtpaciente = new javax.swing.JTextField();
        btnEliminar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtArea2 = new javax.swing.JTextArea();
        btnMute = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 30)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Pacientes");

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

        txtArea1.setBackground(new java.awt.Color(232, 232, 232));
        txtArea1.setColumns(20);
        txtArea1.setRows(5);
        jScrollPane2.setViewportView(txtArea1);

        txtpaciente.setForeground(new java.awt.Color(153, 153, 153));
        txtpaciente.setText("LRRAGA");
        txtpaciente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtpacienteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtpacienteFocusLost(evt);
            }
        });
        txtpaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpacienteActionPerformed(evt);
            }
        });

        btnEliminar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete.png"))); // NOI18N
        btnEliminar.setText("Eliminar paciente");
        btnEliminar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnEliminarMouseEntered(evt);
            }
        });
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        txtArea2.setBackground(new java.awt.Color(232, 232, 232));
        txtArea2.setColumns(20);
        txtArea2.setRows(5);
        jScrollPane3.setViewportView(txtArea2);

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(txtpaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnEliminar))
                            .addComponent(jLabel4))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(233, 233, 233)
                        .addComponent(btnMute)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVolver, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCerrarSesion)
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVolver)
                    .addComponent(btnMute)
                    .addComponent(btnCerrarSesion))
                .addGap(38, 38, 38)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtpaciente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(100, 100, 100))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        MenuDoctor menuD = new MenuDoctor();
        menuD.setUsuario(user);
        menuD.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
    // Obtiene el usuario del paciente ingresado en el campo de texto
    Funciones f = new Funciones();
    String UsPaciente = txtpaciente.getText();
    // Valida que se haya ingresado un usuario de paciente
    if (UsPaciente.isEmpty()) {
        f.mostrarMensaje("Por favor, ingrese el USUARIO del paciente que desea eliminar.");
        return;
    }
    // Realiza la eliminación del paciente en la base de datos
    try {
        Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
        String consulta = "SELECT id FROM pacientes WHERE usuario = ?";
        PreparedStatement sentencia = conexion.prepareStatement(consulta);
        sentencia.setString(1, UsPaciente);

        ResultSet resultSet = sentencia.executeQuery();

        if (!resultSet.next()) {
            f.mostrarMensaje("Paciente inexistente");
            return;
        }

        int pacienteId = resultSet.getInt("id");

        String consultaCitas = "DELETE FROM citas WHERE id_paciente = ?";
        PreparedStatement sentenciaCitas = conexion.prepareStatement(consultaCitas);
        sentenciaCitas.setInt(1, pacienteId);

        sentenciaCitas.executeUpdate();
        
        consulta = "DELETE FROM consultas WHERE id_paciente = ?";
        sentencia = conexion.prepareStatement(consulta);
        sentencia.setInt(1, pacienteId);
        
        sentencia.executeUpdate();
        
        consulta = "DELETE FROM pacientes WHERE usuario = ?";
        sentencia = conexion.prepareStatement(consulta);
        sentencia.setString(1, UsPaciente);
        
        int filasAfectadas = sentencia.executeUpdate();

        if (filasAfectadas > 0) {
            f.mostrarMensaje( "Paciente eliminado correctamente.");
        } else {
            f.mostrarMensaje( "No se encontró un paciente con el USUARIO especificado.");
        }
        
        String consulta2 = "DELETE FROM usuarios WHERE nombre = ?";
        PreparedStatement sentencia2 = conexion.prepareStatement(consulta2);
        sentencia2.setString(1, UsPaciente);
        sentencia2.executeUpdate();
        // Limpia el campo de texto después de la eliminación
        txtpaciente.setText("");
        sentencia.close();
        sentencia2.close();
        conexion.close();
    } catch (SQLException e) {
        e.printStackTrace();
        f.mostrarMensaje( "Error al eliminar el paciente.");
    }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void txtpacienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpacienteFocusGained
        if(txtpaciente.getText().equals("LRRAGA")){
            txtpaciente.setText("");
            txtpaciente.setForeground(Color.BLACK);
        } 
    }//GEN-LAST:event_txtpacienteFocusGained

    private void txtpacienteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpacienteFocusLost
         if(txtpaciente.getText().equals("")){
            txtpaciente.setText("LRRAGA");
            txtpaciente.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_txtpacienteFocusLost
private Clip clip;
       private boolean isMuted = true;
    private void btnVolverMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVolverMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("volver_atras");
        }
    }//GEN-LAST:event_btnVolverMouseEntered

    private void btnCerrarSesionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCerrarSesionMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("cerrar_sesion");
        }
    }//GEN-LAST:event_btnCerrarSesionMouseEntered

    private void btnEliminarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEliminarMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("eliminar_paciente");
        }
    }//GEN-LAST:event_btnEliminarMouseEntered

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

    private void txtpacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpacienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpacienteActionPerformed

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
            java.util.logging.Logger.getLogger(Pacientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Pacientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Pacientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Pacientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Pacientes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnVolver;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea txtArea1;
    private javax.swing.JTextArea txtArea2;
    private javax.swing.JTextField txtpaciente;
    // End of variables declaration//GEN-END:variables
}
