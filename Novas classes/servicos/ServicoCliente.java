package servicos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import entidades.Cliente;

public class ServicoCliente {
	private ArrayList<Cliente> clientes = new ArrayList<>();

	public boolean incluir(Cliente cliente) {
		if (cliente == null || buscarPorCpf(cliente.getCpf()) != null) {
			return false;
		}
		clientes.add(cliente);
		return true;
	}

	// Parâmetro nulo = manter o valor atual
	public boolean alterar(String cpf, String nome, String email, String telefone) {
		Cliente c = buscarPorCpf(cpf);
		if (c == null) {
			return false;
		}
		if (nome != null) {
			c.setNome(nome);
		}
		if (email != null) {
			c.setEmail(email);
		}
		if (telefone != null) {
			c.setTelefone(telefone);
		}
		return true;
	}

	public boolean excluir(String cpf) {
		Cliente c = buscarPorCpf(cpf);
		return c != null && clientes.remove(c);
	}

	public Cliente buscarPorCpf(String cpf) {
		return clientes.stream().filter(c -> c.getCpf().equals(cpf)).findFirst().orElse(null);
	}

	public List<Cliente> buscarPorNome(String trecho) {
		String t = trecho.toLowerCase();
		return clientes.stream().filter(c -> c.getNome().toLowerCase().contains(t)).toList();
	}

	public List<Cliente> listar() {
		return new ArrayList<>(clientes);
	}

	public List<Cliente> listarOrdenadoPorNome() {
		return clientes.stream().sorted(Comparator.comparing(Cliente::getNome, String.CASE_INSENSITIVE_ORDER))
				.toList();
	}

	public void imprimirTabela(List<Cliente> lista) {
		if (lista.isEmpty()) {
			System.out.println("Nenhum cliente encontrado.");
			return;
		}
		String formato = "%-16s %-28s %-32s %-16s%n";
		System.out.printf(formato, "CPF", "NOME", "E-MAIL", "TELEFONE");
		System.out.println("-".repeat(95));
		for (Cliente c : lista) {
			System.out.printf(formato, c.getCpf(), c.getNome(), c.getEmail(), c.getTelefone());
		}
	}
}
