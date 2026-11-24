/**
 * Struktur data buatan sendiri: tabel hash (separate chaining) yang memetakan
 * kode MK (String) ke indeks MK (int). Pencarian rata-rata O(1).
 */
public class TabelHash {

    private static class Entri {
        String kunci;
        int nilai;
        Entri berikut;

        Entri(String kunci, int nilai, Entri berikut) {
            this.kunci = kunci;
            this.nilai = nilai;
            this.berikut = berikut;
        }
    }

    private Entri[] bucket;
    private int jumlah;

    public TabelHash() {
        bucket = new Entri[16];
    }

    private int hash(String kunci, int panjangTabel) {
        int h = 7;
        for (int i = 0; i < kunci.length(); i++) {
            h = h * 31 + kunci.charAt(i);
        }
        if (h < 0) {
            h = -h;
        }
        return h % panjangTabel;
    }

    /** Menyimpan pasangan kunci-nilai (menimpa jika kunci sudah ada). */
    public void taruh(String kunci, int nilai) {
        int b = hash(kunci, bucket.length);
        for (Entri e = bucket[b]; e != null; e = e.berikut) {
            if (e.kunci.equals(kunci)) {
                e.nilai = nilai;
                return;
            }
        }
        bucket[b] = new Entri(kunci, nilai, bucket[b]);
        jumlah++;
        if (jumlah > bucket.length * 3 / 4) {
            perbesar();
        }
    }

    /** Mengembalikan nilai untuk kunci, atau -1 jika tidak ada. */
    public int ambil(String kunci) {
        int b = hash(kunci, bucket.length);
        for (Entri e = bucket[b]; e != null; e = e.berikut) {
            if (e.kunci.equals(kunci)) {
                return e.nilai;
            }
        }
        return -1;
    }

    private void perbesar() {
        Entri[] baru = new Entri[bucket.length * 2];
        for (int i = 0; i < bucket.length; i++) {
            Entri e = bucket[i];
            while (e != null) {
                Entri berikut = e.berikut;
                int b = hash(e.kunci, baru.length);
                e.berikut = baru[b];
                baru[b] = e;
                e = berikut;
            }
        }
        bucket = baru;
    }
}