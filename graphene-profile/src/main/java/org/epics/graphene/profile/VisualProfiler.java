/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene.profile;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


/**
 * Constructs a graphical-user-interface to profile and test the efficiency
 * of <code>Graph2DRenderer</code> subclasses.
 * <p>
 * Provides operations such as:
 * <ul>
 *      <li>Change output settings</li>
 *      <li>Change profile parameters</li>
 *      <li>Change renderer to profile</li>
 *      <li>Provides 1D Table and 2D Table output types</li>
 *      <li>Output analysis</li>
 *      <li>File browsing of output files</li>
 * </ul>
 * 
 * @author asbarber
 */
public class VisualProfiler extends JFrame{
    /**
     * Package location for <code>ProfileGraph2D</code> subclasses.
     */
    private static final String PROFILE_PATH = "org.epics.graphene.profile";
    
    /**
     * Java class names of all <code>ProfileGraph2D</code> subclasses.
     */
    public static final String[] SUPPORTED_PROFILERS = {"Histogram1D",
                                                        "IntensityGraph2D",
                                                        "LineGraph2D",
                                                        "ScatterGraph2D",
                                                        "SparklineGraph2D"
                                                       };
    
    private SwingWorker thread;
    
    private JPanel mainPanel;
    private JTabbedPane tabs;
    
    //Pane: General SaveSettings
    private JComboBox           listRendererTypes;
    private JLabel              lblRendererTypes;
    
    private JTextField          txtTestTime;
    private JLabel              lblTestTime;
    
    private JTextField          txtMaxAttempts;
    private JLabel              lblMaxAttempts;
    
    private JComboBox           listTimeTypes;
    private JLabel              lblTimeTypes;
    
    private JComboBox           listUpdateTypes;
    private JLabel              lblUpdateTypes;
    
    private JLabel              lblSaveMessage;
    private JTextField          txtSaveMessage;
    
    private JLabel              lblAuthorMessage;
    private JTextField          txtAuthorMessage;
    
    
    //Tab: Single Profile
    private JLabel              lblDatasetSize;
    private JTextField          txtDatasetSize;
    
    private JLabel              lblImageWidth;
    private JTextField          txtImageWidth;
    
    private JLabel              lblImageHeight;
    private JTextField          txtImageHeight;
    
    private JLabel              lblShowGraph;
    private JCheckBox           chkShowGraph;
    
    private JButton             btnSingleProfile;
    private JButton             btnSingleProfileAll;
    
    
    //Tab: Control Panel
    private JButton             btnCompareTables;
    private JButton             btnCompareTables1D;
    
    
    //Tab: Multi Layer
    private JLabel              lblResolutions,
                                lblNPoints;
    
    private JButton             btnStart;
    
    private JList<Resolution>               listResolutions;
    private JList<Integer>                  listNPoints;
    private DefaultListModel<Resolution>    modelResolutions;
    private DefaultListModel<Integer>       modelNPoints;
    
    
    //Tab: File Viewer
    private JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode treeRoot;
    private JButton btnTreeOpenFile;
    private JButton btnTreeDeleteFile;
    private JButton btnTreeRefresh;
    
    
    //Pane: Console
    private JTextArea          console;
    private JLabel             lblConsole;
    private JButton            btnClearLog;
    private JButton            btnSaveLog;
    
    private JLabel             lblTime;
    private JTextField         txtTime;
    
    private JButton            btnCancelThread;
    
    
    /**
     * Constructs a graphical-user-interface to profile and test the efficiency
     * of <code>Graph2DRenderer</code> subclasses.
     * <p>
     * The following actions are performed:
     * <ol>
     *      <li>Login initializer</li>
     *      <li>Finalizes and shows the frame</code>
     *      <li>Starts the timer</li>
     * </ol>
     */
    public VisualProfiler(){
        super("Visual Profiler");
                
        initFrame();
        initComponents();
        initMnemonics();
        loadLists();
        addListeners();        
        addComponents();
                
        login();
        
        finalizeFrame();
        
        startTimer();        
    }
    
    
    //Swing Setup
    
