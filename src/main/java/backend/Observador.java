package backend;

public interface Observador {
    void alRecibir(Evento evento, Object datos);
}
