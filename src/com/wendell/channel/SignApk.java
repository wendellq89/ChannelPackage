package com.wendell.channel;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

import com.android.apksig.ApkSigner;
import com.android.apksig.apk.ApkFormatException;
import com.android.apksigner.ApkSignerTool.SignerParams;
import com.android.apksigner.PasswordRetriever;

/**
 * 签名apk，打包详情流程可参考 https://android.googlesource.com/platform/tools/apksig/
 * 满足基本的签名apk的功能
 * 
 * @author WQ
 * 
 */
public class SignApk {

	public static void signApk(SignEntity signEntity) throws IOException, URISyntaxException {
		String originalApkPath = signEntity.getOriginalApkPath();
		String signApkPath = signEntity.getSignApkPath();

		// 只考虑一个签名文件的情况
		List<ApkSigner.SignerConfig> signerConfigs = new ArrayList<>(1);
		SignerParams signerParams = new SignerParams();
		signerParams.keystoreFile = signEntity.getKeyPath();
		signerParams.keystoreKeyAlias = signEntity.getAliasName();
		signerParams.keystorePasswordSpec = "pass:" + signEntity.getKeyPwd();
		signerParams.keyPasswordSpec = "pass:" + signEntity.getAliasPwd();
		PasswordRetriever passwordRetriever = new PasswordRetriever();
		signerParams.name = "signer #0";
		try {
			signerParams.loadPrivateKeyAndCerts(passwordRetriever);
		} catch (Exception e) {
			System.err.println("Failed to load signer \"" + signerParams.name + "\"");
			e.printStackTrace();

		}
		String v1SigBasename;
		if (signerParams.v1SigFileBasename != null) {
			v1SigBasename = signerParams.v1SigFileBasename;
		} else if (signerParams.keystoreKeyAlias != null) {
			v1SigBasename = signerParams.keystoreKeyAlias;
		} else if (signerParams.keyFile != null) {
			String keyFileName = new File(signerParams.keyFile).getName();
			int delimiterIndex = keyFileName.indexOf('.');
			if (delimiterIndex == -1) {
				v1SigBasename = keyFileName;
			} else {
				v1SigBasename = keyFileName.substring(0, delimiterIndex);
			}
		} else {
			throw new RuntimeException("Neither KeyStore key alias nor private key file available");
		}
		ApkSigner.SignerConfig signerConfig = new ApkSigner.SignerConfig.Builder(v1SigBasename, signerParams.privateKey,
				signerParams.certs).build();
		signerConfigs.add(signerConfig);

		File inputApk = new File(originalApkPath);
		File outputApk = new File(signApkPath);
		File tmpOutputApk;
		if (inputApk.getCanonicalPath().equals(outputApk.getCanonicalPath())) {
			tmpOutputApk = File.createTempFile("apksigner", ".apk");
			tmpOutputApk.deleteOnExit();
		} else {
			tmpOutputApk = outputApk;
		}

		ApkSigner.Builder apkSignerBuilder = new ApkSigner.Builder(signerConfigs).setInputApk(inputApk)
				.setOutputApk(tmpOutputApk).setOtherSignersSignaturesPreserved(false).setV1SigningEnabled(true)
				.setV2SigningEnabled(true);
		ApkSigner apkSigner = apkSignerBuilder.build();
		try {
			apkSigner.sign();
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | IllegalStateException
				| ApkFormatException e) {
			e.printStackTrace();
		}
	}

//	private static String toTmpKeyFile() {
//		File file = null;
//		try {
//			InputStream input = ClassLoader.getSystemResourceAsStream("res/key.jks");
//			file = File.createTempFile("tempfile", ".tmp");
//			OutputStream out = new FileOutputStream(file);
//			int read;
//			byte[] bytes = new byte[1024];
//
//			while ((read = input.read(bytes)) != -1) {
//				out.write(bytes, 0, read);
//			}
//			file.deleteOnExit();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//		if (file != null) {
//			return file.getAbsolutePath();
//		}
//		return null;
//	}

}
