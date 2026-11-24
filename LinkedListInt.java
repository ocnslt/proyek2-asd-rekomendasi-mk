/**
 * Struktur data buatan sendiri: singly linked list yang menyimpan bilangan bulat
 * (indeks mata kuliah). Dipakai sebagai "adjacency list" pada graf prasyarat
 * dan sebagai rantai (bucket) pada tabel hash.
 */
public class LinkedListInt {

    private static class Simpul {
        int nilai;
        Simpul berikut;

        Simpul(int nilai) {
            this.nilai = nilai;
        }
    }

    private Simpul kepala;
    private Simpul ekor;
    private int ukuran;

    /** Menambah elemen di akhir list. O(1). */
    public void tambah(int nilai) {
        Simpul baru = new Simpul(nilai);
        if (kepala == null) {
            kepala = baru;
            ekor = baru;
        } else {
            ekor.berikut = baru;
            ekor = baru;
        }
        ukuran++;
    }

    public int ukuran() {
        return ukuran;
    }

    public boolean kosong() {
        return ukuran == 0;
    }

    /** Mengambil elemen ke-i (mulai dari 0). O(n). */
    public int ambil(int indeks) {
        Simpul s = kepala;
        for (int i = 0; i < indeks; i++) {
            s = s.berikut;
        }
        return s.nilai;
    }

    /** Mengubah isi list menjadi array agar mudah di-loop. O(n). */
    public int[] keArray() {
        int[] hasil = new int[ukuran];
        Simpul s = kepala;
        int i = 0;
        while (s != null) {
            hasil[i++] = s.nilai;
            s = s.berikut;
        }
        return hasil;
    }
}