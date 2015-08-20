package jfcraft.client;

/** jfCraft launcher
 *
 * @author pquiring
 */

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import javaforce.*;
import jfcraft.data.Settings;
import jfcraft.data.Static;

import jfcraft.plugin.*;

public class Launcher extends javax.swing.JFrame {

  /**
   * Creates new form Launcher
   */
  public Launcher() {
    initComponents();
    Settings.load();
    player.setText(Settings.current.player);
    setTitle("jfCraft/" + Static.version);
    JF.centerWindow(this);
    JFImage icon = new JFImage();
    icon.loadPNG(this.getClass().getClassLoader().getResourceAsStream("jfcraft.png"));
    setIconImage(icon.getImage());
    plugins = PluginLoader.listPlugins();
    pluginsModel = (DefaultTableModel)pluginsTable.getModel();
    for(int a=0;a<plugins.length;a++) {
      pluginsModel.addRow(new Object[] {true, plugins[a][2]});
    }
    new Thread() {
      public void run() {
        checkVersion();
      }
    }.start();
    initWeb();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    pluginsTable = new javax.swing.JTable();
    jLabel1 = new javax.swing.JLabel();
    start = new javax.swing.JButton();
    jLabel2 = new javax.swing.JLabel();
    player = new javax.swing.JTextField();
    getPlugins = new javax.swing.JButton();
    jLabel3 = new javax.swing.JLabel();
    memory = new javax.swing.JComboBox();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    html = new javax.swing.JEditorPane();
    opts = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("JFCraft");
    setResizable(false);

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

    jLabel1.setText("Plugins");

    start.setText("Start");
    start.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        startActionPerformed(evt);
      }
    });

    jLabel2.setText("Player Name:");

    player.setText("Player");

    getPlugins.setText("Get Plugins");
    getPlugins.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        getPluginsActionPerformed(evt);
      }
    });

    jLabel3.setText("Memory:");

    memory.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "256M", "512M", "1G", "2G", "4G" }));
    memory.setSelectedIndex(2);

    html.setEditable(false);
    html.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
      public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
        htmlHyperlinkUpdate(evt);
      }
    });
    jScrollPane2.setViewportView(html);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane2)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
    );

    opts.setText("Options...");
    opts.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        optsActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(jLabel3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(memory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(opts)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(start))
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 333, Short.MAX_VALUE)
                .addComponent(getPlugins))
              .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(player)))
            .addGap(0, 0, Short.MAX_VALUE))
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(getPlugins))
        .addGap(7, 7, 7)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(player, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(start)
          .addComponent(jLabel3)
          .addComponent(memory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(opts))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void getPluginsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getPluginsActionPerformed
    JF.showMessage("TODO", "Not implemented yet!");
  }//GEN-LAST:event_getPluginsActionPerformed

  private void startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startActionPerformed
    if (player.getText().length() < 3) {
      JF.showError("Error", "Playername must be at least 3 chars");
      return;
    }
    Settings.current.player = player.getText();
    Settings.save();
    try {
      ArrayList<String> cmd = new ArrayList<String>();
      if (JF.isWindows())
        cmd.add(System.getProperty("java.home") + "/bin/javaw.exe");
      else
        cmd.add(System.getProperty("java.home") + "/bin/java");
      cmd.add("-Xmx" + memory.getSelectedItem());
      String cp = "";
      cp += getJar("javaforce.jar");
      cp += File.pathSeparator;
      cp += getJar("lwjgl.jar");
      cp += File.pathSeparator;
      cp += getJar("jfcraft.jar");
      int rows = pluginsModel.getRowCount();
      for(int row=0;row<rows;row++) {
        if ((Boolean)pluginsModel.getValueAt(row,0)) {
          cp += File.pathSeparator + plugins[row][0];
        }
      }
      cmd.add("-cp");
      cmd.add(cp);
      cmd.add("jfcraft.client.Main");
      Runtime.getRuntime().exec(cmd.toArray(new String[0]));
      System.exit(0);
    } catch (Exception e) {
      Static.log(e);
      JF.showError("Error", "Failed to start (see stdout)");
    }
  }//GEN-LAST:event_startActionPerformed

  private void htmlHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_htmlHyperlinkUpdate
    JF.openURL(evt.getURL().toString());
  }//GEN-LAST:event_htmlHyperlinkUpdate

  private void optsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optsActionPerformed
    EditOptions dialog = new EditOptions(this, true);
    dialog.setVisible(true);
  }//GEN-LAST:event_optsActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    if (JF.isMac()) {
      JF.showError("Error", "Sorry, Mac is not supported yet");
      return;
    }
    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new Launcher().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton getPlugins;
  private javax.swing.JEditorPane html;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JComboBox memory;
  private javax.swing.JButton opts;
  private javax.swing.JTextField player;
  private javax.swing.JTable pluginsTable;
  private javax.swing.JButton start;
  // End of variables declaration//GEN-END:variables

  String plugins[][];  //jar/class/name/version/desc
  DefaultTableModel pluginsModel;

  public void checkVersion() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(
        new URL("http://pquiring.github.io/jfcraft/version.html").openStream()));
      String line = reader.readLine();
      if (line.equals(Static.version)) {Static.log("version is up-to-date"); return;}
      Static.log("newer version is available : " + line);
      JOptionPane.showMessageDialog(this,
        "A newer version of jfCraft is available! (v" + line + ")\r\nPlease goto http://pquiring.github.io/jfcraft to download it",
        "Info",
        JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
      Static.log(e);
    }
  }

  public String getJar(String name) {
    String full = "/usr/share/java/" + name;
    if (new File(full).exists()) return full;
    return name;
  }

  public void initWeb() {
    try {
      html.setContentType("text/html");
      html.setPage(new URL("http://pquiring.github.io/jfcraft/news.html"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
