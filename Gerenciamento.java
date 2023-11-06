package estoque_management;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Gerenciamento {
	
	private List<Produto> estoque = new ArrayList<>();

	public static void main(String[] args) throws ParseException {
		bancodedados banco = new bancodedados();
		banco.conectar();
	    Gerenciamento gerenciador = new Gerenciamento();
	    Scanner scanner = new Scanner(System.in);
	   
  
	    int escolha = 0; 
	    do {
	    	System.out.println("Menu:");
	    	System.out.println("1. Adicionar Produto");
	    	System.out.println("2. Atualizar Produto");
	    	System.out.println("3. Remover Produto");
	    	System.out.println("4. Procurar Produto");
	    	System.out.println("5. Calcular Valor Total do Estoque");
	    	System.out.println("6. Gerar Relatório de Produtos Próximos ao Vencimento");
	    	System.out.println("7. Realizar Venda");
	    	System.out.println("8. Devolução ou troca");
	    	System.out.println("9. Consultar venda por NF");
	    	System.out.println("0. Sair");
	    	System.out.print("Escolha uma opção: ");


	      
	        if (scanner.hasNextInt()) {
	            escolha = scanner.nextInt();
	            scanner.nextLine(); 
	        } else {
	            System.out.println("Opção inválida. Digite um número válido.");
	            scanner.nextLine(); 
	            continue;
	        }

	 
	        switch (escolha) {
	        case 1:
	            gerenciador.adicionarProduto();
	            break;
	        case 2:
	            gerenciador.atualizarProduto();
	            break;
	        case 3:
	            gerenciador.removerProduto();
	            break;
	        case 4:
	            gerenciador.procurarProduto();
	            break;
	        case 5:
	            gerenciador.calcularValorTotal();
	            break;
	        case 6:
	            gerenciador.gerarRelatorioVencimento();
	            break;
	        case 7:
	            gerenciador.RealizarVenda();
	            break;
	        case 9:
	        	gerenciador.ConsultarVenda();
                break;
    
	        case 0:
	            System.out.println("Saindo do sistema.");
	            break;
	        default:
	            System.out.println("Opção inválida. Tente novamente.");
	    }

	    } while (escolha != 8);

	    scanner.close();
	    }
	public void adicionarProduto() {
	    Scanner scanner = new Scanner(System.in);

	    System.out.print("Nome do produto: ");
	    String nome = scanner.nextLine();

	    String codigoDeBarras = null;
	    boolean codigoUnico = false;

	    while (!codigoUnico) {
	        System.out.print("Código de barras: ");
	        codigoDeBarras = scanner.nextLine();

	        boolean codigoRepetido = false;
	        for (Produto produto : estoque) {
	            if (produto.getCodigoDeBarras().equals(codigoDeBarras)) {
	                codigoRepetido = true;
	                break;
	            }
	        }

	        if (!codigoRepetido) {
	            codigoUnico = true;
	        } else {
	            System.out.println("Código de barras já existe. Tente novamente.");
	        }
	    }

	    int quantidade = 0;
	    boolean quantidadeValida = false;

	    while (!quantidadeValida) {
	        System.out.print("Quantidade (apenas números): ");
	        String quantidadeStr = scanner.nextLine();

	        try {
	            quantidade = Integer.parseInt(quantidadeStr);
	            quantidadeValida = true;
	        } catch (NumberFormatException e) {
	            System.out.println("A quantidade deve ser um número válido. Tente novamente.");
	        }
	    }

	    double precoUnitario = 0.0;
	    boolean precoValido = false;

	    while (!precoValido) {
	        System.out.print("Preço unitário: ");
	        String precoStr = scanner.nextLine();

	        try {
	            precoUnitario = Double.parseDouble(precoStr);
	            precoValido = true;
	        } catch (NumberFormatException e) {
	            System.out.print("O preço unitário deve ser um número válido. Tente novamente.");
	        }
	    }

	    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyyMMdd");
	    Date dataDeValidade = null;
	    boolean dataValida = false;

	    while (!dataValida) {
	        System.out.print("Data de validade (yyyy-MM-dd): ");
	        String dataValidadeStr = scanner.nextLine();

	        try {
	            dataDeValidade = inputDateFormat.parse(dataValidadeStr);
	            dataValida = true;
	        } catch (ParseException e) {
	            System.out.println("Formato de data inválido. Use o formato yyyy-MM-dd.");
	        }
	    }

	    String dataDeValidadeSemHifens = outputDateFormat.format(dataDeValidade);

	    Produto novoProduto = new Produto(nome, codigoDeBarras, quantidade, precoUnitario, dataDeValidade);
	    estoque.add(novoProduto);
	    bancodedados banco = new bancodedados();
	    banco.conectar();
	    banco.inserirProduto(novoProduto);

	    System.out.println("Produto adicionado com sucesso!");
	}



	public void atualizarProduto() {
	    Scanner scanner = new Scanner(System.in);
	    bancodedados banco = new bancodedados();
	    banco.conectar();

	    try {
	        banco.setAutoCommit(true);

	        System.out.print("Opções de pesquisa:\n1. Por nome\n2. Por código de barras\n3. Por data de validade\nEscolha a opção: ");
	        int opcao = scanner.nextInt();
	        scanner.nextLine();

	        String termoPesquisa;
	        String opcaoString = null;

	        switch (opcao) {
	            case 1:
	                opcaoString = "nome";
	                System.out.print("Digite o nome do produto: ");
	                termoPesquisa = scanner.nextLine();
	                break;
	            case 2:
	                opcaoString = "codigoBarras";
	                System.out.print("Digite o código de barras do produto: ");
	                termoPesquisa = scanner.nextLine();
	                break;
	            case 3:
	                opcaoString = "dataValidade";
	                System.out.print("Digite a data de validade (yyyy-MM-dd): ");
	                termoPesquisa = scanner.nextLine();
	                break;
	            default:
	                System.out.println("Opção inválida.");
	                return;
	        }

	        List<Produto> produtosEncontrados = banco.procurarProduto(opcaoString, termoPesquisa);

	        if (produtosEncontrados.isEmpty()) {
	            System.out.println("Nenhum produto encontrado.");
	        } else {
	            System.out.println("Produtos encontrados:");
	            for (int i = 0; i < produtosEncontrados.size(); i++) {
	                Produto produto = produtosEncontrados.get(i);
	                System.out.println(i + 1 + ". Nome: " + produto.getNome());
	               
	            }

	            System.out.print("Digite o número do produto que deseja alterar ou 0 para cancelar: ");
	            int escolha = scanner.nextInt();
	            scanner.nextLine();

	            if (escolha > 0 && escolha <= produtosEncontrados.size()) {
	                Produto produtoParaAlterar = produtosEncontrados.get(escolha - 1);
	                System.out.print("Você tem certeza de que deseja alterar este produto? (s/n): ");
	                String confirmacao = scanner.nextLine();
	                if (confirmacao.equalsIgnoreCase("s")) {
	                   

	                    System.out.print("Digite o novo nome (ou pressione Tab para manter o atual): ");
	                    String novoNome = scanner.nextLine();
	                    if (!novoNome.isEmpty() && !novoNome.equals("\t")) {
	                        produtoParaAlterar.setNome(novoNome);
	                    }

	  
	                    System.out.print("Digite o novo código de barras (ou pressione Tab para manter o atual): ");
	                    String novoCodigoBarras = scanner.nextLine();
	                    if (!novoCodigoBarras.isEmpty()) {
	                        if (banco.verificarCodigoBarrasExistente(novoCodigoBarras)) {
	                            System.out.println("Código de barras já existe no banco de dados. A atualização foi cancelada.");
	                        } else {
	                            produtoParaAlterar.setCodigoDeBarras(novoCodigoBarras);
	                        }
	                    }

	                    System.out.print("Digite a nova quantidade (ou pressione Enter para manter a atual): ");
	                    String novaQuantidadeStr = scanner.nextLine();
	                    if (!novaQuantidadeStr.isEmpty()) {
	                        try {
	                            int novaQuantidade = Integer.parseInt(novaQuantidadeStr);
	                            produtoParaAlterar.setQuantidade(novaQuantidade);
	                        } catch (NumberFormatException e) {
	                            System.out.println("Quantidade inválida. A quantidade não foi alterada.");
	                        }
	                    }

	                    System.out.print("Digite o novo valor unitário (ou pressione Enter para manter o atual): ");
	                    String novoValorUnitarioStr = scanner.nextLine();
	                    if (!novoValorUnitarioStr.isEmpty()) {
	                        try {
	                            double novoValorUnitario = Double.parseDouble(novoValorUnitarioStr);
	                            produtoParaAlterar.setPrecoUnitario(novoValorUnitario);
	                        } catch (NumberFormatException e) {
	                            System.out.println("Valor unitário inválido. O valor unitário não foi alterado.");
	                        }
	                    }

	                    System.out.print("Digite a nova data de validade (yyyy-MM-dd) (ou pressione Enter para manter a atual): ");
	                    String novaDataValidadeStr = scanner.nextLine();
	                    if (!novaDataValidadeStr.isEmpty()) {
	                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	                        try {
	                            Date novaDataValidade = dateFormat.parse(novaDataValidadeStr);
	                            produtoParaAlterar.setDataDeValidade(novaDataValidade);
	                        } catch (ParseException e) {
	                            System.out.println("Formato de data inválido. A data de validade não foi alterada.");
	                        }
	                    }

	                    try {
	                        banco.atualizarProduto(produtoParaAlterar);
	                        banco.commit(); 
	                        System.out.println("Produto alterado com sucesso!");
	                    } catch (SQLException e) {
	                        banco.rollback(); 
	                    }
	                } else if (confirmacao.equalsIgnoreCase("n")) {
	                    System.out.println("Alteração cancelada.");
	                } else {
	                    System.out.println("Resposta inválida. Operação de alteração cancelada.");
	                }
	            } else {
	                System.out.println("Operação de alteração cancelada.");
	            }
	        }
	    } catch (SQLException e) {
	        banco.rollback();  
	        System.err.println("Erro ao procurar o produto no banco de dados: " + e.getMessage());
	    } finally {
	        banco.setAutoCommit(true);}
	}
	

	public void removerProduto() {
	    Scanner scanner = new Scanner(System.in);
	    bancodedados banco = new bancodedados();
	    banco.conectar();

	    System.out.print("Opções de pesquisa:\n1. Por nome\n2. Por código de barras\n3. Por data de validade\nEscolha a opção: ");
	    int opcao = scanner.nextInt();
	    scanner.nextLine();

	    String termoPesquisa;
	    String opcaoString = null;

	    try {
	        switch (opcao) {
	            case 1:
	                opcaoString = "nome";
	                System.out.print("Digite o nome do produto: ");
	                termoPesquisa = scanner.nextLine();
	                break;
	            case 2:
	                opcaoString = "codigoBarras";
	                System.out.print("Digite o código de barras do produto: ");
	                termoPesquisa = scanner.nextLine();
	                break;
	            case 3:
	                opcaoString = "dataValidade";
	                System.out.print("Digite a data de validade (yyyy-MM-dd): ");
	                termoPesquisa = scanner.nextLine();
	                break;
	            default:
	                System.out.println("Opção inválida.");
	                return;
	        }

	        List<Produto> produtosEncontrados = banco.procurarProduto(opcaoString, termoPesquisa);

	        if (produtosEncontrados.isEmpty()) {
	            System.out.println("Nenhum produto encontrado.");
	        } else {
	            System.out.println("Produtos encontrados:");
	            for (int i = 0; i < produtosEncontrados.size(); i++) {
	                Produto produto = produtosEncontrados.get(i);
	                System.out.println(i + 1 + ". Nome: " + produto.getNome());
	              
	            }

	            System.out.print("Digite o número do produto que deseja excluir ou 0 para cancelar: ");
	            int escolha = scanner.nextInt();
	            scanner.nextLine();

	            if (escolha > 0 && escolha <= produtosEncontrados.size()) {
	                Produto produtoParaExcluir = produtosEncontrados.get(escolha - 1);
	                System.out.print("Você tem certeza de que deseja excluir este produto? (s/n): ");
	                String confirmacao = scanner.nextLine();
	                if (confirmacao.equalsIgnoreCase("s")) {
	                    banco.excluirProduto(produtoParaExcluir);
	                    System.out.println("Produto removido com sucesso!");
	                } else if (confirmacao.equalsIgnoreCase("n")) {
	                    System.out.println("Exclusão cancelada.");
	                } else {
	                    System.out.println("Resposta inválida. Operação de exclusão cancelada.");
	                }
	            } else {
	                System.out.println("Operação de exclusão cancelada.");
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Erro ao procurar o produto no banco de dados: " + e.getMessage());
	    }
	}
	public void procurarProduto() {
	    Scanner scanner = new Scanner(System.in);
	    bancodedados banco = new bancodedados();
	    banco.conectar();

	    System.out.print("Opções de pesquisa:\n1. Por nome\n2. Por código de barras\n3. Por data de validade\nEscolha a opção: ");
	    int opcao = scanner.nextInt();
	    scanner.nextLine();

	    String termoPesquisa;
	    String opcaoString = null;

	    try {
	        switch (opcao) {
	            case 1:
	                opcaoString = "nome";
	                System.out.print("Digite o nome do produto: ");
	                termoPesquisa = scanner.nextLine();
	                break;
	            case 2:
	                opcaoString = "codigoBarras";
	                System.out.print("Digite o código de barras do produto: ");
	                termoPesquisa = scanner.nextLine();
	                break;
	            case 3:
	                opcaoString = "dataValidade";
	                System.out.print("Digite a data de validade (yyyy-MM-dd): ");
	                termoPesquisa = scanner.nextLine();
	                break;
	            default:
	                System.out.println("Opção inválida.");
	                return;
	        }

	        List<Produto> produtosEncontrados = banco.procurarProduto(opcaoString, termoPesquisa);

	        if (produtosEncontrados.isEmpty()) {
	            System.out.println("Nenhum produto encontrado.");
	        } else {
	            System.out.println("Produtos encontrados:");
	            for (int i = 0; i < produtosEncontrados.size(); i++) {
	                Produto produto = produtosEncontrados.get(i);
	                System.out.println(i + 1 + ". Nome: " + produto.getNome());
	                System.out.println("   Código de Barras: " + produto.getCodigoDeBarras());
	                System.out.println("   Quantidade: " + produto.getQuantidade());
	                System.out.println("   Preço Unitário: " + produto.getPrecoUnitario());
	                System.out.println("   Data de Validade: " + produto.getDataDeValidade());
	               
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Erro ao procurar o produto no banco de dados: " + e.getMessage());
	    }
	}


	

	public void calcularValorTotal() {
	    try {
	        bancodedados banco = new bancodedados();
	        banco.conectar();
	        double valorTotal = banco.calcularValorTotalEstoque();
	        System.out.println("Valor total do estoque: R$" + valorTotal);
	    } catch (SQLException e) {
	        System.err.println("Erro ao calcular o valor total do estoque: " + e.getMessage());
	    }
	}

	public void gerarRelatorioVencimento() {
	    try {
	        bancodedados banco = new bancodedados();
	        banco.conectar();
	        List<Produto> produtosProximosVencimento = banco.gerarRelatorioVencimento();

	        if (produtosProximosVencimento.isEmpty()) {
	            System.out.println("Nenhum produto próximo ao vencimento encontrado.");
	        } else {
	            System.out.println("Produtos próximos ao vencimento:");
	            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");

	            for (Produto produto : produtosProximosVencimento) {
	                System.out.println("Nome: " + produto.getNome() + ", Data de Validade: " + dateFormat.format(produto.getDataDeValidade()));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Erro ao gerar relatório de produtos próximos ao vencimento: " + e.getMessage());
	    }
	    
	}
	public void RealizarVenda() {
	    Scanner scanner = new Scanner(System.in);

	 
	    bancodedados banco = new bancodedados();

	    
	    banco.conectar();

	   
	    Venda venda = new Venda();

	    
	    System.out.print("Nome do Produto: ");
	    venda.setNomeProduto(scanner.nextLine());

	    System.out.print("Código de Barras: ");
	    venda.setCodigoBarras(scanner.nextLine());

	    System.out.print("Quantidade: ");
	    venda.setQuantidade(scanner.nextInt());

	    System.out.print("Valor da Venda: ");
	    venda.setValorVenda(scanner.nextDouble());
	    scanner.nextLine(); 

	    System.out.print("Data da Venda (YYYY-MM-DD): ");
	    String data_da_venda_str = scanner.nextLine();
	    Date data_da_venda;

	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    try {
	        data_da_venda = sdf.parse(data_da_venda_str);
	        venda.setDataVenda(data_da_venda);
	    } catch (ParseException e) {
	        e.printStackTrace();
	        System.out.println("Formato de data inválido. Venda cancelada.");
	        scanner.close();
	        return; 
	    }

	    System.out.print("Nome do Vendedor: ");
	    venda.setNomeVendedor(scanner.nextLine());
	  
	    banco.RealizarVenda(venda);

	    System.out.println("Venda realizada com sucesso!");
	}
	
	public void ConsultarVenda() {
		Scanner scanner = new Scanner(System.in);
		bancodedados bancodedados = new bancodedados();
    	bancodedados.conectar();
        System.out.print("Digite o Número da Nota Fiscal: ");
        int numeroNotaFiscal = scanner.nextInt();
      
        Venda vendaEncontrada = bancodedados.consultarVendaPorNumeroNotaFiscal(numeroNotaFiscal);

        if (vendaEncontrada != null) {
        } else {
            System.out.println("Venda não encontrada.");
        }
        
        bancodedados.fecharConexao();
	}
	
}
