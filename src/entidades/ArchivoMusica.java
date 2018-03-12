package entidades;

/**
 *
 * @author danielbeta
 */
public class ArchivoMusica {
    
    private final String nombre;
    private final String nombreArchivo;
    private String estado;
    
    public ArchivoMusica(String nombre, String nombreArchivo, String estado){
        
        this.nombre = nombre;
        this.nombreArchivo = nombreArchivo;
        this.estado = estado;
    }
    
    public void setEstado(String estado) throws Exception{
        
        if(!estado.equalsIgnoreCase("COMPARTIDO") && 
                !estado.equalsIgnoreCase("NO COMPARTIDO"))
            throw new Exception("\"" + estado + "\" no es un estado valido.");
        
        this.estado=estado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getEstado() {
        return estado;
    }
}
