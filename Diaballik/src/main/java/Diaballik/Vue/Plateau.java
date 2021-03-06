package Diaballik.Vue;

import java.awt.event.*;
import java.io.IOException;
import java.awt.*;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import Diaballik.Models.ConfigJeu;
import Diaballik.Models.Jeu;
import Diaballik.Patterns.Observateur;

public class Plateau implements Runnable, Observateur {
    JFrame frame;
    JButton boutonMenu = new JButton("Menu");
    JButton boutonFinTour = new JButton("Fin du tour");
    ObjectMapper objectMapper = new ObjectMapper();
    JButton boutonRecommencer = new JButton("Recommencer");
    JButton boutonRejouer = new JButton("Rejouer");
    JButton sound = new JButton();
    JButton drapeau = new JButton();
    Image icon = Toolkit.getDefaultToolkit().getImage("src/main/java/Diaballik/Vue/img/pionA_ballon.png");
    JLabel boutonInfo;
    PlateauGraphique plat;
    Jeu j;
    ConfigJeu conf;
    CollecteurEvenements control;
    boolean maximized;
    JMenuBar mb = new JMenuBar();
    JMenu m1 = new JMenu();
    JMenu m2 = new JMenu();
    JMenuItem menuTerrain = new JMenuItem("Terrain");
    JMenuItem menuPerso = new JMenuItem("Personnages");
    JCheckBoxMenuItem niveauIAfacile = new JCheckBoxMenuItem("IA facile");
    JCheckBoxMenuItem niveauIAmoyen = new JCheckBoxMenuItem("IA moyen");
    JCheckBoxMenuItem niveauIAdifficile = new JCheckBoxMenuItem("IA difficile");

    static ihm interHM;
    public static Timer timer;
    JLabel iconePion;
    ImageIcon pionA_bas = null;
    ImageIcon pionB_bas = null;
    Image son, mute, drapeauFr, drapeauGB;
    JLabel mouvements = new JLabel();
    JLabel joueur = new JLabel();
    JLabel passe = new JLabel();
    playSound ps = new playSound();

    JCheckBox buttonViewArrow = new JCheckBox("Indicateur coups joués");
    ActionListener al;

    private static JLabel clock;
    private static long x;

    static void setIHM(ihm i) {
        interHM = i;
    }

    Plateau(Jeu jeu, CollecteurEvenements c, ConfigJeu cj) {
        j = jeu;
        control = c;
        conf = cj;
    }

    public static void demarrer(Jeu j, CollecteurEvenements c, ConfigJeu cj) {
        Plateau vue = new Plateau(j, c, cj);
        c.ajouteInterfaceUtilisateur(vue);
        SwingUtilities.invokeLater(vue);
    }

    public static void setEnabled() {

    }

