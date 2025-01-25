/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycompany.clinica;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Funciones {
    // Cambiar de ser necesario
    public static final String bd_url = "jdbc:mysql://localhost:3306/consultorio";
    public static final String bd_usuario = "root";
    public static final String bd_password = "";
    
    private Clip clip;
    
    public Funciones() { }
    
    //Muestra un mensaje personalizado que desaparece tras un segundo
    void mostrarMensaje(String mensaje) {
        JOptionPane optionPane = new JOptionPane(mensaje, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        final JDialog dialog = optionPane.createDialog(null, "Información");
        dialog.setModal(false);
        
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) { }
            
            SwingUtilities.invokeLater(() -> {
                dialog.setVisible(false);
                dialog.dispose();
            });
        });
        
        thread.start();
        dialog.setVisible(true);	
    }        
    
    void mostrarMensajeLargo(String mensaje){
        JOptionPane.showOptionDialog(
                null,
                mensaje,
                "Información",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Aceptar"},
                "Aceptar");
    }
             
    //Completa la fecha en caso se ser necesario
    String completarFecha(String fecha) {            
        String partes[] = fecha.split("/");
        if(partes.length == 3 ) {
            if( Integer.parseInt(partes[0]) < 10 ) partes[0] = "0" + Integer.valueOf(partes[0]);
            if( Integer.parseInt(partes[1]) < 10 ) partes[1] = "0" + Integer.valueOf(partes[1]);
            fecha = partes[0] + "/" + partes[1] + "/" + partes[2];
        }        
        else return "0/0/0";
        
        return fecha;
    }
    
    //Comprueba que la fecha está en el formato que se especifica DD/MM/YYYY
    boolean verificarFecha(String fecha) {        
        //Separa la fecha ingresada en TRES cadenas (DÍA, MES, AÑO), si falla es porque es una fecha incorrecta
        String fechas[] = fecha.split("/");                    
        String nFecha; //Nuevo valor de fecha para cuando el usuario solo ingrese UN DIGITO del día o mes
        
        //Valida que la fecha esté en los parametros correctos
        if(fechas.length != 3) return false;
        else {
            //Todos los días de los meses en un año
            int diasMes[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            int dia = 0, mes = 0, anio = 0;            
            
            nFecha = fechas[0] + "/" + fechas[1] + "/" + fechas[2];                        
            
            //Convierte los valores ingresados para comprobar la fecha como tal
            try {
                dia = Integer.parseInt(fechas[0]);
                mes = Integer.parseInt(fechas[1]);
                anio = Integer.parseInt(fechas[2]);
            } catch(NumberFormatException e) {
                return false;
            }
            //El año debe ser el actual
            if( anio < 2023) return false;            
            
            //Verifica si el día ingresado está entre el rango de días de un mes (Válida bisiestos)
            if(anio % 4 == 0 && (anio % 100 != 0 || anio % 400 == 0)) diasMes[1] = 29;
            if(mes < 1 || mes > 12) return false;           

            //Verifica que el més esté entre los rangos especificados
            if(dia < 1 || dia > diasMes[mes - 1]) return false;
        } //FIN DEL IF ELSE
        
        //Se obtiene la fecha actual y se compara con la fecha ingresada por el usuario
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaActual = LocalDate.now();        
        LocalDate fechaIngresada = LocalDate.parse(nFecha, formato);     
        
        //Válida si es el día es correcto, es decir si el día es el mismo o a futuro
        return !fechaIngresada.isBefore(fechaActual);
    }
    
    // Ventana emergente personalizada para confirmar el cierre forzado del programa
    public static int confirmarCierre () {
        Object[] opciones = {"Cerrar", "Cancelar"};
        
        return JOptionPane.showOptionDialog(null, "¿Estás seguro de salir?", "Confirmar cierre", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, opciones, opciones[0]);
    }
    
    // Ventana emergente personalizada para confirmar si se desea volver al apartado anterior
    public static int confirmarVolver () {
        Object[] opciones = {"Volver", "Cancelar"};

        return JOptionPane.showOptionDialog(null, "¿Estás seguro de volver? Se perderán los datos", "Confirmar Volver", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, opciones, opciones[0]);
    }
    
    // Ventana emergente personalizada para advertir que debe completar información
    public static int mostrarMensajeAcompletarInformacion() {
        Object[] opciones = {"Aceptar"};
        
        return JOptionPane.showOptionDialog(null, "Antes de generar una cita primero debe acompletar su información personal.", "Información requerida", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);
    }
    
    // Ventana emergente para advertir sobre campos inválidos en un formulario
    private static int mostrarInvalidacion(String camposInvalidos) {
        Object[] opciones = {"Aceptar"};
        
        return JOptionPane.showOptionDialog(null, "Los siguientes campos son inválidos: " + camposInvalidos, "¡Inválido!", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, opciones, opciones[0]);
    }
    
    // Identificar si hay un campo (o varios) input de entrada erronea
    public static boolean sonCamposValidos(ArrayList<String> nombres, ArrayList<JTextField> entradas) {
        // Guardar elementos invalidos
        ArrayList<String> nombresInvalidos = new ArrayList<>();
        JTextField entradaInvalida = null;

        for (int i = 0; i < entradas.size(); i++)
            if (entradas.get(i).getText().trim().isEmpty()){
                // Guardamos elemento(s) inválido
                nombresInvalidos.add(nombres.get(i).replace("*", "").replace(":", ""));
                entradaInvalida = (entradaInvalida == null ? entradas.get(i) : entradaInvalida);
            }

        // Todo fue valido
        if (nombresInvalidos.isEmpty())
            return true;
        
        // Mover cursor al primer elemento
        entradaInvalida.requestFocus();
        
        // Concatenamos aquellos campos que sean invalidos y fue inválido un campo
        mostrarInvalidacion(concatenarNombresCamposInvalidos(nombresInvalidos));
        return false;
    }
    
    // Colocar letra "y" en la concatenacion del mensaje
    private static String concatenarNombresCamposInvalidos(ArrayList<String> nombres) {
        // En caso de que solo es un campo invalido
        if (nombres.size() == 1)
            return nombres.get(0);
        
        // Guardar el ultimo nombre para agregar "y" en lugar de ","
        String ultimoCampo = nombres.get(nombres.size()-1);
        
        // Eliminar ultimo elemento
        nombres.remove(nombres.size()-1);
        
        // Retornar concatenacion (e.g.: "Nombre, Fecha y Usuario"
        return String.join(", ", nombres) + " y " + ultimoCampo;
    }
    
    public void audio(String nombreaudio){
        try{
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            File archivoAudio = new File("src/main/java/mycompany/clinica/audios/"+nombreaudio+".wav");
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(archivoAudio));
            clip.start();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al reproducir el audio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
