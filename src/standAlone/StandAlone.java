package standAlone;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class StandAlone {

	//private char checkSun;
	private char id =(char) 0;
	private char replyid =(char)0;
	private String desc; // descrição dispositivo
	private int porta; // porta para conexão utp;
	private String senha; // senha conexão com dispositivo
	private String host; // endereço de conexão com dispositivo
	private char tipoCom; // define o tipo de conexão, por equanto somente
							// udp/ip

	private InetAddress endIp; // endereço do dispostivo recebe String host

	public byte[] recebeu;
	private DatagramSocket socket;
	private DatagramPacket receivePacket;
	private DatagramPacket sendPacket;

	public StandAlone(String desc, String host) {
		super();
		this.desc = desc;
		this.host = host;
		this.porta = 4370;
		
		setHost(this.host);
	}

	public StandAlone(String desc, String host, int porta) {
		super();
		this.desc = desc;
		this.host = host;
		this.porta = porta;
		setHost(this.host);
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getPorta() {
		return porta;
	}

	public boolean setPorta(int porta) {
		if ((porta <= 65539) && (porta >= 1)) {
			this.porta = porta;
			return true;
		}
		return false;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getHost() {
		return host;
	}

	public boolean setHost(String host) {
		if (!host.equals("")) {
			this.host = host;
			try {
				endIp = InetAddress.getByName(this.host);
				return true;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return false;
	}

	public char getTipoCom() {
		return tipoCom;
	}

	public void setTipoCom(char tipoCom) {
		this.tipoCom = tipoCom;
	}

	private static char calcCheckSum(char[] word, int len) {

		int sum = 0;
		int i = 0;
		while (len > 1) {
			sum += word[i];
			if ((sum & 0x80000000) != 0) {
				sum = (sum & 0xFFFF) + (sum >> 16);
			}
			i++;
			len -= 2;
		}
		while (sum > 65536) {
			sum = (sum & 0xFFFF) + (sum >> 16);
		}
		return (char) ~sum;
	}

	public static int toUnsignedInt(byte x) {
		return ((int) x) & 0xff;
	}

	public char[] send(char[] buffer) {
		byte[] receiveData = new byte[1032];
		byte[] pacote = new byte[buffer.length * 2];
		
		
		buffer[3]++; // inc rpid
		System.out.println((int)buffer[3]);
		buffer[1] = 0; // init by chksum
		buffer[1] = calcCheckSum(buffer, buffer.length);

		for (int i = 0; i < buffer.length; i++) {

			pacote[i * 2 + 0] = new Integer((int) buffer[i] & 0xff).byteValue();
			pacote[i * 2 + 1] = new Integer(((int) buffer[i] >> 8) & 0xff).byteValue();
		}

		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(1000);
			sendPacket = new DatagramPacket(pacote, pacote.length, endIp, porta);
			socket.send(sendPacket);
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(receivePacket);
			recebeu=receivePacket.getData();
		} catch (SocketException e) {
			char[] retorno = { (char) 2006 };
			return retorno;

		} catch (IOException e) {
			char[] retorno = { (char) 2007 };
			return retorno;
		}

		String aux = "";
		char[] receiver = new char[receivePacket.getData().length /2];
		int[] receiverBuffer = new int[receivePacket.getData().length];
		for (int i=0; i<receiverBuffer.length; i++) {
            receiverBuffer[i] = toUnsignedInt( receivePacket.getData()[i]);
        }
		for (int i = 0; i < receiverBuffer.length/2; i++) {
			receiver[i] = (char) ((receiverBuffer[i * 2 + 1] << 8) + receiverBuffer[i * 2]);
			aux += (" <" + String.valueOf(i) + ": " + (int) receiver[i]);
		}
		id=receiver[2];
		replyid=receiver[3];
System.out.println(aux);
		return receiver;

	}

	public char conectar() {

		char[] cmd = { 1000, 0, 0, 0 };			
		cmd= send(cmd);
		try {
			Thread.sleep(1000);
			abrirPorta();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmd[0];

	}

	public char desconectar() {
		char[] cmd = { 1001, 0, 0, 0 };
		return send(cmd)[1];
	}

	public char reiniciar() {
		char[] cmd = { 1004, 0, 0, 0 };
		return send(cmd)[0];
	}
	
	public char desligar(){
		char[] cmd = { 1005, 0, 0, 0 };
		return send(cmd)[0];
	}
	
	public char abrirPorta(){
		char[] cmd= {(char)31,(char)0,id,replyid};
		return send(cmd)[0];
	}
	
 	public char[] capturaDigital(){
		char[] cmd = { 1009, 0, 0, 0 };
		return send(cmd);
	}
	
	public char addUsuario(char pin,char privilegio, char[] senhaUsuario, char[] nomeUsuario ){
	
		char[] cmd = new char[2+senhaUsuario.length+nomeUsuario.length];
		cmd[0]=pin;
		cmd[1]=privilegio;
		int i=0;
		for(i=2; i<senhaUsuario.length;i++)cmd[i]=senhaUsuario[i-2];
		//for(i=i; i<nomeUsuario.length;i++)cmd[i]=nomeUsuario[i-2];
		return send(cmd)[0];
	}
	
}

