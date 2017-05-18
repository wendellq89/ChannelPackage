package com.wendell.channel;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.leon.channel.common.verify.VerifyApk;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

/**
 * UI界面，使用javaFx 实现
 *
 * @author WQ
 */
public class MainController {
	@FXML
	private Label labTip;
	@FXML
	private Button btnSelectKey;
	@FXML
	private TextField tfKeyPwd;
	@FXML
	private TextField tfAliasName;
	@FXML
	private TextField tfAliasPwd;
	@FXML
	private SplitMenuButton smbAlias;
	@FXML
	private Button btnChannel;
	@FXML
	private TextArea taChannel;
	@FXML
	private Button btnPackage;
	@FXML
	private ProgressIndicator piTip;

	private String mKeyPath;
	private String mOriginalApkPath;
	private String mSignApkPath;

	public void selectKey(ActionEvent event) throws Exception {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("选择签名文件");
		File apkFile = fileChooser.showOpenDialog(btnPackage.getContextMenu());
		mKeyPath = apkFile.getAbsolutePath();
		labTip.setText("已选择签名" + mKeyPath);

	}

	public void signAPKFile(ActionEvent event) throws Exception {
		if (isEmpty(mKeyPath)) {
			labTip.setText("请先选择签名文件");
			return;
		}
		String keyPwd = tfKeyPwd.getText();
		if (isEmpty(keyPwd)) {
			labTip.setText("请输入key密码");
			return;
		}
		String aliasName = tfAliasName.getText();
		if (isEmpty(aliasName)) {
			labTip.setText("请输入别名名称");
			return;
		}
		String aliasPwd = tfAliasPwd.getText();
		if (isEmpty(aliasPwd)) {
			labTip.setText("请输入别名密码");
			return;
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("选择需要打包apk文件");
		File apkFile = fileChooser.showOpenDialog(btnPackage.getContextMenu());
		mOriginalApkPath = apkFile.getAbsolutePath();
		// 需要添加base的名字，ApkChannelPackage用替换base来多渠道打包
		mSignApkPath = mOriginalApkPath.substring(0, mOriginalApkPath.lastIndexOf(".")) + "_base_sign.apk";
		piTip.setVisible(true);
		labTip.setText("开始签名");
		SignTask signTask = new SignTask(keyPwd, aliasName, aliasPwd);
		signTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				if (signTask.getValue()) {
					labTip.setText("签名成功");
					piTip.setVisible(false);
				} else {
					labTip.setText("签名失败");
					piTip.setVisible(false);
				}
			}
		});

		signTask.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				labTip.setText("签名失败");
				piTip.setVisible(false);
			}
		});

		new Thread(signTask).start();
	}

	public void generateChannelApkFile(ActionEvent event) throws Exception {
		if (mSignApkPath == null) {
			labTip.setText("请重新选择apk文件");
			return;
		}
		String channels = taChannel.getText();
		File signApkFile = new File(mSignApkPath);
		PackageAPK packageAPK = new PackageAPK();
		packageAPK.setChannelList(getChannelList(channels));
		String outputDir = signApkFile.getParent() + "channel";
		packageAPK.generateChannelApk(new File(signApkFile.getAbsolutePath()), new File(outputDir));
		labTip.setText("打包后的apk目录:" + outputDir);
	}

	private List<String> getChannelList(String channels) {
		String[] channelArray = channels.split("\n");
		return Arrays.asList(channelArray);
	}

	;

	public static boolean isEmpty(String str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}

	class SignTask extends Task<Boolean> {
		private String mKeyPwd;
		private String mAliasName;
		private String mAliasPwd;

		public SignTask(String keyPwd, String aliasName, String aliasPwd) {
			super();
			mKeyPwd = keyPwd;
			mAliasName = aliasName;
			mAliasPwd = aliasPwd;
		}

		@Override
		protected Boolean call() throws Exception {
			SignEntity signEntity = new SignEntity();
			signEntity.setOriginalApkPath(mOriginalApkPath);
			signEntity.setSignApkPath(mSignApkPath);
			signEntity.setKeyPath(mKeyPath);
			signEntity.setKeyPwd(mKeyPwd);
			signEntity.setAliasName(mAliasName);
			signEntity.setAliasPwd(mAliasPwd);
			SignApk.signApk(signEntity);
			boolean isV1Success = false;
			boolean isV2Success = false;
			try {
				isV1Success = VerifyApk.verifyV1Signature(new File(mSignApkPath));
				isV2Success = VerifyApk.verifyV2Signature(new File(mSignApkPath));
				if (isV1Success || isV2Success) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

}
