/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhopidquantizacao;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import static java.lang.Math.abs;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

/**
 *
 * @author BrunoHSL007
 */
public class FXMLDocumentController {
    
    @FXML
    private Pane Pane;

    @FXML
    private ImageView imageView;
    
    private File file;
    
    private Quantizador matrizFrequencia;
    
    private int BitCount=0;
    
    private int tamanhoDados=0;
    
    private int sizeLargura;
    
    private int sizeAltura;
    
    @FXML
    void abrirArquivo(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".bmp", "*.bmp"));
        file = fileChooser.showOpenDialog(null);
        if (file != null) {
            exibirImagem();
        }
    }
    @FXML
    void salvarArquivo(ActionEvent event) throws FileNotFoundException, IOException {
            FileChooser salvar = new FileChooser();
            salvar.setTitle("Salve o Arquivo");
            salvar.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".bmp", "*.bmp"));
            File filefile = salvar.showSaveDialog(null);
            FileOutputStream Arq = new FileOutputStream(filefile.getPath());
            Files.copy(file.toPath(), Arq);
            file.delete();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Salvar");
            alert.setHeaderText("ARQUIVO SALVO COM SUCESSO");
            alert.showAndWait();
    }
    void exibirImagem(){
        Image aux1 = new Image(file.toURI().toString());
        System.out.println(""+imageView.isResizable());
        imageView.fitWidthProperty().bind(Pane.widthProperty()); 
        imageView.fitHeightProperty().bind(Pane.heightProperty());
        imageView.setImage(aux1);
        
        
            
    }
    @FXML
    void quantizarVariancia(ActionEvent event) {
        quantizar(true);
    }
    @FXML
    void quantizarAmplitude(ActionEvent event) {
        quantizar(false);
    }
    void quantizar(boolean flag){
        criaHistograma();
        if(BitCount==24) //Se a imagem tiver 24 bits o processo de quantização é feito senão não
        {
            File fileaux;
            String Caminho = file.toPath().toString();
            System.out.println(""+file.toPath().toString());
            int indice = Caminho.lastIndexOf('\\');
            Caminho=Caminho.substring(0,indice);
            fileaux = new File(Caminho+"\\quantization.bmp");
            matrizFrequencia.Quantiza(flag); //Não tá calculando a variância
            try {

                //-recriar a imagem
                FileInputStream imagem = new FileInputStream(file);
                
                FileOutputStream imagemFinal = new FileOutputStream(fileaux);
                    byte aux=(byte) imagem.read(); //Le o primeiro Byte = B
                    imagemFinal.write(aux);
                    aux=(byte) imagem.read();//Le o segundo Byte = M
                    imagemFinal.write(aux);
                    String Size; //Vai conter o tamanho em hexadecimal
                    byte [] data =new byte[4];
                    int alce=tamanhoDados+1078;
                    data[0]=(byte)(alce);//tamanho do arquivo
                    data[1]=(byte)(alce >>> 8);
                    data[2]=(byte)(alce >>> 16);
                    data[3]=(byte)(alce >>> 24);
                    imagemFinal.write(data[0]); //Ou é assim ou é invertido
                    imagemFinal.write(data[1]);
                    imagemFinal.write(data[2]);
                    imagemFinal.write(data[3]);
                    
                    imagem.read();
                    imagem.read();
                    imagem.read();
                    imagem.read();
                    //Pula reservados
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    
                    String OffSetString; //Vai conter o tamanho em hexadecimal
                    int Offset = 1078; //Tamanho do arquivo
                    data[0]=(byte)(Offset);//tamanho do arquivo
                    data[1]=(byte)(Offset >>> 8);
                    data[2]=(byte)(Offset >>> 16);
                    data[3]=(byte)(Offset >>> 24);
                    imagemFinal.write(data[0]);
                    imagemFinal.write(data[1]);
                    imagemFinal.write(data[2]);
                    imagemFinal.write(data[3]);
                    imagem.read();
                    imagem.read();
                    imagem.read();
                    imagem.read();

                    //Tamanho do cabeçalho
                    String TamanhoCabecalho; //Vai conter o tamanho em hexadecimal
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());

                    //Tamanho da Largura da imagem
                    String LarguraString; //Vai conter o tamanho em hexadecimal
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());

                    //Tamanho da Altura da imagem
                    String AlturaString; //Vai conter o tamanho em hexadecimal
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());

                    //Numero de planos
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());

                    //Qtd de bits por pixel
                    BitCount = 8;
                    byte [] data2 =new byte[2];
                    data2[0]=(byte)(BitCount & 0xFF);//tamanho do arquivo
                    data2[1]=(byte)((BitCount >> 8) & 0xFF);
                    imagemFinal.write(data2[0]);
                    imagemFinal.write(data2[1]); //-Verificar se salva em little endian
                    imagem.read();
                    imagem.read();
                    
                    //Compressão
                    String CompressionString; //Vai conter o tamanho em hexadecimal
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());

                    //Tamanho da imagem
                    String SizeImgString; //Vai conter o tamanho em hexadecimal
                    
                    data =new byte[4];
                    data[0]=(byte)(tamanhoDados);//tamanho do arquivo
                    data[1]=(byte)(tamanhoDados >>> 8);
                    data[2]=(byte)(tamanhoDados >>> 16);
                    data[3]=(byte)(tamanhoDados >>> 24);
                    
                    imagemFinal.write(data[0]);
                    imagemFinal.write(data[1]);
                    imagemFinal.write(data[2]);
                    imagemFinal.write(data[3]);
                    imagem.read();
                    imagem.read();
                    imagem.read();
                    imagem.read();

                    //Resolucao X em pixels por metro
                    String XPPmeterString; //Vai conter o tamanho em hexadecimal
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());

                    //Resolucao Y em pixels por metro
                    String YPPmeterString; //Vai conter o tamanho em hexadecimal
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());

                    //Numero de cores de cores usadas
                    String CoresUsadasString; //Vai conter o tamanho em hexadecimal
                    
                    data =new byte[4];
                    int valor = 256;
                    data[0]=(byte)(valor);//tamanho do arquivo
                    data[1]=(byte)(valor >>> 8);
                    data[2]=(byte)(valor >>> 16);
                    data[3]=(byte)(valor >>> 24);
                    
                    imagemFinal.write(data[0]);
                    imagemFinal.write(data[1]);
                    imagemFinal.write(data[2]);
                    imagemFinal.write(data[3]);
                    imagem.read();
                    imagem.read();
                    imagem.read();
                    imagem.read();
                    
                    //Numero de cores de cores importantes
                    String CoresImportantesString; //Vai conter o tamanho em hexadecimal
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    imagemFinal.write(imagem.read());
                    
                    for(int i=0;i<matrizFrequencia.PaletaCores.size();i++){
                        Color color = matrizFrequencia.PaletaCores.get(i);
                        imagemFinal.write((byte)color.getRed());
                        imagemFinal.write((byte)color.getGreen());
                        imagemFinal.write((byte)color.getBlue());
                        imagemFinal.write((byte)0);
                        
                    }
                    //Até aqui tá certo
                    int LarguraAux8bits=0;
                    int r,g,b;
                    int TamanhoLargura = sizeLargura;
                    int LarguraAux24bits = (int) Math.ceil(((double)(TamanhoLargura*3))/4.0); //cada linha deve ter N bits. N divisivel por 4 
                    LarguraAux24bits*=4;
                    for(int i=0;i<sizeAltura;i++){ //B G R
                        LarguraAux8bits = (int) Math.ceil(((double)sizeLargura)/4.0); //cada linha deve ter N bits. N divisivel por 4 
                        
                        LarguraAux8bits*=4;
                        for(int j=0;j<LarguraAux8bits;){
                            if(j<TamanhoLargura){
                                //Cor B
                                j++;
                                b = imagem.read(); //Lê a cor da imagem B
                                //Cor G
                                g = imagem.read(); //Lê a cor da imagem B
                                //Cor R
                                r = imagem.read(); //Lê a cor da imagem B
                                //System.out.println(" R = "+r+" G = "+g+" B = "+b);
                                Color color = new Color(b, g,r);
                                for(int k=0;k<matrizFrequencia.getDivisoes().size();k++){
                                    if((b>=matrizFrequencia.getDivisoes().get(k).getInicioB()) && (b<=matrizFrequencia.getDivisoes().get(k).getFimB())){
                                        if((g>=matrizFrequencia.getDivisoes().get(k).getInicioG()) && (g<=matrizFrequencia.getDivisoes().get(k).getFimG())){
                                            if((r>=matrizFrequencia.getDivisoes().get(k).getInicioR()) && (r<=matrizFrequencia.getDivisoes().get(k).getFimR())){
                                                b = matrizFrequencia.getDivisoes().get(k).getCorB();
                                                g = matrizFrequencia.getDivisoes().get(k).getCorG();
                                                r = matrizFrequencia.getDivisoes().get(k).getCorR();
                                                color = new Color(b, g, r); //Talvez aqui ta ao contrário
                                                break;
                                            }
                                        }
                                    }
                                }
                                
                                imagemFinal.write((byte)matrizFrequencia.PaletaCores.indexOf(color));
                                //System.out.println(""+(cont++));
                            }
                            else{
                                imagem.skip((LarguraAux24bits-(TamanhoLargura*3)));
                                for(int k=0;k<LarguraAux8bits-(TamanhoLargura);k++){
                                    imagemFinal.write((byte)0);
                                }
                                break;
                            }
                        }
                    }
                    
                    
                    
                    //matrizFrequencia = new Quantizador();
                    file=fileaux;
                    
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    exibirImagem();
                    alert.setTitle("Quantizador");
                    alert.setHeaderText("Imagem Quantizada");
                    //alert.setContentText("I have a great message for you!");
                    alert.showAndWait();
                    imagemFinal.close();
                    fileaux.deleteOnExit();
                    
                    

            } catch (IOException ex) {
                Logger.getLogger(
                    FXMLDocumentController.class.getName()).log(
                        Level.SEVERE, null, ex
                    );
            }
            
        }
    }
    void criaHistograma() {
        try {
            FileInputStream imagem = new FileInputStream(file);
            
                int aux=imagem.read(); //Le o primeiro Byte = B
                aux=imagem.read();//Le o segundo Byte = M
                String Size; //Vai conter o tamanho em hexadecimal
                Size = Integer.toHexString(imagem.read());
                Size = Integer.toHexString(imagem.read())+Size;
                Size = Integer.toHexString(imagem.read())+Size;
                Size = Integer.toHexString(imagem.read())+Size;
                int Tamanho = Integer.parseInt(Size,16); //Tamanho do arquivo
                imagem.read(); //pula bits da palavra reservado
                imagem.read();
                imagem.read();
                imagem.read();
                String OffSetString; //Vai conter o tamanho em hexadecimal
                OffSetString = Integer.toHexString(imagem.read());
                OffSetString = Integer.toHexString(imagem.read())+OffSetString;
                OffSetString = Integer.toHexString(imagem.read())+OffSetString;
                OffSetString = Integer.toHexString(imagem.read())+OffSetString;
                int Offset = Integer.parseInt(OffSetString,16); //Tamanho do arquivo
                
                //Tamanho do cabeçalho
                String TamanhoCabecalho; //Vai conter o tamanho em hexadecimal
                TamanhoCabecalho = Integer.toHexString(imagem.read());
                TamanhoCabecalho = Integer.toHexString(imagem.read())+TamanhoCabecalho;
                TamanhoCabecalho = Integer.toHexString(imagem.read())+TamanhoCabecalho;
                TamanhoCabecalho = Integer.toHexString(imagem.read())+TamanhoCabecalho;
                int SizeCabecalho = Integer.parseInt(TamanhoCabecalho,16); //Tamanho do arquivo
                
                //Tamanho da Largura da imagem
                String LarguraString; //Vai conter o tamanho em hexadecimal
                LarguraString = Integer.toHexString(imagem.read());
                LarguraString = Integer.toHexString(imagem.read())+LarguraString;
                LarguraString = Integer.toHexString(imagem.read())+LarguraString;
                LarguraString = Integer.toHexString(imagem.read())+LarguraString;
                sizeLargura = Integer.parseInt(LarguraString,16); //Tamanho do arquivo
                
                //Tamanho da Altura da imagem
                String AlturaString; //Vai conter o tamanho em hexadecimal
                AlturaString = Integer.toHexString(imagem.read());
                AlturaString = Integer.toHexString(imagem.read())+AlturaString;
                AlturaString = Integer.toHexString(imagem.read())+AlturaString;
                AlturaString = Integer.toHexString(imagem.read())+AlturaString;
                sizeAltura = Integer.parseInt(AlturaString,16); //Tamanho do arquivo
                
                //Numero de planos
                imagem.read();
                imagem.read();
                
                //Qtd de bits por pixel
                String QtdString; //Vai conter o tamanho em hexadecimal
                QtdString = Integer.toHexString(imagem.read());
                QtdString = Integer.toHexString(imagem.read())+QtdString;
                BitCount = Integer.parseInt(QtdString,16); //Tamanho do arquivo
                
                if(BitCount!=24){
                    String aviso = "A imagem inserida não está em 24 bits por pixel"; 
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setResizable(false);
                    alert.setTitle("Aviso");
                    alert.setHeaderText("Importante");
                    alert.setContentText(aviso);
                    alert.showAndWait();
                    return;
                }
                
                //Compressão
                String CompressionString; //Vai conter o tamanho em hexadecimal
                CompressionString = Integer.toHexString(imagem.read());
                CompressionString = Integer.toHexString(imagem.read())+CompressionString;
                CompressionString = Integer.toHexString(imagem.read())+CompressionString;
                CompressionString = Integer.toHexString(imagem.read())+CompressionString;
                int Compression = Integer.parseInt(CompressionString,16); //Tamanho do arquivo
                
                //Tamanho da imagem
                String SizeImgString; //Vai conter o tamanho em hexadecimal
                SizeImgString = Integer.toHexString(imagem.read());
                SizeImgString = Integer.toHexString(imagem.read())+SizeImgString;
                SizeImgString = Integer.toHexString(imagem.read())+SizeImgString;
                SizeImgString = Integer.toHexString(imagem.read())+SizeImgString;
                int SizeImg = Integer.parseInt(SizeImgString,16); //Tamanho do arquivo
                
                //Resolucao X em pixels por metro
                String XPPmeterString; //Vai conter o tamanho em hexadecimal
                XPPmeterString = Integer.toHexString(imagem.read());
                XPPmeterString = Integer.toHexString(imagem.read())+XPPmeterString;
                XPPmeterString = Integer.toHexString(imagem.read())+XPPmeterString;
                XPPmeterString = Integer.toHexString(imagem.read())+XPPmeterString;
                int XPPMeter = Integer.parseInt(XPPmeterString,16); //Tamanho do arquivo
                
                //Resolucao Y em pixels por metro
                String YPPmeterString; //Vai conter o tamanho em hexadecimal
                YPPmeterString = Integer.toHexString(imagem.read());
                YPPmeterString = Integer.toHexString(imagem.read())+YPPmeterString;
                YPPmeterString = Integer.toHexString(imagem.read())+YPPmeterString;
                YPPmeterString = Integer.toHexString(imagem.read())+YPPmeterString;
                int YPPMeter = Integer.parseInt(YPPmeterString,16); //Tamanho do arquivo
                
                //Numero de cores de cores usadas
                String CoresUsadasString; //Vai conter o tamanho em hexadecimal
                CoresUsadasString = Integer.toHexString(imagem.read());
                CoresUsadasString = Integer.toHexString(imagem.read())+CoresUsadasString;
                CoresUsadasString = Integer.toHexString(imagem.read())+CoresUsadasString;
                CoresUsadasString = Integer.toHexString(imagem.read())+CoresUsadasString;
                int CoresUsadas = Integer.parseInt(CoresUsadasString,16); //Tamanho do arquivo
                
                //Numero de cores de cores importantes
                String CoresImportantesString; //Vai conter o tamanho em hexadecimal
                CoresImportantesString = Integer.toHexString(imagem.read());
                CoresImportantesString = Integer.toHexString(imagem.read())+CoresImportantesString;
                CoresImportantesString = Integer.toHexString(imagem.read())+CoresImportantesString;
                CoresImportantesString = Integer.toHexString(imagem.read())+CoresImportantesString;
                int CoresImportantes = Integer.parseInt(CoresImportantesString,16); //Tamanho do arquivo
                matrizFrequencia = new Quantizador();
                
                int cor=0;
                int cont=0;
                int LarguraAux=0;
                int r,g,b;
                int TamanhoLargura = sizeLargura*3;
                for(int i=0;i<sizeAltura;i++){ //B G R
                    LarguraAux = (int) Math.ceil(((double)TamanhoLargura)/4.0); //cada linha deve ter N bits. N divisivel por 4 
                    LarguraAux*=4;
                    for(int j=0;j<LarguraAux;){
                        if(j<TamanhoLargura){
                            //Cor B
                            b = imagem.read(); //Lê a cor da imagem G
                            j++;
                            //Cor G
                            g = imagem.read(); //Lê a cor da imagem B
                            j++;
                            //Cor R
                            r = imagem.read(); //Lê a cor da imagem R
                            j++;
                            matrizFrequencia.incrementaMatriz(b, g, r);
                        }
                        else{
                            imagem.skip((LarguraAux-(TamanhoLargura)));
                            break;
                        }
                    }
                }
                tamanhoDados=(int) Math.ceil((double)sizeLargura/4);
                tamanhoDados*=4;
                tamanhoDados*=sizeAltura;
        } catch (IOException ex) {
            Logger.getLogger(
                FXMLDocumentController.class.getName()).log(
                    Level.SEVERE, null, ex
                );
        }
    }    
    
}
