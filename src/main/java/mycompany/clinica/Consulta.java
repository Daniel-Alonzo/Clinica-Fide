package mycompany.clinica;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class Consulta extends javax.swing.JFrame {  
    private Clip clip;
    private boolean isMuted = true;
    private String user;
    private JFileChooser seleccionarArchivo;
    private int archivoSeleccionado = JFileChooser.CANCEL_OPTION;
    
    void setUser(String user) { this.user = user; }
    
    private final ArrayList<Integer> id_paciente = new ArrayList<>();
    private final ArrayList<Integer> id_cita = new ArrayList<>();
    private ArrayList<String> sintomas = new ArrayList<>();
    
    //CONSTRUCTOR
    public Consulta() {
        initComponents();
        btnVolver.setToolTipText("Volver a apartado anterior.");
        btnCerrarSesion.setToolTipText("Haz clic aquí para salir de tu cuenta de manera segura.");
        btnAgregarArchivo.setToolTipText("Haz clic aquí para adjuntar un documento.");
        btnGuardarBorrador.setToolTipText("Haz clic aquí para guardar tu progreso sin enviar la información final.");
        btnRegistrarConsulta.setToolTipText("Haz clic aquí para completar el tratamiento del paciente.");
        btnAgregarMedicamento.setToolTipText("Haz clic aquí para añadir un medicamento recetado al paciente.");
        setLocationRelativeTo(null);
        setTitle("Consulta");
        setResizable(false);
        ImageIcon originalImage = new ImageIcon("src/main/java/mycompany/clinica/img/Consulta.png");
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
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaActual = new Date();
        lblFecha.setText(lblFecha.getText() + sdf.format(fechaActual));
        
        //VERIFICA SI EL DOCTOR TIENE CITAS PENDIENTES ESE DÍA
        obtenerPacientes();
        
        // Coloca los sintomas de paciente al elegir a este
        cboPacientes.addActionListener((ActionEvent e) -> {
            if (e.getSource() == cboPacientes) {
                lblSintomas.setText(sintomas.get(cboPacientes.getSelectedIndex()));
            }
        }); //FIN DEL LISTENER
        
        //Válida que solo se ingresen números en el ID de medicamentos
        ((AbstractDocument) txtIdMedicamento.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[0-9]+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    } //FIN DEL CONSTRUCTOR
 
    //Obtiene los nombres y los sintomas de las citas pendientes de ese día
    private void obtenerPacientes() {
        //Obtiene los nombres de las personas que van a consultar el día de hoy
        Funciones f = new Funciones();
        try {
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT p.id, p.nombre_completo, c.id, c.fecha, c.sintomas, c.consultada FROM pacientes p"
                    + " INNER JOIN citas c ON p.id = c.id_paciente WHERE c.consultada = 'No';";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            ResultSet resultado = sentencia.executeQuery();  
            
            int c = 0;
            //Obtiene las CITAS PENDIENTES de los usuarios que han generado CITAS
            while (resultado.next()) {
                int cita = resultado.getInt("c.id");
                int id = resultado.getInt("p.id");
                String user = resultado.getString("p.nombre_completo");
                String sintoma = resultado.getString("c.sintomas");
                String consultada = resultado.getString("c.consultada");
                
                //Fecha de la CITA
                java.sql.Date fechaObt = resultado.getDate("c.fecha");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fechaF = sdf.format(fechaObt);
                
                //Comprar si la CITA es hoy
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");                
                LocalDate fechaCita = LocalDate.parse(fechaF, formato); 
                LocalDate hoy = LocalDate.now();
        
                //Agrega las citas del día al COMBOBOX y guarda el ID de la cita
                if( fechaCita.equals(hoy) && consultada.equals("No") ) {
                    id_paciente.add(id);
                    id_cita.add(cita);
                    sintomas.add(sintoma);
                    
                    cboPacientes.addItem(user);
                    c++;
                } //FIN DEL IF
                                
            } // FIN DEL WHILE

            resultado.close();
            sentencia.close();
            
            //Si no hay ninguna cita, se deshabilitan todos los txtField y botones inecesarios
            if(c < 1) deshabilitarConsultas();
            else lblSintomas.setText(sintomas.get(0));
            
        } catch(SQLException e) {
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                    + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
        } //FIN DEL TRY CATCH
    }
    
    // Elimina los datos ingresados después de generar la consulta
    private void borrarCampos() {
        txtTratamientos.setText("");
        txtDiagnostico.setText("");
        txtObservaciones.setText("");
        txtIdMedicamento.setText("");
        txtMedicamentos.setText("");
    }
    
    //Deshabilita las consultas porque no hay ninguna pendiente ese día
    private void deshabilitarConsultas() {        
        txtTratamientos.setEnabled(false);
        txtDiagnostico.setEnabled(false);
        txtObservaciones.setEnabled(false);
        cboPacientes.setEnabled(false);
        txtIdMedicamento.setEnabled(false);
        txtMedicamentos.setEnabled(false);
        btnAgregarArchivo.setEnabled(false);
        btnRegistrarConsulta.setEnabled(false);
        btnAgregarMedicamento.setEnabled(false);
       
        Thread pausa = new Thread(() -> {
            try { Thread.sleep(1000); }
            catch (InterruptedException e) { }
        });

        pausa.start();
        
        Funciones f = new Funciones();        
        f.mostrarMensaje("¡Sin consultas por hoy! Descanse, mi doc.");
    }
    
    //Obtiene la extensión de un archivo
    private String obtenerExtension(String nombreArchivo) {
        int lastIndex = nombreArchivo.lastIndexOf('.');
        if (lastIndex > 0) {
            return nombreArchivo.substring(lastIndex);
        }
        return "";
    }
    
    //Copia el archivo subido y lo renombra
    private String copiarArchivo(String newName) {
        String direccion = "";

        //Se selecciona el archivo
        if (archivoSeleccionado == JFileChooser.APPROVE_OPTION) {
            java.io.File archivo = seleccionarArchivo.getSelectedFile();

            try {
                // Ruta original y copia del archivo
                String rutaOriginal = "src/main/java/archivos/" + archivo.getName();
                Path original = Paths.get(rutaOriginal);
                Path destino = original;

                if (Files.exists(original)) {
                    // Si ya hay un archivo con el mismo nombre, se asigna un número al nombre
                    int c = 0;

                    while (Files.exists(destino)) {
                        String nuevo = newName + (++c) + obtenerExtension(archivo.getName());
                        destino = Paths.get("src/main/java/archivos/" + nuevo);                   
                    }

                    //Se guarda la dirección del archivo para subirla a la BD
                    direccion = destino.toString();
                    
                    //Se copia el archivo en la dirección asignada del proyecto
                    Files.copy(archivo.toPath(), destino);
                } else {                    
                    // Si el archivo no existe se copia
                    
                    String nuevo = newName + obtenerExtension(archivo.getName());
                    destino = Paths.get("src/main/java/archivos/" + nuevo);
                    
                    //Se guarda la dirección del archivo para subirla a la BD
                    direccion = destino.toString();

                    Files.copy(archivo.toPath(), destino);
                }
            } catch (IOException ex) {
                Funciones f = new Funciones();
                f.mostrarMensaje("¡Algo falló al intentar subir el archivo!");
            }
        } //FIN DEL IF

        return direccion;
    } //FIN FUNCIÓN

    
    //Genera la consulta en la BD
    private void registrarConsulta() {
        Funciones f = new Funciones();                
        
        int paciente = id_paciente.get(cboPacientes.getSelectedIndex());   
        int cita = id_cita.get(cboPacientes.getSelectedIndex());
        
        try{
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT usuario FROM pacientes WHERE id = ?;";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1, paciente);
            ResultSet resultado = sentencia.executeQuery();
            
            String nombre = "";
            int c = 0;
            
            //Obtiene el historial de CONSULTAS del paciente                
            while (resultado.next()) {
                nombre = resultado.getString("usuario"); 
                c++;
            }

            resultado.close();
            sentencia.close();
            
            //Si no hay resultados arrojados por la consulta
            if(c < 1) {
                f.mostrarMensaje("¡El ID ingresado no existe! Ingrese un paciente válido.");
                return;
            }
            
            //Si no se ha elegido un archivo para subir
            String direccion = copiarArchivo(nombre);    
            if( direccion.isEmpty() ) {
                f.mostrarMensaje("¡Archivo no seleccionado!");
                return;
            }
                    
            // SE CREA UNA NUEVA CONSULTA SOBRE LA CITA ELEGIDA
            consulta = "INSERT INTO consultas (id, id_paciente, tratamientos, diagnostico, observaciones, medicamentos,"
                    + " archivos, id_cita) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?);";
            sentencia = conexion.prepareStatement(consulta);
                        
            sentencia.setInt(1, paciente);
            sentencia.setString(2,txtTratamientos.getText());
            sentencia.setString(3,txtDiagnostico.getText());
            sentencia.setString(4,txtObservaciones.getText());
            sentencia.setString(5,txtMedicamentos.getText());
            sentencia.setString(6, direccion); 
            sentencia.setInt(7, cita);            
            sentencia.executeUpdate();
            sentencia.close();
            
            // Se actualiza el estado de CONSULTADA en la tabla CITAS
            consulta = "UPDATE citas SET consultada = 'Si' WHERE id = ?;";
            sentencia = conexion.prepareStatement(consulta);
            
            sentencia.setInt(1, cita);            
            sentencia.executeUpdate();
            sentencia.close();
            
            f.mostrarMensaje("¡Consulta registrada correctamente!");
        } catch(SQLException e) {
            f.mostrarMensaje("¡Algo falló! Intente nuevamente.");
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

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        btnVolver = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtTratamientos = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDiagnostico = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtIdMedicamento = new javax.swing.JTextField();
        btnAgregarMedicamento = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        lblArchivo = new javax.swing.JLabel();
        btnAgregarArchivo = new javax.swing.JButton();
        btnRegistrarConsulta = new javax.swing.JButton();
        txtObservaciones = new javax.swing.JTextField();
        cboPacientes = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        btnGuardarBorrador = new javax.swing.JButton();
        btnMute = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lblSintomas = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtMedicamentos = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 30)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Atender paciente");

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
        jLabel1.setText("Paciente:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Tratamiento:");

        txtTratamientos.setForeground(new java.awt.Color(153, 153, 153));
        txtTratamientos.setText("Medicamentos y inyeccion");
        txtTratamientos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTratamientosFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTratamientosFocusLost(evt);
            }
        });
        txtTratamientos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtTratamientosMouseEntered(evt);
            }
        });
        txtTratamientos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTratamientosActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Diagnostico:");

        txtDiagnostico.setForeground(new java.awt.Color(153, 153, 153));
        txtDiagnostico.setText("Gripe");
        txtDiagnostico.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiagnosticoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiagnosticoFocusLost(evt);
            }
        });
        txtDiagnostico.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtDiagnosticoMouseEntered(evt);
            }
        });
        txtDiagnostico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiagnosticoActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Observaciones:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Identificador de medicamentos:");

        txtIdMedicamento.setForeground(new java.awt.Color(153, 153, 153));
        txtIdMedicamento.setText("1");
        txtIdMedicamento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtIdMedicamentoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIdMedicamentoFocusLost(evt);
            }
        });
        txtIdMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdMedicamentoActionPerformed(evt);
            }
        });

        btnAgregarMedicamento.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAgregarMedicamento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/plus.png"))); // NOI18N
        btnAgregarMedicamento.setText("Agregar");
        btnAgregarMedicamento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAgregarMedicamentoMouseEntered(evt);
            }
        });
        btnAgregarMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarMedicamentoActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Medicamentos:");

        lblArchivo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblArchivo.setText("Agregar archivo:");
        lblArchivo.setPreferredSize(new Dimension(120,18));

        btnAgregarArchivo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAgregarArchivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add_file.png"))); // NOI18N
        btnAgregarArchivo.setText("Agregar");
        btnAgregarArchivo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAgregarArchivoMouseEntered(evt);
            }
        });
        btnAgregarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarArchivoActionPerformed(evt);
            }
        });

        btnRegistrarConsulta.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrarConsulta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        btnRegistrarConsulta.setText("Registrar consulta");
        btnRegistrarConsulta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnRegistrarConsultaMouseEntered(evt);
            }
        });
        btnRegistrarConsulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarConsultaActionPerformed(evt);
            }
        });

        txtObservaciones.setForeground(new java.awt.Color(153, 153, 153));
        txtObservaciones.setText("Moco Verde");
        txtObservaciones.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtObservacionesFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtObservacionesFocusLost(evt);
            }
        });
        txtObservaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtObservacionesMouseEntered(evt);
            }
        });

        cboPacientes.setPreferredSize(new Dimension(74,24));
        cboPacientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cboPacientesMouseEntered(evt);
            }
        });
        cboPacientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPacientesActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Sintomas:");

        lblFecha.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblFecha.setText("Fecha:");

        btnGuardarBorrador.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuardarBorrador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/attach.png"))); // NOI18N
        btnGuardarBorrador.setText("Guardar borrador");
        btnGuardarBorrador.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnGuardarBorradorMouseEntered(evt);
            }
        });
        btnGuardarBorrador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarBorradorActionPerformed(evt);
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

        lblSintomas.setEditable(false);
        lblSintomas.setColumns(20);
        lblSintomas.setRows(5);
        jScrollPane2.setViewportView(lblSintomas);

        txtMedicamentos.setColumns(20);
        txtMedicamentos.setLineWrap(true);
        txtMedicamentos.setRows(5);
        txtMedicamentos.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtMedicamentos);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboPacientes, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jScrollPane2)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtTratamientos, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)
                                        .addComponent(txtDiagnostico, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel5))
                                    .addComponent(txtObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblFecha))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel6)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtIdMedicamento, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnAgregarMedicamento))
                                    .addComponent(jLabel7)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btnGuardarBorrador)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnRegistrarConsulta))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnAgregarArchivo))
                                    .addComponent(jScrollPane1))))
                        .addGap(0, 144, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMute)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(btnCerrarSesion)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMute)
                    .addComponent(btnCerrarSesion)
                    .addComponent(btnVolver))
                .addGap(44, 44, 44)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboPacientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTratamientos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnAgregarMedicamento)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtIdMedicamento, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtDiagnostico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblFecha))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregarArchivo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrarConsulta)
                    .addComponent(btnGuardarBorrador))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void btnGuardarBorradorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarBorradorActionPerformed
        /*
        Funciones f = new Funciones();

        try{
            int paciente = id_paciente.get(cboPacientes.getSelectedIndex());

            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);

            String consulta = "";

            consulta = "INSERT INTO borradorConsulta(paciente, tratamiento, diagnostico, observaciones)"
            + " VALUES (?, ?, ?, ?)";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1, paciente);
            sentencia.setString(2, txtTratamientos.getText());
            sentencia.setString(2, txtDiagnostico.getText());
            sentencia.setString(2, txtObservaciones.getText());
            ResultSet resultado = sentencia.executeQuery();

            sentencia.close();
            conexion.close();
        } catch(SQLException e) {
            f.mostrarMensajeLargo("Ocurrió un error al intentar conectar con la base de datos."
                + " Intente de nuevo. Si el error persiste, contacte con soporte para más información.");
        } //FIN TRY CATCH
        */
    }//GEN-LAST:event_btnGuardarBorradorActionPerformed

    private void btnGuardarBorradorMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuardarBorradorMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("guardar_borrador");
        }
    }//GEN-LAST:event_btnGuardarBorradorMouseEntered

    private void cboPacientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPacientesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboPacientesActionPerformed

    private void cboPacientesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cboPacientesMouseEntered
        if (!isMuted) {
            new Funciones().audio("paciente");
        }
    }//GEN-LAST:event_cboPacientesMouseEntered

    private void txtObservacionesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtObservacionesMouseEntered
        if (!isMuted) {
            new Funciones().audio("observaciones");
        }
    }//GEN-LAST:event_txtObservacionesMouseEntered

    private void txtObservacionesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtObservacionesFocusLost
        if(txtObservaciones.getText().equals("")){
            txtObservaciones.setText("Moco Verde");
            txtObservaciones.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_txtObservacionesFocusLost

    private void txtObservacionesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtObservacionesFocusGained
        if(txtObservaciones.getText().equals("Moco Verde")){
            txtObservaciones.setText("");
            txtObservaciones.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtObservacionesFocusGained

    private void btnRegistrarConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarConsultaActionPerformed
        Funciones f = new Funciones();
        String tratamientos, diagnostico, observaciones, medicamentos;

        tratamientos = txtTratamientos.getText();
        diagnostico = txtDiagnostico.getText();
        observaciones = txtObservaciones.getText();
        medicamentos = txtMedicamentos.getText();

        // Válida que todos los valores se hayan ingresado
        if( tratamientos.isEmpty() || diagnostico.isEmpty() || observaciones.isEmpty()
            || medicamentos.isEmpty() ) {
            f.mostrarMensaje("¡Faltan valores por ingresar!");
            return;
        }

        //Se registra la consulta en la BD
        registrarConsulta();
        borrarCampos();
        obtenerPacientes();
    }//GEN-LAST:event_btnRegistrarConsultaActionPerformed

    private void btnRegistrarConsultaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarConsultaMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("registrar_consulta");
        }
    }//GEN-LAST:event_btnRegistrarConsultaMouseEntered

    private void btnAgregarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarArchivoActionPerformed
        //El botón al ser clickeado, abre un menú para selecciónar un archivo
        seleccionarArchivo = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF", "png", "jpg", "pdf");
        seleccionarArchivo.setFileFilter(filter);

        //Se obtiene un valor de la operación, dependiendo de lo que suceda hace algo
        archivoSeleccionado = seleccionarArchivo.showOpenDialog(this);

        if (archivoSeleccionado == JFileChooser.APPROVE_OPTION) {
            java.io.File archivo = seleccionarArchivo.getSelectedFile();
            lblArchivo.setText("Adjuntó: " + archivo.getName());
        }
    }//GEN-LAST:event_btnAgregarArchivoActionPerformed

    private void btnAgregarArchivoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgregarArchivoMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("agregar_archivo");
        }
    }//GEN-LAST:event_btnAgregarArchivoMouseEntered

    private void btnAgregarMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarMedicamentoActionPerformed
        Funciones f = new Funciones();

        if( txtIdMedicamento.getText().isEmpty() ) {
            f.mostrarMensaje("¡Ingrese el ID del medicamento a agregar!");
            return;
        }

        try{
            Connection conexion = DriverManager.getConnection(Funciones.bd_url, Funciones.bd_usuario, Funciones.bd_password);
            String consulta = "SELECT nombre FROM medicamentos WHERE id = ?";
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setInt(1, Integer.parseInt(txtIdMedicamento.getText()));
            ResultSet resultado = sentencia.executeQuery();
            int c = 0;
            if(resultado.next()) {
                txtMedicamentos.setText( (txtMedicamentos.getText().isBlank() ? "" : txtMedicamentos.getText() + "," )  + resultado.getString("nombre"));
                c++;
            }
            if(c < 1) f.mostrarMensaje("¡Medicamento no encontrado!");
            sentencia.close();
            conexion.close();
        } catch(SQLException e) {
            f.mostrarMensaje("¡Algo falló! Intente de nuevo.");
        } //FIN TRY CATCH
    }//GEN-LAST:event_btnAgregarMedicamentoActionPerformed

    private void btnAgregarMedicamentoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgregarMedicamentoMouseEntered
        // TODO add your handling code here:
        if (!isMuted) {
            new Funciones().audio("agregar_medicamento");
        }
    }//GEN-LAST:event_btnAgregarMedicamentoMouseEntered

    private void txtIdMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdMedicamentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdMedicamentoActionPerformed

    private void txtIdMedicamentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIdMedicamentoFocusLost
        if(txtIdMedicamento.getText().equals("")){
            txtIdMedicamento.setText("1");
            txtIdMedicamento.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_txtIdMedicamentoFocusLost

    private void txtIdMedicamentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIdMedicamentoFocusGained
        if(txtIdMedicamento.getText().equals("1")){
            txtIdMedicamento.setText("");
            txtIdMedicamento.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtIdMedicamentoFocusGained

    private void txtDiagnosticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiagnosticoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDiagnosticoActionPerformed

    private void txtDiagnosticoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDiagnosticoMouseEntered
        if (!isMuted) {
            new Funciones().audio("diagnostico");
        }
    }//GEN-LAST:event_txtDiagnosticoMouseEntered

    private void txtDiagnosticoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiagnosticoFocusLost
        if(txtDiagnostico.getText().equals("")){
            txtDiagnostico.setText("Gripe");
            txtDiagnostico.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_txtDiagnosticoFocusLost

    private void txtDiagnosticoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiagnosticoFocusGained
        if(txtDiagnostico.getText().equals("Gripe")){
            txtDiagnostico.setText("");
            txtDiagnostico.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtDiagnosticoFocusGained

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed

        if (Funciones.confirmarVolver() == JOptionPane.YES_OPTION) {
            MenuDoctor menuD = new MenuDoctor();
            menuD.setUsuario(user);
            menuD.setVisible(true);
            dispose();
        }else {
            // No hacer nada, el usuario decidió no cerrar sesión
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
        if (!isMuted) {
            new Funciones().audio("cerrar_sesion");
        }
    }//GEN-LAST:event_btnCerrarSesionMouseEntered

    private void txtTratamientosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTratamientosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTratamientosActionPerformed

    private void txtTratamientosMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTratamientosMouseEntered
        if (!isMuted) {
            new Funciones().audio("tratamiento");
        }
    }//GEN-LAST:event_txtTratamientosMouseEntered

    private void txtTratamientosFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTratamientosFocusLost
        if(txtTratamientos.getText().equals("")){
            txtTratamientos.setText("Medicamentos y inyeccion");
            txtTratamientos.setForeground(new java.awt.Color(153,153,153));
        }
    }//GEN-LAST:event_txtTratamientosFocusLost

    private void txtTratamientosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTratamientosFocusGained
        if(txtTratamientos.getText().equals("Medicamentos y inyeccion")){
            txtTratamientos.setText("");
            txtTratamientos.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtTratamientosFocusGained

    
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
            java.util.logging.Logger.getLogger(Consulta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Consulta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Consulta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Consulta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Consulta().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarArchivo;
    private javax.swing.JButton btnAgregarMedicamento;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnGuardarBorrador;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnRegistrarConsulta;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cboPacientes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblArchivo;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JTextArea lblSintomas;
    private javax.swing.JTextField txtDiagnostico;
    private javax.swing.JTextField txtIdMedicamento;
    private javax.swing.JTextArea txtMedicamentos;
    private javax.swing.JTextField txtObservaciones;
    private javax.swing.JTextField txtTratamientos;
    // End of variables declaration//GEN-END:variables
}
