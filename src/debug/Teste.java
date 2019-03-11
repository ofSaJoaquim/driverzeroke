package debug;

import standAlone.StandAlone;

public class Teste {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		StandAlone teste = new StandAlone("Controle Acesso Teste", "192.168.2.201", 4370);
		System.out.println((int)teste.conectar());
		System.out.println((int)teste.conectar());
		System.out.println((int)teste.abrirPorta());
		
		
	}

}
