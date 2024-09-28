# PhoneSampler
### Exam Project for Politecnic di Milano course: Design & Implementation of Mobile Applications
### Grade Awarded: 29/30

PhoneSampler is an **Android** application that allows users to **record audio** from other apps in their phones. 

![alt text](https://github.com/hfoley03/PhoneSampler/blob/main/images/PhoneSampler.png?raw=true)

Sampling is a music production technique where artists sample audio from some source and recontextualise it for use in a musical piece. Artists have always used whatever source is available and abundant. Originally this was records and radio, then cassettes, VHS tapes,  CDs and the internet. Today we consume the majority of our media through our phones.

This gave us the idea to allow music producers to be able to quickly record audio for sampling from applications like Instagram and YouTube. Our application eliminates the need to record phone audio into and audio interface and computer, or record a video of the screen and strip out the audio on a computer later.  

## FEATURES:
1.	**Recording System Audio:** 
PhoneSampler allows a user to record the system audio produced by other applications on their device. Each time a user tries to record audio they are shown with a dialog box that recording is about to begin and asked to accept or decline. 

2.	**Handling of Recorded Audio:**
After recording the audio is converted from the raw PCM format to the WAV lossless format. If there is silence at the beginning and/or end of the recording it is then automatically trimmed. Finally the user is asked to either accept the default naming or rename the recording. 

3.	**Browse Recordings:**
The application allows the user to browse through previously recorded audio by scrolling through a list of sound cards. A search function has been implemented to aid the user in browsing their samples as well as standard list sorting methods. 

4.	**Recording Playback:**
The user may choose a recording after browsing to listen back to the audio. On the playback screen a waveform of the audio file is generated, which the user can use to seek through the recording. On this screen the standard transport controls are available as well as variable speed playback and repeat. 

5.	**Edit Recording:** 
The application allows a user to edit previous recordings through renaming and manual trimming of the audio file. 

6.	**Sharing to Applications:** 
Standard file sharing is available to other applications on the user’s device. 

7.	**FreeSound API:**
The freesound.org is a website that provides a collaborative database of sounds for musicians and producres. The website’s API has been integrated into the application to provide two features: 
    - Upload samples to the freesound to be used by thousands of other musicians for use in their productions. 
    - Search, browse and download samples from freesound from within the application.

![alt text](https://github.com/hfoley03/PhoneSampler/blob/main/images/phoneSamplerTrans.png?raw=true)
