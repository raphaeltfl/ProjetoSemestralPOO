package aplicacao;

import java.util.List;
import java.util.Scanner;

import entidades.Alimenticio;
import entidades.Cliente;
import entidades.Eletronico;
import entidades.Pedido;
import entidades.Produto;
import entidades.Vestuario;
import entidadesEnumerada.StatusPedido;
import entidadesEnumerada.Tamanho;
import servicos.ServicoCliente;
import servicos.ServicoPedido;
import servicos.ServicoProduto;

public class Programa {

	private static final Scanner teclado = new Scanner(System.in);

	private static final ServicoProduto servicoProduto = new ServicoProduto();
	private static final ServicoCliente servicoCliente = new ServicoCliente();
	private static final ServicoPedido servicoPedido = new ServicoPedido();

	public static void main(String[] args) {
		carregarDadosIniciais();
		menuPrincipal();
		System.out.println("Sistema encerrado. Até logo!");
		teclado.close();
	}

	// =====================================================================
	// MENUS
	// =====================================================================

	private static void menuPrincipal() {
		int opcao;
		do {
			System.out.println("\n===== SISTEMA DE LOJA E ESTOQUE =====");
			System.out.println("1 - Produtos");
			System.out.println("2 - Clientes");
			System.out.println("3 - Pedidos");
			System.out.println("0 - Sair");
			opcao = lerInt("Escolha: ");
			switch (opcao) {
			case 1 -> menuProdutos();
			case 2 -> menuClientes();
			case 3 -> menuPedidos();
			case 0 -> {
			}
			default -> System.out.println("Opção inválida.");
			}
		} while (opcao != 0);
	}

	private static void imprimirSubmenu(String titulo) {
		System.out.println("\n--- " + titulo + " ---");
		System.out.println("1 - Incluir");
		System.out.println("2 - Alterar");
		System.out.println("3 - Excluir");
		System.out.println("4 - Listar");
		System.out.println("5 - Buscar");
		System.out.println("6 - Listar ordenado");
		System.out.println("0 - Voltar");
	}

	// ---------------------------------------------------------------------
	// PRODUTOS
	// ---------------------------------------------------------------------

	private static void menuProdutos() {
		int opcao;
		do {
			imprimirSubmenu("PRODUTOS");
			opcao = lerInt("Escolha: ");
			switch (opcao) {
			case 1 -> incluirProduto();
			case 2 -> alterarProduto();
			case 3 -> excluirProduto();
			case 4 -> servicoProduto.imprimirTabela(servicoProduto.listar());
			case 5 -> buscarProduto();
			case 6 -> listarProdutosOrdenado();
			case 0 -> {
			}
			default -> System.out.println("Opção inválida.");
			}
		} while (opcao != 0);
	}

	private static void incluirProduto() {
		System.out.println("Tipo: 1 - Eletrônico | 2 - Alimentício | 3 - Vestuário");
		int tipo = lerInt("Escolha: ");
		if (tipo < 1 || tipo > 3) {
			System.out.println("Tipo inválido.");
			return;
		}
		int codigo = lerInt("Código: ");
		if (servicoProduto.buscarPorCodigo(codigo) != null) {
			System.out.println("Já existe um produto com esse código.");
			return;
		}
		String nome = lerTexto("Nome: ");
		double preco = lerDouble("Preço base (R$): ");
		int estoque = lerInt("Quantidade em estoque: ");
		if (estoque < 0) {
			System.out.println("O estoque não pode ser negativo.");
			return;
		}

		Produto produto;
		switch (tipo) {
		case 1 -> produto = new Eletronico(codigo, nome, preco, estoque, lerInt("Garantia (meses): "));
		case 2 -> produto = new Alimenticio(codigo, nome, preco, estoque, lerInt("Dias para o vencimento: "));
		default -> produto = new Vestuario(codigo, nome, preco, estoque, lerTamanho("Tamanho (PP, P, M, G, GG): "),
				lerTexto("Cor: "));
		}

		if (servicoProduto.incluir(produto)) {
			System.out.println("Produto cadastrado com sucesso.");
		} else {
			System.out.println("Não foi possível cadastrar o produto.");
		}
	}

