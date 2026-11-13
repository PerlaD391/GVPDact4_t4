package mx.culturavibe.act4.dto;

public record AuthRespuesta(
		String token,
		String tipo,
		String email,
		String nombre
) {
	public static AuthRespuesta conToken(String token, String email, String nombre) {
		return new AuthRespuesta(token, "Bearer", email, nombre);
	}
}
