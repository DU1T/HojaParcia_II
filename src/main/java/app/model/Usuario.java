package app.model;

public class Usuario
{
    //Atributos
    private final Integer id;
    private final String username;
    private final String nombre;
    private final String rol;
    private final int estado;

    //Constructor
    public Usuario(Integer id, String username, String nombre, String rol, int estado) {
        this.id = id; this.username = username; this.nombre = nombre; this.rol = rol; this.estado = estado;
    }
    //getters
    public Integer getId() { return id; }
    public String getUsername() { return username; }
    public String getNombre() { return nombre; }
    public String getRol() { return rol; }
    public int getEstado() { return estado; }
}
