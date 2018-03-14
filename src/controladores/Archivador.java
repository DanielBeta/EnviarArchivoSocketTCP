package controladores;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidor.Red;

/**
 * Manejador de archivos.
 * @author danielbeta
 */
public class Archivador {
    
    /**
     * 
     */
    private final String rutaRepositorio;
    private final String nombreArchivo;

    /**
     * Constructor que carga la ruta, por defecto, de los archivos de datos y configuracion.
     */
    public Archivador() throws Exception{
        
        this.rutaRepositorio = "src//repositorio//";
        this.nombreArchivo = "archivos.txt";
    }
    
    
    /**
     * Carga los datos de acceso al servidor (IP, Puerto) desde un archivo plano.
     * 
     * @return Vector cuyas posiciones se distribuyen en: 0 -> IP, 1 -> Puerto
     * @throws FileNotFoundException Cuando el archivo no existe.
     * @throws IOException Cuando hay problemas con los datos.
     */
    public ArrayList<String[]> cargarArchivos() throws FileNotFoundException, IOException, Exception{

        String rutaArchivo = this.rutaRepositorio + this.nombreArchivo;
        ArrayList<String[]> archivos = new ArrayList<>();
        BufferedReader lector;
        File archivo;
        
        archivo = new File(rutaArchivo);                       //Carga la ruta del archivo

        try{
            
            lector = new BufferedReader(new FileReader(archivo));           //Carga el archivo
        } catch(FileNotFoundException ex){
            
            throw new FileNotFoundException("Archivo \"" + rutaArchivo + "\" no existe.");
        }
        
        String linea = lector.readLine();                                   //Lee la primera linea del archivo.
        
        if(linea == null || linea.isEmpty()){
         
            ArrayList<String[]> nombresArchivos = this.obtenerNombresArchivos();
            
            if(!nombresArchivos.isEmpty()){
                
                lector.close();
                this.guardarDatosArchivos(nombresArchivos);
                archivos = this.cargarArchivos();
            }
        }
        
        while(linea != null){
            
            if(!linea.contains(this.nombreArchivo))
                 archivos.add(linea.split("\\|"));
            
            linea = lector.readLine(); 
        }
        
        lector.close();
        return archivos;
    }
    
    /**
     * Guarda en un archivo plano, los datos del diccionario.
     * @param datosDiccionario Tema, terminos y definiciones. Primer item de la lista corresponde al tema, resto de items corresponden a par de palabra y definicion.
     * @throws java.io.IOException 
     */
    public void guardarDatosArchivos(ArrayList<String[]> datosDiccionario) throws IOException{
        
        FileWriter archivo;
        String rutaArchivo = null;
        
        try {
            
            rutaArchivo = this.rutaRepositorio + this.nombreArchivo;
            archivo = new FileWriter(rutaArchivo);
            
            try (PrintWriter pw = new PrintWriter(archivo)) {

                datosDiccionario.stream().forEach((String[] s) -> {

                    pw.write(s[0] + "|" + s[1] + "|" + s[0] + "\n");
                });
            }
        } catch (IOException ex) {
            
            throw new IOException("Ruta \"" + rutaArchivo + "\" no existe.");
        }
    }
    
    /**
     * Obtiene los nombres de los archivos que estan dentro del repositorio.
     * @return Nombres de archivos
     * @throws Exception En caso de que no exista la ruta.
     */
    private ArrayList<String[]> obtenerNombresArchivos() throws Exception{
        
        ArrayList<String[]> nombresArchivos = new ArrayList<>();
        File carpeta = new File(this.rutaRepositorio);
        String[] partesFichero = new String[3];
        
        if(!carpeta.exists())
            throw new Exception("Ruta \"" + this.rutaRepositorio + "\" no existe.");
        
        File[] ficheros = carpeta.listFiles();
        
        for(File fichero : ficheros){
            
            partesFichero[0] = fichero.getName();
            partesFichero[1] = new Red().obtenerIp();
            partesFichero[2] = "";
            
            nombresArchivos.add(partesFichero);
            partesFichero = new String[3];
        }
        
        return nombresArchivos;
    }

    public String getRutaRepositorio() {
        
        return this.rutaRepositorio;
    }
    
    public void vaciarArchivo() throws FileNotFoundException, IOException{ 
        
        FileWriter archivo;
        String rutaArchivo = null; 
    
        rutaArchivo = this.rutaRepositorio + this.nombreArchivo;
        archivo = new FileWriter(rutaArchivo);
        PrintWriter pw = new PrintWriter(archivo);
        pw.write("");
    }

    public void removerArchivo(String nombreArchivo) {
        
        File archivo = new File((this.rutaRepositorio+nombreArchivo).trim());
        
        if(archivo.delete());
    }

    void cargarArchivos(String ruta) throws FileNotFoundException {
        
        ruta = ((this.getRutaRepositorio()+ruta.split("/")[ruta.split("/").length-1]).replace("//", "/")).trim();
        new FileOutputStream(ruta.trim());
    }
    
}
