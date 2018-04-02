package cliente;

import conexiones.cliente.Conexion;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.Socket;
import servidor.Red;

/**
 *
 * @author danielbeta
 */
public class Cliente {

    private final boolean esSegura;
    private final Conexion conexion;
    private final Red RED;
    private final String IP;
    private final int PUERTO;
    
    public Cliente(boolean esSegura) throws Exception{
        
        this.RED = new Red();
        this.IP = this.RED.obtenerIp();
        this.PUERTO = this.RED.obtenerPuerto();
        
        this.esSegura = esSegura;
        
        if(this.esSegura){ this.conexion = new conexiones.cliente.Segura(this.IP, this.PUERTO); } 
        else { this.conexion = new conexiones.cliente.Insegura(this.IP, this.PUERTO); }   
    }
    
    public String enviarMensajeServidor(String mensaje) throws Exception{
         
        String peticion = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        
        try {
            
            this.conexion.crearConexion();
            System.out.println("CLIENT: connecting to server");
            
            this.conexion.establecerES(in, out);
            System.out.println("CLIENT: extracting I/O streams");

            in = this.conexion.obtenerEntrada();
            out = this.conexion.obtenerSalida();


            System.out.println("CLIENT: sending data to server");

            out.write(("C" + (mensaje) + "|").getBytes());

            out.flush();

            System.out.println("CLIENT: receiving data from server");

            byte[] bytes = new byte[100];

            /* Almacena los bytes de la peticion del cliente */
            in.read(bytes);               

           peticion = "";

            /* Construye la peticion del cliente*/
            for(byte b : bytes)
                peticion += (char)b;


            System.out.println("RESPONSE: " + peticion);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(peticion);
        return peticion;
    }

    public String obtenerIp() { return this.IP; }
}