	private static void alterarProduto() {
		Produto p = servicoProduto.buscarPorCodigo(lerInt("Código do produto a alterar: "));
		if (p == null) {
			System.out.println("Produto não encontrado.");
			return;
		}
		System.out.println("(deixe em branco para manter o valor atual)");
		String nome = lerTextoOpcional("Nome [" + p.getNome() + "]: ");
		Double preco = lerDoubleOpcional("Preço base [" + p.getPrecoBase() + "]: ");
		Integer estoque = lerIntOpcional("Estoque [" + p.getQuantidadeEstoque() + "]: ");
		if (estoque != null && estoque < 0) {
			System.out.println("Estoque negativo ignorado.");
			estoque = null;
		}
		servicoProduto.alterar(p.getCodigo(), nome, preco, estoque);

		if (p instanceof Alimenticio a) {
			Integer dias = lerIntOpcional("Dias para o vencimento [" + a.getDiasParaVencer() + "]: ");
			if (dias != null) {
				a.setDiasParaVencer(dias);
			}
		} else if (p instanceof Vestuario v) {
			Tamanho tamanho = lerTamanhoOpcional("Tamanho [" + v.getTamanho() + "]: ");
			if (tamanho != null) {
				v.setTamanho(tamanho);
			}
			String cor = lerTextoOpcional("Cor [" + v.getCor() + "]: ");
			if (cor != null) {
				v.setCor(cor);
			}
		}
		System.out.println("Produto alterado com sucesso.");
	}

	private static void excluirProduto() {
		int codigo = lerInt("Código do produto a excluir: ");
		if (servicoPedido.produtoEmPedido(codigo)) {
			System.out.println("Não é possível excluir: o produto está em algum pedido.");
		} else if (servicoProduto.excluir(codigo)) {
			System.out.println("Produto excluído.");
		} else {
			System.out.println("Produto não encontrado.");
		}
	}

	private static void buscarProduto() {
		System.out.println("Buscar por: 1 - Código | 2 - Nome");
		int opcao = lerInt("Escolha: ");
		if (opcao == 1) {
			Produto p = servicoProduto.buscarPorCodigo(lerInt("Código: "));
			servicoProduto.imprimirTabela(p == null ? List.of() : List.of(p));
		} else if (opcao == 2) {
			servicoProduto.imprimirTabela(servicoProduto.buscarPorNome(lerTexto("Nome (ou parte): ")));
		} else {
			System.out.println("Opção inválida.");
		}
	}

	private static void listarProdutosOrdenado() {
		System.out.println("Ordenar por: 1 - Nome | 2 - Preço final");
		int opcao = lerInt("Escolha: ");
		if (opcao == 1) {
			servicoProduto.imprimirTabela(servicoProduto.listarOrdenadoPorNome());
		} else if (opcao == 2) {
			servicoProduto.imprimirTabela(servicoProduto.listarOrdenadoPorPreco());
		} else {
			System.out.println("Opção inválida.");
		}
	}

	// ---------------------------------------------------------------------
	// CLIENTES
	// ---------------------------------------------------------------------

	private static void menuClientes() {
		int opcao;
		do {
			imprimirSubmenu("CLIENTES");
			opcao = lerInt("Escolha: ");
			switch (opcao) {
			case 1 -> incluirCliente();
			case 2 -> alterarCliente();
			case 3 -> excluirCliente();
			case 4 -> servicoCliente.imprimirTabela(servicoCliente.listar());
			case 5 -> buscarCliente();
			case 6 -> servicoCliente.imprimirTabela(servicoCliente.listarOrdenadoPorNome());
			case 0 -> {
			}
			default -> System.out.println("Opção inválida.");
			}
		} while (opcao != 0);
	}

	private static void incluirCliente() {
		String cpf = lerTexto("CPF: ");
		if (servicoCliente.buscarPorCpf(cpf) != null) {
			System.out.println("Já existe um cliente com esse CPF.");
			return;
		}
		String nome = lerTexto("Nome: ");
		String email = lerTexto("E-mail: ");
		String telefone = lerTexto("Telefone: ");
		servicoCliente.incluir(new Cliente(nome, email, cpf, telefone));
		System.out.println("Cliente cadastrado com sucesso.");
	}

