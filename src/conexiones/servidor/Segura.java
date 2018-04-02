package conexiones.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author danielbeta
 */
public class Segura extends Conexion{

    private SSLServerSocket servidor;
    private SSLSocket cliente;
    
    public Segura(int puerto) {
        
        super(puerto);
    }
    
    @Override
    public void crearConexion() throws Exception {

        SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        this.servidor = (SSLServerSocket) sslServerSocketFactory.createServerSocket(this.PUERTO);
    }

    @Override
    public void esperarCliente() throws Exception {
       
        this.cliente = (SSLSocket)this.servidor.accept();
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

    public SSLServerSocket obtenerServidor() { return this.servidor; }
    public SSLSocket obtenerCliente() { return this.cliente; } 
}
