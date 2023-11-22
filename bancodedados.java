package atividade;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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

    public List<Produto> procurarProdutoPorNome(String termoPesquisa) throws SQLException {
        List<Produto> produtosEncontrados = new ArrayList<>();
        String sql = "SELECT * FROM Produto WHERE Nome LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + termoPesquisa + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String nome = resultSet.getString("Nome");
                    String codigoDeBarras = resultSet.getString("CodigoDeBarras");
                    double precoUnitario = resultSet.getDouble("PrecoUnitario");
                    int quantidade = resultSet.getInt("Quantidade");
                    Date dataValidade = resultSet.getDate("DataValidade");

                    Produto produto = new Produto(nome, codigoDeBarras, quantidade, precoUnitario, dataValidade);
                    produtosEncontrados.add(produto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return produtosEncontrados;
    }


    public Map<String, Double> calcularValoresIndividuaisEstoqueComTotal() throws SQLException {
        Map<String, Double> valoresIndividuais = new HashMap<>();
        double valorTotal = 0.0;

        String sql = "SELECT Nome, PrecoUnitario, Quantidade, (PrecoUnitario * Quantidade) AS ValorProduto FROM Produto";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String nomeProduto = resultSet.getString("Nome");
            double valorProduto = resultSet.getDouble("ValorProduto");

            valoresIndividuais.put(nomeProduto, valorProduto);
            valorTotal += valorProduto;
        }

        valoresIndividuais.put("Total", valorTotal);

        statement.close();
        return valoresIndividuais;
    }



    public List<Produto> gerarRelatorioVencimento() throws SQLException {
        List<Produto> produtosProximosVencimento = new ArrayList<>();
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
        try {
            String sql = "UPDATE Produto SET Nome = ?, Quantidade = ?, PrecoUnitario = ?, DataValidade = ? WHERE CodigoDeBarras = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, produto.getNome());
                statement.setInt(2, produto.getQuantidade());
                statement.setDouble(3, produto.getPrecoUnitario());
                statement.setDate(4, new java.sql.Date(produto.getDataDeValidade().getTime()));
                statement.setString(5, produto.getCodigoDeBarras());
                statement.executeUpdate();
            }
            commit(); // Confirme a transação
        } catch (SQLException e) {
            e.printStackTrace();
            rollback(); // Reverta a transação em caso de exceção
            throw e; // Rejogue a exceção para notificar a camada superior
        }
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

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
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
	        statement.setTimestamp(5, new java.sql.Timestamp(venda.getDataVenda().getTime()));
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
	        int rowsAffected = statementDeleteVenda.executeUpdate();
	        statementDeleteVenda.close();

	        if (rowsAffected > 0) {
	            // A venda foi excluída com sucesso
	            System.out.println("Venda com Número da Nota Fiscal " + numeroNotaFiscal + " excluída com sucesso.");
	        } else {
	            // Nenhuma venda foi excluída (Nota Fiscal não encontrada)
	            System.out.println("Nenhuma venda encontrada com Número da Nota Fiscal " + numeroNotaFiscal);
	        }

	        // Atualiza a quantidade do produto em estoque
	        String sqlUpdateEstoque = "UPDATE Produto SET quantidade = quantidade + ? WHERE CodigoDeBarras = ?";
	        PreparedStatement statementUpdateEstoque = connection.prepareStatement(sqlUpdateEstoque);
	        statementUpdateEstoque.setInt(1, quantidade);
	        statementUpdateEstoque.setString(2, codigoBarras);
	        statementUpdateEstoque.executeUpdate();
	        statementUpdateEstoque.close();

	        System.out.println("Quantidade do produto em estoque atualizada para Codigo de Barras " + codigoBarras);

	        connection.commit();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}



	public void registrarTroca(Troca troca, Venda vendaOriginal) {
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
	        statementTroca.setDouble(4, troca.getNovoValor());

	        // Se a data de troca não estiver definida, use a data atual
	        if (troca.getDataTroca() != null) {
	            statementTroca.setDate(5, new java.sql.Date(troca.getDataTroca().getTime()));
	        } else {
	            // Use a data atual do sistema
	            statementTroca.setDate(5, new java.sql.Date(System.currentTimeMillis()));
	        }

	        statementTroca.setString(6, troca.getVendedor());
	        statementTroca.setInt(7, novoNumeroNotaTroca);

	        statementTroca.executeUpdate();

	        // 4. Atualizar a coluna "Quantidade" na tabela "Produto"
	        String sqlAtualizacao = "UPDATE Produto SET quantidade = quantidade - ? WHERE CodigoDeBarras = ?";
	        PreparedStatement statementAtualizacao = connection.prepareStatement(sqlAtualizacao);

	        statementAtualizacao.setInt(1, troca.getNovaQuantidade());
	        statementAtualizacao.setString(2, troca.getCodigoBarras());

	        statementAtualizacao.executeUpdate();

	        // Confirmar a transação
	        connection.commit();

	        // Exibir mensagem de sucesso
	        JOptionPane.showMessageDialog(null, "Troca realizada com sucesso. Número da Nota de Troca: " + novoNumeroNotaTroca);

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

	 public int consultarQuantidadeEmEstoque(String codigoBarras) {
		    try {
		        String sql = "SELECT Quantidade FROM Produto WHERE CodigoDeBarras = ?";
		        PreparedStatement statement = connection.prepareStatement(sql);
		        statement.setString(1, codigoBarras);

		        ResultSet resultSet = statement.executeQuery();

		        if (resultSet.next()) {
		            return resultSet.getInt("Quantidade");
		        } else {
		            // Produto não encontrado pelo código de barras
		            JOptionPane.showMessageDialog(null, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
		            return -1; // Valor inválido para indicar que o produto não foi encontrado
		        }
		    } catch (SQLException e) {
		        System.err.println("Erro ao consultar quantidade em estoque: " + e.getMessage());
		        return -1; // Valor inválido em caso de erro
		    }
		}
	 public int obterUltimoNumeroNotaFiscal() {
		    try {
		        String consultaUltimoNumeroSQL = "SELECT MAX(numero_nota_fiscal) AS ultimo_numero FROM vendas";
		        PreparedStatement consultaUltimoNumeroStatement = connection.prepareStatement(consultaUltimoNumeroSQL);
		        ResultSet resultadoConsulta = consultaUltimoNumeroStatement.executeQuery();

		        if (resultadoConsulta.next()) {
		            return resultadoConsulta.getInt("ultimo_numero");
		        }
		    } catch (SQLException e) {
		        System.err.println("Erro ao obter o último número da nota fiscal: " + e.getMessage());
		    }

		    return 0; // Se houver um erro, retorna 0 ou um valor padrão
		}
	 public void BuscarEstoqueCompleto() {
		    try {
		        String sql = "SELECT Nome, CodigoDeBarras, PrecoUnitario, Quantidade, DataValidade FROM Produto";
		        PreparedStatement statement = connection.prepareStatement(sql);
		        ResultSet resultSet = statement.executeQuery();

		        // Criar um modelo de tabela para armazenar os dados
		        DefaultTableModel tableModel = new DefaultTableModel();

		        // Adicionar colunas ao modelo
		        tableModel.addColumn("Nome do Produto");
		        tableModel.addColumn("Código de Barras");
		        tableModel.addColumn("Preço Unitário");
		        tableModel.addColumn("Quantidade");
		        tableModel.addColumn("Data de Validade");

		        // Adicionar linhas ao modelo com os dados do banco
		        while (resultSet.next()) {
		            String nomeProduto = resultSet.getString("Nome");
		            String codigoBarras = resultSet.getString("CodigoDeBarras");
		            double precoUnitario = resultSet.getDouble("PrecoUnitario");
		            int quantidade = resultSet.getInt("Quantidade");
		            String dataValidade = resultSet.getString("DataValidade");

		            tableModel.addRow(new Object[]{nomeProduto, codigoBarras, precoUnitario, quantidade, dataValidade});
		        }

		        // Criar a tabela e exibir em um JFrame
		        JTable table = new JTable(tableModel);
		        JScrollPane scrollPane = new JScrollPane(table);

		        JFrame frame = new JFrame("Estoque Completo");
		        frame.setSize(800, 400);
		        frame.setLocationRelativeTo(null);
		        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		        frame.add(scrollPane, BorderLayout.CENTER);

		        frame.setVisible(true);

		    } catch (SQLException e) {
		        System.err.println("Erro ao buscar o estoque completo: " + e.getMessage());
		    }
		


	}
	public void registrarDevolucao(int numeroNotaFiscal) {
		    try {
		    	String consultaUltimoNumeroDevolucaoSQL = "SELECT MAX(nota_devolucao) AS ultimo_numero FROM devolucoes";
		    	PreparedStatement consultaUltimoNumeroDevolucaoStatement = connection.prepareStatement(consultaUltimoNumeroDevolucaoSQL);
		    	ResultSet resultadoConsultaDevolucao = consultaUltimoNumeroDevolucaoStatement.executeQuery();
		    	int ultimoNumeroNotaDevolucao = 0;

		    	if (resultadoConsultaDevolucao.next()) {
		    	    ultimoNumeroNotaDevolucao = resultadoConsultaDevolucao.getInt("ultimo_numero");
		    	}
		    	int novoNumeroNotaDevolucao = ultimoNumeroNotaDevolucao + 1;
		     
		        Venda vendaOriginal = consultarVendaPorNumeroNotaFiscal(numeroNotaFiscal);

		        // Verificar se a venda original existe
		        if (vendaOriginal != null) {
		            // 2. Solicitar confirmação para devolução (Opcional)
		            // int resposta = JOptionPane.showConfirmDialog(null, "Deseja realmente devolver esta venda?", "Confirmação", JOptionPane.YES_NO_OPTION);
		            // if (resposta == JOptionPane.YES_OPTION) {

		            // 3. Registrar a devolução na tabela devolucoes
		        	String sqlDevolucao = "INSERT INTO devolucoes (nome, codigo_de_barras, quantidade, valor_devolucao, data_devolucao, vendedor, nota_devolucao) VALUES (?, ?, ?, ?, ?, ?, ?)";
		        	PreparedStatement statementDevolucao = connection.prepareStatement(sqlDevolucao);

		        	statementDevolucao.setString(1, vendaOriginal.getNomeProduto());
		        	statementDevolucao.setString(2, vendaOriginal.getCodigoBarras());
		        	statementDevolucao.setInt(3, vendaOriginal.getQuantidade());
		        	statementDevolucao.setDouble(4, vendaOriginal.getValorVenda());
		        	statementDevolucao.setDate(5, new java.sql.Date(new Date().getTime())); // Usar a data do sistema
		        	statementDevolucao.setString(6, vendaOriginal.getNomeVendedor()); // Substituir "valor_do_vendedor" pelo valor real
		        	statementDevolucao.setInt(7, novoNumeroNotaDevolucao);// Usar o número da nota fiscal da venda original


		            statementDevolucao.executeUpdate();

		            // 4. Atualizar a quantidade em estoque na tabela Produto
		            String sqlAtualizacaoEstoque = "UPDATE Produto SET quantidade = quantidade + ? WHERE CodigoDeBarras = ?";
		            PreparedStatement statementAtualizacaoEstoque = connection.prepareStatement(sqlAtualizacaoEstoque);

		            statementAtualizacaoEstoque.setInt(1, vendaOriginal.getQuantidade());
		            statementAtualizacaoEstoque.setString(2, vendaOriginal.getCodigoBarras());

		            statementAtualizacaoEstoque.executeUpdate();
		            

		            // 6. Confirmar a transação
		            connection.commit();

		            System.out.println("Devolução realizada com sucesso. Nota Fiscal Devolvida: " + numeroNotaFiscal);

		            // } else {
		            //     System.out.println("Devolução cancelada pelo usuário.");
		            // }
		        } else {
		            JOptionPane.showMessageDialog(null, "Venda não encontrada. Verifique o número da nota fiscal.");
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		        // Reverter a transação em caso de erro
		        try {
		            connection.rollback();
		            System.out.println("Erro durante a devolução. A transação foi revertida.");
		        } catch (SQLException rollbackException) {
		            rollbackException.printStackTrace();
		        }
		    }
		}
	public void mostrarTabelaVendasCompleta() {
	    try {
	        String sql = "SELECT codigo_de_barras, produto, quantidade, valor_venda, data_da_venda, nome_vendedor FROM vendas";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        ResultSet resultSet = statement.executeQuery();

	        // Criar um modelo de tabela para armazenar os dados
	        DefaultTableModel tableModel = new DefaultTableModel();

	        // Adicionar colunas ao modelo
	        tableModel.addColumn("Código de Barras");
	        tableModel.addColumn("Produto");
	        tableModel.addColumn("Quantidade");
	        tableModel.addColumn("Valor de Venda");
	        tableModel.addColumn("Data da Venda");
	        tableModel.addColumn("Nome do Vendedor");

	        // Adicionar linhas ao modelo com os dados do banco
	        while (resultSet.next()) {
	            String codigoBarras = resultSet.getString("codigo_de_barras");
	            String produto = resultSet.getString("produto");
	            int quantidade = resultSet.getInt("quantidade");
	            double valorVenda = resultSet.getDouble("valor_venda");
	            Date dataVenda = resultSet.getDate("data_da_venda");
	            String nomeVendedor = resultSet.getString("nome_vendedor");

	            tableModel.addRow(new Object[]{codigoBarras, produto, quantidade, valorVenda, dataVenda, nomeVendedor});
	        }

	        // Criar a tabela e exibir em um JFrame
	        JTable table = new JTable(tableModel);
	        JScrollPane scrollPane = new JScrollPane(table);

	        JFrame frame = new JFrame("Vendas Completo");
	        frame.setSize(800, 400);
	        frame.setLocationRelativeTo(null);
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	        frame.add(scrollPane, BorderLayout.CENTER);

	        frame.setVisible(true);

	    } catch (SQLException e) {
	        System.err.println("Erro ao buscar as vendas completas: " + e.getMessage());
	    }
	}
	public void mostrarTabelaTrocasCompleta() {
	    try {
	        String sql = "SELECT codigo_de_barras, nome, quantidade, novo_valor, data_devolucao, vendedor, nota_troca FROM trocas";

	        PreparedStatement statement = connection.prepareStatement(sql);
	        ResultSet resultSet = statement.executeQuery();

	        // Criar um modelo de tabela para armazenar os dados
	        DefaultTableModel tableModel = new DefaultTableModel();

	        // Adicionar colunas ao modelo
	        tableModel.addColumn("Código de Barras");
	        tableModel.addColumn("Nome");
	        tableModel.addColumn("Quantidade");
	        tableModel.addColumn("Valor de Troca");
	        tableModel.addColumn("Data da Troca");
	        tableModel.addColumn("Nome do Vendedor");
	        tableModel.addColumn("Nota de Troca");

	        // Adicionar linhas ao modelo com os dados do banco
	        while (resultSet.next()) {
	            String codigoBarras = resultSet.getString("codigo_de_barras");
	            String nome = resultSet.getString("nome");
	            int quantidade = resultSet.getInt("quantidade");
	            double valorTroca = resultSet.getDouble("novo_valor"); // Se a coluna é 'novo_valor'
	            Date dataTroca = resultSet.getDate("data_devolucao");
	            String nomeVendedor = resultSet.getString("vendedor");
	            int notaTroca = resultSet.getInt("nota_troca");

	            tableModel.addRow(new Object[]{codigoBarras, nome, quantidade, valorTroca, dataTroca, nomeVendedor, notaTroca});
	        }

	        // Criar a tabela e exibir em um JFrame
	        JTable table = new JTable(tableModel);
	        JScrollPane scrollPane = new JScrollPane(table);

	        JFrame frame = new JFrame("Trocas Completo");
	        frame.setSize(800, 400);
	        frame.setLocationRelativeTo(null);
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	        frame.add(scrollPane, BorderLayout.CENTER);

	        frame.setVisible(true);

	    } catch (SQLException e) {
	        System.err.println("Erro ao buscar as trocas completas: " + e.getMessage());
	    }
	}

	public void mostrarTabelaDevolucoesCompleta() {
	    try {
	        String sql = "SELECT nome, codigo_de_barras, quantidade, valor_devolucao, data_devolucao, vendedor, nota_devolucao FROM devolucoes";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        ResultSet resultSet = statement.executeQuery();

	        // Criar um modelo de tabela para armazenar os dados
	        DefaultTableModel tableModel = new DefaultTableModel();

	        // Adicionar colunas ao modelo
	        tableModel.addColumn("Nome");
	        tableModel.addColumn("Código de Barras");
	        tableModel.addColumn("Quantidade");
	        tableModel.addColumn("Valor de Devolução");
	        tableModel.addColumn("Data de Devolução");
	        tableModel.addColumn("Vendedor");
	        tableModel.addColumn("Nota de Devolução");

	        // Adicionar linhas ao modelo com os dados do banco
	        while (resultSet.next()) {
	            String nome = resultSet.getString("nome");
	            String codigoBarras = resultSet.getString("codigo_de_barras");
	            int quantidade = resultSet.getInt("quantidade");
	            double valorDevolucao = resultSet.getDouble("valor_devolucao");
	            Date dataDevolucao = resultSet.getDate("data_devolucao");
	            String vendedor = resultSet.getString("vendedor");
	            int notaDevolucao = resultSet.getInt("nota_devolucao");

	            tableModel.addRow(new Object[]{nome, codigoBarras, quantidade, valorDevolucao, dataDevolucao, vendedor, notaDevolucao});
	        }

	        // Criar a tabela e exibir em um JFrame
	        JTable table = new JTable(tableModel);
	        JScrollPane scrollPane = new JScrollPane(table);

	        JFrame frame = new JFrame("Devoluções Completo");
	        frame.setSize(800, 400);
	        frame.setLocationRelativeTo(null);
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	        frame.add(scrollPane, BorderLayout.CENTER);

	        frame.setVisible(true);

	    } catch (SQLException e) {
	        System.err.println("Erro ao buscar as devoluções completas: " + e.getMessage());
	    }
	}
	 }