	private static void alterarCliente() {
		Cliente c = servicoCliente.buscarPorCpf(lerTexto("CPF do cliente a alterar: "));
		if (c == null) {
			System.out.println("Cliente não encontrado.");
			return;
		}
		System.out.println("(deixe em branco para manter o valor atual)");
		String nome = lerTextoOpcional("Nome [" + c.getNome() + "]: ");
		String email = lerTextoOpcional("E-mail [" + c.getEmail() + "]: ");
		String telefone = lerTextoOpcional("Telefone [" + c.getTelefone() + "]: ");
		servicoCliente.alterar(c.getCpf(), nome, email, telefone);
		System.out.println("Cliente alterado com sucesso.");
	}

	private static void excluirCliente() {
		String cpf = lerTexto("CPF do cliente a excluir: ");
		if (servicoPedido.clienteTemPedidos(cpf)) {
			System.out.println("Não é possível excluir: o cliente possui pedidos.");
		} else if (servicoCliente.excluir(cpf)) {
			System.out.println("Cliente excluído.");
		} else {
			System.out.println("Cliente não encontrado.");
		}
	}

	private static void buscarCliente() {
		System.out.println("Buscar por: 1 - CPF | 2 - Nome");
		int opcao = lerInt("Escolha: ");
		if (opcao == 1) {
			Cliente c = servicoCliente.buscarPorCpf(lerTexto("CPF: "));
			servicoCliente.imprimirTabela(c == null ? List.of() : List.of(c));
		} else if (opcao == 2) {
			servicoCliente.imprimirTabela(servicoCliente.buscarPorNome(lerTexto("Nome (ou parte): ")));
		} else {
			System.out.println("Opção inválida.");
		}
	}

	// ---------------------------------------------------------------------
	// PEDIDOS
	// ---------------------------------------------------------------------

	private static void menuPedidos() {
		int opcao;
		do {
			imprimirSubmenu("PEDIDOS");
			opcao = lerInt("Escolha: ");
			switch (opcao) {
			case 1 -> incluirPedido();
			case 2 -> alterarPedido();
			case 3 -> excluirPedido();
			case 4 -> servicoPedido.imprimirTabela(servicoPedido.listar());
			case 5 -> buscarPedido();
			case 6 -> listarPedidosOrdenado();
			case 0 -> {
			}
			default -> System.out.println("Opção inválida.");
			}
		} while (opcao != 0);
	}

	private static void incluirPedido() {
		Cliente c = servicoCliente.buscarPorCpf(lerTexto("CPF do cliente: "));
		if (c == null) {
			System.out.println("Cliente não encontrado. Cadastre-o primeiro.");
			return;
		}
		String pagamento = lerTexto("Forma de pagamento (ex.: PIX, Cartão, Boleto): ");
		Pedido pedido = servicoPedido.incluir(c, pagamento);
		System.out.println("Pedido #" + pedido.getNumero() + " criado.");
		if (lerLinha("Adicionar itens agora? (s/n): ").equalsIgnoreCase("s")) {
			String mais;
			do {
				adicionarItem(pedido);
				mais = lerLinha("Adicionar outro item? (s/n): ");
			} while (mais.equalsIgnoreCase("s"));
		}
		servicoPedido.imprimirDetalhe(pedido);
	}

	private static void adicionarItem(Pedido pedido) {
		Produto produto = servicoProduto.buscarPorCodigo(lerInt("Código do produto: "));
		if (produto == null) {
			System.out.println("Produto não encontrado.");
			return;
		}
		int quantidade = lerInt("Quantidade: ");
		if (quantidade <= 0) {
			System.out.println("Quantidade inválida.");
		} else if (quantidade > produto.getQuantidadeEstoque()) {
			System.out.println("Estoque insuficiente (disponível: " + produto.getQuantidadeEstoque() + ").");
		} else if (servicoPedido.adicionarItem(pedido, produto, quantidade)) {
			System.out.println("Item adicionado. Estoque restante: " + produto.getQuantidadeEstoque());
		} else {
			System.out.println("Só é possível alterar os itens de pedidos PENDENTES.");
		}
	}

