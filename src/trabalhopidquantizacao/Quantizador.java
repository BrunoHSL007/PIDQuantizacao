/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhopidquantizacao;

import java.awt.Color;
import static java.lang.Math.pow;
import java.util.ArrayList;

/**
 *
 * @author BrunoHSL007
 */
public class Quantizador {
    public int[][][] matriz;
    private ArrayList<Cortex> divisoes = new ArrayList<Cortex>();

    public ArrayList<Cortex> getDivisoes() {
        return divisoes;
    }

    public void setDivisoes(ArrayList<Cortex> divisoes) {
        this.divisoes = divisoes;
    }
    
    ArrayList<Color> PaletaCores;

    public Quantizador() {
        this.matriz = new int[256][256][256];
    }

    public void incrementaMatriz(int b, int g, int r) {
        this.matriz[b][g][r]++;
    }
    
    
    public void Quantiza(boolean opcao){ // se 0 chama a função por amplitude, se 1 chama a da variancia 
        Cortex aux = new Cortex();
        aux.setInicioB(0);
        aux.setInicioG(0);
        aux.setInicioR(0);
        aux.setFimB(255);
        aux.setFimG(255);
        aux.setFimR(255);
        //aux.setProcessado(0);
        this.divisoes.add(aux);
        escolheCanal(opcao);
    }
    
    public void escolheCanal(boolean opcao){
        while((this.divisoes.size()< 256)){//||(flag)){
            int valorR, valorG, valorB;
            Cortex aux = this.divisoes.get(0);
            valorB=calculaAmplitude('B',aux.getInicioB(),aux.getFimB(), aux.getInicioR(),aux.getFimR(), aux.getInicioG(),aux.getFimG());
                valorG=calculaAmplitude('G',aux.getInicioG(),aux.getFimG(), aux.getInicioB(),aux.getFimB(), aux.getInicioR(),aux.getFimR());
                valorR=calculaAmplitude('R', aux.getInicioR(),aux.getFimR(), aux.getInicioG(),aux.getFimG(), aux.getInicioB(),aux.getFimB());
                
            if((opcao)){
                valorB=calculaVariancia('B', aux.getInicioB(),aux.getFimB(), aux.getInicioR(),aux.getFimR(), aux.getInicioG(),aux.getFimG());
                valorG=calculaVariancia('G', aux.getInicioG(),aux.getFimG(), aux.getInicioB(),aux.getFimB(), aux.getInicioR(),aux.getFimR());
                valorR=calculaVariancia('R', aux.getInicioR(),aux.getFimR(), aux.getInicioG(),aux.getFimG(), aux.getInicioB(),aux.getFimB());
            }
                if((valorB>=valorR) && (valorB>=valorG)){
                    cortaCanal('B',aux.getInicioB(),aux.getFimB(), aux.getInicioR(),aux.getFimR(), aux.getInicioG(),aux.getFimG());
                    //System.out.println("Cor B");
                }
                else if((valorG>=valorR) && (valorG>=valorB)){
                    cortaCanal('G',aux.getInicioG(),aux.getFimG(), aux.getInicioB(),aux.getFimB(), aux.getInicioR(),aux.getFimR());
                    //System.out.println("Cor G");
                }
                else if((valorR>=valorG) && (valorR>=valorB)){
                    cortaCanal('R', aux.getInicioR(),aux.getFimR(), aux.getInicioG(),aux.getFimG(), aux.getInicioB(),aux.getFimB() );
                    //System.out.println("Cor R");
                }
        }
        this.PaletaCores = new ArrayList<Color>();
        for(int i=0;i<this.divisoes.size();i++){
            int maior=0;
            int posR=0, posG=0, posB=0;
            for(int b=this.divisoes.get(i).getInicioB();b<=this.divisoes.get(i).getFimB();b++){
                for(int g=this.divisoes.get(i).getInicioG();g<=this.divisoes.get(i).getFimG();g++){
                   for(int r=this.divisoes.get(i).getInicioR();r<=this.divisoes.get(i).getFimR();r++){
                       if(this.matriz[b][g][r] >= maior){
                           maior = this.matriz[b][g][r];
                           posB = b;
                           posG = g;
                           posR = r;
                       }
                    } 
                }
            }
            this.divisoes.get(i).setCorB(posB);
            this.divisoes.get(i).setCorG(posG);
            this.divisoes.get(i).setCorR(posR);
            //Criar tabela de cores
            Color aux = new Color(posB,posG,posR);
            this.PaletaCores.add(aux);
            
        }
    }
    
