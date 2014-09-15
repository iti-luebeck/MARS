/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.water.options;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import mars.water.WaterGridFilter;
import mars.water.WaterState;

final class WaterPanel extends javax.swing.JPanel {
    private final WaterOptionsPanelController controller;
    private WaterState state;
    private WaterGridFilter filter;

    WaterPanel(WaterOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        while (WaterState.getInstance() == null);
        state = WaterState.getInstance();
        filter = state.getWaterFilter();
        opacity.setText(String.valueOf(filter.getWaterTransparency()));
        fogDistance.setText(String.valueOf(filter.getUnderWaterFogDistance()));
        normalScale.setText(String.valueOf(filter.getNormalScale()));
        Vector3f lightDirection = filter.getLightDirection();
        lightDirectionX.setText(String.valueOf(lightDirection.x));
        lightDirectionY.setText(String.valueOf(lightDirection.y));
        lightDirectionZ.setText(String.valueOf(lightDirection.z));
        ColorRGBA lightColor = filter.getLightColor();
        lightColorR.setText(String.valueOf(lightColor.r));
        lightColorG.setText(String.valueOf(lightColor.g));
        lightColorB.setText(String.valueOf(lightColor.b));
        lightColorA.setText(String.valueOf(lightColor.a));
        ColorRGBA waterColor = filter.getWaterColor();
        waterColorR.setText(String.valueOf(waterColor.r));
        waterColorG.setText(String.valueOf(waterColor.g));
        waterColorB.setText(String.valueOf(waterColor.b));
        waterColorA.setText(String.valueOf(waterColor.a));
        ColorRGBA deepColor = filter.getDeepWaterColor();
        deepColorR.setText(String.valueOf(deepColor.r));
        deepColorG.setText(String.valueOf(deepColor.g));
        deepColorB.setText(String.valueOf(deepColor.b));
        deepColorA.setText(String.valueOf(deepColor.a));
        Vector3f extinction = filter.getColorExtinction();
        extinctionR.setText(String.valueOf(extinction.x));
        extinctionG.setText(String.valueOf(extinction.y));
        extinctionB.setText(String.valueOf(extinction.z));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        opacity = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        fogDistance = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        normalScale = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        lightDirectionX = new javax.swing.JFormattedTextField();
        lightDirectionY = new javax.swing.JFormattedTextField();
        lightDirectionZ = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        lightColorR = new javax.swing.JFormattedTextField();
        lightColorG = new javax.swing.JFormattedTextField();
        lightColorB = new javax.swing.JFormattedTextField();
        lightColorA = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        waterColorA = new javax.swing.JFormattedTextField();
        waterColorB = new javax.swing.JFormattedTextField();
        waterColorG = new javax.swing.JFormattedTextField();
        waterColorR = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        deepColorA = new javax.swing.JFormattedTextField();
        deepColorB = new javax.swing.JFormattedTextField();
        deepColorG = new javax.swing.JFormattedTextField();
        deepColorR = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        extinctionB = new javax.swing.JFormattedTextField();
        extinctionG = new javax.swing.JFormattedTextField();
        extinctionR = new javax.swing.JFormattedTextField();
        heightTexture = new javax.swing.JButton();
        normalTexture = new javax.swing.JButton();

        opacity.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.opacity.text")); // NOI18N
        opacity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opacityActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.jLabel1.text")); // NOI18N

        fogDistance.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.fogDistance.text")); // NOI18N
        fogDistance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fogDistanceActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.jLabel2.text")); // NOI18N

        normalScale.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.normalScale.text")); // NOI18N
        normalScale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normalScaleActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.jLabel3.text")); // NOI18N

        lightDirectionX.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.lightDirectionX.text")); // NOI18N
        lightDirectionX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightDirectionXActionPerformed(evt);
            }
        });

        lightDirectionY.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.lightDirectionY.text")); // NOI18N
        lightDirectionY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightDirectionYActionPerformed(evt);
            }
        });

        lightDirectionZ.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.lightDirectionZ.text")); // NOI18N
        lightDirectionZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightDirectionZActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.jLabel4.text")); // NOI18N

        lightColorR.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.lightColorR.text")); // NOI18N
        lightColorR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightColorRActionPerformed(evt);
            }
        });

        lightColorG.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.lightColorG.text")); // NOI18N
        lightColorG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightColorGActionPerformed(evt);
            }
        });

        lightColorB.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.lightColorB.text")); // NOI18N
        lightColorB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightColorBActionPerformed(evt);
            }
        });

        lightColorA.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.lightColorA.text")); // NOI18N
        lightColorA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lightColorAActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.jLabel5.text")); // NOI18N

        waterColorA.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.waterColorA.text")); // NOI18N
        waterColorA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterColorAActionPerformed(evt);
            }
        });

        waterColorB.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.waterColorB.text")); // NOI18N
        waterColorB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterColorBActionPerformed(evt);
            }
        });

        waterColorG.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.waterColorG.text")); // NOI18N
        waterColorG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterColorGActionPerformed(evt);
            }
        });

        waterColorR.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.waterColorR.text")); // NOI18N
        waterColorR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterColorRActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.jLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.jLabel7.text")); // NOI18N

        deepColorA.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.deepColorA.text")); // NOI18N
        deepColorA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deepColorAActionPerformed(evt);
            }
        });

        deepColorB.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.deepColorB.text")); // NOI18N
        deepColorB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deepColorBActionPerformed(evt);
            }
        });

        deepColorG.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.deepColorG.text")); // NOI18N
        deepColorG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deepColorGActionPerformed(evt);
            }
        });

        deepColorR.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.deepColorR.text")); // NOI18N
        deepColorR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deepColorRActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.jLabel8.text")); // NOI18N

        extinctionB.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.extinctionB.text")); // NOI18N
        extinctionB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extinctionBActionPerformed(evt);
            }
        });

        extinctionG.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.extinctionG.text")); // NOI18N
        extinctionG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extinctionGActionPerformed(evt);
            }
        });

        extinctionR.setText(org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.extinctionR.text")); // NOI18N
        extinctionR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extinctionRActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(heightTexture, org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.heightTexture.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(normalTexture, org.openide.util.NbBundle.getMessage(WaterPanel.class, "WaterPanel.normalTexture.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(heightTexture)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(normalTexture))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fogDistance, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(opacity, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(normalScale, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lightDirectionX, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lightDirectionY, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lightDirectionZ, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lightColorR, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lightColorG, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lightColorB, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lightColorA, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(waterColorR, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(waterColorG, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(waterColorB, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(waterColorA, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(deepColorR, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deepColorG, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deepColorB, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deepColorA, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(extinctionR, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(extinctionG, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(extinctionB, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(opacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fogDistance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(normalScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lightDirectionX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lightDirectionY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lightDirectionZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lightColorR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lightColorG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lightColorB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lightColorA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(waterColorR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(waterColorG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(waterColorB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(waterColorA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deepColorR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deepColorG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deepColorB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deepColorA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(extinctionR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(extinctionG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(extinctionB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(heightTexture)
                    .addComponent(normalTexture))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fogDistanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fogDistanceActionPerformed
        filter.setUnderWaterFogDistance(Float.parseFloat(fogDistance.getText()));
    }//GEN-LAST:event_fogDistanceActionPerformed

    private void normalScaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normalScaleActionPerformed
        filter.setNormalScale(Float.parseFloat(normalScale.getText()));
    }//GEN-LAST:event_normalScaleActionPerformed

    private void lightDirectionXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lightDirectionXActionPerformed
        Vector3f light = filter.getLightDirection();
        light.x = Float.parseFloat(lightDirectionX.getText());
        filter.setLightDirection(light);
    }//GEN-LAST:event_lightDirectionXActionPerformed

    private void lightDirectionYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lightDirectionYActionPerformed
        Vector3f light = filter.getLightDirection();
        light.y = Float.parseFloat(lightDirectionY.getText());
        filter.setLightDirection(light);
    }//GEN-LAST:event_lightDirectionYActionPerformed

    private void lightDirectionZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lightDirectionZActionPerformed
        Vector3f light = filter.getLightDirection();
        light.z = Float.parseFloat(lightDirectionZ.getText());
        filter.setLightDirection(light);
    }//GEN-LAST:event_lightDirectionZActionPerformed

    private void lightColorRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lightColorRActionPerformed
        ColorRGBA color = filter.getLightColor();
        color.r = Float.parseFloat(lightColorR.getText());
        filter.setLightColor(color);
    }//GEN-LAST:event_lightColorRActionPerformed

    private void lightColorGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lightColorGActionPerformed
        ColorRGBA color = filter.getLightColor();
        color.g = Float.parseFloat(lightColorG.getText());
        filter.setLightColor(color);
    }//GEN-LAST:event_lightColorGActionPerformed

    private void lightColorBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lightColorBActionPerformed
        ColorRGBA color = filter.getLightColor();
        color.b = Float.parseFloat(lightColorB.getText());
        filter.setLightColor(color);
    }//GEN-LAST:event_lightColorBActionPerformed

    private void lightColorAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lightColorAActionPerformed
        ColorRGBA color = filter.getLightColor();
        color.a = Float.parseFloat(lightColorA.getText());
        filter.setLightColor(color);
    }//GEN-LAST:event_lightColorAActionPerformed

    private void waterColorAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterColorAActionPerformed
        ColorRGBA color = filter.getWaterColor();
        color.a = Float.parseFloat(waterColorA.getText());
        filter.setWaterColor(color);
    }//GEN-LAST:event_waterColorAActionPerformed

    private void waterColorBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterColorBActionPerformed
        ColorRGBA color = filter.getWaterColor();
        color.b = Float.parseFloat(waterColorB.getText());
        filter.setWaterColor(color);
    }//GEN-LAST:event_waterColorBActionPerformed

    private void waterColorGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterColorGActionPerformed
        ColorRGBA color = filter.getWaterColor();
        color.g = Float.parseFloat(waterColorG.getText());
        filter.setWaterColor(color);
    }//GEN-LAST:event_waterColorGActionPerformed

    private void waterColorRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterColorRActionPerformed
        ColorRGBA color = filter.getWaterColor();
        color.r = Float.parseFloat(waterColorR.getText());
        filter.setWaterColor(color);
    }//GEN-LAST:event_waterColorRActionPerformed

    private void deepColorAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deepColorAActionPerformed
        ColorRGBA color = filter.getDeepWaterColor();
        color.a = Float.parseFloat(deepColorA.getText());
        filter.setDeepWaterColor(color);
    }//GEN-LAST:event_deepColorAActionPerformed

    private void deepColorBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deepColorBActionPerformed
        ColorRGBA color = filter.getDeepWaterColor();
        color.b= Float.parseFloat(deepColorB.getText());
        filter.setDeepWaterColor(color);
    }//GEN-LAST:event_deepColorBActionPerformed

    private void deepColorGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deepColorGActionPerformed
        ColorRGBA color = filter.getDeepWaterColor();
        color.g = Float.parseFloat(deepColorG.getText());
        filter.setDeepWaterColor(color);
    }//GEN-LAST:event_deepColorGActionPerformed

    private void deepColorRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deepColorRActionPerformed
        ColorRGBA color = filter.getDeepWaterColor();
        color.r = Float.parseFloat(deepColorR.getText());
        filter.setDeepWaterColor(color);
    }//GEN-LAST:event_deepColorRActionPerformed

    private void extinctionBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extinctionBActionPerformed
        Vector3f extinction = filter.getColorExtinction();
        extinction.z = Float.parseFloat(extinctionB.getText());
        filter.setColorExtinction(extinction);
    }//GEN-LAST:event_extinctionBActionPerformed

    private void extinctionGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extinctionGActionPerformed
        Vector3f extinction = filter.getColorExtinction();
        extinction.y = Float.parseFloat(extinctionG.getText());
        filter.setColorExtinction(extinction);
    }//GEN-LAST:event_extinctionGActionPerformed

    private void extinctionRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extinctionRActionPerformed
        Vector3f extinction = filter.getColorExtinction();
        extinction.x = Float.parseFloat(extinctionR.getText());
        filter.setColorExtinction(extinction);
    }//GEN-LAST:event_extinctionRActionPerformed

    private void opacityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opacityActionPerformed
        filter.setWaterTransparency(Float.parseFloat(opacity.getText()));
    }//GEN-LAST:event_opacityActionPerformed

    void load() {
        // TODO read settings and initialize GUI
        // Example:        
        // someCheckBox.setSelected(Preferences.userNodeForPackage(WaterPanel.class).getBoolean("someFlag", false));
        // or for org.openide.util with API spec. version >= 7.4:
        // someCheckBox.setSelected(NbPreferences.forModule(WaterPanel.class).getBoolean("someFlag", false));
        // or:
        // someTextField.setText(SomeSystemOption.getDefault().getSomeStringProperty());
    }

    void store() {
        // TODO store modified settings
        // Example:
        // Preferences.userNodeForPackage(WaterPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or for org.openide.util with API spec. version >= 7.4:
        // NbPreferences.forModule(WaterPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or:
        // SomeSystemOption.getDefault().setSomeStringProperty(someTextField.getText());
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField deepColorA;
    private javax.swing.JFormattedTextField deepColorB;
    private javax.swing.JFormattedTextField deepColorG;
    private javax.swing.JFormattedTextField deepColorR;
    private javax.swing.JFormattedTextField extinctionB;
    private javax.swing.JFormattedTextField extinctionG;
    private javax.swing.JFormattedTextField extinctionR;
    private javax.swing.JFormattedTextField fogDistance;
    private javax.swing.JButton heightTexture;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JFormattedTextField lightColorA;
    private javax.swing.JFormattedTextField lightColorB;
    private javax.swing.JFormattedTextField lightColorG;
    private javax.swing.JFormattedTextField lightColorR;
    private javax.swing.JFormattedTextField lightDirectionX;
    private javax.swing.JFormattedTextField lightDirectionY;
    private javax.swing.JFormattedTextField lightDirectionZ;
    private javax.swing.JFormattedTextField normalScale;
    private javax.swing.JButton normalTexture;
    private javax.swing.JFormattedTextField opacity;
    private javax.swing.JFormattedTextField waterColorA;
    private javax.swing.JFormattedTextField waterColorB;
    private javax.swing.JFormattedTextField waterColorG;
    private javax.swing.JFormattedTextField waterColorR;
    // End of variables declaration//GEN-END:variables
}