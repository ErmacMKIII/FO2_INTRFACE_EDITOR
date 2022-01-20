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
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.frm.Palette;
import rs.alexanderstojanovich.fo2ie.helper.ButtonEditor;
import rs.alexanderstojanovich.fo2ie.helper.ButtonRenderer;
import rs.alexanderstojanovich.fo2ie.helper.ToggleButtonEditor;
import rs.alexanderstojanovich.fo2ie.helper.ToggleButtonRenderer;
import rs.alexanderstojanovich.fo2ie.intrface.Configuration;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.intrface.ResolutionPragma;
import rs.alexanderstojanovich.fo2ie.intrface.Section;
import rs.alexanderstojanovich.fo2ie.intrface.Section.SectionName;
import rs.alexanderstojanovich.fo2ie.ogl.GLComponent;
import rs.alexanderstojanovich.fo2ie.ogl.Text;
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
    private final Module module = new Module();
    private final ModuleRenderer mdlRenderer = new ModuleRenderer(fpsAnim, module, intrface) {
        @Override
        public void afterSelection() {
            updateFeaturePreview();
            updateComponentsPreview();
        }

        @Override
        public void afterModuleBuild() {
            //btnBuild.setEnabled(true);
            btnMdlePreview.setEnabled(true);
            initFeaturePreview();
            initComponentsPreview();
        }
    };

    private final WindowRenderer winRenderer = new WindowRenderer(fpsAnim, module, intrface) {
        @Override
        public void afterSelection() {
            updateFeaturePreview();
            updateComponentsPreview();
        }
    };

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

    public static final String ICON_BTN_ENABLED = "eye_enabled_icon.png";
    public static final String ICON_BTN_DISABLED = "eye_disabled_icon.png";

    private File targetIniFile;

    private ImageIcon bteEnabledIcon;
    private ImageIcon bteDisabledIcon;

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
        initIconsForFeatTables();
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
        GL_CANVAS.addGLEventListener(mdlRenderer);
        // for moving GLComponents
        GL_CANVAS.addKeyListener(mdlRenderer);
        GL_CANVAS.addMouseListener(mdlRenderer);
        GL_CANVAS.addMouseMotionListener(mdlRenderer);

        GL_CANVAS.setSize(panelModule.getSize());
        GL_CANVAS.setPreferredSize(panelModule.getPreferredSize());
        this.panelModule.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                buildModuleComponents();
            }
        });

        GL_WINDOW.setTitle("Module preview");
        GL_WINDOW.addGLEventListener(winRenderer);
        GL_WINDOW.addKeyListener(winRenderer);

        GL_WINDOW.addGLEventListener(winRenderer);
        GL_WINDOW.addMouseListener(winRenderer);
        GL_WINDOW.addKeyListener(winRenderer);

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
        if (mdlRenderer.state == ModuleRenderer.State.BUILD) {
            return;
        }

        btnMdlePreview.setEnabled(false);

        initBuildModule();
        buildModuleComponents();
    }

    private void initIconsForFeatTables() {
        URL url_bteicon1 = GUI.class.getResource(RESOURCES_DIR + ICON_BTN_ENABLED);
        bteEnabledIcon = new ImageIcon(url_bteicon1);

        URL url_bteicon2 = GUI.class.getResource(RESOURCES_DIR + ICON_BTN_DISABLED);
        bteDisabledIcon = new ImageIcon(url_bteicon2);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        jPanel1 = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtFldSearch = new javax.swing.JTextField();
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
        setTitle("FOnline2 S3 Interface Editor - JAPANESE");
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));
        setSize(new java.awt.Dimension(800, 600));
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

        cmbBoxSection.setMaximumRowCount(5);
        cmbBoxSection.setModel(DCBM);
        cmbBoxSection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBoxSectionActionPerformed(evt);
            }
        });
        pnlIntrface.add(cmbBoxSection);

        lblResolution.setText("Resolution:");
        pnlIntrface.add(lblResolution);

        cmbBoxResolution.setMaximumRowCount(5);
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
        pnlTable.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        lblSearch.setText("Filter:");
        jPanel1.add(lblSearch);
        jPanel1.add(txtFldSearch);

        pnlTable.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        tabPaneBrowser.setEnabled(false);
        tabPaneBrowser.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabPaneBrowserStateChanged(evt);
            }
        });

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

        pnlTable.add(tabPaneBrowser, java.awt.BorderLayout.CENTER);

        btnAddFeat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rs/alexanderstojanovich/fo2ie/res/feat_plus.png"))); // NOI18N
        btnAddFeat.setText("Add Feature");
        btnAddFeat.setToolTipText("Add new feature for this section");
        btnAddFeat.setEnabled(false);
        btnAddFeat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFeatXDeselectActionPerformed(evt);
            }
        });
        pnlTable.add(btnAddFeat, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(pnlTable);

        panelModule.setBorder(javax.swing.BorderFactory.createTitledBorder("Module"));
        panelModule.setLayout(new java.awt.BorderLayout());
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
        mdlRenderer.getModule().components.clear();
        loadFromButton();
        initFeaturePreview();
        initComponentsPreview();
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
            sb.append("VERSION v1.2 - JAPANESE (PUBLIC BUILD reviewed on 2022-01-20 at 02:00).\n");
            sb.append("This software is free software, \n");
            sb.append("licensed under GNU General Public License (GPL).\n");
            sb.append("\n");
            sb.append("Changelog since v1.2 JAPANESE:\n");
            sb.append("\t- Feature \"eye\" button - to hide/reveal components. [VVish] \n");
            sb.append("\t- Search bar for the both tables (Feature & Component tabs).\n");
            sb.append("\t- Reworked selection: CTRL + A -> select by mouse cursor, CTRL + D -> deselect, '[' - ']' -> select range.\n");
            sb.append("\n");
            sb.append("Changelog since v1.1 IODINE:\n");
            sb.append("\t- Fixed that only one popup window\n");
            sb.append("\t  for add feature / edit feature / edit component can be.\n");
            sb.append("\t- Feature to edit position of the components (which also affects feature values).\n");
            sb.append("\t- Module preview has correct border.\n");
            sb.append("\n");
            sb.append("Changelog since v1.0 HUNS:\n");
            sb.append("\t- Modules are being build faster.\n");
            sb.append("\t- Changes made to the components tab.\n");
            sb.append("\t- Component position updated whilst component moving.\n");
            sb.append("\t- Removed build module button. Everything is done automatically.\n");
            sb.append("\t- Add feature detects feature value type automatically.\n");
            sb.append("\t- Screenshots can be taken from the preview window (hotkey F12).\n");
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
            sb.append("\n");
            sb.append("Purpose:\n");
            sb.append("FOnline2 S3 Interface Editor is a way to edit interface\n");
            sb.append("through linking features/components and modifies only the .ini\n");
            sb.append("and is not an image editor itself.\n");
            sb.append("\n");
            sb.append("Copyright © 2022\n");
            sb.append("Alexander \"Ermac\" Stojanovich\n");
            sb.append("\n");
            ImageIcon icon = new ImageIcon(icon_url);
            JTextArea textArea = new JTextArea(sb.toString(), 15, 50);
            JScrollPane jsp = new JScrollPane(textArea);
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, jsp, "About", JOptionPane.INFORMATION_MESSAGE, icon);
        }
    }

    private void infoHelp() {
        URL icon_url = getClass().getResource(RESOURCES_DIR + LOGOX_FILE_NAME);
        if (icon_url != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("- FOR THE PURPOSE ABOUT THIS PROGRAM, \n");
            sb.append("check About. Make sure that you checked it first.\n");
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
            sb.append("[*] Trivia:\n");
            sb.append("- Each line of the .ini file represents one feature.\n");
            sb.append("- One section module (or just module) consists of features.\n");
            sb.append("- Interface in the game is made of many modules.\n");
            sb.append("- By using \"All resolutions\" you're ignoring target resolution when module being built.\n");
            sb.append("- Preview module in the window with \"Preview Module\".\n");
            sb.append("- Table has two tabs, interface features and rendering components\n");
            sb.append("  which can be edited in either of these two mods,\n");
            sb.append("- In order to view Components build the module.\n");
            sb.append("- To include resolution e.g. 1366x768 put \"resolution 1366 768\" as a new line in the .ini\n");
            sb.append("  and reload your interface.\n");
            sb.append("\n");
            ImageIcon icon = new ImageIcon(icon_url);
            JTextArea textArea = new JTextArea(sb.toString(), 15, 50);
            JScrollPane jsp = new JScrollPane(textArea);
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, jsp, "How to use", JOptionPane.INFORMATION_MESSAGE, icon);
        }
    }

    // builds Animator Renderer GL components
    public void buildModuleComponents() {
        if (mode == Mode.ALL_RES) {
            mdlRenderer.buildMode = ModuleRenderer.BuildMode.ALL_RES;
            mdlRenderer.state = ModuleRenderer.State.BUILD;
        } else if (mode == Mode.TARGET_RES) {
            mdlRenderer.buildMode = ModuleRenderer.BuildMode.TARGET_RES;
            mdlRenderer.state = ModuleRenderer.State.BUILD;
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

        // featKey, featVal, intrface, btnTogAllRes.isSelected()
        final FeatValueEditor fve = FeatValueEditor.getInstance(this);
        fve.popUp(featKey, featVal, intrface, btnTogAllRes.isSelected());

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

            DefaultTableModel model = (DefaultTableModel) tblFeats.getModel();
            model.removeRow(srow);

            updateFeaturePreview();
            updateComponentsPreview();

            buildModuleComponents();
        }
    }

    // gives ability to add new features
    private void addFeature() {
        SectionName sectionName = (SectionName) cmbBoxSection.getSelectedItem();
        Section section = intrface.getNameToSectionMap().get(sectionName);

        FeatValueAdder fva = FeatValueAdder.getInstance(this);
        fva.popUp(section, intrface, btnTogAllRes.isSelected());

        fva.setVisible(true);
        fva.setResizable(false);
        fva.pack();
    }

    // makes preview for the feature table
    public synchronized void initFeaturePreview() {
        if (mode == Mode.ALL_RES) {
            final DefaultTableModel ftTblMdl = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return (column == 3 || column == 4);
                }
            };

            TableRowSorter<DefaultTableModel> sort = new TableRowSorter<>(ftTblMdl);
            txtFldSearch.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    String str = txtFldSearch.getText();
                    if (str.trim().length() == 0) {
                        sort.setRowFilter(null);
                    } else {
                        sort.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    String str = txtFldSearch.getText();
                    if (str.trim().length() == 0) {
                        sort.setRowFilter(null);
                    } else {
                        sort.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {

                }
            });
            tblFeats.setRowSorter(sort);

            ftTblMdl.addColumn("Feature Key");
            ftTblMdl.addColumn("Feature Value");
            ftTblMdl.addColumn("Overrides");
            ftTblMdl.addColumn("Edit Feature");
            ftTblMdl.addColumn("Remove Feature");

            final ButtonEditor btnModifyEditor = new ButtonEditor(new JButton("Edit"));
            btnModifyEditor.getButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editFeatureValue();
                }
            });
            final ButtonRenderer btnModifyRenderer = new ButtonRenderer(btnModifyEditor.getButton());

            final ButtonEditor btnRemoveEditor = new ButtonEditor(new JButton("Remove"));
            btnRemoveEditor.getButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeFeature();
                }
            });
            final ButtonRenderer btnRemoveRenderer = new ButtonRenderer(btnRemoveEditor.getButton());

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

                TableRowSorter<DefaultTableModel> sort = new TableRowSorter<>(ftTblMdl);
                txtFldSearch.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        String str = txtFldSearch.getText();
                        if (str.trim().length() == 0) {
                            sort.setRowFilter(null);
                        } else {
                            sort.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                        }
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        String str = txtFldSearch.getText();
                        if (str.trim().length() == 0) {
                            sort.setRowFilter(null);
                        } else {
                            sort.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                        }
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {

                    }
                });
                tblFeats.setRowSorter(sort);

                ftTblMdl.addColumn("Feature Key");
                ftTblMdl.addColumn("Feature Value");
                ftTblMdl.addColumn("Overrides");
                ftTblMdl.addColumn("Edit Feature");
                ftTblMdl.addColumn("Remove Feature");

                final ButtonEditor btnModifyEditor = new ButtonEditor(new JButton("Edit"));
                btnModifyEditor.getButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        editFeatureValue();
                    }
                });
                final ButtonRenderer btnModifyRenderer = new ButtonRenderer(btnModifyEditor.getButton());

                final ButtonEditor btnRemoveEditor = new ButtonEditor(new JButton("Remove"));
                btnRemoveEditor.getButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        removeFeature();
                    }
                });
                final ButtonRenderer btnRemoveRenderer = new ButtonRenderer(btnRemoveEditor.getButton());

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

    // makes preview for the feature table
    public synchronized void updateFeaturePreview() {
        if (mode == Mode.ALL_RES) {
            DefaultTableModel ftTblMdl = (DefaultTableModel) this.tblFeats.getModel();

            for (int row = 0; row < ftTblMdl.getRowCount(); row++) {
                FeatureKey featKey = FeatureKey.valueOf((String) ftTblMdl.getValueAt(row, 0));
                FeatureValue featVal = intrface.getCommonFeatMap().get(featKey);
                int overrides = 0;
                for (ResolutionPragma resPragma : intrface.getCustomResolutions()) {
                    if (resPragma.getCustomFeatMap().containsKey(featKey)) {
                        overrides++;
                    }
                }

                if (featVal != null) {
                    Object[] objs = {featKey.getStringValue(), featVal.getStringValue(), overrides};
                    int col = 0;
                    for (Object obj : objs) {
                        ftTblMdl.setValueAt(obj, row, col);
                        col++;
                    }
                }
            }

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
                DefaultTableModel ftTblMdl = (DefaultTableModel) this.tblFeats.getModel();

                for (int row = 0; row < ftTblMdl.getRowCount(); row++) {
                    FeatureKey featKey = FeatureKey.valueOf((String) ftTblMdl.getValueAt(row, 0));
                    FeatureValue featVal = resolutionPragma.getCustomFeatMap().get(featKey);

                    boolean overrides = intrface.getCommonFeatMap().containsKey(featKey);

                    if (featVal != null) {
                        Object[] objs = {featKey.getStringValue(), featVal.getStringValue(), overrides};
                        int col = 0;
                        for (Object obj : objs) {
                            ftTblMdl.setValueAt(obj, row, col);
                            col++;
                        }
                    }

                }
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
        updateFeaturePreview();
        updateComponentsPreview();
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
        initFeaturePreview();
        initComponentsPreview();
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
            initFeaturePreview();
            initComponentsPreview();
            mdlRenderer.getModule().components.clear();
            workOnBuildComponents(); // important!
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
        mdlRenderer.module.components.clear();
        initFeaturePreview();
        initComponentsPreview();
        workOnBuildComponents();
    }//GEN-LAST:event_cmbBoxSectionActionPerformed

    private void cmbBoxResolutionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBoxResolutionActionPerformed
        // TODO add your handling code here:
        initFeaturePreview();
        initComponentsPreview();
        workOnBuildComponents();
    }//GEN-LAST:event_cmbBoxResolutionActionPerformed

    private void btnAddFeatXDeselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFeatXDeselectActionPerformed
        // TODO add your handling code here:
        addFeature(); // add feature
    }//GEN-LAST:event_btnAddFeatXDeselectActionPerformed

    private void toolsScreenshotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolsScreenshotActionPerformed
        // TODO add your handling code here:
        mdlRenderer.state = ModuleRenderer.State.SCREENSHOT;
    }//GEN-LAST:event_toolsScreenshotActionPerformed

    private void tabPaneBrowserStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPaneBrowserStateChanged
        // TODO add your handling code here:
        if (tabPaneBrowser.getSelectedIndex() == 0) {
            updateFeaturePreview();
        } else if (tabPaneBrowser.getSelectedIndex() == 1) {
            updateComponentsPreview();
        }
    }//GEN-LAST:event_tabPaneBrowserStateChanged

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
        if (mdlRenderer.selected != null) {
            mdlRenderer.selected.setColor(mdlRenderer.savedColor);
        }
        mdlRenderer.selected = null;
    }

    public void toggleEnableComponent() {
        final int srow = tblComps.getSelectedRow();
        final int scol = tblComps.getSelectedColumn();
        Object valueAtKey = tblComps.getValueAt(srow, scol - 4);
        final FeatureKey featKey = FeatureKey.valueOf((String) valueAtKey);

        // deselect
        deselect();

        for (GLComponent glc : mdlRenderer.module.components) {
            if (glc.getFeatureKey() == featKey) {
                boolean en = glc.isEnabled();
                glc.setEnabled(!en);
                if (glc instanceof Text) {
                    Text txt = (Text) glc;
                    txt.getOverlay().setEnabled(!en);
                }
                tblComps.setValueAt(glc.isEnabled(), srow, scol);
                break;
            }
        }
    }

    private void selectComponent() {
        final int srow = tblComps.getSelectedRow();
        final int scol = tblComps.getSelectedColumn();
        Object valueAtKey = tblComps.getValueAt(srow, scol - 6);
        final FeatureKey featKey = FeatureKey.valueOf((String) valueAtKey);

        // deselect
        deselect();

        for (GLComponent glc : mdlRenderer.module.components) {
            if (glc.getFeatureKey() == featKey && glc.isEnabled()) {
                mdlRenderer.selected = glc;
                mdlRenderer.savedColor = glc.getColor();
                mdlRenderer.selected.setColor(GLColor.awtColorToVec4(cfg.getSelectCol()));
                break;
            }
        }

    }

    private void editComponent() {
        final int srow = tblComps.getSelectedRow();
        final int scol = tblComps.getSelectedColumn();

        Object valueAtKey = tblComps.getValueAt(srow, scol - 5);

        final FeatureKey featKey = FeatureKey.valueOf((String) valueAtKey);
        FeatureValue featVal = null;

        if (mode == Mode.ALL_RES) {
            featVal = intrface.getCommonFeatMap().get(featKey);
        } else if (mode == Mode.TARGET_RES) {
            ResolutionPragma resolutionPragma = intrface.getResolutionPragma();
            if (resolutionPragma != null) {
                featVal = resolutionPragma.getCustomFeatMap().get(featKey);
            }
        }

        GLComponent glcKey = null;
        for (GLComponent glc : mdlRenderer.module.components) {
            if (glc.getFeatureKey() == featKey && glc.isEnabled()) {
                glcKey = glc;
                break;
            }
        }

        if (glcKey != null && featVal != null) {
            //(featKey, featVal, intrface, btnTogAllRes.isSelected(), glcKey)
            final ComponentEditor compEditor = ComponentEditor.getInstance(this);
            compEditor.popUp(featKey, featVal, intrface, rootPaneCheckingEnabled, glcKey);
            compEditor.setVisible(true);
            compEditor.setResizable(false);
            compEditor.pack();
        }
    }

    public synchronized void initComponentsPreview() {
        final DefaultTableModel compTblMdl = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return ((column == 4) || (column == 5) || (column == 6));
            }
        };

        TableRowSorter<DefaultTableModel> sort = new TableRowSorter<>(compTblMdl);
        txtFldSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String str = txtFldSearch.getText();
                if (str.trim().length() == 0) {
                    sort.setRowFilter(null);
                } else {
                    sort.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String str = txtFldSearch.getText();
                if (str.trim().length() == 0) {
                    sort.setRowFilter(null);
                } else {
                    sort.setRowFilter(RowFilter.regexFilter("(?i)" + str));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        tblComps.setRowSorter(sort);

        compTblMdl.addColumn("Name");
        compTblMdl.addColumn("Position");
        compTblMdl.addColumn("Dimension");
        compTblMdl.addColumn("Type");
        compTblMdl.addColumn("Enabled");
        compTblMdl.addColumn("Edit Position");
        compTblMdl.addColumn("Select & Drag");

        final ToggleButtonEditor bteEdit = new ToggleButtonEditor(new JToggleButton("Disable", bteEnabledIcon));
        bteEdit.getToggleButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleEnableComponent();
            }
        });
        final ToggleButtonRenderer bteRend = new ToggleButtonRenderer(bteEdit.getToggleButton(), bteEnabledIcon, bteDisabledIcon);

        final ButtonEditor propEdit = new ButtonEditor(new JButton("Edit"));
        propEdit.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editComponent();
            }
        });
        final ButtonRenderer propRend = new ButtonRenderer(propEdit.getButton());

        final ButtonEditor selEdit = new ButtonEditor(new JButton("Select"));
        selEdit.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectComponent();
            }
        });
        final ButtonRenderer selRend = new ButtonRenderer(selEdit.getButton());

        for (GLComponent glc : mdlRenderer.module.components) {
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
                Object[] row = {fk.getStringValue(), pos, dim, glc.getType(), glc.isEnabled()};
                compTblMdl.addRow(row);
            }
        }

        tblComps.setModel(compTblMdl);

        TableColumn enCol = tblComps.getColumn("Enabled");
        enCol.setCellEditor(bteEdit);
        enCol.setCellRenderer(bteRend);

        TableColumn selCol = tblComps.getColumn("Select & Drag");
        selCol.setCellEditor(selEdit);
        selCol.setCellRenderer(selRend);

        TableColumn propCol = tblComps.getColumn("Edit Position");
        propCol.setCellEditor(propEdit);
        propCol.setCellRenderer(propRend);
    }

    public synchronized void updateComponentsPreview() {
        DefaultTableModel compTblMdl = (DefaultTableModel) tblComps.getModel();

        for (int row = 0; row < tblComps.getRowCount(); row++) {
            FeatureKey fk = FeatureKey.valueOf((String) compTblMdl.getValueAt(row, 0));
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
                GLComponent glcTarg = null;
                for (GLComponent glc : mdlRenderer.module.components) {
                    if (glc.getFeatureKey() == fk) {
                        glcTarg = glc;
                        break;
                    }
                }

                if (glcTarg != null) {
                    String dim = glcTarg.getWidth() + "x" + glcTarg.getHeight();
                    String pos = Math.round(glcTarg.getPos().x - glcTarg.getWidth() / 2.0f) + ", " + Math.round(glcTarg.getPos().y - glcTarg.getHeight() / 2.0f);
                    Object[] objs = {fk.getStringValue(), pos, dim, glcTarg.getType(), glcTarg.isEnabled()};

                    int col = 0;
                    for (Object obj : objs) {
                        compTblMdl.setValueAt(obj, row, col);
                        col++;
                    }
                }
            }

        }
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblInput;
    private javax.swing.JLabel lblOutput;
    private javax.swing.JLabel lblResolution;
    private javax.swing.JLabel lblSearch;
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
    private javax.swing.JTextField txtFldSearch;
    // End of variables declaration//GEN-END:variables
}
