package jfcraft.client;

/**
 *
 * @author pquiring
 */

import javaforce.*;
import javaforce.awt.*;
import javaforce.media.*;

import jfcraft.data.Settings;

public class EditOptions extends javax.swing.JDialog {

  /**
   * Creates new form EditOptios
   */
  public EditOptions(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
    JFAWT.centerWindow(this);
    load();
    listDevices();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    audio_system = new javax.swing.ButtonGroup();
    mic_mode = new javax.swing.ButtonGroup();
    jPanel1 = new javax.swing.JPanel();
    server_voip = new javax.swing.JCheckBox();
    pvp = new javax.swing.JCheckBox();
    jPanel2 = new javax.swing.JPanel();
    client_voip = new javax.swing.JCheckBox();
    jLabel1 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    mic = new javax.swing.JComboBox();
    ptt = new javax.swing.JRadioButton();
    phone = new javax.swing.JRadioButton();
    jLabel4 = new javax.swing.JLabel();
    spk = new javax.swing.JComboBox();
    ok = new javax.swing.JButton();
    cancel = new javax.swing.JButton();
    jPanel3 = new javax.swing.JPanel();
    dropItemsOnDeath = new javax.swing.JCheckBox();
    clouds = new javax.swing.JCheckBox();
    jLabel2 = new javax.swing.JLabel();
    fpsSlider = new javax.swing.JSlider();
    fpsLabel = new javax.swing.JLabel();
    fancy = new javax.swing.JCheckBox();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Options");

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Server Options (Open To Lan)"));

    server_voip.setSelected(true);
    server_voip.setText("VoIP");

    pvp.setSelected(true);
    pvp.setText("PvP");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(pvp)
          .addComponent(server_voip))
        .addContainerGap(286, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(pvp)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(server_voip)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Client Options"));

    client_voip.setSelected(true);
    client_voip.setText("Enable VoIP In-Game");

    jLabel1.setText("Mic Mode:");

    jLabel3.setText("Mic Device:");

    mic_mode.add(ptt);
    ptt.setSelected(true);
    ptt.setText("Push to Talk (PTT)");
    ptt.setToolTipText("Press Right CTRL to Talk.");

    mic_mode.add(phone);
    phone.setText("Phone");
    phone.setToolTipText("Mic is always enabled.");

    jLabel4.setText("Speaker Device:");

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(jLabel3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(mic, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(client_voip)
              .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ptt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(phone)))
            .addGap(0, 95, Short.MAX_VALUE))
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(jLabel4)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(spk, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(client_voip)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(ptt)
          .addComponent(phone))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(mic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(spk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    ok.setText("Ok");
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

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("World Options"));

    dropItemsOnDeath.setSelected(true);
    dropItemsOnDeath.setText("Drop Items on Death");

    clouds.setSelected(true);
    clouds.setText("Clouds");

    jLabel2.setText("FPS");

    fpsSlider.setMajorTickSpacing(1);
    fpsSlider.setMaximum(260);
    fpsSlider.setMinimum(10);
    fpsSlider.setMinorTickSpacing(10);
    fpsSlider.setSnapToTicks(true);
    fpsSlider.setValue(60);
    fpsSlider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        fpsSliderStateChanged(evt);
      }
    });

    fpsLabel.setText("50");

    fancy.setSelected(true);
    fancy.setText("Fancy Graphics");

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(fpsSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(fpsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(dropItemsOnDeath)
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(clouds)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fancy)))
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(fpsLabel)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(dropItemsOnDeath)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(clouds)
              .addComponent(fancy))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(fpsSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(cancel)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(ok)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(ok)
          .addComponent(cancel))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
    dispose();
  }//GEN-LAST:event_cancelActionPerformed

  private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
    save();
    dispose();
  }//GEN-LAST:event_okActionPerformed

  private void fpsSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fpsSliderStateChanged
    updateFPS();
  }//GEN-LAST:event_fpsSliderStateChanged

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup audio_system;
  private javax.swing.JButton cancel;
  private javax.swing.JCheckBox client_voip;
  private javax.swing.JCheckBox clouds;
  private javax.swing.JCheckBox dropItemsOnDeath;
  private javax.swing.JCheckBox fancy;
  private javax.swing.JLabel fpsLabel;
  private javax.swing.JSlider fpsSlider;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JComboBox mic;
  private javax.swing.ButtonGroup mic_mode;
  private javax.swing.JButton ok;
  private javax.swing.JRadioButton phone;
  private javax.swing.JRadioButton ptt;
  private javax.swing.JCheckBox pvp;
  private javax.swing.JCheckBox server_voip;
  private javax.swing.JComboBox spk;
  // End of variables declaration//GEN-END:variables

  private boolean ready = false;

  private void load() {
    server_voip.setSelected(Settings.current.server_voip);
    pvp.setSelected(Settings.current.pvp);
    dropItemsOnDeath.setSelected(Settings.current.dropItemsOnDeath);
    client_voip.setSelected(Settings.current.client_voip);
    ptt.setSelected(Settings.current.ptt);
    fancy.setSelected(Settings.current.fancy);
    clouds.setSelected(Settings.current.clouds);
    if (Settings.current.FPS == -1)
      fpsSlider.setValue(260);
    else
      fpsSlider.setValue(Settings.current.FPS);
    updateFPS();
    ready = true;
  }

  private void save() {
    Settings.current.server_voip = server_voip.isSelected();
    Settings.current.pvp = pvp.isSelected();
    Settings.current.dropItemsOnDeath = dropItemsOnDeath.isSelected();
    Settings.current.client_voip = client_voip.isSelected();
    Settings.current.ptt = ptt.isSelected();
    Settings.current.mic = (String)mic.getSelectedItem();
    Settings.current.spk = (String)spk.getSelectedItem();
    Settings.current.fancy = fancy.isSelected();
    Settings.current.clouds = clouds.isSelected();
    int fps = fpsSlider.getValue();
    if (fps == 260) fps = -1;
    Settings.current.FPS = fps;
  }

  private void listDevices() {
    listInputDevices();
    listOutputDevices();
  }

  private void listInputDevices() {
    AudioInput input = new AudioInput();
    String devices[] = input.listDevices();
    mic.removeAllItems();
    int idx = -1;
    for(int a=0;a<devices.length;a++) {
      String device = devices[a];
      mic.addItem(device);
      if (device.equals(Settings.current.mic)) {
        idx = a;
      }
    }
    mic.setSelectedIndex(idx);
  }

  private void listOutputDevices() {
    AudioOutput output = new AudioOutput();
    String devices[] = output.listDevices();
    spk.removeAllItems();
    int idx = -1;
    for(int a=0;a<devices.length;a++) {
      String device = devices[a];
      spk.addItem(device);
      if (device.equals(Settings.current.spk)) {
        idx = a;
      }
    }
    spk.setSelectedIndex(idx);
  }

  private void updateFPS() {
    int fps = fpsSlider.getValue();
    if (fps == 260)
      fpsLabel.setText("MAX");
    else
      fpsLabel.setText(Integer.toString(fps));
  }
}
