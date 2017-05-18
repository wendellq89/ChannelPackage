package com.wendell.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import com.leon.channel.common.ApkSectionInfo;
import com.leon.channel.common.V1SchemeUtil;
import com.leon.channel.common.V2SchemeUtil;
import com.leon.channel.common.verify.VerifyApk;
import com.leon.channel.reader.ChannelReader;
import com.leon.channel.writer.ChannelWriter;
/**
 * 添加渠道信息，使用了ApkChannelPackage的实现
 * @author WQ
 *
 */
public class PackageAPK {
    public static final int DEFAULT_MODE = -1;
    public static final int V1_MODE = 1;
    public static final int V2_MODE = 2;
    public List<String> mChannelList;

    public void setChannelList(List<String> channelList){
    	mChannelList = channelList;
    }
    
	public int judgeChannelPackageMode(File baseApk) {
        if (V2SchemeUtil.containV2Signature(baseApk)) {
            return V2_MODE;
        } else if (V1SchemeUtil.containV1Signature(baseApk)) {
            return V1_MODE;
        } else {
            return DEFAULT_MODE;
        }
    }
	
	 public  void generateChannelApk(File baseApk, File outputDir) throws Exception {
	        int mode = judgeChannelPackageMode(baseApk);

	        if (mode == V1_MODE) {
	            generateV1ChannelApk(baseApk, outputDir);
	        } else if (mode ==V2_MODE) {
	            generateV2ChannelApk(baseApk, outputDir);
	        }
	        
	    }
	 
	 public  void generateV1ChannelApk(File baseApk, File outputDir) throws Exception {

	        if (!V1SchemeUtil.containV1Signature(baseApk)) {
	            throw new Exception("apk  not signed by v1 , please check your signingConfig , if not have v1 signature , you can't install Apk below 7.0");
	        }
	        String baseReleaseApkName = baseApk.getName();
	        for (String channel : mChannelList) {
	        	String apkChannelName = getChannelApkName(baseReleaseApkName, channel);
	        	 File destFile = new File(outputDir, apkChannelName);
	        	 copyTo(baseApk, destFile);
	        	 V1SchemeUtil.writeChannel(destFile, channel);
	        	 if (V1SchemeUtil.verifyChannel(destFile, channel)) {
		                System.out.println("generateV1ChannelApk add channel success");
		            } else {
		                throw new Exception("generateV1ChannelApk add channel failure");
		            }
		            //verify v1 signature
		            if (VerifyApk.verifyV1Signature(destFile)) {
		            	System.out.println("generateV1ChannelApk , after add channel , apk  v1 verify success");
		            } else {
		                throw new Exception("generateV1ChannelApk , after add channel , apk  v1 verify failure");
		            }
			}
	      

	  
	    }
	 
	   public  void generateV2ChannelApk(File baseApk, File outputDir) throws Exception {

	        String baseReleaseApkName = baseApk.getName();
	        ApkSectionInfo apkSectionInfo = V2SchemeUtil.getApkSectionInfo(baseApk);
	        for (String channel : mChannelList) {
	        	  String apkChannelName = getChannelApkName(baseReleaseApkName, channel);
	        	  File destFile = new File(outputDir, apkChannelName);
	        	  ChannelWriter.addChannel(apkSectionInfo, destFile, channel);
	        	  if (ChannelReader.verifyChannel(destFile, channel)) {
	        		  System.out.println("generateV2ChannelApk add channel success");
		            } else {
		                throw new Exception("generateV2ChannelApk add channel failure");
		            }
	        	  boolean success = VerifyApk.verifyV2Signature(destFile);
	      	            if (success) {
	      	            	 System.out.println("generateV2ChannelApk after add channel , apk  v2 verify success");
	      	            } else {
	      	                throw new Exception("generateV2ChannelApk , after add channel , apk ${destFile} v2 verify failure");
	      	            }
			}
	        
	    }
	 
	 
	 
	    String getChannelApkName(String baseApkName, String channel) {
	        return baseApkName.replace("base", channel);
	    }
	    
	    
	    @SuppressWarnings("resource")
		public static void copyTo(File source, File dest) throws IOException {    
	        FileChannel inputChannel = null;    
	        FileChannel outputChannel = null;    
	    try {
	        inputChannel = new FileInputStream(source).getChannel();
	        outputChannel = new FileOutputStream(dest).getChannel();
	        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
	    } finally {
	        inputChannel.close();
	        outputChannel.close();
	    }
	}

}
