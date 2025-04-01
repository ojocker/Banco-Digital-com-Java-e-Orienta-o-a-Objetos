// Interface para operações bancárias básicas

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

interface IConta {
    void sacar(double valor);
    void depositar(double valor);
    void transferir(double valor, IConta contaDestino);
    void imprimirExtrato();
}

// Classe abstrata que implementa a interface e define comportamentos comuns
abstract class Conta implements IConta {
    private static final int AGENCIA_PADRAO = 1;
    private static int SEQUENCIAL = 1;

    protected int agencia;
    protected int numero;
    protected double saldo;
    protected Cliente cliente;

    public Conta(Cliente cliente) {
        this.agencia = Conta.AGENCIA_PADRAO;
        this.numero = SEQUENCIAL++;
        this.cliente = cliente;
    }

    @Override
    public void sacar(double valor) {
        if (valor > saldo) {
            System.out.println("Saldo insuficiente para saque.");
            return;
        }
        saldo -= valor;
    }

    @Override
    public void depositar(double valor) {
        if (valor <= 0) {
            System.out.println("Valor de depósito inválido.");
            return;
        }
        saldo += valor;
    }

    @Override
    public void transferir(double valor, IConta contaDestino) {
        if (valor > saldo) {
            System.out.println("Saldo insuficiente para transferência.");
            return;
        }
        this.sacar(valor);
        contaDestino.depositar(valor);
    }

    protected void imprimirInfosComuns() {
        System.out.println(String.format("Titular: %s", this.cliente.getNome()));
        System.out.println(String.format("Agência: %d", this.agencia));
        System.out.println(String.format("Número: %d", this.numero));
        System.out.println(String.format("Saldo: R$ %.2f", this.saldo));
    }

    // Getters
    public int getAgencia() {
        return agencia;
    }

    public int getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }
}

// Implementação específica para Conta Corrente
class ContaCorrente extends Conta {
    private static final double TAXA_MANUTENCAO = 12.50;

    public ContaCorrente(Cliente cliente) {
        super(cliente);
    }

    @Override
    public void imprimirExtrato() {
        System.out.println("=== Extrato Conta Corrente ===");
        super.imprimirInfosComuns();
        System.out.println(String.format("Taxa de manutenção mensal: R$ %.2f", TAXA_MANUTENCAO));
    }

    // Método específico da Conta Corrente
    public void cobrarTaxaManutencao() {
        this.saldo -= TAXA_MANUTENCAO;
        System.out.println(String.format("Taxa de manutenção de R$ %.2f cobrada com sucesso.", TAXA_MANUTENCAO));
    }
}

// Implementação específica para Conta Poupança
class ContaPoupanca extends Conta {
    private static final double TAXA_JUROS_MENSAL = 0.004; // 0.4% ao mês

    public ContaPoupanca(Cliente cliente) {
        super(cliente);
    }

    @Override
    public void imprimirExtrato() {
        System.out.println("=== Extrato Conta Poupança ===");
        super.imprimirInfosComuns();
        System.out.println(String.format("Taxa de juros mensal: %.2f%%", TAXA_JUROS_MENSAL * 100));
    }

    // Método específico da Conta Poupança
    public void calcularRendimentoMensal() {
        double rendimento = this.saldo * TAXA_JUROS_MENSAL;
        this.saldo += rendimento;
        System.out.println(String.format("Rendimento mensal de R$ %.2f creditado com sucesso.", rendimento));
    }
}

// Classe para representar os clientes do banco
class Cliente {
    private String nome;
    private String cpf;
    private String email;

    public Cliente(String nome, String cpf, String email) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

// Classe para representar o banco
class Banco {
    private String nome;
    private List<Conta> contas = new ArrayList<>();

    public Banco(String nome) {
        this.nome = nome;
    }

    public void adicionarConta(Conta conta) {
        this.contas.add(conta);
    }

    public List<Conta> getContas() {
        return contas;
    }

    public String getNome() {
        return nome;
    }

    public void listarClientes() {
        System.out.println("=== Clientes do " + this.nome + " ===");
        
        // Usando Set para evitar duplicidade de clientes
        Set<Cliente> clientes = new HashSet<>();
        for (Conta conta : this.contas) {
            clientes.add(conta.cliente);
        }
        
        for (Cliente cliente : clientes) {
            System.out.println("Nome: " + cliente.getNome() + " | CPF: " + cliente.getCpf());
        }
    }
}

// Classe principal para testar o sistema
public class BancoDigital {
    public static void main(String[] args) {
        // Criando o banco
        Banco banco = new Banco("Banco Digital");
        
        // Criando clientes
        Cliente joao = new Cliente("João da Silva", "123.456.789-00", "joao@example.com");
        Cliente maria = new Cliente("Maria Oliveira", "987.654.321-00", "maria@example.com");
        
        // Criando contas
        Conta contaCorrenteJoao = new ContaCorrente(joao);
        Conta contaPoupancaJoao = new ContaPoupanca(joao);
        Conta contaCorrenteMaria = new ContaCorrente(maria);
        
        // Adicionando contas ao banco
        banco.adicionarConta(contaCorrenteJoao);
        banco.adicionarConta(contaPoupancaJoao);
        banco.adicionarConta(contaCorrenteMaria);
        
        // Realizando operações
        contaCorrenteJoao.depositar(1000);
        contaCorrenteJoao.transferir(300, contaPoupancaJoao);
        contaCorrenteMaria.depositar(1500);
        contaCorrenteMaria.transferir(500, contaCorrenteJoao);
        
        // Operações específicas de cada tipo de conta
        ((ContaCorrente) contaCorrenteJoao).cobrarTaxaManutencao();
        ((ContaPoupanca) contaPoupancaJoao).calcularRendimentoMensal();
        
        // Exibindo extratos
        System.out.println("\n=== Extratos ===\n");
        contaCorrenteJoao.imprimirExtrato();
        System.out.println();
        contaPoupancaJoao.imprimirExtrato();
        System.out.println();
        contaCorrenteMaria.imprimirExtrato();
        
        // Listando clientes do banco
        System.out.println();
        banco.listarClientes();
    }
}