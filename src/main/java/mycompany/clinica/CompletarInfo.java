/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mycompany.clinica;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.JLabel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.text.*;

public class CompletarInfo extends javax.swing.JFrame {
    private Clip clip;
    private boolean isMuted = true;
    private String user;
    private int id;
    int id_sange; 
    
    public CompletarInfo(int id) {
        initComponents();
        
        Funciones f = new Funciones();
        
        btnVolver.setToolTipText("Volver a apartado anterior.");
        btnCerrarSesion.setToolTipText("Haz clic aquí para salir de tu cuenta de manera segura.");
        btnGuardar.setToolTipText("Haz clic aquí para guardar tu información personal.");
        setLocationRelativeTo(null);
        setTitle("Completar Información");
        setResizable(false);
        ImageIcon originalImage = new ImageIcon("src/main/java/mycompany/clinica/img/Completar Información.png");
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
        KeyStroke ctrlQ = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK);
        
        jPanel1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(volver, "volverAtras");
        jPanel1.getActionMap().put("volverAtras", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnVolver.doClick();
            }
        });
        
        jPanel1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enter, "guardar");
        jPanel1.getActionMap().put("guardar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnGuardar.doClick();
            }
        });
        
        jPanel1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlQ, "cerrarSesion");
        jPanel1.getActionMap().put("cerrarSesion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCerrarSesion.doClick();
            }
        });
                
        //Colocar los tipos de sangre en el ComboBoxTipoSangre para ser seleccionados
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT * FROM tipos_sangre;";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                String item = resultado.getString("nombre");
                cboTipoSangre.addItem(item);
                id_sange = resultado.getInt("id");
            }
            resultado.close();
            sentencia.close();
            
            String consultas = "SELECT * FROM pacientes WHERE id = ?;";
            PreparedStatement sentencias = conexion.prepareStatement(consultas);
            sentencias.setInt(1, id);
            ResultSet resultados = sentencias.executeQuery();
            while (resultados.next()) {
                String a = resultados.getString("alergias");
                float e = resultados.getFloat("estatura");
                float p = resultados.getFloat("peso");
                txtAlergias.setText(a);
                txtAlergias.setForeground(Color.BLACK);
                txtEstatura.setText(e+"");
                txtEstatura.setForeground(Color.BLACK);
                txtPeso.setText(p+"");
                txtPeso.setForeground(Color.BLACK);
            }
            resultado.close();
            sentencia.close();
       
        } catch (SQLException e) {
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                    + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
        }
        
        //Válida que el txtFieldPeso solo permita valores númericos
        ((AbstractDocument) txtPeso.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[.|0-9]+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[.|0-9]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        
        //Válida que el txtFieldEstatura solo permita valores númericos
        ((AbstractDocument) txtEstatura.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[.|0-9]+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[.|0-9]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
    
    public void setId(int id) { this.id = id; }
    
    public int getId() { return id; }
    
    public void setUsuario(String user) { this.user = user; }
    
    //Función para conectar a la BD y actualizar los datos de la persona
    void actualizarDatos() {
        Funciones f = new Funciones();
        
        //Actuzaliza los campos del usuario actual
        float pesos = Float.parseFloat(txtPeso.getText());
        float estaturas = Float.parseFloat(txtEstatura.getText());

        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "UPDATE pacientes SET id_tipo_sangre = ?, alergias = ?, estatura = ?, peso = ?"
                    + "         WHERE usuario = ?;";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1, id_sange);
            sentencia.setString(2, txtAlergias.getText());
            sentencia.setFloat(3, estaturas);
            sentencia.setFloat(4, pesos);
            sentencia.setString(5, user);
            
            int filasAfectadas = sentencia.executeUpdate();
            System.out.println("Filas afectadas: " + filasAfectadas);
            
            sentencia.close();
            conexion.close();
            
            f.mostrarMensaje("¡Datos actualizados correctamente!");
        } catch (SQLException e) {
                        
        } //FIN DEL TRY CATCH
    } //Fin de la función actualizarDatos
    
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
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cboTipoSangre = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtEstatura = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPeso = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnMute = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAlergias = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Información");

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

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Tipo de sangre:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Alergias:");

        cboTipoSangre.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cboTipoSangreMouseEntered(evt);
            }
        });
        cboTipoSangre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTipoSangreActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Estatura: (m)");

        txtEstatura.setForeground(new java.awt.Color(153, 153, 153));
        txtEstatura.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEstatura.setText("1.83");
        txtEstatura.setActionCommand("null");
        txtEstatura.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtEstaturaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtEstaturaFocusLost(evt);
            }
        });
        txtEstatura.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtEstaturaMouseEntered(evt);
            }
        });
        txtEstatura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEstaturaActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Peso: (kg)");

        txtPeso.setForeground(new java.awt.Color(153, 153, 153));
        txtPeso.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPeso.setText("87");
        txtPeso.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPesoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPesoFocusLost(evt);
            }
        });
        txtPeso.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtPesoMouseEntered(evt);
            }
        });
        txtPeso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesoActionPerformed(evt);
            }
        });

        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnGuardarMouseEntered(evt);
            }
        });
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
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

        txtAlergias.setColumns(20);
        txtAlergias.setLineWrap(true);
        txtAlergias.setRows(5);
        txtAlergias.setWrapStyleWord(true);
        txtAlergias.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAlergiasFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAlergiasFocusLost(evt);
            }
        });
        txtAlergias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtAlergiasMouseEntered(evt);
            }
        });
        jScrollPane1.setViewportView(txtAlergias);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1)
                            .addComponent(cboTipoSangre, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(btnGuardar))
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(txtEstatura, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(txtPeso))))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(227, Short.MAX_VALUE)
                .addComponent(btnMute)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrarSesion)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVolver)
                    .addComponent(btnCerrarSesion)
                    .addComponent(btnMute))
                .addGap(34, 34, 34)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboTipoSangre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEstatura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnGuardar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
         }else {
        // No hacer nada, el usuario decidió no cerrar sesión
        }
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        Funciones f = new Funciones();
        String tipoSangre, estatura, peso;
        
        tipoSangre = cboTipoSangre.getSelectedItem().toString();
        estatura = txtEstatura.getText();
        peso = txtPeso.getText();        
        
        //Válida que todos los campos estén llenos
        if( tipoSangre.isEmpty() || estatura.isEmpty() || peso.isEmpty() ) {                        
            f.mostrarMensaje("¡Faltan valores por ingresar!");
            return;
        }
        
        //Válida que la estatura esté entre los rangos
        if( Float.parseFloat(estatura ) < 0.7 || Float.parseFloat(estatura) > 2.5 ) {            
            f.mostrarMensaje("¡Ingrese una estatura válida! (0.7 m - 2.5 m)");
            return;
        }
        
        //Válida que el peso esté entre los rangos
        if( Float.parseFloat(peso ) < 30 || Float.parseFloat(peso ) > 250 ) {            
            f.mostrarMensaje("¡Ingrese un peso válido! (30 kg - 250 kg)");
            return;
        }                

        actualizarDatos();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void cboTipoSangreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTipoSangreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboTipoSangreActionPerformed

    private void txtEstaturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstaturaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEstaturaActionPerformed

    private void txtPesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesoActionPerformed

    private void txtEstaturaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEstaturaFocusGained
        if(txtEstatura.getText().equals("1.83")){
            txtEstatura.setText("");
            txtEstatura.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtEstaturaFocusGained

    private void txtEstaturaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEstaturaFocusLost
        if(txtEstatura.getText().equals("")){
            txtEstatura.setText("1.83");
            txtEstatura.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_txtEstaturaFocusLost

    private void txtPesoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPesoFocusGained
        if(txtPeso.getText().equals("87")){
            txtPeso.setText("");
            txtPeso.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtPesoFocusGained

    private void txtPesoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPesoFocusLost
        if(txtPeso.getText().equals("")){
            txtPeso.setText("87");
            txtPeso.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_txtPesoFocusLost

    private void btnVolverMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVolverMouseEntered
        if (!isMuted) {
            new Funciones().audio("volver");
        }
    }//GEN-LAST:event_btnVolverMouseEntered

    private void btnCerrarSesionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCerrarSesionMouseEntered
        if (!isMuted) {
            new Funciones().audio("cerrar_sesion");
        }
    }//GEN-LAST:event_btnCerrarSesionMouseEntered

    private void btnGuardarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarMouseEntered
        if (!isMuted) {
            new Funciones().audio("guardar");
        }
    }//GEN-LAST:event_btnGuardarMouseEntered

    private void btnMuteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMuteMouseEntered
        // TODO add your handling code here:        
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

    private void cboTipoSangreMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cboTipoSangreMouseEntered
        if (!isMuted) {
            new Funciones().audio("tipo_de_sangre");
        }
    }//GEN-LAST:event_cboTipoSangreMouseEntered

    private void txtEstaturaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtEstaturaMouseEntered
        if (!isMuted) {
            new Funciones().audio("estatura");
        }
    }//GEN-LAST:event_txtEstaturaMouseEntered

    private void txtPesoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPesoMouseEntered
        if (!isMuted) {
            new Funciones().audio("peso");
        }
    }//GEN-LAST:event_txtPesoMouseEntered

    private void txtAlergiasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAlergiasFocusGained
        if(txtAlergias.getText().equals("Ninguna")){
            txtAlergias.setText("");
            txtAlergias.setForeground(Color.BLACK);
        }  
    }//GEN-LAST:event_txtAlergiasFocusGained

    private void txtAlergiasFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAlergiasFocusLost
        if(txtAlergias.getText().equals("")){
            txtAlergias.setText("Ninguna");
            txtAlergias.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_txtAlergiasFocusLost

    private void txtAlergiasMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtAlergiasMouseEntered
        if (!isMuted) {
            new Funciones().audio("alergias");
        }
    }//GEN-LAST:event_txtAlergiasMouseEntered

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
            java.util.logging.Logger.getLogger(CompletarInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CompletarInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CompletarInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CompletarInfo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cboTipoSangre;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtAlergias;
    private javax.swing.JTextField txtEstatura;
    private javax.swing.JTextField txtPeso;
    // End of variables declaration//GEN-END:variables
}
