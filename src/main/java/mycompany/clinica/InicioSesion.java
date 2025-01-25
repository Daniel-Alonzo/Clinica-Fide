/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mycompany.clinica;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sound.sampled.Clip;

import javax.swing.*;

public class InicioSesion extends javax.swing.JFrame {
    private Clip clip;
    private boolean isMuted = true;
    private boolean mostrar = false;

    public InicioSesion() {
        initComponents();
        btnInicioSesion.setToolTipText("Ingresa los datos correspondientes de tu cuenta");
        btnRegistrar.setToolTipText("Registrar nuevo usuario paciente");
        setLocationRelativeTo(null);
        setTitle("Inicio de sesion");
        setResizable(false);
        ImageIcon originalImage = new ImageIcon("src/main/java/mycompany/clinica/img/Inicio Sesion.png");
        Image scaledImage = originalImage.getImage().getScaledInstance(jPanel1.getWidth(), jPanel1.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel background = new JLabel("", scaledIcon, JLabel.CENTER);
        jPanel1.add(background);
        background.setBounds(0, 0, jPanel1.getWidth(), jPanel1.getHeight()); // Ajusta las dimensiones según el tamaño del panel
        
        //Atajos de tecllado
        KeyStroke ctrl1 = KeyStroke.getKeyStroke(KeyEvent.VK_1, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        KeyStroke ctrl2 = KeyStroke.getKeyStroke(KeyEvent.VK_2, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke ctrlR = KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

        // Ctrl + 1
        jPanel1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrl1, "focusUsuario");
        jPanel1.getActionMap().put("focusUsuario", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtUsuario.requestFocusInWindow();
            }
        });
        
        jPanel1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrl2, "focusContra");
        jPanel1.getActionMap().put("focusContra", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtContra.requestFocusInWindow();
            }
        });
        
        jPanel1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enter, "clickBtnSesion");
        jPanel1.getActionMap().put("clickBtnSesion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {                
                btnInicioSesion.doClick();
            }
        });
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Funciones.confirmarCierre() == 0) dispose();
           }
        });        
    }
    
    void iniciarSesion() {
        //Variables para guardar el USUARIO Y CONTRASEÑA
        String usuario, contra;
        
        //Extrae la info del JTextFiel txtUsuario y txtContra
        usuario = txtUsuario.getText();
        contra = new String(txtContra.getPassword());
        
        //Si alguno de los TextField está vacío, se manda una alerta
        if(usuario.isEmpty() || contra.isEmpty()) {
            Funciones f = new Funciones();
            f.mostrarMensaje("¡Faltan valores por ingresar!");
            return;
        }        
        
        Funciones f = new Funciones();
        
        String usr = "";
        int idRol = 0;
        int id = 0;
        //int id_usuario = 0;
        try {
            //Inicia sesión
            try (Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password)) {
                String consulta ="SELECT * FROM usuarios WHERE nombre = ? and contrasena = ?";
                PreparedStatement sentencia = conexion.prepareStatement(consulta);
                sentencia.setString(1, usuario);
                sentencia.setString(2, contra);
                ResultSet resultado = sentencia.executeQuery();
                if (resultado.next()) {
                    id = resultado.getInt("id");
                    usr = resultado.getString("nombre");
                    f.mostrarMensaje("Iniciando sesión...");                    
                    idRol = resultado.getInt("id_rol");
                    //id_usuario = resultado.getInt("id");
                } else {
                    f.mostrarMensaje("¡Usuario no encontrado!");
                    txtUsuario.setText("");
                    txtContra.setText("");
                    txtUsuario.requestFocus();
                    return;
                }
                
                //Obtener ID del paciente para despues utilizarlo
                String consultas ="SELECT * FROM pacientes WHERE usuario = ?";
                PreparedStatement sentencias = conexion.prepareStatement(consultas);
                sentencias.setString(1,txtUsuario.getText());
                ResultSet resultados = sentencias.executeQuery();
                if (resultados.next()) {
                    id = resultados.getInt("id"); 
                    usr = resultados.getString("usuario");
                }
                
                //Se cierra la operacion SQL
                resultado.close();
                sentencia.close();
            }

            //Muestra un MENÚ de acuerdo al tipo de usuario
            switch (idRol) {
                case 1 ->   {
                    MenuAdmin m = new MenuAdmin();
                    m.setVisible(true);
                }
                case 2 ->                         {
                    MenuFarmacia m = new MenuFarmacia();
                    m.setVisible(true);
                }
                case 3 ->                         { 
                    MenuDoctor m = new MenuDoctor();
                    m.setId(id);                    
                    m.setUsuario(usr);
                    m.setVisible(true);
                }
                case 4 ->                         {
                    MenuPaciente m = new MenuPaciente();
                    m.setId(id);
                    m.mostrarFechaCita(id);
                    m.setUsuario(usr);
                    m.setVisible(true);                    
                }
                default -> { return; }
            } //FIN DEL SWITCH
            
            //Cierra la ventana actual
            dispose();

        } catch (SQLException e) {
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                    + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
            //e.printStackTrace();
        } //FIN DEL TRY CATCH SQL
        
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
        txtUsuario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnInicioSesion = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnMute = new javax.swing.JButton();
        txtContra = new javax.swing.JPasswordField();
        btnContrasena = new javax.swing.JButton();
        btnRegistrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Usuario:");

        txtUsuario.setForeground(new java.awt.Color(153, 153, 153));
        txtUsuario.setText("Enter Username");
        txtUsuario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUsuarioFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUsuarioFocusLost(evt);
            }
        });
        txtUsuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtUsuarioMouseEntered(evt);
            }
        });
        txtUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Contraseña:");

        btnInicioSesion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnInicioSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/log_in.png"))); // NOI18N
        btnInicioSesion.setMnemonic('1');
        btnInicioSesion.setText("Iniciar Sesion");
        btnInicioSesion.setPreferredSize(new java.awt.Dimension(120, 24));
        btnInicioSesion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnInicioSesionMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnInicioSesionMouseEntered(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnInicioSesionMouseReleased(evt);
            }
        });
        btnInicioSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInicioSesionActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel5.setText("Ctrl+1");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel6.setText("Ctrl+2");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel7.setText("Enter");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel8.setText("Ctrl+R");

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

        txtContra.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtContraMouseEntered(evt);
            }
        });
        txtContra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContraActionPerformed(evt);
            }
        });

        btnContrasena.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/eye_closed.png"))); // NOI18N
        btnContrasena.setMaximumSize(new java.awt.Dimension(22, 22));
        btnContrasena.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContrasenaActionPerformed(evt);
            }
        });

        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/create_account.png"))); // NOI18N
        btnRegistrar.setMnemonic('1');
        btnRegistrar.setText("Registrar");
        btnRegistrar.setPreferredSize(new java.awt.Dimension(120, 24));
        btnRegistrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRegistrarMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnRegistrarMouseEntered(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnRegistrarMouseReleased(evt);
            }
        });
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnMute))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnInicioSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtContra, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(35, 35, 35)
                                        .addComponent(jLabel1)
                                        .addGap(139, 139, 139))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)))
                                .addComponent(jLabel4)))
                        .addGap(0, 376, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(btnMute)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(17, 17, 17)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtContra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(btnInicioSesion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(17, 17, 17)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8)))
                        .addGap(12, 12, 12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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

    private void btnInicioSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInicioSesionActionPerformed
        iniciarSesion();
    }//GEN-LAST:event_btnInicioSesionActionPerformed

    private void txtUsuarioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUsuarioFocusGained
        if(txtUsuario.getText().equals("Enter Username")){
            txtUsuario.setText("");
            txtUsuario.setForeground(Color.BLACK);
        }      
    }//GEN-LAST:event_txtUsuarioFocusGained

    private void txtUsuarioFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUsuarioFocusLost
        if(txtUsuario.getText().equals("")){
            txtUsuario.setText("Enter Username");
            txtUsuario.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_txtUsuarioFocusLost

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioActionPerformed

    private void btnInicioSesionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnInicioSesionMouseClicked
       
    }//GEN-LAST:event_btnInicioSesionMouseClicked

    private void btnInicioSesionMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnInicioSesionMouseReleased

    }//GEN-LAST:event_btnInicioSesionMouseReleased

    private void btnInicioSesionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnInicioSesionMouseEntered
        if (!isMuted) {
            new Funciones().audio("iniciar_sesion");
        }
    }//GEN-LAST:event_btnInicioSesionMouseEntered

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

    private void btnMuteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMuteMouseEntered
        
    }//GEN-LAST:event_btnMuteMouseEntered

    private void btnContrasenaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContrasenaActionPerformed
        // Mostrar la contraseña
        if (txtContra.getEchoChar() != (char) 0) {
            txtContra.setEchoChar((char) 0);
            btnContrasena.setIcon(new ImageIcon(InicioSesion.class.getResource("/icons/eye_opened.png")));
        } else {
            txtContra.setEchoChar('*');
            btnContrasena.setIcon(new ImageIcon(InicioSesion.class.getResource("/icons/eye_closed.png")));
        }

        mostrar = !mostrar;
    }//GEN-LAST:event_btnContrasenaActionPerformed

    private void btnRegistrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRegistrarMouseClicked

    private void btnRegistrarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarMouseEntered
        if (!isMuted) {
            new Funciones().audio("registrar");
        }
    }//GEN-LAST:event_btnRegistrarMouseEntered

    private void btnRegistrarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRegistrarMouseReleased

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        Registrar registro = new Registrar();
        registro.setVisible(true);
        dispose(); 
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void txtContraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtContraActionPerformed

    private void txtContraMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtContraMouseEntered
        if (!isMuted) {
            new Funciones().audio("contraseña");
        }
    }//GEN-LAST:event_txtContraMouseEntered

    private void txtUsuarioMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtUsuarioMouseEntered
        if (!isMuted) {
            new Funciones().audio("ingresa_usuario");
        }
    }//GEN-LAST:event_txtUsuarioMouseEntered

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
            java.util.logging.Logger.getLogger(InicioSesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InicioSesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InicioSesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InicioSesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InicioSesion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContrasena;
    private javax.swing.JButton btnInicioSesion;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField txtContra;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
