/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wendell.channel;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 配合 https://github.com/ltlovezh/ApkChannelPackage 使用的工具
 * 解决360加固后，渠道信息获取不到的问题。使用方法：在360加固后，使用此工具打包并添加渠道信息
 * @author WQ
 * 
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/res/Main.fxml"));
        initNode(root);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/res/application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/res/icon.png")));
        primaryStage.setTitle("渠道打包");
        primaryStage.show();
    }

    private void initNode(Parent root) {
        ProgressIndicator pi = (ProgressIndicator)root.lookup("#piTip");
        Label labTip = (Label)root.lookup("#labTip");
        pi.setProgress(-1);
        pi.setVisible(false);
        labTip.setText("选择已加固但是未签名的apk");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
