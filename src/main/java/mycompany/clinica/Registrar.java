/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mycompany.clinica;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.text.*;

public class Registrar extends javax.swing.JFrame {
    private Clip clip;
    private boolean isMuted = true;
    private boolean mostrar = false;
    
    // CONSTRUCTOR
    public Registrar() {
        initComponents();
        btnVolver.setToolTipText("Volver a apartado anterior.");
        btnRegistrar.setToolTipText("Haz clic aquí para registrar un nuevo paciente.");
        setLocationRelativeTo(null);
        setTitle("Registro de paciente");
        setResizable(false);
        ImageIcon originalImage = new ImageIcon("src/main/java/mycompany/clinica/img/Registro Pacientes.png");
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
        
        KeyStroke volver = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        
        jPanel1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(volver, "volverAtras");
        jPanel1.getActionMap().put("volverAtras", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnVolver.doClick();
            }
        });
        
        jPanel1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enter, "registrar");
        jPanel1.getActionMap().put("registrar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnRegistrar.doClick();
            }
        });
    } //FIN CONSTRUCTOR    
    
    //Registra un nuevo usuario
    void registrarUsuario() {  
        Funciones f = new Funciones();
        try{
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            
            //Válida que el usuario ingresado no exista (Que no se repita)
            String consulta = "SELECT nombre FROM usuarios";
            try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                ResultSet resultado = sentencia.executeQuery();   
                
                // Si ya hay un usuario con el mismo USUARIO, se detiene la operación y se le notifica al usuario
                while (resultado.next()) {
                    String user = resultado.getString("nombre");                    
                    if( user.equals(txtUsuario.getText() ) ) {
                        f.mostrarMensaje("¡El usuario ingresado ya se encuentra en uso!");
                        txtUsuario.setText("");
                        txtUsuario.requestFocus();
                        resultado.close();
                        sentencia.close();
                        
                        return;
                    }
                }
                
                resultado.close();
                sentencia.close();
            }
            
            // Si no ocurre ningún error, el usuario se guarda en la BD
            consulta ="INSERT INTO usuarios (id,nombre,contrasena,id_rol) VALUES (NULL, ?, ?, 4);";
            try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                sentencia.setString(1,txtUsuario.getText());
                sentencia.setString(2, txtContra.getText());
                sentencia.executeUpdate();
                sentencia.close();
            }            
            
            //Formato de la fecha
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            
            // Se guarda el paciente en su respectiva tabla
            consulta ="INSERT INTO pacientes (id, usuario, nombre_completo, fecha_nacimiento, genero, id_tipo_sangre, alergias,"
                    + "estatura, peso, foto) VALUES (NULL, ?, ?, str_to_date(?, '%d/%m/%Y'), ?,NULL,NULL,NULL,NULL,NULL);";
            try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                sentencia.setString(1,txtUsuario.getText());
                sentencia.setString(2, txtNombre.getText());
                sentencia.setString(3, format.format(txtFecha.getCalendar().getTime()));
                sentencia.setString(4, cboGenero.getSelectedItem().toString());
                sentencia.executeUpdate();
                sentencia.close();
            }                        
            
            txtNombre.setText("");            
            txtUsuario.setText("");
            txtContra.setText("");
            
            txtNombre.requestFocus();
            f.mostrarMensaje("¡Usuario registrado correctamente!"); 
            
        } catch(SQLException e) {
            //e.printStackTrace();
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                    + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
        } //FIN TRY CATCH
        
    } //FIN REGISTRO    
    
    //Comprueba que la fecha está en el formato que se especifica DD/MM/YYYY
    boolean verificarFecha(String fecha) {        
        Funciones f = new Funciones();
        //Separa la feha en TRES cadenas (DÍA, MES, AÑO), si falla es porque es una fecha incorrecta
        String partes[] = fecha.split("/"); 
        if(partes.length != 3) {
            f.mostrarMensaje("¡La fecha está incompleta!");
            return false;
        }
        else {
            int diasMes[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            int dia, mes, anio;                        
            
            //Convierte los valores ingresados para comprobar la fecha como tal           
            dia = Integer.parseInt(partes[0]);
            mes = Integer.parseInt(partes[1]);
            anio = Integer.parseInt(partes[2]);

            //El año de nacimiento debe ser mayor a 1900 y menor a 2018 (La persona debe ser mayor de 18)
            if(anio < 1900 || anio > 2018) {
                f.mostrarMensaje("¡Ingrese un año válido! (1900 - 2018)");
                return false;
            }

            //Verifica si el día ingresado está entre el rango de días de un mes (Válida bisiestos)
            if(anio % 4 == 0 && (anio % 100 != 0 || anio % 400 == 0)) diasMes[1] = 29;
            if(mes < 1 || mes > 12) {
                f.mostrarMensaje("¡Ingrese un mes válido!");
                return false;
            }

            //Verifica que el més esté entre los rangos especificados
            if(dia < 1 || dia > diasMes[mes - 1]) {
                f.mostrarMensaje("¡Ingrese un día válido!");
                return false;
            }
        }                
        
        return true;
        
    } //FIN COMPROBAR FECHA

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnMute = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cboGenero = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnVolver = new javax.swing.JButton();
        btnRegistrar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnMute1 = new javax.swing.JButton();
        txtFecha = new com.toedter.calendar.JDateChooser();
        txtContra = new javax.swing.JPasswordField();
        btnContrasena = new javax.swing.JButton();

        btnMute.setText("Silenciar");
        btnMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMuteActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("*Nombre completo:");

        txtNombre.setForeground(new java.awt.Color(153, 153, 153));
        txtNombre.setText("Daniel Alonzo");
        txtNombre.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNombreFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNombreFocusLost(evt);
            }
        });
        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("*Fecha de nacimiento");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("*Genero:");

        cboGenero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hombre", "Mujer" }));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("*Usuario:");

        txtUsuario.setForeground(new java.awt.Color(153, 153, 153));
        txtUsuario.setText("Dany");
        txtUsuario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUsuarioFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUsuarioFocusLost(evt);
            }
        });
        txtUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("*Contraseña:");

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

        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/create_account.png"))); // NOI18N
        btnRegistrar.setText("Crear cuenta");
        btnRegistrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnRegistrarMouseEntered(evt);
            }
        });
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Alt+<-");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Enter");

        btnMute1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMute1.setText("Activar narrador");
        btnMute1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMute1MouseEntered(evt);
            }
        });
        btnMute1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMute1ActionPerformed(evt);
            }
        });

        txtFecha.setToolTipText("");
        txtFecha.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtFechaPropertyChange(evt);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(214, 214, 214)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 262, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(143, 143, 143))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnRegistrar)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel4)
                        .addComponent(jLabel3)
                        .addComponent(jLabel2)
                        .addComponent(jLabel1)
                        .addComponent(txtUsuario)
                        .addComponent(cboGenero, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(btnContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtContra, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMute1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVolver)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMute1)
                    .addComponent(btnVolver))
                .addGap(45, 45, 45)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(5, 5, 5)
                .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboGenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtContra))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(btnRegistrar)))
                .addContainerGap())
        );

        txtFecha.getAccessibleContext().setAccessibleName("");
        txtFecha.getAccessibleContext().setAccessibleDescription("");

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

    private void btnMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMuteActionPerformed
 
    }//GEN-LAST:event_btnMuteActionPerformed

    private void txtFechaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtFechaPropertyChange
        cboGenero.requestFocus();
    }//GEN-LAST:event_txtFechaPropertyChange

    private void btnMute1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMute1ActionPerformed
        isMuted = !isMuted;
        if (isMuted) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            btnMute1.setText("Activar narrador");
        } else {
            btnMute1.setText("Desactivar narrador");
        }
    }//GEN-LAST:event_btnMute1ActionPerformed

    private void btnMute1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMute1MouseEntered

    }//GEN-LAST:event_btnMute1MouseEntered

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        //Datos a guardar de un nuevo usuario
        String nombre, genero, usuario, contra;

        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");

        String fechaNacimiento = "";
        Calendar calendar = txtFecha.getCalendar();

        //Extrae la info del JTextFiel txtUsuario y txtContra
        nombre = txtNombre.getText();
        if( calendar != null )
        fechaNacimiento = date.format(txtFecha.getCalendar().getTime());
        genero = cboGenero.getSelectedItem().toString();
        usuario = txtUsuario.getText();
        contra = txtContra.getText();

        Funciones f = new Funciones();

        // Validacion del formulario (texto)
        boolean esValido = Funciones.sonCamposValidos(
            new ArrayList<String>(Arrays.asList(jLabel1.getText(), jLabel2.getText(), jLabel4.getText(), jLabel5.getText())),
            new ArrayList<JTextField>(Arrays.asList(txtNombre, (new JTextField(fechaNacimiento)) , txtUsuario, txtContra))
        );

        if (!esValido)
        return;

        //Completa la fecha en caso de ser necesario, para evitar problemas con la BD
        fechaNacimiento = f.completarFecha(fechaNacimiento);

        //Verifca que la fecha de nacimiento ingresada sea valida
        if( verificarFecha(fechaNacimiento) == false ) return;

        //Gurda el usuario en la BD después de comprobar la información
        registrarUsuario();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnRegistrarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarMouseEntered
        if (!isMuted) {
            new Funciones().audio("crear_cuenta");
        }
    }//GEN-LAST:event_btnRegistrarMouseEntered

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed

        if (Funciones.confirmarVolver() == JOptionPane.YES_OPTION) {
            InicioSesion inicio = new InicioSesion();
            inicio.setVisible(true);
            dispose();
        }else {
            // No hacer nada, el usuario decidió no cerrar sesión
        }
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnVolverMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVolverMouseEntered
        if (!isMuted) {
            new Funciones().audio("volver_atras");
        }
    }//GEN-LAST:event_btnVolverMouseEntered

    private void txtUsuarioFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUsuarioFocusLost
        if(txtUsuario.getText().equals("")){
            txtUsuario.setText("Dany");
            txtUsuario.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_txtUsuarioFocusLost

    private void txtUsuarioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUsuarioFocusGained
        if(txtUsuario.getText().equals("Dany")){
            txtUsuario.setText("");
            txtUsuario.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtUsuarioFocusGained

    private void txtNombreFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNombreFocusLost
        if(txtNombre.getText().equals("")){
            txtNombre.setText("Daniel Alonzo");
            txtNombre.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_txtNombreFocusLost

    private void txtNombreFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNombreFocusGained
        if(txtNombre.getText().equals("Daniel Alonzo")){
            txtNombre.setText("");
            txtNombre.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtNombreFocusGained

    private void txtContraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtContraActionPerformed

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

    private void txtContraMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtContraMouseEntered
        if (!isMuted) {
            new Funciones().audio("contraseña");
        }        // TODO add your handling code here:
    }//GEN-LAST:event_txtContraMouseEntered

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioActionPerformed


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
            java.util.logging.Logger.getLogger(Registrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Registrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Registrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Registrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Registrar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContrasena;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnMute1;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cboGenero;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField txtContra;
    private com.toedter.calendar.JDateChooser txtFecha;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
