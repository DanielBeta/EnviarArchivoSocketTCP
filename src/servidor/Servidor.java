package servidor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author danielbeta
 */
public class Servidor {
    
    public final int PUERTO;  // you may change this
    
    private FileInputStream fis;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private ServerSocket servidor = null;
    private Socket cliente = null;
    
    
    
    private String mensaje;
    private String peticion;
    private String respuesta;

    public Servidor() throws IOException{
        
        this.PUERTO = 2317;
        this.subir();
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
    
    
    public void subir() throws IOException{
        
        try {

          servidor = new ServerSocket(PUERTO);

          while (true) {

            this.mensaje="[SERVIDOR] Esperando cliente...";
            System.out.println(this.mensaje);
            this.peticion="";
            
            try {
                
                cliente = servidor.accept();
                this.mensaje="[SERVIDOR] Conexion establecida con el cliente: " + cliente; 
                System.out.println(this.mensaje);
                
                entrada = new DataInputStream(cliente.getInputStream());
                salida = new DataOutputStream(cliente.getOutputStream());
                
                /**************************************************************
                 * 
                 * ESTE BLOQUE RECIBE Y DESCIFRA LA PETICION DEL CLIENTE
                 * 
                 **************************************************************/
                
                    this.mensaje="[SERVIDOR] Esperando peticion del cliente...";
                    System.out.println(this.mensaje);

                    /*  Crea el arreglo de bytes con el tamaño de bytes de la peticion del cliente. 
                        La peticion debe preceder del caracter virgulilla desde el lado del cliente.*/
                    
                    byte[] bytes = new byte[entrada.read()];
                    
                    /* Almacena los bytes de la peticion del cliente */
                    entrada.read(bytes);               

                    /* Construye la peticion del cliente*/
                    for(byte b : bytes)
                        this.peticion += (char)b;
                    
                    this.mensaje="[SERVIDOR] Esta es la peticion del cliente: \"" + this.peticion  + "\".";
                    System.out.println(this.mensaje);
                
                /**************************************************************
                 * 
                 * FIN DEL BLOQUE
                 * 
                 **************************************************************/
                   
                 /*  */   
                //byte[] respuestaBytes = new Protocolo().gestionarPeticion();
                    
                this.respuesta="Usted ha solicitado: \"" + this.peticion + "\".";
                this.mensaje="[SERVIDOR] Responder al cliente: " + this.respuesta;
                System.out.println(this.mensaje);
                salida.write((" " + this.respuesta).getBytes());
                
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
        
        try {
            Servidor s = new Servidor();
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
