package controladores;

import entidades.ArchivoMusica;
import java.util.ArrayList;

/**
 *
 * @author danielbeta
 */
public class Repositorio {

    private final String CARACTER_SEPARACION="|";
    private final String CARACTER_SALTO=";";
    private ArrayList<ArchivoMusica> archivos;
    
    public Repositorio(){
        
        this.archivos = new ArrayList<>();
    }
    
    public void agregarArchivo(ArchivoMusica archivo) throws Exception{
        
        if(archivo == null)
            throw new Exception("Archivo no existe.");
        
        this.archivos.add(archivo);
    }
    
    public void removerArchivo(String nombreArchivo) throws Exception{
        
        int indiceArchivo = -1;
        
        for(int i=0; i<this.archivos.size(); i++)
            if(this.archivos.get(i).getNombreArchivo().equals(nombreArchivo)){
                
                indiceArchivo=i;
                break;                
            }
        
        this.archivos.remove(indiceArchivo);
    }

    public String buscarArchivo(String nombreArchivo) {
        
        for(ArchivoMusica am : this.archivos)
            if(am.getNombre().contains(nombreArchivo))
                return am.getNombre() + this.CARACTER_SEPARACION +  am.getEstado() + this.CARACTER_SEPARACION +  am.getNombreArchivo();
        
        return null;
    }

    public void cargarObjetos(ArrayList<String[]> archivos) throws Exception {
        
        this.archivos = new ArrayList<>();
        
        for(String[] archivo : archivos)
            this.agregarArchivo(new ArchivoMusica(archivo[0], archivo[2], archivo[1]));
    }

    public String obtenerNombreArchivo(String nombre) {
       
        for(ArchivoMusica am : this.archivos)
            if(am.getNombre().equalsIgnoreCase(nombre))
                return am.getNombreArchivo();
        
        return null;
    }

    public String listarCanciones() {
        
        String lista = "";
        
        for(ArchivoMusica am : this.archivos)
            lista += am.getNombre() + this.CARACTER_SEPARACION 
                    +  am.getEstado() + this.CARACTER_SEPARACION 
                        +  am.getNombreArchivo() + this.CARACTER_SALTO;            
        
        return lista;
    }
}
