package controller;


/*
 * Copyright 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// [START tts_quickstart]
// Imports the Google Cloud client library
import com.google.cloud.texttospeech.v1.AudioConfig;


import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 * Google Cloud TextToSpeech API sample application. Example usage: mvn package exec:java
 * -Dexec.mainClass='com.example.texttospeech.QuickstartSample'
 */
public class GoTTS {

  /** Demonstrates using the Text-to-Speech API. */
  public static void main(String s_param) throws Exception {
    // Instantiates a client
    try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
      // Set the text input to be synthesized
      SynthesisInput input = SynthesisInput.newBuilder().setText(s_param).build();

      // Build the voice request, select the language code ("en-US") and the ssml voice gender
      // ("neutral")
      System.out.println(">> Save Mp3 ");
      VoiceSelectionParams voice =
    		  VoiceSelectionParams.newBuilder()
              .setLanguageCode("ko_KR")
              .setSsmlGender(SsmlVoiceGender.FEMALE)
              .build();

      // Select the type of audio file you want returned
      AudioConfig audioConfig =
          AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

      // Perform the text-to-speech request on the text input with the selected voice parameters and
      // audio file type
      SynthesizeSpeechResponse response =
          textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

      // Get the audio contents from the response
      ByteString audioContents = response.getAudioContent();

      // Write the response to the output file.
      try (OutputStream out = new FileOutputStream("output.mp3")) {
        out.write(audioContents.toByteArray());
        System.out.println("Audio content written to file \"output.mp3\"");
      }
      
      System.out.println(">> Play Mp3 "); 
      
      
      FileInputStream fis = new FileInputStream("output.mp3");
      AdvancedPlayer player = new AdvancedPlayer(fis);
      
      player.setPlayBackListener(new PlaybackListener() {
          
          @Override
          public void playbackFinished(PlaybackEvent event) {
              event.getFrame();
          }
      });
      player.play();
          
    } catch(Exception e) {        	
    	System.out.println("Exception[" + e.toString() +"]");
    }
    
    
  }

}