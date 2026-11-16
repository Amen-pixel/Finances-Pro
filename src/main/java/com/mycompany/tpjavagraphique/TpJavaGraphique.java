package com.mycompany.tpjavagraphique;



import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * FINANCES PRO 2.0 — gestionnaire de budgets multi-projets (Swing, 1 seul fichier, 0 dépendance).
 * Données : dossier ~/FinancesPro (un CSV par projet, config, rapports, sauvegardes).
 */
public class TpJavaGraphique extends JFrame {

    static final String VERSION = "2.0";

    // ===================== PALETTE & OUTILS =====================
    static boolean CLAIR = false;
    static Color BG, PANEL, HEAD, LINE, TXT, TXT2, INPUT, GREEN, RED, BLUE, PURPLE, ORANGE, YELLOW,
            SUR, BG1, BG2, GRID, AXE, TRACK, TILE, TBL, TGRID, THEAD, ZEBRA, CHAMP, SEL;
    static Color[] COULEURS, PAL;

    /** Applique le thème : sombre « néon » ou clair « institutionnel ». */
    static void appliquerTheme(boolean clair) {
        CLAIR = clair;
        if (clair) {
            BG = new Color(233, 237, 245); PANEL = Color.WHITE; HEAD = new Color(247, 249, 253); LINE = new Color(185, 195, 215);
            TXT = new Color(18, 30, 60); TXT2 = new Color(80, 92, 120); INPUT = new Color(240, 243, 249);
            GREEN = new Color(30, 140, 90); RED = new Color(190, 50, 60); BLUE = new Color(25, 95, 185); PURPLE = new Color(95, 75, 150);
            ORANGE = new Color(205, 125, 25); YELLOW = new Color(180, 140, 10); SUR = Color.WHITE;
            BG1 = new Color(236, 240, 248); BG2 = new Color(212, 222, 241); GRID = new Color(225, 230, 242); AXE = new Color(150, 160, 185);
            TRACK = new Color(222, 228, 240); TILE = new Color(247, 249, 253); TBL = Color.WHITE; TGRID = new Color(215, 221, 235);
            THEAD = new Color(225, 231, 243); ZEBRA = new Color(246, 248, 252); CHAMP = new Color(150, 162, 190); SEL = new Color(60, 120, 200);
        } else {
            BG = new Color(6, 6, 12); PANEL = new Color(16, 16, 26); HEAD = new Color(10, 10, 18); LINE = new Color(70, 80, 115);
            TXT = Color.WHITE; TXT2 = new Color(210, 220, 250); INPUT = new Color(30, 32, 48);
            GREEN = new Color(0, 255, 140); RED = new Color(255, 40, 100); BLUE = new Color(0, 180, 255); PURPLE = new Color(200, 60, 255);
            ORANGE = new Color(255, 150, 0); YELLOW = new Color(255, 220, 0); SUR = Color.BLACK;
            BG1 = new Color(8, 8, 20); BG2 = new Color(24, 8, 36); GRID = new Color(40, 45, 65); AXE = new Color(100, 110, 150);
            TRACK = new Color(25, 25, 38); TILE = new Color(22, 24, 38); TBL = new Color(12, 12, 18); TGRID = new Color(50, 60, 90);
            THEAD = new Color(25, 25, 40); ZEBRA = new Color(22, 22, 32); CHAMP = CHAMP; SEL = new Color(0, 140, 255);
        }
        COULEURS = new Color[]{GREEN, BLUE, PURPLE, ORANGE, RED, YELLOW};
        PAL = new Color[]{GREEN, BLUE, PURPLE, ORANGE, RED, YELLOW, new Color(255, 90, 180), new Color(0, 200, 190), new Color(120, 190, 40), clair ? new Color(110, 120, 140) : Color.WHITE};
        UIManager.put("OptionPane.background", PANEL);
        UIManager.put("Panel.background", PANEL);
        UIManager.put("OptionPane.messageForeground", TXT);
        UIManager.put("Label.foreground", TXT);
        UIManager.put("Button.background", INPUT);
        UIManager.put("Button.foreground", TXT);
        UIManager.put("ToolTip.background", INPUT);
        UIManager.put("ToolTip.foreground", TXT);
        UIManager.put("ProgressBar.selectionForeground", TXT);
        UIManager.put("ProgressBar.selectionBackground", TXT);
    }

    static { appliquerTheme(false); }

    static Color arc(float h) { return CLAIR ? Color.getHSBColor(0.58f + 0.07f * (float) Math.sin(h * 6.28), 0.9f, 0.55f) : Color.getHSBColor(h, 0.7f, 1f); }
    static final String[] NOMS_COULEURS = {"Vert", "Bleu", "Violet", "Orange", "Rouge", "Jaune"};
    static final String[] ICONES = {"Épargne", "Objectif", "Maison", "Voiture", "Études", "Informatique", "Voyage", "Courses", "Téléphone", "Cadeau", "Santé", "Croissance"};
    static String typeProjet(String ic) { for (String s : ICONES) if (s.equals(ic)) return s; return ""; }
    static final String[] TYPES = {"Revenu", "Dépense", "Épargne"};
    static final String[] CATS = {"Bourse / Salaire", "Projet Tech / Freelance", "Alimentation", "Transport",
        "Internet & Électricité", "Fonds d'Urgence", "Loyer", "Santé", "Loisirs", "Éducation", "Autre"};

