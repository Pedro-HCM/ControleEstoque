package trabalho;

import java.awt.BorderLayout;
import javax.swing.table.DefaultTableModel;
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
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.sql.Connection;



public class Telas extends JFrame {
    
    

    private List<Produto> estoque = new ArrayList<>();
    private static bancodedados banco = new bancodedados();
    Venda venda = new Venda();
    Venda vendaOriginal;
  
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
        setLayout(new GridLayout(11, 1, 10, 10)); // Agora com 11 linhas

        JButton btnAdicionarProduto = criarBotao("Adicionar Produto");
        JButton btnAtualizarProduto = criarBotao("Atualizar Produto");
        JButton btnRemoverProduto = criarBotao("Remover Produto");
        JButton btnProcurarProduto = criarBotao("Procurar Produto");
        JButton btnCalcularValorEstoque = criarBotao("Calcular Valor Total do Estoque");
        JButton btnRelatorioVencimento = criarBotao("Gerar Relatório de Produtos Próximos ao Vencimento");
        JButton btnRealizarVenda = criarBotao("Realizar Venda");
        JButton btnTroca = criarBotao("Troca");
        JButton btnDevolucao = criarBotao("Devolução");
        JButton btnConsultarVendaNF = criarBotao("Consultar Venda por NF");
        JButton btnEstoque = criarBotao("Estoque"); // Novo botão Estoque
        JButton btnSair = criarBotao("Sair");