    public void run() {
        final BoutonSuggestion suggestion = new BoutonSuggestion(j);
        // Creation d'une fenetre
        frame = new JFrame("Diaballik");
        frame.setIconImage(icon);
        m1.add(menuTerrain);
        m1.add(menuPerso);
        m2.add(niveauIAfacile);
        m2.add(niveauIAmoyen);
        m2.add(niveauIAdifficile);

        niveauIAfacile.setMnemonic(KeyEvent.VK_C);
        niveauIAmoyen.setMnemonic(KeyEvent.VK_C);
        niveauIAdifficile.setMnemonic(KeyEvent.VK_C);

        mb.add(m1);
        mb.add(m2);
        mb.setBounds(0, 0, 800, 20);
        m2.setMnemonic(KeyEvent.VK_F);
        frame.add(mb);
        frame.add(mb, BorderLayout.NORTH);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (staticConfig.blang == true) {
                    if (msgBox.msgYesNo("Voulez-vous quitter?", "Quitter") == 0) {
                        if (msgBox.msgYesNo("Voulez-vous sauvegarder votre partie", "Sauvegarder") == 0) {
                            control.toucheClavier("Save");
                        }
                        System.exit(0);
                    } else {
                        return;
                    }
                } else {
                    if (msgBox.msgYesNo("Do you want to quit?", "Quit") == 0) {
                        if (msgBox.msgYesNo("Do you want to save ?", "Save") == 0) {
                            control.toucheClavier("Save");
                        }
                        System.exit(0);
                    } else {
                        return;
                    }
                }

            }
        });
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // Ajout de notre composant de dessin dans la fenetre
        plat = new VuePlateau(j);

        // Texte et contrôles à droite de la fenêtre
        Box boiteTexte = Box.createVerticalBox();
        boiteTexte.setOpaque(true);
        // boiteTexte.setBackground(Color.decode( "#6699ff"));

        Image img = null;
        try {

            img = ImageIO.read(this.getClass().getResourceAsStream(("/info2.png"))).getScaledInstance(30, 30,
                    Image.SCALE_DEFAULT);
            son = ImageIO.read(this.getClass().getResourceAsStream("/sound.png")).getScaledInstance(20, 20,
                    Image.SCALE_DEFAULT);
            mute = ImageIO.read(this.getClass().getResourceAsStream("/mute.png")).getScaledInstance(20, 20,
                    Image.SCALE_DEFAULT);

            if (staticConfig.bmute == true) {
                sound.setIcon(new ImageIcon(mute));
            } else {
                sound.setIcon(new ImageIcon(son));
            }

            if (staticConfig.blang == false) {
                drapeauGB = ImageIO.read(this.getClass().getResourceAsStream("/drapeauuk.jpg")).getScaledInstance(20,
                        20, Image.SCALE_DEFAULT);
                drapeau.setIcon(new ImageIcon(drapeauGB));
                words wEn = objectMapper.readValue(this.getClass().getResourceAsStream("/languesEn.json"), words.class);
                boutonFinTour.setText(wEn.finTour);
                buttonViewArrow.setText(wEn.indicateur);
                mouvements.setText(wEn.deplacements + " : " + j.joueurCourant.nbMove);
                passe.setText(wEn.passe + " : " + j.joueurCourant.passeDispo);
                joueur.setText("Player : " + j.joueurCourant.name);
                m1.setText(wEn.themes);
                m2.setText(wEn.niveauIA);
                suggestion.setText(wEn.coupsConseilles);
                niveauIAfacile.setText(wEn.niveauIAfacile);
                niveauIAmoyen.setText(wEn.niveauIAmoyen);
                niveauIAdifficile.setText(wEn.niveauIAdifficile);
                menuTerrain.setText(wEn.terrain);
                menuPerso.setText(wEn.personnage);

            } else {
                drapeauFr = ImageIO.read(this.getClass().getResourceAsStream(("/drapeaufr.png"))).getScaledInstance(20,
                        20, Image.SCALE_DEFAULT);
                drapeau.setIcon(new ImageIcon(drapeauFr));
                words wFr = objectMapper.readValue(this.getClass().getResourceAsStream("/languesFr.json"), words.class);
                boutonFinTour.setText(wFr.finTour);
                buttonViewArrow.setText(wFr.indicateur);
                mouvements.setText(wFr.deplacements + " : " + j.joueurCourant.nbMove);
                passe.setText(wFr.passe + " : " + j.joueurCourant.passeDispo);
                joueur.setText("Joue : " + j.joueurCourant.name);
                m1.setText(wFr.themes);
                m2.setText(wFr.niveauIA);
                suggestion.setText(wFr.coupsConseilles);
                niveauIAfacile.setText(wFr.niveauIAfacile);
                niveauIAmoyen.setText(wFr.niveauIAmoyen);
                niveauIAdifficile.setText(wFr.niveauIAdifficile);
                menuTerrain.setText(wFr.terrain);
                menuPerso.setText(wFr.personnage);

            }

        } catch (Exception e) {
            System.out.println(e);
        }

        sound.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (staticConfig.bmute == false) {
                        sound.setIcon(new ImageIcon(mute));
                        staticConfig.bmute = true;
                    } else {
                        sound.setIcon(new ImageIcon(son));
                        staticConfig.bmute = false;
                    }

                } catch (Exception e1) {
                    System.out.println(e1);
                }
            }
        });

        menuTerrain.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TerrainCustomize(conf);
                frame.repaint();
                frame.revalidate();
            }
        });

        menuPerso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NewGame.cp1 = new choisirPerso(1);
                NewGame.cp2 = new choisirPerso(2);
            }
        });

        if (j.IA == false) {
            niveauIAfacile.setEnabled(false);
            niveauIAmoyen.setEnabled(false);
            niveauIAdifficile.setEnabled(false);
        } else if (j.IA == true) {
            if (conf.getIALevel() == ConfigJeu.IALevel.facile) {
                niveauIAfacile.setSelected(true);
                niveauIAmoyen.setSelected(false);
                niveauIAdifficile.setSelected(false);
            } else if (conf.getIALevel() == ConfigJeu.IALevel.moyen) {
                niveauIAfacile.setSelected(false);
                niveauIAmoyen.setSelected(true);
                niveauIAdifficile.setSelected(false);
            } else if (conf.getIALevel() == ConfigJeu.IALevel.difficile) {
                niveauIAfacile.setSelected(false);
                niveauIAmoyen.setSelected(false);
                niveauIAdifficile.setSelected(true);
            }

        }

        niveauIAfacile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conf.setIALevel(ConfigJeu.IALevel.facile);
                niveauIAfacile.setSelected(true);
                niveauIAmoyen.setSelected(false);
                niveauIAdifficile.setSelected(false);
            }
        });

        niveauIAmoyen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conf.setIALevel(ConfigJeu.IALevel.moyen);
                niveauIAfacile.setSelected(false);
                niveauIAmoyen.setSelected(true);
                niveauIAdifficile.setSelected(false);
            }
        });

        niveauIAdifficile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conf.setIALevel(ConfigJeu.IALevel.difficile);
                niveauIAfacile.setSelected(false);
                niveauIAmoyen.setSelected(false);
                niveauIAdifficile.setSelected(true);

            }
        });

        drapeau.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (staticConfig.blang == true) {
                        drapeauGB = ImageIO.read(this.getClass().getResourceAsStream("/drapeauuk.jpg"))
                                .getScaledInstance(20, 20, Image.SCALE_DEFAULT);
                        drapeau.setIcon(new ImageIcon(drapeauGB));
                        words wEn = objectMapper.readValue(this.getClass().getResourceAsStream("/languesEn.json"),
                                words.class);

                        boutonFinTour.setText(wEn.finTour);
                        buttonViewArrow.setText(wEn.indicateur);
                        mouvements.setText(wEn.deplacements + " : " + j.joueurCourant.nbMove);
                        passe.setText(wEn.passe + " : " + j.joueurCourant.passeDispo);
                        joueur.setText("Player : " + j.joueurCourant.name);
                        m1.setText(wEn.themes);
                        m2.setText(wEn.niveauIA);
                        suggestion.setText(wEn.coupsConseilles);
                        niveauIAfacile.setText(wEn.niveauIAfacile);
                        niveauIAmoyen.setText(wEn.niveauIAmoyen);
                        niveauIAdifficile.setText(wEn.niveauIAdifficile);
                        menuTerrain.setText(wEn.terrain);
                        menuPerso.setText(wEn.personnage);

                        staticConfig.blang = false;

                    } else {
                        drapeauFr = ImageIO.read(this.getClass().getResourceAsStream(("/drapeaufr.png")))
                                .getScaledInstance(20, 20, Image.SCALE_DEFAULT);
                        drapeau.setIcon(new ImageIcon(drapeauFr));
                        words wFr = objectMapper.readValue(this.getClass().getResourceAsStream("/languesFr.json"),
                                words.class);

                        boutonFinTour.setText(wFr.finTour);
                        buttonViewArrow.setText(wFr.indicateur);
                        mouvements.setText(wFr.deplacements + " : " + j.joueurCourant.nbMove);
                        passe.setText(wFr.passe + " : " + j.joueurCourant.passeDispo);
                        joueur.setText("Joue : " + j.joueurCourant.name);
                        m1.setText(wFr.themes);
                        m2.setText(wFr.niveauIA);
                        suggestion.setText(wFr.coupsConseilles);
                        niveauIAfacile.setText(wFr.niveauIAfacile);
                        niveauIAmoyen.setText(wFr.niveauIAmoyen);
                        niveauIAdifficile.setText(wFr.niveauIAdifficile);
                        menuTerrain.setText(wFr.terrain);
                        menuPerso.setText(wFr.personnage);

                        staticConfig.blang = true;

                    }
                } catch (Exception e1) {
                    System.out.println(e1);
                }
            }
        });

        boutonInfo = new JLabel(new ImageIcon(img));
        String sInfo = "<html> <b>Raccourcis : </b> <br> f : fin du tour <br> z : annuler <br> y : refaire  <br> s : sauvegarder <br> r : recommencer <br> echap : plein écran <br> q : quitter  </html>  ";
        boutonInfo.setToolTipText(sInfo);
        boutonInfo.setFocusable(false);

        // Bouton Menu
        boutonMenu.setFocusable(false);
        // boiteTexte.add(boutonMenu);
        boutonMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ps.play("/buttonClick.wav", staticConfig.bmute);
                if (timer != null)
                    timer.stop();
                JFrame f = new JFrame();
                f.setSize(700, 530);
                f.setLayout(null);
                MenuEnJeu m = new MenuEnJeu(f, frame);
                m.SetControl(control);
                f.setContentPane(m);
                f.setVisible(true);
            }
        });

        // Ajout des boutons en haut
        drapeau.setFocusable(false);
        drapeau.add(Box.createRigidArea(new Dimension(10, 20)));
        drapeau.setAlignmentX(Component.RIGHT_ALIGNMENT);
        boiteTexte.add(drapeau);
        Box topButton = Box.createHorizontalBox();
        drapeau.setAlignmentX(Component.LEFT_ALIGNMENT);
        topButton.add(drapeau);
        topButton.add(Box.createRigidArea(new Dimension(10, 0)));
        topButton.add(boutonMenu);
        boutonMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        topButton.add(Box.createRigidArea(new Dimension(20, 0)));
        topButton.add(boutonInfo);
        topButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boiteTexte.add(topButton);
        boiteTexte.add(Box.createRigidArea(new Dimension(0, 10)));
        // indicateur de mouvements
        buttonViewArrow.setSelected(true);
        buttonViewArrow.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    VuePlateau.viewArrow = true;
                    j.metAJour();
                } else {
                    VuePlateau.viewArrow = false;
                    j.metAJour();
                }
                ;
            }
        });
        buttonViewArrow.setFocusable(false);
        buttonViewArrow.setAlignmentX(Component.CENTER_ALIGNMENT);
        boiteTexte.add(buttonViewArrow);
        boiteTexte.add(Box.createRigidArea(new Dimension(0, 90)));

        // Info joueur
        joueur.setAlignmentX(Component.CENTER_ALIGNMENT);
        joueur.setAlignmentY(Component.CENTER_ALIGNMENT);

        // icone pion joueur
        if (NewGame.cp1 == null) {
            try {
                pionA_bas = new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream(("/pionA_bas.png")))
                        .getScaledInstance(100, 100, Image.SCALE_DEFAULT));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            Image newimg = NewGame.cp1.panel.joueur1.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
            pionA_bas = new ImageIcon(newimg);

        }
        if (NewGame.cp2 == null) {
            try {
                pionB_bas = new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream(("/pionB_bas.png")))
                        .getScaledInstance(100, 100, Image.SCALE_DEFAULT));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            Image newimg2 = NewGame.cp2.panel.joueur2.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
            pionB_bas = new ImageIcon(newimg2);
        }

        if (j.joueurCourant == j.joueur1) {
            iconePion = new JLabel(pionA_bas);

        } else {
            iconePion = new JLabel(pionB_bas);
        }

        iconePion.setAlignmentX(Component.CENTER_ALIGNMENT);
        boiteTexte.add(iconePion);
        boiteTexte.add(joueur);
        boiteTexte.add(Box.createRigidArea(new Dimension(0, 20)));

        // Info mouvements
        mouvements.setAlignmentX(Component.CENTER_ALIGNMENT);
        boiteTexte.add(mouvements);

        // Info passe
        passe.setAlignmentX(Component.CENTER_ALIGNMENT);
        boiteTexte.add(passe);
        boiteTexte.add(Box.createRigidArea(new Dimension(0, 20)));

        // timer
        final long temps;
        if (conf.getTimer() != ConfigJeu.Timer.illimite) {
            if (conf.getTimer() == ConfigJeu.Timer.un)
                temps = 10000;
            else if (conf.getTimer() == ConfigJeu.Timer.deux)
                temps = 30000;
            else if (conf.getTimer() == ConfigJeu.Timer.trois)
                temps = 60000;
            else
                temps = 60000;

            final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("mm : ss");
            clock = new JLabel(sdf.format(new Date(temps)), JLabel.CENTER);
            // clock.setPreferredSize(new Dimension(250, 100));
            clock.setOpaque(true);
            clock.setBackground(Color.black);
            clock.setForeground(Color.white);
            clock.setFont(new Font("Serif", Font.BOLD, 20));
            clock.setAlignmentX(Component.CENTER_ALIGNMENT);
            x = temps - 1000;
            al = new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    ps.play("/buttonClick.wav", staticConfig.bmute);
                    if (x <= 0) {
                        control.toucheClavier("FinTour");
                        x = temps;
                    }
                    clock.setText(sdf.format(new Date(x)));
                    x -= 1000;

                }
            };
            if (timer == null)
                timer = new javax.swing.Timer(1000, al);
            timer.start();
            boiteTexte.add(clock);

        }

        // bouton fin de tour
        boiteTexte.add(Box.createRigidArea(new Dimension(0, 10)));
        boutonFinTour.setFocusable(false);
        boutonFinTour.setAlignmentX(Component.CENTER_ALIGNMENT);
        boiteTexte.add(boutonFinTour);

        // bouton recommencer
        boiteTexte.add(Box.createRigidArea(new Dimension(0, 20)));
        boutonRecommencer.setFocusable(false);
        boutonRecommencer.setAlignmentX(Component.CENTER_ALIGNMENT);
        boiteTexte.add(boutonRecommencer);
        boutonRecommencer.setVisible(false);
        boutonRecommencer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (conf.getTimer() != ConfigJeu.Timer.illimite) {
                    if (conf.getTimer() == ConfigJeu.Timer.un)
                        setX(10000);
                    else if (conf.getTimer() == ConfigJeu.Timer.deux)
                        setX(30000);
                    else if (conf.getTimer() == ConfigJeu.Timer.trois)
                        setX(60000);
                    else
                        setX(60000);
                }
                if (timer != null)
                    timer.restart();
                j.init();
                j.start();
            }
        });

        // suggestion
        boiteTexte.add(Box.createGlue());
        suggestion.setFocusable(false);
        suggestion.setAlignmentX(Component.CENTER_ALIGNMENT);
        boiteTexte.add(suggestion);
        boiteTexte.add(Box.createRigidArea(new Dimension(0, 20)));

        // Annuler / refaire
        BoutonAnnuler annuler = new BoutonAnnuler(j);
        annuler.setFocusable(false);
        annuler.setToolTipText("Annuler un coup");
        BoutonRefaire refaire = new BoutonRefaire(j);
        refaire.setFocusable(false);
        refaire.setToolTipText("Refaire un  coup");
        Box annulerRefaire = Box.createHorizontalBox();
        annulerRefaire.add(annuler);
        annulerRefaire.add(refaire);
        annulerRefaire.setAlignmentX(Component.CENTER_ALIGNMENT);
        annulerRefaire.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        boiteTexte.add(annulerRefaire);
        boiteTexte.add(Box.createRigidArea(new Dimension(0, 10)));

        // Retransmission des évènements au contrôleur
        plat.addMouseListener(new AdaptateurSouris(plat, control));
        frame.addKeyListener(new AdaptateurClavier(control));
        boutonFinTour.addActionListener(new AdaptateurFinTour(control));
        annuler.addActionListener(new AdaptateurAnnuler(control));
        refaire.addActionListener(new AdaptateurRefaire(control));
        suggestion.addActionListener(new AdaptateurSuggestion(control));

        // Mise en place de l'interface
        boiteTexte.setPreferredSize(new Dimension(170, 600));
        frame.add(boiteTexte, BorderLayout.EAST);
        frame.add(Box.createGlue());
        plat.setPreferredSize(new Dimension(600, 600));
        frame.add(plat, BorderLayout.CENTER);
        j.ajouteObservateur(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public void toggleFullscreen() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        if (maximized) {
            device.setFullScreenWindow(null);
            maximized = false;
        } else {
            device.setFullScreenWindow(frame);
            maximized = true;
        }
    }

    public static void applyQualityRenderingHints(Graphics2D g2d) {

        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

    }

    /**
     * @param x the x to set
     */
    public static void setX(long x) {
        Plateau.x = x;
    }

    @Override
    public void miseAJour() {
        try {
            if (j.gameOver) {
                if (j.antijeuBool) {
                    String nameAdv = (j.joueurCourant == j.joueur1) ? j.joueur2.name : j.joueur1.name;
                    mouvements.setText(nameAdv + " a fait antijeu");
                } else
                    mouvements.setVisible(false);
                passe.setVisible(false);
                boutonFinTour.setVisible(false);
                joueur.setText("Victoire de " + j.joueurCourant.name + " ! ");
                boutonRecommencer.setVisible(true);
                clock.setVisible(false);
            } else {
                boutonRecommencer.setVisible(false);
                mouvements.setVisible(true);
                passe.setVisible(true);
                boutonFinTour.setVisible(true);

                if (staticConfig.blang == false) {
                    joueur.setText("Player : " + j.joueurCourant.name);
                    mouvements.setText("Moves : " + j.joueurCourant.nbMove);
                    passe.setText("Pass : " + j.joueurCourant.passeDispo);
                } else {
                    joueur.setText("Joue : " + j.joueurCourant.name);
                    mouvements.setText("Déplacements : " + j.joueurCourant.nbMove);
                    passe.setText("Passe : " + j.joueurCourant.passeDispo);
                }

                if (conf.getTimer() != ConfigJeu.Timer.illimite)
                    clock.setVisible(true);
                // maj icon pion
                if (NewGame.cp1 == null) {
                    try {
                        pionA_bas = new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream(("/pionA_bas.png")))
                                .getScaledInstance(100, 100, Image.SCALE_DEFAULT));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Image newimg = NewGame.cp1.panel.joueur1.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
                    pionA_bas = new ImageIcon(newimg);

                }
                if (NewGame.cp2 == null) {
                    try {
                        pionB_bas = new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream(("/pionB_bas.png")))
                                .getScaledInstance(100, 100, Image.SCALE_DEFAULT));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Image newimg2 = NewGame.cp2.panel.joueur2.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
                    pionB_bas = new ImageIcon(newimg2);
                }

                if (j.joueurCourant == j.joueur1) {
                    iconePion.setIcon(pionA_bas);

                } else {
                    iconePion.setIcon(pionB_bas);
                }
            }
        } catch (Exception e) {
        }

    }

}