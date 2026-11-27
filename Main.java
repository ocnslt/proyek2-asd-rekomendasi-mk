import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Proyek 2 Algoritma dan Struktur Data - Sistem Rekomendasi Pengambilan MK.
 *
 * Cara pakai:
 *   java Main matakuliah.csv prasyarat.csv mhs_01.txt
 * Jika argumen tidak diberikan, program akan menanyakan nama ketiga berkas.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String fMK;
        String fPrasyarat;
        String fMhs;
        if (args.length >= 3) {
            fMK = args[0];
            fPrasyarat = args[1];
            fMhs = args[2];
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("File matakuliah.csv : ");
            fMK = in.readLine().trim();
            System.out.print("File prasyarat.csv  : ");
            fPrasyarat = in.readLine().trim();
            System.out.print("File mahasiswa (txt): ");
            fMhs = in.readLine().trim();
        }

        // 1. Bangun graf dari kedua file CSV
        GrafPrasyarat graf = new GrafPrasyarat(64);
        bacaMataKuliah(fMK, graf);
        bacaPrasyarat(fPrasyarat, graf);

        // 2. Baca data mahasiswa
        int n = graf.jumlahMK();
        boolean[] lulus = new boolean[n];
        int semesterTujuan = bacaMahasiswa(fMhs, graf, lulus);

        // 3. Proses & tampilkan rekomendasi
        tampilkanHasil(graf, lulus, semesterTujuan);
    }

    // ---------- Pembacaan berkas ----------

    private static String bersihkan(String s) {
        // buang BOM (jika ada) dan spasi
        if (s.length() > 0 && s.charAt(0) == '\uFEFF') {
            s = s.substring(1);
        }
        return s.trim();
    }

    private static int parseTawaran(String s) {
        String t = s.trim().toLowerCase();
        if (t.equals("ganjil")) {
            return MataKuliah.GANJIL;
        }
        if (t.equals("genap")) {
            return MataKuliah.GENAP;
        }
        return MataKuliah.KEDUANYA; // "Keduanya"
    }

    // Format: Kode;Nama;sks;semester;ganjil/genap (baris pertama = header). 
    private static void bacaMataKuliah(String file, GrafPrasyarat graf) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String baris = br.readLine(); // lewati header
        while ((baris = br.readLine()) != null) {
            baris = bersihkan(baris);
            if (baris.length() == 0) {
                continue;
            }
            String[] f = baris.split(";");
            graf.tambahMK(new MataKuliah(f[0].trim(), f[1].trim(),
                    Integer.parseInt(f[2].trim()), Integer.parseInt(f[3].trim()),
                    parseTawaran(f[4])));
        }
        br.close();
    }

    // Format: prasyarat;matakuliah (baris pertama = header). 
    private static void bacaPrasyarat(String file, GrafPrasyarat graf) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String baris = br.readLine(); // lewati header
        while ((baris = br.readLine()) != null) {
            baris = bersihkan(baris);
            if (baris.length() == 0) {
                continue;
            }
            String[] f = baris.split(";");
            if (!graf.tambahPrasyarat(f[0].trim(), f[1].trim())) {
                System.err.println("Peringatan: kode tidak dikenal di prasyarat.csv -> " + baris);
            }
        }
        br.close();
    }

    /**
     * Format:
     *   semester_berikutnya=ganjil
     *   lulus=KODE1,KODE2,...
     * Mengisi array lulus dan mengembalikan semester tujuan.
     */
    private static int bacaMahasiswa(String file, GrafPrasyarat graf, boolean[] lulus)
            throws IOException {
        int semester = MataKuliah.GANJIL;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String baris;
        while ((baris = br.readLine()) != null) {
            baris = bersihkan(baris);
            int sama = baris.indexOf('=');
            if (sama < 0) {
                continue;
            }
            String kunci = baris.substring(0, sama).trim().toLowerCase();
            String isi = baris.substring(sama + 1).trim();
            if (kunci.equals("semester_berikutnya")) {
                semester = parseTawaran(isi);
            } else if (kunci.equals("lulus")) {
                String[] kode = isi.split(",");
                for (int i = 0; i < kode.length; i++) {
                    String k = kode[i].trim();
                    if (k.length() == 0) {
                        continue;
                    }
                    int idx = graf.cariIndeks(k);
                    if (idx < 0) {
                        System.err.println("Peringatan: kode MK lulus tidak dikenal -> " + k);
                    } else {
                        lulus[idx] = true;
                    }
                }
            }
        }
        br.close();
        return semester;
    }

    // ---------- Logika rekomendasi ----------

    private static void tampilkanHasil(GrafPrasyarat graf, boolean[] lulus, int semesterTujuan) {
        int n = graf.jumlahMK();
        DaftarBerantai bisa = new DaftarBerantai();
        DaftarBerantai belum = new DaftarBerantai();

        for (int i = 0; i < n; i++) {
            MataKuliah m = graf.ambilMK(i);
            if (lulus[i] || !m.ditawarkan(semesterTujuan)) {
                continue; // sudah lulus atau tidak dibuka semester depan
            }
            if (graf.semuaPrasyaratLulus(i, lulus)) {
                bisa.tambah(i);
            } else {
                belum.tambah(i);
            }
        }

        System.out.println();
        System.out.println("Daftar MK yang bisa diambil:");
        if (bisa.kosong()) {
            System.out.println("(tidak ada)");
        }
        int[] a = bisa.keArray();
        for (int k = 0; k < a.length; k++) {
            MataKuliah m = graf.ambilMK(a[k]);
            System.out.println((k + 1) + ". " + m.kode + " " + m.nama + " " + m.sks + " SKS");
        }

        System.out.println();
        System.out.println("Daftar MK yang belum bisa diambil semester depan:");
        if (belum.kosong()) {
            System.out.println("(tidak ada)");
        }
        int[] b = belum.keArray();
        for (int k = 0; k < b.length; k++) {
            MataKuliah m = graf.ambilMK(b[k]);
            boolean[] halang = graf.penghalang(b[k], lulus);
            StringBuilder alasan = new StringBuilder();
            for (int j = 0; j < n; j++) { // urutan indeks = urutan semester
                if (halang[j]) {
                    if (alasan.length() > 0) {
                        alasan.append(", ");
                    }
                    alasan.append(graf.ambilMK(j).kode);
                }
            }
            System.out.println(m.kode + ": belum lulus " + alasan);
        }
    }
}