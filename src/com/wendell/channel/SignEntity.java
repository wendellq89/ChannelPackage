package com.wendell.channel;
/**
 * @author WQ
 * 
 */
public class SignEntity {
	private String originalApkPath;
	private String signApkPath;
	private String keyPath;
	private String keyPwd;
	private String aliasName;
	private String aliasPwd;
	public String getOriginalApkPath() {
		return originalApkPath;
	}
	public void setOriginalApkPath(String originalApkPath) {
		this.originalApkPath = originalApkPath;
	}
	public String getSignApkPath() {
		return signApkPath;
	}
	public void setSignApkPath(String signApkPath) {
		this.signApkPath = signApkPath;
	}
	public String getKeyPath() {
		return keyPath;
	}
	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}
	public String getKeyPwd() {
		return keyPwd;
	}
	public void setKeyPwd(String keyPwd) {
		this.keyPwd = keyPwd;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	public String getAliasPwd() {
		return aliasPwd;
	}
	public void setAliasPwd(String aliasPwd) {
		this.aliasPwd = aliasPwd;
	}
	
}
