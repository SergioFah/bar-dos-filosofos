package barDosFilosofos;

/**
*
* @Autor Antônio Sérgio A. Faheina // Matrícula 1394159
* 
* @Classe Requisição
* 
* @Objetivos
* 
* - Servir de interface para as modificações feitas nas variáveis estaticas para garantir a sincronização e consistência dos dados.
* - Recebe os pedidos para beber, respectivamente o ID do Filosofo que a chamou e o número de garrafas que este requisitou (2 à n)
* - Bloco de código SINCRONIZADO pra limitar o acesso às variaveis estáticas do Bar.
* - Maneja a prioridade de cada filosofo, se este tentou beber e não conseguiu, deve ascender de prioridade, caso beba, retorna à prioridade 1.
* 
* 
* 
*/


public class Requisicao {

	 public  void Requisitar(int id, int n) {
		 while(true) { // Loop que só é quebrado ao se conseguir as garrafas requisitadas.
			 synchronized(this){
				 
				 System.out.println("(Filosofo " + id + ") Requisitou: " + n);
				 int garrafasConseguidas = 0;
				 
				 
				 // Verifica matriz auxiliar de garrafas para perceber se estas estão sendo usadas (0-NÃO COMPARTILHA, 1-COMPARTILHA + GARRAFA LIVRE, 2-COMPARTILHA-GARRAFA OCUPADA)
				 
				 for(int i = 0; i < Bar.bebidas.length; i++) {
					 if(Bar.bebidas[id][i] == 1) {
						 Bar.filosofo[id].requisitadas[i] = 1;   //preenche uma matriz partiuclar do filosofo que indica quais das requisitadas foram liberadas para serem bebidas
						 garrafasConseguidas++;
					 }
					 if(garrafasConseguidas>=n) {
						 break;  
					 }
				 }
				 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				 
				 if(garrafasConseguidas >= n) {   //No caso de conseguir beber.
					 Thread.currentThread().setPriority(1);
					 Bar.vezesBebidas[id]++;
					 notifyAll();
					 break;
				 }else if(Thread.currentThread().getPriority() < 10){
					 Thread.currentThread().setPriority(Thread.currentThread().getPriority()+1);  //aumenta a prioridade a cada falha.
				 }else {
					 
				 }
				 for(int i = 0; i<Bar.filosofo[id].requisitadas.length; i++) {
					 Bar.filosofo[id].requisitadas[i] = 0; //limpa a matriz de requisitadas caso não consiga pegar todas as garrafas que almejava.
				 }	
				 
				 try{
					 System.out.println("(Filosofo " + id +") não conseguiu beber (Prioridade " + Thread.currentThread().getPriority() + ")");
					 wait(1000);  //Tempo mínimo até que um outro filosofo possa liberar e este possa concorrer novamente pela garrafa.
				 }catch(InterruptedException ex){
					 System.out.println("Erro ao fazer o wait");
				 }
			 }
		 }	 
	}
}


