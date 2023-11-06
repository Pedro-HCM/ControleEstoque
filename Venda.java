package atividade;

import java.util.Calendar;
import java.util.Date;

public class Venda {
    

    public String produto; // Em vez de nomeProduto
    public String codigo_de_barras; // Em vez de codigoBarras
    public int quantidade;
    public double valor_venda; // Em vez de valorVenda
    public Date data_da_venda; // Em vez de dataVenda
    public String nome_vendedor;

    public Venda(String codigo_de_barras, String produto, int quantidade, double valor_venda, Date data_da_venda, String nome_vendedor) {
        this.produto = produto;
        this.codigo_de_barras = codigo_de_barras;
        this.quantidade = quantidade;
        this.valor_venda = valor_venda;
        this.data_da_venda = data_da_venda;
        this.nome_vendedor = nome_vendedor;
    }

    public Venda() {
		// TODO Auto-generated constructor stub
	}

	// Getters e Setters para cada variável, se necessário
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

    
}
