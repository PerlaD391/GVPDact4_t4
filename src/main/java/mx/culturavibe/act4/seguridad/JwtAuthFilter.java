package mx.culturavibe.act4.seguridad;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.culturavibe.act4.servicio.JwtService;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	private final UserDetailsService userDetailsService;

	public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String encabezado = request.getHeader("Authorization");
		if (encabezado == null || !encabezado.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		String token = encabezado.substring(7);
		try {
			String email = jwtService.extraerEmail(token);
			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails usuario = userDetailsService.loadUserByUsername(email);
				if (jwtService.esValido(token, usuario.getUsername())) {
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
							usuario, null, usuario.getAuthorities());
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		} catch (Exception e) {
			SecurityContextHolder.clearContext();
		}
		filterChain.doFilter(request, response);
	}
}
