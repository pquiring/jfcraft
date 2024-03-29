package jfcraft.server;

/**
 * Main for dedicated server
 *
 * @author pquiring
 *
 * Created : Aug 7, 2014
 */

import javaforce.*;
import javaforce.awt.*;

import javax.swing.*;

import jfcraft.data.*;

public class Main extends javax.swing.JFrame {

  /**
   * Creates new form Main
   */
  public Main() {
    this.main = this;
    initComponents();
    if (worldFolder != null) {
      setContentPane(new PanelServer(worldFolder));
    } else {
      setContentPane(new PanelSettings());
    }
    JFAWT.centerWindow(this);
    JFImage icon = new JFImage();
    icon.loadPNG(this.getClass().getClassLoader().getResourceAsStream("jfcraft.png"));
    setIconImage(icon.getImage());
    setTitle("jfCraft Server/" + Static.version);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Server");
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 608, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 489, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    if (server != null) {
      server.close();
    }
  }//GEN-LAST:event_formWindowClosing

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    Settings.load();
    boolean loadGUI = true;
    if (args != null && args.length > 0) {
      for(int a=0;a<args.length;a++) {
        if (args[a].equals("-mlogs")) {
          Static.mlogs = true;
        }
        if (args[a].equals("-start")) {
          a++;
          startWorldCLI(args[a]);
          loadGUI = false;
        }
        if (args[a].equals("-startw")) {
          a++;
          startWorldGUI(args[a]);
        }
      }
    }
    if (loadGUI) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          new Main().setVisible(true);
        }
      });
    }
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables

  private static void startWorldCLI(String name) {
    Static.log("Loading...");
    Static.registerAll(false);
    String worldFolder = Static.getWorldsPath() + name;
    server = new Server();
    server.startWorld(worldFolder);
  }

  private static void startWorldGUI(String name) {
    Static.log("Loading...");
    Static.registerAll(false);
    worldFolder = Static.getWorldsPath() + name;
  }

  private static Main main;
  private static String worldFolder;

  public static Server server;

  public static void setContentPanel(JPanel panel) {
    main.setContentPane(panel);
    main.revalidate();
  }
}
