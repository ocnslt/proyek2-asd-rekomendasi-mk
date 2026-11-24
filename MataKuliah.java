/**
 * Menyimpan data satu mata kuliah (satu baris matakuliah.csv).
 */
public class MataKuliah {
    public static final int GANJIL = 1;
    public static final int GENAP = 2;
    public static final int KEDUANYA = 3;

    public final String kode;
    public final String nama;
    public final int sks;
    public final int semester;
    public final int tawaran; // GANJIL, GENAP, atau KEDUANYA

    public MataKuliah(String kode, String nama, int sks, int semester, int tawaran) {
        this.kode = kode;
        this.nama = nama;
        this.sks = sks;
        this.semester = semester;
        this.tawaran = tawaran;
    }

    /** True jika MK ditawarkan pada semester yang diminta (GANJIL / GENAP). */
    public boolean ditawarkan(int semesterTujuan) {
        return tawaran == KEDUANYA || tawaran == semesterTujuan;
    }
}