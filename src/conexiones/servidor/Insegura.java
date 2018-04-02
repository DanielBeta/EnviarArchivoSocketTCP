/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexiones.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author danielbeta
 */
public class Insegura  extends Conexion{

    private ServerSocket servidor;
    private Socket cliente;
    
    public Insegura(int puerto) {
        super(puerto);
    }
    
    @Override
    public void crearConexion() throws IOException{
        
        servidor = new ServerSocket(this.PUERTO);
    }

    @Override
    public void esperarCliente() throws Exception {
        
        this.cliente = this.servidor.accept();
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
    
    public ServerSocket obtenerServidor() { return this.servidor; }
    public Socket obtenerCliente() { return this.cliente; } 
}
