package br.com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nome;
    private Integer dataDeNascimento;
    private Integer dataDeFalecimento;

    @OneToMany (mappedBy = "autor", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Livro> livros = new ArrayList<>();

    public Autor() {}

    public Autor(DadosAuthors dadosAuthors){

        this.nome =  dadosAuthors.nome();
        this.dataDeNascimento = dadosAuthors.dataNascimento();
        this.dataDeFalecimento = dadosAuthors.dataMorte();

    }

    public Autor pegaAutor(DadosLivro dadosLivro){
        DadosAuthors dadosAuthors = dadosLivro.dadosAutor().get(0);
        return new Autor(dadosAuthors);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getDataDeNascimento() {
        return dataDeNascimento;
    }

    public void setDataDeNascimento(int dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public int getDataDeFalecimento() {
        return dataDeFalecimento;
    }

    public void setDataDeFalecimento(int dataDeFalecimento) {
        this.dataDeFalecimento = dataDeFalecimento;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        livros.forEach(l -> l.setAutor(this));
        this.livros = livros;
    }

    @Override
    public String toString() {
        String nomeLivros = livros.stream()
                .map(Livro::getTitulo)
                .collect(Collectors.joining(", "));

        return "Autor: " + nome + "\n" +
                " Ano de nascimento: " + dataDeNascimento + "," +
                " Ano de falecimento: " + dataDeFalecimento + "," +
                " Livros: " + nomeLivros;
    }


}