        add(btnAdicionarProduto);
        add(btnAtualizarProduto);
        add(btnRemoverProduto);
        add(btnProcurarProduto);
        add(btnCalcularValorEstoque);
        add(btnRelatorioVencimento);
        add(btnRealizarVenda);
        add(btnTroca);
        add(btnDevolucao);
        add(btnConsultarVendaNF);
        add(btnEstoque); // Adicionando o botão Estoque
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
                abrirTelaProcurarProdutoParaAtualizar();
                break;
            case "Remover Produto":
                removerProdutoPorNome();
                break;
            case "Procurar Produto":
                abrirTelaProcurarProduto();
                break;
            case "Calcular Valor Total do Estoque":
                abrirTelaCalculoEstoque();
                break;
            case "Gerar Relatório de Produtos Próximos ao Vencimento":
                gerarRelatorioVencimento();
                break;
            case "Realizar Venda":
                new AbrirTelaRealizarVenda();
                break;  
            case "Estoque":
                abrirTelaEstoque();
                break; 
            case "Troca":
            	abrirTelaTroca();
                break;
            case "Devolução":
            	abrirTelaDevolucao();
                break;
            case "Consultar Venda por NF":
                abrirTelaConsultarVendaPorNotaFiscal();
                break;
            case "Sair":
                System.out.println("Saindo do programa. Até mais!");
                System.exit(0);
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
        }
    }
    public void abrirTelaDevolucao() {
        // Solicitar o número da nota fiscal ao usuário
        String inputNotaFiscal = JOptionPane.showInputDialog("Digite o número da nota fiscal:");

        // Verificar se o usuário cancelou a entrada
        if (inputNotaFiscal == null) {
            System.out.println("Operação cancelada pelo usuário.");
            return;
        }

        try {
            // Converter a entrada para um número inteiro
            int numeroNotaFiscal = Integer.parseInt(inputNotaFiscal);

            // Iniciar uma transação para garantir que todas as operações sejam executadas ou revertidas
            banco.conectar();

            // Verificar se a venda original existe (não precisa consultar novamente)
            Venda vendaOriginal = banco.consultarVendaPorNumeroNotaFiscal(numeroNotaFiscal);

            // Verificar se a venda original existe
            if (vendaOriginal != null) {
                // Exibir os dados da venda em uma interface gráfica
                exibirDadosVenda(vendaOriginal);

                // Solicitar confirmação para devolução
                int resposta = JOptionPane.showConfirmDialog(null, "Deseja realmente devolver esta venda?", "Confirmação", JOptionPane.YES_NO_OPTION);

                if (resposta == JOptionPane.YES_OPTION) {
                    // Registrar a devolução no banco de dados
                    banco.registrarDevolucao(numeroNotaFiscal);

                    // Confirmar a transação
                    banco.commit();

                    // Exemplo: Exibir uma mensagem após a devolução
                    JOptionPane.showMessageDialog(null, "Devolução registrada com sucesso!");

                    // Restante do código...
                    // Pode incluir outras ações, atualizações de interface, etc.
                } else {
                    System.out.println("Devolução cancelada pelo usuário.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Venda não encontrada. Verifique o número da nota fiscal.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Número de nota fiscal inválido. Digite um número válido.");
        }
    }

    private void exibirDadosVenda(Venda venda) {
        // Criar uma mensagem formatada com os dados da venda
        String mensagem = "Dados da venda:\n" +
                "Produto: " + venda.getNomeProduto() + "\n" +
                "Quantidade: " + venda.getQuantidade() + "\n" +
                "Valor de Venda: " + venda.getValorVenda() + "\n" +
                "Data da Venda: " + venda.getDataVenda() + "\n" +
                "Nome do Vendedor: " + venda.getNomeVendedor();

        // Exibir a mensagem em uma JOptionPane
        JOptionPane.showMessageDialog(null, mensagem, "Dados da Venda", JOptionPane.INFORMATION_MESSAGE);
    }


   
    private void abrirTelaTroca() {
        JFrame frame = new JFrame("Troca");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 5, 5));

        JTextField txtNotaFiscal = new JTextField();
        JButton btnConsultar = new JButton("Consultar");
        JButton btnProximo = new JButton("Próximo");

        // Componentes para a segunda tela de troca
        JTextField txtNomeVendedor = new JTextField();
        JTextField txtNovoProduto = new JTextField();
        JTextField txtCodigoBarras = new JTextField();
        JTextField txtNovaQuantidade = new JTextField();
        JTextField txtValorTroca = new JTextField();
        JTextField txtDataTroca = new JTextField();

        // Adiciona os componentes da primeira tela
        panel.add(new JLabel("Nota Fiscal da venda:"));
        panel.add(txtNotaFiscal);
        panel.add(new JLabel());
        panel.add(btnConsultar);

        frame.add(panel);
        frame.setVisible(true);

        btnConsultar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Solicita a nota fiscal da venda
                int numeroNotaFiscal = Integer.parseInt(txtNotaFiscal.getText());

                // Obtém a venda original do banco de dados
                vendaOriginal = banco.consultarVendaPorNumeroNotaFiscal(numeroNotaFiscal);

                // Verifica se a venda original existe
                if (vendaOriginal != null) {
                    // Remove os componentes da primeira tela
                    panel.removeAll();

                    // Adiciona os componentes da segunda tela
                    panel.setLayout(new GridLayout(7, 2, 5, 5));
                    panel.add(new JLabel("Nome do vendedor:"));
                    panel.add(txtNomeVendedor);
                    panel.add(new JLabel("Novo produto:"));
                    panel.add(txtNovoProduto);
                    panel.add(new JLabel("Código de barras:"));
                    panel.add(txtCodigoBarras);
                    panel.add(new JLabel("Nova quantidade:"));
                    panel.add(txtNovaQuantidade);
                    panel.add(new JLabel("Valor da troca:"));
                    panel.add(txtValorTroca);
                    panel.add(new JLabel("Data da troca (yyyy-MM-dd):"));
                    panel.add(txtDataTroca);
                    panel.add(new JLabel());
                    panel.add(btnProximo);

                    // Exibe os detalhes da venda em um JOptionPane
                    JOptionPane.showMessageDialog(frame, "Detalhes da Venda:\n" +
                            "Nome do Produto: " + vendaOriginal.getNomeProduto() + "\n" +
                            "Código de Barras: " + vendaOriginal.getCodigoBarras() + "\n" +
                            "Quantidade: " + vendaOriginal.getQuantidade() + "\n" +
                            "Valor da Venda: " + vendaOriginal.getValorVenda() + "\n" +
                            "Data da Venda: " + vendaOriginal.getDataVenda());

                    frame.revalidate();
                    frame.repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Venda não encontrada. Verifique o número da nota fiscal.");
                }
            }
        });
    

        btnProximo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtém os dados inseridos
                String nomeVendedor = txtNomeVendedor.getText();
                String novoProduto = txtNovoProduto.getText();
                String codigoBarras = txtCodigoBarras.getText();
                int novaQuantidade = Integer.parseInt(txtNovaQuantidade.getText());
                double valorTroca = Double.parseDouble(txtValorTroca.getText());
                Date dataTroca = null; // Aqui você precisa converter a string para um objeto Date

                // Chama o método para registrar a troca no banco de dados
                banco.registrarTroca(
                    new Troca(
                        novoProduto,       // nome
                        codigoBarras,      // codigo_de_barras
                        novaQuantidade,    // quantidade
                        valorTroca,        // novo_valor
                        dataTroca,         // data_devolucao
                        nomeVendedor,      // vendedor
                        0                  // nota_troca
                    ),
                    vendaOriginal
                );

                // Chama o método para excluir a venda do banco de dados
                banco.excluirVenda(vendaOriginal.getNumeroNotaFiscal(), vendaOriginal.getCodigoBarras(), vendaOriginal.getQuantidade());

                frame.dispose();
            }
        });
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
        frame.setSize(400, 350); // Aumentei a altura para acomodar o novo JLabel
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2, 5, 5));

        JTextField txtNovoNome = new JTextField(produto.getNome());
        JTextField txtNovoCodigo = new JTextField(produto.getCodigoDeBarras());
        JTextField txtNovaQuantidade = new JTextField(String.valueOf(produto.getQuantidade()));
        JTextField txtNovoPreco = new JTextField(String.valueOf(produto.getPrecoUnitario()));
        JTextField txtNovaDataValidade = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(produto.getDataDeValidade()));

        JLabel lblAtualizacao = new JLabel(); // JLabel para exibir as informações atualizadas

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para atualizar o produto
                try {
                    String novoNome = txtNovoNome.getText().trim();
                    String novoCodigoDeBarras = txtNovoCodigo.getText().trim();
                    int novaQuantidade = Integer.parseInt(txtNovaQuantidade.getText().trim());
                    double novoPreco = Double.parseDouble(txtNovoPreco.getText().trim());

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date novaDataValidade = dateFormat.parse(txtNovaDataValidade.getText().trim());

                    // Desativar o modo de confirmação automática
                    banco.setAutoCommit(false);

                    // Atualizar os atributos do produto
                    produto.setNome(novoNome);
                    produto.setCodigoDeBarras(novoCodigoDeBarras);
                    produto.setQuantidade(novaQuantidade);
                    produto.setPrecoUnitario(novoPreco);
                    produto.setDataDeValidade(novaDataValidade);

                    // Atualizar no banco de dados
                    banco.atualizarProduto(produto);

                    // Confirmar a transação
                    banco.commit();
                    
                    // Exibir informações atualizadas no JLabel
                    String infoAtualizadas = String.format("Produto Atualizado:\nNome: %s\nCódigo de Barras: %s\nQuantidade: %d\nPreço: %.2f\nData de Validade: %s",
                            produto.getNome(), produto.getCodigoDeBarras(), produto.getQuantidade(), produto.getPrecoUnitario(), dateFormat.format(produto.getDataDeValidade()));
                    lblAtualizacao.setText(infoAtualizadas);

                    JOptionPane.showMessageDialog(null, "Produto atualizado com sucesso!");
                } catch (SQLException | ParseException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao atualizar produto.", "Erro", JOptionPane.ERROR_MESSAGE);
                    
                    // Reverter a transação em caso de exceção
                    banco.rollback();
                } finally {
                    try {
                        // Ativar o modo de confirmação automática novamente
                        banco.setAutoCommit(true);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
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
        
        // Adiciona o JLabel para exibir as informações atualizadas
        panel.add(new JLabel("Informações Atualizadas:"));
        panel.add(lblAtualizacao);

        frame.add(panel);
        frame.setVisible(true);
    }
    private void abrirTelaCalculoEstoque() {
        JFrame frame = new JFrame("Calcular Valor Total do Estoque");
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTextArea resultadoTextArea = new JTextArea();
        resultadoTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultadoTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton calcularButton = new JButton("Calcular");
        calcularButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Map<String, Double> valoresIndividuais = banco.calcularValoresIndividuaisEstoqueComTotal();

                    // Exibe os resultados no JTextArea
                    resultadoTextArea.setText("Valores Individuais do Estoque:\n");
                    
                    // Mostra valores individuais
                    for (Map.Entry<String, Double> entry : valoresIndividuais.entrySet()) {
                        if (!entry.getKey().equals("Total")) {
                            resultadoTextArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
                        }
                    }

                    // Mostra o valor total por último
                    Double valorTotal = valoresIndividuais.get("Total");
                    if (valorTotal != null) {
                        resultadoTextArea.append("Total: " + valorTotal);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(calcularButton, BorderLayout.SOUTH);

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
    
    
    private void abrirTelaConsultarVendaPorNotaFiscal() {
        JFrame frame = new JFrame("Consultar Venda por Número de Nota Fiscal");
        frame.setSize(200, 180);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Digite o número da nota fiscal:");
        frame.add(label, BorderLayout.NORTH);

        JTextField textField = new JTextField();
        JButton btnConsultar = new JButton("Consultar");

        btnConsultar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consultarVendaPorNotaFiscal(textField.getText());
                frame.dispose();
            }
        });

        frame.add(textField, BorderLayout.CENTER);
        frame.add(btnConsultar, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void consultarVendaPorNotaFiscal(String numeroNotaFiscal) {
        try {
            int numeroNota = Integer.parseInt(numeroNotaFiscal);
            Venda vendaEncontrada = banco.consultarVendaPorNumeroNotaFiscal(numeroNota);

            if (vendaEncontrada != null) {
                // Exibir as informações em uma nova janela
                new TelaConsultaVenda(vendaEncontrada);
            } else {
                JOptionPane.showMessageDialog(null, "Venda não encontrada para o Número da Nota Fiscal informado.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao consultar venda. Verifique os dados inseridos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class TelaConsultaVenda {
        private JFrame frame;

        public TelaConsultaVenda(Venda venda) {
            frame = new JFrame("Detalhes da Venda");
            frame.setSize(400, 200);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new GridLayout(5, 2));

            adicionarInformacao("Produto:", venda.getNomeProduto());
            adicionarInformacao("Quantidade:", String.valueOf(venda.getQuantidade()));
            adicionarInformacao("Valor de Venda:", String.valueOf(venda.getValorVenda()));
            adicionarInformacao("Data da Venda:", new SimpleDateFormat("yyyy-MM-dd").format(venda.getDataVenda()));
            adicionarInformacao("Nome do Vendedor:", venda.getNomeVendedor());

            frame.setVisible(true);
        }

        private void adicionarInformacao(String rotulo, String valor) {
            frame.add(new JLabel(rotulo));
            frame.add(new JLabel(valor));
        }
    }
   public class AbrirTelaRealizarVenda implements ActionListener {

        private JFrame frame;
        private JTextField txtNomeProduto;
        private JTextField txtCodigoBarras;
        private JTextField txtQuantidade;
        private JTextField txtValorVenda;
        private JTextField txtDataVenda;
        private JTextField txtNomeVendedor;
        private bancodedados banco;

        public AbrirTelaRealizarVenda() {
            frame = new JFrame("Realizar Venda");
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(7, 2, 5, 5));

            txtNomeProduto = new JTextField();
            txtCodigoBarras = new JTextField();
            txtQuantidade = new JTextField();
            txtValorVenda = new JTextField();
            txtDataVenda = new JTextField();
            txtNomeVendedor = new JTextField();

            JButton btnRealizarVenda = new JButton("Realizar Venda");
            btnRealizarVenda.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    realizarVenda();
                }
            });

            panel.add(new JLabel("Nome do Produto:"));
            panel.add(txtNomeProduto);
            panel.add(new JLabel("Código de Barras:"));
            panel.add(txtCodigoBarras);
            panel.add(new JLabel("Quantidade:"));
            panel.add(txtQuantidade);
            panel.add(new JLabel("Valor da Venda:"));
            panel.add(txtValorVenda);
            panel.add(new JLabel("Data da Venda (YYYY-MM-DD):"));
            panel.add(txtDataVenda);
            panel.add(new JLabel("Nome do Vendedor:"));
            panel.add(txtNomeVendedor);
            panel.add(new JLabel());
            panel.add(btnRealizarVenda);

            frame.add(panel);
            frame.setVisible(true);

            banco = new bancodedados();
            banco.conectar();
        }

        private void realizarVenda() {
            

            venda.setNomeProduto(txtNomeProduto.getText().trim());
            String codigoBarras = txtCodigoBarras.getText().trim();

            // Verificar se o produto existe e corresponde ao código de barras
            if (!banco.verificarCorrespondenciaProduto(codigoBarras, venda.getNomeProduto())) {
                JOptionPane.showMessageDialog(null, "O nome do produto não corresponde ao código de barras. Venda cancelada.");
                return;
            }

            venda.setCodigoBarras(codigoBarras);

            venda.setQuantidade(Integer.parseInt(txtQuantidade.getText().trim()));
            venda.setValorVenda(Double.parseDouble(txtValorVenda.getText().trim()));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date dataVenda = sdf.parse(txtDataVenda.getText().trim());
                venda.setDataVenda(dataVenda);
            } catch (ParseException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Formato de data inválido. Venda cancelada.");
                return;
            }

            venda.setNomeVendedor(txtNomeVendedor.getText().trim());

            // Verificar quantidade em estoque
            int quantidadeEmEstoque = banco.consultarQuantidadeEmEstoque(codigoBarras);

            if (quantidadeEmEstoque >= venda.getQuantidade()) {
                // Se a quantidade em estoque for suficiente, realizar a venda
                banco.RealizarVenda(venda);

                // Obter o número da nota fiscal recém-gerado
                int numeroNotaFiscalGerado = banco.obterUltimoNumeroNotaFiscal();

                JOptionPane.showMessageDialog(null, "Venda realizada com sucesso!\nNúmero da Nota Fiscal: " + numeroNotaFiscalGerado);
                limparCampos();
                mostrarTela();
            } else {
                // Quantidade em estoque insuficiente
                JOptionPane.showMessageDialog(null, "Quantidade insuficiente em estoque. Venda cancelada.");
            }

        }

        private void limparCampos() {
            txtNomeProduto.setText("");
            txtCodigoBarras.setText("");
            txtQuantidade.setText("");
            txtValorVenda.setText("");
            txtDataVenda.setText("");
            txtNomeVendedor.setText("");
        }

        public void mostrarTela() {
            frame.setVisible(true);
        }

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
}
   private void abrirTelaEstoque() {
    banco.BuscarEstoqueCompleto();
}
}
