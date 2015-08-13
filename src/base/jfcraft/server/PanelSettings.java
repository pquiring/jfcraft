package jfcraft.server;

import java.io.*;
import java.util.ArrayList;
import javax.swing.table.*;

import javaforce.*;

import jfcraft.data.*;
import jfcraft.plugin.*;

/**
 *
 * @author pquiring
 */
public class PanelSettings extends javax.swing.JPanel {

  /**
   * Creates new form SettingsPanel
   */
  public PanelSettings() {
    initComponents();
    listWorlds();
    plugins = PluginLoader.listPlugins();
    pluginsModel = (DefaultTableModel)pluginsTable.getModel();
    for(int a=0;a<plugins.length;a++) {
      pluginsModel.addRow(new Object[] {true, plugins[a][2]});
    }
    pvp.setSelected(Settings.current.pvp);
    dropItemsOnDeath.setSelected(Settings.current.dropItemsOnDeath);
    voip.setSelected(Settings.current.server_voip);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    worlds = new javax.swing.JComboBox();
    jButton1 = new javax.swing.JButton();
    jButton2 = new javax.swing.JButton();
    jPanel1 = new javax.swing.JPanel();
    voip = new javax.swing.JCheckBox();
    pvp = new javax.swing.JCheckBox();
    dropItemsOnDeath = new javax.swing.JCheckBox();
    jScrollPane1 = new javax.swing.JScrollPane();
    pluginsTable = new javax.swing.JTable();
    jLabel3 = new javax.swing.JLabel();
    memory = new javax.swing.JComboBox();
    jLabel2 = new javax.swing.JLabel();

    jLabel1.setText("World");

    jButton1.setText("Start Server");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    jButton2.setText("Create");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
      }
    });

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));

    voip.setSelected(true);
    voip.setText("VoIP");

    pvp.setSelected(true);
    pvp.setText("PvP");

    dropItemsOnDeath.setSelected(true);
    dropItemsOnDeath.setText("Drop Items on Death");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(pvp)
          .addComponent(voip)
          .addComponent(dropItemsOnDeath))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(pvp)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(voip)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(dropItemsOnDeath)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pluginsTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "en", "plugin"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.Boolean.class, java.lang.Object.class
      };
      boolean[] canEdit = new boolean [] {
        true, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    jScrollPane1.setViewportView(pluginsTable);
    if (pluginsTable.getColumnModel().getColumnCount() > 0) {
      pluginsTable.getColumnModel().getColumn(0).setMinWidth(25);
      pluginsTable.getColumnModel().getColumn(0).setPreferredWidth(25);
      pluginsTable.getColumnModel().getColumn(0).setMaxWidth(25);
    }

    jLabel3.setText("Memory:");

    memory.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "256M", "512M", "1G", "2G", "4G" }));
    memory.setSelectedIndex(2);

    jLabel2.setText("Plugins");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(worlds, 0, 279, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton2))
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel2)
            .addGap(0, 0, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(memory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton1)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(worlds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jButton2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jButton1)
          .addComponent(memory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel3))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    start();
  }//GEN-LAST:event_jButton1ActionPerformed

  private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    create();
  }//GEN-LAST:event_jButton2ActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox dropItemsOnDeath;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JComboBox memory;
  private javax.swing.JTable pluginsTable;
  private javax.swing.JCheckBox pvp;
  private javax.swing.JCheckBox voip;
  private javax.swing.JComboBox worlds;
  // End of variables declaration//GEN-END:variables

  String plugins[][];  //jar/class/name/version/desc
  DefaultTableModel pluginsModel;

  public void listWorlds() {
    File folder = new File(Static.getWorldsPath());
    if (!folder.exists()) {
      folder.mkdirs();
    }
    worlds.removeAllItems();
    File saves[] = folder.listFiles();
    for(int a=0;a<saves.length;a++) {
      File save = saves[a];
      if (!save.isDirectory()) continue;
      worlds.addItem(save.getName());
    }
  }

  public void create() {
    DialogCreateWorld dialog = new DialogCreateWorld(null, true);
    dialog.setVisible(true);
    listWorlds();
  }

  public void start() {
    String name = (String)worlds.getSelectedItem();
    if (name == null) {
      JF.showError("Error", "Select or create a world first");
      return;
    }
    String worldFolder = Static.getWorldsPath() + name;
    if (!new File(worldFolder + "/world.dat").exists()) {
      JF.showError("Error", "World does not exist");
      return;
    }
    Settings.current.server_voip = voip.isSelected();
    Settings.current.pvp = pvp.isSelected();
    Settings.current.dropItemsOnDeath = dropItemsOnDeath.isSelected();
    Settings.save();
    if (false) {
      //run in this instance of Java
      Main.setContentPanel(new PanelServer(worldFolder));
      return;
    }
    try {
      ArrayList<String> cmd = new ArrayList<String>();
      if (JF.isWindows())
        cmd.add(System.getProperty("java.home") + "/bin/javaw.exe");
      else
        cmd.add(System.getProperty("java.home") + "/bin/java");
      cmd.add("-Xmx" + memory.getSelectedItem());
      String cp;
      if (JF.isWindows() || JF.isMac())
        cp = "javaforce.jar" + File.pathSeparator + "jfcraft.jar";
      else
        cp = "/usr/share/java/javaforce.jar" + File.pathSeparator + "/usr/share/java/jfcraft.jar";
      int rows = pluginsModel.getRowCount();
      for(int row=0;row<rows;row++) {
        if ((Boolean)pluginsModel.getValueAt(row,0)) {
          cp += File.pathSeparator + plugins[row][0];
        }
      }
      cmd.add("-cp");
      cmd.add(cp);
      cmd.add("jfcraft.server.Main");
      cmd.add("-startw");
      cmd.add(name);
      Runtime.getRuntime().exec(cmd.toArray(new String[0]));
      System.exit(0);
    } catch (Exception e) {
      Static.log(e);
      JF.showError("Error", "Failed to start (see stdout)");
    }
  }
}