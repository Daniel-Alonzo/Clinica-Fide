/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mycompany.clinica;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class RegistrarAsociado extends javax.swing.JFrame {
    private Clip clip;
    private boolean isMuted = true;    
    private int id = 0;
    
    // CONSTRUCTOR
    public RegistrarAsociado() {
        initComponents();
        btnVolver.setToolTipText("Volver a apartado anterior.");
        btnCerrarSesion.setToolTipText("Haz clic aquí para salir de tu cuenta de manera segura.");
        btnRegistrar.setToolTipText("Haz clic aquí para registrar al personal asociado.");
        setLocationRelativeTo(null);
        setTitle("Registrar Asociado");
        setResizable(false);
        ImageIcon originalImage = new ImageIcon("src/main/java/mycompany/clinica/img/Registrador tran.png");
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
        
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT * FROM especialidades;";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                String item = resultado.getString("nombre");
                comboArea.addItem(item);
            }
            resultado.close();
            sentencia.close();
            
            ((AbstractDocument) textFecha.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[/|0-9]+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
                public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    if (text.matches("[/|0-9]+")) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            });    
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    } //FIN CONSTRUCTOR
    
    //Registra nuevos usarios en la BD, su usuario y algo de información del paciente
    void registrarUsuario() {  
        Funciones f = new Funciones();
        try{
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            
            //Válida que el usuario ingresado no exista (Que no se repita)
            String consulta = "SELECT nombre FROM usuarios";
            try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                ResultSet resultado = sentencia.executeQuery();                
                while (resultado.next()) {
                    String user = resultado.getString("nombre");                    
                    if( user.equals(textUsuario.getText() ) ) {
                        f.mostrarMensaje("¡El usuario ingresado ya se encuentra en uso!");
                        return;
                    }
                }
                resultado.close();
                sentencia.close();
            }
            String area = comboArea.getSelectedItem().toString();
            consulta = "SELECT id FROM especialidades WHERE nombre = ? ";
            try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                sentencia.setString(1,area);
                ResultSet resultado = sentencia.executeQuery();                
                while (resultado.next()) {
                    id = resultado.getInt("id");                    
                }
                resultado.close();
                sentencia.close();
            }
            
            consulta ="INSERT INTO usuarios (id,nombre,contrasena,id_rol) VALUES (NULL, ?, ?, 3);";
            try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                sentencia.setString(1,textUsuario.getText());
                sentencia.setString(2, textContra.getText());
                sentencia.executeUpdate();
                sentencia.close();
            }            
            
            
            
            consulta ="INSERT INTO medicos (id,usuario,nombre_completo,genero,fecha_nacimiento,id_especialidad) VALUES (NULL, ?, ?, ?,str_to_date(?, '%d/%m/%Y'),?);";
            try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                sentencia.setString(1,textUsuario.getText());
                sentencia.setString(2, textNombre.getText());
                sentencia.setString(3,comboGenero.getSelectedItem().toString());
                sentencia.setString(4, textFecha.getText());
                sentencia.setInt(5, id);
                sentencia.executeUpdate();
                sentencia.close();
            }            
            f.mostrarMensaje("¡Usuario registrado correctamente!");
            textNombre.setText("");
            textUsuario.setText("");
            textContra.setText("");
            textFecha.requestFocus();
        }catch(SQLException e){
            //e.printStackTrace();
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                    + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
        }
    } 
    
    //Verifica que la fecha ingresada sea válida y que el usuario sea mayor de edad
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

            //El año de nacimiento debe ser mayor a 1900 y menor a 2005 (La persona debe ser mayor de 18)
            if(anio < 1900 || anio > 2005) {
                f.mostrarMensaje("¡Ingrese un año válido! (1900 - 2005)");
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
        textUsuario = new javax.swing.JTextField();
        btnRegistrar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        textContra = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        textNombre = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        textFecha = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        comboArea = new javax.swing.JComboBox<>();
        comboGenero = new javax.swing.JComboBox<>();
        btnMute = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 30)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Registrar Asociado");

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

        textUsuario.setForeground(new java.awt.Color(153, 153, 153));
        textUsuario.setText("Doctor");
        textUsuario.setToolTipText("");
        textUsuario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textUsuarioFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textUsuarioFocusLost(evt);
            }
        });
        textUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textUsuarioActionPerformed(evt);
            }
        });

        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/create_account.png"))); // NOI18N
        btnRegistrar.setText("Registrar");
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

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("*Usuario");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("*Nombre Completo");

        textContra.setForeground(new java.awt.Color(153, 153, 153));
        textContra.setText("ragaluis712");
        textContra.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textContraFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textContraFocusLost(evt);
            }
        });
        textContra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textContraActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("*Contraseña");

        textNombre.setForeground(new java.awt.Color(153, 153, 153));
        textNombre.setText("Luis Reyes");
        textNombre.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textNombreFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textNombreFocusLost(evt);
            }
        });
        textNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textNombreActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("*Fecha de nacimiento (dd/MM/yyyy)");

        textFecha.setForeground(new java.awt.Color(153, 153, 153));
        textFecha.setText("30/12/2003");
        textFecha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFechaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFechaFocusLost(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("*Género:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("*Area de Especialidad");

        comboArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboAreaActionPerformed(evt);
            }
        });

        comboGenero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hombre", "Mujer" }));
        comboGenero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboGeneroActionPerformed(evt);
            }
        });

        btnMute.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMute.setText("Act. narrador");
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
                        .addContainerGap(303, Short.MAX_VALUE)
                        .addComponent(btnMute)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCerrarSesion))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jLabel4))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(88, 88, 88)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(textUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel2)
                                            .addComponent(textNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(textContra, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel3))
                                        .addGap(73, 73, 73)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(comboGenero, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(textFecha)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel6)
                                            .addComponent(comboArea, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnRegistrar, javax.swing.GroupLayout.Alignment.TRAILING))))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVolver)
                    .addComponent(btnMute)
                    .addComponent(btnCerrarSesion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboGenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(comboArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addGap(28, 28, 28)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textContra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(36, 36, 36)
                .addComponent(btnRegistrar)
                .addGap(37, 37, 37))
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

    private void comboAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboAreaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboAreaActionPerformed

    private void comboGeneroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboGeneroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboGeneroActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        //Datos a guardar de un nuevo usuario
        String nombre, fechaNacimiento, genero, usuario, contra;
        
        //Extrae la info del JTextFiel txtUsuario y txtContra
        nombre = textNombre.getText();
        fechaNacimiento = textFecha.getText();
        genero = comboGenero.getSelectedItem().toString();
        usuario = textUsuario.getText();
        contra = textContra.getText();  
        
        Funciones f = new Funciones();        
        
        //Si alguno de los TextField está vacío, se manda una alerta y termina la ejecuión del botón  
        if(nombre.isEmpty() || fechaNacimiento.isEmpty() || genero.isEmpty()|| usuario.isEmpty() || contra.isEmpty()) {            
            f.mostrarMensaje("¡Faltan valores por ingresar!");
            return;
        }
        
        
        //Válida si se ingresaron datos
        boolean esValido = Funciones.sonCamposValidos(
                new ArrayList<String>(Arrays.asList(jLabel1.getText(), jLabel2.getText(), jLabel3.getText(), jLabel5.getText(), jLabel6.getText(), jLabel7.getText())), 
                new ArrayList<JTextField>(Arrays.asList(textUsuario, textNombre, textContra, new JTextField(fechaNacimiento), new JTextField(genero), new JTextField(comboArea.getSelectedItem().toString())))
        );
        
        if (!esValido)
            return;
        
        //Completa la fecha en caso de ser necesario, para evitar problemas con la BD
        fechaNacimiento = f.completarFecha(fechaNacimiento);
        
        //Verifca que la fecha de nacimiento ingresada sea valida
        if( verificarFecha(fechaNacimiento) == false ) return;        
        
        registrarUsuario();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void textNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textNombreActionPerformed

    private void textUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textUsuarioActionPerformed

    private void textUsuarioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textUsuarioFocusGained
        if(textUsuario.getText().equals("DoctorChapatin")){
            textUsuario.setText("");
            textUsuario.setForeground(Color.BLACK);
        } 
    }//GEN-LAST:event_textUsuarioFocusGained

    private void textUsuarioFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textUsuarioFocusLost
   if(textUsuario.getText().equals("")){
            textUsuario.setText("DoctorChapatin");
            textUsuario.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_textUsuarioFocusLost

    private void textNombreFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textNombreFocusGained
        if(textNombre.getText().equals("Daniel Alonzo")){
            textNombre.setText("");
            textNombre.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_textNombreFocusGained

    private void textNombreFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textNombreFocusLost
        if(textNombre.getText().equals("")){
            textNombre.setText("Daniel Alonzo");
            textNombre.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_textNombreFocusLost

    private void textFechaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFechaFocusGained
        if(textFecha.getText().equals("30/12/2003")){
            textFecha.setText("00/00/0000");
            textFecha.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_textFechaFocusGained

    private void textFechaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFechaFocusLost
        if(textFecha.getText().equals("00/00/0000")){
            textFecha.setText("27/11/2002");
            textFecha.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_textFechaFocusLost

    private void btnVolverMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVolverMouseEntered
       
        if (!isMuted) {
            new Funciones().audio("volver_atras");
        }
    }//GEN-LAST:event_btnVolverMouseEntered

    private void btnCerrarSesionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCerrarSesionMouseEntered
       
        if (!isMuted) {
            new Funciones().audio("cerrar_sesion");
        }
    }//GEN-LAST:event_btnCerrarSesionMouseEntered

    private void btnRegistrarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarMouseEntered
      
        if (!isMuted) {
            new Funciones().audio("registrar");
        }
    }//GEN-LAST:event_btnRegistrarMouseEntered

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

    private void textContraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textContraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textContraActionPerformed

    private void textContraFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textContraFocusLost
        if(textContra.getText().equals("")){
            textContra.setText("contraseña123");
            textContra.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_textContraFocusLost

    private void textContraFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textContraFocusGained
        if(textContra.getText().equals("contraseña123")){
            textContra.setText("");
            textContra.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_textContraFocusGained

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
            java.util.logging.Logger.getLogger(RegistrarAsociado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistrarAsociado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistrarAsociado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistrarAsociado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistrarAsociado().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> comboArea;
    private javax.swing.JComboBox<String> comboGenero;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField textContra;
    private javax.swing.JTextField textFecha;
    private javax.swing.JTextField textNombre;
    private javax.swing.JTextField textUsuario;
    // End of variables declaration//GEN-END:variables
}