    public void cortaCanal(char canal, int inicio1, int fim1, int inicio2, int fim2, int inicio3, int fim3){
        int mediana = 0;
        //Valor é a soma de todas as frequencias/2 que é a mediana
        for(int i=inicio1;i<=fim1;i++){
            if(canal=='B')
                mediana+=calculaForB(i,inicio2, fim2, inicio3, fim3);
            else if(canal=='G')
                mediana+=calculaForG(i,inicio2, fim2, inicio3, fim3);
            else if(canal=='R')
                mediana+=calculaForR(i,inicio2, fim2, inicio3, fim3);
        }
        mediana=mediana/2; //mediana
        int freqAcu=0; //Vai somando as frequencias
        int indice=0; //Cor do canal
        if((fim1-inicio1)==0){
            //remove o primeiro valor e coloca ele no final da lista
            this.divisoes.add(this.divisoes.get(0));
            this.divisoes.remove(0);
        }
        else{
                for(int i=inicio1;i<=fim1;i++){
                    int freq=0; //frequencia da rodelinha
                    if(canal=='B')
                        freq=calculaForB(i,inicio2, fim2, inicio3, fim3);
                    else if(canal=='G')
                        freq=calculaForG(i,inicio2, fim2, inicio3, fim3);
                    else if(canal=='R')
                        freq=calculaForR(i,inicio2, fim2, inicio3, fim3);
                    int IniMed;
                    IniMed=freqAcu+freq;
                    //MedFim=fim1-IniMed;
                    if((IniMed>=mediana)&&(freq!=0)){
                        int valorAnterior = mediana - freqAcu;
                        int valorProximo = IniMed - mediana;
                        if(valorAnterior<=valorProximo){
                            if(i!=inicio1)//caso a frequencia já passe na primeira passada do laço
                                indice = i-1;
                            else{
                                indice = i;
                            }
                        }
                        else{
                            indice = i;
                        }
                        break;
                    }
                    freqAcu+=freq;
                }
            Cortex enzo = new Cortex();
            Cortex valentina = new Cortex();// os dois filhos de francisco
            if(canal=='B'){
                enzo.setInicioB(this.divisoes.get(0).getInicioB());
                enzo.setInicioG(this.divisoes.get(0).getInicioG());
                enzo.setInicioR(this.divisoes.get(0).getInicioR());
                enzo.setFimB(indice);
                enzo.setFimG(this.divisoes.get(0).getFimG());
                enzo.setFimR(this.divisoes.get(0).getFimR());

                if(indice!=fim1){
                    valentina.setInicioB(indice+1);
                }
                else{
                    valentina.setInicioB(indice);
                }
                valentina.setInicioG(this.divisoes.get(0).getInicioG());
                valentina.setInicioR(this.divisoes.get(0).getInicioR());
                valentina.setFimB(this.divisoes.get(0).getFimB());
                valentina.setFimG(this.divisoes.get(0).getFimG());
                valentina.setFimR(this.divisoes.get(0).getFimR());
            }
            else if(canal=='G'){
                enzo.setInicioB(this.divisoes.get(0).getInicioB());
                enzo.setInicioG(this.divisoes.get(0).getInicioG());
                enzo.setInicioR(this.divisoes.get(0).getInicioR());
                enzo.setFimB(this.divisoes.get(0).getFimB());
                enzo.setFimG(indice);
                enzo.setFimR(this.divisoes.get(0).getFimR());

                valentina.setInicioB(this.divisoes.get(0).getInicioB());
                if(indice!=fim1){
                    valentina.setInicioG(indice+1);
                }
                else{
                    valentina.setInicioG(indice);
                }
                valentina.setInicioR(this.divisoes.get(0).getInicioR());
                valentina.setFimB(this.divisoes.get(0).getFimB());
                valentina.setFimG(this.divisoes.get(0).getFimG());
                valentina.setFimR(this.divisoes.get(0).getFimR());
            }
            else if(canal=='R'){
                enzo.setInicioB(this.divisoes.get(0).getInicioB());
                enzo.setInicioG(this.divisoes.get(0).getInicioG());
                enzo.setInicioR(this.divisoes.get(0).getInicioR());
                enzo.setFimB(this.divisoes.get(0).getFimB());
                enzo.setFimG(this.divisoes.get(0).getFimG());
                enzo.setFimR(indice);
                valentina.setInicioB(this.divisoes.get(0).getInicioB());
                valentina.setInicioG(this.divisoes.get(0).getInicioG());
                if(indice!=fim1){
                    valentina.setInicioR(indice+1);
                }
                else{
                    valentina.setInicioR(indice);
                }
                valentina.setFimB(this.divisoes.get(0).getFimB());
                valentina.setFimG(this.divisoes.get(0).getFimG());
                valentina.setFimR(this.divisoes.get(0).getFimR());
            }
            this.divisoes.remove(0);
            
            
            //-Reconstruir a imagem
            freqAcu=0;
            for(int i=enzo.getInicioB();i<=enzo.getFimB();i++){
                freqAcu+=calculaForB(i,enzo.getInicioR(), enzo.getFimR(), enzo.getInicioG(), enzo.getFimG());
            }
            if(freqAcu!=0){
                this.divisoes.add(enzo);
            }
            
            freqAcu=0;
            for(int i=valentina.getInicioB();i<=valentina.getFimB();i++){
                freqAcu+=calculaForB(i,valentina.getInicioR(), valentina.getFimR(), valentina.getInicioG(), valentina.getFimG());
            }
            if(freqAcu!=0){
                this.divisoes.add(valentina);
            }
        }
        
    }
    public int calculaVariancia(char canal, int inicio1, int fim1, int inicio2, int fim2, int inicio3, int fim3){
        int soma1 = 0;
        int soma2 = 0;
        int soma3 = 0;
        for(int i=inicio1;i<=fim1;i++){
            if(canal=='B'){
                soma1+=i*(calculaForB(i,inicio2, fim2, inicio3, fim3));
                soma2+=(calculaForB(i,inicio2, fim2, inicio3, fim3));
                soma3+=pow(i,2)*(calculaForB(i,inicio2, fim2, inicio3, fim3));
            }
            else if(canal=='G'){
                soma1+=i*calculaForG(i,inicio2, fim2, inicio3, fim3);
                soma2+=calculaForG(i,inicio2, fim2, inicio3, fim3);
                soma3+=pow(i,2)*calculaForG(i,inicio2, fim2, inicio3, fim3);
            }
            else if(canal=='R'){
                soma1+=i*calculaForR(i,inicio2, fim2, inicio3, fim3);
                soma2+=calculaForR(i,inicio2, fim2, inicio3, fim3);
                soma3+=pow(i,2)*calculaForR(i,inicio2, fim2, inicio3, fim3);
            }
        }
        return (int) ((soma3-(pow(soma1,2)/soma2))/soma2);
    }
    public int calculaAmplitude(char canal, int inicio1, int fim1, int inicio2, int fim2, int inicio3, int fim3){
        int valor=0;
        int maximo=0;
        int minimo=9999999;
        for(int i=inicio1;i<=fim1;i++){
            if(canal=='B')
                valor=calculaForB(i,inicio2, fim2, inicio3, fim3);
            else if(canal=='G')
                valor=calculaForG(i,inicio2, fim2, inicio3, fim3);            
            else if(canal=='R')
                valor=calculaForR(i,inicio2, fim2, inicio3, fim3);
            
            if(valor!=0){
                minimo = i;
                break;
            }
            
        }
        valor=0;
        for(int i=fim1;i>=minimo;i--){
            if(canal=='B')
                valor=calculaForB(i,inicio2, fim2, inicio3, fim3);
            else if(canal=='G')
                valor=calculaForG(i,inicio2, fim2, inicio3, fim3);
            else if(canal=='R')
                valor=calculaForR(i,inicio2, fim2, inicio3, fim3);
            if(valor!=0){
                maximo = i;
                break;
            }
            
        }
        if(canal=='B'){
                this.divisoes.get(0).setFimB(maximo);
                this.divisoes.get(0).setInicioB(minimo);
        }
        else if(canal=='G'){
                this.divisoes.get(0).setFimG(maximo);
                this.divisoes.get(0).setInicioG(minimo);
        }        
        else if(canal=='R'){
                this.divisoes.get(0).setFimR(maximo);
                this.divisoes.get(0).setInicioR(minimo);
        }
        return maximo - minimo; //Retorna amplitude
    }
    public int calculaForB(int b, int inicioR, int fimR, int inicioG, int fimG){
        int valor=0;
        for(int r=inicioR;r<=fimR;r++){
            for(int g=inicioG;g<=fimG;g++){
                valor+=this.matriz[b][g][r];
            }
        }
        return valor;
    }
    public int calculaForG(int g, int inicioB, int fimB, int inicioR, int fimR){
        int valor=0;
        for(int b=inicioB;b<=fimB;b++){
            for(int r=inicioR;r<=fimR;r++){
                valor+=this.matriz[b][g][r];
            }
        }
        return valor;
    }
    public int calculaForR(int r, int inicioG, int fimG, int inicioB, int fimB){
        int valor=0;
        for(int g=inicioG;g<=fimG;g++){
            for(int b=inicioB;b<=fimB;b++){
                valor+=this.matriz[b][g][r];
            }
        }
        return valor;
    }
    
    
    
}
