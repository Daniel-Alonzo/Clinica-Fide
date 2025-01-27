/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mycompany.clinica;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GenerarCita extends javax.swing.JFrame {    
    private String user;
    private int id;
    private Clip clip;
    private boolean isMuted = true;    
   
    public void setUsuario(String user) { this.user = user; }        
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public GenerarCita() {
        initComponents();
        btnVolver.setToolTipText("Volver a apartado anterior.");
        btnCerrarSesion.setToolTipText("Haz clic aquí para salir de tu cuenta de manera segura.");
        btnGenerarCita.setToolTipText("Haz clic aquí para agendar la fecha de la cita.");
        setLocationRelativeTo(null);
        setTitle("Generar cita");
        setResizable(false);
        ImageIcon originalImage = new ImageIcon("src/main/java/mycompany/clinica/img/Generar CIta.png");
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
        
        //Buscar especialidades
        Funciones f = new Funciones();
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            
            //Consulta
            String consulta = "SELECT * FROM especialidades";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);            
            ResultSet resultado = sentencia.executeQuery();
            
            while (resultado.next()) {
                String area = resultado.getString("nombre");
                
                cbxAreaAtencion.addItem(area);                         
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                    + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
        }
        
        this.limitesCaracteres();
        this.agregarEspecialidades();
    }
    
    // Establecer y mostrar limites de caracteres en la entrada de Sintomas
    private void limitesCaracteres() {
        this.txtSintomas.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                this.validarEntrada();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                this.validarEntrada();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                this.validarEntrada();
            }
            
            // Válidacion de la entrada
            public void validarEntrada() {
                int longitud = txtSintomas.getText().length();
                lblSintomasErrorLimite.setText(longitud + "/200");
                if (longitud > 200){
                    lblSintomasErrorLimite.setForeground(Color.RED);
                    btnGenerarCita.setEnabled(false);
                } else {
                    lblSintomasErrorLimite.setForeground(Color.BLACK);
                    btnGenerarCita.setEnabled(true);
                }
            }
        });
    }
    
    // Mostrar especialidades en Select
    public void agregarEspecialidades() {
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT * FROM especialidades;";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();
            
            while (resultado.next()) {
                String item = resultado.getString("nombre");
                this.cbxAreaAtencion.addItem(item);
            }
            
            resultado.close();
            sentencia.close();
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
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

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        btnVolver = new javax.swing.JButton();
        lblFecha = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnGenerarCita = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btnMute2 = new javax.swing.JButton();
        lblSintomasErrorLimite = new javax.swing.JLabel();
        txtFecha = new com.toedter.calendar.JDateChooser();
        cbxAreaAtencion = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtSintomas = new javax.swing.JTextArea();

        jButton1.setText("Cerrar sesion");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Paciente: Usuario");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Agendar cita");

        btnCerrarSesion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCerrarSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/log_out.png"))); // NOI18N
        btnCerrarSesion.setText("Cerrar sesion");
        btnCerrarSesion.setToolTipText("");
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

        lblFecha.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblFecha.setText("Fecha:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Area de atencion:");

        btnGenerarCita.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGenerarCita.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/schedule.png"))); // NOI18N
        btnGenerarCita.setText("Generar cita");
        btnGenerarCita.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnGenerarCitaMouseEntered(evt);
            }
        });
        btnGenerarCita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarCitaActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Sintomas:");

        btnMute2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMute2.setText("Activar narrador");
        btnMute2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMute2MouseEntered(evt);
            }
        });
        btnMute2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMute2ActionPerformed(evt);
            }
        });

        lblSintomasErrorLimite.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        lblSintomasErrorLimite.setText("0/200");

        txtFecha.setToolTipText("");

        txtSintomas.setColumns(20);
        txtSintomas.setLineWrap(true);
        txtSintomas.setRows(5);
        txtSintomas.setToolTipText("Ingrese los síntomas");
        txtSintomas.setWrapStyleWord(true);
        txtSintomas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtSintomasMouseEntered(evt);
            }
        });
        jScrollPane3.setViewportView(txtSintomas);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 229, Short.MAX_VALUE)
                        .addComponent(btnMute2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVolver)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCerrarSesion))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(lblFecha)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbxAreaAtencion, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnGenerarCita)
                                    .addComponent(jScrollPane3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblSintomasErrorLimite)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMute2)
                    .addComponent(btnVolver)
                    .addComponent(btnCerrarSesion))
                .addGap(27, 27, 27)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(lblFecha)
                .addGap(10, 10, 10)
                .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxAreaAtencion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblSintomasErrorLimite, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGenerarCita)
                .addContainerGap(33, Short.MAX_VALUE))
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //No lleva nada se bugeo
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cbxAreaAtencionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxAreaAtencionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxAreaAtencionActionPerformed

    private void btnMute2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMute2ActionPerformed
        isMuted = !isMuted;
        if (isMuted) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            btnMute2.setText("Activar narrador");
        } else {
            btnMute2.setText("Desactivar narrador");
        }
    }//GEN-LAST:event_btnMute2ActionPerformed

    private void btnMute2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMute2MouseEntered

    }//GEN-LAST:event_btnMute2MouseEntered

    private void btnGenerarCitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarCitaActionPerformed
        Funciones f = new Funciones();

        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");

        //Obtiene los datos del form
        String fecha = "";
        Calendar calendar = txtFecha.getCalendar();

        if( calendar != null )
        fecha = date.format(txtFecha.getCalendar().getTime());
        String area = cbxAreaAtencion.getSelectedItem().toString();
        String sintomas = txtSintomas.getText();

        //Válida si se ingresaron datos
        boolean esValido = Funciones.sonCamposValidos(
            new ArrayList<>(Arrays.asList(lblFecha.getText(), jLabel4.getText(), jLabel5.getText())),
            new ArrayList<>(Arrays.asList(new JTextField(fecha), new JTextField(area)))
        );

        if (!esValido)
        return;

        //Verifca que la fecha de nacimiento ingresada sea valida
        fecha = f.completarFecha(fecha);
        if( f.verificarFecha(fecha) == false ) {
            f.mostrarMensaje("¡Ingrese una fecha valida!");
            txtFecha.requestFocus();
            return;
        }

        //Conecta la BD y guarda la CITA
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);

            String consulta = "SELECT * FROM citas WHERE id_paciente = ? AND fecha LIKE CONCAT('%', STR_TO_DATE(?, '%d/%m/%Y'), '%')";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1, id);
            sentencia.setString(2, fecha);

            ResultSet resultado = sentencia.executeQuery();

            if(resultado.next()){
                resultado.close();
                sentencia.close();
                txtFecha.requestFocus();
                f.mostrarMensaje("¡Ya tiene una cita pendiente para el día ingresado!");
                return;
            }

            consulta = "INSERT INTO citas (id_paciente, fecha, area_atencion, sintomas, consultada)"
            + " VALUES (?, str_to_date(?, '%d/%m/%Y'), ?, ?, 'No');";
            sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1, id);
            sentencia.setString(2, fecha);
            sentencia.setString(3, area);
            sentencia.setString(4, sintomas);

            sentencia.executeUpdate();
            sentencia.close();

            f.mostrarMensaje("¡Cita generada correctamente!!");
            cbxAreaAtencion.setSelectedIndex(0);
            txtSintomas.setText("");
            txtFecha.requestFocus();

        } catch(SQLException e){
            // Usuario no existe en tabla 'pacientes'
            if (e.getErrorCode() == 1452)
            Funciones.mostrarMensajeAcompletarInformacion();
            else
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
            System.err.println("SQL Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();

        } //FIN DEL TRY CATCH

    }//GEN-LAST:event_btnGenerarCitaActionPerformed

    private void btnGenerarCitaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGenerarCitaMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("generar_cita");
        }
    }//GEN-LAST:event_btnGenerarCitaMouseEntered

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        if (Funciones.confirmarVolver() == JOptionPane.YES_OPTION) {
            MenuPaciente menuP = new MenuPaciente();
            int id = getId();
            menuP.setId(id);
            menuP.setUsuario(user);
            menuP.mostrarFechaCita(id);
            menuP.setVisible(true);
            dispose();
        }
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnVolverMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVolverMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("volver_atras");
        }
    }//GEN-LAST:event_btnVolverMouseEntered

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

    private void btnCerrarSesionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCerrarSesionMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("cerrar_sesion");
        }
    }//GEN-LAST:event_btnCerrarSesionMouseEntered

    private void txtSintomasMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSintomasMouseEntered
        if (!isMuted) {
            new Funciones().audio("sintomas");
        }
    }//GEN-LAST:event_txtSintomasMouseEntered

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
            java.util.logging.Logger.getLogger(GenerarCita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GenerarCita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GenerarCita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GenerarCita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GenerarCita().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnGenerarCita;
    private javax.swing.JButton btnMute2;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cbxAreaAtencion;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblSintomasErrorLimite;
    private com.toedter.calendar.JDateChooser txtFecha;
    private javax.swing.JTextArea txtSintomas;
    // End of variables declaration//GEN-END:variables
}
