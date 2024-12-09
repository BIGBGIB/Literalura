package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.*;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivroRepository;

import br.com.alura.literalura.service.ConsumoApi;
import br.com.alura.literalura.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String endereco = "https://gutendex.com/books?search=";

    private LivroRepository repositorioLivro;
    private AutorRepository repositorioAutor;

    private List<Livro> livros = new ArrayList<>();
    private List<Autor> autores = new ArrayList<>();

    @Autowired
    public Principal(LivroRepository repositorioLivro, AutorRepository repositorioAutor) {
        this.repositorioLivro = repositorioLivro;
        this.repositorioAutor = repositorioAutor;
    }

    public void  exibeMenu(){
        var opcao = -1;
        while (opcao != 0) {
            System.out.println("\n Bem vindo ao Literalura!");
            var menu = """
                    1 - Buscar lívro pelo título
                    2 - Lista lívros registrados
                    3 - Listar autores registrados
                    4 - Listar livros por idioma
                    5 - Listar autores vivos em determinado ano
                    
                    0 - Sair
                    """;
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine(); //limpa a caixa de entrada

            switch (opcao) {
                case 1:
                    buscarLivroWeb();
                    break;
                case 2:
                    listarLivrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarLivrosPorIdioma();
                    break;
                case 5:
                    listarAutoresVivosEmDeterminadoAno();
                    break;
                case 0:
                    System.out.println("Até breve!");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarLivroWeb() {

        DadosLivro dadosLivro = getDadosLivro();

        Optional<Livro> livroExistente = repositorioLivro.findByTituloContainingIgnoreCase(dadosLivro.titulo());
        if (livroExistente.isPresent()){
            System.out.println("Esse lívro ja está cadastrado!");
            listarLivrosRegistrados();
            return;
        }

        Autor autor = new Autor().pegaAutor(dadosLivro);
        Optional<Autor> autorExistente = repositorioAutor.findByNome(autor.getNome());
        if (autorExistente.isPresent()) {
            autor = autorExistente.get();
        } else {
            autor = repositorioAutor.save(autor); // Salva o autor se for novo
        }

        Livro livro = new Livro(dadosLivro);
        livro.setAutor(autor);
        autor.getLivros().add(livro);
        repositorioLivro.save(livro);;
        System.out.println(livro);

    }

    private DadosLivro getDadosLivro() {

        System.out.println("Digite o título do lívro para busca:");
        var titulo = leitura.nextLine();
        var json = consumoApi.obterDados(endereco + titulo.toLowerCase().replace(" ", "+"));
        DadosResult dadosResult = conversor.obterDados(json, DadosResult.class);
        var dadosFinal = dadosResult.dadosDoResult().get(0);

        return dadosFinal;
    }

    private void listarLivrosRegistrados() {

        livros = repositorioLivro.findAll();
        livros.forEach(System.out::println);

    }

    private void listarAutoresRegistrados() {

      autores = repositorioAutor.findAll();
      autores.forEach(System.out::println);

    }

    private void listarLivrosPorIdioma() {

       System.out.println(""" 
                Informe o idioma desejado:
                en - inglês
                es - espanhol
                fr - francês
                pt - português
                """);
        var idiomaInformado = leitura.nextLine();
        List<Livro> livrosPorIdioma = repositorioLivro.livrosPorIdioma(idiomaInformado.toLowerCase());
        if (livrosPorIdioma.isEmpty()){
            System.out.println("Nenhum livro encontrado");
        } else {
            System.out.println("Livros do idioma " + idiomaInformado);
            livrosPorIdioma.forEach(System.out::println);
        }

    }

    private void listarAutoresVivosEmDeterminadoAno() {

        System.out.println("Informe o ano: ");
        var ano = leitura.nextInt();
        List<Autor> autoresVivosEmDeterminadoAno = repositorioAutor.autoresVivosEmDeterminadoAno(ano);
        if (autoresVivosEmDeterminadoAno.isEmpty()) {
            System.out.println("Nenhum autor encontrado");
        } else {
            System.out.println("Autores vivos em " + ano);
            autoresVivosEmDeterminadoAno.forEach(System.out::println);
        }


    }
}
