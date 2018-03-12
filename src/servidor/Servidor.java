package servidor;

import controladores.Protocolo;
import interfaces.Consola;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author danielbeta
 */
public class Servidor {
    
    private Protocolo protocolo;
    private Consola consola;
    private final Red RED;
    private final int PUERTO;  // you may change this
    
    private FileInputStream fis;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private ServerSocket servidor = null;
    private Socket cliente = null;
    
    
    
    private String mensaje;
    private String peticion;
    private String respuesta;

    public Servidor() throws IOException, Exception{
        
        this.RED = new Red();
        this.PUERTO = this.RED.obtenerPuerto();
        
        this.protocolo = new Protocolo();
    }
    
//    public void subir() throws IOException{
//        
//        ServerSocket servidor = new ServerSocket(PUERTO);
//        
//        while(true){
//            
//           
//            System.out.println("Esperando cliente...");
//            
//            Socket cliente = servidor.accept();
//            System.out.println("Cliente Aceptado.");
//
//            DataInputStream in = new DataInputStream(cliente.getInputStream());
//            DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
//
//            byte[] bytes = new byte[cliente.getInputStream().read()];
//            System.out.println(bytes.length);
//
//            /* Almacena los bytes de la peticion del cliente */
//            in.read(bytes);               
//
//            /* Construye la peticion del cliente*/
//            for(byte b : bytes)
//                this.peticion += (char)b;
//
//            System.out.println(this.peticion);
//
//            out.write(bytes);
//        }
//    }
    
    public void asignarConsola(Consola consola){
        
        this.consola = consola;
    }
    
    public void subir() throws IOException, Exception{
  
        try {

          servidor = new ServerSocket(PUERTO);

          while (true) {

            this.mensaje="[SERVIDOR] Esperando cliente...";
            this.consola.mostrarMensajesServidor(this.mensaje);
            this.peticion="";
            
            try {
                
                cliente = servidor.accept();
                this.mensaje="[SERVIDOR] Conexion establecida con el cliente: " + cliente; 
                this.consola.mostrarMensajesServidor(this.mensaje);
                
                entrada = new DataInputStream(cliente.getInputStream());
                salida = new DataOutputStream(cliente.getOutputStream());
                
                /**************************************************************
                 * 
                 * ESTE BLOQUE RECIBE Y DESCIFRA LA PETICION DEL CLIENTE
                 * 
                 **************************************************************/
                
                    this.mensaje="[SERVIDOR] Esperando peticion del cliente...";
                    this.consola.mostrarMensajesServidor(this.mensaje);

                    /*  Crea el arreglo de bytes con el tamaño de bytes de la peticion del cliente. 
                        La peticion debe preceder del caracter virgulilla desde el lado del cliente.*/
                    byte[] bytes = new byte[100];
                    
                    /* Almacena los bytes de la peticion del cliente */
                    entrada.read(bytes);               

                    /* Construye la peticion del cliente*/
                    for(byte b : bytes)
                        this.peticion += (char)b;
                    
                    this.mensaje="[SERVIDOR] Esta es la peticion del cliente: " + this.peticion;
                    this.consola.mostrarMensajesServidor(this.mensaje);
                
                /**************************************************************
                 * 
                 * FIN DEL BLOQUE
                 * 
                 **************************************************************/ 
                    
                this.respuesta="Usted ha solicitado: " + this.peticion + "";
                
                /* Analiza la peticion proveniente del cliente */
                this.evaluarRespuesta(this.protocolo.gestionarPeticion(this.peticion.substring(1)));   
            } finally {
                
              if (entrada != null) entrada.close();
              if (salida != null) salida.close();
              if (cliente!=null) cliente.close();
            }
          }
        } finally {
            
          if (servidor != null) servidor.close();
        }
    }
    
    public void evaluarRespuesta(String respuesta) throws Exception{
        
        if(respuesta.equals(this.protocolo.archivoNoEncontradoComando()))
            if(this.peticion.startsWith("C"))
                this.buscarArchivoLAN("S" + this.peticion.substring(1));
        
        this.respuesta = respuesta;
        
        this.mensaje="[SERVIDOR] Responder al cliente: " + this.respuesta;
        this.consola.mostrarMensajesServidor(this.mensaje);
        salida.write((this.respuesta).getBytes());
        salida.flush();
    }
    
    public String obtenerIP(){
        
        return this.RED.obtenerIp();
    }
    
    private void buscarArchivoLAN(String peticion) throws Exception{
        
        String respuesta = "";
        ArrayList<String> ips = this.RED.obtenerIPSEnRed();
        int puerto = this.RED.obtenerPuerto();
        
        for(String ip : ips){
            
            Socket s = new Socket(ip, puerto);
            
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            out.write(respuesta.getBytes());

            out.flush();
            
            byte[] bytes = new byte[100];

            in.read(bytes);       

            for(byte b : bytes)
                respuesta += (char)b;

            this.respuesta = respuesta;
            
            if(!this.respuesta.equals(this.protocolo.archivoNoEncontradoComando()))
                break;
        }
    }
    
    /*
    public void transferirArchivo(){
        
        
                
                File myFile = new File (FILE_TO_SEND);
                byte [] mybytearray  = new byte [(int)myFile.length()];
                fis = new FileInputStream(myFile);
                entrada = new BufferedInputStream(fis);
                entrada.read(mybytearray,0,mybytearray.length);
                salida = cliente.getOutputStream();
                System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
                salida.write(mybytearray,0,mybytearray.length);
                salida.flush();
    }*/
    
    
    public static void main(String[] args) {
        
        Servidor s;
        Consola c;
        
        try {
            
            s = new Servidor();
            
            c = new Consola(s);
            c.setLocationRelativeTo(null);
            c.setVisible(true);
            
            s.asignarConsola(c);
            s.subir();
            } catch (Exception ex) {
                
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
