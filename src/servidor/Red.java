package servidor;

import conexiones.cliente.Conexion;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * *#*#4636#*#*  
 * @author danielbeta
 */
public final class Red {

    private final int puerto;
    private final int numBits;
    private final int octeto;
    private final char[] red;
    private final char[] mascara;
    private final char[] broadcast;
    private final char[] ip;
    private ArrayList<String> ipsConectadas;
    private Conexion conexion;
    private boolean esSegura;
     
    /**
     * 
     * @param esSegura
     * @throws Exception 
     */
    public Red(boolean esSegura) throws Exception{
        
        this.puerto = 2317;
        this.numBits=32;
        this.octeto=8;
        this.ip = new char[numBits];
        this.red = new char[numBits];
        this.broadcast = new char[numBits];
        this.mascara = new char[numBits];
        this.ipsConectadas = new ArrayList<>();
        this.ipsConectadas.add("192.168.0.4");  //Pruebas
        
        this.esSegura = esSegura;
        
        if(this.esSegura){ this.conexion = new conexiones.cliente.Segura(this.convertirDireccionBinariaADecimal(this.ip), this.puerto); } 
        else { this.conexion = new conexiones.cliente.Insegura(this.convertirDireccionBinariaADecimal(this.ip), this.puerto); } 
        
        this.obtenerProtocoloRedEquipoLocal();
    }
    
    public Red() throws Exception{
        
        this.puerto = 2317;
        this.numBits=32;
        this.octeto=8;
        this.ip = new char[numBits];
        this.red = new char[numBits];
        this.broadcast = new char[numBits];
        this.mascara = new char[numBits];        
        this.esSegura = false;
        this.obtenerProtocoloRedEquipoLocal();
    }
    
    /**
     * Analiza las interfaces de red activas del equipo y determina la el protocolo de red
     * @return IP del equipo.
     * @throws Exception En caso de que el equipo no este conectado a red.
     */
    private void obtenerProtocoloRedEquipoLocal() throws Exception{

        String ip = "";
        String mascara = null;
        
        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces(); //Obtiene todas las interfaces activas de red del equipo.
        
        for (; n.hasMoreElements();){ //Recorre todas las interfaces encontradas
            
                NetworkInterface e = n.nextElement();
                Enumeration<InetAddress> a = e.getInetAddresses();
                
                for (; a.hasMoreElements();){
                    
                    InetAddress addr = a.nextElement();
                        
                    if(addr.getHostAddress().startsWith("192")){ //Verifica que la direccion de host sea de la red local.
                        
                        ip = addr.getHostAddress();
                        mascara = Short.toString(e.getInterfaceAddresses().get(1).getNetworkPrefixLength());
                        break;
                    }
                }
                
                if(ip.startsWith("192"))
                    break;
        }
        
        if(mascara==null)
            throw new Exception("Equipo no conectado a red.");
        
        this.guardarIPEnBinario(ip);
        this.guardarMascaraEnBinario(mascara);
        this.hallarDireccionRed();
        this.hallarDireccionBroadcast();
    }
    
    /**
     * Convierte un numero decimal en binario, si la cantidad de bits es menor a ocho, se agrega el faltante con ceros a la izquierda.
     * @param decimal Numero decimal entero a convertir.
     * @return Lista con los bits que corresponden al numero decimal.
     */
    private ArrayList<String> convertirDecimalBinario(int decimal){
        
        ArrayList<String> binariosReves = new ArrayList<>();
        ArrayList<String> binarios = new ArrayList<>();

        while(decimal !=0){
            
            binariosReves.add(Integer.toString(decimal%2));
            decimal = decimal/2;
        }
        
        while(binariosReves.size() < octeto)
            binariosReves.add("0");
        
        for(int i=binariosReves.size()-1; i>=0; i--)
            binarios.add(binariosReves.get(i));
        
        return binarios;
    }
    
    /**
     * Convierte un numero binario en decimal
     * @param binario Binario a convertir
     * @return Representacion decimal del numero binario
     */
    private String convertirBinarioDecimal(String binario){
        
        int decimal = 0;
        char[] binarios = binario.toCharArray();
        
        for(int i=0; i<binarios.length; i++){
            
            if(binarios[i] == '1')
                decimal += Math.pow(2, (binarios.length-1)-i);
        }
        
        return Integer.toString(decimal);
    }

    /**
     * 
     * @param ip 
     */
    private void guardarIPEnBinario(String ip) {
        
        String[] partesIP = ip.split("\\.");
        
        /* Convierte cada octeto de la ip en binario */
        int posicionBit = 0;
        for(String parte : partesIP)
            for(String bit : this.convertirDecimalBinario(Integer.parseInt(parte))){
                this.ip[posicionBit] = bit.charAt(0);
                posicionBit++;
            }
    }

    /**
     * 
     * @param mascara 
     */
    private void guardarMascaraEnBinario(String mascara) {
        
         /* Cnvierte la mascara en binario*/
        for(int bit=0; bit<numBits; bit++)
            if(bit < Integer.parseInt(mascara))
                this.mascara[bit]='1';
            else
                this.mascara[bit]='0';
    }

    /**
     * 
     */
    private void hallarDireccionRed() {
        
         for(int bit=0; bit<numBits; bit++){
            
            if(ip[bit]=='1' && mascara[bit]=='1')
                red[bit]='1';
            else
                red[bit]='0';
        }
    }

