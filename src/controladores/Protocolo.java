package controladores;

/**
 *
 * @author danielbeta
 */
public class Protocolo {

    private final String DESCARGAR_ARCHIVO="0010";
    private final String ARCHIVO_NO_ENCONTRADO="Archivo no encontrado";
    private final String CARACTER_SEPARACION="\\|";
    private Repositorio repositorio;
    private final Archivador archivador;
    
    public Protocolo() throws Exception {
        
        this.repositorio = new Repositorio();
        this.archivador = new Archivador();
        this.repositorio.cargarObjetos(this.archivador.cargarArchivos());
    }

    public String gestionarPeticion(String peticion) {
        
       String[] peticionPartes = peticion.substring(1).split(this.CARACTER_SEPARACION);
       String respuesta="";
       
       switch(peticionPartes[0]){
       
           /* Listar todos los archivos que esten en el repositorio */
           case "0000" : {
               
               respuesta = this.repositorio.listarCanciones();
               break;
           }
           
           /* buscar archivo en repositorio */
           case "0001" : {
                     
               respuesta = this.repositorio.buscarArchivo(peticionPartes[1]);
               
               if(respuesta == null)
                   respuesta = this.ARCHIVO_NO_ENCONTRADO;
               
               break;
           }
           
           /* descargar archivo en repositorio remoto */
           case "0010" : {
               
                if(peticion.startsWith("S"))
                    respuesta = this.archivador.getRutaRepositorio() + peticionPartes[1];
                else
                    respuesta = this.DESCARGAR_ARCHIVO;
                
                break;
           }
           
           case "0011" : {
               
               respuesta = "Publicar archivo en repositorio remoto.";
               break;
           }
           
           case "0100" : {
               
               respuesta = "Cambiar estado de transferencia.";
               break;
           }
           
           case "0101" : {
               
               respuesta = "Cargar archivo a respositorio local.";
               break;
           }
           
           default : { respuesta="Comando no valido."; break; }
       }
       
       return respuesta;
    }
    
    
    public String archivoNoEncontradoComando(){
        
        return this.ARCHIVO_NO_ENCONTRADO;
    }

    public String descargarArchivoComando() {
        return this.DESCARGAR_ARCHIVO;
    }

    public String obtenerPathRepositorio() { return this.archivador.getRutaRepositorio(); }

    public String comandoSeparacion() { return this.CARACTER_SEPARACION; }
}
