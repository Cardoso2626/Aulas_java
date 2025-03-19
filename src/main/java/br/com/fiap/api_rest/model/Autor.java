package br.com.fiap.api_rest.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "autor_livros",
            joinColumns = @JoinColumns(name = "id_livros", referencedColumn = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_autor", referencedColumnName = "id")
    )
    private List<Livro> livros;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }
}