	private static void alterarPedido() {
		Pedido pedido = servicoPedido.buscarPorNumero(lerInt("Número do pedido: "));
		if (pedido == null) {
			System.out.println("Pedido não encontrado.");
			return;
		}
		servicoPedido.imprimirDetalhe(pedido);
		System.out.println("\n1 - Adicionar item");
		System.out.println("2 - Remover item");
		System.out.println("3 - Alterar status");
		System.out.println("4 - Alterar forma de pagamento");
		System.out.println("0 - Cancelar");
		int opcao = lerInt("Escolha: ");
		switch (opcao) {
		case 1 -> adicionarItem(pedido);
		case 2 -> {
			if (servicoPedido.removerItem(pedido, lerInt("Código do produto a remover: "))) {
				System.out.println("Item removido e estoque devolvido.");
			} else {
				System.out.println("Não foi possível remover (item inexistente ou pedido não está PENDENTE).");
			}
		}
		case 3 -> alterarStatus(pedido);
		case 4 -> {
			servicoPedido.alterarFormaPagamento(pedido.getNumero(), lerTexto("Nova forma de pagamento: "));
			System.out.println("Forma de pagamento alterada.");
		}
		case 0 -> {
		}
		default -> System.out.println("Opção inválida.");
		}
	}

	private static void alterarStatus(Pedido pedido) {
		StatusPedido[] valores = StatusPedido.values();
		for (int i = 0; i < valores.length; i++) {
			System.out.println((i + 1) + " - " + valores[i]);
		}
		int escolha = lerInt("Novo status: ");
		if (escolha < 1 || escolha > valores.length) {
			System.out.println("Opção inválida.");
		} else if (servicoPedido.alterarStatus(pedido.getNumero(), valores[escolha - 1])) {
			System.out.println("Status alterado para " + valores[escolha - 1] + ".");
		} else {
			System.out.println("Não foi possível alterar (pedido ENTREGUE/CANCELADO ou status igual ao atual).");
		}
	}

	private static void excluirPedido() {
		if (servicoPedido.excluir(lerInt("Número do pedido a excluir: "))) {
			System.out.println("Pedido excluído e estoque devolvido.");
		} else {
			System.out.println("Pedido não encontrado.");
		}
	}

	private static void buscarPedido() {
		System.out.println("Buscar por: 1 - Número | 2 - Cliente");
		int opcao = lerInt("Escolha: ");
		if (opcao == 1) {
			Pedido p = servicoPedido.buscarPorNumero(lerInt("Número: "));
			if (p == null) {
				System.out.println("Pedido não encontrado.");
			} else {
				servicoPedido.imprimirDetalhe(p);
			}
		} else if (opcao == 2) {
			servicoPedido.imprimirTabela(servicoPedido.buscarPorCliente(lerTexto("Nome do cliente (ou parte): ")));
		} else {
			System.out.println("Opção inválida.");
		}
	}

	private static void listarPedidosOrdenado() {
		System.out.println("Ordenar por: 1 - Número | 2 - Total");
		int opcao = lerInt("Escolha: ");
		if (opcao == 1) {
			servicoPedido.imprimirTabela(servicoPedido.listarOrdenadoPorNumero());
		} else if (opcao == 2) {
			servicoPedido.imprimirTabela(servicoPedido.listarOrdenadoPorTotal());
		} else {
			System.out.println("Opção inválida.");
		}
	}

	// =====================================================================
	// LEITURA DO TECLADO
	// =====================================================================

	private static String lerLinha(String mensagem) {
		System.out.print(mensagem);
		if (!teclado.hasNextLine()) {
			System.out.println("\nEntrada encerrada. Saindo...");
			System.exit(0);
		}
		return teclado.nextLine().trim();
	}

	private static String lerTexto(String mensagem) {
		while (true) {
			String s = lerLinha(mensagem);
			if (!s.isEmpty()) {
				return s;
			}
			System.out.println("Este campo é obrigatório.");
		}
	}

