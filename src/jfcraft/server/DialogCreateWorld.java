package jfcraft.server;

import java.io.*;
import java.util.*;

import javaforce.*;
import javaforce.awt.*;

import jfcraft.data.*;

/** Dialog to create world for server out of game.
 *
 * @author pquiring
 */
public class DialogCreateWorld extends javax.swing.JDialog {

  /**
   * Creates new form DialogCreateWorld
   */
  public DialogCreateWorld(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
    JFAWT.centerWindow(this);
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
    name = new javax.swing.JTextField();
    ok = new javax.swing.JButton();
    cancel = new javax.swing.JButton();
    jLabel2 = new javax.swing.JLabel();
    seed = new javax.swing.JTextField();
    random = new javax.swing.JButton();
    doSteps = new javax.swing.JCheckBox();
    doGrassBank = new javax.swing.JCheckBox();
    doFlatWorld = new javax.swing.JCheckBox();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Create New World");

    jLabel1.setText("Name:");

    name.setText("New World");

    ok.setText("OK");
    ok.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okActionPerformed(evt);
      }
    });

    cancel.setText("Cancel");
    cancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelActionPerformed(evt);
      }
    });

    jLabel2.setText("Seed:");

    seed.setText("0");

    random.setText("Random");
    random.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        randomActionPerformed(evt);
      }
    });

    doSteps.setText("Generate Smooth Steps");

    doGrassBank.setText("Generate Grass Banks");

    doFlatWorld.setText("Generate Flat World");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(name))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(cancel)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(ok))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(seed, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(random))
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(doSteps)
              .addComponent(doGrassBank)
              .addComponent(doFlatWorld))
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(seed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(random))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(doSteps)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(doGrassBank)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(doFlatWorld)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(ok)
          .addComponent(cancel))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
    create();
  }//GEN-LAST:event_okActionPerformed

  private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
    dispose();
  }//GEN-LAST:event_cancelActionPerformed

  private void randomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomActionPerformed
    random();
  }//GEN-LAST:event_randomActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton cancel;
  private javax.swing.JCheckBox doFlatWorld;
  private javax.swing.JCheckBox doGrassBank;
  private javax.swing.JCheckBox doSteps;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JTextField name;
  private javax.swing.JButton ok;
  private javax.swing.JButton random;
  private javax.swing.JTextField seed;
  // End of variables declaration//GEN-END:variables

  private void create() {
    String worldName = name.getText();
    if (worldName.length() == 0) return;
    File folder = new File(Static.getBasePath() + "/saves");
    if (!folder.exists()) {
      folder.mkdirs();
    }
    World world = new World(true);
    world.init();
    world.chunks = new Chunks(world);
    world.name = worldName;
    world.type = "default";
    world.options.seed = Long.valueOf(seed.getText());
    world.options.doSteps = doSteps.isSelected();
    world.options.doGrassBank = doGrassBank.isSelected();
    world.options.doFlatWorld = doFlatWorld.isSelected();
    String folderName = world.createFolderName(worldName);
    new File(folderName).mkdirs();
    world.save(folderName + "/world.dat");
    dispose();
  }

  private void random() {
    Random r = new Random();
    seed.setText("" + r.nextLong());
  }
}
