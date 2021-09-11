/* 
 * Copyright (C) 2020 Alexander Stojanovich <coas91@rocketmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rs.alexanderstojanovich.fo2ie.main;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.frm.Palette;
import rs.alexanderstojanovich.fo2ie.helper.ButtonEditor;
import rs.alexanderstojanovich.fo2ie.helper.ButtonRenderer;
import rs.alexanderstojanovich.fo2ie.intrface.Configuration;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.intrface.ResolutionPragma;
import rs.alexanderstojanovich.fo2ie.intrface.Section;
import rs.alexanderstojanovich.fo2ie.intrface.Section.SectionName;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;
import rs.alexanderstojanovich.fo2ie.util.GLColor;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class GUI extends javax.swing.JFrame {

    public static enum Mode {
        ALL_RES, TARGET_RES
    };

    private Mode mode = Mode.TARGET_RES;

    public static final Dimension DIM = Toolkit.getDefaultToolkit().getScreenSize();
    public static final List<Image> ICONS = initFO2IELogos();

    public static final GLProfile GL20 = GLProfile.get(GLProfile.GL2);
    public static final GLCapabilities GL_CAP = new GLCapabilities(GL20);

    public static final GLWindow GL_WINDOW = GLWindow.create(GL_CAP);
    public static final GLCanvas GL_CANVAS = new GLCanvas(GL_CAP);

    private static final Configuration cfg = Configuration.getInstance();
    private static final DefaultComboBoxModel<Section.SectionName> DCBM = new DefaultComboBoxModel<>(Section.SectionName.values());
    private final Intrface intrface = new Intrface();
    private final FPSAnimator fpsAnim = new FPSAnimator(GL_CANVAS, FPSAnimator.DEFAULT_FRAMES_PER_INTERVAL, true);
    private final ModuleRenderer renderer = new ModuleRenderer(fpsAnim, intrface) {
        @Override
        public void afterSelection() {
            featurePreview();
            componentsPreview();
        }

        @Override
        public void afterModuleBuild() {
            //btnBuild.setEnabled(true);
            btnMdlePreview.setEnabled(true);
            featurePreview();
            componentsPreview();
        }
    };
    public static final ExecutorService EXEC_BUILD = Executors.newSingleThreadExecutor();

    // cool it's our new logo :)
    private static final String LOGO_FILE_NAME = "fo2ie_logo.png";
    // and logox variant with black outline
    private static final String LOGOX_FILE_NAME = "fo2ie_logox.png";

    public static final String RESOURCES_DIR = "/rs/alexanderstojanovich/fo2ie/res/";
    public static final String LICENSE_LOGO_FILE_NAME = "gplv3_logo.png";

    // OpenGL stuff
    public static final String PRIM_VERTEX_SHADER = "primitiveVS.glsl";
    public static final String PRIM_FRAGMENT_SHADER = "primitiveFS.glsl";

    public static final String IMG_VERTEX_SHADER = "imageVS.glsl";
    public static final String IMG_FRAGMENT_SHADER = "imageFS.glsl";

    public static final String FNT_VERTEX_SHADER = "fontVS.glsl";
    public static final String FNT_FRAGMENT_SHADER = "fontFS.glsl";

    public static final String FNT_PIC = "font.png";
    public static final String QMARK_PIC = "qmark.png";

    public static final String BUILD_ICON = "build_icon.png";

    public static final String FEAT_ICON = "feat_icon.png";
    public static final String COMP_ICON = "comp_icon.png";

    public static final String SPLASH_FILE_NAME = "fo2ie_splash.png";
    public static final String SCREENSHOT_DIR = "screenshots";

    private File targetIniFile;

    private static float progress = 0.0f;

    /**
     * Creates new form GUI
     */
    public GUI() {
        progress += 25.0f;
        initComponents(); // netbeans loading components        
        progress += 25.0f;

        initLogos4App(); // logos for app
        initPaths(); // set paths from config
        initGL(); // sets GL canvas 
        progress += 25.0f;
        initIntEn(); // init enable intrface panel components {comboxes, buildTargetRes module, preview values etc}
        initPosition(); // centers the GUI
        initMenuDialogs(); // init menu dialogs
        initTabPaneIcons(); // init tab icons
        progress += 25.0f;
    }

    // init both logos
    private static List<Image> initFO2IELogos() {
        List<Image> result = new ArrayList<>();

        URL url_logo = GUI.class.getResource(RESOURCES_DIR + LOGO_FILE_NAME);
        URL url_logox = GUI.class.getResource(RESOURCES_DIR + LOGOX_FILE_NAME);
        if (url_logo != null && url_logox != null) {
            ImageIcon logo = new ImageIcon(url_logo);
            ImageIcon logox = new ImageIcon(url_logox);

            result.add(logo.getImage());
            result.add(logox.getImage());
        }

        return result;
    }

    private void initLogos4App() {
        this.setIconImages(ICONS);
    }

    private void initPaths() {
        this.txtFldInPath.setText(cfg.getInDir().getPath());
        this.txtFldOutPath.setText(cfg.getOutDir().getPath());
        this.txtFldInPath.setToolTipText(cfg.getInDir().getPath());
        this.txtFldOutPath.setToolTipText(cfg.getOutDir().getPath());

        if (!cfg.getInDir().getPath().isEmpty()) {
            btnLoad.setEnabled(true);
        }

        if (!cfg.getOutDir().getPath().isEmpty()) {
            btnSave.setEnabled(true);
        }
    }

    private void initGL() {
        // registering events
        GL_CANVAS.addGLEventListener(renderer);
        // for moving GLComponents
        GL_CANVAS.addMouseListener(renderer);
        GL_CANVAS.addMouseMotionListener(renderer);

        GL_CANVAS.setSize(panelModule.getSize());
        GL_CANVAS.setPreferredSize(panelModule.getPreferredSize());
        this.panelModule.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = e.getComponent().getWidth();
                int height = e.getComponent().getHeight();
                GL_CANVAS.setSize(width, height);
                buildModuleComponents();
//                FO2IELogger.reportInfo(GL_CANVAS.getSize().toString(), null);
            }
        });
        GL_WINDOW.setTitle("Module preview");
        GL_WINDOW.addGLEventListener(renderer);
        GL_WINDOW.addKeyListener(renderer);
        GL_WINDOW.setSharedAutoDrawable(GL_CANVAS);
        GL_WINDOW.setSize(800, 600);
        fpsAnim.add(GL_WINDOW);
        this.panelModule.add(GL_CANVAS);
    }

    private void initIntEn() {

        boolean ok = cfg.isIgnoreErrors() || (intrface.isInitialized() && intrface.getErrorNum() == 0);

        for (int i = 0; i < this.pnlIntrface.getComponentCount(); i++) {
            this.pnlIntrface.getComponent(i).setEnabled(ok);
        }

        btnAddFeat.setEnabled(ok);
        tabPaneBrowser.setEnabled(ok);
    }

    // Center the GUI window into center of the screen
    private void initPosition() {
        this.setLocation(DIM.width / 2 - this.getSize().width / 2, DIM.height / 2 - this.getSize().height / 2);
    }

    private void initMenuDialogs() {
        final FileNameExtensionFilter iniFilter = new FileNameExtensionFilter("FOnline Interface ini (*.ini)", "ini");
        this.fileChooserIniLoad.setFileFilter(iniFilter);
        this.fileChooserIniSave.setFileFilter(iniFilter);
    }

    // init tab icons (tab pane browser)
    private void initTabPaneIcons() {
        URL featURL = GUI.class.getResource(RESOURCES_DIR + FEAT_ICON);
        URL compURL = GUI.class.getResource(RESOURCES_DIR + COMP_ICON);
        if (featURL != null) {
            ImageIcon featIcon = new ImageIcon(featURL);
            tabPaneBrowser.setIconAt(0, featIcon);
        }

        if (compURL != null) {
            ImageIcon compIcon = new ImageIcon(compURL);
            tabPaneBrowser.setIconAt(1, compIcon);
        }
    }

    // bulding the module priv method
    private void workOnBuildComponents() {
        // dont build if already build in progress
        if (renderer.state == ModuleRenderer.State.BUILD) {
            return;
        }

        btnMdlePreview.setEnabled(false);

        initBuildModule();
        buildModuleComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        fileChooserDirInput = new javax.swing.JFileChooser();
        fileChooserDirOutput = new javax.swing.JFileChooser();
        fileChooserIniLoad = new javax.swing.JFileChooser();
        fileChooserIniSave = new javax.swing.JFileChooser();
        pnlFilePaths = new javax.swing.JPanel();
        lblInput = new javax.swing.JLabel();
        txtFldInPath = new javax.swing.JTextField();
        btnChooseInPath = new javax.swing.JButton();
        lblOutput = new javax.swing.JLabel();
        txtFldOutPath = new javax.swing.JTextField();
        btnChoosePathOut = new javax.swing.JButton();
        btnLoad = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCheck = new javax.swing.JButton();
        pnlIntrface = new javax.swing.JPanel();
        lblSection = new javax.swing.JLabel();
        cmbBoxSection = new javax.swing.JComboBox<>();
        lblResolution = new javax.swing.JLabel();
        cmbBoxResolution = new javax.swing.JComboBox<>();
        btnTogAllRes = new javax.swing.JToggleButton();
        btnMdlePreview = new javax.swing.JButton();
        pnlTable = new javax.swing.JPanel();
        tabPaneBrowser = new javax.swing.JTabbedPane();
        sbFeatures = new javax.swing.JScrollPane();
        tblFeats = new javax.swing.JTable();
        sbComps = new javax.swing.JScrollPane();
        tblComps = new javax.swing.JTable();
        btnAddFeat = new javax.swing.JButton();
        panelModule = new javax.swing.JPanel();
        mainMenu = new javax.swing.JMenuBar();
        mainMenuFile = new javax.swing.JMenu();
        fileMenuLoad = new javax.swing.JMenuItem();
        fileMenuSep0 = new javax.swing.JPopupMenu.Separator();
        fileMenuSave = new javax.swing.JMenuItem();
        fileMenuSaveAs = new javax.swing.JMenuItem();
        fileMenuSep1 = new javax.swing.JPopupMenu.Separator();
        fileMenuExit = new javax.swing.JMenuItem();
        mainMenuTools = new javax.swing.JMenu();
        toolsScreenshot = new javax.swing.JMenuItem();
        mainMenuInfo = new javax.swing.JMenu();
        infoMenuAbout = new javax.swing.JMenuItem();
        infoMenuHelp = new javax.swing.JMenuItem();

        fileChooserDirInput.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        fileChooserDirOutput.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        fileChooserDirOutput.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        fileChooserIniSave.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FOnline2 S3 Interface Editor - HUNS");
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));
        setSize(new java.awt.Dimension(640, 360));
        getContentPane().setLayout(new java.awt.GridLayout(2, 2));

        pnlFilePaths.setBorder(javax.swing.BorderFactory.createTitledBorder("Directory Paths"));
        pnlFilePaths.setLayout(new java.awt.GridLayout(3, 3, 2, 2));

        lblInput.setText("Input data directory:");
        pnlFilePaths.add(lblInput);

        txtFldInPath.setEditable(false);
        txtFldInPath.setColumns(15);
        pnlFilePaths.add(txtFldInPath);

        btnChooseInPath.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rs/alexanderstojanovich/fo2ie/res/dir_icon.png"))); // NOI18N
        btnChooseInPath.setText("Input dir...");
        btnChooseInPath.setToolTipText("Choose input directory");
        btnChooseInPath.setIconTextGap(5);
        btnChooseInPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseInPathActionPerformed(evt);
            }
        });
        pnlFilePaths.add(btnChooseInPath);

        lblOutput.setText("Output data directory:");
        pnlFilePaths.add(lblOutput);

        txtFldOutPath.setEditable(false);
        txtFldOutPath.setColumns(15);
        pnlFilePaths.add(txtFldOutPath);

        btnChoosePathOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rs/alexanderstojanovich/fo2ie/res/dir_icon.png"))); // NOI18N
        btnChoosePathOut.setText("Output dir...");
        btnChoosePathOut.setToolTipText("Choose output directory");
        btnChoosePathOut.setIconTextGap(5);
        btnChoosePathOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChoosePathOutActionPerformed(evt);
            }
        });
        pnlFilePaths.add(btnChoosePathOut);

        btnLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rs/alexanderstojanovich/fo2ie/res/load_icon.png"))); // NOI18N
        btnLoad.setText("Load");
        btnLoad.setToolTipText("Load interface from input directory");
        btnLoad.setEnabled(false);
        btnLoad.setIconTextGap(16);
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });
        pnlFilePaths.add(btnLoad);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rs/alexanderstojanovich/fo2ie/res/diskette_icon.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setToolTipText("Save interface to the output directory");
        btnSave.setEnabled(false);
        btnSave.setIconTextGap(16);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        pnlFilePaths.add(btnSave);

        btnCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rs/alexanderstojanovich/fo2ie/res/hashtag.png"))); // NOI18N
        btnCheck.setText("Check");
        btnCheck.setToolTipText("Check interface status");
        btnCheck.setIconTextGap(5);
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckActionPerformed(evt);
            }
        });
        pnlFilePaths.add(btnCheck);

        getContentPane().add(pnlFilePaths);

        pnlIntrface.setBorder(javax.swing.BorderFactory.createTitledBorder("Interface"));
        pnlIntrface.setLayout(new java.awt.GridLayout(3, 3, 2, 2));

        lblSection.setText("Section:");
        pnlIntrface.add(lblSection);

        cmbBoxSection.setModel(DCBM);
        cmbBoxSection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBoxSectionActionPerformed(evt);
            }
        });
        pnlIntrface.add(cmbBoxSection);

        lblResolution.setText("Resolution:");
        pnlIntrface.add(lblResolution);

        cmbBoxResolution.setMaximumRowCount(6);
        cmbBoxResolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBoxResolutionActionPerformed(evt);
            }
        });
        pnlIntrface.add(cmbBoxResolution);

        btnTogAllRes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rs/alexanderstojanovich/fo2ie/res/monitor_icon.png"))); // NOI18N
        btnTogAllRes.setText("All Resolutions");
        btnTogAllRes.setToolTipText("Toggle All Resolutions");
        btnTogAllRes.setIconTextGap(5);
        btnTogAllRes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTogAllResActionPerformed(evt);
            }
        });
        pnlIntrface.add(btnTogAllRes);

        btnMdlePreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rs/alexanderstojanovich/fo2ie/res/window_preview_icon.png"))); // NOI18N
        btnMdlePreview.setText("Preview Module");
        btnMdlePreview.setToolTipText("Preview module in window");
        btnMdlePreview.setIconTextGap(5);
        btnMdlePreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMdlePreviewActionPerformed(evt);
            }
        });
        pnlIntrface.add(btnMdlePreview);

        getContentPane().add(pnlIntrface);

        pnlTable.setBorder(javax.swing.BorderFactory.createTitledBorder("Features & Components"));
        pnlTable.setLayout(new java.awt.GridBagLayout());

        tabPaneBrowser.setEnabled(false);

        sbFeatures.setPreferredSize(new java.awt.Dimension(240, 135));

        tblFeats.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblFeats.setRowHeight(24);
        sbFeatures.setViewportView(tblFeats);

        tabPaneBrowser.addTab("Features", sbFeatures);

        sbComps.setPreferredSize(new java.awt.Dimension(240, 135));

        tblComps.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblComps.setRowHeight(24);
        sbComps.setViewportView(tblComps);

        tabPaneBrowser.addTab("Components", sbComps);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlTable.add(tabPaneBrowser, gridBagConstraints);

        btnAddFeat.setText("Add Feature");
        btnAddFeat.setEnabled(false);
        btnAddFeat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFeatXDeselectActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlTable.add(btnAddFeat, gridBagConstraints);

        getContentPane().add(pnlTable);

        panelModule.setBorder(javax.swing.BorderFactory.createTitledBorder("Module"));

        javax.swing.GroupLayout panelModuleLayout = new javax.swing.GroupLayout(panelModule);
        panelModule.setLayout(panelModuleLayout);
        panelModuleLayout.setHorizontalGroup(
            panelModuleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );
        panelModuleLayout.setVerticalGroup(
            panelModuleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 265, Short.MAX_VALUE)
        );

        getContentPane().add(panelModule);

        mainMenuFile.setText("File");

        fileMenuLoad.setText("Load...");
        fileMenuLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuLoadActionPerformed(evt);
            }
        });
        mainMenuFile.add(fileMenuLoad);
        mainMenuFile.add(fileMenuSep0);

        fileMenuSave.setText("Save");
        fileMenuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuSaveActionPerformed(evt);
            }
        });
        mainMenuFile.add(fileMenuSave);

        fileMenuSaveAs.setText("Save as...");
        fileMenuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuSaveAsActionPerformed(evt);
            }
        });
        mainMenuFile.add(fileMenuSaveAs);
        mainMenuFile.add(fileMenuSep1);

        fileMenuExit.setText("Exit");
        fileMenuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuExitActionPerformed(evt);
            }
        });
        mainMenuFile.add(fileMenuExit);

        mainMenu.add(mainMenuFile);

        mainMenuTools.setText("Tools");

        toolsScreenshot.setText("Take Screenshot");
        toolsScreenshot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toolsScreenshotActionPerformed(evt);
            }
        });
        mainMenuTools.add(toolsScreenshot);

        mainMenu.add(mainMenuTools);

        mainMenuInfo.setText("Info");

        infoMenuAbout.setText("About");
        infoMenuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoMenuAboutActionPerformed(evt);
            }
        });
        mainMenuInfo.add(infoMenuAbout);

        infoMenuHelp.setText("How to use");
        infoMenuHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoMenuHelpActionPerformed(evt);
            }
        });
        mainMenuInfo.add(infoMenuHelp);

        mainMenu.add(mainMenuInfo);

        setJMenuBar(mainMenu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChooseInPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseInPathActionPerformed
        // TODO add your handling code here:
        fileInOpen();
    }//GEN-LAST:event_btnChooseInPathActionPerformed

    private void btnChoosePathOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoosePathOutActionPerformed
        // TODO add your handling code here:
        fileOutOpen();
    }//GEN-LAST:event_btnChoosePathOutActionPerformed

    private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
        renderer.getModule().components.clear();
        loadFromButton();
        featurePreview();
        componentsPreview();
        workOnBuildComponents();
    }//GEN-LAST:event_btnLoadActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveFromButton();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void fileMenuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuExitActionPerformed
        // TODO add your handling code here:
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_fileMenuExitActionPerformed

    private void infoMenuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoMenuAboutActionPerformed
        // TODO add your handling code here:
        infoAbout();
    }//GEN-LAST:event_infoMenuAboutActionPerformed

    private void infoMenuHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoMenuHelpActionPerformed
        // TODO add your handling code here:
        infoHelp();
    }//GEN-LAST:event_infoMenuHelpActionPerformed

    private void loadFromButton() {
        boolean ok = intrface.readIniFile();
        if (ok) {
            final List<String> resStrs = new ArrayList<>();
            List<ResolutionPragma> customResolutions = intrface.getCustomResolutions();

            for (ResolutionPragma resPrag : customResolutions) {
                String resStr = String.valueOf(resPrag.getWidth()) + "x" + String.valueOf(resPrag.getHeight());
                resStrs.add(resStr);
            }

            final DefaultComboBoxModel<Object> resModel = new DefaultComboBoxModel<>(resStrs.toArray());
            this.cmbBoxResolution.setModel(resModel);

            if (intrface.getErrorNum() == 0) {
                JOptionPane.showMessageDialog(this, "App successfully loaded desired interface!", "Interface Load", JOptionPane.INFORMATION_MESSAGE);
            } else if (cfg.isIgnoreErrors()) {
                JOptionPane.showMessageDialog(this, "App detected syntax errors (ignored by user)!", "Syntax Errors", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "App detected syntax errors!", "Syntax errors", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "App cannot find desired interface,\ncheck paths again!", "Interface Load Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel defFtTblModel = (DefaultTableModel) tblFeats.getModel();
        for (int i = tblFeats.getRowCount() - 1; i >= 0; i--) {
            defFtTblModel.removeRow(i);
        }

        initIntEn();
    }

    private void loadFromMenu() {
        boolean ok = intrface.readIniFile(targetIniFile);
        if (ok) {
            final List<String> resStrs = new ArrayList<>();
            List<ResolutionPragma> customResolutions = intrface.getCustomResolutions();

            for (ResolutionPragma resPrag : customResolutions) {
                String resStr = String.valueOf(resPrag.getWidth()) + "x" + String.valueOf(resPrag.getHeight());
                resStrs.add(resStr);
            }

            final DefaultComboBoxModel<Object> resModel = new DefaultComboBoxModel<>(resStrs.toArray());
            this.cmbBoxResolution.setModel(resModel);

            if (intrface.getErrorNum() == 0) {
                JOptionPane.showMessageDialog(this, "App successfully loaded desired interface!", "Interface Load", JOptionPane.INFORMATION_MESSAGE);
            } else if (cfg.isIgnoreErrors()) {
                JOptionPane.showMessageDialog(this, "App detected syntax errors (ignored by user)!", "Syntax Errors", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "App detected syntax errors!", "Syntax errors", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "App cannot find desired interface,\ncheck paths again!", "Interface Load Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel defFtTblModel = (DefaultTableModel) tblFeats.getModel();
        for (int i = tblFeats.getRowCount() - 1; i >= 0; i--) {
            defFtTblModel.removeRow(i);
        }

        initIntEn();
    }

    private void saveFromButton() {
        if (intrface.isInitialized()) {
            final File file = new File(cfg.getOutDir().getPath() + File.separator + cfg.getDefaultIni());
            boolean ok = intrface.writeIniFile(file);
            if (ok) {
                JOptionPane.showMessageDialog(this, "App successfully saved ini of your new interface!", "Interface Save", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "App encountered errors whilst saving ini of your interface!", "Interface Save Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to save, please load your interface!", "Interface Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFromMenu() {
        if (intrface.isInitialized() && targetIniFile != null) {
            boolean ok = intrface.writeIniFile(targetIniFile);
            if (ok) {
                JOptionPane.showMessageDialog(this, "App successfully saved ini of your new interface!", "Interface Save", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "App encountered errors whilst saving ini of your interface!", "Interface Save Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to save, please load your interface!", "Interface Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void infoAbout() {
        URL icon_url = getClass().getResource(RESOURCES_DIR + LICENSE_LOGO_FILE_NAME);
        if (icon_url != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><b>VERSION v1.0 - HUNS (PUBLIC BUILD reviewed on 2021-09-11 at 08:00).</b></html>\n");
            sb.append("<html><b>This software is free software, </b></html>\n");
            sb.append("<html><b>licensed under GNU General Public License (GPL).</b></html>\n");
            sb.append("\n");
            sb.append("Changelog since v1.0 HUNS:\n");
            sb.append("\t- Modules are being build faster.\n");
            sb.append("\t- Changes made to the components tab.\n");
            sb.append("\t- Component position updated whilst component moving.\n");
            sb.append("\t- Removed build module button. Everything is done automatically.\n");
            sb.append("\t- Add feature detects feature value type automatically.\n");
            sb.append("\n");
            sb.append("Changelog since v1.0 BETA1 GOTHS:\n");
            sb.append("\t- Fix for some modules (Barter, PipBoy etc.).\n");
            sb.append("\t- Question mark is put as annotation for missing or unknown picture.\n");
            sb.append("\t- Pictures with variable location on the screen for inventory and action points.\n");
            sb.append("\t- Feature to take screenshot of the module.\n");
            sb.append("\n");
            sb.append("Changelog since v0.5 FINLAND:\n");
            sb.append("\t- Modules are being build faster.\n");
            sb.append("\t- Preview Values Removed. Table is updated automatically.\n");
            sb.append("\t- Switch view between feature mode and component.\n");
            sb.append("\t- Feature to select & move components around.\n");
            sb.append("\n");
            sb.append("Changelog since v0.4 ESTONIA:\n");
            sb.append("\t- Add/Remove for features.\n");
            sb.append("\t- Fonts are properly scaled.\n");
            sb.append("\t- Fix for some modules to display correctly (like Global Map).\n");
            sb.append("\t- Fix for the Ini Writer.\n");
            sb.append("\n");
            sb.append("Changelog since v0.3 DEUTERIUM:\n");
            sb.append("\t- Initial pre-release.\n");
            sb.append("\n");
            sb.append("\n");
            sb.append("Purpose:\n");
            sb.append("FOnline2 S3 Interface Editor is a way to edit interface\n");
            sb.append("through linking features/components and modifies only .ini\n");
            sb.append("and is not an image editor itself.\n");
            sb.append("\n");
            sb.append("<html><b>Copyright © 2021</b></html>\n");
            sb.append("<html><b>Alexander \"Ermac\" Stojanovich</b></html>\n");
            sb.append("\n");
            ImageIcon icon = new ImageIcon(icon_url);
            JOptionPane.showMessageDialog(this, sb.toString(), "About", JOptionPane.INFORMATION_MESSAGE, icon);
        }
    }

    private void infoHelp() {
        URL icon_url = getClass().getResource(RESOURCES_DIR + LOGOX_FILE_NAME);
        if (icon_url != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><b>- FOR THE PURPOSE ABOUT THIS PROGRAM, </b></html>\n");
            sb.append("<html><b>check About. Make sure that you checked it first.</b></html>\n");
            sb.append("\n");
            sb.append("- Editing interface consists of several steps:\n");
            sb.append("\t1. Put (extract from archive if needed) interface to a single location,\n");
            sb.append("\t2. Choose input directory where \"art > intrface\" is,\n");
            sb.append("\t3. Choose output where modified result is gonna be stored,\n");
            sb.append("\t4. Click \"Load\" to load the interface from the input path,\n");
            sb.append("\t5. \"Check\" if loading the interface result in errors,\n");
            sb.append("\t6. Edit interface by editing it's features and (or) components,\n");
            sb.append("\t7. Click \"Save\" to save the interface on the output path.\n");
            sb.append("\n");
            sb.append("\t- Each line of the .ini file represents one feature.\n");
            sb.append("\t- One section module (or just module) consists of features.\n");
            sb.append("\t- Interface in the game is made of many modules.\n");
            sb.append("\t- By using \"All resolutions\" you're ignoring target resolution when module being built.\n");
            sb.append("\t- Preview module in the window with \"Preview Module\".\n");
            sb.append("\t- Table has two tabs, interface features and rendering components\n");
            sb.append("\t  which can be edited in either of these two mods,\n");
            sb.append("\t- In order to view Components build the module.\n");
            sb.append("\n");
            ImageIcon icon = new ImageIcon(icon_url);
            JOptionPane.showMessageDialog(this, sb.toString(), "How to use", JOptionPane.INFORMATION_MESSAGE, icon);
        }
    }

    // builds Animator Renderer GL components
    private void buildModuleComponents() {
        if (mode == Mode.ALL_RES) {
            renderer.buildMode = ModuleRenderer.BuildMode.ALL_RES;
            renderer.state = ModuleRenderer.State.BUILD;
        } else if (mode == Mode.TARGET_RES) {
            renderer.buildMode = ModuleRenderer.BuildMode.TARGET_RES;
            renderer.state = ModuleRenderer.State.BUILD;
        }
    }

    // edit feature value in the subform
    private void editFeatureValue() {
        final int srow = tblFeats.getSelectedRow();
        final int scol = tblFeats.getSelectedColumn();
        Object valueAtKey = tblFeats.getValueAt(srow, scol - 3);
        Object valueAtVal = tblFeats.getValueAt(srow, scol - 2);
        final FeatureKey featKey = FeatureKey.valueOf((String) valueAtKey);
        final FeatureValue featVal = FeatureValue.valueOf((String) valueAtVal);
        final FeatValueEditor fve = new FeatValueEditor(featKey, featVal, intrface, btnTogAllRes.isSelected()) {
            @Override
            public void execute() {
                tblFeats.setValueAt(featVal.getStringValue(), srow, scol - 2);
                buildModuleComponents();
            }
        };

        fve.setVisible(true);
        fve.setResizable(false);
        fve.pack();
    }

    // remove feature (with yes/no "are you sure" dialog)
    private void removeFeature() {
        final int srow = tblFeats.getSelectedRow();
        final int scol = tblFeats.getSelectedColumn();
        Object valueAtKey = tblFeats.getValueAt(srow, scol - 4);
        final FeatureKey featKey = FeatureKey.valueOf((String) valueAtKey);
        int val = JOptionPane.showConfirmDialog(this, "Are you sure you wanna remove feature " + featKey.getStringValue() + "?", "Remove feature", JOptionPane.YES_NO_OPTION);
        if (val == JOptionPane.YES_OPTION) {
            if (mode == Mode.ALL_RES) {
                intrface.getCommonFeatMap().remove(featKey);
            } else if (mode == Mode.TARGET_RES) {
                ResolutionPragma resolutionPragma = intrface.getResolutionPragma();
                if (resolutionPragma != null) {
                    resolutionPragma.getCustomFeatMap().remove(featKey);
                }
            }

//            DefaultTableModel model = (DefaultTableModel) tblFeatsComps.getModel();
//            model.removeRow(srow);
            featurePreview();
            componentsPreview();
            buildModuleComponents();
        }
    }

    // gives ability to add new features
    private void addFeature() {
        SectionName sectionName = (SectionName) cmbBoxSection.getSelectedItem();
        Section section = intrface.getNameToSectionMap().get(sectionName);

        FeatValueAdder fva = new FeatValueAdder(section, intrface, btnTogAllRes.isSelected()) {
            @Override
            public void execute() {
                featurePreview();
                componentsPreview();
                buildModuleComponents();
            }
        };
        fva.setVisible(true);
        fva.setResizable(false);
        fva.pack();
    }

    // makes preview for the feature table
    private synchronized void featurePreview() {
        if (mode == Mode.ALL_RES) {
            final DefaultTableModel ftTblMdl = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 3 || column == 4;
                }
            };
            ftTblMdl.addColumn("Feature Key");
            ftTblMdl.addColumn("Feature Value");
            ftTblMdl.addColumn("Overrides");
            ftTblMdl.addColumn("Edit Feature");
            ftTblMdl.addColumn("Remove Feature");

            ButtonEditor btnModifyEditor = new ButtonEditor(new JButton("Edit"));
            btnModifyEditor.getButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editFeatureValue();
                }
            });
            ButtonRenderer btnModifyRenderer = new ButtonRenderer(btnModifyEditor.getButton());

            ButtonEditor btnRemoveEditor = new ButtonEditor(new JButton("Remove"));
            btnRemoveEditor.getButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeFeature();
                }
            });
            ButtonRenderer btnRemoveRenderer = new ButtonRenderer(btnRemoveEditor.getButton());

            SectionName sectionName = (SectionName) cmbBoxSection.getSelectedItem();
            Section section = intrface.getNameToSectionMap().get(sectionName);
            for (FeatureKey featKey : section.getKeys()) {
                FeatureValue featVal = intrface.getCommonFeatMap().get(featKey);
                int overrides = 0;
                for (ResolutionPragma resPragma : intrface.getCustomResolutions()) {
                    if (resPragma.getCustomFeatMap().containsKey(featKey)) {
                        overrides++;
                    }
                }

                if (featVal != null) {
                    Object[] row = {featKey.getStringValue(), featVal.getStringValue(), overrides};
                    ftTblMdl.addRow(row);
                }
            }

            tblFeats.setModel(ftTblMdl);
            TableColumn editCol = tblFeats.getColumn("Edit Feature");
            editCol.setCellEditor(btnModifyEditor);
            editCol.setCellRenderer(btnModifyRenderer);

            TableColumn remCol = tblFeats.getColumn("Remove Feature");
            remCol.setCellEditor(btnRemoveEditor);
            remCol.setCellRenderer(btnRemoveRenderer);

        } else if (mode == Mode.TARGET_RES) {
            String resStr = (String) cmbBoxResolution.getSelectedItem();
            ResolutionPragma resolutionPragma = null;
            if (resStr != null) {
                String[] things = resStr.trim().split("x");
                int width = Integer.parseInt(things[0]);
                int height = Integer.parseInt(things[1]);
                for (ResolutionPragma resolution : intrface.getCustomResolutions()) {
                    if (resolution.getWidth() == width && resolution.getHeight() == height) {
                        resolutionPragma = resolution;
                        break;
                    }
                }
            }

            if (resolutionPragma != null) {
                final DefaultTableModel ftTblMdl = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return column == 3 || column == 4;
                    }
                };
                ftTblMdl.addColumn("Feature Key");
                ftTblMdl.addColumn("Feature Value");
                ftTblMdl.addColumn("Overrides");
                ftTblMdl.addColumn("Edit Feature");
                ftTblMdl.addColumn("Remove Feature");

                ButtonEditor btnModifyEditor = new ButtonEditor(new JButton("Edit"));
                btnModifyEditor.getButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        editFeatureValue();
                    }
                });
                ButtonRenderer btnModifyRenderer = new ButtonRenderer(btnModifyEditor.getButton());

                ButtonEditor btnRemoveEditor = new ButtonEditor(new JButton("Remove"));
                btnRemoveEditor.getButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        removeFeature();
                    }
                });
                ButtonRenderer btnRemoveRenderer = new ButtonRenderer(btnRemoveEditor.getButton());

                SectionName sectionName = (SectionName) cmbBoxSection.getSelectedItem();
                Section section = intrface.getNameToSectionMap().get(sectionName);
                for (FeatureKey featKey : section.getKeys()) {
                    FeatureValue featVal = resolutionPragma.getCustomFeatMap().get(featKey);

                    boolean overrides = intrface.getCommonFeatMap().containsKey(featKey);

                    if (featVal != null) {
                        Object[] row = {featKey.getStringValue(), featVal.getStringValue(), overrides};
                        ftTblMdl.addRow(row);
                    }
                }

                tblFeats.setModel(ftTblMdl);
                TableColumn editCol = tblFeats.getColumn("Edit Feature");
                editCol.setCellEditor(btnModifyEditor);
                editCol.setCellRenderer(btnModifyRenderer);

                TableColumn remCol = tblFeats.getColumn("Remove Feature");
                remCol.setCellEditor(btnRemoveEditor);
                remCol.setCellRenderer(btnRemoveRenderer);

            }
        }
    }

    // cuz its called from another thread (and may be called repeatedly)
    private void initBuildModule() {
        SectionName sectionName = (SectionName) cmbBoxSection.getSelectedItem();
        if (mode == Mode.ALL_RES) {
            intrface.setSectionName(sectionName);
            intrface.setResolutionPragma(null);
        } else if (mode == Mode.TARGET_RES) {
            String resStr = (String) cmbBoxResolution.getSelectedItem();
            if (resStr != null) {
                String[] things = resStr.trim().split("x");
                int width = Integer.parseInt(things[0]);
                int height = Integer.parseInt(things[1]);

                ResolutionPragma resolutionPragma = null;
                for (ResolutionPragma resolution : intrface.getCustomResolutions()) {
                    if (resolution.getWidth() == width && resolution.getHeight() == height) {
                        resolutionPragma = resolution;
                        break;
                    }
                }

                if (resolutionPragma != null) {
                    intrface.setSectionName(sectionName);
                    intrface.setResolutionPragma(resolutionPragma);
                }
            }
        }

    }

    private void btnMdlePreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMdlePreviewActionPerformed
        // TODO add your handling code here:        
        deselect();
        featurePreview();
        componentsPreview();
        workOnBuildComponents();
        if (intrface.getResolutionPragma() != null) {
            int width = intrface.getResolutionPragma().getWidth();
            int height = intrface.getResolutionPragma().getHeight();
            GL_WINDOW.setSize(width, height);
            GL_WINDOW.setTitle(cmbBoxSection.getSelectedItem() + " (Press F12 to take screenshot)");
        }
        GL_WINDOW.setVisible(true);
    }//GEN-LAST:event_btnMdlePreviewActionPerformed

    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckActionPerformed
        // TODO add your handling code here:
        StringBuilder sb = new StringBuilder();
        sb.append("Interface initialized: ")
                .append(intrface.isInitialized())
                .append("\n").append("Number of errors: ")
                .append(intrface.getErrorNum()).append("\n");
        sb.append("\n");
        sb.append(intrface.getErrStrMsg());
        sb.append("\n");
        if (intrface.isInitialized() && intrface.getErrorNum() == 0 && intrface.getErrStrMsg().length() == 0) {
            sb.append("Status: OK");
        } else if (!intrface.isInitialized()) {
            sb.append("Status: NOT INITIALIZED");
        } else {
            sb.append("Status: ERRONEOUS");
        }
        JTextArea textArea = new JTextArea(sb.toString(), 7, 20);
        JScrollPane jsp = new JScrollPane(textArea);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, jsp, "Interface status", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnCheckActionPerformed

    private void btnTogAllResActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTogAllResActionPerformed
        // TODO add your handling code here:
        this.mode = btnTogAllRes.isSelected() ? Mode.ALL_RES : Mode.TARGET_RES;
        cmbBoxResolution.setEnabled(!btnTogAllRes.isSelected());
        featurePreview();
        componentsPreview();
        workOnBuildComponents(); // important!
    }//GEN-LAST:event_btnTogAllResActionPerformed

    private void fileMenuLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuLoadActionPerformed
        // TODO add your handling code here:
        int returnVal = fileChooserIniLoad.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.targetIniFile = fileChooserIniLoad.getSelectedFile();
            cfg.setInDir(fileChooserIniLoad.getSelectedFile().getParentFile());
            txtFldInPath.setText(cfg.getInDir().getPath());
            txtFldInPath.setToolTipText(cfg.getInDir().getPath());
            loadFromMenu();
            featurePreview();
            componentsPreview();
            renderer.getModule().components.clear();
        }

        if (!cfg.getInDir().getPath().isEmpty()) {
            btnLoad.setEnabled(true);
        }

    }//GEN-LAST:event_fileMenuLoadActionPerformed

    private void fileMenuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuSaveActionPerformed
        // TODO add your handling code here: 
        if (intrface.isInitialized()) {
            if (targetIniFile != null) {
                saveFromMenu();
            } else {
                int returnVal = fileChooserIniSave.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    targetIniFile = fileChooserIniSave.getSelectedFile();
                    cfg.setOutDir(fileChooserIniSave.getSelectedFile().getParentFile());
                    txtFldOutPath.setText(cfg.getInDir().getPath());
                    txtFldOutPath.setToolTipText(cfg.getInDir().getPath());

                    saveFromMenu();
                }

                if (!cfg.getInDir().getPath().isEmpty()) {
                    btnLoad.setEnabled(true);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to save, please load your interface!", "Interface Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_fileMenuSaveActionPerformed

    private void fileMenuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuSaveAsActionPerformed
        // TODO add your handling code here:
        if (intrface.isInitialized()) {
            int returnVal = fileChooserIniSave.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                targetIniFile = fileChooserIniSave.getSelectedFile();
                cfg.setOutDir(fileChooserIniSave.getSelectedFile().getParentFile());
                txtFldInPath.setText(cfg.getInDir().getPath());
                txtFldInPath.setToolTipText(cfg.getInDir().getPath());
                saveFromMenu();
            }

            if (!cfg.getInDir().getPath().isEmpty()) {
                btnLoad.setEnabled(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to save, please load your interface!", "Interface Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_fileMenuSaveAsActionPerformed

    private void cmbBoxSectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBoxSectionActionPerformed
        // TODO add your handling code here:
        deselect();
        renderer.module.components.clear();
        featurePreview();
        componentsPreview();
        workOnBuildComponents();
    }//GEN-LAST:event_cmbBoxSectionActionPerformed

    private void cmbBoxResolutionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBoxResolutionActionPerformed
        // TODO add your handling code here:
        featurePreview();
        componentsPreview();
        workOnBuildComponents();
    }//GEN-LAST:event_cmbBoxResolutionActionPerformed

    private void btnAddFeatXDeselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFeatXDeselectActionPerformed
        // TODO add your handling code here:
        addFeature(); // add feature
    }//GEN-LAST:event_btnAddFeatXDeselectActionPerformed

    private void toolsScreenshotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolsScreenshotActionPerformed
        // TODO add your handling code here:
        renderer.state = ModuleRenderer.State.SCREENSHOT;
    }//GEN-LAST:event_toolsScreenshotActionPerformed

    private void fileInOpen() {
        int returnVal = fileChooserDirInput.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            cfg.setInDir(fileChooserDirInput.getSelectedFile());
            txtFldInPath.setText(cfg.getInDir().getPath());
            txtFldInPath.setToolTipText(cfg.getInDir().getPath());
        }

        if (!cfg.getInDir().getPath().isEmpty()) {
            btnLoad.setEnabled(true);
        }

        if (fileChooserDirInput.getSelectedFile() != null
                && fileChooserDirInput.getSelectedFile().equals(fileChooserDirOutput.getSelectedFile())) {
            JOptionPane.showMessageDialog(this, "Input and output path are the same,\nIt may lead to undesired results!", "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void fileOutOpen() {
        int returnVal = fileChooserDirOutput.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            cfg.setOutDir(fileChooserDirOutput.getSelectedFile());
            txtFldOutPath.setText(cfg.getOutDir().getPath());
            txtFldOutPath.setToolTipText(cfg.getOutDir().getPath());
        }

        if (!cfg.getOutDir().getPath().isEmpty()) {
            btnSave.setEnabled(true);
        }

        if (fileChooserDirOutput.getSelectedFile() != null
                && fileChooserDirOutput.getSelectedFile().equals(fileChooserDirInput.getSelectedFile())) {
            JOptionPane.showMessageDialog(this, "Input and output path are the same,\nIt may lead to undesired results!", "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void deselect() {
        if (renderer.selected != null) {
            renderer.selected.setColor(renderer.savedColor);
        }
        renderer.selected = null;
    }

    private void selectComponent() {
        final int srow = tblComps.getSelectedRow();
        final int scol = tblComps.getSelectedColumn();
        Object valueAtKey = tblComps.getValueAt(srow, scol - 4);
        final FeatureKey featKey = FeatureKey.valueOf((String) valueAtKey);

        // deselect
        deselect();

        for (GLComponent glc : renderer.module.components) {
            if (glc.getFeatureKey() == featKey) {
                renderer.selected = glc;
                renderer.savedColor = glc.getColor();
                renderer.selected.setColor(GLColor.awtColorToVec4(cfg.getSelectCol()));
                break;
            }
        }

    }

    private synchronized void componentsPreview() {
        final DefaultTableModel compTblMdl = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        compTblMdl.addColumn("Name");
        compTblMdl.addColumn("Position");
        compTblMdl.addColumn("Dimension");
        compTblMdl.addColumn("Type");
        compTblMdl.addColumn("Select");

        ButtonEditor selEdit = new ButtonEditor(new JButton("Select"));
        selEdit.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectComponent();
            }
        });
        ButtonRenderer selRend = new ButtonRenderer(selEdit.getButton());

        for (GLComponent glc : renderer.module.components) {
            FeatureKey fk = glc.getFeatureKey();
            FeatureValue fv = null;
            if (mode == Mode.ALL_RES) {
                fv = intrface.getCommonFeatMap().get(fk);
            } else if (mode == Mode.TARGET_RES) {
                ResolutionPragma resolutionPragma = intrface.getResolutionPragma();
                if (resolutionPragma != null) {
                    fv = resolutionPragma.getCustomFeatMap().get(fk);
                }
            }

            if (fk != null && fv != null) {
                String dim = glc.getWidth() + "x" + glc.getHeight();
                String pos = Math.round(glc.getPos().x - glc.getWidth() / 2.0f) + ", " + Math.round(glc.getPos().y - glc.getHeight() / 2.0f);
                Object[] row = {fk.getStringValue(), pos, dim, glc.getType()};
                compTblMdl.addRow(row);
            }
        }

        tblComps.setModel(compTblMdl);
        TableColumn selCol = tblComps.getColumn("Select");
        selCol.setCellEditor(selEdit);
        selCol.setCellRenderer(selRend);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FO2IELogger.init(args.length > 0 && args[0].equals("-debug"));
        cfg.readConfigFile();

        // start splash screen now!
        GUISplashScreen splashScreen = new GUISplashScreen();
        splashScreen.setUp();

        Thread splashUpdater = new Thread(splashScreen, "Splash Screen Updater");
        splashUpdater.start();

        // Load Palette for FRMs
        Palette.load("Fallout Palette.act");

        // to measure elapsed time in interval [0, 120) cycled
        GameTime gameTime = GameTime.getInstance();
        gameTime.start();

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel("com.bulenkov.darcula.DarculaLaf");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            FO2IELogger.reportError(ex.getMessage(), ex);
        }
        //</editor-fold>
        /* Create and display the form */
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI gui = new GUI();
                gui.setVisible(true);
                gui.pack();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cfg.writeConfigFile();
            }
        });

    }

    public static float getProgress() {
        return progress;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddFeat;
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnChooseInPath;
    private javax.swing.JButton btnChoosePathOut;
    private javax.swing.JButton btnLoad;
    private javax.swing.JButton btnMdlePreview;
    private javax.swing.JButton btnSave;
    private javax.swing.JToggleButton btnTogAllRes;
    private javax.swing.JComboBox<Object> cmbBoxResolution;
    private javax.swing.JComboBox<Section.SectionName> cmbBoxSection;
    private javax.swing.JFileChooser fileChooserDirInput;
    private javax.swing.JFileChooser fileChooserDirOutput;
    private javax.swing.JFileChooser fileChooserIniLoad;
    private javax.swing.JFileChooser fileChooserIniSave;
    private javax.swing.JMenuItem fileMenuExit;
    private javax.swing.JMenuItem fileMenuLoad;
    private javax.swing.JMenuItem fileMenuSave;
    private javax.swing.JMenuItem fileMenuSaveAs;
    private javax.swing.JPopupMenu.Separator fileMenuSep0;
    private javax.swing.JPopupMenu.Separator fileMenuSep1;
    private javax.swing.JMenuItem infoMenuAbout;
    private javax.swing.JMenuItem infoMenuHelp;
    private javax.swing.JLabel lblInput;
    private javax.swing.JLabel lblOutput;
    private javax.swing.JLabel lblResolution;
    private javax.swing.JLabel lblSection;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JMenu mainMenuFile;
    private javax.swing.JMenu mainMenuInfo;
    private javax.swing.JMenu mainMenuTools;
    private javax.swing.JPanel panelModule;
    private javax.swing.JPanel pnlFilePaths;
    private javax.swing.JPanel pnlIntrface;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JScrollPane sbComps;
    private javax.swing.JScrollPane sbFeatures;
    private javax.swing.JTabbedPane tabPaneBrowser;
    private javax.swing.JTable tblComps;
    private javax.swing.JTable tblFeats;
    private javax.swing.JMenuItem toolsScreenshot;
    private javax.swing.JTextField txtFldInPath;
    private javax.swing.JTextField txtFldOutPath;
    // End of variables declaration//GEN-END:variables
}
