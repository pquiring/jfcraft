package jfcraft.client;

/** Main Window
 *
 * @author pquiring
 *
 * Created : Mar 21, 2014
 */

//list java imports
import jfcraft.opengl.RenderEngine;
import java.awt.*;
import javax.swing.*;

//list javaforce imports
import javaforce.*;
import javaforce.gl.*;
import jfcraft.data.*;

//list JFCraft imports

public class Main extends javax.swing.JFrame {

  /**
   * Creates new form Main
   */
  public Main() {
    initComponents();
    frame = this;
    setTitle("JFCraft/" + Static.version);
    setVisible(true);
    Insets insets = this.getInsets();
    setSize(512 + insets.left + insets.right, 512 + insets.top + insets.bottom);
    setPosition();
    JFImage icon = new JFImage();
    icon.loadPNG(this.getClass().getClassLoader().getResourceAsStream("jfcraft.png"));
    setIconImage(icon.getImage());
    init();
//    test();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

//    canvas = new GLCanvas();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    setName("Main"); // NOI18N
    getContentPane().setLayout(new java.awt.GridLayout(1, 0));
    getContentPane().add(canvas);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    if (args != null && args.length > 0) {
      for(int a=0;a<args.length;a++) {
        if (args[a].equals("-mlogs")) {
          Static.mlogs = true;
        }
      }
    }
    Settings.load();
    /* Create and display the form */
    JFLog.enableTimestamp(true);
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new Main();
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private java.awt.Canvas canvas;
  // End of variables declaration//GEN-END:variables

  private void setPosition() {
    Dimension d = getSize();
    Rectangle s = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    setLocation(s.width/2 - d.width/2, s.height/2 - d.height/2);
  }

  private void init() {
//    ((GLCanvas)canvas).init(new RenderEngine(new Loading()));
  }

  public static JFrame frame;
}
