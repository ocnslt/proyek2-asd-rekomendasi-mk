/**
 * Struktur data utama: graf berarah (directed graph) mata kuliah.
 * Setiap MK adalah simpul. Jika A adalah prasyarat B, ada sisi B -> A
 * (disimpan sebagai daftar prasyarat milik B) sehingga prasyarat sebuah MK
 * dapat dibaca langsung. Implementasi: array simpul + adjacency list
 * (DaftarBerantai) + TabelHash untuk pencarian kode -> indeks.
 */
public class GrafPrasyarat {

    private MataKuliah[] mk;          // simpul; urutan = urutan di matakuliah.csv (per semester)
    private DaftarBerantai[] prasyarat; // prasyarat[i] = daftar indeks prasyarat langsung MK i
    private TabelHash indeksKode;
    private int jumlah;

    public GrafPrasyarat(int kapasitas) {
        mk = new MataKuliah[kapasitas];
        prasyarat = new DaftarBerantai[kapasitas];
        indeksKode = new TabelHash();
    }

    public void tambahMK(MataKuliah m) {
        if (jumlah == mk.length) { // perbesar array jika penuh
            MataKuliah[] mkBaru = new MataKuliah[jumlah * 2];
            DaftarBerantai[] prBaru = new DaftarBerantai[jumlah * 2];
            for (int i = 0; i < jumlah; i++) {
                mkBaru[i] = mk[i];
                prBaru[i] = prasyarat[i];
            }
            mk = mkBaru;
            prasyarat = prBaru;
        }
        mk[jumlah] = m;
        prasyarat[jumlah] = new DaftarBerantai();
        indeksKode.taruh(m.kode, jumlah);
        jumlah++;
    }

    // Menambah sisi: kodePrasyarat harus lulus dulu sebelum kodeMK. 
    public boolean tambahPrasyarat(String kodePrasyarat, String kodeMK) {
        int p = indeksKode.ambil(kodePrasyarat);
        int m = indeksKode.ambil(kodeMK);
        if (p < 0 || m < 0) {
            return false;
        }
        prasyarat[m].tambah(p);
        return true;
    }

    public int jumlahMK() {
        return jumlah;
    }

    public MataKuliah ambilMK(int i) {
        return mk[i];
    }

    public int cariIndeks(String kode) {
        return indeksKode.ambil(kode);
    }

    // Syarat boleh diambil: semua prasyarat LANGSUNG sudah lulus. O(derajat).
    public boolean semuaPrasyaratLulus(int i, boolean[] lulus) {
        int[] p = prasyarat[i].keArray();
        for (int k = 0; k < p.length; k++) {
            if (!lulus[p[k]]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Mencari semua MK yang belum lulus dan menjadi penghalang MK i
     * (prasyarat langsung maupun tidak langsung) dengan DFS menelusuri sisi
     * prasyarat. Penelusuran berhenti di MK yang sudah lulus. Hasil berupa
     * array boolean per indeks; urutan indeks = urutan semester.
     * Kompleksitas O(V + E).
     */
    public boolean[] penghalang(int i, boolean[] lulus) {
        boolean[] halang = new boolean[jumlah];
        boolean[] dikunjungi = new boolean[jumlah];
        dfs(i, lulus, halang, dikunjungi);
        halang[i] = false; // MK itu sendiri bukan penghalang
        return halang;
    }

    private void dfs(int i, boolean[] lulus, boolean[] halang, boolean[] dikunjungi) {
        dikunjungi[i] = true;
        int[] p = prasyarat[i].keArray();
        for (int k = 0; k < p.length; k++) {
            int j = p[k];
            if (!lulus[j]) {
                halang[j] = true;
                if (!dikunjungi[j]) {
                    dfs(j, lulus, halang, dikunjungi);
                }
            }
        }
    }
}