    /**
     * Initializes frame properties.
     */
    private void initFrame(){
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * Initializes all graphical user interface components.
     */
    private void initComponents(){
        mainPanel = new JPanel();        
        mainPanel.setLayout(new BorderLayout());
        tabs = new JTabbedPane();
        
        //General SaveSettings
        listRendererTypes = new JComboBox(VisualProfiler.SUPPORTED_PROFILERS);
        lblRendererTypes = new JLabel("Renderer Type: ");
        
        txtTestTime = new JTextField("20");
        lblTestTime = new JLabel("Test Time: ");
                
        txtMaxAttempts = new JTextField("1000000");
        lblMaxAttempts = new JLabel("Max Attempts: ");
        
        listTimeTypes = new JComboBox(StopWatch.TimeType.values());
        lblTimeTypes = new JLabel("Timing Based Off: ");
        
        listUpdateTypes = new JComboBox();
        lblUpdateTypes = new JLabel("Apply Update: ");
        this.updateUpdateVariations();
        
        lblSaveMessage = new JLabel("Save Message: ");
        txtSaveMessage = new JTextField("");
        
        lblAuthorMessage = new JLabel("Author: ");
        txtAuthorMessage = new JTextField("");
        
        
        //Tab: Single Profile
        //------------
        lblDatasetSize = new JLabel("Number of Data Points: ");
        lblDatasetSize.setToolTipText("Format for IntensityGraph2D: 1000x1000");        
        txtDatasetSize = new JTextField("10000");
        
        lblImageWidth = new JLabel("Image Width: ");
        txtImageWidth = new JTextField("640");
        
        lblImageHeight = new JLabel("Image Height: ");
        txtImageHeight = new JTextField("480");
        
        lblShowGraph = new JLabel("Graph Results: ");
        chkShowGraph = new JCheckBox("Show Graph");
        
        btnSingleProfile = new JButton("Profile");
        btnSingleProfileAll = new JButton("Profile For All Renderers");
        
        
        //Tab: Control Panel
        //------------
        btnCompareTables = new JButton("Compare Profile Tables");
        btnCompareTables1D = new JButton("Analyze Single Profile Tables");
        
        
        //Tab: Multi Layer
        //------------
        lblResolutions = new JLabel("Resolutions");
        lblNPoints = new JLabel("N Points");
        btnStart = new JButton("Start");
        
        listResolutions = new JList<>();
        listNPoints = new JList<>();
        
        
        //Tab: File Browser
        //--------
        treeRoot = new DefaultMutableTreeNode(new File(ProfileGraph2D.LOG_FILEPATH));
        treeModel = new DefaultTreeModel(treeRoot);
        tree = new JTree(treeRoot){
            @Override
            public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus){  
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                try{
                    File f = (File) node.getUserObject();
                    return f.getName();
                }
                catch(Exception e){
                    return node.toString();
                }
            }
        };        
        tree.setModel(treeModel);
        this.addNodes(treeRoot);
        tree.expandRow(0);
        btnTreeOpenFile = new JButton("Open File(s)");
        btnTreeDeleteFile = new JButton("Delete File(s)");
        btnTreeRefresh = new JButton("Refresh");
        
        //Console
        console = new JTextArea(20, 50);
        console.setEditable(false);
        lblConsole = new JLabel("Console");
        btnSaveLog = new JButton("Save Log");
        btnClearLog = new JButton("Clear Log");
        lblTime = new JLabel("Timer:");
        txtTime = new JTextField("00:00:00");
        txtTime.setEditable(false);
        btnCancelThread = new JButton("Cancel");
        btnCancelThread.setEnabled(false);
    }
    
    /**
     * Initializes the mnemonic (hotkeys) for all button components.
     */
    private void initMnemonics(){
       this.btnSingleProfile.setMnemonic('P');
       this.btnSingleProfileAll.setMnemonic('A');
       
       this.btnStart.setMnemonic('S');
       
       //this.btnCompareTables.setMnemonic('');
       //this.btnCompareTables1D.setMnemonic('');
       
       this.btnTreeOpenFile.setMnemonic('O');
       this.btnTreeDeleteFile.setMnemonic('D');
       this.btnTreeRefresh.setMnemonic('R');
       
       this.btnSaveLog.setMnemonic('L');
       this.btnClearLog.setMnemonic('C');
       this.btnCancelThread.setMnemonic('T');
       
    }
    
    /**
     * Loads the lists for <code>Resolution</code>s and the
     * dataset sizes for the <code>MultiLevelProfiler</code>.
     */
    private void loadLists(){
        modelResolutions = new DefaultListModel<>();
        modelNPoints = new DefaultListModel<>();
        
        for (Resolution resolution : Resolution.defaultResolutions()){
            modelResolutions.addElement(resolution);
        }
        
        for (Integer datasetSize : MultiLevelProfiler.defaultDatasetSizes()){
            modelNPoints.addElement(datasetSize);
        }
                
        listResolutions.setModel(modelResolutions);
        listNPoints.setModel(modelNPoints);
    }
    
