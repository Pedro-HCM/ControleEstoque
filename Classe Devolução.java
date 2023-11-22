package atividade;

import java.util.Date;

public class Devolucao {
    private String nome;
    private String codigoBarras;
    private int quantidade;
    private double valorDevolucao;
    private Date dataDevolucao;
    private String vendedor;
    private int notaDevolucao;

    // Construtor padrão
    public Devolucao() {
    }

    // Construtor com todos os campos
    public Devolucao(String nome, String codigoBarras, int quantidade, double valorDevolucao,
                     Date dataDevolucao, String vendedor, int notaDevolucao) {
        this.nome = nome;
        this.codigoBarras = codigoBarras;
        this.quantidade = quantidade;
        this.valorDevolucao = valorDevolucao;
        this.dataDevolucao = dataDevolucao;
        this.vendedor = vendedor;
        this.notaDevolucao = notaDevolucao;
    }

    // Getters e Setters

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getValorDevolucao() {
        return valorDevolucao;
    }

    public void setValorDevolucao(double valorDevolucao) {
        this.valorDevolucao = valorDevolucao;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public int getNotaDevolucao() {
        return notaDevolucao;
    }

    public void setNotaDevolucao(int notaDevolucao) {
        this.notaDevolucao = notaDevolucao;
    }
}