    static final File DOSSIER = new File(System.getProperty("user.home"), "FinancesPro");
    static final File INDEX = new File(DOSSIER, "projets.csv");
    static final File FCFG = new File(DOSSIER, "config.properties");
    static final Properties CFG = new Properties();
    static String DEV = "FCFA";
    static final DecimalFormat FMT;
    static {
        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.FRANCE);
        s.setGroupingSeparator(' ');
        FMT = new DecimalFormat("#,##0", s);
    }
    static final DateTimeFormatter FDATE = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy  •  HH:mm:ss", Locale.FRENCH);

    static Font f(int style, int size) { return new Font("Segoe UI", style, size); }
    static Color alpha(Color c, int a) { return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, a))); }
    static String cut(String s, int n) { return s.length() > n ? s.substring(0, Math.max(1, n - 1)) + "…" : s; }
    static void centre(Graphics2D g, String s, int cx, int y) { g.drawString(s, cx - g.getFontMetrics().stringWidth(s) / 2, y); }
    static String esc(String s) { return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"); }
    static String slug(String s) { return s.replaceAll("[^\\p{L}\\p{N}]+", "_"); }
    static String org() { return CFG.getProperty("nom", "").trim(); }
    static boolean pinActif() { return CFG.getProperty("pin") != null; }

    static double nombre(String s) {
        double v = Double.parseDouble(s.trim().replace(" ", "").replace("\u00A0", "").replace("\u202F", "").replace(",", "."));
        if (Double.isNaN(v) || Double.isInfinite(v)) throw new NumberFormatException();
        return v;
    }

    static JLabel label(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    static JButton bouton(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(f(Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static Border bordureChamp(Color c) {
        return new CompoundBorder(new LineBorder(c, 2), new EmptyBorder(2, 8, 2, 8));
    }

    static void styler(JComponent c) {
        c.setBackground(INPUT);
        c.setForeground(TXT);
        c.setFont(f(Font.PLAIN, 13));
        if (c instanceof JTextField) {
            JTextField tf = (JTextField) c;
            tf.setCaretColor(YELLOW);
            tf.setBorder(bordureChamp(CHAMP));
            tf.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { tf.setBorder(bordureChamp(GREEN)); }
                @Override public void focusLost(FocusEvent e) { tf.setBorder(bordureChamp(CHAMP)); }
            });
        } else if (c instanceof JComboBox) {
            JComboBox<?> cb = (JComboBox<?>) c;
            cb.setBorder(new LineBorder(CHAMP, 2));
            cb.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
                    Component r = super.getListCellRendererComponent(l, v, i, sel, foc);
                    r.setBackground(sel ? BLUE : INPUT);
                    r.setForeground(sel ? SUR : TXT);
                    return r;
                }
            });
            if (cb.isEditable()) {
                Component ed = cb.getEditor().getEditorComponent();
                ed.setBackground(INPUT);
                ed.setForeground(TXT);
                if (ed instanceof JTextField) ((JTextField) ed).setCaretColor(YELLOW);
            }
        }
        c.setPreferredSize(new Dimension(Math.max(60, c.getPreferredSize().width), 32));
    }

    static JTextArea zoneTexte(Color c) {
        JTextArea a = new JTextArea(3, 20);
        a.setEditable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setOpaque(false);
        a.setForeground(c);
        a.setFont(f(Font.BOLD, 15));
        a.setAlignmentX(Component.LEFT_ALIGNMENT);
        return a;
    }

    static Color scoreCouleur(double s) { return s >= 70 ? GREEN : s >= 45 ? ORANGE : RED; }
    static String scoreTexte(double s) { return s >= 80 ? "Excellent" : s >= 60 ? "Bon" : s >= 40 ? "À surveiller" : "Critique"; }

    // ===================== CONFIG / SÉCURITÉ =====================
    static void chargerCfg() {
        try (java.io.Reader r = Files.newBufferedReader(FCFG.toPath(), StandardCharsets.UTF_8)) { CFG.load(r); } catch (Exception ignored) { }
        DEV = CFG.getProperty("devise", "FCFA");
    }

    static void sauverCfg() {
        try (java.io.Writer w = Files.newBufferedWriter(FCFG.toPath(), StandardCharsets.UTF_8)) { CFG.store(w, "Finances Pro"); } catch (Exception ignored) { }
    }

    static String hash(String pin) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(("fp#2026#" + pin).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return ""; }
    }

    // ===================== DONNÉES =====================
    static class Projet {
        String id, nom, icone;
        int couleur;
        double budget, objectif;
    }

    final List<Projet> projets = new ArrayList<>();

    static void ecrire(File fi, List<String> l) throws IOException {
        File tmp = new File(fi.getPath() + ".tmp");
        Files.write(tmp.toPath(), l, StandardCharsets.UTF_8);
        Files.move(tmp.toPath(), fi.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    void sauverProjets() {
        List<String> l = new ArrayList<>();
        for (Projet p : projets)
            l.add(p.id + ";" + p.nom.replace(";", ",") + ";" + p.icone + ";" + p.couleur + ";" + (long) p.budget + ";" + (long) p.objectif);
        try {
            DOSSIER.mkdirs();
            ecrire(INDEX, l);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Sauvegarde impossible : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    static List<Projet> chargerProjets() {
        List<Projet> l = new ArrayList<>();
        if (!INDEX.exists()) return l;
        try {
            for (String s : Files.readAllLines(INDEX.toPath(), StandardCharsets.UTF_8)) {
                try {
                    String[] a = s.split(";", -1);
                    if (a.length < 6) continue;
                    Projet p = new Projet();
                    p.id = a[0]; p.nom = a[1]; p.icone = a[2];
                    p.couleur = Math.floorMod(Integer.parseInt(a[3]), COULEURS.length);
                    p.budget = Double.parseDouble(a[4]);
                    p.objectif = Double.parseDouble(a[5]);
                    l.add(p);
                } catch (Exception ignored) { }
            }
        } catch (Exception ignored) { }
        return l;
    }

    static Object[] parseLigne(String s) {
        try {
            String[] a = s.replace("\uFEFF", "").split(";", -1);
            if (a.length < 5) return null;
            LocalDate.parse(a[0].trim());
            String t = a[3].trim();
            if (!Arrays.asList(TYPES).contains(t)) return null;
            return new Object[]{a[0].trim(), a[1].trim(), a[2].trim(), t, Long.valueOf(Math.round(nombre(a[4])))};
        } catch (Exception e) {
            return null;
        }
    }

    static List<Object[]> lireFichier(File fi) {
        List<Object[]> l = new ArrayList<>();
        if (!fi.exists()) return l;
        try {
            for (String s : Files.readAllLines(fi.toPath(), StandardCharsets.UTF_8)) {
                Object[] o = parseLigne(s);
                if (o != null) l.add(o);
            }
        } catch (Exception ignored) { }
        return l;
    }

    static List<Object[]> lireOps(String id) { return lireFichier(new File(DOSSIER, id + ".csv")); }

    static List<String> versLignes(DefaultTableModel m) {
        List<String> l = new ArrayList<>();
        for (int i = 0; i < m.getRowCount(); i++)
            l.add(m.getValueAt(i, 0) + ";" + m.getValueAt(i, 1).toString().replace(";", ",").replace("\n", " ") + ";"
                    + m.getValueAt(i, 2).toString().replace(";", ",") + ";" + m.getValueAt(i, 3) + ";" + m.getValueAt(i, 4));
        return l;
    }

    static double[] stats(Projet p) {
        double rev = 0, dep = 0, epa = 0;
        List<Object[]> ops = lireOps(p.id);
        for (Object[] o : ops) {
            double m = ((Number) o[4]).doubleValue();
            if ("Revenu".equals(o[3])) rev += m;
            else if ("Dépense".equals(o[3])) dep += m;
            else epa += m;
        }
        return new double[]{rev, dep, epa, ops.size()};
    }

    // ===================== RAPPORTS HTML (imprimables / PDF) =====================
    static String css() {
        return "<style>body{font-family:'Segoe UI',Arial,sans-serif;margin:0;background:#eef1f8;color:#1b2340}"
                + ".page{max-width:980px;margin:24px auto;background:#fff;padding:36px 44px;box-shadow:0 4px 24px rgba(0,0,0,.15)}"
                + "h1{margin:0;color:#0b2a6f}h2{border-bottom:3px solid #0b2a6f;padding-bottom:6px;margin-top:34px;color:#0b2a6f}"
                + ".kp{display:flex;gap:12px;margin-top:18px}.k{flex:1;border-radius:10px;padding:14px;color:#000}.k b{display:block;font-size:21px;margin-top:4px}"
                + "table{width:100%;border-collapse:collapse;font-size:13px}th{background:#0b2a6f;color:#fff;text-align:left;padding:8px}"
                + "td{padding:7px 8px;border-bottom:1px solid #e0e4ef}.bar{background:#e6eaf5;border-radius:6px;height:14px}"
                + ".bar i{display:block;height:14px;border-radius:6px;background:#0b6fff}.r{text-align:right}.noprint{margin:0 0 16px}"
                + ".pied{margin-top:40px;font-size:11px;color:#667;text-align:center}"
                + "@media print{.noprint{display:none}body{background:#fff}.page{box-shadow:none;margin:0;max-width:none}}</style>";
    }

    static String entete(String titre, String sous) {
        return "<!DOCTYPE html><html lang='fr'><head><meta charset='UTF-8'><title>" + esc(titre) + "</title>" + css()
                + "</head><body><div class='page'><div class='noprint'><button onclick='window.print()' style='padding:8px 16px;font-size:14px'>🖨 Imprimer / Enregistrer en PDF</button></div>"
                + "<h1>" + esc(titre) + "</h1><p style='color:#556'>" + esc(sous) + "</p>";
    }

    static String pied() {
        return "<div class='pied'>Document généré par Finances Pro " + VERSION + " le " + LocalDate.now() + (org().isEmpty() ? "" : " — " + esc(org())) + "</div></div></body></html>";
    }

    static String kpi(String t, String v, String col) {
        return "<div class='k' style='background:" + col + "'>" + t + "<b>" + v + "</b></div>";
    }

    static String rapportHtml(Projet p, List<Object[]> ops) {
        double rev = 0, dep = 0, epa = 0;
        Map<String, Double> cats = new HashMap<>();
        TreeMap<String, double[]> mois = new TreeMap<>();
        for (Object[] o : ops) {
            double m = ((Number) o[4]).doubleValue();
            int t = Arrays.asList(TYPES).indexOf(o[3]);
            mois.computeIfAbsent(o[0].toString().substring(0, 7), k -> new double[3])[t] += m;
            if (t == 0) rev += m; else if (t == 1) { dep += m; cats.merge(o[2].toString(), m, Double::sum); } else epa += m;
        }
        StringBuilder h = new StringBuilder(entete(p.nom, "Rapport financier du projet" + (org().isEmpty() ? "" : " — " + org())));
        h.append("<div class='kp'>").append(kpi("Revenus", FMT.format(rev) + " " + DEV, "#b8f5d6")).append(kpi("Dépenses", FMT.format(dep) + " " + DEV, "#ffc2d4"))
                .append(kpi("Épargne", FMT.format(epa) + " " + DEV, "#bfe7ff")).append(kpi("Reste à vivre", FMT.format(rev - dep - epa) + " " + DEV, "#ead0ff")).append("</div>");
        if (p.objectif > 0) h.append("<p><b>Objectif d'épargne :</b> ").append(FMT.format(p.objectif)).append(" ").append(DEV).append(" — atteint à ")
                .append((int) (epa * 100 / p.objectif)).append(" %</p>");
        h.append("<h2>Dépenses par catégorie</h2><table>");
        List<Map.Entry<String, Double>> es = new ArrayList<>(cats.entrySet());
        es.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        for (Map.Entry<String, Double> e : es)
            h.append("<tr><td style='width:28%'>").append(esc(e.getKey())).append("</td><td><div class='bar'><i style='width:").append((int) (e.getValue() / dep * 100))
                    .append("%'></i></div></td><td class='r' style='width:22%'>").append(FMT.format(e.getValue())).append(" (").append((int) Math.round(e.getValue() / dep * 100)).append("%)</td></tr>");
        if (es.isEmpty()) h.append("<tr><td>Aucune dépense.</td></tr>");
        h.append("</table><h2>Synthèse mensuelle</h2><table><tr><th>Mois</th><th class='r'>Revenus</th><th class='r'>Dépenses</th><th class='r'>Épargne</th><th class='r'>Net</th></tr>");
        for (Map.Entry<String, double[]> e : mois.entrySet()) {
            double[] v = e.getValue();
            h.append("<tr><td>").append(e.getKey()).append("</td><td class='r'>").append(FMT.format(v[0])).append("</td><td class='r'>").append(FMT.format(v[1]))
                    .append("</td><td class='r'>").append(FMT.format(v[2])).append("</td><td class='r'><b>").append(FMT.format(v[0] - v[1] - v[2])).append("</b></td></tr>");
        }
        h.append("</table><h2>Détail des opérations (").append(ops.size()).append(")</h2><table><tr><th>Date</th><th>Description</th><th>Catégorie</th><th>Type</th><th class='r'>Montant</th></tr>");
        List<Object[]> tri = new ArrayList<>(ops);
        tri.sort((a, b) -> b[0].toString().compareTo(a[0].toString()));
        for (Object[] o : tri)
            h.append("<tr><td>").append(o[0]).append("</td><td>").append(esc(o[1].toString())).append("</td><td>").append(esc(o[2].toString())).append("</td><td>")
                    .append(o[3]).append("</td><td class='r'>").append(FMT.format(((Number) o[4]).doubleValue())).append("</td></tr>");
        return h.append("</table>").append(pied()).toString();
    }

    static String rapportGlobal(List<Projet> ps) {
        StringBuilder h = new StringBuilder(entete("Rapport global — tous les projets", org().isEmpty() ? "Vue d'ensemble" : org()));
        double R = 0, D = 0, E = 0;
        StringBuilder rows = new StringBuilder();
        for (Projet p : ps) {
            double[] s = stats(p);
            R += s[0]; D += s[1]; E += s[2];
            rows.append("<tr><td>").append(esc(p.nom)).append("</td><td class='r'>").append(FMT.format(s[0])).append("</td><td class='r'>").append(FMT.format(s[1]))
                    .append("</td><td class='r'>").append(FMT.format(s[2])).append("</td><td class='r'><b>").append(FMT.format(s[0] - s[1] - s[2])).append("</b></td><td class='r'>")
                    .append((long) s[3]).append("</td></tr>");
        }
        h.append("<div class='kp'>").append(kpi("Revenus", FMT.format(R) + " " + DEV, "#b8f5d6")).append(kpi("Dépenses", FMT.format(D) + " " + DEV, "#ffc2d4"))
                .append(kpi("Épargne", FMT.format(E) + " " + DEV, "#bfe7ff")).append(kpi("Solde global", FMT.format(R - D - E) + " " + DEV, "#ead0ff")).append("</div>");
        h.append("<h2>Détail par projet (").append(ps.size()).append(")</h2><table><tr><th>Projet</th><th class='r'>Revenus</th><th class='r'>Dépenses</th><th class='r'>Épargne</th><th class='r'>Reste</th><th class='r'>Opérations</th></tr>")
                .append(rows).append("</table>");
        return h.append(pied()).toString();
    }

    // ===================== FENÊTRE PRINCIPALE =====================
    final CardLayout racine = new CardLayout();
    final JPanel conteneur = new JPanel(racine);
    final ToastPane toastPane = new ToastPane();
    Accueil accueil;
    Verrou verrou;
    VueProjet vue;
    boolean plein = false;

    public TpJavaGraphique() {
        setTitle("Finances Pro " + VERSION + " — Gestion budgétaire");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { quitter(); } });
        setSize(1280, 900);
        setMinimumSize(new Dimension(1120, 740));
        DOSSIER.mkdirs();
        chargerCfg();
        appliquerTheme("clair".equals(CFG.getProperty("theme")));
        getContentPane().setBackground(BG);
        projets.addAll(chargerProjets());
        accueil = new Accueil();
        verrou = new Verrou();
        conteneur.add(new Splash(this::apresSplash), "SPLASH");
        conteneur.add(accueil, "ACCUEIL");
        conteneur.add(verrou, "LOCK");
        add(conteneur);
        setGlassPane(toastPane);
        toastPane.setVisible(true);
        raccourcis();
        setLocationRelativeTo(null);
    }

    void basculerTheme() {
        if (!(vueActive() || accueil.isShowing())) return;
        boolean clair = !CLAIR;
        Projet pr = vueActive() ? vue.p : null;
        int ecr = vue != null ? vue.ecran : 0;
        if (vue != null) vue.sauver();
        appliquerTheme(clair);
        CFG.setProperty("theme", clair ? "clair" : "sombre");
        sauverCfg();
        conteneur.removeAll();
        vue = null;
        getContentPane().setBackground(BG);
        accueil = new Accueil();
        verrou = new Verrou();
        conteneur.add(accueil, "ACCUEIL");
        conteneur.add(verrou, "LOCK");
        if (pr != null) {
            vue = new VueProjet(pr);
            conteneur.add(vue, "PROJET");
            vue.changer(ecr);
            racine.show(conteneur, "PROJET");
        } else racine.show(conteneur, "ACCUEIL");
        conteneur.revalidate();
        conteneur.repaint();
        toast(clair ? "Thème clair « institutionnel » activé" : "Thème sombre « néon » activé", GREEN);
    }

    void apresSplash() {
        if (pinActif()) verrouiller();
        else { accueil.rafraichir(); racine.show(conteneur, "ACCUEIL"); }
    }

    void verrouiller() {
        if (!pinActif()) { toast("Définissez d'abord un code PIN (bouton « Paramètres généraux »)", ORANGE); return; }
        if (vue != null) vue.sauver();
        racine.show(conteneur, "LOCK");
        verrou.champ.requestFocusInWindow();
    }

    void toast(String m, Color c) { toastPane.montrer(m, c); }
    boolean vueActive() { return vue != null && vue.isShowing(); }

    void lier(String ks, Runnable r) {
        KeyStroke k = KeyStroke.getKeyStroke(ks);
        String id = "act " + ks;
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(k, id);
        getRootPane().getActionMap().put(id, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { r.run(); }
        });
    }

    void raccourcis() {
        lier("F11", this::basculerPlein);
        lier("ctrl L", this::verrouiller);
        lier("ctrl T", this::basculerTheme);
        lier("ctrl H", () -> { if (vueActive()) retourAccueil(); });
        lier("ctrl N", () -> { if (vueActive()) { vue.changer(0); vue.tDesc.requestFocusInWindow(); } else if (accueil.isShowing()) nouveauProjet(); });
        lier("ctrl Z", () -> { if (vueActive()) vue.annuler(); });
        lier("ctrl E", () -> { if (vueActive()) vue.exporter(); });
        lier("ctrl R", () -> { if (vueActive()) vue.rapport(); });
        for (int i = 1; i <= 8; i++) { final int k = i - 1; lier("ctrl " + i, () -> { if (vueActive()) vue.changer(k); }); }
    }

    void basculerPlein() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        try {
            if (!plein) { dispose(); setUndecorated(true); setVisible(true); gd.setFullScreenWindow(this); }
            else { gd.setFullScreenWindow(null); dispose(); setUndecorated(false); setVisible(true); }
            plein = !plein;
        } catch (Exception e) { toast("Plein écran indisponible", RED); }
    }

    void ouvrir(Projet p) {
        if (vue != null) conteneur.remove(vue);
        vue = new VueProjet(p);
        conteneur.add(vue, "PROJET");
        racine.show(conteneur, "PROJET");
    }

    void retourAccueil() {
        if (vue != null) vue.sauver();
        accueil.rafraichir();
        racine.show(conteneur, "ACCUEIL");
    }

    void nouveauProjet() {
        Projet p = dialogueProjet(null);
        if (p == null) return;
        projets.add(p);
        sauverProjets();
        accueil.rafraichir();
        ouvrir(p);
        toast("Projet « " + p.nom + " » créé", GREEN);
    }

    void supprimerProjet(Projet p) {
        int n = lireOps(p.id).size();
        int r = JOptionPane.showConfirmDialog(this, "Supprimer définitivement « " + p.nom + " » et ses " + n + " opération(s) ?\n(Une sauvegarde automatique est faite à la fermeture.)",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        projets.remove(p);
        new File(DOSSIER, p.id + ".csv").delete();
        new File(DOSSIER, p.id + ".rec").delete();
        sauverProjets();
        accueil.rafraichir();
        toast("Projet supprimé", ORANGE);
    }

    void dupliquerProjet(Projet p) {
        Projet n = new Projet();
        n.id = Long.toString(System.currentTimeMillis(), 36);
        n.nom = cut(p.nom, 24) + " (copie)"; n.icone = p.icone; n.couleur = p.couleur; n.budget = p.budget; n.objectif = p.objectif;
        try {
            for (String ext : new String[]{".csv", ".rec"}) {
                File src = new File(DOSSIER, p.id + ext);
                if (src.exists()) Files.copy(src.toPath(), new File(DOSSIER, n.id + ext).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) { toast("Duplication impossible", RED); return; }
        projets.add(n);
        sauverProjets();
        accueil.rafraichir();
        toast("Projet dupliqué", GREEN);
    }

    Projet dialogueProjet(Projet init) {
        JTextField nom = new JTextField(init == null ? "" : init.nom);
        JComboBox<String> ic = new JComboBox<>(ICONES);
        JComboBox<String> co = new JComboBox<>(NOMS_COULEURS);
        JTextField bud = new JTextField(init == null ? "0" : String.valueOf((long) init.budget));
        JTextField obj = new JTextField(init == null ? "0" : String.valueOf((long) init.objectif));
        if (init != null) { ic.setSelectedItem(typeProjet(init.icone).isEmpty() ? ICONES[0] : init.icone); co.setSelectedIndex(init.couleur); }
        JPanel pn = new JPanel(new GridLayout(0, 2, 8, 10));
        pn.setBackground(PANEL);
        pn.setPreferredSize(new Dimension(460, 230));
        String[] lib = {"Nom du projet :", "Type de projet :", "Couleur :", "Budget dépenses / mois (0 = aucun) :", "Objectif d'épargne en " + DEV + " (0 = aucun) :"};
        JComponent[] cs = {nom, ic, co, bud, obj};
        for (int i = 0; i < cs.length; i++) { styler(cs[i]); pn.add(label(lib[i], f(Font.BOLD, 12), TXT2)); pn.add(cs[i]); }
        while (true) {
            int r = JOptionPane.showConfirmDialog(this, pn, init == null ? "Nouveau projet" : "Réglages du projet", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) return null;
            try {
                if (nom.getText().isBlank()) throw new IllegalArgumentException();
                Projet p = new Projet();
                p.id = init == null ? Long.toString(System.currentTimeMillis(), 36) : init.id;
                p.nom = nom.getText().trim().replace(";", ",");
                p.icone = (String) ic.getSelectedItem();
                p.couleur = co.getSelectedIndex();
                p.budget = Math.max(0, nombre(bud.getText()));
                p.objectif = Math.max(0, nombre(obj.getText()));
                return p;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Nom obligatoire et montants numériques valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void reglagesGeneraux() {
        JTextField nom = new JTextField(CFG.getProperty("nom", "")), dev = new JTextField(DEV);
        JPasswordField p1 = new JPasswordField(), p2 = new JPasswordField();
        JCheckBox retirer = new JCheckBox("Supprimer le code PIN actuel");
        retirer.setOpaque(false);
        retirer.setForeground(TXT2);
        boolean a = pinActif();
        JPanel pn = new JPanel(new GridLayout(0, 2, 8, 10));
        pn.setBackground(PANEL);
        pn.setPreferredSize(new Dimension(500, a ? 260 : 220));
        for (JComponent c : new JComponent[]{nom, dev, p1, p2}) styler(c);
        pn.add(label("Nom de l'organisation / utilisateur :", f(Font.BOLD, 12), TXT2)); pn.add(nom);
        pn.add(label("Devise (affichée partout) :", f(Font.BOLD, 12), TXT2)); pn.add(dev);
        pn.add(label(a ? "Nouveau PIN (vide = inchangé) :" : "Définir un code PIN (4-12 car.) :", f(Font.BOLD, 12), TXT2)); pn.add(p1);
        pn.add(label("Confirmer le code PIN :", f(Font.BOLD, 12), TXT2)); pn.add(p2);
        if (a) { pn.add(new JLabel("")); pn.add(retirer); }
        while (true) {
            if (JOptionPane.showConfirmDialog(this, pn, "Paramètres généraux", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
            String x1 = new String(p1.getPassword());
            if (!x1.isEmpty() && (x1.length() < 4 || x1.length() > 12 || !x1.equals(new String(p2.getPassword())))) {
                JOptionPane.showMessageDialog(this, "Le code PIN doit faire 4 à 12 caractères et être identique dans les deux champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            CFG.setProperty("nom", nom.getText().trim());
            CFG.setProperty("devise", dev.getText().isBlank() ? "FCFA" : dev.getText().trim());
            DEV = CFG.getProperty("devise");
            if (retirer.isSelected()) CFG.remove("pin");
            else if (!x1.isEmpty()) CFG.setProperty("pin", hash(x1));
            sauverCfg();
            accueil.rafraichir();
            toast("Paramètres enregistrés", GREEN);
            return;
        }
    }

    void aide() {
        JTextArea a = new JTextArea("FINANCES PRO " + VERSION + "\n\nRACCOURCIS\n"
                + "  F11 ........ plein écran (mode présentation)\n  Ctrl+N ..... nouveau projet / nouvelle opération\n  Ctrl+1…8 ... changer d'onglet dans un projet\n"
                + "  Ctrl+Z ..... annuler une suppression\n  Ctrl+E ..... exporter en CSV\n  Ctrl+R ..... rapport imprimable (PDF)\n  Ctrl+H ..... retour à l'accueil\n  Ctrl+L ..... verrouiller\n  Ctrl+T ..... thème clair / sombre\n\n"
                + "DONNÉES\n  Dossier : " + DOSSIER.getAbsolutePath() + "\n  Sauvegarde automatique (zip) à chaque fermeture, 7 jours conservés.\n");
        a.setEditable(false);
        a.setBackground(PANEL);
        a.setForeground(TXT);
        a.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, a, "ℹ Aide & à propos", JOptionPane.PLAIN_MESSAGE);
    }

    void ouvrirFichier(File fi) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) Desktop.getDesktop().browse(fi.toURI());
            else JOptionPane.showMessageDialog(this, "Rapport créé :\n" + fi.getAbsolutePath());
            toast("Rapport généré : " + fi.getName(), GREEN);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Rapport créé :\n" + fi.getAbsolutePath()); }
    }

    void rapportGlobalAction() {
        try {
            File d = new File(DOSSIER, "rapports");
            d.mkdirs();
            File fi = new File(d, "rapport_global_" + LocalDate.now() + ".html");
            ecrire(fi, Collections.singletonList(rapportGlobal(projets)));
            ouvrirFichier(fi);
        } catch (IOException e) { toast("Rapport impossible : " + e.getMessage(), RED); }
    }

    void sauvegarder(File dest) throws IOException {
        File[] fs = DOSSIER.listFiles();
        try (ZipOutputStream z = new ZipOutputStream(Files.newOutputStream(dest.toPath()))) {
            if (fs != null) for (File fi : fs)
                if (fi.isFile() && (fi.getName().endsWith(".csv") || fi.getName().endsWith(".rec"))) {
                    z.putNextEntry(new ZipEntry(fi.getName()));
                    Files.copy(fi.toPath(), z);
                    z.closeEntry();
                }
        }
    }

    void restaurer(File src) throws IOException {
        try (ZipInputStream z = new ZipInputStream(Files.newInputStream(src.toPath()))) {
            ZipEntry e;
            while ((e = z.getNextEntry()) != null) {
                String n = new File(e.getName()).getName();
                if (e.isDirectory() || !(n.endsWith(".csv") || n.endsWith(".rec"))) continue;
                Files.copy(z, new File(DOSSIER, n).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        projets.clear();
        projets.addAll(chargerProjets());
        accueil.rafraichir();
    }

    void menuSauvegarde(Component src) {
        JPopupMenu m = new JPopupMenu();
        JMenuItem a = new JMenuItem("Créer une sauvegarde (.zip)…"), b = new JMenuItem("Restaurer une sauvegarde…");
        a.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("FinancesPro_sauvegarde_" + LocalDate.now() + ".zip"));
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            try { sauvegarder(fc.getSelectedFile()); toast("Sauvegarde créée", GREEN); } catch (IOException ex) { toast("Échec : " + ex.getMessage(), RED); }
        });
        b.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
            if (JOptionPane.showConfirmDialog(this, "La restauration remplace les projets du même nom. Continuer ?", "Restaurer", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try { restaurer(fc.getSelectedFile()); toast("Sauvegarde restaurée", GREEN); } catch (IOException ex) { toast("Échec : " + ex.getMessage(), RED); }
        });
        m.add(a);
        m.add(b);
        m.show(src, 0, src.getHeight());
    }

    void quitter() {
        if (vue != null) vue.sauver();
        try {
            File d = new File(DOSSIER, "sauvegardes");
            d.mkdirs();
            sauvegarder(new File(d, "auto_" + LocalDate.now() + ".zip"));
            File[] fs = d.listFiles((dd, n) -> n.startsWith("auto_"));
            if (fs != null && fs.length > 7) { Arrays.sort(fs); for (int i = 0; i < fs.length - 7; i++) fs[i].delete(); }
        } catch (Exception ignored) { }
        System.exit(0);
    }

    // ===================== NOTIFICATION (TOAST) =====================
    class ToastPane extends JComponent {
        String msg = "";
        Color col = GREEN;
        float a = 0;
        Timer t;

        ToastPane() { setOpaque(false); }

        @Override public boolean contains(int x, int y) { return false; }

        void montrer(String m, Color c) {
            msg = m; col = c; a = 1f;
            if (t != null) t.stop();
            final int[] n = {0};
            t = new Timer(30, e -> {
                n[0]++;
                if (n[0] > 80) { a -= 0.07f; if (a <= 0) { a = 0; ((Timer) e.getSource()).stop(); } }
                repaint();
            });
            t.start();
            repaint();
        }

        @Override protected void paintComponent(Graphics g0) {
            if (a <= 0) return;
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(f(Font.BOLD, 15));
            int w = g.getFontMetrics().stringWidth(msg) + 56, h = 44, x = (getWidth() - w) / 2, y = getHeight() - 96 + (int) ((1 - a) * 14);
            g.setColor(alpha(PANEL, (int) (240 * a)));
            g.fillRoundRect(x, y, w, h, 22, 22);
            g.setColor(alpha(col, (int) (255 * a)));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(x, y, w, h, 22, 22);
            g.fillOval(x + 14, y + 17, 10, 10);
            g.setColor(alpha(TXT, (int) (255 * a)));
            g.drawString(msg, x + 34, y + 28);
            g.dispose();
        }
    }

    // ===================== ÉCRAN DE DÉMARRAGE =====================
    class Splash extends JPanel {
        int tick = 0;
        final Timer t;
        final String[] ETAPES = {"Initialisation du moteur graphique…", "Chargement de la configuration…", "Lecture des projets…",
            "Vérification de l'intégrité des données…", "Préparation de l'interface…"};

        Splash(Runnable fin) {
            setBackground(BG);
            t = new Timer(25, e -> {
                tick++;
                repaint();
                if (tick >= 100) { ((Timer) e.getSource()).stop(); fin.run(); }
            });
            t.start();
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), cx = w / 2, cy = h / 2 - 60;
            g.setPaint(new GradientPaint(0, 0, BG1, 0, h, BG2));
            g.fillRect(0, 0, w, h);
            for (int k = 0; k < 4; k++) {
                int r = 52 + k * 20;
                g.setStroke(new BasicStroke(5f - k * 0.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(COULEURS[(k * 2) % COULEURS.length]);
                g.draw(new Arc2D.Double(cx - r, cy - r, 2 * r, 2 * r, tick * (4 + k * 2) * (k % 2 == 0 ? 1 : -1), 110 + k * 30, Arc2D.OPEN));
            }
            g.setColor(TXT);
            g.setFont(f(Font.BOLD, 44));
            centre(g, "FP", cx, cy + 16);
            g.setFont(f(Font.BOLD, 46));
            String tt = "FINANCES PRO";
            int tw = g.getFontMetrics().stringWidth(tt);
            g.setPaint(new GradientPaint(cx - tw / 2f, 0, arc((tick * 3 % 300) / 300f), cx + tw / 2f, 0, arc(((tick * 3 + 120) % 300) / 300f)));
            centre(g, tt, cx, cy + 150);
            g.setPaint(null);
            g.setColor(TXT2);
            g.setFont(f(Font.PLAIN, 16));
            centre(g, org().isEmpty() ? "Gestion budgétaire multi-projets" : org(), cx, cy + 180);
            int bw = 380, bx = cx - bw / 2, by = cy + 215;
            g.setColor(alpha(TXT, 35));
            g.fillRoundRect(bx, by, bw, 10, 10, 10);
            g.setPaint(new GradientPaint(bx, 0, GREEN, bx + bw, 0, BLUE));
            g.fillRoundRect(bx, by, bw * Math.min(tick, 100) / 100, 10, 10, 10);
            g.setPaint(null);
            g.setColor(TXT2);
            g.setFont(f(Font.PLAIN, 12));
            centre(g, ETAPES[Math.min(ETAPES.length - 1, tick * ETAPES.length / 100)], cx, by + 30);
            g.setColor(alpha(TXT2, 150));
            centre(g, "Version " + VERSION, cx, h - 20);
            g.dispose();
        }
    }

    // ===================== ÉCRAN DE VERROUILLAGE =====================
    class Verrou extends JPanel {
        final JPasswordField champ = new JPasswordField();
        final JLabel msg = label(" ", f(Font.BOLD, 14), RED);
        Timer tm;
        int essais = 0, tick = 0;

        Verrou() {
            super(new GridBagLayout());
            setBackground(BG);
            JPanel carte = new JPanel(new GridLayout(0, 1, 0, 12));
            carte.setOpaque(false);
            carte.setPreferredSize(new Dimension(340, 220));
            JLabel t = label("Espace verrouillé", f(Font.BOLD, 22), TXT);
            t.setHorizontalAlignment(SwingConstants.CENTER);
            msg.setHorizontalAlignment(SwingConstants.CENTER);
            styler(champ);
            champ.setHorizontalAlignment(SwingConstants.CENTER);
            champ.setFont(f(Font.BOLD, 22));
            JButton ok = bouton("Déverrouiller", GREEN, SUR);
            champ.addActionListener(e -> verifier());
            ok.addActionListener(e -> verifier());
            carte.add(t);
            carte.add(champ);
            carte.add(ok);
            carte.add(msg);
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(150, 0, 0, 0);
            add(carte, gc);
            tm = new Timer(33, e -> { tick++; if (isShowing()) repaint(); });
            tm.start();
        }

        @Override public void removeNotify() { super.removeNotify(); if (tm != null) tm.stop(); }

        void verifier() {
            String pin = new String(champ.getPassword());
            champ.setText("");
            if (hash(pin).equals(CFG.getProperty("pin"))) {
                essais = 0;
                msg.setText(" ");
                accueil.rafraichir();
                racine.show(conteneur, "ACCUEIL");
            } else {
                essais++;
                msg.setText("Code incorrect (" + essais + "/3)");
                if (essais >= 3) {
                    champ.setEnabled(false);
                    msg.setText("Trop d'essais — patientez 10 secondes");
                    Timer t = new Timer(10000, e -> { champ.setEnabled(true); essais = 0; msg.setText(" "); champ.requestFocusInWindow(); });
                    t.setRepeats(false);
                    t.start();
                }
            }
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), cx = w / 2, cy = h / 2 - 110;
            g.setPaint(new GradientPaint(0, 0, BG1, 0, h, BG2));
            g.fillRect(0, 0, w, h);
            for (int k = 0; k < 4; k++) {
                double ph = (tick / 40.0 + k * 0.25) % 1.0;
                int r = (int) (40 + ph * 110);
                g.setColor(alpha(GREEN, (int) (110 * (1 - ph))));
                g.setStroke(new BasicStroke(2f));
                g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
            }
            g.setFont(f(Font.BOLD, 26));
            g.setColor(TXT);
            centre(g, "VERROUILLÉ", cx, cy + 10);
            g.dispose();
        }
    }

    // ===================== ÉCRAN D'ACCUEIL =====================
    class Accueil extends JPanel {
        final List<float[]> parts = new ArrayList<>();
        final Random rnd = new Random();
        final JPanel grille = new JPanel(new GridLayout(0, 3, 22, 22));
        final JTextField recherche = new JTextField();
        final Compteurs cptr = new Compteurs();
        final Defilant defil = new Defilant();
        Timer tm;
        int tick = 0;

        @Override public void removeNotify() { super.removeNotify(); if (tm != null) tm.stop(); }
        final String SOUS = "Un dossier par projet : revenus, dépenses, épargne… tout au même endroit.";
        final String[] TIPS = {
            "La règle 50/30/20 : 50% besoins, 30% envies, 20% épargne.",
            "Créez un projet par objectif : ordinateur, voyage, fonds d'urgence…",
            "Épargnez dès que vous recevez de l'argent, pas à la fin du mois.",
            "Fixez un budget mensuel dans « Réglages du projet » pour être alerté en cas de dépassement.",
            "Générez un rapport imprimable (PDF) depuis chaque projet : Ctrl+R.",
            "Un fonds d'urgence = 3 à 6 mois de dépenses.",
            "F11 : mode présentation plein écran — Ctrl+L : verrouiller l'application."};

        Accueil() {
            super(new BorderLayout(0, 8));
            setBackground(BG);
            for (int i = 0; i < 70; i++)
                parts.add(new float[]{rnd.nextFloat(), rnd.nextFloat(), 0.0008f + rnd.nextFloat() * 0.002f,
                    4 + rnd.nextFloat() * 10, rnd.nextInt(COULEURS.length), rnd.nextFloat() * 6.28f});

            JPanel haut = new JPanel() {
                @Override protected void paintComponent(Graphics g0) {
                    Graphics2D g = (Graphics2D) g0.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    int w = getWidth();
                    g.setFont(f(Font.PLAIN, 14));
                    g.setColor(TXT2);
                    String date = LocalDateTime.now().format(FDATE);
                    g.drawString(date, w - g.getFontMetrics().stringWidth(date) - 25, 26);
                    g.setFont(f(Font.BOLD, 62));
                    String t = "FINANCES PRO";
                    int tw = g.getFontMetrics().stringWidth(t), x = (w - tw) / 2, bob = (int) (Math.sin(tick / 15.0) * 4);
                    Color c1 = arc((tick % 300) / 300f), c2 = arc(((tick + 100) % 300) / 300f);
                    g.setPaint(new GradientPaint(x, 0, c1, x + tw, 0, c2));
                    g.drawString(t, x, 90 + bob);
                    g.setPaint(null);
                    g.setFont(f(Font.PLAIN, 16));
                    g.setColor(TXT2);
                    int n = Math.min(SOUS.length(), tick / 2);
                    g.drawString(SOUS.substring(0, n) + ((tick / 10) % 2 == 0 ? "|" : ""), (w - g.getFontMetrics().stringWidth(SOUS)) / 2, 125);
                    g.setFont(f(Font.BOLD, 15));
                    g.setColor(GREEN);
                    String o = org();
                    centre(g, (LocalTime.now().getHour() < 18 ? "Bonjour" : "Bonsoir") + (o.isEmpty() ? "" : ", " + o) + " ! Choisissez un projet ou créez-en un nouveau", w / 2, 155);
                    g.dispose();
                }
            };
            haut.setOpaque(false);
            haut.setPreferredSize(new Dimension(0, 170));

            JButton nouveau = bouton("NOUVEAU PROJET", GREEN, SUR);
            nouveau.setFont(f(Font.BOLD, 16));
            nouveau.setPreferredSize(new Dimension(260, 46));
            nouveau.addActionListener(e -> nouveauProjet());
            styler(recherche);
            recherche.setPreferredSize(new Dimension(210, 40));
            recherche.setToolTipText("Rechercher un projet");
            recherche.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { rafraichir(); }
                public void removeUpdate(DocumentEvent e) { rafraichir(); }
                public void changedUpdate(DocumentEvent e) { rafraichir(); }
            });
            JPanel barre = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
            barre.setOpaque(false);
            barre.add(nouveau);
            barre.add(label("Rechercher :", f(Font.BOLD, 14), TXT));
            barre.add(recherche);
            String[][] pb = {{"Rapport global", "Rapport global (imprimable / PDF)"}, {"Sauvegarde", "Sauvegarde & restauration"}, {"Paramètres généraux", "Paramètres généraux & code PIN"}, {"Verrouiller", "Verrouiller (Ctrl+L)"}, {"Aide", "Aide & raccourcis"}, {CLAIR ? "Thème sombre" : "Thème clair", "Changer de thème : clair / sombre (Ctrl+T)"}};
            for (int i = 0; i < pb.length; i++) {
                final int k = i;
                JButton b = bouton(pb[i][0], INPUT, TXT);
                b.setToolTipText(pb[i][1]);
                b.setPreferredSize(new Dimension(b.getPreferredSize().width, 40));
                b.addActionListener(e -> {
                    if (k == 0) rapportGlobalAction();
                    else if (k == 1) menuSauvegarde(b);
                    else if (k == 2) reglagesGeneraux();
                    else if (k == 3) verrouiller();
                    else if (k == 4) aide();
                    else basculerTheme();
                });
                barre.add(b);
            }
            JPanel nord = new JPanel(new BorderLayout());
            nord.setOpaque(false);
            nord.add(haut, BorderLayout.CENTER);
            nord.add(barre, BorderLayout.SOUTH);

            grille.setOpaque(false);
            JPanel enveloppe = new JPanel(new BorderLayout());
            enveloppe.setOpaque(false);
            enveloppe.setBorder(new EmptyBorder(10, 40, 10, 40));
            enveloppe.add(grille, BorderLayout.NORTH);
            JScrollPane sp = new JScrollPane(enveloppe, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            sp.setOpaque(false);
            sp.getViewport().setOpaque(false);
            sp.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
            sp.setBorder(null);
            sp.getVerticalScrollBar().setUnitIncrement(18);

            JPanel bas = new JPanel(new BorderLayout(0, 8));
            bas.setOpaque(false);
            bas.setBorder(new EmptyBorder(4, 40, 12, 40));
            cptr.setPreferredSize(new Dimension(0, 74));
            defil.setPreferredSize(new Dimension(0, 30));
            bas.add(cptr, BorderLayout.CENTER);
            bas.add(defil, BorderLayout.SOUTH);

            add(nord, BorderLayout.NORTH);
            add(sp, BorderLayout.CENTER);
            add(bas, BorderLayout.SOUTH);

            tm = new Timer(33, e -> {
                tick++;
                if (!isShowing()) return;
                for (float[] p : parts) {
                    p[1] -= p[2];
                    if (p[1] < -0.05f) { p[1] = 1.05f; p[0] = rnd.nextFloat(); }
                }
                for (int i = 0; i < 4; i++) cptr.aff[i] += (cptr.cible[i] - cptr.aff[i]) * 0.08;
                repaint();
            });
            tm.start();
            rafraichir();
        }

        void rafraichir() {
            grille.removeAll();
            double rev = 0, dep = 0, epa = 0;
            String q = recherche.getText().trim().toLowerCase();
            for (Projet p : projets) {
                double[] s = stats(p);
                rev += s[0]; dep += s[1]; epa += s[2];
                if (q.isEmpty() || p.nom.toLowerCase().contains(q)) grille.add(new Carte(p, s));
            }
            grille.add(new Carte(null, null));
            cptr.cible = new double[]{rev, dep, epa, rev - dep - epa};
            defil.texte = "   " + projets.size() + " projet(s)   •   " + String.join("   •   ", TIPS) + "   ";
            grille.revalidate();
            grille.repaint();
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g.setPaint(new GradientPaint(0, 0, BG1, 0, h, BG2));
            g.fillRect(0, 0, w, h);
            for (float[] p : parts) {
                int x = (int) (p[0] * w + Math.sin(tick * 0.03 + p[5]) * 20), y = (int) (p[1] * h), d = (int) p[3];
                Color c = COULEURS[(int) p[4]];
                g.setColor(alpha(c, 70));
                g.fillOval(x, y, d, d);
                if (d > 9) {
                    g.setColor(alpha(c, 150));
                    g.setFont(f(Font.BOLD, d));
                    g.drawString("F", x + d / 4, y + d);
                }
            }
            g.dispose();
        }

        class Compteurs extends JPanel {
            double[] cible = new double[4], aff = new double[4];
            final String[] tt = {"REVENUS CUMULÉS", "DÉPENSES CUMULÉES", "ÉPARGNE CUMULÉE", "SOLDE GLOBAL"};
            final Color[] cc = {GREEN, RED, BLUE, PURPLE};

            Compteurs() { setOpaque(false); }

            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int gap = 14, cw = (getWidth() - 3 * gap) / 4, h = getHeight();
                for (int i = 0; i < 4; i++) {
                    int x = i * (cw + gap);
                    g.setColor(alpha(cc[i], 40));
                    g.fillRoundRect(x, 4, cw, h - 8, 18, 18);
                    g.setColor(cc[i]);
                    g.setStroke(new BasicStroke(2f));
                    g.drawRoundRect(x, 4, cw, h - 8, 18, 18);
                    g.setFont(f(Font.BOLD, 11));
                    g.setColor(TXT2);
                    g.drawString(tt[i], x + 14, 26);
                    g.setFont(f(Font.BOLD, 22));
                    g.setColor(i == 3 && aff[i] < 0 ? RED : TXT);
                    g.drawString(FMT.format(aff[i]) + " " + DEV, x + 14, 55);
                }
                g.dispose();
            }
        }

        class Defilant extends JPanel {
            String texte = "";

            Defilant() { setOpaque(false); }

            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(alpha(HEAD, 215));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g.setColor(LINE);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g.setFont(f(Font.PLAIN, 13));
                g.setColor(TXT2);
                int tw = Math.max(1, g.getFontMetrics().stringWidth(texte));
                int x = -(int) ((tick * 2L) % tw);
                for (; x < getWidth(); x += tw) g.drawString(texte, x, 20);
                g.dispose();
            }
        }

        class Carte extends JPanel {
            final Projet p;
            final double[] s;
            boolean hover;
            int mx = -1, my = -1;

            Carte(Projet p, double[] s) {
                this.p = p;
                this.s = s;
                setOpaque(false);
                setPreferredSize(new Dimension(300, 190));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setToolTipText(p == null ? "Créer un nouveau projet" : "Ouvrir « " + p.nom + " »");
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; }
                    @Override public void mouseExited(MouseEvent e) { hover = false; mx = my = -1; }
                    @Override public void mousePressed(MouseEvent e) { clic(e.getX(), e.getY()); }
                });
                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override public void mouseMoved(MouseEvent e) { mx = e.getX(); my = e.getY(); }
                });
            }

            Ellipse2D.Float croix() { return new Ellipse2D.Float(getWidth() - 38, 14, 26, 26); }
            Ellipse2D.Float copie() { return new Ellipse2D.Float(getWidth() - 70, 14, 26, 26); }

            @Override public String getToolTipText(MouseEvent e) {
                if (p == null) return super.getToolTipText();
                if (croix().contains(e.getX(), e.getY())) return "Supprimer le projet";
                if (copie().contains(e.getX(), e.getY())) return "Dupliquer le projet";
                return super.getToolTipText();
            }

            void clic(int x, int y) {
                if (p == null) nouveauProjet();
                else if (croix().contains(x, y)) supprimerProjet(p);
                else if (copie().contains(x, y)) dupliquerProjet(p);
                else ouvrir(p);
            }

            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g.translate(0, hover ? -4 : 0);
                RoundRectangle2D r = new RoundRectangle2D.Float(4, 8, w - 8, h - 16, 26, 26);
                if (p == null) {
                    float pulse = (float) (0.5 + 0.5 * Math.sin(tick / 8.0));
                    g.setColor(alpha(GREEN, 15 + (int) (pulse * 35) + (hover ? 30 : 0)));
                    g.fill(r);
                    g.setColor(alpha(GREEN, 150 + (int) (pulse * 105)));
                    g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{10, 8}, tick * 0.5f));
                    g.draw(r);
                    g.setFont(f(Font.BOLD, 60 + (int) (pulse * 8)));
                    centre(g, "+", w / 2, h / 2 + 18);
                    g.setFont(f(Font.BOLD, 16));
                    g.setColor(TXT);
                    centre(g, "Nouveau projet / épargne", w / 2, h / 2 + 52);
                } else {
                    Color c = COULEURS[p.couleur];
                    g.setPaint(new GradientPaint(0, 0, alpha(c, hover ? 110 : 70), 0, h, PANEL));
                    g.fill(r);
                    g.setPaint(null);
                    g.setColor(c);
                    g.setStroke(new BasicStroke(hover ? 3.5f : 2f));
                    g.draw(r);
                    g.setColor(TXT);
                    g.setColor(alpha(c, 70));
                    g.fillOval(20, 30, 42, 42);
                    g.setColor(TXT);
                    g.setFont(f(Font.BOLD, 22));
                    centre(g, p.nom.isEmpty() ? "?" : p.nom.substring(0, 1).toUpperCase(), 41, 59);
                    g.setFont(f(Font.BOLD, 19));
                    g.drawString(cut(p.nom, 15), 72, 52);
                    g.setFont(f(Font.PLAIN, 12));
                    g.setColor(TXT2);
                    g.drawString((typeProjet(p.icone).isEmpty() ? "" : typeProjet(p.icone) + "  •  ") + ((long) s[3]) + " opération(s)", 72, 70);
                    g.drawString("Reste à vivre", 22, 100);
                    double solde = s[0] - s[1] - s[2];
                    g.setFont(f(Font.BOLD, 24));
                    g.setColor(solde >= 0 ? GREEN : RED);
                    g.drawString(FMT.format(solde) + " " + DEV, 22, 128);
                    double ratio = p.objectif > 0 ? s[2] / p.objectif : (s[0] > 0 ? s[2] / s[0] / 0.2 : 0);
                    String lbl = p.objectif > 0 ? "Objectif : " + (int) Math.min(999, ratio * 100) + "% de " + FMT.format(p.objectif)
                            : "Épargne : " + (s[0] > 0 ? (int) Math.round(s[2] / s[0] * 100) : 0) + "% des revenus";
                    g.setColor(alpha(TXT, 40));
                    g.fillRoundRect(22, 140, w - 52, 10, 10, 10);
                    g.setColor(c);
                    g.fillRoundRect(22, 140, (int) ((w - 52) * Math.min(1, Math.max(0, ratio))), 10, 10, 10);
                    g.setFont(f(Font.PLAIN, 11));
                    g.setColor(TXT2);
                    g.drawString(lbl, 22, 168);
                    boolean surX = croix().contains(mx, my), surC = copie().contains(mx, my);
                    g.setColor(surX ? RED : alpha(TXT, 40));
                    g.fill(croix());
                    g.setColor(surX ? Color.WHITE : TXT);
                    g.setFont(f(Font.BOLD, 13));
                    centre(g, "X", w - 25, 32);
                    g.setColor(surC ? BLUE : alpha(TXT, 40));
                    g.fill(copie());
                    g.setColor(surC ? Color.WHITE : TXT);
                    g.setStroke(new BasicStroke(1.6f));
                    int ccx = w - 57, ccy = 27;
                    g.drawRoundRect(ccx - 7, ccy - 3, 9, 9, 2, 2);
                    g.drawRoundRect(ccx - 3, ccy - 7, 9, 9, 2, 2);
                }
                g.dispose();
            }
        }
    }

    // ===================== JAUGE ANIMÉE (santé financière) =====================
    class Jauge extends JPanel {
        double cible = 0, aff = 0;
        String cap = "Aucune donnée";
        int tick = 0;
        Timer tm;

        @Override public void removeNotify() { super.removeNotify(); if (tm != null) tm.stop(); }

        Jauge() {
            setOpaque(false);
            setPreferredSize(new Dimension(176, 176));
            tm = new Timer(30, e -> { tick++; aff += (cible - aff) * 0.07; if (isShowing()) repaint(); });
            tm.start();
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), cx = w / 2, cy = h / 2 - 8, R = 54;
            Color c = scoreCouleur(aff);
            g.setColor(PANEL);
            g.fillRoundRect(0, 0, w - 1, h - 1, 22, 22);
            g.setColor(alpha(c, 90 + (int) (60 * Math.sin(tick / 12.0))));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(0, 0, w - 1, h - 1, 22, 22);
            g.setColor(alpha(TXT, 28));
            g.setStroke(new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Arc2D.Double(cx - R, cy - R, 2 * R, 2 * R, 90, -359.9, Arc2D.OPEN));
            g.setColor(alpha(c, 45));
            g.fill(new Arc2D.Double(cx - R + 8, cy - R + 8, 2 * R - 16, 2 * R - 16, -tick * 5, 40, Arc2D.PIE));
            g.setColor(c);
            g.draw(new Arc2D.Double(cx - R, cy - R, 2 * R, 2 * R, 90, -aff / 100.0 * 359.9, Arc2D.OPEN));
            g.setColor(TXT);
            g.setFont(f(Font.BOLD, 34));
            centre(g, String.valueOf((int) Math.round(aff)), cx, cy + 12);
            g.setFont(f(Font.PLAIN, 11));
            g.setColor(TXT2);
            centre(g, "/ 100", cx, cy + 28);
            g.setFont(f(Font.BOLD, 10));
            centre(g, "SANTÉ FINANCIÈRE", cx, h - 22);
            g.setColor(c);
            g.setFont(f(Font.BOLD, 11));
            centre(g, cap, cx, h - 8);
            g.dispose();
        }
    }

    // ===================== ESPACE DE TRAVAIL D'UN PROJET =====================
    class VueProjet extends JPanel {
        final Projet p;
        final DefaultTableModel modele = nouveauModele();
        final DefaultTableModel recModele = nouveauModele();
        final CardLayout cl = new CardLayout();
        final JPanel centre = new JPanel(cl);
        final JButton[] navs = new JButton[8];
        final Deque<Object[]> corbeille = new ArrayDeque<>();
        final Jauge jauge = new Jauge();
        JLabel lTitre, lRev, lDep, lEpa, lSolde, lInfoEpargne, lInfoBudget, lStatut, lCompte;
        JLabel[] kpi = new JLabel[6];
        JProgressBar barEpargne, barBudget, barObj;
        JTextField tDesc, tMontant, tDate, tCible, tMois, tRecherche;
        JComboBox<String> cType, cCat, cFiltreType;
        JTable table, tableRec;
        TableRowSorter<DefaultTableModel> tri;
        GraphHisto histo;
        GraphCourbe courbe;
        GraphDonut donut;
        GraphMois mois;
        JTextArea res1, res2, conseils;
        double revenus, depenses, epargne;
        int ecran = 0;

        DefaultTableModel nouveauModele() {
            return new DefaultTableModel(new String[]{"Date", "Description", "Catégorie", "Type", "Montant"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
                @Override public Class<?> getColumnClass(int c) { return c == 4 ? Long.class : String.class; }
            };
        }

        VueProjet(Projet p) {
            super(new BorderLayout(14, 0));
            this.p = p;
            setBackground(BG);
            setBorder(new EmptyBorder(10, 14, 10, 18));
            for (Object[] o : lireOps(p.id)) modele.addRow(o);
            for (Object[] o : lireFichier(recFile())) recModele.addRow(o);

            // --- barre latérale ---
            JPanel side = new JPanel(new BorderLayout(0, 10));
            side.setBackground(HEAD);
            side.setBorder(new CompoundBorder(new LineBorder(LINE, 1), new EmptyBorder(14, 12, 14, 12)));
            side.setPreferredSize(new Dimension(212, 0));
            JButton retour = bouton("Accueil", INPUT, TXT);
            retour.setToolTipText("Retour à l'accueil (Ctrl+H)");
            retour.addActionListener(e -> retourAccueil());
            lTitre = label("", f(Font.BOLD, 16), TXT);
            JPanel hautS = new JPanel(new GridLayout(0, 1, 0, 8));
            hautS.setOpaque(false);
            hautS.add(retour);
            hautS.add(lTitre);
            String[] noms = {"Tableau de bord", "Historique", "Courbe", "Répartition", "Mensuel", "Analyse", "Récurrentes", "Objectifs"};
            JPanel navP = new JPanel(new GridLayout(0, 1, 0, 5));
            navP.setOpaque(false);
            for (int i = 0; i < noms.length; i++) {
                final int k = i;
                navs[i] = bouton(noms[i], INPUT, TXT2);
                navs[i].setHorizontalAlignment(SwingConstants.LEFT);
                navs[i].setToolTipText("Ctrl+" + (i + 1));
                navs[i].addActionListener(e -> changer(k));
                navP.add(navs[i]);
            }
            JButton bRap = bouton("Rapport (PDF)", PURPLE, Color.WHITE), bReg = bouton("Réglages du projet", INPUT, TXT2),
                    bThe = bouton(CLAIR ? "Passer en sombre" : "Passer en clair", INPUT, TXT2);
            bRap.setHorizontalAlignment(SwingConstants.LEFT);
            bReg.setHorizontalAlignment(SwingConstants.LEFT);
            bRap.setToolTipText("Ouvre un rapport imprimable (Ctrl+R)");
            bRap.addActionListener(e -> rapport());
            bReg.addActionListener(e -> reglages());
            bThe.setHorizontalAlignment(SwingConstants.LEFT);
            bThe.setToolTipText("Thème clair / sombre (Ctrl+T)");
            bThe.addActionListener(e -> basculerTheme());
            navP.add(bRap);
            navP.add(bReg);
            navP.add(bThe);
            JPanel navW = new JPanel(new BorderLayout());
            navW.setOpaque(false);
            navW.add(navP, BorderLayout.NORTH);
            side.add(hautS, BorderLayout.NORTH);
            side.add(navW, BorderLayout.CENTER);
            side.add(jauge, BorderLayout.SOUTH);

            // --- 4 cartes ---
            JPanel cartes = new JPanel(new GridLayout(1, 4, 14, 0));
            cartes.setOpaque(false);
            lRev = new JLabel(); lDep = new JLabel(); lEpa = new JLabel(); lSolde = new JLabel();
            String[] tt = {"REVENUS TOTAUX (+)", "DÉPENSES TOTALES (-)", "ÉPARGNE PLACÉE", "RESTE À VIVRE"};
            JLabel[] ll = {lRev, lDep, lEpa, lSolde};
            Color[] cc = {GREEN, RED, BLUE, PURPLE};
            for (int i = 0; i < 4; i++) {
                JPanel c = new JPanel(new BorderLayout(5, 6));
                c.setBackground(cc[i]);
                c.setBorder(new CompoundBorder(new LineBorder(Color.WHITE, 2), new EmptyBorder(10, 16, 10, 16)));
                c.add(label(tt[i], f(Font.BOLD, 12), SUR), BorderLayout.NORTH);
                ll[i].setForeground(SUR);
                ll[i].setFont(f(Font.BOLD, 20));
                c.add(ll[i], BorderLayout.CENTER);
                cartes.add(c);
            }

            // --- écrans ---
            centre.setOpaque(false);
            centre.add(ecranDash(), "0");
            centre.add(ecranHisto(), "1");
            courbe = new GraphCourbe();
            centre.add(courbe, "2");
            donut = new GraphDonut();
            centre.add(donut, "3");
            mois = new GraphMois();
            centre.add(mois, "4");
            centre.add(ecranAnalyse(), "5");
            centre.add(ecranRec(), "6");
            centre.add(ecranObjectifs(), "7");

            // --- pied ---
            JPanel pied = new JPanel(new GridLayout(1, 2, 14, 0));
            pied.setOpaque(false);
            lInfoEpargne = label("", f(Font.BOLD, 13), TXT);
            lInfoBudget = label("", f(Font.BOLD, 13), TXT);
            barEpargne = new JProgressBar(0, 100);
            barBudget = new JProgressBar(0, 100);
            pied.add(blocBarre(lInfoEpargne, barEpargne));
            pied.add(blocBarre(lInfoBudget, barBudget));

            JPanel droite = new JPanel(new BorderLayout(0, 12));
            droite.setOpaque(false);
            droite.add(cartes, BorderLayout.NORTH);
            droite.add(centre, BorderLayout.CENTER);
            droite.add(pied, BorderLayout.SOUTH);
            add(side, BorderLayout.WEST);
            add(droite, BorderLayout.CENTER);

            reglerTitre();
            actualiser();
            changer(0);
        }

        File recFile() { return new File(DOSSIER, p.id + ".rec"); }
        Color acc() { return COULEURS[p.couleur]; }
        String s(int r, int c) { return modele.getValueAt(r, c).toString(); }
        double mt(int r) { return ((Number) modele.getValueAt(r, 4)).doubleValue(); }
        Object[] ligne(int r) {
            return new Object[]{modele.getValueAt(r, 0), modele.getValueAt(r, 1), modele.getValueAt(r, 2), modele.getValueAt(r, 3), modele.getValueAt(r, 4)};
        }
        void reglerTitre() { lTitre.setText(cut(p.nom, 15)); lTitre.setForeground(acc()); }
        void sauver() {
            try { ecrire(new File(DOSSIER, p.id + ".csv"), versLignes(modele)); ecrire(recFile(), versLignes(recModele)); }
            catch (IOException ex) { toast("Sauvegarde impossible : " + ex.getMessage(), RED); }
        }
        void info(String m) { JOptionPane.showMessageDialog(this, m); }
        void erreur(String m) { JOptionPane.showMessageDialog(this, m, "Erreur", JOptionPane.ERROR_MESSAGE); }

        JPanel blocBarre(JLabel l, JProgressBar b) {
            JPanel pn = new JPanel(new BorderLayout(0, 6));
            pn.setBackground(PANEL);
            pn.setBorder(new CompoundBorder(new LineBorder(LINE, 2), new EmptyBorder(10, 16, 10, 16)));
            b.setStringPainted(true);
            b.setBackground(TRACK);
            b.setForeground(GREEN);
            b.setPreferredSize(new Dimension(0, 22));
            pn.add(l, BorderLayout.NORTH);
            pn.add(b, BorderLayout.CENTER);
            return pn;
        }

        void changer(int k) {
            ecran = k;
            for (int i = 0; i < navs.length; i++) {
                navs[i].setBackground(i == k ? acc() : INPUT);
                navs[i].setForeground(i == k ? SUR : TXT2);
            }
            cl.show(centre, String.valueOf(k));
            if (k == 0) histo.lancer();
            else if (k == 2) { courbe.rafraichirFiltres(); courbe.lancer(); }
            else if (k == 3) donut.lancer();
            else if (k == 4) mois.lancer();
            else if (k == 5) majAnalyse();
            else if (k == 7) majObjectif();
        }

        void reglages() {
            Projet n = dialogueProjet(p);
            if (n == null) return;
            p.nom = n.nom; p.icone = n.icone; p.couleur = n.couleur; p.budget = n.budget; p.objectif = n.objectif;
            sauverProjets();
            reglerTitre();
            actualiser();
            changer(ecran);
            toast("Réglages enregistrés", GREEN);
        }

        void rapport() {
            sauver();
            try {
                File d = new File(DOSSIER, "rapports");
                d.mkdirs();
                File fi = new File(d, slug(p.nom) + "_" + LocalDate.now() + ".html");
                ecrire(fi, Collections.singletonList(rapportHtml(p, lireOps(p.id))));
                ouvrirFichier(fi);
            } catch (IOException ex) { erreur("Rapport impossible : " + ex.getMessage()); }
        }

        double depensesMois(YearMonth ym) {
            double t = 0;
            String pre = ym.toString();
            for (int i = 0; i < modele.getRowCount(); i++)
                if ("Dépense".equals(s(i, 3)) && s(i, 0).startsWith(pre)) t += mt(i);
            return t;
        }

        double rythmeEpargne() {
            Set<String> ms = new HashSet<>();
            double tot = 0;
            for (int i = 0; i < modele.getRowCount(); i++)
                if ("Épargne".equals(s(i, 3))) { ms.add(s(i, 0).substring(0, 7)); tot += mt(i); }
            return ms.isEmpty() ? 0 : tot / ms.size();
        }

        double score() {
            if (revenus <= 0) return 0;
            double pEp = Math.min(1, epargne / revenus / 0.2) * 40, ratio = depenses / revenus;
            double pDep = ratio <= 0.7 ? 40 : ratio >= 1 ? 0 : (1 - ratio) / 0.3 * 40, pBud = 20;
            double dm = depensesMois(YearMonth.now());
            if (p.budget > 0 && dm > p.budget) pBud = Math.max(0, 20 * (1 - (dm / p.budget - 1)));
            return Math.max(0, Math.min(100, pEp + pDep + pBud));
        }

        List<String> toutesCats() {
            Set<String> st = new LinkedHashSet<>(Arrays.asList(CATS));
            for (int i = 0; i < modele.getRowCount(); i++) st.add(s(i, 2));
            return new ArrayList<>(st);
        }

        void majCategories() {
            if (cCat == null) return;
            Object sel = cCat.getSelectedItem();
            cCat.removeAllItems();
            for (String c : toutesCats()) cCat.addItem(c);
            if (sel != null) cCat.setSelectedItem(sel);
        }

        // ---------- calculs globaux ----------
        void actualiser() {
            double rev = 0, dep = 0, epa = 0;
            for (int i = 0; i < modele.getRowCount(); i++) {
                String t = s(i, 3);
                if ("Revenu".equals(t)) rev += mt(i);
                else if ("Dépense".equals(t)) dep += mt(i);
                else if ("Épargne".equals(t)) epa += mt(i);
            }
            revenus = rev; depenses = dep; epargne = epa;
            sauver();
            lRev.setText(FMT.format(rev) + " " + DEV);
            lDep.setText(FMT.format(dep) + " " + DEV);
            lEpa.setText(FMT.format(epa) + " " + DEV);
            lSolde.setText(FMT.format(rev - dep - epa) + " " + DEV);
            int taux = rev > 0 ? (int) Math.round(epa / rev * 100) : 0;
            barEpargne.setValue(Math.min(100, taux * 100 / 20));
            barEpargne.setString(taux + "% de vos revenus épargnés");
            lInfoEpargne.setText("Taux d'épargne : " + taux + "%  (objectif recommandé : 20% des revenus)");
            double dm = depensesMois(YearMonth.now());
            if (p.budget > 0) {
                int pc = (int) Math.min(100, dm / p.budget * 100);
                barBudget.setValue(pc);
                barBudget.setForeground(pc >= 100 ? RED : pc >= 80 ? ORANGE : GREEN);
                barBudget.setString(FMT.format(dm) + " / " + FMT.format(p.budget) + " " + DEV);
                lInfoBudget.setText(dm > p.budget ? "Budget du mois DÉPASSÉ !" : "Budget dépenses du mois en cours");
            } else {
                barBudget.setValue(0);
                barBudget.setString("Dépenses du mois : " + FMT.format(dm) + " " + DEV);
                lInfoBudget.setText("Budget mensuel non défini (bouton « Réglages du projet »)");
            }
            double sc = score();
            jauge.cible = sc;
            jauge.cap = modele.getRowCount() == 0 || rev <= 0 ? "Ajoutez des revenus" : scoreTexte(sc);
            majCategories();
            if (histo != null) histo.maj();
            if (donut != null) donut.maj();
            if (mois != null) mois.maj();
            if (courbe != null) { courbe.rafraichirFiltres(); courbe.maj(); }
            if (lCompte != null) filtrer();
            majObjectif();
            majAnalyse();
        }

        // ---------- tableaux ----------
        JTable creerTable(DefaultTableModel m) {
            JTable t = new JTable(m);
            t.setRowHeight(32);
            t.setBackground(TBL);
            t.setForeground(TXT);
            t.setGridColor(TGRID);
            t.setFont(f(Font.PLAIN, 14));
            t.getTableHeader().setBackground(THEAD);
            t.getTableHeader().setForeground(TXT);
            t.getTableHeader().setFont(f(Font.BOLD, 14));
            t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(tb, v, sel, foc, row, col);
                    String type = tb.getValueAt(row, 3).toString();
                    setBorder(new EmptyBorder(0, 8, 0, 8));
                    setHorizontalAlignment(col == 4 ? RIGHT : LEFT);
                    if (col == 4 && v instanceof Number) setText(FMT.format(v) + " " + DEV);
                    if (sel) { setBackground(SEL); setForeground(Color.WHITE); }
                    else {
                        setBackground(row % 2 == 0 ? PANEL : ZEBRA);
                        if (col == 3 || col == 4) {
                            setForeground("Revenu".equals(type) ? GREEN : "Dépense".equals(type) ? RED : BLUE);
                            setFont(f(Font.BOLD, 14));
                        } else { setForeground(TXT); setFont(f(Font.PLAIN, 14)); }
                    }
                    return this;
                }
            });
            return t;
        }

        // ---------- écran 0 : tableau de bord ----------
        JPanel ecranDash() {
            JPanel ecr = new JPanel(new BorderLayout(14, 0));
            ecr.setOpaque(false);
            JPanel form = new JPanel(new BorderLayout(0, 12));
            form.setBackground(PANEL);
            form.setPreferredSize(new Dimension(320, 0));
            form.setBorder(new CompoundBorder(new LineBorder(LINE, 2), new EmptyBorder(16, 16, 16, 16)));
            tDesc = new JTextField();
            tMontant = new JTextField();
            tDate = new JTextField(LocalDate.now().toString());
            cType = new JComboBox<>(TYPES);
            cCat = new JComboBox<>();
            cCat.setEditable(true);
            for (String c : toutesCats()) cCat.addItem(c);
            String[] lib = {"Description :", "Montant (" + DEV + ") :", "Type de flux :", "Catégorie (modifiable, tapez-en une) :", "Date (AAAA-MM-JJ) :"};
            JComponent[] cs = {tDesc, tMontant, cType, cCat, tDate};
            JPanel champs = new JPanel(new GridLayout(10, 1, 0, 3));
            champs.setOpaque(false);
            for (int i = 0; i < cs.length; i++) {
                styler(cs[i]);
                champs.add(label(lib[i], f(Font.BOLD, 12), TXT2));
                champs.add(cs[i]);
            }
            tDesc.addActionListener(e -> tMontant.requestFocus());
            tMontant.addActionListener(e -> ajouter());
            tDate.addActionListener(e -> ajouter());
            JPanel milieu = new JPanel(new BorderLayout());
            milieu.setOpaque(false);
            milieu.add(champs, BorderLayout.NORTH);
            lStatut = label(" ", f(Font.BOLD, 13), GREEN);
            JButton ajout = bouton("Ajouter l'opération", GREEN, SUR);
            ajout.setFont(f(Font.BOLD, 14));
            ajout.setPreferredSize(new Dimension(0, 42));
            ajout.addActionListener(e -> ajouter());
            JPanel sud = new JPanel(new BorderLayout(0, 8));
            sud.setOpaque(false);
            sud.add(lStatut, BorderLayout.NORTH);
            sud.add(ajout, BorderLayout.CENTER);
            form.add(label("Nouvelle opération", f(Font.BOLD, 16), TXT), BorderLayout.NORTH);
            form.add(milieu, BorderLayout.CENTER);
            form.add(sud, BorderLayout.SOUTH);
            histo = new GraphHisto();
            ecr.add(form, BorderLayout.WEST);
            ecr.add(histo, BorderLayout.CENTER);
            return ecr;
        }

        void statut(String m, Color c) {
            lStatut.setText(m);
            lStatut.setForeground(c);
            Timer t = new Timer(3000, e -> lStatut.setText(" "));
            t.setRepeats(false);
            t.start();
        }

        void ajouter() {
            String desc = tDesc.getText().trim();
            if (desc.isEmpty()) { statut("Description obligatoire", ORANGE); return; }
            double m;
            try {
                m = nombre(tMontant.getText());
                if (m <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) { statut("Montant invalide (> 0)", RED); return; }
            LocalDate d;
            try {
                d = tDate.getText().isBlank() ? LocalDate.now() : LocalDate.parse(tDate.getText().trim());
            } catch (DateTimeParseException ex) { statut("Date : format AAAA-MM-JJ", RED); return; }
            String type = (String) cType.getSelectedItem();
            Object sc = cCat.getSelectedItem();
            String cat = sc == null || sc.toString().isBlank() ? "Autre" : sc.toString().trim().replace(";", ",");
            long montant = Math.round(m);
            if ("Dépense".equals(type) && montant > revenus - depenses - epargne) {
                int r = JOptionPane.showConfirmDialog(this, "Cette dépense dépasse votre reste à vivre (" + FMT.format(revenus - depenses - epargne)
                        + " " + DEV + ").\nL'ajouter quand même ?", "Solde insuffisant", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (r != JOptionPane.YES_OPTION) return;
            }
            modele.addRow(new Object[]{d.toString(), desc, cat, type, Long.valueOf(montant)});
            tDesc.setText("");
            tMontant.setText("");
            tDate.setText(LocalDate.now().toString());
            actualiser();
            histo.lancer();
            toast("" + type + " de " + FMT.format(montant) + " " + DEV + " ajouté", "Dépense".equals(type) ? RED : "Revenu".equals(type) ? GREEN : BLUE);
            if ("Dépense".equals(type) && p.budget > 0 && depensesMois(YearMonth.from(d)) > p.budget)
                JOptionPane.showMessageDialog(this, "Vous avez dépassé votre budget mensuel de " + FMT.format(p.budget) + " " + DEV + " !", "Budget dépassé", JOptionPane.WARNING_MESSAGE);
            tDesc.requestFocus();
        }

        Object[] dialogueOp(Object[] init, String titre) {
            JTextField d = new JTextField((String) init[1]), m = new JTextField(String.valueOf(init[4])), dt = new JTextField((String) init[0]);
            JComboBox<String> ty = new JComboBox<>(TYPES), ca = new JComboBox<>(toutesCats().toArray(new String[0]));
            ca.setEditable(true);
            ty.setSelectedItem(init[3]);
            ca.setSelectedItem(init[2]);
            JPanel pn = new JPanel(new GridLayout(0, 2, 8, 10));
            pn.setBackground(PANEL);
            pn.setPreferredSize(new Dimension(440, 220));
            String[] lib = {"Description :", "Montant (" + DEV + ") :", "Type :", "Catégorie :", "Date (AAAA-MM-JJ) :"};
            JComponent[] cs = {d, m, ty, ca, dt};
            for (int i = 0; i < cs.length; i++) { styler(cs[i]); pn.add(label(lib[i], f(Font.BOLD, 12), TXT2)); pn.add(cs[i]); }
            while (true) {
                if (JOptionPane.showConfirmDialog(this, pn, titre, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return null;
                try {
                    if (d.getText().isBlank()) throw new IllegalArgumentException();
                    double v = nombre(m.getText());
                    if (v <= 0) throw new NumberFormatException();
                    LocalDate ld = LocalDate.parse(dt.getText().trim());
                    Object sc = ca.getSelectedItem();
                    return new Object[]{ld.toString(), d.getText().trim().replace(";", ","), sc == null || sc.toString().isBlank() ? "Autre" : sc.toString().trim().replace(";", ","),
                        ty.getSelectedItem(), Long.valueOf(Math.round(v))};
                } catch (Exception ex) { erreur("Vérifiez : description, montant > 0 et date AAAA-MM-JJ."); }
            }
        }

        // ---------- écran 1 : historique ----------
        JPanel ecranHisto() {
            JPanel pn = new JPanel(new BorderLayout(0, 10));
            pn.setBackground(PANEL);
            pn.setBorder(new CompoundBorder(new LineBorder(LINE, 2), new EmptyBorder(16, 16, 16, 16)));
            tRecherche = new JTextField();
            styler(tRecherche);
            tRecherche.setPreferredSize(new Dimension(220, 32));
            tRecherche.setToolTipText("Rechercher dans la description ou la catégorie");
            tRecherche.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { filtrer(); }
                public void removeUpdate(DocumentEvent e) { filtrer(); }
                public void changedUpdate(DocumentEvent e) { filtrer(); }
            });
            cFiltreType = new JComboBox<>(new String[]{"Tous", "Revenu", "Dépense", "Épargne"});
            styler(cFiltreType);
            cFiltreType.setPreferredSize(new Dimension(120, 32));
            cFiltreType.addActionListener(e -> filtrer());
            JPanel recherche = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            recherche.setOpaque(false);
            recherche.add(label("", f(Font.PLAIN, 16), TXT));
            recherche.add(tRecherche);
            recherche.add(cFiltreType);
            JPanel ligne1 = new JPanel(new BorderLayout());
            ligne1.setOpaque(false);
            ligne1.add(label("Historique des transactions", f(Font.BOLD, 17), TXT), BorderLayout.WEST);
            ligne1.add(recherche, BorderLayout.EAST);

            JButton bMod = bouton("Modifier", BLUE, SUR), bSup = bouton("Supprimer", RED, Color.WHITE),
                    bUndo = bouton("Annuler (Ctrl+Z)", ORANGE, SUR), bExp = bouton("Exporter CSV", PURPLE, Color.WHITE),
                    bImp = bouton("Importer CSV", INPUT, TXT);
            bMod.addActionListener(e -> modifier());
            bSup.addActionListener(e -> supprimer());
            bUndo.addActionListener(e -> annuler());
            bExp.addActionListener(e -> exporter());
            bImp.addActionListener(e -> importer());
            lCompte = label("", f(Font.PLAIN, 12), TXT2);
            JPanel boutons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            boutons.setOpaque(false);
            for (JButton b : new JButton[]{bMod, bSup, bUndo, bExp, bImp}) boutons.add(b);
            JPanel ligne2 = new JPanel(new BorderLayout());
            ligne2.setOpaque(false);
            ligne2.add(boutons, BorderLayout.WEST);
            ligne2.add(lCompte, BorderLayout.EAST);
            JPanel haut = new JPanel(new GridLayout(2, 1, 0, 10));
            haut.setOpaque(false);
            haut.add(ligne1);
            haut.add(ligne2);

            table = creerTable(modele);
            tri = new TableRowSorter<>(modele);
            table.setRowSorter(tri);
            tri.setSortKeys(Collections.singletonList(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
            table.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) modifier(); }
            });
            JScrollPane sp = new JScrollPane(table);
            sp.getViewport().setBackground(PANEL);
            sp.setBorder(new LineBorder(LINE));
            pn.add(haut, BorderLayout.NORTH);
            pn.add(sp, BorderLayout.CENTER);
            return pn;
        }

        void filtrer() {
            if (tri == null) return;
            List<RowFilter<Object, Object>> fl = new ArrayList<>();
            String q = tRecherche.getText().trim();
            if (!q.isEmpty()) fl.add(RowFilter.regexFilter("(?i)" + Pattern.quote(q), 1, 2));
            String t = (String) cFiltreType.getSelectedItem();
            if (t != null && !"Tous".equals(t)) fl.add(RowFilter.regexFilter("^" + Pattern.quote(t) + "$", 3));
            tri.setRowFilter(fl.isEmpty() ? null : RowFilter.andFilter(fl));
            lCompte.setText(table.getRowCount() + " / " + modele.getRowCount() + " opération(s) affichée(s)");
        }

        void modifier() {
            int[] rs = table.getSelectedRows();
            if (rs.length != 1) { toast("Sélectionnez UNE ligne à modifier", ORANGE); return; }
            int r = table.convertRowIndexToModel(rs[0]);
            Object[] n = dialogueOp(ligne(r), "Modifier l'opération");
            if (n == null) return;
            for (int c = 0; c < 5; c++) modele.setValueAt(n[c], r, c);
            actualiser();
            toast("Opération modifiée", BLUE);
        }

        void supprimer() {
            int[] rs = table.getSelectedRows();
            if (rs.length == 0) { toast("Cliquez d'abord sur une (ou plusieurs) ligne(s)", ORANGE); return; }
            int[] ms = new int[rs.length];
            for (int i = 0; i < rs.length; i++) ms[i] = table.convertRowIndexToModel(rs[i]);
            Arrays.sort(ms);
            for (int i = ms.length - 1; i >= 0; i--) { corbeille.push(ligne(ms[i])); modele.removeRow(ms[i]); }
            actualiser();
            toast(ms.length + " opération(s) supprimée(s) — Ctrl+Z pour annuler", ORANGE);
        }

        void annuler() {
            if (corbeille.isEmpty()) { toast("Rien à restaurer", ORANGE); return; }
            modele.addRow(corbeille.pop());
            actualiser();
            toast("Opération restaurée", GREEN);
        }

        void exporter() {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(slug(p.nom) + ".csv"));
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            File fi = fc.getSelectedFile();
            if (!fi.getName().toLowerCase().endsWith(".csv")) fi = new File(fi.getPath() + ".csv");
            List<String> l = versLignes(modele);
            l.add(0, "\uFEFFDate;Description;Catégorie;Type;Montant");
            try { Files.write(fi.toPath(), l, StandardCharsets.UTF_8); toast("Export réussi : " + fi.getName(), GREEN); }
            catch (IOException ex) { erreur("Export impossible : " + ex.getMessage()); }
        }

        void importer() {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
            try {
                int n = 0;
                for (String s : Files.readAllLines(fc.getSelectedFile().toPath(), StandardCharsets.UTF_8)) {
                    Object[] o = parseLigne(s);
                    if (o != null) { modele.addRow(o); n++; }
                }
                actualiser();
                toast(n + " opération(s) importée(s)", GREEN);
            } catch (IOException ex) { erreur("Import impossible : " + ex.getMessage()); }
        }

        // ---------- écran 5 : analyse intelligente ----------
        JPanel ecranAnalyse() {
            JPanel pn = new JPanel(new BorderLayout(0, 14));
            pn.setBackground(PANEL);
            pn.setBorder(new CompoundBorder(new LineBorder(LINE, 2), new EmptyBorder(16, 16, 16, 16)));
            JPanel tuiles = new JPanel(new GridLayout(2, 3, 12, 12));
            tuiles.setOpaque(false);
            String[] tt = {"SCORE DE SANTÉ", "DÉPENSE MOYENNE / JOUR", "PRÉVISION DÉPENSES FIN DE MOIS", "PLUS GROSSE DÉPENSE", "CATÉGORIE DOMINANTE", "MOIS LE PLUS DÉPENSIER"};
            Color[] cc = {GREEN, ORANGE, BLUE, RED, PURPLE, YELLOW};
            for (int i = 0; i < 6; i++) {
                JPanel t = new JPanel(new BorderLayout(0, 8));
                t.setBackground(TILE);
                t.setBorder(new CompoundBorder(new LineBorder(cc[i], 2), new EmptyBorder(12, 14, 12, 14)));
                t.add(label(tt[i], f(Font.BOLD, 11), cc[i]), BorderLayout.NORTH);
                kpi[i] = label("—", f(Font.BOLD, 19), TXT);
                t.add(kpi[i], BorderLayout.CENTER);
                tuiles.add(t);
            }
            conseils = zoneTexte(TXT2);
            conseils.setFont(f(Font.PLAIN, 14));
            conseils.setRows(7);
            JPanel bas = new JPanel(new BorderLayout(0, 6));
            bas.setOpaque(false);
            bas.add(label("Conseils automatiques", f(Font.BOLD, 15), YELLOW), BorderLayout.NORTH);
            bas.add(conseils, BorderLayout.CENTER);
            pn.add(label("Analyse intelligente du projet", f(Font.BOLD, 17), TXT), BorderLayout.NORTH);
            pn.add(tuiles, BorderLayout.CENTER);
            pn.add(bas, BorderLayout.SOUTH);
            return pn;
        }

        void majAnalyse() {
            if (kpi[5] == null || conseils == null) return;
            YearMonth ym = YearMonth.now();
            double dm = depensesMois(ym), sc = score();
            int j = LocalDate.now().getDayOfMonth(), len = ym.lengthOfMonth(), n = modele.getRowCount();
            double prev = dm / j * len;
            kpi[0].setText(n == 0 || revenus <= 0 ? "—" : (int) sc + " / 100 — " + scoreTexte(sc));
            kpi[0].setForeground(n == 0 || revenus <= 0 ? TXT : scoreCouleur(sc));
            kpi[1].setText(FMT.format(dm / j) + " " + DEV);
            kpi[2].setText(FMT.format(prev) + " " + DEV);
            int bi = -1;
            double bm = 0;
            for (int i = 0; i < n; i++) if ("Dépense".equals(s(i, 3)) && mt(i) > bm) { bm = mt(i); bi = i; }
            kpi[3].setText(bi < 0 ? "—" : cut(FMT.format(bm) + " · " + s(bi, 1), 24));
            Map<String, Double> cats = sommeParCat("Dépense");
            String dom = "—";
            double domPct = 0;
            if (!cats.isEmpty()) {
                Map.Entry<String, Double> e = cats.entrySet().iterator().next();
                domPct = depenses > 0 ? e.getValue() / depenses * 100 : 0;
                dom = cut(e.getKey(), 16) + " (" + (int) Math.round(domPct) + "%)";
            }
            kpi[4].setText(dom);
            Map<String, Double> pm = new TreeMap<>();
            for (int i = 0; i < n; i++) if ("Dépense".equals(s(i, 3))) pm.merge(s(i, 0).substring(0, 7), mt(i), Double::sum);
            String mp = "—";
            double mx = 0;
            for (Map.Entry<String, Double> e : pm.entrySet()) if (e.getValue() > mx) { mx = e.getValue(); mp = e.getKey() + " · " + FMT.format(mx); }
            kpi[5].setText(mp);

            StringBuilder c = new StringBuilder();
            if (n == 0 || revenus <= 0) c.append("• Ajoutez vos revenus et vos dépenses pour obtenir une analyse personnalisée.\n");
            else {
                double taux = epargne / revenus * 100, ratio = depenses / revenus * 100;
                if (taux < 10) c.append("• Votre taux d'épargne est de ").append((int) taux).append("% : visez au moins 10 à 20% de vos revenus.\n");
                else if (taux >= 20) c.append("• Excellent : vous épargnez ").append((int) taux).append("% de vos revenus (objectif 20% atteint).\n");
                if (ratio > 80) c.append("• Vos dépenses représentent ").append((int) ratio).append("% de vos revenus : cherchez des postes à réduire.\n");
                if (revenus - depenses - epargne < 0) c.append("• Attention : votre reste à vivre est négatif (").append(FMT.format(revenus - depenses - epargne)).append(" ").append(DEV).append(").\n");
                if (domPct > 50) c.append("• La catégorie « ").append(cats.keySet().iterator().next()).append(" » pèse ").append((int) domPct).append("% de vos dépenses.\n");
                if (p.budget > 0 && dm > p.budget) c.append("• Budget du mois dépassé de ").append(FMT.format(dm - p.budget)).append(" ").append(DEV).append(".\n");
                else if (p.budget > 0 && prev > p.budget) c.append("• À ce rythme, vous dépasserez votre budget mensuel (prévision : ").append(FMT.format(prev)).append(" ").append(DEV).append(").\n");
                double ry = rythmeEpargne();
                if (p.objectif > 0 && epargne < p.objectif && ry > 0)
                    c.append("• Objectif d'épargne : encore ").append((int) Math.ceil((p.objectif - epargne) / ry)).append(" mois au rythme de ").append(FMT.format(ry)).append(" ").append(DEV).append("/mois.\n");
                if (c.length() == 0) c.append("• Rien à signaler : vos finances sont bien maîtrisées. Continuez ainsi !\n");
            }
            conseils.setText(c.toString());
        }

        // ---------- écran 6 : opérations récurrentes ----------
        JPanel ecranRec() {
            JPanel pn = new JPanel(new BorderLayout(0, 10));
            pn.setBackground(PANEL);
            pn.setBorder(new CompoundBorder(new LineBorder(LINE, 2), new EmptyBorder(16, 16, 16, 16)));
            JButton bAdd = bouton("Ajouter un modèle", GREEN, SUR), bDel = bouton("Supprimer", RED, Color.WHITE), bApp = bouton("Appliquer pour ce mois", PURPLE, Color.WHITE);
            bAdd.addActionListener(e -> ajouterRec());
            bDel.addActionListener(e -> supprimerRec());
            bApp.addActionListener(e -> appliquerRec());
            JPanel boutons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            boutons.setOpaque(false);
            boutons.add(bAdd);
            boutons.add(bDel);
            boutons.add(bApp);
            JTextArea aide = zoneTexte(TXT2);
            aide.setFont(f(Font.PLAIN, 13));
            aide.setRows(2);
            aide.setText("Modèles d'opérations qui reviennent chaque mois (loyer, abonnement, salaire…). Le jour du mois est celui de la date saisie. "
                    + "« Appliquer » crée les opérations du mois en cours, sans doublon.");
            JPanel haut = new JPanel(new BorderLayout(0, 8));
            haut.setOpaque(false);
            haut.add(label("Opérations récurrentes", f(Font.BOLD, 17), TXT), BorderLayout.NORTH);
            haut.add(aide, BorderLayout.CENTER);
            haut.add(boutons, BorderLayout.SOUTH);
            tableRec = creerTable(recModele);
            JScrollPane sp = new JScrollPane(tableRec);
            sp.getViewport().setBackground(PANEL);
            sp.setBorder(new LineBorder(LINE));
            pn.add(haut, BorderLayout.NORTH);
            pn.add(sp, BorderLayout.CENTER);
            return pn;
        }

        void ajouterRec() {
            Object[] n = dialogueOp(new Object[]{LocalDate.now().toString(), "", "Autre", "Dépense", Long.valueOf(0)}, "Nouveau modèle récurrent");
            if (n == null) return;
            recModele.addRow(n);
            sauver();
            toast("Modèle ajouté", GREEN);
        }

        void supprimerRec() {
            int[] rs = tableRec.getSelectedRows();
            if (rs.length == 0) { toast("Sélectionnez un modèle", ORANGE); return; }
            for (int i = rs.length - 1; i >= 0; i--) recModele.removeRow(rs[i]);
            sauver();
        }

        void appliquerRec() {
            YearMonth ym = YearMonth.now();
            int n = 0;
            for (int i = 0; i < recModele.getRowCount(); i++) {
                String desc = recModele.getValueAt(i, 1).toString(), type = recModele.getValueAt(i, 3).toString();
                int jour = LocalDate.parse(recModele.getValueAt(i, 0).toString()).getDayOfMonth();
                String date = ym.atDay(Math.min(jour, ym.lengthOfMonth())).toString();
                boolean existe = false;
                for (int k = 0; k < modele.getRowCount() && !existe; k++)
                    existe = s(k, 1).equals(desc) && s(k, 3).equals(type) && s(k, 0).startsWith(ym.toString());
                if (existe) continue;
                modele.addRow(new Object[]{date, desc, recModele.getValueAt(i, 2), type, recModele.getValueAt(i, 4)});
                n++;
            }
            actualiser();
            toast(n == 0 ? "Rien à ajouter : déjà appliqué ce mois-ci" : n + " opération(s) récurrente(s) ajoutée(s)", n == 0 ? ORANGE : GREEN);
        }

        // ---------- écran 7 : objectifs ----------
        JScrollPane ecranObjectifs() {
            JPanel col = new JPanel();
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
            col.setBackground(PANEL);
            col.setBorder(new EmptyBorder(24, 36, 24, 36));
            barObj = new JProgressBar(0, 100);
            barObj.setStringPainted(true);
            barObj.setForeground(PURPLE);
            barObj.setBackground(TRACK);
            barObj.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            barObj.setAlignmentX(Component.LEFT_ALIGNMENT);
            tCible = new JTextField(p.objectif > 0 ? String.valueOf((long) p.objectif) : "450000");
            tMois = new JTextField("12");
            for (JTextField t : new JTextField[]{tCible, tMois}) {
                styler(t);
                t.setFont(f(Font.BOLD, 17));
                t.setForeground(GREEN);
                t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
                t.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            res1 = zoneTexte(GREEN);
            res2 = zoneTexte(BLUE);
            JButton b1 = bouton("Calculer le temps nécessaire selon mon rythme d'épargne", PURPLE, Color.WHITE);
            JButton b2 = bouton("Calculer l'épargne mensuelle nécessaire", BLUE, SUR);
            b1.setAlignmentX(Component.LEFT_ALIGNMENT);
            b2.setAlignmentX(Component.LEFT_ALIGNMENT);
            b1.addActionListener(e -> simulerTemps());
            b2.addActionListener(e -> simulerMensuel());
            JLabel t1 = label("Simulateur d'objectifs & projection", f(Font.BOLD, 18), BLUE);
            t1.setAlignmentX(Component.LEFT_ALIGNMENT);
            Component[] cs = {t1, Box.createVerticalStrut(12), barObj, Box.createVerticalStrut(18),
                lbl("Montant cible du projet (" + DEV + ") — mémorisé pour ce dossier :"), tCible, Box.createVerticalStrut(8), b1, Box.createVerticalStrut(6), res1,
                Box.createVerticalStrut(14), lbl("En combien de mois voulez-vous y arriver ?"), tMois, Box.createVerticalStrut(8), b2, Box.createVerticalStrut(6), res2};
            for (Component c : cs) col.add(c);
            JScrollPane sp = new JScrollPane(col);
            sp.setBorder(new LineBorder(LINE, 2));
            sp.getViewport().setBackground(PANEL);
            return sp;
        }

        JLabel lbl(String t) {
            JLabel l = label(t, f(Font.PLAIN, 14), TXT);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            return l;
        }

        void majObjectif() {
            if (barObj == null) return;
            if (p.objectif > 0) {
                barObj.setValue((int) Math.min(100, epargne * 100 / p.objectif));
                barObj.setString((int) (epargne * 100 / p.objectif) + "% de l'objectif  (" + FMT.format(epargne) + " / " + FMT.format(p.objectif) + " " + DEV + ")");
            } else {
                barObj.setValue(0);
                barObj.setString("Aucun objectif défini — saisissez un montant ci-dessous");
            }
        }

        double lireCible() {
            try {
                double c = nombre(tCible.getText());
                if (c <= 0) throw new NumberFormatException();
                return c;
            } catch (NumberFormatException e) { erreur("Entrez un montant cible numérique > 0."); return -1; }
        }

        void simulerTemps() {
            double cible = lireCible();
            if (cible < 0) return;
            p.objectif = cible;
            sauverProjets();
            majObjectif();
            majAnalyse();
            double restant = Math.max(0, cible - epargne), rythme = rythmeEpargne();
            res1.setForeground(GREEN);
            if (restant == 0) res1.setText("Objectif atteint ! Vous avez déjà " + FMT.format(epargne) + " " + DEV + " d'épargne.");
            else if (rythme <= 0) {
                res1.setForeground(RED);
                res1.setText("Ajoutez d'abord une opération de type « Épargne » pour calculer votre rythme.");
            } else {
                int ms = (int) Math.ceil(restant / rythme);
                res1.setText("Rythme moyen : " + FMT.format(rythme) + " " + DEV + "/mois. Il reste " + FMT.format(restant) + " " + DEV + " à réunir, soit environ "
                        + ms + " mois (vers " + LocalDate.now().plusMonths(ms).format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH)) + ").");
            }
        }

        void simulerMensuel() {
            double cible = lireCible();
            if (cible < 0) return;
            int ms;
            try { ms = Integer.parseInt(tMois.getText().trim()); if (ms <= 0) throw new NumberFormatException(); }
            catch (NumberFormatException e) { erreur("Entrez un nombre de mois entier > 0."); return; }
            double restant = Math.max(0, cible - epargne);
            res2.setText(restant == 0 ? "Objectif déjà atteint !" : "Pour réunir " + FMT.format(restant) + " " + DEV + " en " + ms + " mois, épargnez environ "
                    + FMT.format(Math.ceil(restant / ms)) + " " + DEV + " par mois.");
        }

        // ---------- graphiques ----------
        Map<String, Double> sommeParCat(String type) {
            Map<String, Double> m = new HashMap<>();
            for (int i = 0; i < modele.getRowCount(); i++) if (type.equals(s(i, 3))) m.merge(s(i, 2), mt(i), Double::sum);
            List<Map.Entry<String, Double>> l = new ArrayList<>(m.entrySet());
            l.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            Map<String, Double> r = new LinkedHashMap<>();
            for (Map.Entry<String, Double> e : l) r.put(e.getKey(), e.getValue());
            return r;
        }

        void vide(Graphics2D g, int w, int h, String msg) {
            g.setFont(f(Font.ITALIC, 14));
            g.setColor(TXT2);
            centre(g, msg, w / 2, h / 2);
        }

        abstract class Anime extends JPanel {
            float anim = 1f;
            Timer t;
            int mx = -1, my = -1;
            final JPanel zone;

            Anime() {
                super(new BorderLayout(0, 8));
                setBackground(PANEL);
                setBorder(new CompoundBorder(new LineBorder(LINE, 2), new EmptyBorder(12, 12, 12, 12)));
                zone = new JPanel() {
                    @Override protected void paintComponent(Graphics g0) {
                        super.paintComponent(g0);
                        Graphics2D g = (Graphics2D) g0.create();
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        dessiner(g, getWidth(), getHeight());
                        g.dispose();
                    }
                };
                zone.setBackground(PANEL);
                zone.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override public void mouseMoved(MouseEvent e) { mx = e.getX(); my = e.getY(); zone.repaint(); }
                });
                zone.addMouseListener(new MouseAdapter() {
                    @Override public void mouseExited(MouseEvent e) { mx = my = -1; zone.repaint(); }
                });
                add(zone, BorderLayout.CENTER);
            }

            abstract void dessiner(Graphics2D g, int w, int h);

            void lancer() {
                if (t != null) t.stop();
                anim = 0f;
                t = new Timer(15, e -> {
                    anim += 0.045f;
                    if (anim >= 1f) { anim = 1f; ((Timer) e.getSource()).stop(); }
                    zone.repaint();
                });
                t.start();
            }

            float ease() { float x = 1 - anim; return 1 - x * x * x; }
            void maj() { zone.repaint(); }

            void bulle(Graphics2D g, int w, String l1, String l2) {
                g.setFont(f(Font.BOLD, 13));
                int bw = Math.max(g.getFontMetrics().stringWidth(l1), g.getFontMetrics().stringWidth(l2)) + 24, x = Math.min(mx + 14, w - bw - 5), y = Math.max(5, my - 56);
                g.setColor(new Color(0, 0, 0, 225));
                g.fillRoundRect(x, y, bw, 46, 12, 12);
                g.setColor(LINE);
                g.drawRoundRect(x, y, bw, 46, 12, 12);
                g.setColor(Color.WHITE);
                g.drawString(l1, x + 12, y + 19);
                g.setColor(YELLOW);
                g.drawString(l2, x + 12, y + 37);
            }
        }

        class GraphHisto extends Anime {
            @Override void dessiner(Graphics2D g, int w, int h) {
                g.setFont(f(Font.BOLD, 16));
                g.setColor(TXT);
                g.drawString("Dépenses par catégorie", 10, 22);
                Map<String, Double> m = sommeParCat("Dépense");
                if (m.isEmpty()) { vide(g, w, h, "Aucune dépense enregistrée pour l'instant."); return; }
                List<Map.Entry<String, Double>> es = new ArrayList<>(m.entrySet());
                if (es.size() > 8) es = es.subList(0, 8);
                double max = Math.max(10000, es.get(0).getValue() * 1.1);
                int left = 70, bottom = h - 45, top = 45, ch = bottom - top;
                g.setFont(f(Font.PLAIN, 10));
                for (int k = 0; k <= 4; k++) {
                    int y = bottom - ch * k / 4;
                    g.setColor(GRID);
                    g.drawLine(left, y, w - 15, y);
                    g.setColor(TXT2);
                    g.drawString(FMT.format(max * k / 4), 6, y + 4);
                }
                int n = es.size(), slot = (w - left - 20) / n, bw = Math.min(90, slot * 6 / 10), hov = -1;
                for (int i = 0; i < n; i++) {
                    double v = es.get(i).getValue();
                    int bh = (int) (v / max * ch * ease()), x = left + i * slot + (slot - bw) / 2, y = bottom - bh;
                    boolean on = mx >= x && mx <= x + bw && my >= top && my <= bottom;
                    if (on) hov = i;
                    Color c = PAL[i % PAL.length];
                    g.setPaint(new GradientPaint(x, y, on ? c.brighter() : c, x, bottom, (CLAIR ? c.darker() : c.darker().darker())));
                    g.fillRoundRect(x, y, bw, Math.max(bh, 1), 10, 10);
                    g.setPaint(null);
                    g.setColor(alpha(TXT, on ? 255 : 130));
                    g.setStroke(new BasicStroke(on ? 3f : 1f));
                    g.drawRoundRect(x, y, bw, Math.max(bh, 1), 10, 10);
                    g.setStroke(new BasicStroke(1f));
                    g.setFont(f(Font.BOLD, 12));
                    g.setColor(TXT);
                    centre(g, FMT.format(v), x + bw / 2, y - 8);
                    g.setColor(TXT2);
                    centre(g, cut(es.get(i).getKey(), Math.max(6, slot / 8)), x + bw / 2, bottom + 20);
                }
                if (hov >= 0 && depenses > 0)
                    bulle(g, w, es.get(hov).getKey(), FMT.format(es.get(hov).getValue()) + " " + DEV + " (" + (int) Math.round(es.get(hov).getValue() / depenses * 100) + "%)");
            }
        }

        class GraphDonut extends Anime {
            final JComboBox<String> cmb = new JComboBox<>(TYPES);

            GraphDonut() {
                cmb.setSelectedItem("Dépense");
                styler(cmb);
                cmb.setPreferredSize(new Dimension(150, 32));
                cmb.addActionListener(e -> lancer());
                JPanel barre = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                barre.setOpaque(false);
                barre.add(label("Répartition par catégorie — type :", f(Font.BOLD, 14), TXT));
                barre.add(cmb);
                add(barre, BorderLayout.NORTH);
            }

            @Override void dessiner(Graphics2D g, int w, int h) {
                String type = (String) cmb.getSelectedItem();
                Map<String, Double> m = sommeParCat(type);
                double total = 0;
                for (double v : m.values()) total += v;
                if (total <= 0) { vide(g, w, h, "Aucune opération de type « " + type + " »."); return; }
                List<Map.Entry<String, Double>> es = new ArrayList<>(m.entrySet());
                int d = Math.max(100, Math.min(h - 40, w / 2 - 40)), x0 = Math.max(10, (w / 2 - d) / 2 + 20), y0 = (h - d) / 2;
                double[] deb = new double[es.size()], ext = new double[es.size()];
                double ang = 90;
                for (int i = 0; i < es.size(); i++) {
                    deb[i] = ang;
                    ext[i] = -360.0 * es.get(i).getValue() / total * ease();
                    g.setColor(PAL[i % PAL.length]);
                    g.fill(new Arc2D.Double(x0, y0, d, d, deb[i], ext[i], Arc2D.PIE));
                    ang += ext[i];
                }
                g.setColor(PANEL);
                g.fillOval(x0 + d / 4, y0 + d / 4, d / 2, d / 2);
                g.setColor(TXT2);
                g.setFont(f(Font.PLAIN, 12));
                centre(g, "TOTAL", x0 + d / 2, y0 + d / 2 - 6);
                g.setColor(TXT);
                g.setFont(f(Font.BOLD, 15));
                centre(g, FMT.format(total * ease()), x0 + d / 2, y0 + d / 2 + 14);
                int lx = w / 2 + 20, ly = Math.max(20, h / 2 - Math.min(es.size(), 10) * 14);
                for (int i = 0; i < es.size() && i < 10; i++) {
                    g.setColor(PAL[i % PAL.length]);
                    g.fillRoundRect(lx, ly + i * 28, 16, 16, 5, 5);
                    g.setColor(TXT);
                    g.setFont(f(Font.BOLD, 13));
                    g.drawString(cut(es.get(i).getKey(), 24), lx + 26, ly + i * 28 + 13);
                    g.setColor(TXT2);
                    g.setFont(f(Font.PLAIN, 12));
                    g.drawString(FMT.format(es.get(i).getValue()) + " " + DEV + "  (" + String.format("%.0f%%", es.get(i).getValue() / total * 100) + ")", lx + 26 + 190, ly + i * 28 + 13);
                }
                if (mx >= 0 && anim >= 1f) {
                    double dx = mx - (x0 + d / 2.0), dy = (y0 + d / 2.0) - my, r = Math.hypot(dx, dy);
                    if (r <= d / 2.0 && r >= d / 4.0) {
                        double pos = ((90 - Math.toDegrees(Math.atan2(dy, dx))) % 360 + 360) % 360, cum = 0;
                        for (int i = 0; i < es.size(); i++) {
                            cum += 360.0 * es.get(i).getValue() / total;
                            if (pos <= cum) {
                                g.setColor(TXT);
                                g.setStroke(new BasicStroke(3f));
                                g.draw(new Arc2D.Double(x0, y0, d, d, deb[i], ext[i], Arc2D.PIE));
                                g.setStroke(new BasicStroke(1f));
                                bulle(g, w, es.get(i).getKey(), FMT.format(es.get(i).getValue()) + " " + DEV + " (" + String.format("%.0f%%", es.get(i).getValue() / total * 100) + ")");
                                break;
                            }
                        }
                    }
                }
            }
        }

        class GraphMois extends Anime {
            @Override void dessiner(Graphics2D g, int w, int h) {
                g.setFont(f(Font.BOLD, 16));
                g.setColor(TXT);
                g.drawString("Comparaison mensuelle (12 derniers mois)", 10, 22);
                TreeMap<String, double[]> m = new TreeMap<>();
                for (int i = 0; i < modele.getRowCount(); i++)
                    m.computeIfAbsent(s(i, 0).substring(0, 7), k -> new double[3])[Arrays.asList(TYPES).indexOf(s(i, 3))] += mt(i);
                if (m.isEmpty()) { vide(g, w, h, "Aucune donnée pour l'instant."); return; }
                List<String> ks = new ArrayList<>(m.keySet());
                if (ks.size() > 12) ks = ks.subList(ks.size() - 12, ks.size());
                double max = 10000;
                for (String k : ks) for (double v : m.get(k)) max = Math.max(max, v);
                max *= 1.1;
                int left = 70, bottom = h - 70, top = 55, ch = bottom - top, n = ks.size(), slot = (w - left - 20) / n, gw = slot * 8 / 10, bw = Math.max(6, gw / 3);
                g.setFont(f(Font.PLAIN, 10));
                for (int k = 0; k <= 4; k++) {
                    int y = bottom - ch * k / 4;
                    g.setColor(GRID);
                    g.drawLine(left, y, w - 15, y);
                    g.setColor(TXT2);
                    g.drawString(FMT.format(max * k / 4), 6, y + 4);
                }
                Color[] cc = {GREEN, RED, BLUE};
                String[] nm = {"Revenus", "Dépenses", "Épargne"};
                for (int i = 0; i < n; i++) {
                    double[] v = m.get(ks.get(i));
                    int x0 = left + i * slot + (slot - gw) / 2;
                    for (int j = 0; j < 3; j++) {
                        int bh = (int) (v[j] / max * ch * ease()), x = x0 + j * bw, y = bottom - bh;
                        boolean on = mx >= x && mx < x + bw && my >= top && my <= bottom;
                        g.setPaint(new GradientPaint(x, y, on ? cc[j].brighter() : cc[j], x, bottom, (CLAIR ? cc[j].darker() : cc[j].darker().darker())));
                        g.fillRoundRect(x, y, bw - 2, Math.max(bh, 1), 6, 6);
                        g.setPaint(null);
                        if (on) bulle(g, w, ks.get(i) + " — " + nm[j], FMT.format(v[j]) + " " + DEV);
                    }
                    g.setFont(f(Font.PLAIN, 11));
                    g.setColor(TXT2);
                    String k = ks.get(i);
                    centre(g, k.substring(5) + "/" + k.substring(2, 4), x0 + gw / 2, bottom + 17);
                    double net = v[0] - v[1] - v[2];
                    g.setFont(f(Font.BOLD, 10));
                    g.setColor(net >= 0 ? GREEN : RED);
                    centre(g, (net >= 0 ? "+" : "") + FMT.format(net), x0 + gw / 2, bottom + 33);
                }
                int lx = w - 330;
                for (int j = 0; j < 3; j++) {
                    g.setColor(cc[j]);
                    g.fillRoundRect(lx + j * 105, 12, 12, 12, 4, 4);
                    g.setColor(TXT2);
                    g.setFont(f(Font.PLAIN, 12));
                    g.drawString(nm[j], lx + j * 105 + 17, 23);
                }
                g.setColor(TXT2);
                g.setFont(f(Font.ITALIC, 11));
                g.drawString("Sous chaque mois : net = revenus − dépenses − épargne", left, h - 12);
            }
        }

        class GraphCourbe extends Anime {
            final JComboBox<String> cCatF = new JComboBox<>(), cTyp = new JComboBox<>(TYPES), cPer = new JComboBox<>();
            final JCheckBox chk = new JCheckBox("Cumulé");
            boolean enMaj = false;

            GraphCourbe() {
                cTyp.setSelectedItem("Dépense");
                styler(cCatF); styler(cTyp); styler(cPer);
                cCatF.setPreferredSize(new Dimension(190, 30));
                cTyp.setPreferredSize(new Dimension(110, 30));
                cPer.setPreferredSize(new Dimension(160, 30));
                chk.setOpaque(false);
                chk.setForeground(TXT);
                chk.setFont(f(Font.BOLD, 12));
                chk.setFocusPainted(false);
                JPanel barre = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
                barre.setBackground(HEAD);
                barre.setBorder(new LineBorder(LINE, 1));
                barre.add(label("Filtrer :", f(Font.BOLD, 13), BLUE));
                barre.add(cTyp);
                barre.add(cCatF);
                barre.add(cPer);
                barre.add(chk);
                add(barre, BorderLayout.NORTH);
                ActionListener al = e -> { if (!enMaj) lancer(); };
                cTyp.addActionListener(al); cCatF.addActionListener(al); cPer.addActionListener(al); chk.addActionListener(al);
            }

            void rafraichirFiltres() {
                enMaj = true;
                String c = (String) cCatF.getSelectedItem(), pe = (String) cPer.getSelectedItem();
                Set<String> cats = new TreeSet<>(), ms = new TreeSet<>();
                for (int i = 0; i < modele.getRowCount(); i++) {
                    cats.add(s(i, 2));
                    if (s(i, 0).length() >= 7) ms.add(s(i, 0).substring(0, 7));
                }
                cCatF.removeAllItems();
                cCatF.addItem("Toutes catégories");
                for (String x : cats) cCatF.addItem(x);
                if (c != null) cCatF.setSelectedItem(c);
                cPer.removeAllItems();
                cPer.addItem("Toutes les périodes");
                for (String x : ms) cPer.addItem(x);
                if (pe != null) cPer.setSelectedItem(pe);
                enMaj = false;
            }

            @Override void dessiner(Graphics2D g, int w, int h) {
                String type = (String) cTyp.getSelectedItem(), cat = (String) cCatF.getSelectedItem(), per = (String) cPer.getSelectedItem();
                boolean cumul = chk.isSelected();
                Color col = "Revenu".equals(type) ? GREEN : "Épargne".equals(type) ? BLUE : RED;
                g.setFont(f(Font.BOLD, 16));
                g.setColor(TXT);
                g.drawString("Évolution chronologique — " + type + (cumul ? " (cumulé)" : ""), 10, 22);
                List<Object[]> pts = new ArrayList<>();
                for (int i = 0; i < modele.getRowCount(); i++) {
                    if (!type.equals(s(i, 3))) continue;
                    if (cat != null && !cat.startsWith("Toutes") && !cat.equals(s(i, 2))) continue;
                    if (per != null && !per.startsWith("Toutes") && !s(i, 0).startsWith(per)) continue;
                    pts.add(new Object[]{s(i, 0), mt(i)});
                }
                if (pts.isEmpty()) { vide(g, w, h, "Aucune opération ne correspond aux filtres sélectionnés."); return; }
                pts.sort((a, b) -> ((String) a[0]).compareTo((String) b[0]));
                int n = pts.size();
                double[] v = new double[n];
                double run = 0, tot = 0, max = 10000;
                for (int i = 0; i < n; i++) {
                    double x = (Double) pts.get(i)[1];
                    tot += x;
                    run = cumul ? run + x : x;
                    v[i] = run;
                    max = Math.max(max, run);
                }
                max *= 1.1;
                int left = 85, right = 30, top = 45, bottom = h - 85, gw = w - left - right, gh = bottom - top;
                g.setFont(f(Font.PLAIN, 10));
                for (int k = 0; k <= 4; k++) {
                    int y = top + gh * k / 4;
                    g.setColor(GRID);
                    g.drawLine(left, y, w - right, y);
                    g.setColor(TXT2);
                    g.drawString(FMT.format(max * (1 - k / 4.0)) + " F", 6, y + 4);
                }
                g.setColor(AXE);
                g.drawLine(left, bottom, w - right, bottom);
                g.drawLine(left, top, left, bottom);
                int[] xs = new int[n], ys = new int[n];
                for (int i = 0; i < n; i++) {
                    xs[i] = left + (n == 1 ? gw / 2 : i * gw / (n - 1));
                    ys[i] = bottom - (int) (v[i] / max * gh);
                }
                Shape old = g.getClip();
                g.setClip(0, 0, (int) (w * ease()) + 1, h);
                if (n > 1) {
                    Polygon poly = new Polygon();
                    poly.addPoint(xs[0], bottom);
                    for (int i = 0; i < n; i++) poly.addPoint(xs[i], ys[i]);
                    poly.addPoint(xs[n - 1], bottom);
                    g.setColor(alpha(col, 45));
                    g.fillPolygon(poly);
                }
                g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(col);
                for (int i = 0; i < n - 1; i++) g.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
                int step = Math.max(1, (int) Math.ceil(60.0 / Math.max(1.0, gw / (double) Math.max(1, n - 1)))), hov = -1;
                for (int i = 0; i < n; i++) {
                    if (mx >= 0 && Math.abs(mx - xs[i]) < 10 && Math.abs(my - ys[i]) < 14) hov = i;
                    g.setStroke(new BasicStroke(2f));
                    g.setColor(Color.WHITE);
                    g.fillOval(xs[i] - 5, ys[i] - 5, 10, 10);
                    g.setColor(col);
                    g.drawOval(xs[i] - 7, ys[i] - 7, 14, 14);
                    if (i % step == 0 || i == n - 1) {
                        g.setFont(f(Font.BOLD, 11));
                        g.setColor(TXT);
                        centre(g, FMT.format(v[i]) + " F", xs[i], ys[i] - 14);
                        g.setFont(f(Font.PLAIN, 10));
                        g.setColor(TXT2);
                        centre(g, (String) pts.get(i)[0], xs[i], bottom + 18);
                    }
                }
                g.setClip(old);
                g.setColor(TILE);
                g.fillRoundRect(left, h - 40, gw, 30, 8, 8);
                g.setColor(LINE);
                g.drawRoundRect(left, h - 40, gw, 30, 8, 8);
                g.setFont(f(Font.BOLD, 12));
                g.setColor(GREEN);
                g.drawString("Stats :", left + 12, h - 21);
                g.setColor(TXT);
                g.drawString("Total : " + FMT.format(tot) + " " + DEV + "   |   Moyenne : " + FMT.format(tot / n) + " " + DEV + "   |   Opérations : " + n, left + 100, h - 21);
                if (hov >= 0) bulle(g, w, (String) pts.get(hov)[0], FMT.format(v[hov]) + " " + DEV);
            }
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) { }
        final long[] dernier = {0};
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            if (System.currentTimeMillis() - dernier[0] < 5000) return;
            dernier[0] = System.currentTimeMillis();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Une erreur inattendue s'est produite (vos données sont conservées) :\n" + e, "Erreur", JOptionPane.ERROR_MESSAGE));
        });
        SwingUtilities.invokeLater(() -> new TpJavaGraphique().setVisible(true));
    }
}
