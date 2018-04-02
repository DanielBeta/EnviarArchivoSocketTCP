/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexiones.cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author danielbeta
 */
public class Insegura  extends Conexion{

    private Socket cliente;
    
    public Insegura(String ip, int puerto) {
        super(ip, puerto);
    }
    
    @Override
    public void crearConexion() throws IOException{
        
        this.cliente = new Socket(this.IP, this.PUERTO);
    }
    
    @Override
    public void establecerES(DataInputStream entrada, DataOutputStream salida) throws Exception {
        
        super.establecerES(new DataInputStream(this.cliente.getInputStream()), 
                                new DataOutputStream(this.cliente.getOutputStream()));
    }
    
    @Override
    public void cerrarConexiones() throws IOException {
        
        this.cliente.close();        
        super.cerrarConexiones();
    }
        
    @Override
    public Socket obtenerSocket() { return this.cliente; }
}
