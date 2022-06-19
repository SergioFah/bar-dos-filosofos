package barDosFilosofos;


/**
*
* @Autor Antônio Sérgio A. Faheina // Matrícula 1394159
* 
* @Classe Filosofo
* 
* @Objetivos
* 
* - Unidade das threades que concorrerão na simulação
* - Guarda variaveis particulares de cada filosofo
* - Alterna por si só entre os estados de acordo com o fluxo de execução.
* - Implementa a temporarização de cada estado
* - Armazena seu tempo de execução e o tempo de "Sede" média
* 
* 
* 
*/

public class Filosofo implements Runnable{
	
	
	int id;  //Número de identificação de cada um
	int nGarrafasCompartilhadas = 0; //Numero de garrafas que esse filosofo compartilha
	
	final int TRANQUILO = 0;
	final int SEDE = 1;
	final int BEBENDO = 2; 
	
	//TEMPORARIZAÇÃO///
	long tempoSedeTotal = 0; 
	long ultimoSedeMedido = 0;
	long timeStart;
	///////////////////////////
	
	public int requisitadas[]; //Garrafas sendo requisitadas no momento
	
	
	public Filosofo(int id) {  // Construtor para iniciar as variaveis instanciadas.
		this.id = id;
		new Thread(this).start(); 
		timeStart = System.currentTimeMillis();
		ultimoSedeMedido = System.currentTimeMillis();
		
		Thread.currentThread().setPriority(1);
		for(int i = 0; i<Bar.matriz.length; i++) {
			if(Bar.matriz[id][i] == 1){
				nGarrafasCompartilhadas ++; 
			}
		}
		
		requisitadas = new int[Bar.bebidas.length];
		for(int i = 0; i<requisitadas.length; i++) {
			requisitadas[i] = 0;
		}				
				
	}
	
	
	public  void Tranquilo() { //Chamada após a função bebendo e no inicio do programa
		synchronized(this) {
			for(int i = 0; i<requisitadas.length; i++) { // Limpar a matriz de bebida.
				 if(requisitadas[i]==1) {
					 Bar.bebidas[id][i] = 1; //Relação com essa garrafa
					 Bar.bebidas[i][id] = 1; //Relação espelhada
				 }
			 }	
		}
		for(int i = 0; i<requisitadas.length; i++) {
			requisitadas[i] = 0;  //Limpa a matriz de requisição, já que este está tranquilo 
		}	
		Bar.estado[this.id] = TRANQUILO; //Atualiza seu estado
		System.out.println("(Filosofo " + this.id + ") está TRANQUILO");
		synchronized(this) {
			notifyAll();
		}
		try {
			int rand = ((int)(Math.random()*2000));  //Sorteia o tempo de tranquilo até ter sede, de 0-2 segundos.
			Thread.sleep(rand);
		} catch (InterruptedException ex) {
            System.out.println("ERROR>" + ex.getMessage());
            return;
        }
	}
	
	
	public  void Sede() {
		tempoSedeTotal += System.currentTimeMillis() - ultimoSedeMedido; //temporizador somatório
		Bar.estado[this.id] = SEDE;  //tt status
		System.out.println("(Filosofo " + this.id + ") está com SEDE");
		new Requisicao().Requisitar(id, ((int)(Math.random()*(nGarrafasCompartilhadas-1)))+2);  //Inicia requisição para que este possa beber de acordo com o numero sorteado entre 2 e n garrafas que esse compartilha 
		ultimoSedeMedido = System.currentTimeMillis();
	}
	
	
	public void  Bebendo() {  //Chamada após requisição ser atendida
		
		synchronized(this) {
			for(int i = 0; i<requisitadas.length; i++) {  //Sinaliza que as requisitadas estão ocupadas
				 if(requisitadas[i]==1) {
					 Bar.bebidas[id][i] = 2; //sinaliza garrafa pega
					 Bar.bebidas[i][id] = 2; //sua relação simétrica
				 }
			 }	
		}
		Bar.estado[this.id] = BEBENDO; //att status 
		System.out.println("(Filosofo " + this.id + ") está BEBENDO " + "(Sessão "+Bar.vezesBebidas[id]+")");
		
		try { 
			Thread.sleep(1000); //Tempo de bebida
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void run() {
		while(Bar.vezesBebidas[id] < Bar.nBebidas) { //Enquanto não satisfizer o numero estipulado de bebidas, repetir.
			Tranquilo();
			Sede();
			Bebendo();
		}
		Tranquilo();
		System.out.println("\n\n=================================================");
		System.out.println("(Filosofo " + this.id + ") foi SATISFEITO em: " + (System.currentTimeMillis()-timeStart)/1000.0f);
		System.out.println("(Filosofo " + this.id + ") teve seu tempo de sede médio em: " + ((tempoSedeTotal/1000.0f)/Bar.nBebidas));
		System.out.println("=================================================\n\n");
	}
	
}