	private static String lerTextoOpcional(String mensagem) {
		String s = lerLinha(mensagem);
		return s.isEmpty() ? null : s;
	}

	private static int lerInt(String mensagem) {
		while (true) {
			try {
				return Integer.parseInt(lerLinha(mensagem));
			} catch (NumberFormatException e) {
				System.out.println("Digite um número inteiro válido.");
			}
		}
	}

	private static Integer lerIntOpcional(String mensagem) {
		while (true) {
			String s = lerLinha(mensagem);
			if (s.isEmpty()) {
				return null;
			}
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				System.out.println("Digite um número inteiro válido.");
			}
		}
	}

	private static double lerDouble(String mensagem) {
		while (true) {
			try {
				double valor = Double.parseDouble(lerLinha(mensagem).replace(',', '.'));
				if (valor >= 0) {
					return valor;
				}
				System.out.println("O valor não pode ser negativo.");
			} catch (NumberFormatException e) {
				System.out.println("Digite um número válido (ex.: 19,90).");
			}
		}
	}

	private static Double lerDoubleOpcional(String mensagem) {
		while (true) {
			String s = lerLinha(mensagem);
			if (s.isEmpty()) {
				return null;
			}
			try {
				double valor = Double.parseDouble(s.replace(',', '.'));
				if (valor >= 0) {
					return valor;
				}
				System.out.println("O valor não pode ser negativo.");
			} catch (NumberFormatException e) {
				System.out.println("Digite um número válido (ex.: 19,90).");
			}
		}
	}

	private static Tamanho lerTamanho(String mensagem) {
		while (true) {
			try {
				return Tamanho.valueOf(lerLinha(mensagem).toUpperCase());
			} catch (IllegalArgumentException e) {
				System.out.println("Tamanho inválido. Use PP, P, M, G ou GG.");
			}
		}
	}

	private static Tamanho lerTamanhoOpcional(String mensagem) {
		while (true) {
			String s = lerLinha(mensagem);
			if (s.isEmpty()) {
				return null;
			}
			try {
				return Tamanho.valueOf(s.toUpperCase());
			} catch (IllegalArgumentException e) {
				System.out.println("Tamanho inválido. Use PP, P, M, G ou GG.");
			}
		}
	}

	// =====================================================================
	// CARGA INICIAL (7 objetos de cada classe)
	// =====================================================================

	private static void carregarDadosIniciais() {
		// 7 Eletrônicos
		servicoProduto.incluir(new Eletronico(101, "Notebook Dell Inspiron", 3500.00, 15, 12));
		servicoProduto.incluir(new Eletronico(102, "Smartphone Galaxy A54", 1800.00, 30, 12));
		servicoProduto.incluir(new Eletronico(103, "Fone Bluetooth JBL", 250.00, 50, 6));
		servicoProduto.incluir(new Eletronico(104, "Smart TV 50 pol 4K", 2600.00, 12, 24));
		servicoProduto.incluir(new Eletronico(105, "Mouse Gamer Logitech", 180.00, 40, 12));
		servicoProduto.incluir(new Eletronico(106, "Teclado Mecânico Redragon", 320.00, 25, 12));
		servicoProduto.incluir(new Eletronico(107, "Carregador Turbo 65W", 120.00, 60, 6));

		// 7 Alimentícios (último parâmetro = dias para vencer)
		servicoProduto.incluir(new Alimenticio(201, "Arroz Tipo 1 5kg", 28.90, 100, 300));
		servicoProduto.incluir(new Alimenticio(202, "Feijão Carioca 1kg", 8.50, 120, 250));
		servicoProduto.incluir(new Alimenticio(203, "Leite Integral 1L", 5.20, 80, 5));
		servicoProduto.incluir(new Alimenticio(204, "Iogurte Natural 170g", 3.80, 60, 3));
		servicoProduto.incluir(new Alimenticio(205, "Pão de Forma 500g", 6.90, 45, 6));
		servicoProduto.incluir(new Alimenticio(206, "Queijo Mussarela kg", 42.00, 30, 20));
		servicoProduto.incluir(new Alimenticio(207, "Chocolate ao Leite 90g", 7.50, 90, 180));

		// 7 Vestuários
		servicoProduto.incluir(new Vestuario(301, "Camiseta Básica", 49.90, 70, Tamanho.M, "Branca"));
		servicoProduto.incluir(new Vestuario(302, "Calça Jeans", 129.90, 40, Tamanho.G, "Azul"));
		servicoProduto.incluir(new Vestuario(303, "Jaqueta Corta-Vento", 189.90, 25, Tamanho.GG, "Preta"));
		servicoProduto.incluir(new Vestuario(304, "Vestido Floral", 119.90, 30, Tamanho.P, "Estampado"));
		servicoProduto.incluir(new Vestuario(305, "Bermuda de Sarja", 79.90, 35, Tamanho.M, "Bege"));
		servicoProduto.incluir(new Vestuario(306, "Moletom Canguru", 139.90, 28, Tamanho.G, "Cinza"));
		servicoProduto.incluir(new Vestuario(307, "Meia Esportiva", 19.90, 150, Tamanho.PP, "Preta"));

		// 7 Clientes (nome, email, cpf, telefone)
		servicoCliente.incluir(new Cliente("Ana Souza", "ana.souza@email.com", "111.111.111-11", "(71) 99111-1111"));
		servicoCliente
				.incluir(new Cliente("Bruno Lima", "bruno.lima@email.com", "222.222.222-22", "(71) 99222-2222"));
		servicoCliente
				.incluir(new Cliente("Carla Mendes", "carla.mendes@email.com", "333.333.333-33", "(71) 99333-3333"));
		servicoCliente
				.incluir(new Cliente("Daniel Rocha", "daniel.rocha@email.com", "444.444.444-44", "(71) 99444-4444"));
		servicoCliente.incluir(
				new Cliente("Eduarda Nunes", "eduarda.nunes@email.com", "555.555.555-55", "(71) 99555-5555"));
		servicoCliente
				.incluir(new Cliente("Felipe Alves", "felipe.alves@email.com", "666.666.666-66", "(71) 99666-6666"));
		servicoCliente.incluir(
				new Cliente("Gabriela Costa", "gabriela.costa@email.com", "777.777.777-77", "(71) 99777-7777"));

		// 7 Pedidos (cada um com itens; o estoque diminui ao adicionar)
		criarPedidoInicial("111.111.111-11", "PIX", StatusPedido.PAGO, new int[][] { { 101, 1 }, { 107, 2 } });
		criarPedidoInicial("222.222.222-22", "Cartão de crédito", StatusPedido.ENVIADO,
				new int[][] { { 102, 1 }, { 103, 1 } });
		criarPedidoInicial("333.333.333-33", "Boleto", StatusPedido.PENDENTE,
				new int[][] { { 301, 2 }, { 302, 1 }, { 307, 4 } });
		criarPedidoInicial("444.444.444-44", "PIX", StatusPedido.ENTREGUE, new int[][] { { 201, 2 }, { 203, 6 } });
		criarPedidoInicial("555.555.555-55", "Cartão de débito", StatusPedido.CANCELADO,
				new int[][] { { 104, 1 } });
		criarPedidoInicial("666.666.666-66", "PIX", StatusPedido.PAGO,
				new int[][] { { 303, 1 }, { 305, 2 }, { 204, 10 } });
		criarPedidoInicial("777.777.777-77", "Cartão de crédito", StatusPedido.PENDENTE,
				new int[][] { { 105, 2 }, { 106, 1 }, { 205, 3 } });
	}

	private static void criarPedidoInicial(String cpf, String pagamento, StatusPedido status, int[][] itens) {
		Pedido pedido = servicoPedido.incluir(servicoCliente.buscarPorCpf(cpf), pagamento);
		for (int[] item : itens) {
			servicoPedido.adicionarItem(pedido, servicoProduto.buscarPorCodigo(item[0]), item[1]);
		}
		if (status != StatusPedido.PENDENTE) {
			servicoPedido.alterarStatus(pedido.getNumero(), status);
		}
	}
}
