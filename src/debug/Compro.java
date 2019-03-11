package debug;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JOptionPane;
public class Compro {
	





	
	    static Socket client;
	    static DatagramSocket clientSocket;
	    static int porta;
	    static InetAddress IPAddress;
	    
	    public static void main(String[] args)  {
			System.out.println("***** Dialer_Socket.java ******");
		
		
	    	client = null;
	    	String s = "";

		
	            System.out.println("0");
	            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
	            clientSocket = new DatagramSocket();
	            clientSocket.setSoTimeout(1000);
	            IPAddress = InetAddress.getByName("192.168.2.201");
	            porta = 4370;
	            char[] cmd = new char[4];
	          
	            
	           // cmd[0] = Short.valueOf(JOptionPane.showInputDialog("Informe"));
	            System.out.println("udp CMD_CONNECT");
	            cmd[0] = (char) 1000;  // command
	            cmd[1] = (char) 0x0;   // chksum
	            cmd[2] = (char) 0x0;   // sid
	            cmd[3] = (char) 0x0;   // replyId
	            
	           // cmd[4] = (char) 0x0;
	            cmd = sendCmd(cmd);
	 
	            
	           // cmd[0] = Short.valueOf(JOptionPane.showInputDialog("Informe II"));
	           Thread.sleep(1000);
	            System.out.println("udp CMD_UNLOCK");
	            cmd[0] = (char) 31;
	            cmd = sendCmd(cmd);
	            
	            Thread.sleep(1000);
	            System.out.println("udp CMD_UNLOCK");
	            cmd[0] = (char) 1801;
	            cmd = sendCmd(cmd);
	            
	                       
	            Thread.sleep(1000);
	            System.out.println("udp CMD_DISCONNET");
	            cmd[0] = (char) 1100;
	           cmd = sendCmd(cmd);


	            clientSocket.disconnect();
	            clientSocket.close();
	
		}
		
	    
	    
	    public static  int toUnsignedInt(byte x){
	        return ((int) x) & 0xff;
	    }

	    public static  char[] sendCmd(char[] cmd) {
	        byte[] bytesRecebidos = new byte[1032];
	        byte[] bytesEnviados = new byte[8];
	        int[] resyltUnsinints = new int[8];	        
	        String aux="";
	        try {     
	            cmd[3]++; // inc rpid	            
	            cmd[1] = 0; // init by chksum
	            cmd[1] = calcCheckSum(cmd,cmd.length);
	            for (int i=0; i<cmd.length; i++) {
	                aux += (" >"+String.valueOf(i)+": "+cmd[i]);
	                int temp = (int)cmd[i];
	                bytesEnviados[i*2+0] = new Integer(temp & 0xff).byteValue();
	                bytesEnviados[i*2+1] = new Integer((temp >> 8) & 0xff).byteValue();    
	            }
	           //byte[]pacote2={pacote[0],pacote[1],pacote[2],pacote[3],pacote[4],pacote[5],pacote[6],pacote[7],pacote[8],pacote[9],
	        		//   pacote[12],pacote[11],pacote[13],pacote[14],pacote[15]};
	            System.out.println(cmd.length);
	            
	            DatagramPacket sendPacket = new DatagramPacket(bytesEnviados, bytesEnviados.length, IPAddress, porta);
	            clientSocket.send(sendPacket);
	            aux = "udp send "+aux;
	            System.out.println(aux);
	            DatagramPacket receivePacket = new DatagramPacket(bytesRecebidos, bytesRecebidos.length);
	            clientSocket.receive(receivePacket);
	            String modifiedSentence = new String(receivePacket.getData());
	            //modifiedSentence = modifiedSentence.trim();
	            byte[] resultBytes = receivePacket.getData();
	            //System.out.println("FROM SERVER:" + String.valueOf(modifiedSentence.length()));
	            aux = "";
	            for (int i=0; i<8; i++) {
	                resyltUnsinints[i] = toUnsignedInt(resultBytes[i]);
	            }
	            
	            for(int i = 0; i < 4; i++){
	                cmd[i] = (char)((resyltUnsinints[i*2+1] << 8) + resyltUnsinints[i*2]);
	                aux += (" <"+String.valueOf(i)+": "+(int)cmd[i]);
	            }
	            
	            aux = "udp receive" + aux;
	            System.out.println(aux);
		} catch (Exception e) {
		    System.out.println("Erro:"+e.toString());
		}
	        return cmd;
	    }
	    
	    
	    //ORIGINAL LINE: unsigned short in_chksum(unsigned char *p, int len)
	    private static char calcCheckSum(char[] word,int len) {
	        
	       int sum = 0;         
	       int i = 0;
	       len=len*2;
	       
	       while(len>1){
	           sum += word[i];
	           
	           if((sum & 0x80000000) != 0){
	               sum = (sum & 0xFFFF) + (sum >> 16);
	           }
	           i++;
	           len -= 2;
	    }
	       while(sum >> 16 != 0){
	           sum = (sum & 0xFFF) + (sum >> 16);
	       }
	       
	       return(char) ~sum;
	    }    
	}


