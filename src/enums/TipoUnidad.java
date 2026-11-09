package enums;

public enum TipoUnidad {
    UNIVERSIDAD,
    FACULTAD,
    CARRERA,
    NIVEL,
    PARALELO;

    public boolean puedeTenerHijo(TipoUnidad hijo) {
        return hijo != null && hijo.ordinal() == this.ordinal() + 1;
    }
}
