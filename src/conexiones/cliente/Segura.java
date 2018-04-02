package conexiones.cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author danielbeta
 */
public class Segura extends Conexion{

    private SSLSocket cliente;
    
    public Segura(String ip, int puerto) {
        
        super(ip, puerto);
    }
    
    @Override
    public void crearConexion() throws Exception {

        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        this.cliente = (SSLSocket) sslSocketFactory.createSocket(this.IP, this.PUERTO);
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
    public SSLSocket obtenerSocket() { return this.cliente; }    
}
