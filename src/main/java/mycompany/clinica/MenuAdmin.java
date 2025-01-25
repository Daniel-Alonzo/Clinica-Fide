/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mycompany.clinica;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.JOptionPane;

public class MenuAdmin extends javax.swing.JFrame {
    private int id;
    private Clip clip;
    private boolean isMuted = true;
   
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public MenuAdmin() {
        initComponents();
        btnCerrarSesion.setToolTipText("Haz clic aquí para salir de tu cuenta de manera segura.");
        btnInventario.setToolTipText("Haz clic aquí para consultar el inventario actual.");
        btnEstadisticas.setToolTipText("Haz clic aquí para consultar las estadísticas de \nlos medicamentos más usados y enfermedades más comunes");
        actualizarEquipo(""); // Llama al método para actualizar el equipo al abrir la ventana
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Menu Administración");
        ImageIcon originalImage = new ImageIcon("src/main/java/mycompany/clinica/img/Menu Administracion.png");
        Image scaledImage = originalImage.getImage().getScaledInstance(jPanel8.getWidth(), jPanel8.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel background = new JLabel("", scaledIcon, JLabel.CENTER);
        jPanel8.add(background);
        background.setBounds(0, 0, jPanel8.getWidth(), jPanel8.getHeight());
        
        // En caso de cierre accidental
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Funciones.confirmarCierre() == 0) dispose();
           }
        });
    }    
    
    private void actualizarEquipo(String equipo) {   
        Funciones f = new Funciones();
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);

            String consulta;
            if (equipo.isEmpty()) consulta = "SELECT * FROM equipos";
            else consulta = "SELECT * FROM equipos"
                    + " WHERE nombre LIKE ?";

            PreparedStatement sentencia = conexion.prepareStatement(consulta);                                  
            if (!equipo.isEmpty()) sentencia.setString(1, equipo + "%");                  
            ResultSet resultado = sentencia.executeQuery();

            // Variables para rastrear el artículo con la cantidad más baja
            String articuloAgotandose = null;
            int cantidadMinima = Integer.MAX_VALUE;

            while (resultado.next()) {
                String prod = resultado.getString("nombre");
                int cant = resultado.getInt("cantidad");

                // Comprobar si la cantidad está por agotarse
                if (cant < cantidadMinima) {
                    cantidadMinima = cant;
                    articuloAgotandose = prod;
                }
            }

            resultado.close();
            sentencia.close();

            // Verificar si hay algún artículo por agotarse
            if (articuloAgotandose != null) {
                jLabel24.setText(articuloAgotandose + " (" + cantidadMinima + " unidades)");
            } else {
                jLabel24.setText("No hay artículos por agotarse.");
            }

        } catch (SQLException e) {
            //e.printStackTrace();
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                        + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
        }
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel8 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        btnInventario = new javax.swing.JButton();
        btnEstadisticas = new javax.swing.JButton();
        btnRegistrarTrabajador = new javax.swing.JButton();
        btnPagos = new javax.swing.JButton();
        btnMute = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel22.setFont(new java.awt.Font("Segoe UI", 3, 30)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Adminstración");

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

        jLabel23.setText("Suministros por agotarse:");

        btnInventario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnInventario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/storage.png"))); // NOI18N
        btnInventario.setText("Inventario");
        btnInventario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnInventario1(evt);
            }
        });
        btnInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarioActionPerformed(evt);
            }
        });

        btnEstadisticas.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEstadisticas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/statistics.png"))); // NOI18N
        btnEstadisticas.setText("Estadisticas");
        btnEstadisticas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnEstadisticasMouseEntered(evt);
            }
        });
        btnEstadisticas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEstadisticasActionPerformed(evt);
            }
        });

        btnRegistrarTrabajador.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrarTrabajador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add_partners.png"))); // NOI18N
        btnRegistrarTrabajador.setText("Registrar trabajador");
        btnRegistrarTrabajador.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnRegistrarTrabajadorMouseEntered(evt);
            }
        });
        btnRegistrarTrabajador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarTrabajadorActionPerformed(evt);
            }
        });

        btnPagos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPagos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/wallet.png"))); // NOI18N
        btnPagos.setText("Pagos ");
        btnPagos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPagosMouseEntered(evt);
            }
        });
        btnPagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagosActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(btnInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEstadisticas, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnRegistrarTrabajador)
                        .addGap(18, 18, 18)
                        .addComponent(btnPagos, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(btnMute)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCerrarSesion)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))
                        .addGap(29, 29, 29))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMute)
                    .addComponent(btnCerrarSesion))
                .addGap(30, 30, 30)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInventario)
                    .addComponent(btnEstadisticas)
                    .addComponent(btnRegistrarTrabajador)
                    .addComponent(btnPagos))
                .addGap(18, 18, 18)
                .addComponent(jLabel23)
                .addGap(18, 18, 18)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(113, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void btnRegistrarTrabajadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarTrabajadorActionPerformed
        RegistrarAsociado registrarAsociado = new RegistrarAsociado();
        registrarAsociado.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnRegistrarTrabajadorActionPerformed

    private void btnPagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagosActionPerformed
        Pagos pagos = new Pagos();
        pagos.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnPagosActionPerformed

    private void btnCerrarSesionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCerrarSesionMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("cerrar_sesion");
        }
    }//GEN-LAST:event_btnCerrarSesionMouseEntered

    private void btnInventarioMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnInventarioMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("inventario");
        }
    }//GEN-LAST:event_btnInventarioMouseEntered

    private void btnRegistrarTrabajadorMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarTrabajadorMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("registrar_trabajador");
        }
    }//GEN-LAST:event_btnRegistrarTrabajadorMouseEntered

    private void btnPagosMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPagosMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("pagos");
        }
    }//GEN-LAST:event_btnPagosMouseEntered

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

    private void btnInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarioActionPerformed
        InventarioAdmin inventarioA = new InventarioAdmin();
        inventarioA.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnInventarioActionPerformed

    private void btnInventario1(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnInventario1
        // TODO add your handling code here:
    }//GEN-LAST:event_btnInventario1

    private void btnEstadisticasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEstadisticasActionPerformed
        Estadisticas estadisticas = new Estadisticas();
        estadisticas.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnEstadisticasActionPerformed

    private void btnEstadisticasMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEstadisticasMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("estadisticas");
        }
    }//GEN-LAST:event_btnEstadisticasMouseEntered

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
            java.util.logging.Logger.getLogger(MenuAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuAdmin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnEstadisticas;
    private javax.swing.JButton btnInventario;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnPagos;
    private javax.swing.JButton btnRegistrarTrabajador;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JPanel jPanel8;
    // End of variables declaration//GEN-END:variables
}