    /**
     * Adds action listeners to each button: associates each button
     * with a method of the <code>VisualProfiler</code>.
     */
    private void addListeners(){
       this.listRendererTypes.addItemListener(new ItemListener(){

           @Override
           public void itemStateChanged(ItemEvent e) {
               if (e.getStateChange() == ItemEvent.SELECTED){
                   VisualProfiler.this.updateUpdateVariations();
               }
           }
           
       });
       
       this.btnSingleProfile.addActionListener(new ActionListener(){

           @Override
           public void actionPerformed(ActionEvent e) {
               VisualProfiler.this.singleProfileAction();
           }
           
       });
       this.btnSingleProfileAll.addActionListener(new ActionListener(){

           @Override
           public void actionPerformed(ActionEvent e) {
               VisualProfiler.this.singleProfileActionAll();
           }
           
       });
       
       this.btnStart.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                VisualProfiler.this.startAction();
            }
            
        });
       
       this.btnCompareTables.addActionListener(new ActionListener(){

           @Override
           public void actionPerformed(ActionEvent e) {
               VisualProfiler.this.compareTablesAction();
           }
           
       });
       this.btnCompareTables1D.addActionListener(new ActionListener(){

           @Override
           public void actionPerformed(ActionEvent e) {
               VisualProfiler.this.analyzeTables1DAction();
           }
           
       });
       
       this.btnTreeOpenFile.addActionListener(new ActionListener(){

           @Override
           public void actionPerformed(ActionEvent e) {
               VisualProfiler.this.openFiles();
           }
           
       });
       this.btnTreeDeleteFile.addActionListener(new ActionListener(){

           @Override
           public void actionPerformed(ActionEvent e) {
               VisualProfiler.this.deleteFiles();
           }
           
       });
       this.btnTreeRefresh.addActionListener(new ActionListener(){

           @Override
           public void actionPerformed(ActionEvent e) {
               VisualProfiler.this.refresh();
           }
           
       });
       
       this.btnSaveLog.addActionListener(new ActionListener(){

           @Override
           public void actionPerformed(ActionEvent e) {
               VisualProfiler.this.saveLog();
           }
           
       });
       this.btnClearLog.addActionListener(new ActionListener(){

           @Override
           public void actionPerformed(ActionEvent e) {
               VisualProfiler.this.clearLog();
           }
           
       });
       this.btnCancelThread.addActionListener(new ActionListener(){

           @Override
           public void actionPerformed(ActionEvent e) {
               VisualProfiler.this.cancelThread();
           }
           
       });
       
       this.tabs.addChangeListener(new ChangeListener(){

           @Override
           public void stateChanged(ChangeEvent e) {
               VisualProfiler.this.refresh(true);
           }
           
       });
    }    
    
    /**
     * Adds all graphical user interface components to the <code>JFrame</code>.
     */
    private void addComponents(){
        
        //General SaveSettings
        JPanel settingsPane = new JPanel();
        settingsPane.setLayout(new GridLayout(0, 2));
        
            settingsPane.add(this.lblRendererTypes);
            settingsPane.add(this.listRendererTypes);

            settingsPane.add(this.lblTestTime);
            settingsPane.add(this.txtTestTime);

            settingsPane.add(this.lblMaxAttempts);
            settingsPane.add(this.txtMaxAttempts);
            
            settingsPane.add(this.lblTimeTypes);
            settingsPane.add(this.listTimeTypes);            
            
            settingsPane.add(this.lblUpdateTypes);
            settingsPane.add(this.listUpdateTypes);
            
            settingsPane.add(lblSaveMessage);
            settingsPane.add(txtSaveMessage);
            
            settingsPane.add(lblAuthorMessage);
            settingsPane.add(txtAuthorMessage);            
            
        //Tab: Single Profile
        JPanel singleProfileTab = new JPanel();
        singleProfileTab.setLayout(new GridLayout(0, 2));
        
            singleProfileTab.add(lblDatasetSize);
            singleProfileTab.add(txtDatasetSize);

            singleProfileTab.add(lblImageWidth);
            singleProfileTab.add(txtImageWidth);

            singleProfileTab.add(lblImageHeight);
            singleProfileTab.add(txtImageHeight);

            singleProfileTab.add(lblShowGraph);
            singleProfileTab.add(chkShowGraph);
            
            singleProfileTab.add(blankPanel(btnSingleProfile));
            singleProfileTab.add(blankPanel(btnSingleProfileAll));
        
            
        //Tab: Control Panel
        JPanel controlPane = new JPanel();        
            controlPane.add(this.btnCompareTables);
            controlPane.add(this.btnCompareTables1D);
        
        
        //Tab: Multi Layer
                JPanel multiLayerLeft = new JPanel();
                multiLayerLeft.setLayout(new BorderLayout());
                multiLayerLeft.add(lblResolutions, BorderLayout.NORTH);
                multiLayerLeft.add(new JScrollPane(listResolutions), BorderLayout.CENTER);

                JPanel multiLayerMiddle = new JPanel();
                multiLayerMiddle.setLayout(new BorderLayout());
                multiLayerMiddle.add(lblNPoints, BorderLayout.NORTH);
                multiLayerMiddle.add(new JScrollPane(listNPoints), BorderLayout.CENTER);

                JPanel multiLayerRight = new JPanel();
                multiLayerRight.setLayout(new BorderLayout());        
                multiLayerRight.add(blankPanel(btnStart), BorderLayout.NORTH);
        
            final JSplitPane multiLayerInner = new JSplitPane();
            final JSplitPane multiLayerOuter = new JSplitPane();

            multiLayerInner.setLeftComponent(multiLayerLeft);
            multiLayerInner.setRightComponent(multiLayerMiddle);

            multiLayerOuter.setLeftComponent(multiLayerInner);
            multiLayerOuter.setRightComponent(multiLayerRight);
            
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    multiLayerOuter.setDividerLocation(0.8);
                    multiLayerInner.setDividerLocation(0.8);                                        
                }
                
            });            
        
            
        //Tab: File Browser
        JSplitPane fileTab = new JSplitPane();
        
            JPanel fileTabRight = new JPanel();
            fileTabRight.setLayout(new BoxLayout(fileTabRight, BoxLayout.Y_AXIS));
            fileTabRight.add(this.btnTreeOpenFile);
            fileTabRight.add(this.btnTreeDeleteFile);
            fileTabRight.add(this.btnTreeRefresh);
            
        fileTab.setLeftComponent(new JScrollPane(tree));
        fileTab.setRightComponent(fileTabRight);
           
        
        //Console
        JPanel consolePanel = new JPanel();
        consolePanel.setLayout(new BorderLayout());
        consolePanel.setBorder(BorderFactory.createLineBorder(Color.black));   
        
            JPanel consoleBottom = new JPanel();
            consoleBottom.setLayout(new GridLayout(3, 2));
                consoleBottom.add(blankPanel(this.btnSaveLog));
                consoleBottom.add(blankPanel(this.btnClearLog));
            
                consoleBottom.add(blankPanel(this.lblTime));
                consoleBottom.add(blankPanel(this.txtTime));
                
                consoleBottom.add(blankPanel(this.btnCancelThread));
            
            consolePanel.add(lblConsole, BorderLayout.NORTH);
            consolePanel.add(new JScrollPane(console), BorderLayout.CENTER);
            consolePanel.add(consoleBottom, BorderLayout.SOUTH);
            
        
        //Tabs
        tabs.addTab("Single Profile", singleProfileTab);
        tabs.addTab("Multi Layer", multiLayerOuter);
        tabs.addTab("Control Panel", controlPane);
        tabs.addTab("File Browser", fileTab);
        
        
        //Add to panel hiearchy
        mainPanel.add(settingsPane, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);
        mainPanel.add(consolePanel, BorderLayout.SOUTH);
                
        super.add(mainPanel);
    }
    
    /**
     * Finalizes frame properties (packs and shows frame).
     */
    private void finalizeFrame(){
        super.setVisible(true);
        super.pack();
        super.setLocationRelativeTo(null);  //Centers
    }
    
    /**
     * Simple login feature that ensures all output files generated
     * by profiling is associated with an author.
     * <p>
     * Provides the option to exit the application if the user so chooses.
     */
    private void login(){
        String input = null;
        boolean exit = false;
        final String cancelMessage = "Do you want to exit the application?";
        
        //Validate
        while (!exit){
            input = JOptionPane.showInputDialog(null, "Username: ", "Login",
                                                JOptionPane.PLAIN_MESSAGE);
            
            //Warning
            if (input == null || input.equals("")){
                int cancel = JOptionPane.showConfirmDialog(null, cancelMessage,
                                                           "Cancel",
                                                           JOptionPane.YES_NO_OPTION);
                
                //User wants to close program
                if (cancel == JOptionPane.YES_OPTION){
                    exit = true;
                    this.processWindowEvent(new WindowEvent(
                                            this, WindowEvent.WINDOW_CLOSING));
                }
            }
            //Valid, exits looop
            else{
                exit = true;
            }
        }
        
        this.txtAuthorMessage.setText(input);
    }
    
    
    //Actions
    
    private void updateUpdateVariations(){
        DefaultComboBoxModel model = new DefaultComboBoxModel(
            getProfilerType().getVariations().keySet().toArray()
        );
        this.listUpdateTypes.setModel(model);        
    }
    
    /**
     * Thread safe operation to start a <code>ProfileGraph2D</code>
     * for the renderer selected from the graphical user interface.
     * Uses the given settings taken from the graphical user interface
     * and saves to the specified output file.
     */    
    private void singleProfileAction(){
        String strDatasetSize = txtDatasetSize.getText();
        String strImageWidth = txtImageWidth.getText();
        String strImageHeight = txtImageHeight.getText();
        String strAuthor = this.txtAuthorMessage.getText();
        
        int datasetSize = -1;
        int xSize = -1,
            ySize = -1;
        int imageWidth;
        int imageHeight;
        String saveMessage = this.txtSaveMessage.getText();
        final boolean showGraphs = this.chkShowGraph.isSelected();
        final ProfileGraph2D profiler = getProfiler();
        
        //Invalid Profiler
        if (profiler == null){
            return;
        }
        
        
        //Datset Size
            //Checks if 2D in form of NUMBERxNUMBER
            if (strDatasetSize.toLowerCase().contains("x")){
                try{
                    //Index of "x"
                    int splitIndex = strDatasetSize.toLowerCase().indexOf("x");

                    //Before the "x"
                    xSize = Integer.parseInt(strDatasetSize.substring(0, splitIndex));

                    //After the "x"
                    ySize = Integer.parseInt(strDatasetSize.substring(splitIndex+1));

                    //Validates
                    if (xSize <= 0 || ySize <= 0){
                        throw new NumberFormatException();
                    }
                }
                catch (NumberFormatException e){
                    String msg = "Enter positive non-zero integers separated by an \"x\", ie- 1000x1000";
                    JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
                    return;            
                }                      
            }
            //1D data
            else{
                try{
                    datasetSize = Integer.parseInt(strDatasetSize);

                    //Validates
                    if (datasetSize <= 0){
                        throw new NumberFormatException();
                    }
                }
                catch (NumberFormatException e){
                    JOptionPane.showMessageDialog(null, "Enter a positive non-zero integer for the dataset size.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;            
                }                
            }

        
        //Image Width
        try{
            imageWidth = Integer.parseInt(strImageWidth);
            
            if (imageWidth <= 0){
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Enter a positive non-zero integer for the image width.", "Error", JOptionPane.ERROR_MESSAGE);
            return;            
        }        
        
        //Image Height
        try{
            imageHeight = Integer.parseInt(strImageHeight);
            
            if (imageHeight <= 0){
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Enter a positive non-zero integer for the image height.", "Error", JOptionPane.ERROR_MESSAGE);
            return;            
        }   
        
        //Applies setting changes        
        if ((profiler instanceof ProfileIntensityGraph2D) && xSize > 0 && ySize > 0){
            ((ProfileIntensityGraph2D) profiler).setNumXDataPoints(xSize);
            ((ProfileIntensityGraph2D) profiler).setNumYDataPoints(ySize);
        }
        else{
            profiler.setNumDataPoints(datasetSize);        
        }
        profiler.setImageWidth(imageWidth);
        profiler.setImageHeight(imageHeight);
        profiler.getSaveSettings().setSaveMessage(saveMessage);
        profiler.getSaveSettings().setAuthorMessage(strAuthor);
        
        SwingWorker worker = new SwingWorker<Object, String>(){            
            @Override
            protected Object doInBackground() throws Exception {
                setEnabledActions(false);
                threadStarted(this);
                
                ///Begin message
                publish("--------\n");
                publish(profiler.getGraphTitle() + ": Single Profile\n\n");
                
                //Runs
                publish("Running...\n");
                profiler.profile();
                publish("Running finished.\n");
                
                //Saves
                if (!Thread.currentThread().isInterrupted()){
                    publish("Saving...\n");
                    profiler.saveStatistics();
                    publish("Saving finished.\n");


                    //Displays results graph if checked
                    if (showGraphs){
                        publish("\nGraphing Results...\n");
                        profiler.graphStatistics();
                        publish("Graphing Complete.\n");
                    }
                    
                    //Finish message
                    publish("\nProfiling completed.\n");
                    publish("--------\n");                    
                }else{
                    //Finish message
                    publish("\nProfiling cancelled.\n");
                    publish("--------\n");                        
                }
                
                setEnabledActions(true);
                threadFinished();
                
                return null;
            }
            
            
            @Override
            protected void process(List<String> chunks){
                for (String chunk: chunks){
                    VisualProfiler.this.print(chunk);
                }
            }            
        };
        worker.execute();        
    }
    
    /**
     * Thread safe operation to start a <code>ProfileGraph2D</code>
     * for every supported renderer.
     * Uses the given settings taken from the graphical user interface
     * and saves to the specified output file.
     */
    private void singleProfileActionAll(){
        //Get inputs
        String strTestTime = this.txtTestTime.getText();
        String strMaxAttempts = this.txtMaxAttempts.getText();
        
        //Intended variables
        int testTime;
        int maxAttempts;
                
        //Test Time
        try{
            testTime = Integer.parseInt(strTestTime);
            
            if (testTime <= 0){
                throw new NumberFormatException();
            }
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Enter a positive non-zero integer for test time.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        //Test Time
        try{
            maxAttempts = Integer.parseInt(strMaxAttempts);
            
            if (maxAttempts <= 0){
                throw new NumberFormatException();
            }
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Enter a positive non-zero integer for max attempts.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        String strDatasetSize = txtDatasetSize.getText();
        String strImageWidth = txtImageWidth.getText();
        String strImageHeight = txtImageHeight.getText();
        
        int datasetSize;
        int imageWidth;
        int imageHeight;
        String saveMessage = this.txtSaveMessage.getText();
        final boolean showGraphs = this.chkShowGraph.isSelected();
        
        //Datset Size
        try{
            datasetSize = Integer.parseInt(strDatasetSize);
            
            if (datasetSize <= 0){
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Enter a positive non-zero integer for the dataset size.", "Error", JOptionPane.ERROR_MESSAGE);
            return;            
        }
        
        //Image Width
        try{
            imageWidth = Integer.parseInt(strImageWidth);
            
            if (imageWidth <= 0){
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Enter a positive non-zero integer for the image width.", "Error", JOptionPane.ERROR_MESSAGE);
            return;            
        }        
        
        //Image Height
        try{
            imageHeight = Integer.parseInt(strImageHeight);
            
            if (imageHeight <= 0){
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Enter a positive non-zero integer for the image height.", "Error", JOptionPane.ERROR_MESSAGE);
            return;            
        }   
        
        
        //Profile Creation
        final List<ProfileGraph2D> profilers = new ArrayList<>();
        
        
        for (int i = 0; i < VisualProfiler.SUPPORTED_PROFILERS.length; i++){
            
            //Instance creation                
            try {
                Class profileClass = Class.forName(PROFILE_PATH + ".Profile" + VisualProfiler.SUPPORTED_PROFILERS[i]);
                profilers.add((ProfileGraph2D) profileClass.newInstance());
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "This class is not currently accessible.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (InstantiationException ex) {
                return;
            } catch (IllegalAccessException ex) {
                return;
            }
            
            //Update
            profilers.get(profilers.size()-1).setTestTime(testTime);
            profilers.get(profilers.size()-1).setMaxTries(maxAttempts);   
            profilers.get(profilers.size()-1).setNumDataPoints(datasetSize);
            profilers.get(profilers.size()-1).setImageWidth(imageWidth);
            profilers.get(profilers.size()-1).setImageHeight(imageHeight);
            profilers.get(profilers.size()-1).getSaveSettings().setSaveMessage(saveMessage);            

        }

        SwingWorker worker = new SwingWorker<Object, String>(){

            @Override
            protected Object doInBackground() throws Exception {
                setEnabledActions(false);
                threadStarted(this);
                
                for (final ProfileGraph2D profiler: profilers){
                    if (Thread.currentThread().isInterrupted()){
                        break;  //quits loop if interrupted
                    }
                    
                    ///Begin message
                    publish("--------\n");
                    publish(profiler.getGraphTitle() + ": Single Profile\n\n");

                    //Runs
                    publish("Running...\n");
                    profiler.profile();
                    publish("Running finished.\n");

                    if (!Thread.currentThread().isInterrupted()){
                        //Saves
                        publish("Saving...\n");
                        profiler.saveStatistics();
                        publish("Saving finished.\n");

                        //Displays results graph if checked
                        if (showGraphs){
                            publish("\nGraphing Results...\n");
                            profiler.graphStatistics();
                            publish("Graphing Complete.\n");
                        }
 
                        //Finish message
                        publish("\nProfiling completed.\n");
                        publish("--------\n");                        
                    }else{
                        //Finish message
                        publish("\nProfiling cancelled.\n");
                        publish("--------\n");                             
                    }
                    

                }
                
                setEnabledActions(true);
                threadFinished();
                return null;
            }


            @Override
            protected void process(List<String> chunks){
                for (String chunk: chunks){
                    VisualProfiler.this.print(chunk);
                }
            }            
        };
        worker.execute();   
        
    }
    
    /**
     * Thread safe operation to start a <code>MultiLevelProfiler</code>
     * profile and saves the results to an output file.
     */
    private void startAction(){
        List<Resolution> resolutions = listResolutions.getSelectedValuesList();
        List<Integer> datasetSizes = listNPoints.getSelectedValuesList();
        ProfileGraph2D profiler = this.getProfiler();

        if (!resolutions.isEmpty() && !datasetSizes.isEmpty() && getProfiler() != null){
            ProfilerWorker worker = new ProfilerWorker(profiler, resolutions, datasetSizes);
            worker.execute();            
        }    
        else{
            JOptionPane.showMessageDialog(null, "Profiling was cancelled due to invalid settings.", "Run Fail", JOptionPane.ERROR_MESSAGE);
        }   
    }
    
    /**
     * Thread safe operation to analyze all 1D tables
     * (<code>MultiLevelProfiler</code> output, not <code>ProfileGraph2D</code> output)
     * and saves the results to an output file.
     */
    private void compareTablesAction(){
        SwingWorker worker = new SwingWorker<Object, String>(){

            @Override
            protected Object doInBackground() throws Exception {
                setEnabledActions(false);     
                threadStarted(this);
                publish("--------\n");
                publish("Compare Tables\n");
                ProfileAnalysis.compareTables2D();   
                publish("\nComparison completed.\n");
                publish("--------\n");
                setEnabledActions(true); 
                threadFinished();
                return null;
            }
            
            @Override
            protected void process(List<String> chunks){
                for (String chunk: chunks){
                    VisualProfiler.this.print(chunk);
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Thread safe operation to analyze all 1D tables
     * (<code>ProfileGraph2D</code> output, not <code>MultiLevelProfiler</code> output)
     * and print the results (performance increase/decrease) to the
     * graphical user interface console.
     */
    private void analyzeTables1DAction(){
        SwingWorker worker = new SwingWorker<Object, String>(){

            @Override
            protected Object doInBackground() throws Exception {
                setEnabledActions(false);        
                threadStarted(this);
                publish("--------\n");
                publish("Comparing Single Profile Tables\n\n");
                
                List<String> output = ProfileAnalysis.analyzeTables1D();
                for (String out: output){
                    publish(out + "\n");
                }
                
                publish("\nComparison completed.\n");
                publish("--------\n");
                setEnabledActions(true);     
                threadFinished();
                return null;
            }
            
            @Override
            protected void process(List<String> chunks){
                for (String chunk: chunks){
                    VisualProfiler.this.print(chunk);
                }
            }
        };
        worker.execute();  
    }
    
    /**
     * Thread safe operation to open all selected files from the 
     * file tree with the default application to open the files.
     */
    private void openFiles(){
        SwingWorker worker = new SwingWorker<Object, String>(){

            @Override
            protected Object doInBackground() throws Exception {
                setEnabledActions(false);     
                threadStarted(this);
                publish("--------\n");
                publish("Opening Files\n\n");
                
                Desktop desktop = Desktop.getDesktop();
                TreePath[] paths = tree.getSelectionPaths();

                if (paths != null && desktop != null){
                    for (TreePath path: paths){
                        if (Thread.currentThread().isInterrupted()){
                            break;
                        }
                        
                        if (path != null){
                            try{
                                File toOpen = (File) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                                desktop.open(toOpen);
                                
                                publish(toOpen.getName() + "...opened successfully!\n");
                            }
                            catch(IOException e){
                                publish("Unable to open: " +
                                        ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() +
                                        "\n"
                                       );
                            }
                            catch(ClassCastException e){
                                //unable to open
                            }
                        }
                    }
                }
                
                if (Thread.currentThread().isInterrupted()){
                    publish("\nFile operations completed.\n");
                    publish("--------\n");
                }
                else{
                    publish("\nFile operations cancelled.\n");
                    publish("--------\n");                    
                }
                
                setEnabledActions(true);    
                threadFinished();
                return null;
                
            }
            
            @Override
            protected void process(List<String> chunks){
                for (String chunk: chunks){
                    VisualProfiler.this.print(chunk);
                }
            }
        };
        worker.execute();   
    }
    
    /**
     * Thread safe operation to delete all selected files from the
     * file tree.
     */
    private void deleteFiles(){
        SwingWorker worker = new SwingWorker<Object, String>(){

            @Override
            protected Object doInBackground() throws Exception {
                setEnabledActions(false);    
                threadStarted(this); 
                
                publish("--------\n");
                publish("Opening Files\n\n");
                
                TreePath[] paths = tree.getSelectionPaths();

                if (paths != null){
                    for (TreePath path: paths){
                        if (Thread.currentThread().isInterrupted()){
                            break;  //quits loop
                        }
                        
                        if (path != null){
                            try{
                                File toDelete = (File) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                                
                                Files.delete(toDelete.toPath());
                                publish(toDelete.getName() + "...deleted successfully!\n");
                            }
                            catch(IOException e){
                                publish("Unable to delete: " +
                                        ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() +
                                        "\n"
                                       );
                            }
                            catch(ClassCastException e){
                                //unable to open
                            }
                        }
                    }
                }
                
                VisualProfiler.this.refreshNodes();
                
                if (!Thread.currentThread().isInterrupted()){
                    publish("\nFile operations completed.\n");
                    publish("--------\n");
                }
                else{
                    publish("\nFile operations cancelled.\n");
                    publish("--------\n");                    
                }
                
                setEnabledActions(true);  
                threadFinished();
                return null;
            }
            
            @Override
            protected void process(List<String> chunks){
                for (String chunk: chunks){
                    VisualProfiler.this.print(chunk);
                }
            }
        };
        worker.execute();     
    }
    
    /**
     * Thread safe operation to refresh nodes of the file tree.
     * Prints a 'refresh' message to the console log.
     */
    private void refresh(){
        this.refresh(false);
    }
    
    /**
     * Thread safe operation to refresh nodes of the file tree.
     * @param silent true to not print a 'refresh' message to the console log,
     *               false to print a 'refresh' message to the console log
     */
    private void refresh(final boolean silent){
        SwingWorker worker = new SwingWorker<Object, String>(){

            @Override
            protected Object doInBackground() throws Exception {
                if (!silent){
                    setEnabledActions(false);     
                    threadStarted(this);
                    publish("--------\n");
                    publish("Refreshing File Browser\n");
                }
                
                VisualProfiler.this.refreshNodes();
                
                if (!silent){
                    publish("Refresh completed.\n");
                    publish("--------\n");
                    setEnabledActions(true);     
                    threadFinished();
                }

                return null;                
            }
            
            @Override
            protected void process(List<String> chunks){
                for (String chunk: chunks){
                    VisualProfiler.this.print(chunk);
                }
            }
        };
        worker.execute();  
    }    
    
    /**
     * Thread safe operation to save the console log of the graphical
     * user interface.
     * The log is saved as a <b>.txt</b> file with the current timestamp.
     */
    private void saveLog(){
        SwingWorker worker = new SwingWorker<Object, String>(){

            @Override
            protected Object doInBackground() throws Exception {
                setEnabledActions(false);                
                threadStarted(this);
                
                //Where saving occurs
                saveFile();
                
                publish("--------\n");
                publish("Saving Log\n\n");
                
                //Saves beforehand to prevent this from being in log
                
                publish("\nSaving completed.\n");
                publish("--------\n");
                setEnabledActions(true);  
                threadFinished();
                return null;
            }
            
            private void saveFile(){
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String date = format.format(new Date());         
                
                //Creates file
                File outputFile = new File(ProfileGraph2D.LOG_FILEPATH + 
                                  date + 
                                  "-Log.txt");
                
                try {
                    outputFile.createNewFile();

                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));

                    //Prints console
                    out.print(VisualProfiler.this.console.getText());

                    out.close();
                } catch (IOException ex) {
                    System.err.println("Output errors exist.");
                }                
            }
            
            @Override
            protected void process(List<String> chunks){
                for (String chunk: chunks){
                    VisualProfiler.this.print(chunk);
                }
            }
        };
        worker.execute();           
    }
    
    /**
     * Empties the console log of the graphical user interface.
     */
    private void clearLog(){
        console.setText("");
    }
    
    //TODO: fix these methods
    private void threadStarted(SwingWorker worker){
        if (worker == null){
            throw new IllegalArgumentException("Must have a non-null thread.");
        }
        
        this.thread = worker;
        this.btnCancelThread.setEnabled(true);
    }
    private void threadFinished(){
        this.thread = null;
        this.btnCancelThread.setEnabled(false);
        

        
        setEnabledActions(true);
    }
    private void cancelThread(){
        if (this.thread != null){
            thread.cancel(true);
            this.btnCancelThread.setEnabled(false);
            
            SwingWorker worker = new SwingWorker(){

                @Override
                protected Object doInBackground() throws Exception {
                    publish("\nAction Cancelled\n");                                
                    publish("--------\n");
                    return null;
                }

            };
            worker.execute();            
        }
    }
    
    /**
     * Enables/disables all button components of the graphical user interface.
     * @param enabled true to enable all buttons,
     *                false to disable all buttons
     */
    private void setEnabledActions(boolean enabled){
        this.btnCompareTables.setEnabled(enabled);
        this.btnCompareTables1D.setEnabled(enabled);
        this.btnSingleProfile.setEnabled(enabled);
        this.btnSingleProfileAll.setEnabled(enabled);
        this.btnStart.setEnabled(enabled);
        this.btnTreeOpenFile.setEnabled(enabled);
        this.btnTreeDeleteFile.setEnabled(enabled);
        this.btnTreeRefresh.setEnabled(enabled);
        this.btnSaveLog.setEnabled(enabled);
        this.btnClearLog.setEnabled(enabled);
    }

    /**
     * Starts a thread-safe timing mechanism to display
     * the current time in real-time to the graphical
     * user interface.
     */
    private void startTimer(){
        SwingWorker worker = new SwingWorker<Object, String>(){

            @Override
            protected Object doInBackground() throws Exception {
                Timer t = new Timer();
                t.scheduleAtFixedRate(new TimerTask(){

                        @Override
                        public void run() {         
                            publish(getTime());
                        }
                                      
                    }
                    
                    , 1000, 1000
                );
                return null;
            }

            @Override
            protected void process(List<String> chunks){
                for (String chunk: chunks){
                    VisualProfiler.this.txtTime.setText(chunk);
                }
            }
        };
        worker.execute();   
    }
    
    
    //Helper
   
    /**
     * Creates a thread safe <code>SwingWorker</code> that performs
     * a <code>MultiLevelProfiler</code> profile and prints
     * the results to the console log of the graphical user interface
     * as the results are received.
     */
    private class ProfilerWorker extends SwingWorker<Object, String>{
        private ProfilerWorker.VisualMultiLevelProfiler multiProfiler;
        
        public ProfilerWorker(ProfileGraph2D profiler, List<Resolution> resolutions, List<Integer> datasetSizes){
            setEnabledActions(false);        
            threadStarted(this);
            publish("--------\n");
            publish(profiler.getGraphTitle() + "\n\n");
            
            String strAuthor = VisualProfiler.this.txtAuthorMessage.getText();
            String saveMessage = VisualProfiler.this.txtSaveMessage.getText();   
            
            this.multiProfiler = new ProfilerWorker.VisualMultiLevelProfiler(profiler);
            this.multiProfiler.getSaveSettings().setAuthorMessage(strAuthor);
            this.multiProfiler.getSaveSettings().setSaveMessage(saveMessage);
            this.multiProfiler.setImageSizes(resolutions);
            this.multiProfiler.setDatasetSizes(datasetSizes);
        }
        
        @Override
        protected Object doInBackground() throws Exception {
            this.multiProfiler.run();
            
            if (!Thread.currentThread().isInterrupted()){
                this.multiProfiler.saveStatistics();
                publish("\nProfiling complete." + "\n");
                publish("--------\n");
            }
            else{
                publish("\nProfiling cancelled." + "\n");
                publish("--------\n");                
            }
            
            setEnabledActions(true);
            threadFinished();
            return true;
        }
        
        @Override
        protected void process(List<String> chunks){
            for (String chunk: chunks){
                VisualProfiler.this.print(chunk);
            }
        }        
                
        private class VisualMultiLevelProfiler extends MultiLevelProfiler{
            public VisualMultiLevelProfiler(ProfileGraph2D profiler){
                super(profiler);
            }

            @Override
            public void processTimeWarning(int estimatedTime){
                ProfilerWorker.this.publish("The estimated run time is " + estimatedTime + " seconds." + "\n\n");
            }

            @Override
            public void processPreResult(Resolution resolution, int datasetSize){
                //Publishes
                ProfilerWorker.this.publish(resolution + ": " + datasetSize + ":" + "    ");
            }

            @Override
            public void processResult(Resolution resolution, int datasetSize, Statistics stats){
                ProfilerWorker.this.publish(stats.getAverageTime() + "ms" + "\n");                    
            }        
        };        
    };
    
    /**
     * 
     * @return returns a graph profiler based on the renderer
     *         selected from the graphical user interface,
     *         returns <code>null</code> if unable to get a profiler
     */
    public ProfileGraph2D getProfiler(){
        //Get inputs
        String strTestTime = this.txtTestTime.getText();
        String strMaxAttempts = this.txtMaxAttempts.getText();
        
        //Intended variables
        int testTime;
        int maxAttempts;
        StopWatch.TimeType timeType;
        ProfileGraph2D renderer;
                
        //Test Time
        try{
            testTime = Integer.parseInt(strTestTime);
            
            if (testTime <= 0){
                throw new NumberFormatException();
            }
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Enter a positive non-zero integer for test time.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        //Max Attempts
        try{
            maxAttempts = Integer.parseInt(strMaxAttempts);
            
            if (maxAttempts <= 0){
                throw new NumberFormatException();
            }
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Enter a positive non-zero integer for max attempts.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        //Time Type
        timeType = (StopWatch.TimeType) this.listTimeTypes.getSelectedItem();
        
        //Instance creation                
        renderer = getProfilerType();
        if (renderer == null){ return null; }
        
        //Update
        renderer.setTestTime(testTime);
        renderer.setMaxTries(maxAttempts);
        renderer.setTimeType(timeType);
        
        //Final Format
        return renderer;
    }
    
    public ProfileGraph2D getProfilerType(){
        String strClass = listRendererTypes.getSelectedItem().toString();
        ProfileGraph2D renderer;
        
        //Instance creation                
        try {
            Class profileClass = Class.forName(PROFILE_PATH + ".Profile" + strClass);
            renderer = (ProfileGraph2D) profileClass.newInstance();
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "This class is not currently accessible.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (InstantiationException ex) {
            Logger.getLogger(VisualProfiler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VisualProfiler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        return renderer;
    }
    
    /**
     * Prints the text to the graphical user interface console.
     * @param output text to print to the console
     */
    private void print(final String output){
        console.append(output);
    }
    
    /**
     * Creates a panel with the default layout manager
     * to add and center the component.
     * @param itemToAdd component to add to the panel
     * @return JPanel with default layout manager with
     *         component added to it
     */
    private JPanel blankPanel(Component itemToAdd){
        JPanel tmp = new JPanel();
        tmp.add(itemToAdd);
        return tmp;
    }
    
    /**
     * Takes a node with a <code>File</code> user object and adds
     * all subfiles as children nodes.
     * 
     * @param parentNode must contain a <code>File</code> as the user object;
     *                   adds all subfiles of the node
     */
    private void addNodes(DefaultMutableTreeNode parentNode){
        //Get subfiles of the node
        File[] subfiles = ((File)parentNode.getUserObject()).listFiles();
        
        //If there are files within (non-directories would not have subfiles)
        if (subfiles != null){
            
            //All subfiles
            for (File subfile: subfiles){
                
                //Is valid subfile
                if (subfile != null){
                    //Add node
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(subfile);
                    parentNode.add(childNode);
                    this.addNodes(childNode);
                }
            }
        }
    }
    
    /**
     * Reloads all files of the JTree.
     */
    private void refreshNodes(){
        //Resets
        this.treeRoot.removeAllChildren();
        
        //Re-adds
        this.addNodes(treeRoot);
        
        //Updates GUI
        this.treeModel.nodeStructureChanged(this.treeRoot);
        this.repaint();              
    }
    
    /**
     * Gets the current time (HH:MM:SS) to be displayed in the
     * graphical user interface.
     * @return current time (HH:MM:SS)
     */
    private String getTime(){
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        String format = "%02d";     

        return String.format(format, hour) +
               ":" +
               String.format(format, minute) +
               ":" +
               String.format(format, second);
                                
    }
    
    
    //Static
    
    /**
     * Constructs a thread safe <code>VisualProfiler</code> JFrame.
     */
    public static void invokeVisualAid(){
        EventQueue.invokeLater(new Runnable(){

            @Override
            public void run() {
                VisualProfiler frame = new VisualProfiler();
            }
            
        });
    }  
    
    /**
     * Constructs a <code>VisualProfiler</code> to provide
     * graphical user interface options to profile renderers.
     * @param args no effect
     */
    public static void main(String[] args){
        VisualProfiler.invokeVisualAid();
    }
}