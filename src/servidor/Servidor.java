package servidor;

import controladores.Protocolo;
import interfaces.Consola;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author danielbeta
 */
public class Servidor {
    
    private Protocolo protocolo;
    private Consola consola;
    private final Red RED;
    private final int PUERTO;  // you may change this
    
    private DataInputStream entrada;
    private DataOutputStream salida;
    //private ServerSocket servidor = null;
    private SSLServerSocket servidor = null;
    private SSLSocket cliente = null;
    
    
    
    private String mensaje;
    private String peticion;
    private String respuesta;

    public Servidor() throws IOException, Exception{
        
        this.RED = new Red();
        this.PUERTO = this.RED.obtenerPuerto();
        
        this.protocolo = new Protocolo();
    }
    
    public void asignarConsola(Consola consola){
        
        this.consola = consola;
    }
    
    public void subir() throws IOException, Exception{
  
        try {

          //servidor = new ServerSocket(PUERTO);
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) 
            SSLServerSocketFactory.getDefault();
            this.servidor = (SSLServerSocket) 
            sslServerSocketFactory.createServerSocket(PUERTO);

          while (true) {

            this.mensaje="[SERVIDOR] Esperando cliente...";
            this.consola.mostrarMensajesServidor(this.mensaje);
            this.peticion="";
            
            try {
                
                cliente = (SSLSocket)servidor.accept();
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

                    byte[] bytes = new byte[10000];
                    
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
                    
                this.respuesta = this.protocolo.gestionarPeticion(this.peticion);
                    
                /* Analiza la peticion proveniente del cliente */
                this.evaluarRespuesta(this.respuesta);   
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
        
        if(respuesta.startsWith(this.protocolo.obtenerPathRepositorio())){
         
            File myFile = new File (respuesta.replaceAll("//", "/").trim());
            byte [] mybytearray  = new byte [(int)myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray,0,mybytearray.length);

            this.respuesta = "Enviando archivo: " + respuesta;
            this.mensaje="[SERVIDOR] Responder al cliente: " + this.respuesta;
            this.consola.mostrarMensajesServidor(this.mensaje);

            salida.write(mybytearray,0,mybytearray.length);
            salida.flush(); 
        } else {
        
            if(respuesta.equals(this.protocolo.archivoNoEncontradoComando())){
                if(this.peticion.startsWith("C")){
                    this.buscarArchivoLAN("S" + this.peticion.substring(1));
                }
            } else {

                if(respuesta.equals(this.protocolo.descargarArchivoComando())){
                    this.buscarArchivoLAN("S" + this.peticion.substring(1));
                } 
            }
            
            this.mensaje="[SERVIDOR] Responder al cliente: " + this.respuesta;
            this.consola.mostrarMensajesServidor(this.mensaje);
            salida.write((this.respuesta).getBytes());
            salida.flush();            
        }
    }
    
    public String obtenerIP(){
        
        return this.RED.obtenerIp();
    }
    
    private void buscarArchivoLAN(String peticion) throws Exception{
        
        
        
        String respuesta = "";
        ArrayList<String> ips = this.RED.obtenerIPSEnRed();
        int puerto = this.RED.obtenerPuerto();
        
        for(String ip : ips){
            
            this.mensaje="[SERVIDOR] Buscar archivo donde mi amiguito " + ip;
            this.consola.mostrarMensajesServidor(this.mensaje);
            
           // Socket s = new Socket(ip, puerto);
            SSLSocket s;
            
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) 
            SSLSocketFactory.getDefault();
            s = (SSLSocket) 
            sslSocketFactory.createSocket(ip, puerto);
                   
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            out.write(peticion.getBytes());
            out.flush();
            
            if(peticion.contains(this.protocolo.descargarArchivoComando())){
                this.descargarArchivoLAN(s, peticion);
                this.respuesta = "Archivo descargado.";
            }
            else{
            
                byte[] bytes = new byte[100];

                in.read(bytes);       

                for(byte b : bytes)
                    respuesta += (char)b;
            }
            
            s.close();
            in.close();
            out.close();
            
            this.respuesta = respuesta;
            
            if(!this.respuesta.equals(this.protocolo.archivoNoEncontradoComando()))
                break;
        }
    }
    
    private void descargarArchivoLAN(Socket s, String peticion) throws Exception{
  
        int bytesRead, current=0;

        byte [] mybytearray  = new byte [100];
        InputStream is = s.getInputStream();
        String ruta = this.protocolo.obtenerPathRepositorio() + peticion.split(this.protocolo.comandoSeparacion())[1];
        FileOutputStream fos = new FileOutputStream(ruta.trim());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bytesRead = is.read(mybytearray,0,mybytearray.length);
        current = bytesRead;

        do {
           bytesRead =
              is.read(mybytearray, current, (mybytearray.length-current));
           if(bytesRead >= 0) current += bytesRead;
        } while(bytesRead > -1);
        
        bos.write(mybytearray, 0 , current);
        bos.flush();
        
        this.protocolo.gestionarPeticion("S"+this.protocolo.ACTUALIZAR_REPOSITORIO);
    }
    
    public static void main(String[] args) {
        
        System.setProperty("javax.net.ssl.keyStore","myKeystone.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","d1053837737d.");
        
        System.setProperty("javax.net.ssl.trustStore", "myKeystone.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "d1053837737d.");
        
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
