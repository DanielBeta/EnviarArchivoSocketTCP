package conexiones.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author danielbeta
 */
public abstract class Conexion {
    
    protected final int PUERTO;
    protected DataInputStream entrada;
    protected DataOutputStream salida;
    
    public Conexion(int puerto){
        
        this.PUERTO = puerto;
    }
    
    /**
     *
     * @throws java.lang.Exception
     */
    public void crearConexion() throws Exception {}
    
    public void esperarCliente() throws Exception {}
    
    public void establecerES(DataInputStream entrada, DataOutputStream salida) throws Exception {
    
        this.entrada = entrada;
        this.salida = salida;
    }
    
    public  void cerrarConexiones() throws IOException{
        
        this.entrada.close();
        this.salida.close();
    }
      
    public DataInputStream obtenerEntrada() { return this.entrada; }
    public DataOutputStream obtenerSalida() { return this.salida; }
}
