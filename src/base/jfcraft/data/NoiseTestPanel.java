package jfcraft.data;

/** Noise test panel
 *
 * @author pquiring
 */

import java.awt.*;
import javax.swing.*;
import java.util.*;

import javaforce.*;

public class NoiseTestPanel extends javax.swing.JPanel {

  /**
   * Creates new form NoiseTest
   */
  public NoiseTestPanel() {
    initComponents();
    generate();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    mode = new javax.swing.JComboBox();
    canvas = new java.awt.Canvas() {
      public void paint(java.awt.Graphics g) {
        if (img == null) return;
        g.drawImage(img.getImage(), 0, 0, null);
      }
    };
    jLabel13 = new javax.swing.JLabel();
    jButton1 = new javax.swing.JButton();
    angle = new javax.swing.JSlider();
    jPanel1 = new javax.swing.JPanel();
    seed = new javax.swing.JSpinner();
    jLabel1 = new javax.swing.JLabel();
    yscale = new javax.swing.JSpinner();
    jLabel9 = new javax.swing.JLabel();
    jLabel16 = new javax.swing.JLabel();
    base = new javax.swing.JSpinner();
    jLabel2 = new javax.swing.JLabel();
    jLabel15 = new javax.swing.JLabel();
    cz = new javax.swing.JSpinner();
    cy = new javax.swing.JSpinner();
    jLabel7 = new javax.swing.JLabel();
    scale = new javax.swing.JSpinner();
    threshold = new javax.swing.JSpinner();
    octaves = new javax.swing.JSpinner();
    jLabel3 = new javax.swing.JLabel();
    cx = new javax.swing.JSpinner();
    persist = new javax.swing.JSpinner();
    jLabel8 = new javax.swing.JLabel();
    jLabel17 = new javax.swing.JLabel();
    jLabel14 = new javax.swing.JLabel();
    jPanel2 = new javax.swing.JPanel();
    seed2 = new javax.swing.JSpinner();
    jLabel4 = new javax.swing.JLabel();
    yscale2 = new javax.swing.JSpinner();
    jLabel10 = new javax.swing.JLabel();
    jLabel18 = new javax.swing.JLabel();
    base2 = new javax.swing.JSpinner();
    jLabel5 = new javax.swing.JLabel();
    jLabel19 = new javax.swing.JLabel();
    cz2 = new javax.swing.JSpinner();
    cy2 = new javax.swing.JSpinner();
    jLabel11 = new javax.swing.JLabel();
    scale2 = new javax.swing.JSpinner();
    threshold2 = new javax.swing.JSpinner();
    octaves2 = new javax.swing.JSpinner();
    jLabel6 = new javax.swing.JLabel();
    cx2 = new javax.swing.JSpinner();
    persist2 = new javax.swing.JSpinner();
    jLabel12 = new javax.swing.JLabel();
    jLabel20 = new javax.swing.JLabel();
    jLabel21 = new javax.swing.JLabel();

    mode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2D", "3D", "X2", "X3" }));
    mode.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        modeItemStateChanged(evt);
      }
    });
    mode.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        modeActionPerformed(evt);
      }
    });

    jLabel13.setText("Mode");

    jButton1.setText("Generate");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    angle.setMaximum(128);
    angle.setOrientation(javax.swing.JSlider.VERTICAL);
    angle.setToolTipText("");
    angle.setValue(128);
    angle.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        angleStateChanged(evt);
      }
    });

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("X1"));

    seed.setModel(new javax.swing.SpinnerNumberModel(1L, null, null, 1L));
    seed.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        seedStateChanged(evt);
      }
    });

    jLabel1.setText("Octaves");

    yscale.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(128.0f), Float.valueOf(0.0f), Float.valueOf(128.0f), Float.valueOf(1.0f)));
    yscale.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        yscaleStateChanged(evt);
      }
    });

    jLabel9.setText("Y:");

    jLabel16.setText("y scale");

    base.setModel(new javax.swing.SpinnerNumberModel(128, 0, 256, 1));
    base.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        baseStateChanged(evt);
      }
    });

    jLabel2.setText("Persist");

    jLabel15.setText("base");

    cz.setModel(new javax.swing.SpinnerNumberModel(0, -100, 100, 1));
    cz.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        czStateChanged(evt);
      }
    });

    cy.setModel(new javax.swing.SpinnerNumberModel(0, -100, 100, 1));
    cy.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        cyStateChanged(evt);
      }
    });

    jLabel7.setText("X:");

    scale.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.01f), Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(0.01f)));
    scale.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        scaleStateChanged(evt);
      }
    });

    threshold.setModel(new javax.swing.SpinnerNumberModel(128, 0, 256, 1));
    threshold.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        thresholdStateChanged(evt);
      }
    });

    octaves.setModel(new javax.swing.SpinnerNumberModel(2, 1, 16, 1));
    octaves.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        octavesStateChanged(evt);
      }
    });

    jLabel3.setText("Scale");

    cx.setModel(new javax.swing.SpinnerNumberModel(0, -100, 100, 1));
    cx.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        cxStateChanged(evt);
      }
    });

    persist.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.5f), Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(0.1f)));
    persist.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        persistStateChanged(evt);
      }
    });

    jLabel8.setText("Z:");

    jLabel17.setText("3d thres");

    jLabel14.setText("seed");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel17)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(threshold))
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel16)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(yscale))
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel1)
              .addComponent(jLabel2)
              .addComponent(jLabel3)
              .addComponent(jLabel7)
              .addComponent(jLabel9)
              .addComponent(jLabel8)
              .addComponent(jLabel14)
              .addComponent(jLabel15))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(seed, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(scale, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(persist, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(octaves, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(cx, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
              .addComponent(cy, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(cz, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(base))))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(octaves, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(4, 4, 4)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(persist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(scale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel3))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel7)
          .addComponent(cx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel9)
          .addComponent(cy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel8)
          .addComponent(cz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel14)
          .addComponent(seed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel15)
          .addComponent(base, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel16)
          .addComponent(yscale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel17)
          .addComponent(threshold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("X2"));

    seed2.setModel(new javax.swing.SpinnerNumberModel(1L, null, null, 1L));
    seed2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        seed2StateChanged(evt);
      }
    });

    jLabel4.setText("Octaves");

    yscale2.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(128.0f), Float.valueOf(0.0f), Float.valueOf(128.0f), Float.valueOf(1.0f)));
    yscale2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        yscale2StateChanged(evt);
      }
    });

    jLabel10.setText("Y:");

    jLabel18.setText("y scale");

    base2.setModel(new javax.swing.SpinnerNumberModel(128, 0, 256, 1));
    base2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        base2StateChanged(evt);
      }
    });

    jLabel5.setText("Persist");

    jLabel19.setText("base");

    cz2.setModel(new javax.swing.SpinnerNumberModel(0, -100, 100, 1));
    cz2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        cz2StateChanged(evt);
      }
    });

    cy2.setModel(new javax.swing.SpinnerNumberModel(0, -100, 100, 1));
    cy2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        cy2StateChanged(evt);
      }
    });

    jLabel11.setText("X:");

    scale2.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.01f), Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(0.01f)));
    scale2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        scale2StateChanged(evt);
      }
    });

    threshold2.setModel(new javax.swing.SpinnerNumberModel(128, 0, 256, 1));
    threshold2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        threshold2StateChanged(evt);
      }
    });

    octaves2.setModel(new javax.swing.SpinnerNumberModel(2, 1, 16, 1));
    octaves2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        octaves2StateChanged(evt);
      }
    });

    jLabel6.setText("Scale");

    cx2.setModel(new javax.swing.SpinnerNumberModel(0, -100, 100, 1));
    cx2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        cx2StateChanged(evt);
      }
    });

    persist2.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.5f), Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(0.1f)));
    persist2.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        persist2StateChanged(evt);
      }
    });

    jLabel12.setText("Z:");

    jLabel20.setText("3d thres");

    jLabel21.setText("seed");

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
            .addComponent(jLabel20)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(threshold2))
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
            .addComponent(jLabel18)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(yscale2))
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel4)
              .addComponent(jLabel5)
              .addComponent(jLabel6)
              .addComponent(jLabel11)
              .addComponent(jLabel10)
              .addComponent(jLabel12)
              .addComponent(jLabel21)
              .addComponent(jLabel19))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(seed2, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(scale2, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(persist2, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(octaves2, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(cx2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
              .addComponent(cy2, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(cz2, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(base2))))
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(octaves2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(4, 4, 4)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(persist2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel5))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(scale2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel6))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel11)
          .addComponent(cx2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel10)
          .addComponent(cy2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel12)
          .addComponent(cz2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel21)
          .addComponent(seed2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel19)
          .addComponent(base2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel18)
          .addComponent(yscale2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel20)
          .addComponent(threshold2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(canvas, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(angle, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel13)
            .addGap(18, 18, 18)
            .addComponent(mode, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton1)))
        .addContainerGap(50, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(canvas, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(mode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel13)
              .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(angle, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents

  private void modeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_modeActionPerformed

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    generate();
  }//GEN-LAST:event_jButton1ActionPerformed

  private void octavesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_octavesStateChanged
    generate();
  }//GEN-LAST:event_octavesStateChanged

  private void persistStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_persistStateChanged
    generate();
  }//GEN-LAST:event_persistStateChanged

  private void scaleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_scaleStateChanged
    generate();
  }//GEN-LAST:event_scaleStateChanged

  private void cxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cxStateChanged
    generate();
  }//GEN-LAST:event_cxStateChanged

  private void cyStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cyStateChanged
    generate();
  }//GEN-LAST:event_cyStateChanged

  private void czStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_czStateChanged
    generate();
  }//GEN-LAST:event_czStateChanged

  private void baseStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_baseStateChanged
    generate();
  }//GEN-LAST:event_baseStateChanged

  private void yscaleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_yscaleStateChanged
    generate();
  }//GEN-LAST:event_yscaleStateChanged

  private void modeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_modeItemStateChanged
    generate();
  }//GEN-LAST:event_modeItemStateChanged

  private void angleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_angleStateChanged
    generate();
  }//GEN-LAST:event_angleStateChanged

  private void thresholdStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_thresholdStateChanged
    generate();
  }//GEN-LAST:event_thresholdStateChanged

  private void seedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_seedStateChanged
    generate();
  }//GEN-LAST:event_seedStateChanged

  private void seed2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_seed2StateChanged
    generate();
  }//GEN-LAST:event_seed2StateChanged

  private void yscale2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_yscale2StateChanged
    generate();
  }//GEN-LAST:event_yscale2StateChanged

  private void base2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_base2StateChanged
    generate();
  }//GEN-LAST:event_base2StateChanged

  private void cz2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cz2StateChanged
    generate();
  }//GEN-LAST:event_cz2StateChanged

  private void cy2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cy2StateChanged
    generate();
  }//GEN-LAST:event_cy2StateChanged

  private void scale2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_scale2StateChanged
    generate();
  }//GEN-LAST:event_scale2StateChanged

  private void threshold2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_threshold2StateChanged
    generate();
  }//GEN-LAST:event_threshold2StateChanged

  private void octaves2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_octaves2StateChanged
    generate();
  }//GEN-LAST:event_octaves2StateChanged

  private void cx2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cx2StateChanged
    generate();
  }//GEN-LAST:event_cx2StateChanged

  private void persist2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_persist2StateChanged
    generate();
  }//GEN-LAST:event_persist2StateChanged


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JSlider angle;
  private javax.swing.JSpinner base;
  private javax.swing.JSpinner base2;
  private java.awt.Canvas canvas;
  private javax.swing.JSpinner cx;
  private javax.swing.JSpinner cx2;
  private javax.swing.JSpinner cy;
  private javax.swing.JSpinner cy2;
  private javax.swing.JSpinner cz;
  private javax.swing.JSpinner cz2;
  private javax.swing.JButton jButton1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel10;
  private javax.swing.JLabel jLabel11;
  private javax.swing.JLabel jLabel12;
  private javax.swing.JLabel jLabel13;
  private javax.swing.JLabel jLabel14;
  private javax.swing.JLabel jLabel15;
  private javax.swing.JLabel jLabel16;
  private javax.swing.JLabel jLabel17;
  private javax.swing.JLabel jLabel18;
  private javax.swing.JLabel jLabel19;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel20;
  private javax.swing.JLabel jLabel21;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JComboBox mode;
  private javax.swing.JSpinner octaves;
  private javax.swing.JSpinner octaves2;
  private javax.swing.JSpinner persist;
  private javax.swing.JSpinner persist2;
  private javax.swing.JSpinner scale;
  private javax.swing.JSpinner scale2;
  private javax.swing.JSpinner seed;
  private javax.swing.JSpinner seed2;
  private javax.swing.JSpinner threshold;
  private javax.swing.JSpinner threshold2;
  private javax.swing.JSpinner yscale;
  private javax.swing.JSpinner yscale2;
  // End of variables declaration//GEN-END:variables

  private JFImage img = new JFImage(256, 384);

  public void generate() {
    Noise noise1 = new Noise();
    Noise noise2 = new Noise();
    Noise noise3 = new Noise();
    Random rand = new Random();
    rand.setSeed((Long)seed.getValue());
    noise1.init(rand, (Integer)octaves.getValue(), (Float)persist.getValue(), (Float)scale.getValue());
    rand.setSeed((Long)seed2.getValue());
    noise2.init(rand, (Integer)octaves2.getValue(), (Float)persist2.getValue(), (Float)scale2.getValue());
    noise3.init(rand, (Integer)octaves.getValue(), (Float)persist.getValue(), (Float)scale.getValue());
    img.fill(0, 0, 256, 384, 0xff000000, true);
    int clr = Color.gray.getRGB();
    int red = Color.red.getRGB();
    img.line(0, 128, 128, 0, clr);
    img.line(0, 128, 128, 255, clr);
    img.line(128, 0, 255, 128, clr);
    img.line(128, 255, 255, 128, clr);

    img.line(0, 128, 0, 255, clr);
    img.line(255, 128, 255, 255, clr);

    img.line(0, 255, 128, 383, clr);
    img.line(128, 383, 255, 255, clr);
    clr = Color.white.getRGB();

    int _cx = (Integer)cx.getValue() * 16;
    int _cy = (Integer)cy.getValue() * 16;
    int _cz = (Integer)cz.getValue() * 16;
    int _base = (Integer)base.getValue();
    float _yscale = (Float)yscale.getValue();

    int _cx2 = (Integer)cx2.getValue() * 16;
    int _cy2 = (Integer)cy2.getValue() * 16;
    int _cz2 = (Integer)cz2.getValue() * 16;
    int _base2 = (Integer)base2.getValue();
    float _yscale2 = (Float)yscale2.getValue();

    float _angle = (Integer)angle.getValue();
    _angle /= 128.0f;
    switch (mode.getSelectedIndex()) {
      case 0: {
        //2d
        for(float x=0;x<128;x++) {
          for(float z=0;z<128;z++) {
            float nx = x + _cx;
            float nz = z + _cz;
            float y = (noise1.noise_2d(nx,nz) * _yscale + _base) / 2.0f;
            float _x = x + z;
            float _y2 = -x * _angle + z * _angle + 256;
            float _y1 = _y2 - y;
            img.putPixel((int)_x, (int)_y1, nx == 0 && nz == 0 ? red : clr);
          }
        }
        break;
      }
      case 1: {
        //3d
        int r,b,g;
        int _threshold = (Integer)threshold.getValue();
        for(float y=0;y<128;y+=3) {
          for(float x=0;x<128;x+=3) {
            for(float z=0;z<128;z+=3) {
              int a = (int)(noise1.noise_3d(x + _cx,y + _cy, z + _cz) * _yscale * 2/* + _base*/);
//              if (Math.abs(a) <= _threshold) continue;
//              a &= 0xff;
              float _x = x + z;
              float _y = 256 + (-x * _angle) + (z * _angle) - y;
  /*
              float o = 256 - a;
              a *= 256.0f;
              clr = img.getPixel((int)_x, (int)_y);
              r = (clr & 0xff0000) >> 16;
              g = (clr & 0xff00) >> 8;
              b = clr & 0xff;
              r = (int)(r * o + a) >> 8;
              g = (int)(g * o + a) >> 8;
              b = (int)(b * o + a) >> 8;
              clr = r << 16 + g << 8 + b;
  */
              if (a < 0) {
                a *= -1;
                a &= 0xff;
                clr = (a << 16);
              } else {
                a &= 0xff;
                clr = (a << 16) + (a << 8) + (a & 0xff);
              }
              img.putPixel((int)_x, (int)_y, clr);
            }
          }
        }
        break;
      }
      case 2: {
        //X2
        int _threshold = (Integer)threshold.getValue();
        for(float x=0;x<128;x++) {
          for(float z=0;z<128;z++) {
            int y1 = (int)((noise1.noise_2d(x + _cx,z + _cz) * _yscale + _base));
            int y2 = (int)((noise2.noise_2d(x + _cx,z + _cz) * _yscale2 + _base2));
            if (Static.abs(y1 - y2) > _threshold) continue;
            float _x = x + z;
            float _y2 = -x * _angle + z * _angle + 256;
            float _y1 = _y2 - 64;
            img.putPixel((int)_x, (int)_y1, clr);
          }
        }
        break;
      }
      case 3: {
        //X3
        for(float x=0;x<128;x++) {
          for(float z=0;z<128;z++) {
            int y1 = (int)((noise1.noise_2d(x + _cx,z + _cz) * _yscale + _base));
            int y2 = (int)((noise2.noise_2d(x + _cx,z + _cz) * _yscale + _base));
            int y3 = (int)((noise2.noise_2d(x + _cx,z + _cz) * _yscale + _base));
            if (Static.abs(y1 - y2 - y3) > 2) continue;
            float _x = x + z;
            float _y2 = -x * _angle + z * _angle + 256;
            float _y1 = _y2 - 64;
            img.putPixel((int)_x, (int)_y1, clr);
          }
        }
        break;
      }
    }
    canvas.repaint();
  }

  public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {public void run() {
      JFrame frame = new JFrame();
      frame.setSize(600, 500);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(new NoiseTestPanel());
      frame.setVisible(true);
    }});
  }
}
