package trabalho;

import java.awt.BorderLayout;
import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import java.sql.Connection;

public class Telas extends JFrame {
	
	
	private List<Produto> estoque = new ArrayList<>();
    private static bancodedados banco = new bancodedados();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Telas().setVisible(true);
            }
        });
    }

    public Telas() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(10, 1, 10, 10)); // Layout de grade com 10 linhas, 1 coluna

        JButton btnAdicionarProduto = criarBotao("Adicionar Produto");
        JButton btnAtualizarProduto = criarBotao("Atualizar Produto");
        JButton btnRemoverProduto = criarBotao("Remover Produto");
        JButton btnProcurarProduto = criarBotao("Procurar Produto");
        JButton btnCalcularValorEstoque = criarBotao("Calcular Valor Total do Estoque");
        JButton btnRelatorioVencimento = criarBotao("Gerar Relatório de Produtos Próximos ao Vencimento");
        JButton btnRealizarVenda = criarBotao("Realizar Venda");
        JButton btnDevolucaoTroca = criarBotao("Devolução ou Troca");
        JButton btnConsultarVendaNF = criarBotao("Consultar Venda por NF");
        JButton btnSair = criarBotao("Sair");

        add(btnAdicionarProduto);
        add(btnAtualizarProduto);
        add(btnRemoverProduto);
        add(btnProcurarProduto);
        add(btnCalcularValorEstoque);
        add(btnRelatorioVencimento);
        add(btnRealizarVenda);
        add(btnDevolucaoTroca);
        add(btnConsultarVendaNF);
        add(btnSair);

        banco.conectar(); // Conectar ao banco de dados ao iniciar a aplicação
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setAlignmentX(CENTER_ALIGNMENT);

        botao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acaoBotao(texto);
            }
        });

        return botao;
    }

    private void acaoBotao(String opcao) {
        switch (opcao) {
            case "Adicionar Produto":
                abrirTelaAdicionarProduto();
                break;
            case "Atualizar Produto":
            	abrirTelaProcurarProdutoParaAtualizar(	);
                break;
            case "Remover Produto":
            	removerProdutoPorNome();
                break;
            case "Procurar Produto":
            	abrirTelaProcurarProduto();
                break;
            case "Calcular Valor Total do Estoque":
            	calcularValorTotalEstoque();
                break;
            case "Gerar Relatório de Produtos Próximos ao Vencimento":
            	gerarRelatorioVencimento();
                break;
            case "Realizar Venda":
                // Implemente a lógica para a opção "Realizar Venda"
                break;
            case "Devolução ou Troca":
                // Implemente a lógica para a opção "Devolução ou Troca"
                break;
            case "Consultar Venda por NF":
                // Implemente a lógica para a opção "Consultar Venda por NF"
                break;
            case "Sair":
                System.out.println("Saindo do programa. Até mais!");
                System.exit(0);
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
        }
        
    }
    
    private void abrirTelaProcurarProduto() {
        JFrame frame = new JFrame("Procurar Produto por Nome");
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Digite o nome do produto:");
        frame.add(label, BorderLayout.NORTH);

        JTextField textField = new JTextField();
        JButton btnProcurar = new JButton("Procurar");

        bancodedados banco = this.banco;  // Criar uma instância de bancodedados

        btnProcurar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeProduto = textField.getText();
                try {
                    List<Produto> produtosEncontrados = banco.procurarProdutoPorNome(nomeProduto);

                    if (!produtosEncontrados.isEmpty()) {
                        exibirTelaProduto(produtosEncontrados.get(0));
                    } else {
                        JOptionPane.showMessageDialog(null, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao procurar produto.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
                frame.dispose(); // Fecha a janela após a pesquisa
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(textField);
        panel.add(btnProcurar);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void exibirTelaProduto(Produto produto) {
        JFrame frame = new JFrame("Detalhes do Produto");
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(5, 2));

        frame.add(new JLabel("Nome:"));
        frame.add(new JLabel(produto.getNome()));

        frame.add(new JLabel("Código de Barras:"));
        frame.add(new JLabel(produto.getCodigoDeBarras()));

        frame.add(new JLabel("Preço Unitário:"));
        frame.add(new JLabel(Double.toString(produto.getPrecoUnitario())));

        frame.add(new JLabel("Quantidade:"));
        frame.add(new JLabel(Integer.toString(produto.getQuantidade())));

        frame.add(new JLabel("Data de Validade:"));
        frame.add(new JLabel(produto.getDataDeValidade().toString()));

        frame.setVisible(true);
    }
    
    private void abrirTelaProcurarProdutoParaAtualizar() {
        JFrame frame = new JFrame("Procurar Produto para Atualizar");
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Digite o nome do produto:");
        frame.add(label, BorderLayout.NORTH);

        JTextField textField = new JTextField();
        JButton btnProcurar = new JButton("Procurar");

        btnProcurar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeProduto = textField.getText();
                try {
                    List<Produto> produtosEncontrados = banco.procurarProdutoPorNome(nomeProduto);

                    if (!produtosEncontrados.isEmpty()) {
                        exibirTelaAtualizarProduto(produtosEncontrados.get(0));
                    } else {
                        JOptionPane.showMessageDialog(null, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao procurar produto.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
                frame.dispose(); // Fecha a janela após a pesquisa
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(textField);
        panel.add(btnProcurar);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
    
    private static void exibirTelaAtualizarProduto(Produto produto) {
        JFrame frame = new JFrame("Atualizar Produto");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 5, 5));

        JTextField txtNovoNome = new JTextField(produto.getNome());
        JTextField txtNovoCodigo = new JTextField(produto.getCodigoDeBarras());
        JTextField txtNovaQuantidade = new JTextField(String.valueOf(produto.getQuantidade()));
        JTextField txtNovoPreco = new JTextField(String.valueOf(produto.getPrecoUnitario()));
        JTextField txtNovaDataValidade = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(produto.getDataDeValidade()));

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para atualizar o produto
                try {
                    String novoNome = txtNovoNome.getText().trim();
                    String novoCodigo = txtNovoCodigo.getText().trim();
                    int novaQuantidade = Integer.parseInt(txtNovaQuantidade.getText().trim());
                    double novoPreco = Double.parseDouble(txtNovoPreco.getText().trim());

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date novaDataValidade = dateFormat.parse(txtNovaDataValidade.getText().trim());

                    // Verificar se o código de barras já existe (exceto para o próprio produto)
                    if (!produto.getCodigoDeBarras().equals(novoCodigo) && banco.verificarCodigoBarrasExistente(novoCodigo)) {
                        JOptionPane.showMessageDialog(null, "Código de barras já existe. Tente novamente com um código diferente.");
                        return;
                    }

                    // Atualizar os atributos do produto
                    produto.setNome(novoNome);
                    produto.setCodigoDeBarras(novoCodigo);
                    produto.setQuantidade(novaQuantidade);
                    produto.setPrecoUnitario(novoPreco);
                    produto.setDataDeValidade(novaDataValidade);

                    // Atualizar no banco de dados
                    banco.atualizarProduto(produto);
                    banco.commit();
                    banco.setAutoCommit(true);
                    JOptionPane.showMessageDialog(null, "Produto atualizado com sucesso!");
                } catch (SQLException | ParseException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao atualizar produto.", "Erro", JOptionPane.ERROR_MESSAGE);
                }

                frame.dispose();
            }
        });

        panel.add(new JLabel("Novo nome do produto:"));
        panel.add(txtNovoNome);
        panel.add(new JLabel("Novo código de barras:"));
        panel.add(txtNovoCodigo);
        panel.add(new JLabel("Nova quantidade:"));
        panel.add(txtNovaQuantidade);
        panel.add(new JLabel("Novo preço unitário:"));
        panel.add(txtNovoPreco);
        panel.add(new JLabel("Nova data de validade (yyyy-MM-dd):"));
        panel.add(txtNovaDataValidade);
        panel.add(new JLabel());
        panel.add(btnSalvar);

        frame.add(panel);
        frame.setVisible(true);
    }
    
    private void abrirTelaAdicionarProduto() {
        JFrame frame = new JFrame("Adicionar Produto");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 5, 5));

        JTextField txtNome = new JTextField();
        JTextField txtCodigo = new JTextField();
        JTextField txtQuantidade = new JTextField();
        JTextField txtPreco = new JTextField();
        JTextField txtDataValidade = new JTextField();

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarProduto(txtNome.getText(), txtCodigo.getText(), txtQuantidade.getText(),
                        txtPreco.getText(), txtDataValidade.getText());

                frame.dispose();
            }
        });

        panel.add(new JLabel("Nome do produto:"));
        panel.add(txtNome);
        panel.add(new JLabel("Código de barras:"));
        panel.add(txtCodigo);
        panel.add(new JLabel("Quantidade:"));
        panel.add(txtQuantidade);
        panel.add(new JLabel("Preço unitário:"));
        panel.add(txtPreco);
        panel.add(new JLabel("Data de validade (yyyy-MM-dd):"));
        panel.add(txtDataValidade);
        panel.add(new JLabel());
        panel.add(btnSalvar);

        frame.add(panel);
        frame.setVisible(true);
    }
    

    private void adicionarProduto(String nome, String codigo, String quantidadeStr, String precoStr, String dataValidadeStr) {
    	int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeStr);
            if (quantidade <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "A quantidade deve ser um número inteiro positivo. Tente novamente.");
            return;
        }

        // Verificar se o preço unitário é um número positivo
        double precoUnitario;
        try {
            precoUnitario = Double.parseDouble(precoStr);
            if (precoUnitario <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "O preço unitário deve ser um número positivo. Tente novamente.");
            return;
        }

        // Verificar se o código de barras já existe no banco de dados
        if (banco.verificarCodigoBarrasExistente(codigo)) {
            JOptionPane.showMessageDialog(null, "Código de barras já existe. Tente novamente com um código diferente.");
            return;
        }

        // Converter a data de String para Date
        Date dataDeValidade;
        try {
            dataDeValidade = new SimpleDateFormat("yyyy-MM-dd").parse(dataValidadeStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Erro ao converter a data. Certifique-se de usar o formato yyyy-MM-dd.");
            e.printStackTrace();
            return; // Retornar se houver um erro na conversão da data
        }

        // Criar um objeto Produto com os dados inseridos
        Produto novoProduto = new Produto(nome, codigo, quantidade, precoUnitario, dataDeValidade);

        // Chamar o método da sua classe bancodedados
        banco.inserirProduto(novoProduto);

        JOptionPane.showMessageDialog(null, "Produto adicionado com sucesso!");
    }
    
    private void removerProdutoPorNome() {
        JFrame frame = new JFrame("Remover Produto por Nome");
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Digite o nome do produto a ser removido:");
        frame.add(label, BorderLayout.NORTH);

        JTextField textField = new JTextField();
        JButton btnRemover = new JButton("Remover");

        btnRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeProduto = textField.getText();
                try {
                    List<Produto> produtosEncontrados = banco.procurarProdutoPorNome(nomeProduto);

                    if (!produtosEncontrados.isEmpty()) {
                        Produto produtoParaRemover = produtosEncontrados.get(0);
                        int confirmacao = JOptionPane.showConfirmDialog(null, "Você tem certeza de que deseja remover o produto '" + produtoParaRemover.getNome() + "'?", "Confirmação", JOptionPane.YES_NO_OPTION);

                        if (confirmacao == JOptionPane.YES_OPTION) {
                            banco.excluirProduto(produtoParaRemover);
                            JOptionPane.showMessageDialog(null, "Produto removido com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Operação de remoção cancelada.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao procurar produto.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
                frame.dispose(); // Fecha a janela após a remoção
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(textField);
        panel.add(btnRemover);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
    
    private Connection connection;	
    
    private void calcularValorTotalEstoque() {
        try {
        	try {
                this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/estoque", "root", "21798213");
            } catch (SQLException e) {
                // Lide com erros de conexão aqui
                e.printStackTrace();
            }
            Object[][] data = obterDadosEstoque();
            String[] colunas = {"ID", "Nome do Produto", "Quantidade", "Preço Unitário", "Valor Total"};

            JTable table = new JTable(data, colunas);
            JScrollPane scrollPane = new JScrollPane(table);

            double valorTotal = banco.calcularValorTotalEstoque();
            JOptionPane.showMessageDialog(null, "Valor total do estoque: R$" + valorTotal, "Valor Total do Estoque", JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(null, scrollPane, "Detalhes do Estoque", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao calcular o valor total do estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Object[][] obterDadosEstoque() throws SQLException {
        String sql = "SELECT ID, Nome, Quantidade, PrecoUnitario, (PrecoUnitario * Quantidade) AS ValorTotal FROM Produto";
        PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet resultSet = statement.executeQuery();

        // Obtém o número de linhas no ResultSet
        resultSet.last();
        int rowCount = resultSet.getRow();
        resultSet.beforeFirst();

        Object[][] data = new Object[rowCount][5]; // 5 colunas: ID, NomeProduto, Quantidade, PrecoUnitario, ValorTotal

        int row = 0;
        while (resultSet.next()) {
            data[row][0] = resultSet.getInt("ID");
            data[row][1] = resultSet.getString("Nome");
            data[row][2] = resultSet.getInt("Quantidade");
            data[row][3] = resultSet.getDouble("PrecoUnitario");
            data[row][4] = resultSet.getDouble("ValorTotal");
            row++;
        }

        statement.close();
        return data;
    }
    
    private void gerarRelatorioVencimento() {
        try {
            // Chamar o método para obter a lista de produtos próximos ao vencimento
            List<Produto> produtosProximosVencimento = banco.gerarRelatorioVencimento();

            if (produtosProximosVencimento.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nenhum produto próximo ao vencimento encontrado.");
            } else {
                // Criar um modelo de tabela para armazenar os dados
                DefaultTableModel tableModel = new DefaultTableModel();
                tableModel.addColumn("Nome");
                tableModel.addColumn("Data de Validade");

                // Preencher o modelo de tabela com os dados dos produtos
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                for (Produto produto : produtosProximosVencimento) {
                    Object[] rowData = {produto.getNome(), dateFormat.format(produto.getDataDeValidade())};
                    tableModel.addRow(rowData);
                }

                // Criar a tabela e configurá-la
                JTable tabelaProdutos = new JTable(tableModel);
                tabelaProdutos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                JScrollPane scrollPane = new JScrollPane(tabelaProdutos);

                // Exibir a tabela em uma janela
                JFrame frame = new JFrame("Relatório de Produtos Próximos ao Vencimento");
                frame.setSize(600, 400);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                
                // Adicionar a tabela ao frame
                frame.add(scrollPane);
                
                // Exibir o frame
                frame.setVisible(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao gerar relatório de produtos próximos ao vencimento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

}
