//Ness arquivo você encontrara as conexões entre os metodos usados no Codigo Main e o Banco de Dados MYSQL.
package atividade;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class bancodedados {
private Connection connection;
    private int proximoNumeroNotaTroca = 1;
    public void conectar() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/estoque2";
        String user = "root";
        String password = "635241";

        try {
            connection = DriverManager.getConnection(jdbcUrl, user, password);
            connection.setAutoCommit(false); 
            System.out.println("Conexão com o banco de dados estabelecida com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    public void commit() {
        try {
            if (connection != null) {
                connection.commit();
               
            }
        } catch (SQLException e) {
            System.err.println("Erro ao confirmar a transação: " + e.getMessage());
        }
    }

    public void rollback() {
        try {
            if (connection != null) {
                connection.rollback();
                System.out.println("Transação desfeita com sucesso.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao desfazer a transação: " + e.getMessage());
        }
        
    }

    public void inserirProduto(Produto produto) {
        try {
          
            String sql = "INSERT INTO Produto (Nome, CodigoDeBarras, PrecoUnitario, Quantidade, DataValidade) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, produto.getNome());
            statement.setString(2, produto.getCodigoDeBarras());
            statement.setDouble(3, produto.getPrecoUnitario());
            statement.setInt(4, produto.getQuantidade());
            statement.setDate(5, new java.sql.Date(produto.getDataDeValidade().getTime()));

           
            statement.executeUpdate();
            System.out.println("Produto inserido com sucesso no banco de dados.");
            commit(); 
        } catch (SQLException e) {
            rollback(); 
            System.err.println("Erro ao inserir o produto no banco de dados: " + e.getMessage());
        }
    }

    public void excluirProduto(Produto produto) {
        try {
           
            String sql = "DELETE FROM Produto WHERE CodigoDeBarras = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, produto.getCodigoDeBarras());

            
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Produto excluído com sucesso do banco de dados.");
                commit(); 
            } else {
                System.out.println("Produto não encontrado no banco de dados. Nenhum produto foi excluído.");
                rollback();  
            }
        } catch (SQLException e) {
            rollback();
            System.err.println("Erro ao excluir o produto do banco de dados: " + e.getMessage());
        }
    }

    public List<Produto> procurarProduto(String opcao, String termoPesquisa) throws SQLException {
        List<Produto> produtosEncontrados = new ArrayList<>();
        String sql = "";
        PreparedStatement statement = null;

        try {
            if ("nome".equalsIgnoreCase(opcao)) {
                sql = "SELECT * FROM Produto WHERE Nome LIKE ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, "%" + termoPesquisa + "%");
            } else if ("codigoBarras".equalsIgnoreCase(opcao)) {
                sql = "SELECT * FROM Produto WHERE CodigoDeBarras = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, termoPesquisa);
            } else if ("dataValidade".equalsIgnoreCase(opcao)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dataPesquisa = dateFormat.parse(termoPesquisa);
                sql = "SELECT * FROM Produto WHERE DataValidade = ?";
                statement = connection.prepareStatement(sql);
                statement.setDate(1, new java.sql.Date(dataPesquisa.getTime()));
            } else {
                System.out.println("Opção de pesquisa inválida.");
                return produtosEncontrados;
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String nome = resultSet.getString("Nome");
                String codigoDeBarras = resultSet.getString("CodigoDeBarras");
                double precoUnitario = resultSet.getDouble("PrecoUnitario");
                int quantidade = resultSet.getInt("Quantidade");
                Date dataValidade = resultSet.getDate("DataValidade");

                Produto produto = new Produto(nome, codigoDeBarras, quantidade, precoUnitario, dataValidade);
                produtosEncontrados.add(produto);
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }

        return produtosEncontrados;
    }

    public double calcularValorTotalEstoque() throws SQLException {
        double valorTotal = 0.0;
        String sql = "SELECT SUM(PrecoUnitario * Quantidade) AS ValorTotal FROM Produto";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            valorTotal = resultSet.getDouble("ValorTotal");
        }

        statement.close();
        return valorTotal;
    }

    public List<Produto> gerarRelatorioVencimento() throws SQLException {
        List<Produto> produtosProximosVencimento = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date dataAtual = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataAtual);
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date dataFutura = calendar.getTime();

        String sql = "SELECT * FROM Produto WHERE DataValidade <= ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setDate(1, new java.sql.Date(dataFutura.getTime()));
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String nome = resultSet.getString("Nome");
            String codigoDeBarras = resultSet.getString("CodigoDeBarras");
            double precoUnitario = resultSet.getDouble("PrecoUnitario");
            int quantidade = resultSet.getInt("Quantidade");
            Date dataValidade = resultSet.getDate("DataValidade");

            Produto produto = new Produto(nome, codigoDeBarras, quantidade, precoUnitario, dataValidade);
            produtosProximosVencimento.add(produto);
        }

        statement.close();
        return produtosProximosVencimento;
    }

    public void atualizarProduto(Produto produto) throws SQLException {
        String sql = "UPDATE Produto SET Nome = ?, Quantidade = ?, PrecoUnitario = ?, DataValidade = ? WHERE CodigoDeBarras = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, produto.getNome());
        statement.setInt(2, produto.getQuantidade());
        statement.setDouble(3, produto.getPrecoUnitario());
        statement.setDate(4, new java.sql.Date(produto.getDataDeValidade().getTime()));
        statement.setString(5, produto.getCodigoDeBarras());
        statement.executeUpdate();
        commit(); 
    }

    public void fecharConexao() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Conexão com o banco de dados fechada com sucesso.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
        }
    }

	public void setAutoCommit(boolean b) {

		
	}
	public boolean verificarQuantidadeEmEstoque(String codigoDeBarras, int quantidadeDesejada) {
	    try {
	       
	        String sql = "SELECT quantidade FROM Produto WHERE CodigoDeBarras = ?";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setString(1, codigoDeBarras);

	      
	        ResultSet resultSet = statement.executeQuery();

	        if (resultSet.next()) {
	            int quantidadeEmEstoque = resultSet.getInt("quantidade");

	         
	            if (quantidadeEmEstoque >= quantidadeDesejada) {
	                return true;
	            } else {
	                return false; 
	            }
	        } else {
	            return false; 
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; 
	    }
	}
	
	public void RealizarVenda(Venda venda) {
	    try {

	        String consultaUltimoNumeroSQL = "SELECT MAX(numero_nota_fiscal) AS ultimo_numero FROM vendas";
	        PreparedStatement consultaUltimoNumeroStatement = connection.prepareStatement(consultaUltimoNumeroSQL);
	        ResultSet resultadoConsulta = consultaUltimoNumeroStatement.executeQuery();
	        int ultimoNumeroNotaFiscal = 0;
	        String codigoDeBarras = venda.getCodigoBarras();
	        int quantidadeDesejada = venda.getQuantidade();

	        if (resultadoConsulta.next()) {
	            ultimoNumeroNotaFiscal = resultadoConsulta.getInt("ultimo_numero");
	        }
	        int novoNumeroNotaFiscal = ultimoNumeroNotaFiscal + 1;
	        
	        if (verificarQuantidadeEmEstoque(codigoDeBarras, quantidadeDesejada)) {

	        String sql = "INSERT INTO vendas (produto, codigo_de_barras, quantidade, valor_venda, data_da_venda, nome_vendedor, numero_nota_fiscal) VALUES (?, ?, ?, ?, ?, ?, ?)";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        
	        String sqlAtualizarQuantidade = "UPDATE Produto SET Quantidade = Quantidade - ? WHERE CodigoDeBarras = ?";
            PreparedStatement statementAtualizarQuantidade = connection.prepareStatement(sqlAtualizarQuantidade);
            statementAtualizarQuantidade.setInt(1, venda.getQuantidade());
            statementAtualizarQuantidade.setString(2, venda.getCodigoBarras());
            statementAtualizarQuantidade.executeUpdate();
	        
	        statement.setString(1, venda.getNomeProduto());
	        statement.setString(2, venda.getCodigoBarras());
	        statement.setInt(3, venda.getQuantidade());
	        statement.setDouble(4, venda.getValorVenda());
	        statement.setDate(5, new java.sql.Date(venda.getDataVenda().getTime()));
	        statement.setString(6, venda.getNomeVendedor());
	        statement.setInt(7, novoNumeroNotaFiscal);

	        statement.executeUpdate();
	        System.out.println("Venda inserida com sucesso. Número da nota fiscal: " + novoNumeroNotaFiscal);
	        commit();
	        } else {
	            System.out.println("Quantidade insuficiente em estoque para realizar a venda.");
	            rollback();
	        }
	    } catch (SQLException e) {
	        rollback();
	        System.err.println("Erro ao inserir a venda no banco de dados: " + e.getMessage());
	    }
	}

	public Venda consultarVendaPorNumeroNotaFiscal(int numeroNotaFiscal) {
	    Venda vendaEncontrada = null;
	    try {
	        String sql = "SELECT * FROM vendas WHERE numero_nota_fiscal = ?";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setInt(1, numeroNotaFiscal);
	        ResultSet resultSet = statement.executeQuery();

	        if (resultSet.next()) {
	            String codigoDeBarras = resultSet.getString("codigo_de_barras");
	            String produto = resultSet.getString("produto");
	            int quantidade = resultSet.getInt("quantidade");
	            double valorVenda = resultSet.getDouble("valor_venda");
	            Date dataVenda = resultSet.getDate("data_da_venda");
	            String nomeVendedor = resultSet.getString("nome_vendedor");

	            vendaEncontrada = new Venda(codigoDeBarras, produto, quantidade, valorVenda, dataVenda, nomeVendedor);

	            System.out.println("Venda encontrada: ");
	            System.out.println("Produto: " + vendaEncontrada.getNomeProduto());
	            System.out.println("Quantidade: " + vendaEncontrada.getQuantidade());
	            System.out.println("Valor de Venda: " + vendaEncontrada.getValorVenda());
	            System.out.println("Data da Venda: " + vendaEncontrada.getDataVenda());
	            System.out.println("Nome do Vendedor: " + vendaEncontrada.getNomeVendedor());
	        } else {
	            System.out.println("Venda com Número da Nota Fiscal " + numeroNotaFiscal + " não encontrada.");
	        }
	        statement.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return vendaEncontrada;
	}


	public boolean verificarCodigoBarrasExistente(String codigoDeBarras) {
	    try {
	        String sql = "SELECT COUNT(*) AS count FROM Produto WHERE CodigoDeBarras = ?";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setString(1, codigoDeBarras);
	        ResultSet resultSet = statement.executeQuery();

	        if (resultSet.next()) {
	            int count = resultSet.getInt("count");
	            return count > 0;
	        } else {
	            return false;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public void excluirVenda(int numeroNotaFiscal, String codigoBarras, int quantidade) {
	    try {
	        // Exclui a venda do registro de vendas
	        String sqlDeleteVenda = "DELETE FROM vendas WHERE numero_nota_fiscal = ?";
	        PreparedStatement statementDeleteVenda = connection.prepareStatement(sqlDeleteVenda);
	        statementDeleteVenda.setInt(1, numeroNotaFiscal);
	        statementDeleteVenda.executeUpdate();
	        statementDeleteVenda.close();

	        // Atualiza a quantidade do produto em estoque
	        String sqlUpdateEstoque = "UPDATE Produto SET quantidade = quantidade + ? WHERE CodigoDeBarras = ?";
	        PreparedStatement statementUpdateEstoque = connection.prepareStatement(sqlUpdateEstoque);
	        statementUpdateEstoque.setInt(1, quantidade);
	        statementUpdateEstoque.setString(2, codigoBarras);
	        statementUpdateEstoque.executeUpdate();
	        statementUpdateEstoque.close();

	        System.out.println("Venda com Número da Nota Fiscal " + numeroNotaFiscal + " excluída com sucesso e quantidade do produto em estoque atualizada.");
	        connection.commit();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	public void registrarTrocaDevolucao(Troca troca, Venda vendaOriginal) {
	    try {
	        // Iniciar uma transação para garantir que todas as operações sejam executadas ou revertidas
	        connection.setAutoCommit(false);

	        // 1. Consulte o último número de nota de troca
	        String consultaUltimoNumeroSQL = "SELECT MAX(nota_troca) AS ultimo_numero FROM trocas";
	        PreparedStatement consultaUltimoNumeroStatement = connection.prepareStatement(consultaUltimoNumeroSQL);
	        ResultSet resultadoConsulta = consultaUltimoNumeroStatement.executeQuery();
	        int ultimoNumeroNotaTroca = 0;

	        if (resultadoConsulta.next()) {
	            ultimoNumeroNotaTroca = resultadoConsulta.getInt("ultimo_numero");
	        }

	        // 2. Incremente o número de nota de troca para obter o próximo número
	        int novoNumeroNotaTroca = ultimoNumeroNotaTroca + 1;

	        // 3. Registrar a troca no banco de dados
	        String sqlTroca = "INSERT INTO trocas (nome, codigo_de_barras, quantidade, novo_valor, data_devolucao, vendedor, nota_troca) VALUES (?, ?, ?, ?, ?, ?, ?)";
	        PreparedStatement statementTroca = connection.prepareStatement(sqlTroca);

	        statementTroca.setString(1, troca.getNovoNomeProduto());
	        statementTroca.setString(2, troca.getCodigoBarras());
	        statementTroca.setInt(3, troca.getNovaQuantidade());
	        statementTroca.setDouble(4, troca.getValorTroca());
	        statementTroca.setDate(5, new java.sql.Date(troca.getDataTroca().getTime()));
	        statementTroca.setString(6, troca.getVendedor()); // Defina o vendedor
	        statementTroca.setInt(7, novoNumeroNotaTroca); // Use o novo número de nota de troca

	        statementTroca.executeUpdate();

	        // 4. Atualizar a coluna "Quantidade" na tabela "Produto"
	        String sqlAtualizacao = "UPDATE Produto SET quantidade = quantidade - ? WHERE CodigoDeBarras = ?";
	        PreparedStatement statementAtualizacao = connection.prepareStatement(sqlAtualizacao);

	        statementAtualizacao.setInt(1, troca.getNovaQuantidade());
	        statementAtualizacao.setString(2, troca.getCodigoBarras());

	        statementAtualizacao.executeUpdate();

	       

	        // Confirmar a transação
	        connection.commit();

	        System.out.println("Troca realizada com sucesso. Número da Nota de Troca: " + novoNumeroNotaTroca);

	    } catch (SQLException e) {
	        e.printStackTrace();
	        // Reverter a transação em caso de erro
	        try {
	            connection.rollback();
	            System.out.println("Erro durante a troca. A transação foi revertida.");
	        } catch (SQLException rollbackException) {
	            rollbackException.printStackTrace();
	        }
	    }
	}





	public boolean verificarCorrespondenciaProduto(String codigoBarras, String nomeProduto) {
	    try {
	        String sql = "SELECT Nome FROM Produto WHERE CodigoDeBarras = ?";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setString(1, codigoBarras);

	        ResultSet resultSet = statement.executeQuery();

	        if (resultSet.next()) {
	            String nomeDoBancoDeDados = resultSet.getString("Nome");

	            // Verifique se o nome do produto corresponde ao nome do banco de dados
	            if (nomeProduto.equals(nomeDoBancoDeDados)) {
	                return true; // Correspondência encontrada
	            } else {
	                return false; // Correspondência não encontrada
	            }
	        } else {
	            return false; // Código de barras não encontrado
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // Tratamento de exceções
	    }
	}
	 public int gerarNumeroNotaTroca() {
	        // Retorna o próximo número de nota de troca na sequência e depois o incrementa
	        int notaTroca = proximoNumeroNotaTroca;
	        proximoNumeroNotaTroca++;
	        return notaTroca;
	    }

	    // Outros métodos da classe
	}

