package atividade;
import java.util.Calendar;
import java.util.Date;

public class Venda {

    public String produto;
    public String codigo_de_barras;
    public int quantidade;
    public double valor_venda;
    public Date data_da_venda;
    public String nome_vendedor;
    private int numeroNotaFiscal;

    // Construtor padrão sem argumentos
    public Venda() {
    }

    // Construtor com argumentos
    public Venda(String codigo_de_barras, String produto, int quantidade, double valor_venda, Date data_da_venda, String nome_vendedor) {
        this.produto = produto;
        this.codigo_de_barras = codigo_de_barras;
        this.quantidade = quantidade;
        this.valor_venda = valor_venda;
        this.data_da_venda = data_da_venda;
        this.nome_vendedor = nome_vendedor;
    }

    public String getNomeProduto() {
        return produto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.produto = nomeProduto;
    }

    public String getCodigoBarras() {
        return codigo_de_barras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigo_de_barras = codigoBarras;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getValorVenda() {
        return valor_venda;
    }

    public void setValorVenda(double valorVenda) {
        this.valor_venda = valorVenda;
    }

    public Date getDataVenda() {
        return data_da_venda;
    }

    public void setDataVenda(Date dataVenda) {
        this.data_da_venda = dataVenda;
    }

    public String getNomeVendedor() {
        return nome_vendedor;
    }

    public void setNomeVendedor(String nomeVendedor) {
        this.nome_vendedor = nomeVendedor;
    }

    public Calendar getDataDevolucao() {
        return null;
    }

    public int getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(int numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }
}
