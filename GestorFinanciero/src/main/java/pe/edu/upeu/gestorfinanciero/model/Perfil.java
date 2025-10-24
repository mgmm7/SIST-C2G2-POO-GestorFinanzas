package pe.edu.upeu.gestorfinanciero.model;

import jakarta.persistence.*;
import lombok.Data;
    @Data
    @Entity
    @Table(name = "upeu_perfil")

    public class Perfil {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Basic(optional = false)
        @Column(name = "id_perfil", nullable = false)
        private Long idPerfil;
        //@Size(max = 20)
        private String nombre;
        //@Size(max = 6)
        private String codigo;
}
