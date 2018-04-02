package servidor;

import cliente.Cliente;
import conexiones.servidor.Conexion;
import controladores.Protocolo;
import interfaces.InterfazCliente;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author danielbeta
 */
public class Servidor {
    
    private final Protocolo protocolo;
    private final Red RED;
    private final int PUERTO;  // you may change this
    
    private String mensaje;
    private String peticion;
    private String respuesta;
    private final Conexion conexion;
    private final boolean esSegura;

    public Servidor(boolean esSegura) throws IOException, Exception{
        
        this.RED = new Red(esSegura);
        
        System.out.println("Direccion IP: " + this.RED.obtenerIp());
        System.out.println("Mascara de Subred: " + this.RED.obtenerMascara());
        
        this.PUERTO = this.RED.obtenerPuerto();
        this.protocolo = new Protocolo();
        this.RED.escanearIPSConectadasARed(); //revisar el constructor de la clase Red
        
        this.esSegura = esSegura;
        
        if(this.esSegura){ this.conexion = new conexiones.servidor.Segura(this.PUERTO); } 
        else { this.conexion = new conexiones.servidor.Insegura(this.PUERTO); }
        this.conexion.crearConexion();
        
    }
    
    public void subir() throws IOException, Exception{
  
        try {

          while (true) {

            this.mensaje="[SERVIDOR] Esperando cliente...";
            System.out.println(this.mensaje);
            this.peticion="";
                    
            this.conexion.esperarCliente();
            this.conexion.establecerES(null, null);
            this.mensaje="[SERVIDOR] Conexion establecida con el cliente";
            System.out.println(this.mensaje);
            DataInputStream entrada = this.conexion.obtenerEntrada();
            DataOutputStream salida = this.conexion.obtenerSalida();
                
                
                
                /**************************************************************
                 * 
                 * ESTE BLOQUE RECIBE Y DESCIFRA LA PETICION DEL CLIENTE
                 * 
                 **************************************************************/
                
                    this.mensaje="[SERVIDOR] Esperando peticion del cliente...";
                    System.out.println(this.mensaje);

                    byte[] bytes = new byte[100];
                    
                    /* Almacena los bytes de la peticion del cliente */
                    entrada.read(bytes);               

                    /* Construye la peticion del cliente*/
                    for(byte b : bytes)
                        this.peticion += (char)b;
                    
                    this.mensaje="[SERVIDOR] Esta es la peticion del cliente: " + this.peticion;
                    System.out.println(this.mensaje);
                
                /**************************************************************
                 * 
                 * FIN DEL BLOQUE
                 * 
                 **************************************************************/ 
                    
                this.respuesta = this.protocolo.gestionarPeticion(this.peticion);
                    
                /* Analiza la peticion proveniente del cliente */
                this.evaluarRespuesta(this.respuesta); 
            }
        } finally { this.conexion.cerrarConexiones(); }
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
            System.out.println(this.mensaje);

            this.conexion.obtenerSalida().write(mybytearray,0,mybytearray.length);
            this.conexion.obtenerSalida().flush(); 
        } else {
        
            if(respuesta.equals(this.protocolo.archivoNoEncontradoComando())){
                if(this.peticion.startsWith("C")){
                    this.buscarArchivoLAN("S" + this.peticion.substring(1));
                }
            } else {

                if(respuesta.equals(this.protocolo.descargarArchivoComando())){
                    
                    this.buscarArchivoLAN("S" + this.peticion.substring(1));
                } else{
                    
                    if(respuesta.equals(this.protocolo.OBTENER_IPS)){
                        
                        String ips="";
                        for(String ip : this.RED.obtenerIPSEnRed()){

                            ips = ip + "|";
                        }

                        this.respuesta = ips;
                    } else {
                        
                        if(respuesta.equals(this.protocolo.PUBLICAR_ARCHIVO)){
                         
                            String[] peticionPartes = this.peticion.split(this.protocolo.comandoSeparacion());
                            this.publicarArchivo(peticionPartes[1], peticionPartes[2]);
                        }                        
                    }
                }
            }
            
            this.mensaje="[SERVIDOR] Responder al cliente: " + this.respuesta;
            System.out.println(this.mensaje);
            this.conexion.obtenerSalida().write((this.respuesta).getBytes());
            this.conexion.obtenerSalida().flush();            
        }
    }
    
    public String obtenerIP(){
        
        return this.RED.obtenerIp();
    }
    
    private void buscarArchivoLAN(String peticion) throws Exception{
        
        conexiones.cliente.Conexion conexionCliente;
        String respuesta = "";
        ArrayList<String> ips = this.RED.obtenerIPSEnRed();
        DataInputStream in;
        DataOutputStream out;
        
        for(String ip : ips){
            
            if(this.esSegura){ conexionCliente = new conexiones.cliente.Segura(ip, this.PUERTO); } 
            else { conexionCliente = new conexiones.cliente.Insegura(ip, this.PUERTO); }
            conexionCliente.crearConexion();
            conexionCliente.establecerES(null, null);
            
            this.mensaje="[SERVIDOR] Buscar archivo donde mi amiguito " + ip;
            System.out.println(this.mensaje);
                
            in = conexionCliente.obtenerEntrada();
            out = conexionCliente.obtenerSalida();           

            out.write(peticion.getBytes());
            out.flush();
            
            if(peticion.contains(this.protocolo.descargarArchivoComando())){

                this.descargarArchivoLAN(peticion, conexionCliente);
                respuesta = this.respuesta;
            }
            else{
            
                byte[] bytes = new byte[100];

                in.read(bytes);       

                for(byte b : bytes)
                    respuesta += (char)b;
            }
            
            conexionCliente.cerrarConexiones();
            in.close();
            out.close();
            
            this.respuesta = respuesta;
            
            if(!this.respuesta.equals(this.protocolo.archivoNoEncontradoComando()))
                break;
        }
    }
    
    private void descargarArchivoLAN(String peticion, conexiones.cliente.Conexion conexion) throws Exception{
  
        int bytesRead, current;

        byte [] mybytearray  = new byte [100];
        InputStream is;

        if(esSegura){ is = ((conexiones.cliente.Segura)conexion).obtenerSocket().getInputStream();}
        else { is = (((conexiones.cliente.Insegura)conexion).obtenerSocket()).getInputStream(); }
        
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
    
    
    private void publicarArchivo(String ip, String archivo){
        
    }
    
    public static void main(String[] args) {

        String firma = "myKeystone.jks";
        String contrasena = "d1053837737d.";
        System.setProperty("javax.net.ssl.keyStore", firma);
        System.setProperty("javax.net.ssl.keyStorePassword",contrasena);
        System.setProperty("javax.net.ssl.trustStore", firma);
        System.setProperty("javax.net.ssl.trustStorePassword", contrasena);
        
        int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea conexion segura con cifrado de mensajes?", "Atencion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        boolean esSeguro = false;
        
        if(respuesta == 0)
            esSeguro = true;
        
        InterfazCliente ic;
        Servidor s;
        Cliente cliente;
        
        try {
            
            cliente = new Cliente(esSeguro);
            ic = new InterfazCliente(cliente);
            ic.asignarDatosDeProductosATabla(new ArrayList<>());
            ic.setLocationRelativeTo(null);
            ic.setVisible(true);
            
            JOptionPane.showMessageDialog(null, "Por favor espere mientras se escanea la red.");
            s = new Servidor(esSeguro);
            s.subir();
            } catch (Exception ex) {
                
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } 
    } 
}
