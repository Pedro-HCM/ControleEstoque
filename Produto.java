package estoque_management;
import java.util.Date;

public class Produto {
   private String nome;
   private String codigoDeBarras;
   private int quantidade;
   private double precoUnitario;
   private Date dataDeValidade;

   public Produto(String nome, String codigoDeBarras, int quantidade, double precoUnitario, Date dataDeValidade) {
       this.nome = nome;
       this.codigoDeBarras = codigoDeBarras;
       this.quantidade = quantidade;
       this.precoUnitario = precoUnitario;
       this.dataDeValidade = dataDeValidade;
   }

   public String getCodigoDeBarras() {
       return codigoDeBarras;
   }

   public String getNome() {
       return nome;
   }

   public Date getDataDeValidade() {
       return dataDeValidade;
   }

   public int getQuantidade() {
       return quantidade;
   }

   public double getPrecoUnitario() {
       return precoUnitario;
   }

   public void setQuantidade(int novaQuantidade) {
       this.quantidade = novaQuantidade;
   }

   public void setDataDeValidade(Date novaDataValidade) {
       this.dataDeValidade = novaDataValidade;
   }

   public void setNome(String novoNome) {
       this.nome = novoNome;
   }

   public void setCodigoDeBarras(String novoCodigoDeBarras) {
       this.codigoDeBarras = novoCodigoDeBarras;
   }

   public void setPrecoUnitario(double novoPrecoUnitario) {
       this.precoUnitario = novoPrecoUnitario;
   }
}
