/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

/**
 *
 * @author danielbeta
 */
class Protocolo {

    private final String CARACTER_SEPARACION="-";
    
    public Protocolo() {
    }

    public String gestionarPeticion(String peticion) {
        
       String[] peticionPartes = peticion.split(this.CARACTER_SEPARACION);
       String respuesta="";
       
       switch(peticionPartes[0]){
       
           /* Listar todos los archivos que esten en el repositorio */
           case "0000" : {
               
               respuesta = "Listar canciones.";
               break;
           }
           
           /* buscar archivo en repositorio */
           case "0001" : {
               
               respuesta = "Buscar archivo.";
               break;
           }
           
           /* buscar archivo en repositorio */
           case "0010" : {
               
               respuesta = "Descargar archivo a repositorio local.";
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
}