    /**
     * 
     */
    private void hallarDireccionBroadcast() {
        
        char[] mascaraCambiada = new char[numBits];
        
        for(int bit=0; bit<numBits; bit++)
            if(mascara[bit] == '1')
                mascaraCambiada[bit]='0';
            else
                mascaraCambiada[bit]='1';
        
        for(int bit=0; bit<numBits; bit++){
            
            if(red[bit]=='1' || mascaraCambiada[bit]=='1')
                broadcast[bit]='1';
            else
                broadcast[bit]='0';
        }
    }
    
    public String convertirDireccionBinariaADecimal(char[] direccion){
        
        String dir = "";   
        
        for(int i=0; i<4; i++){
            
            String octeto = "";
            
            for(int j=(i*this.octeto); j<(i*this.octeto)+this.octeto; j++)
                octeto += direccion[j];
            
            dir += this.convertirBinarioDecimal(octeto) + ".";
        }
        
       return dir.substring(0, dir.length()-1);
    }
    
    
    /**
     * 
     * @param ip
     * @return
     * @throws UnknownHostException
     * @throws IOException 
     */
    private boolean hacerPing(String ip) throws UnknownHostException, IOException, Exception{

        System.out.print("Escaneando direccion IP: " + ip);
        if(InetAddress.getByName(ip).isReachable(1000)){
            
            System.out.print(" -> Alcanzada");
            try{
                
                this.conexion.crearConexion();
                System.out.println(" -> ¡SI es mi amiguito!");
                
                this.conexion.establecerES(null, null);
                DataInputStream entrada = this.conexion.obtenerEntrada();
                DataOutputStream salida = this.conexion.obtenerSalida();
                
                salida.write(("¿Eres mi amiguito?").getBytes());
                salida.flush();
                
                if(entrada.read() > 0)
                    this.ipsConectadas.add(ip);
                
                this.conexion.cerrarConexiones();
            } catch(ConnectException ex){
            
                System.out.println(" -> ¡NO es mi amiguito!");
            }
            
            return true;
        } else { System.out.println(" -> No alcanzada"); }
        
        return false;
    }
    
    
    /**
     * Analiza la red donde se encuentra el equipo en busca de ips activas.
     * @throws IOException 
     */
    public void escanearIPSConectadasARed() throws IOException, Exception{
        
        String[] redPartes = this.convertirDireccionBinariaADecimal(red).split("\\.");
        String[] broadcastPartes = this.convertirDireccionBinariaADecimal(broadcast).split("\\.");
        String[] ipActualPartes = {redPartes[0], redPartes[1], redPartes[2], redPartes[3]};
        String ipActual;
        
        int[] broadcastPartesInt = {Integer.parseInt(broadcastPartes[0]), Integer.parseInt(broadcastPartes[1]), Integer.parseInt(broadcastPartes[2]), Integer.parseInt(broadcastPartes[3])};
        //int[] ipActualPartesInt = {Integer.parseInt(ipActualPartes[0]), Integer.parseInt(ipActualPartes[1]), Integer.parseInt(ipActualPartes[2]), Integer.parseInt(ipActualPartes[3])+1};
        int[] ipActualPartesInt = {Integer.parseInt(ipActualPartes[0]), Integer.parseInt(ipActualPartes[1]), Integer.parseInt(ipActualPartes[2]), Integer.parseInt(ipActualPartes[3])+0};
        
        while(  ipActualPartesInt[0] < 192 || //COLOCAR LAS PARTES DEL BROADCASR
                ipActualPartesInt[1] < 168 ||
                ipActualPartesInt[2] < 0 ||
                //ipActualPartesInt[3] < broadcastPartesInt[3]-1){
                ipActualPartesInt[3] < 10){
            
            ipActual=ipActualPartesInt[0] + "." + ipActualPartesInt[1] + "." + ipActualPartesInt[2] + "." + ipActualPartesInt[3];
            
            this.hacerPing(ipActual);
                
            ipActualPartesInt[3] = ipActualPartesInt[3]+1;
            
            if(ipActualPartesInt[3] > 255){
                
                ipActualPartesInt[3]=0;
                ipActualPartesInt[2] = ipActualPartesInt[2]+1;
                
                if(ipActualPartesInt[2] > 255
                        ){
                    
                    ipActualPartesInt[2]=0;
                    ipActualPartesInt[1] = ipActualPartesInt[1]+1;
                    
                    if(ipActualPartesInt[1] > 255){
                    
                        ipActualPartesInt[1]=0;
                        ipActualPartesInt[0] = ipActualPartesInt[0]+1;
                    }
                }
            }
                
        }
        
        if(this.ipsConectadas.size() <= 0 ){
            
            System.out.println("Escaneo finalizado: " + this.ipsConectadas.size() + " direcciones IP amigas :'(");
        } else {
            
            System.out.println("Escaneo finalizado: " + this.ipsConectadas.size() + " direcciones IP amigas :D");
        }
    }

    public String obtenerIp() {
        
        return this.convertirDireccionBinariaADecimal(this.ip);
    }

    public ArrayList<String> obtenerIPSEnRed() {
       
        return this.ipsConectadas;
    }

    public int obtenerPuerto() {
        
        return this.puerto;
    }

    public String obtenerMascara() { return this.convertirDireccionBinariaADecimal(this.mascara); }
}
