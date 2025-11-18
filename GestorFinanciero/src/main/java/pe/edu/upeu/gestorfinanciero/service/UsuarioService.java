package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    // ---------------------- REGISTRO ----------------------
    public Usuario registrar(String username, String email, String password) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        if (usuarioRepository.existsByUsername(username.trim())) {
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username.trim());
        usuario.setEmail(email.trim().toLowerCase());
        usuario.setPassword(passwordEncoder.encode(password.trim()));
        usuario.setFechaRegistro(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }


    // ----------------------- LOGIN ------------------------
    public Usuario login(String username, String password) {

        Optional<Usuario> opt = usuarioRepository.findByUsername(username.trim());

        if (opt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado.");
        }

        Usuario usuario = opt.get();

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta.");
        }

        return usuario;
    }


    // ---------------- CAMBIAR CONTRASEÑA -----------------
    public void cambiarPassword(Usuario usuario, String nuevaPassword) {
        usuario.setPassword(passwordEncoder.encode(nuevaPassword.trim()));
        usuarioRepository.save(usuario);
    }
    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}
