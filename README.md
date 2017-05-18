# ChannelPackage
package unsigned apk file and set channel by ApkChannelPackage project
打包未签名文件，并使用[ltlovezh/ApkChannelPackage](https://github.com/ltlovezh/ApkChannelPackage/)添加渠道信息。
ApkChannelPackage  可以很方便的生成渠道信息。但是打包完成后的渠道包交给运营同事后，提交到360渠道会要求加固apk并重新打包后无法获取到渠道信息。
在通过360加固后，使用此工具可以方便的签名并添加渠道信息。
### 效果
![](https://github.com/wang-qian/ChannelPackage/blob/master/ui.png)<br/>

### 使用方式
1、电脑安装jdk 开发工具
2、选择output目录下的signtools.jar(如果无法打开，右键“打开方式”，选择Java platform SE binary打开)
3、选择apk的签名文件，输入密码、签名别名、别名密码，点击签名apk，最后点击生成渠道
