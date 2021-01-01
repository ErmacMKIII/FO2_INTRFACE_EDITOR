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
package rs.alexanderstojanovich.fo2ie.editor;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import rs.alexanderstojanovich.fo2ie.feature.FeatureKey;
import rs.alexanderstojanovich.fo2ie.feature.FeatureValue;
import rs.alexanderstojanovich.fo2ie.frm.Palette;
import rs.alexanderstojanovich.fo2ie.intrface.Configuration;
import rs.alexanderstojanovich.fo2ie.intrface.Intrface;
import rs.alexanderstojanovich.fo2ie.intrface.ResolutionPragma;
import rs.alexanderstojanovich.fo2ie.intrface.Section;
import rs.alexanderstojanovich.fo2ie.intrface.Section.SectionName;
import rs.alexanderstojanovich.fo2ie.util.FO2IELogger;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class GUI extends javax.swing.JFrame {

    public static final GLProfile GL20 = GLProfile.get(GLProfile.GL2);
    public static final GLCapabilities GL_CAP = new GLCapabilities(GL20);

    public static final GLWindow GL_WINDOW = GLWindow.create(GL_CAP);
    public static final GLCanvas GL_CANVAS = new GLCanvas(GL_CAP);

    private static final Configuration cfg = Configuration.getInstance();
    private static final DefaultComboBoxModel<Section.SectionName> DCBM = new DefaultComboBoxModel<>(Section.SectionName.values());
    private final Intrface intrface = new Intrface();
    private final ModuleAnimation mdlAnim = new ModuleAnimation(intrface);
    private final FPSAnimator canvAnim = new FPSAnimator(GL_CANVAS, 60, true);

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

    /**
     * Creates new form GUI
     */
    public GUI() {
        initComponents(); // netbeans loading components
        initFO2IELogos(); // logos for app
        initPaths(); // set paths from config
        initGL(); // sets GL canvas 
        initIntEn(); // init enable intrface panel components {comboxes, build module, preview values etc}
    }

    // init both logos
    private void initFO2IELogos() {
        URL url_logo = getClass().getResource(RESOURCES_DIR + LOGO_FILE_NAME);
        URL url_logox = getClass().getResource(RESOURCES_DIR + LOGOX_FILE_NAME);
        if (url_logo != null && url_logox != null) {
            ImageIcon logo = new ImageIcon(url_logo);
            ImageIcon logox = new ImageIcon(url_logox);
            List<Image> icons = new ArrayList<>();
            icons.add(logo.getImage());
            icons.add(logox.getImage());
            this.setIconImages(icons);//.getScaledInstance(23, 14, Image.SCALE_SMOOTH));
        }
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
        GL_CANVAS.addGLEventListener(mdlAnim);
        GL_CANVAS.setSize(panelModule.getSize());
        GL_CANVAS.setPreferredSize(panelModule.getPreferredSize());
        this.panelModule.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = e.getComponent().getWidth();
                int height = e.getComponent().getHeight();
                GL_CANVAS.setSize(width, height);
//                FO2IELogger.reportInfo(GL_CANVAS.getSize().toString(), null);
            }
        });
        this.panelModule.add(GL_CANVAS);
    }

    private void initIntEn() {

        boolean ok = intrface.isInitialized() && intrface.getErrorNum() == 0;

        for (int i = 0; i < this.pnlIntrface.getComponentCount(); i++) {
            this.pnlIntrface.getComponent(i).setEnabled(ok);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooserInput = new javax.swing.JFileChooser();
        fileChooserOutput = new javax.swing.JFileChooser();
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
        cmbBoxResolution = new javax.swing.JComboBox<>();
        lblResolution = new javax.swing.JLabel();
        cmbBoxSection = new javax.swing.JComboBox<>();
        btnTogAllRes = new javax.swing.JToggleButton();
        btnTblePreview = new javax.swing.JButton();
        btnBuild = new javax.swing.JButton();
        btnMdlePreview = new javax.swing.JButton();
        pnlTable = new javax.swing.JPanel();
        sbFeatures = new javax.swing.JScrollPane();
        tblFeatures = new javax.swing.JTable();
        panelModule = new javax.swing.JPanel();
        mainMenu = new javax.swing.JMenuBar();
        mainMenuFile = new javax.swing.JMenu();
        fileMenuExit = new javax.swing.JMenuItem();
        mainMenuInfo = new javax.swing.JMenu();
        infoMenuAbout = new javax.swing.JMenuItem();
        infoMenuHelp = new javax.swing.JMenuItem();

        fileChooserInput.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        fileChooserOutput.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        fileChooserOutput.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FOnline2 S3 Interface Editor");
        setMinimumSize(new java.awt.Dimension(960, 540));
        setPreferredSize(new java.awt.Dimension(960, 540));
        setSize(new java.awt.Dimension(960, 540));
        getContentPane().setLayout(new java.awt.GridLayout(2, 2));

        pnlFilePaths.setBorder(javax.swing.BorderFactory.createTitledBorder("Directory Paths"));
        pnlFilePaths.setLayout(new java.awt.GridLayout(3, 3));

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
        pnlIntrface.setLayout(new java.awt.GridLayout(4, 3));

        lblSection.setText("Section:");
        pnlIntrface.add(lblSection);
        pnlIntrface.add(cmbBoxResolution);

        lblResolution.setText("Resolution:");
        pnlIntrface.add(lblResolution);

        cmbBoxSection.setModel(DCBM);
        pnlIntrface.add(cmbBoxSection);

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

        btnTblePreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rs/alexanderstojanovich/fo2ie/res/table_icon.png"))); // NOI18N
        btnTblePreview.setText("Preview Values");
        btnTblePreview.setToolTipText("Preview features in the table");
        btnTblePreview.setIconTextGap(5);
        btnTblePreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTblePreviewActionPerformed(evt);
            }
        });
        pnlIntrface.add(btnTblePreview);

        btnBuild.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rs/alexanderstojanovich/fo2ie/res/build_icon.png"))); // NOI18N
        btnBuild.setText("Build Module");
        btnBuild.setToolTipText("Build module");
        btnBuild.setIconTextGap(5);
        btnBuild.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuildActionPerformed(evt);
            }
        });
        pnlIntrface.add(btnBuild);

        btnMdlePreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rs/alexanderstojanovich/fo2ie/res/eye_icon.png"))); // NOI18N
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

        pnlTable.setBorder(javax.swing.BorderFactory.createTitledBorder("Feature Table"));
        pnlTable.setLayout(new java.awt.BorderLayout());

        sbFeatures.setPreferredSize(new java.awt.Dimension(240, 135));

        tblFeatures.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        sbFeatures.setViewportView(tblFeatures);

        pnlTable.add(sbFeatures, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlTable);

        panelModule.setBorder(javax.swing.BorderFactory.createTitledBorder("Module"));

        javax.swing.GroupLayout panelModuleLayout = new javax.swing.GroupLayout(panelModule);
        panelModule.setLayout(panelModuleLayout);
        panelModuleLayout.setHorizontalGroup(
            panelModuleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 470, Short.MAX_VALUE)
        );
        panelModuleLayout.setVerticalGroup(
            panelModuleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 235, Short.MAX_VALUE)
        );

        getContentPane().add(panelModule);

        mainMenuFile.setText("File");

        fileMenuExit.setText("Exit");
        fileMenuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuExitActionPerformed(evt);
            }
        });
        mainMenuFile.add(fileMenuExit);

        mainMenu.add(mainMenuFile);

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
                JOptionPane.showMessageDialog(this, "App successfully loaded desired interface!", "Interface load", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "App detected syntax errors!", "Syntax errors", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "App cannot find desired interface,\ncheck paths again!", "Interface Load Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel defFtTblModel = (DefaultTableModel) tblFeatures.getModel();
        for (int i = tblFeatures.getRowCount() - 1; i >= 0; i--) {
            defFtTblModel.removeRow(i);
        }

        initIntEn();
    }//GEN-LAST:event_btnLoadActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnTblePreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTblePreviewActionPerformed
        // TODO add your handling code here:
        String resStr = (String) cmbBoxResolution.getSelectedItem();
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
            final DefaultTableModel ftTblMdl = new DefaultTableModel();
            ftTblMdl.addColumn("Feature Key");
            ftTblMdl.addColumn("Feature Value");
            SectionName sectionName = (SectionName) cmbBoxSection.getSelectedItem();
            Section section = intrface.getNameToSectionMap().get(sectionName);
            for (FeatureKey featKey : section.getKeys()) {
                FeatureValue featVal = resolutionPragma.getCustomFeatMap().get(featKey);
                if (featVal != null) {
                    Object[] row = {featKey.getStringValue(), featVal.getStringValue()};
                    ftTblMdl.addRow(row);
                }
            }

            tblFeatures.setModel(ftTblMdl);
        }

    }//GEN-LAST:event_btnTblePreviewActionPerformed

    private void btnBuildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuildActionPerformed
        // TODO add your handling code here:
        SectionName sectionName = (SectionName) cmbBoxSection.getSelectedItem();
        String resStr = (String) cmbBoxResolution.getSelectedItem();
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
            mdlAnim.state = ModuleAnimation.State.BUILD;
        }

    }//GEN-LAST:event_btnBuildActionPerformed

    private void fileMenuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuExitActionPerformed
        // TODO add your handling code here:
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_fileMenuExitActionPerformed

    private void infoMenuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoMenuAboutActionPerformed
        // TODO add your handling code here:
        URL icon_url = getClass().getResource(RESOURCES_DIR + LICENSE_LOGO_FILE_NAME);
        if (icon_url != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><b>VERSION v0.2 - CHINESE (PUBLIC BUILD reviewed on 2021-01-01 at 10:30).</b></html>\n");
            sb.append("<html><b>This software is free software, </b></html>\n");
            sb.append("<html><b>licensed under GNU General Public License (GPL).</b></html>\n");
            sb.append("\n");
            sb.append("Changelog for v0.2 CHINESE:\n");
            sb.append("\t- Initial pre-release.\n");
            sb.append("\n");
//            sb.append("Changelog since V1.1 GOTHS:\n");
//            sb.append("\t- Added preview of palette for FRMs.\n");
//            sb.append("\t- Changed default item colors.\n");
//            sb.append("\t- Changed description of step 1 in \"How to use\" [Randall].\n");
//            sb.append("\t- Fixed bad quality FRM images.\n");
//            sb.append("\t- Fixed missing custom color for resources.\n");
//            sb.append("\t- Fixed color not updating for buttons [Randall].\n");
//            sb.append("\n");
//            sb.append("Objective:\n");
//            sb.append("\tThe purpose of this program is\n");
//            sb.append("\tcolorizing onground and scenery items for FOnline series.\n");
//            sb.append("\n");
//            sb.append("\tDesignated to use primarily for FOnline 2 Season 3.\n");
            sb.append("\n");
            sb.append("<html><b>Copyright © 2021</b></html>\n");
            sb.append("<html><b>Alexander \"Ermac\" Stojanovich</b></html>\n");
            ImageIcon icon = new ImageIcon(icon_url);
            JOptionPane.showMessageDialog(this, sb.toString(), "About", JOptionPane.INFORMATION_MESSAGE, icon);
        }
    }//GEN-LAST:event_infoMenuAboutActionPerformed

    private void infoMenuHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoMenuHelpActionPerformed
        // TODO add your handling code here:
        URL icon_url = getClass().getResource(RESOURCES_DIR + LOGOX_FILE_NAME);
        if (icon_url != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><b>- FOR THE PURPOSE ABOUT THIS PROGRAM, </b></html>\n");
            sb.append("<html><b>check About. Make sure that you checked it first.</b></html>\n");
            sb.append("\n");
            sb.append("- Editing interface of several steps:\n");
            sb.append("\t1. Put (extract from archive if needed) interface to a single location.\n");
            sb.append("\t2. Choose input directory where \"art > intrface\" is,\n");
            sb.append("\t3. Choose output where modified result is gonna be stored,\n");
            sb.append("\t4. Click \"Load\" to load the interface from the input path,\n");
            sb.append("\t5. Click \"Save\" to save the interface on the output path,\n");
            sb.append("\n");
            sb.append("\t- Feel free to take a look at table preview on \"Preview Values.\"\n");
            sb.append("\t- Build module as an image with \"Build Module.\"\n");
            sb.append("\n");
            ImageIcon icon = new ImageIcon(icon_url);
            JOptionPane.showMessageDialog(this, sb.toString(), "How to use", JOptionPane.INFORMATION_MESSAGE, icon);
        }
    }//GEN-LAST:event_infoMenuHelpActionPerformed

    private void btnMdlePreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMdlePreviewActionPerformed
        // TODO add your handling code here:      
        GL_WINDOW.setTitle("Module preview");
        if (intrface.getResolutionPragma() != null) {
            int width = intrface.getResolutionPragma().getWidth();
            int height = intrface.getResolutionPragma().getHeight();
            GL_WINDOW.setSize(width, height);
        }
        GL_WINDOW.addGLEventListener(mdlAnim);
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
        JOptionPane.showMessageDialog(this, sb.toString(), "Interface status", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnCheckActionPerformed

    private void btnTogAllResActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTogAllResActionPerformed
        // TODO add your handling code here:
        cmbBoxResolution.setEnabled(!btnTogAllRes.isSelected());
    }//GEN-LAST:event_btnTogAllResActionPerformed

    private void fileInOpen() {
        int returnVal = fileChooserInput.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            cfg.setInDir(fileChooserInput.getSelectedFile());
            txtFldInPath.setText(cfg.getInDir().getPath());
            txtFldInPath.setToolTipText(cfg.getInDir().getPath());
        }

        if (!cfg.getInDir().getPath().isEmpty()) {
            btnLoad.setEnabled(true);
        }

        if (fileChooserInput.getSelectedFile() != null
                && fileChooserInput.getSelectedFile().equals(fileChooserOutput.getSelectedFile())) {
            JOptionPane.showMessageDialog(this, "Input and output path are the same,\nIt may lead to undesired results!", "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void fileOutOpen() {
        int returnVal = fileChooserOutput.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            cfg.setOutDir(fileChooserOutput.getSelectedFile());
            txtFldOutPath.setText(cfg.getOutDir().getPath());
            txtFldOutPath.setToolTipText(cfg.getOutDir().getPath());
        }

        if (!cfg.getOutDir().getPath().isEmpty()) {
            btnSave.setEnabled(true);
        }

        if (fileChooserOutput.getSelectedFile() != null
                && fileChooserOutput.getSelectedFile().equals(fileChooserInput.getSelectedFile())) {
            JOptionPane.showMessageDialog(this, "Input and output path are the same,\nIt may lead to undesired results!", "Warning", JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FO2IELogger.init(args.length > 0 && args[0].equals("-debug"));
        cfg.readConfigFile();
        Palette.load("Fallout Palette.act");

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

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI gui = new GUI();
                gui.setVisible(true);
                gui.pack();
                gui.canvAnim.start();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cfg.writeConfigFile();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuild;
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnChooseInPath;
    private javax.swing.JButton btnChoosePathOut;
    private javax.swing.JButton btnLoad;
    private javax.swing.JButton btnMdlePreview;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnTblePreview;
    private javax.swing.JToggleButton btnTogAllRes;
    private javax.swing.JComboBox<Object> cmbBoxResolution;
    private javax.swing.JComboBox<Section.SectionName> cmbBoxSection;
    private javax.swing.JFileChooser fileChooserInput;
    private javax.swing.JFileChooser fileChooserOutput;
    private javax.swing.JMenuItem fileMenuExit;
    private javax.swing.JMenuItem infoMenuAbout;
    private javax.swing.JMenuItem infoMenuHelp;
    private javax.swing.JLabel lblInput;
    private javax.swing.JLabel lblOutput;
    private javax.swing.JLabel lblResolution;
    private javax.swing.JLabel lblSection;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JMenu mainMenuFile;
    private javax.swing.JMenu mainMenuInfo;
    private javax.swing.JPanel panelModule;
    private javax.swing.JPanel pnlFilePaths;
    private javax.swing.JPanel pnlIntrface;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JScrollPane sbFeatures;
    private javax.swing.JTable tblFeatures;
    private javax.swing.JTextField txtFldInPath;
    private javax.swing.JTextField txtFldOutPath;
    // End of variables declaration//GEN-END:variables
}
