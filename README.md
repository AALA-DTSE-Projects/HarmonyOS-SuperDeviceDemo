# SuperDeviceDemo

## Description

Distributed video player demo on tablet and smartwatch.  
User can use a smartwatch to control a video player on tablet.  

## Project structure

**smartwatch (smartwatch module)**</br>
&emsp;Remote control module, is customized to run on not only smartwatch but also other devices as tablet, smartphone
 - controller
   - Const.java: to hold const values
   - LogUtil.java: to print logs
   - PlayerRemoteProxy.java: to control the tablet remotely
 - slice
   - MainAbilitySlice.java: main UI class to interact with user
 - MainAbility.java: end point Ability
 - MyApplication.java: application class
- tablet (tablet module)  

**Video player module**
 - controller
   - Const.java: to hold const values
   - DistributeNotificationPlugin.java: to manage internal event, including sending, subscribing and unsubscribing
   - LogUtil.java: to print logs
   - PlayerRemote.java: to process the remote command request from smartwatch
   - ThreadPoolManager.java: to do the multi-threading, including execute and cancel a thread
   - VideoElementManager.java: to load the video resources, including internal video files and internet video links
   - VideoPlayerPlugin.java: to control a video player, including playing, pausing, rewinding, seeking,...
 - slice
   - MainAbilitySlice.java: main UI class to interact with user and process remote command from a smartwatch
 - MainAbility.java: end point Ability
 - VideoPlayerServiceAbility.java: service Ability, to connect with smartwatch Ability 
 - MyApplication.java: application class
 
**litewearable(lite wearable module)**</br>
&emsp;Now lite wearable hasn't supported distributed capability yet so we will develop it in the future
 
## How to run
- Create your own keystore, cert, provision file
- Get your devices' UDID, add in AGC and download the updated provision file  
- Connect your device to PC then deploy 
  - tablet module on tablet as a video player
  - smartwatch module on tablet or another device as a remote control 

## Demo result
![](screenshot/distributedVideoDemo.gif)
