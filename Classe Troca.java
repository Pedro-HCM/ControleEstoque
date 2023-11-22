package atividade;
import java.util.Date;

public class Troca {
    private String novoNomeProduto;
    private String codigoBarras;
    private int novaQuantidade;
    private double novoValor;
    private Date dataTroca;
    private String vendedor;
    private int notaTroca;


        public Troca(String novoNomeProduto, String codigoBarras, int novaQuantidade, double novoValor, Date dataTroca, String vendedor, int notaTroca) {
            this.novoNomeProduto = novoNomeProduto;
            this.codigoBarras = codigoBarras;
            this.novaQuantidade = novaQuantidade;
            this.novoValor = novoValor;
            this.dataTroca = dataTroca;
            this.vendedor = vendedor;
            this.notaTroca = notaTroca;
        }

        // Getters e setters para os atributos de troca
        public String getNovoNomeProduto() {
            return novoNomeProduto;
        }

        public void setNovoNomeProduto(String novoNomeProduto) {
            this.novoNomeProduto = novoNomeProduto;
        }

        public String getCodigoBarras() {
            return codigoBarras;
        }

        public void setCodigoBarras(String codigoBarras) {
            this.codigoBarras = codigoBarras;
        }

        public int getNovaQuantidade() {
            return novaQuantidade;
        }

        public void setNovaQuantidade(int novaQuantidade) {
            this.novaQuantidade = novaQuantidade;
        }

        public double getNovoValor() {
            return novoValor;
        }

        public void setNovoValor(double novoValor) {
            this.novoValor = novoValor;
        }

        public Date getDataTroca() {
            return dataTroca;
        }

        public void setDataTroca(Date dataTroca) {
            this.dataTroca = dataTroca;
        }

        public String getVendedor() {
            return vendedor;
        }

        public void setVendedor(String vendedor) {
            this.vendedor = vendedor;
        }

        public int getNotaTroca() {
            return notaTroca;
        }

        public void setNotaTroca(int notaTroca) {
            this.notaTroca = notaTroca;
        }
    
}
