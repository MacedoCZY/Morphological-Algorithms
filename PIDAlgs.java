import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PIDAlgs {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Args imagem");
            return;
        }

        String caminhoImagem = args[0];
        
        try {
            File arquivoImagem = new File(caminhoImagem);
            BufferedImage imagemOriginal = ImageIO.read(arquivoImagem);
            
            BufferedImage imagemBinaria = binarizar(imagemOriginal);
            
            // matriz para o kernel 
            int[][] kernel = {
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
            };
            
            BufferedImage imagemDilatada = dilatar(imagemBinaria, kernel);
            BufferedImage imagemErosionada = erosionar(imagemBinaria, kernel);
            BufferedImage imagemAbertura = abertura(imagemBinaria, kernel);
            BufferedImage imagemFechamento = fechamento(imagemBinaria, kernel);
            
            ImageIO.write(imagemDilatada, "png", new File("imagem_dilatada.png"));
            ImageIO.write(imagemErosionada, "png", new File("imagem_erosionada.png"));
            ImageIO.write(imagemAbertura, "png", new File("imagem_abertura.png"));
            ImageIO.write(imagemFechamento, "png", new File("imagem_fechamento.png"));
            
            System.out.println("Processo finalizado");
        } catch (IOException e) {
            System.out.println("Erro : " + e.getMessage());
        }
    }

    public static BufferedImage binarizar(BufferedImage imagem) {
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();
        BufferedImage imagemBinaria = new BufferedImage(largura, altura, BufferedImage.TYPE_BYTE_BINARY);
        
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                int cor = imagem.getRGB(x, y);
                int r = (cor >> 16) & 0xFF;
                int g = (cor >> 8) & 0xFF;
                int b = cor & 0xFF;
                
                // para cinza
                int cinza = (int) (0.3 * r + 0.59 * g + 0.11 * b);
                
                // limite 128
                int valorPixel = (cinza < 128) ? 0 : 255;
                
                Color corPixel = new Color(valorPixel, valorPixel, valorPixel);
                imagemBinaria.setRGB(x, y, corPixel.getRGB());
            }
        }
        return imagemBinaria;
    }

    public static BufferedImage dilatar(BufferedImage imagemBinaria, int[][] kernel) {
        int largura = imagemBinaria.getWidth();
        int altura = imagemBinaria.getHeight();
        BufferedImage resultado = new BufferedImage(largura, altura, BufferedImage.TYPE_BYTE_BINARY);
        
        int kernelLargura = kernel.length;
        int kernelAltura = kernel[0].length;
        int deslocamentoX = kernelLargura / 2;
        int deslocamentoY = kernelAltura / 2;
        
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                boolean dilatacao = false;
                
                for (int ky = 0; ky < kernelAltura; ky++) {
                    for (int kx = 0; kx < kernelLargura; kx++) {
                        int posX = x + kx - deslocamentoX;
                        int posY = y + ky - deslocamentoY;
                        if (posX >= 0 && posY >= 0 && posX < largura && posY < altura) {
                            if (imagemBinaria.getRGB(posX, posY) == Color.BLACK.getRGB() && kernel[kx][ky] == 1) {
                                dilatacao = true;
                            }
                        }
                    }
                }
                resultado.setRGB(x, y, dilatacao ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return resultado;
    }

    public static BufferedImage erosionar(BufferedImage imagemBinaria, int[][] kernel) {
        int largura = imagemBinaria.getWidth();
        int altura = imagemBinaria.getHeight();
        BufferedImage resultado = new BufferedImage(largura, altura, BufferedImage.TYPE_BYTE_BINARY);
        
        int kernelLargura = kernel.length;
        int kernelAltura = kernel[0].length;
        int deslocamentoX = kernelLargura / 2;
        int deslocamentoY = kernelAltura / 2;
        
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                boolean erosao = true;
                
                for (int ky = 0; ky < kernelAltura; ky++) {
                    for (int kx = 0; kx < kernelLargura; kx++) {
                        int posX = x + kx - deslocamentoX;
                        int posY = y + ky - deslocamentoY;
                        if (posX >= 0 && posY >= 0 && posX < largura && posY < altura) {
                            if (imagemBinaria.getRGB(posX, posY) != Color.BLACK.getRGB() && kernel[kx][ky] == 1) {
                                erosao = false;
                            }
                        }
                    }
                }
                resultado.setRGB(x, y, erosao ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return resultado;
    }

    // erosão seguida de dilatação - abertura
    public static BufferedImage abertura(BufferedImage imagemBinaria, int[][] kernel) {
        BufferedImage imagemErosao = erosionar(imagemBinaria, kernel);
        return dilatar(imagemErosao, kernel);
    }

    // dilatação seguida de erosão - fechamento
    public static BufferedImage fechamento(BufferedImage imagemBinaria, int[][] kernel) {
        BufferedImage imagemDilatacao = dilatar(imagemBinaria, kernel);
        return erosionar(imagemDilatacao, kernel);
    }
}
