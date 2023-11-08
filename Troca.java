package atividade;
import java.util.Date;

public class Troca {
    private String novonomeProduto;
    private String codigoBarras;
    private int novaquantidade;
    private double novovalor;
    private Date dataTroca;
    private String vendedor;
    private int nota_troca;

    public Troca(String novonomeProduto, String codigoBarras, int novaquantidade, double novovalor, Date dataTroca, String vendedor, int nota_troca) {
        this.novonomeProduto = novonomeProduto;
        this.codigoBarras = codigoBarras;
        this.novaquantidade = novaquantidade;
        this.novovalor = novovalor;
        this.dataTroca = dataTroca;
        this.vendedor = vendedor;
        this.nota_troca = nota_troca;
    }

    // Getters e setters para os atributos de troca
    public String getNovoNomeProduto() {
        return novonomeProduto;
    }

    public void setNovoNomeProduto(String nomeProduto) {
        this.novonomeProduto = nomeProduto;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public int getNovaQuantidade() {
        return novaquantidade;
    }

    public void setNovaQuantidade(int quantidade) {
        this.novaquantidade = quantidade;
    }

    public double getValorTroca() {
        return novovalor;
    }

    public void setValorTroca(double valorTroca) {
        this.novovalor = valorTroca;
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
        return nota_troca;
    }

    public void setNotaTroca(int notaTroca) {
        this.nota_troca = notaTroca;
    }
}
